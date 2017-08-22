package com.madhouse.media.tencent;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.madhouse.cache.CacheManager;
import com.madhouse.cache.MediaBidMetaData;
import com.madhouse.cache.MediaMappingMetaData;
import com.madhouse.media.MediaBaseHandler;
import com.madhouse.media.tencent.GPBForDSP.Request;
import com.madhouse.media.tencent.GPBForDSP.Request.App;
import com.madhouse.media.tencent.GPBForDSP.Request.Device;
import com.madhouse.media.tencent.GPBForDSP.Request.Impression;
import com.madhouse.media.tencent.GPBForDSP.Response;
import com.madhouse.ssp.Constant;
import com.madhouse.ssp.avro.MediaBid;
import com.madhouse.ssp.avro.MediaRequest;
import com.madhouse.ssp.avro.MediaResponse;
import com.madhouse.ssp.avro.MediaResponse.Builder;
import com.madhouse.util.ObjectUtils;

public class TencentHandler extends MediaBaseHandler {
    
    @Override
    public boolean parseMediaRequest(HttpServletRequest req, MediaBidMetaData mediaBidMetaData, HttpServletResponse resp) {
        
        try {
            GPBForDSP.Request bidRequest = GPBForDSP.Request.parseFrom(IOUtils.toByteArray(req.getInputStream()));
            logger.info("TencentBidRequest Request params is : {}",bidRequest.toString());
            int status = validateRequiredParam(bidRequest);
            if(Constant.StatusCode.OK == status){
                MediaRequest mediaRequest = conversionToPremiumMADDataModel(bidRequest);
                if(mediaRequest != null){
                    mediaBidMetaData.getMediaBidBuilder().setRequest(mediaRequest);
                    mediaBidMetaData.setRequestObject(bidRequest);
                    return true;
                }
            }
            resp.setStatus(Constant.StatusCode.NO_CONTENT);
            return false;
        } catch (Exception e) {
            logger.error(e.toString() + "_Status_" + Constant.StatusCode.NO_CONTENT);
            resp.setStatus(Constant.StatusCode.NO_CONTENT);
            return false;
        }
    }
    
   
    private MediaRequest conversionToPremiumMADDataModel(Request bidRequest) {
        MediaRequest.Builder mediaRequest = MediaRequest.newBuilder();
        
        Impression impression =bidRequest.getImpression(0);
        Device device=bidRequest.getDevice();
        App app=bidRequest.getApp();
        StringBuilder sb = new StringBuilder();
        sb.append("TENC:");
        String TencAdspaceId = bidRequest.getImpression(0).getTagid();//腾讯广告位(广告位ID，同资源报表中的广告位ID，如 Ent_F_Width1)
        sb.append(TencAdspaceId).append(":");
        MediaMappingMetaData mappingMetaData = CacheManager.getInstance().getMediaMapping(sb.toString());
        if (mappingMetaData != null) {
            mediaRequest.setAdspacekey(mappingMetaData.getAdspaceKey());
        } else { 
            mappingMetaData = CacheManager.getInstance().getMediaMapping("TENC:0:");
            if(mappingMetaData != null){
                mediaRequest.setAdspacekey(mappingMetaData.getAdspaceKey());
            }else{
                return null;
            }
        }
        mediaRequest.setBid(bidRequest.getId());
        
        Integer w = null;
        Integer h = null;
        if(!impression.hasBanner()){
            w = impression.getBanner().getWidth();
            h = impression.getBanner().getHeight();
        }else if(!impression.hasVideo()){
            w = impression.getVideo().getWidth();
            h = impression.getVideo().getHeight();
        }
        mediaRequest.setW(w);
        mediaRequest.setH(h);
        mediaRequest.setDevicetype(Constant.DeviceType.UNKNOWN);
        String os = device.getOs();//iPhone.OS.9.3.2
        if (!StringUtils.isEmpty(os)) {
            if(os.toLowerCase().contains(TencentStatusCode.Os.OS_IOS)){
                mediaRequest.setOs(Constant.OSType.IOS);
                if(TencentStatusCode.Encryption.EXPRESS == device.getIdfaEnc()){
                    mediaRequest.setIfa(device.getIdfa());
                }
                if(!device.hasOpenudid()){
                    mediaRequest.setDpid(device.getOpenudid());
                }
            }else{
                mediaRequest.setOs(Constant.OSType.ANDROID);
                if(!device.hasImei()){
                    mediaRequest.setDidmd5(device.getImei());
                }
                if(!device.hasAndroidid()){
                    mediaRequest.setDpidmd5(device.getAndroidid());
                }
            }
        }
        if(!device.hasCarrier()){
            int carrier = device.getCarrier();
            switch (carrier) {
                case TencentStatusCode.Carrier.CHINA_MOBILE:
                    mediaRequest.setCarrier(Constant.Carrier.CHINA_MOBILE);
                    break;
                case TencentStatusCode.Carrier.CHINA_TELECOM:
                    mediaRequest.setCarrier(Constant.Carrier.CHINA_TELECOM);
                    break;
                case TencentStatusCode.Carrier.CHINA_UNICOM:
                    mediaRequest.setCarrier(Constant.Carrier.CHINA_UNICOM);
                    break;
                default:
                    mediaRequest.setCarrier(Constant.Carrier.UNKNOWN);
                    break;
            }
        }else{
            mediaRequest.setCarrier(Constant.Carrier.UNKNOWN);
        }
        if(!device.hasConnectiontype()){
            int connectiontype = device.getConnectiontype();
            switch (connectiontype) {
                case TencentStatusCode.ConnectionType.Ethernet:
                    mediaRequest.setConnectiontype(Constant.ConnectionType.ETHERNET);
                    break;
                case TencentStatusCode.ConnectionType._2G:
                    mediaRequest.setConnectiontype(Constant.ConnectionType._2G);
                    break;
                case TencentStatusCode.ConnectionType._3G:
                    mediaRequest.setConnectiontype(Constant.ConnectionType._3G);
                    break;
                case TencentStatusCode.ConnectionType._4G:
                    mediaRequest.setConnectiontype(Constant.ConnectionType._4G);
                    break;
                case TencentStatusCode.ConnectionType.WIFI:
                    mediaRequest.setConnectiontype(Constant.ConnectionType.WIFI);
                    break;
                default:
                    mediaRequest.setConnectiontype(Constant.ConnectionType.CELL);
                    break;
            }
        }else{
            mediaRequest.setConnectiontype(Constant.ConnectionType.CELL);
        }
        if (!device.hasOsv()) {
            mediaRequest.setOsv(device.getOs());
        }
        if(!device.hasMac()){
            mediaRequest.setMacmd5(device.getMac());
        }
        if (!app.hasName()) {
            mediaRequest.setName(app.getName());
        }
        if (!device.hasMake()) {
            mediaRequest.setMake(device.getMake());
        }
        if (!device.hasModel()) {
            mediaRequest.setModel(device.getModel());
        }
        if (!StringUtils.isEmpty(device.getUa())) {
            mediaRequest.setUa(device.getUa());
        }
        if (!StringUtils.isEmpty(device.getIp())) {
            mediaRequest.setIp(device.getIp());
        }
        if (!device.getGeo().hasLongitude()) {
            mediaRequest.setLat(device.getGeo().getLatitude());
        }
        if (!device.getGeo().hasLongitude()) {
            mediaRequest.setLon(device.getGeo().getLongitude());
        }
        mediaRequest.setType(bidRequest.hasSite() ? Constant.MediaType.APP : Constant.MediaType.SITE);
        return mediaRequest.build();
    }

