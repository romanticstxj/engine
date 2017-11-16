package com.madhouse.dsp.reachMax;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.googlecode.protobuf.format.JsonFormat;
import com.madhouse.resource.ResourceManager;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;
import com.google.protobuf.ByteString;
import com.madhouse.cache.AdBlockMetaData;
import com.madhouse.cache.CacheManager;
import com.madhouse.cache.DSPBidMetaData;
import com.madhouse.cache.DSPMappingMetaData;
import com.madhouse.cache.MediaMetaData;
import com.madhouse.cache.PlcmtMetaData;
import com.madhouse.cache.PolicyMetaData;
import com.madhouse.dsp.DSPBaseHandler;
import com.madhouse.dsp.reachMax.ReachMaxAds.Ad;
import com.madhouse.dsp.reachMax.ReachMaxAds.AdSlot;
import com.madhouse.dsp.reachMax.ReachMaxAds.App;
import com.madhouse.dsp.reachMax.ReachMaxAds.BidRequest;
import com.madhouse.dsp.reachMax.ReachMaxAds.BidResponse;
import com.madhouse.dsp.reachMax.ReachMaxAds.CreativeType;
import com.madhouse.dsp.reachMax.ReachMaxAds.Device;
import com.madhouse.dsp.reachMax.ReachMaxAds.Ad.MaterialMeta;
import com.madhouse.dsp.reachMax.ReachMaxAds.Device.Os;
import com.madhouse.dsp.reachMax.ReachMaxAds.Device.UdId;
import com.madhouse.dsp.reachMax.ReachMaxAds.Network;
import com.madhouse.dsp.reachMax.ReachMaxAds.Size;
import com.madhouse.dsp.reachMax.ReachMaxAds.Version;
import com.madhouse.ssp.Constant;
import com.madhouse.ssp.avro.MediaBid.Builder;
import com.madhouse.ssp.avro.DSPRequest;
import com.madhouse.ssp.avro.DSPResponse;
import com.madhouse.ssp.avro.MediaRequest;
import com.madhouse.ssp.avro.Monitor;
import com.madhouse.ssp.avro.Track;
import com.madhouse.util.StringUtil;

public class ReachMaxHandler extends DSPBaseHandler {

    @Override
    protected HttpRequestBase packageBidRequest(Builder mediaBidBuilder, MediaMetaData mediaMetaData, PlcmtMetaData plcmtMetaData, AdBlockMetaData adBlockMetaData, PolicyMetaData policyMetaData,
        DSPBidMetaData dspBidMetaData) {
        
        MediaRequest.Builder builder = mediaBidBuilder.getRequestBuilder();
        Device.Builder deviceBuilder = getDevice(builder);
        if (deviceBuilder == null) {
            return null;
        }

        Network.Builder networkBuilder = getNetwork(builder,mediaBidBuilder.getLocation() == null ? "" : mediaBidBuilder.getLocation());
        if (networkBuilder == null) {
            return null;
        }
        
        DSPMappingMetaData dspMappingMetaData = CacheManager.getInstance().getDSPMapping(dspBidMetaData.getDspMetaData().getId(), plcmtMetaData.getId());
        String adspaceId = Long.toString(plcmtMetaData.getId());
        if (dspMappingMetaData != null && !StringUtils.isEmpty(dspMappingMetaData.getMappingKey())) {
            adspaceId = dspMappingMetaData.getMappingKey();
        }
        AdSlot.Builder adSlotBuilder = getAdslot(builder, plcmtMetaData,adspaceId);
        if (adSlotBuilder == null) {
            return null;
        }

        if (!StringUtils.isEmpty(policyMetaData.getDealId())) {
            adSlotBuilder.setDealid(policyMetaData.getDealId());
        }

        App.Builder appBuilder = getApp(builder, adspaceId);
        if (appBuilder == null) {
            return null;
        }

        BidRequest.Builder pg = BidRequest.newBuilder()//
            .setPrice(policyMetaData.getDeliveryType() == Constant.DeliveryType.RTB ? plcmtMetaData.getBidFloor() : 0)//价格必填
            .setRequestId(dspBidMetaData.getDspBidBuilder().getRequestBuilder().getId())//
            .setApiVersion(Version.newBuilder().setMajor(2).setMinor(3))//
            .setApp(appBuilder)//
            .setDevice(deviceBuilder)//
            .setNetwork(networkBuilder)//
            .addAdslots(adSlotBuilder);

        BidRequest bidRequest = pg.build();
        logger.info("ReachMax request url:{}", JsonFormat.printToString(bidRequest));
        HttpPost request = new HttpPost(dspBidMetaData.getDspMetaData().getBidUrl());
        request.setHeader(HTTP.CONTENT_TYPE, HTTP.OCTET_STREAM_TYPE);
        request.setEntity(new ByteArrayEntity(bidRequest.toByteArray()));
        return request;
    }

