package com.madhouse.dsp.proctergamble;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.google.protobuf.ByteString;
import com.madhouse.cache.AdBlockMetaData;
import com.madhouse.cache.CacheManager;
import com.madhouse.cache.DSPBidMetaData;
import com.madhouse.cache.DSPMappingMetaData;
import com.madhouse.cache.MediaMetaData;
import com.madhouse.cache.PlcmtMetaData;
import com.madhouse.cache.PolicyMetaData;
import com.madhouse.dsp.DSPBaseHandler;
import com.madhouse.dsp.proctergamble.PGMadAds.Ad;
import com.madhouse.dsp.proctergamble.PGMadAds.Ad.MaterialMeta;
import com.madhouse.dsp.proctergamble.PGMadAds.AdSlot;
import com.madhouse.dsp.proctergamble.PGMadAds.App;
import com.madhouse.dsp.proctergamble.PGMadAds.BidRequest;
import com.madhouse.dsp.proctergamble.PGMadAds.BidResponse;
import com.madhouse.dsp.proctergamble.PGMadAds.CreativeType;
import com.madhouse.dsp.proctergamble.PGMadAds.Device;
import com.madhouse.dsp.proctergamble.PGMadAds.Device.Os;
import com.madhouse.dsp.proctergamble.PGMadAds.Device.UdId;
import com.madhouse.dsp.proctergamble.PGMadAds.Network;
import com.madhouse.dsp.proctergamble.PGMadAds.Size;
import com.madhouse.dsp.proctergamble.PGMadAds.Version;
import com.madhouse.ssp.Constant;
import com.madhouse.ssp.avro.DSPResponse;
import com.madhouse.ssp.avro.MediaBid.Builder;
import com.madhouse.ssp.avro.DSPRequest;
import com.madhouse.ssp.avro.MediaRequest;
import com.madhouse.ssp.avro.Monitor;
import com.madhouse.ssp.avro.Track;
import com.madhouse.util.StringUtil;

public class ProcterGambleHandler extends DSPBaseHandler {