    private int validateRequiredParam(Request bidRequest) {
        if (ObjectUtils.isNotEmpty(bidRequest)) {
            String id = bidRequest.getId();
            if (StringUtils.isEmpty(id)) {
                logger.debug("Tencent.bidRequest.id is missing");
                return Constant.StatusCode.BAD_REQUEST;
            }
            Impression impression =bidRequest.getImpression(0);
            if(ObjectUtils.isNotEmpty(impression)){
                if(impression.hasBanner()){
                    logger.debug("{}:Tencent.bidRequest.impression.Banner is missing",id);
                    return Constant.StatusCode.BAD_REQUEST;
                }else if(impression.hasVideo()){
                    logger.debug("{}:Tencent.bidRequest.impression.Video is missing",id);
                    return Constant.StatusCode.BAD_REQUEST;
                }
                if(!impression.hasBanner()){
                    if(impression.getBanner().hasWidth()){
                        logger.debug("{}:Tencent.bidRequest.impression.Banner.W is missing",id);
                        return Constant.StatusCode.BAD_REQUEST;
                    }
                    if(impression.getBanner().hasWidth()){
                        logger.debug("{}:Tencent.bidRequest.impression.Video.H is missing",id);
                        return Constant.StatusCode.BAD_REQUEST;
                    }
                }else if(!impression.hasVideo()){
                    if(impression.getVideo().hasWidth()){
                        logger.debug("{}:Tencent.bidRequest.impression.Banner.W is missing",id);
                        return Constant.StatusCode.BAD_REQUEST;
                    }
                    if(impression.getVideo().hasHeight()){
                        logger.debug("{}:Tencent.bidRequest.impression.Video.H is missing",id);
                        return Constant.StatusCode.BAD_REQUEST;
                    }
                }
            }else{
                logger.debug("{}:Tencent.bidRequest.impression is missing",id);
                return Constant.StatusCode.BAD_REQUEST;
            }
            if(bidRequest.hasDevice()){
                logger.debug("{}:Tencent.bidRequest.Device is missing",id);
                return Constant.StatusCode.BAD_REQUEST;
            }
            if(bidRequest.hasApp()){
                logger.debug("{}:Tencent.bidRequest.App is missing",id);
                return Constant.StatusCode.BAD_REQUEST;
            }
            return Constant.StatusCode.OK;
        }
        return Constant.StatusCode.BAD_REQUEST;
    }