    private com.madhouse.dsp.reachMax.ReachMaxAds.Device.Builder getDevice(com.madhouse.ssp.avro.MediaRequest.Builder builder) {
        // Type
        Device.Builder deviceBuilder = Device.newBuilder().setType(Device.Type.PHONE);
        UdId.Builder udidBuilder = UdId.newBuilder();

        switch (builder.getOs()) {
            case Constant.OSType.ANDROID:
                deviceBuilder.setOs(Os.ANDROID);

                if (!StringUtils.isEmpty(builder.getDidmd5())) {
                    udidBuilder.setImei(builder.getDidmd5());
                } else {
                    if (!StringUtils.isEmpty(builder.getDid())) {
                        udidBuilder.setImei(StringUtil.getMD5(builder.getDid()));
                    }
                }

                if (!StringUtils.isEmpty(builder.getDpidmd5())) {
                    udidBuilder.setAndroidId(builder.getDpidmd5());
                } else {
                    if (!StringUtils.isEmpty(builder.getDpid())) {
                        udidBuilder.setAndroidId(StringUtil.getMD5(builder.getDpid()));
                    }
                }

                break;

            case Constant.OSType.IOS:
                deviceBuilder.setOs(Os.IOS);
                if (!StringUtils.isEmpty(builder.getIfa())) {
                    udidBuilder.setIdfa(builder.getIfa());
                }

                break;
            default:
                return null;
        }

        if (!StringUtils.isEmpty(builder.getMacmd5())) {
            udidBuilder.setMac(builder.getMacmd5());
        } else {
            if (!StringUtils.isEmpty(builder.getMac())) {
                udidBuilder.setMac(StringUtil.getMD5(builder.getMac()));
            }
        }

        deviceBuilder.setUdid(udidBuilder);

        // os version
        String osv = builder.getOsv();// os版本，必填点号分割
        if (osv != null) {
            String[] osvs = osv.split("\\.");
            if (osvs.length >= 1) {
                try {
                    //noinspection Duplicates
                    if (osvs.length == 1) {
                        deviceBuilder.setOsVersion(Version.newBuilder().setMajor(Integer.parseInt(osvs[0])));
                    } else if (osvs.length == 2) {
                        deviceBuilder.setOsVersion(Version.newBuilder().setMajor(Integer.parseInt(osvs[0])).setMinor(Integer.parseInt(osvs[1])));
                    } else if (osvs.length >= 3) {
                        deviceBuilder.setOsVersion(Version.newBuilder().setMajor(Integer.parseInt(osvs[0])).setMinor(Integer.parseInt(osvs[1])).setMicro(Integer.parseInt(osvs[2])));
                    }
                } catch (Exception e) {
                    logger.error("pg:osv format error " + e.toString());
                }
            }
        }
        //优听没有传osv，针对这种情况需要设置一个osv
        Version osVersion = deviceBuilder.getOsVersion();
        if (osVersion == null || osVersion.getMajor() == 0) {
            String o = builder.getOs().toString();
            if (o != null && o.equals("1")) { //ios ,osv 设置成9.0 
                deviceBuilder.setOsVersion(Version.newBuilder().setMajor(9).setMinor(0));
            } else { //other android 设置成5.0
                deviceBuilder.setOsVersion(Version.newBuilder().setMajor(5).setMinor(0));
            }
        }
        // Vendor Model
        String device = builder.getModel();
        if (device != null && device.length() > 0) {
            String[] str = device.split(" ");
            if (str.length >= 1) {
                if (str.length == 1) {
                    deviceBuilder.setVendorBytes(ByteString.copyFromUtf8(str[0])).setModelBytes(ByteString.copyFromUtf8("others"));
                } else {
                    deviceBuilder.setVendorBytes(ByteString.copyFromUtf8(str[0])).setModelBytes(ByteString.copyFromUtf8(str[1]));
                }
            } else {
                deviceBuilder.setVendorBytes(ByteString.copyFromUtf8("phone")).setModelBytes(ByteString.copyFromUtf8("others"));
            }
        } else {
            deviceBuilder.setVendorBytes(ByteString.copyFromUtf8("phone")).setModelBytes(ByteString.copyFromUtf8("others"));
        }
        return deviceBuilder;
    }