    @Override
    public HttpRequestBase packageBidRequest(Builder mediaBidBuilder, MediaMetaData mediaMetaData, PlcmtMetaData plcmtMetaData, AdBlockMetaData adBlockMetaData, PolicyMetaData policyMetaData, DSPBidMetaData dspBidMetaData) {
        MediaRequest.Builder builder=  mediaBidBuilder.getRequestBuilder();
        Device.Builder deviceBuilder = getDevice(builder);
        if (deviceBuilder == null) {
            return null;
        }
        Network.Builder networkBuilder = getNetwork(builder);
        if (networkBuilder == null) {
            return null;
        }
        
        
        DSPMappingMetaData dspMappingMetaData = CacheManager.getInstance().getDSPMapping(dspBidMetaData.getDspMetaData().getId(), plcmtMetaData.getId());
        String adspaceId = plcmtMetaData.getAdspaceKey();
        if (dspBidMetaData != null && !StringUtils.isEmpty(dspMappingMetaData.getMappingKey())) {
            adspaceId = dspMappingMetaData.getMappingKey();
        }
        AdSlot.Builder adSlotBuilder = getAdslot(builder, plcmtMetaData,adspaceId);
        if (adSlotBuilder == null) {
            return null;
        }
        
        
        App.Builder appBuilder = getApp(builder, adspaceId);
        if (appBuilder == null) {
            return null;
        }
        BidRequest.Builder pg = BidRequest.newBuilder()//
            .setPrice(policyMetaData.getDeliveryType() == Constant.DeliveryType.RTB ? plcmtMetaData.getBidFloor() : policyMetaData.getAdspaceInfoMap().get(plcmtMetaData.getId()).getBidFloor())//价格必填
            .setRequestId(dspBidMetaData.getDspBidBuilder().getRequestBuilder().getId())//
            .setApiVersion(Version.newBuilder().setMajor(2).setMinor(3))//
            .setApp(appBuilder)//
            .setDevice(deviceBuilder)//
            .setNetwork(networkBuilder)//
            .addAdslots(adSlotBuilder);

        logger.info("ProcterGamble request url:{}", pg.toString());
        
        HttpPost request = new HttpPost(dspBidMetaData.getDspMetaData().getBidUrl());
        request.setHeader(HTTP.CONTENT_TYPE, HTTP.OCTET_STREAM_TYPE);
        request.setEntity(new ByteArrayEntity(pg.build().toByteArray()));
        return request;
    }
    private com.madhouse.dsp.proctergamble.PGMadAds.Device.Builder getDevice(com.madhouse.ssp.avro.MediaRequest.Builder builder) {
        // Type
        Device.Builder deviceBuilder = Device.newBuilder().setType(Device.Type.PHONE);
        switch (builder.getOs()) {
            case Constant.OSType.ANDROID:
                deviceBuilder.setOs(Os.ANDROID);
                UdId.Builder udidBuilder = UdId.newBuilder();
                String aid = builder.getDpid();
                if (aid != null) {
                    udidBuilder.setAndroidId(aid);
                } else if (builder.getDid() != null) {
                    udidBuilder.setAndroidId(builder.getDid());
                }
                
                String imei = builder.getDid();
                if (imei != null) {
                    deviceBuilder = deviceBuilder.setUdid(udidBuilder.setImei(imei));
                } else if (builder.getMac() != null) {
                    deviceBuilder = deviceBuilder.setUdid(udidBuilder.setMac(builder.getMac()));
                }
                
                break;
            case Constant.OSType.IOS:
                deviceBuilder.setOs(Os.IOS);
                String idfa = builder.getIfa();
                if (idfa != null) {
                    deviceBuilder.setUdid(UdId.newBuilder().setIdfa(idfa));
                } else if (builder.getMac() != null) {
                    deviceBuilder.setUdid(UdId.newBuilder().setMac(builder.getMac()));
                } else {
                    return null;
                }
                break;
            default:
                return null;
        }
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

    private com.madhouse.dsp.proctergamble.PGMadAds.Network.Builder getNetwork(com.madhouse.ssp.avro.MediaRequest.Builder builder) {
        
        // 网络类型
        Network.Builder networkBuilder = Network.newBuilder();
        //ip
        String ip = builder.getIp();
        if (StringUtils.isEmpty(ip)) {
            return null;
        }
        networkBuilder.setIpv4(ip);
        return networkBuilder;
    }
    
    
    private AdSlot.Builder getAdslot(MediaRequest.Builder builder, PlcmtMetaData plcmtMetaData, String adspaceId) {
        // adSlot
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
        appBuilder.setId(adspaceId);
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
    public boolean parseBidResponse(HttpResponse httpResponse, DSPBidMetaData dspBidMetaData) {
        DSPResponse.Builder dspResponse = DSPResponse.newBuilder();
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        try {
            if(statusCode == Constant.StatusCode.OK){
                BidResponse bidResponse = BidResponse.parseFrom(EntityUtils.toByteArray(httpResponse.getEntity()));
                logger.info("ProcterGambleHandler Response is:{}",bidResponse.toString());
                List<Ad> ads = bidResponse.getAdsList();
                if (ads != null && ads.size() != 0) { // 只取第一个广告
                    Ad ad = ads.get(0);
                    if (ad != null && ad.getMaterialMeta() != null) {
                        DSPRequest.Builder dspRequest = dspBidMetaData.getDspBidBuilder().getRequestBuilder();
                        MaterialMeta materialMeta = ad.getMaterialMeta();
                        //dspResponse.setCid(value)
                        
                        dspResponse.setId(dspRequest.getId());
                        dspResponse.setImpid(dspRequest.getImpid());
                        
                        dspResponse.setLpgurl(materialMeta.getClickUrl());
                        dspResponse.setDesc(materialMeta.getDescription1());
                        dspResponse.setTitle(materialMeta.getTitle());
                        dspResponse.setIcon(materialMeta.getIconUrl());
                        
                        Monitor.Builder monitor = Monitor.newBuilder();
                        List<Track> tracks=new ArrayList<>();
                        for (int i = 0; i < materialMeta.getWinNoticeUrlCount(); i++) {
                            String noticeurl = materialMeta.getWinNoticeUrl(i);
                            if (noticeurl != null && noticeurl.contains("{AUCTION_PRICE}")) {//替换保洁的price,同时保存transactionId
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
                        monitor.setImpurl(tracks);
                        dspResponse.setMonitorBuilder(monitor);
                        dspResponse.getAdm().add(materialMeta.getMediaUrl());
                        dspResponse.setActtype(Constant.ActionType.OPEN_IN_APP);
                        dspBidMetaData.getDspBidBuilder().setStatus(Constant.StatusCode.OK);
                        dspBidMetaData.getDspBidBuilder().setResponse(dspResponse.build());
                        return true;
                    }
                }
            }
            dspBidMetaData.getDspBidBuilder().setStatus(Constant.StatusCode.INTERNAL_ERROR);
        } catch (Exception e) {
            logger.error("ProcterGambleHandler Response error:{}", dspBidMetaData.getDspBidBuilder().toString());
            dspBidMetaData.getDspBidBuilder().setStatus(Constant.StatusCode.INTERNAL_ERROR);
            return false;
        }
        return false;
    }
    
}