    @Override
    public boolean packageMediaResponse(MediaBidMetaData mediaBidMetaData, HttpServletResponse resp) {
        try {
            if (mediaBidMetaData != null && mediaBidMetaData.getMediaBidBuilder() != null) {
                MediaBid.Builder mediaBid = mediaBidMetaData.getMediaBidBuilder();
                if (mediaBid.getResponseBuilder() != null && mediaBid.getStatus() == Constant.StatusCode.OK) {
                    GPBForDSP.Response bidResponse = convertToTencentResponse(mediaBidMetaData);
                    if(null != bidResponse){
                        resp.setContentType("application/octet-stream;charset=UTF-8");
                        resp.getOutputStream().write(bidResponse.toByteArray());
                        resp.setStatus(Constant.StatusCode.OK);
                        return true;
                    }
                } else {
                    resp.setStatus(mediaBid.getStatus());
                    return false;
                }
            } 
        } catch (Exception e) {
            logger.error(e.toString() + "_Status_" + Constant.StatusCode.NO_CONTENT);
            resp.setStatus(Constant.StatusCode.NO_CONTENT);
            return false;
        }
        resp.setStatus(Constant.StatusCode.NO_CONTENT);
        return false;
    }

    private Response convertToTencentResponse(MediaBidMetaData mediaBidMetaData) {
        GPBForDSP.Response.Builder responseBuiler = GPBForDSP.Response.newBuilder();
        GPBForDSP.Response.SeatBid.Builder seatBuilder =GPBForDSP.Response.SeatBid.newBuilder();
        
        GPBForDSP.Request bidRequest = (GPBForDSP.Request)mediaBidMetaData.getRequestObject();
        Builder mediaResponse = mediaBidMetaData.getMediaBidBuilder().getResponseBuilder();
        if(null != mediaResponse){
            responseBuiler.setId(mediaBidMetaData.getMediaBidBuilder().getImpid());
            GPBForDSP.Response.Bid.Builder bidResponseBuilder = GPBForDSP.Response.Bid.newBuilder();
            bidResponseBuilder.setId(mediaBidMetaData.getMediaBidBuilder().getImpid());
            bidResponseBuilder.setImpid(bidRequest.getImpression(0).getId());
            bidResponseBuilder.setAdid(mediaResponse.getAdmid());
            if (mediaResponse.getMonitorBuilder() != null && mediaResponse.getMonitorBuilder().getImpurl() != null && mediaResponse.getMonitorBuilder().getImpurl().size() > 0) {
                if (mediaResponse.getMonitorBuilder().getImpurl().size() >= 2) {
                    bidResponseBuilder.setExt2(mediaResponse.getMonitorBuilder().getImpurl().get(0).getUrl());//设置为dsp的监测
                }
                bidResponseBuilder.addDispExts(mediaResponse.getMonitorBuilder().getImpurl().get(mediaResponse.getMonitorBuilder().getImpurl().size() - 1).getUrl());//取最后一个（exchange自己的建波）
            }
            //如果有落地页就取值，如果没有，判断thclkurl的大小，如果size=2，第一条设置为ClickMonitor，第二条设置为ext2，；如果size=1，则只设置为ext2的值
            if (mediaResponse.getMonitorBuilder().getClkurl() != null ) {
                if (mediaResponse.getLpgurl() != null && mediaResponse.getLpgurl() != "") {
                    bidResponseBuilder.setExt(mediaResponse.getLpgurl());//落地页地址
                } else {
                    if (mediaResponse.getMonitorBuilder().getClkurl().size() >=2){
                        bidResponseBuilder.setExt(mediaResponse.getMonitorBuilder().getClkurl().get(0));
                    }
                }
                
                if (mediaResponse.getMonitorBuilder().getClkurl().size() >= 0) {
                    bidResponseBuilder.addClickExts(mediaResponse.getMonitorBuilder().getClkurl().get(mediaResponse.getMonitorBuilder().getClkurl().size() - 1));
                }
            }
            seatBuilder.addBid(bidResponseBuilder);//与request中的impression对应，可以对多个impression回复参与竞价，也可以对其中一部分回复参与竞价
            responseBuiler.addSeatbid(seatBuilder);
            logger.info("Tencent Response params is : {}", responseBuiler.toString());
        }else{
            return null;
        }
        return responseBuiler.build();
    }
    
}