    private com.madhouse.dsp.reachMax.ReachMaxAds.Network.Builder getNetwork(com.madhouse.ssp.avro.MediaRequest.Builder builder, String location) {
        
        // 网络类型
        Network.Builder networkBuilder = Network.newBuilder();
        //ip
        String ip = builder.getIp();
        if (StringUtils.isEmpty(ip)) {
            return null;
        }
        networkBuilder.setIpv4(ip);

        networkBuilder.setIpv6(StringUtil.toString(ResourceManager.getInstance().getIpTools().getLocation(ip)));
        return networkBuilder;
    }

    private AdSlot.Builder getAdslot(MediaRequest.Builder builder, PlcmtMetaData plcmtMetaData, String adspaceId) {
        AdSlot.Builder adSlotBuilder = AdSlot.newBuilder().setId(adspaceId).setSize(Size.newBuilder().setWidth(builder.getW()).setHeight(builder.getH()));
        //optional
        AdSlot.StaticInfo adSlotStaticInfo;

        //保洁信息流不根据adtype判断，根据adspace的adType来判断
        Integer supplierAdsapceAdType = plcmtMetaData.getAdType();
        if (supplierAdsapceAdType != null && supplierAdsapceAdType.equals(8)) { //8是信息流
            adSlotStaticInfo = AdSlot.StaticInfo.newBuilder().addAcceptedCreativeTypes(CreativeType.TEXT).setType(AdSlot.StaticInfo.Type.NEWS_FEED).build();
        }else{
            adSlotStaticInfo = AdSlot.StaticInfo.newBuilder().addAcceptedCreativeTypes(CreativeType.IMAGE).setType(AdSlot.StaticInfo.Type.BANNER).build();
        }
        if (adSlotStaticInfo != null) {
            adSlotBuilder.setStaticInfo(adSlotStaticInfo);
        }
        return adSlotBuilder;
    }

    private App.Builder getApp(com.madhouse.ssp.avro.MediaRequest.Builder builder, String adspaceId) {
        App.Builder appBuilder = App.newBuilder();
        appBuilder.setId("madhouse");
        App.StaticInfo.Builder appStaticInfoBuilder = App.StaticInfo.newBuilder();
        String pkgname = builder.getBundle();
        if (pkgname != null && pkgname.length() > 0) {
            try {
                appStaticInfoBuilder.setBundleId(URLEncoder.encode(pkgname, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                logger.error(adspaceId + "pkgname error is:" + e.toString());
            }
        }
        String appname = builder.getName();
        if (appname != null && appname.length() > 0) {
            try {
                appStaticInfoBuilder.setName(URLEncoder.encode(appname, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                logger.error(adspaceId + "appname error is:" + e.toString());
            }
        }
        int category = builder.getCategory();
        if (StringUtils.isEmpty(category+"")) {
            try {
                appStaticInfoBuilder.addCategories(category);
            } catch (Exception e) {
                logger.error(adspaceId + "pcat error is:" + e.toString());
            }
        }
        appBuilder.setStaticInfo(appStaticInfoBuilder);
        return appBuilder;
    }
    @Override
    protected boolean parseBidResponse(HttpResponse httpResponse, DSPBidMetaData dspBidMetaData) {
        DSPResponse.Builder dspResponse = DSPResponse.newBuilder();
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        try {
            if(statusCode == Constant.StatusCode.OK){
                BidResponse bidResponse = BidResponse.parseFrom(EntityUtils.toByteArray(httpResponse.getEntity()));
                logger.info("ReachMax Response is:{}", JsonFormat.printToString(bidResponse));
                List<Ad> ads = bidResponse.getAdsList();
                if (ads != null && ads.size() != 0) { // 只取第一个广告
                    Ad ad = ads.get(0);
                    if (ad != null && ad.getMaterialMeta() != null) {
                        MaterialMeta materialMeta = ad.getMaterialMeta();
                        //dspResponse.setCid(value)
                        DSPRequest.Builder dspRequest = dspBidMetaData.getDspBidBuilder().getRequestBuilder();
                        dspResponse.setId(dspRequest.getId());
                        dspResponse.setImpid(dspRequest.getImpid());
                        
                        dspResponse.setLpgurl(StringUtil.toString(materialMeta.getClickUrl()));
                        dspResponse.setDesc(StringUtil.toString(materialMeta.getDescription1()));
                        dspResponse.setTitle(StringUtil.toString(materialMeta.getTitle()));
                        dspResponse.setIcon(StringUtil.toString(materialMeta.getIconUrl()));
                        
                        Monitor.Builder monitor = Monitor.newBuilder();
                        
                        //点击监测
                        List<String> clicks =new ArrayList<>();
                        if(null != materialMeta.getClickTrackingList()) {
                            for (String url : materialMeta.getClickTrackingList()) {
                                clicks.add(url);
                            }
                        }
                        
                        List<Track> tracks=new ArrayList<>();
                        for (int i = 0; i < materialMeta.getWinNoticeUrlCount(); i++) {
                            String noticeurl = materialMeta.getWinNoticeUrl(i);
                            if (noticeurl != null && noticeurl.contains("{AUCTION_PRICE}")) {//替换的price,同时保存transactionId
                                noticeurl = noticeurl.replace("{AUCTION_PRICE}", "1");
                                try{
                                    int i1 = noticeurl.lastIndexOf("transactionId");
                                    int i2 = noticeurl.indexOf("&", i1);
                                    String transactionId = noticeurl.substring(i1 + 14, i2);
                                    dspResponse.setBidid(transactionId);
                                }catch(Exception e){
                                    e.printStackTrace();
                                }
                            }
                            tracks.add(new Track(0, noticeurl));
                        }
                        
                        
                        monitor.setClkurl(clicks);
                        monitor.setImpurl(tracks);
                        dspResponse.setMonitorBuilder(monitor);
                        if (dspResponse.getAdm() == null) {
                            dspResponse.setAdm(new LinkedList<>());
                        }
                        dspResponse.getAdm().add(materialMeta.getMediaUrl());
                        dspResponse.setActtype(Constant.ActionType.OPEN_IN_APP);
                        dspBidMetaData.getDspBidBuilder().setStatus(Constant.StatusCode.OK);
                        dspBidMetaData.getDspBidBuilder().setResponseBuilder(dspResponse);
                        return true;
                    } else {
                        dspBidMetaData.getDspBidBuilder().setStatus(Constant.StatusCode.BAD_REQUEST);
                    }
                } else {
                    dspBidMetaData.getDspBidBuilder().setStatus(Constant.StatusCode.BAD_REQUEST);
                }
            } else {
                dspBidMetaData.getDspBidBuilder().setStatus(Constant.StatusCode.NO_CONTENT);
            }

        } catch (Exception e) {
            logger.error("ReachMax Response error:{}", JSON.toJSONString(dspResponse));
            dspBidMetaData.getDspBidBuilder().setStatus(Constant.StatusCode.INTERNAL_ERROR);
            return false;
        }
        return false;
    }
    
    
}
