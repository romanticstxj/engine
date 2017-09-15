package com.madhouse.media.toutiao;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.madhouse.ssp.avro.Geo;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.madhouse.cache.CacheManager;
import com.madhouse.cache.MediaBidMetaData;
import com.madhouse.cache.MediaMappingMetaData;
import com.madhouse.media.MediaBaseHandler;
import com.madhouse.media.toutiao.TOUTIAOAds.AdType;
import com.madhouse.media.toutiao.TOUTIAOAds.BidRequest;
import com.madhouse.media.toutiao.TOUTIAOAds.SeatBid;
import com.madhouse.ssp.Constant;
import com.madhouse.ssp.avro.MediaBid;
import com.madhouse.ssp.avro.MediaRequest;
import com.madhouse.ssp.avro.MediaRequest.Builder;
import com.madhouse.ssp.avro.MediaResponse;
import com.madhouse.util.ObjectUtils;
import com.madhouse.util.StringUtil;

public class ToutiaoHandler extends MediaBaseHandler {
    
    @Override
    public boolean parseMediaRequest(HttpServletRequest req, MediaBidMetaData mediaBidMetaData, HttpServletResponse resp) {
        
        boolean isSandbox = false;
        String env = req.getHeader("env");
        if (env != null && env.equals("sandbox")) {
            isSandbox = true;
        }
        try {
            TOUTIAOAds.BidRequest bidRequest = TOUTIAOAds.BidRequest.parseFrom(IOUtils.toByteArray(req.getInputStream()));
            logger.info("ToutiaoHandler params is {} "+bidRequest.toString());
            int status =  validateRequiredParam(bidRequest);
            if(Constant.StatusCode.OK == status){
                MediaRequest.Builder request = conversionToPremiumMADDataModel(isSandbox,bidRequest);
                if(request != null){
                    mediaBidMetaData.getMediaBidBuilder().setRequestBuilder(request);
                    mediaBidMetaData.setRequestObject(bidRequest);
                    return true;
                }
            } 
            resp.setStatus(Constant.StatusCode.BAD_REQUEST);
            TOUTIAOAds.BidResponse.Builder builder=convertToutiaoResponse(bidRequest, mediaBidMetaData.getMediaBidBuilder().getRequestBuilder(), mediaBidMetaData, Constant.StatusCode.BAD_REQUEST);
            outputStreamWrite(builder, resp);
            return false;
        } catch (Exception e) {
            logger.error(e.toString() + "_Status_" + Constant.StatusCode.BAD_REQUEST);
            resp.setStatus(Constant.StatusCode.BAD_REQUEST);
            return false;
        }
    }
    private MediaRequest.Builder conversionToPremiumMADDataModel(boolean isSandbox, BidRequest bidRequest) {
        MediaRequest.Builder mediaRequest = MediaRequest.newBuilder();
        
        
        TOUTIAOAds.AdSlot adSlot = bidRequest.getAdslots(0);
        TOUTIAOAds.Pmp.Deal deal = bidRequest.getAdslots(0).getPmp().getDeals(0);
        TOUTIAOAds.AdSlot.Banner banner = adSlot.getBanner(0);
        TOUTIAOAds.Device device = bidRequest.getDevice();
        TOUTIAOAds.App app = bidRequest.getApp();
        TOUTIAOAds.Geo geo = device.getGeo();
        
        //广告请求流水号
        mediaRequest.setBid(bidRequest.getRequestId());
        TOUTIAOAds.AdType adtypeObj = getAdType(bidRequest);
        if (adtypeObj != null) {
            mediaRequest.setAdtype(adtypeObj.getNumber());
            MediaMappingMetaData mappingMetaData =  CacheManager.getInstance().getMediaMapping(new StringBuffer("TT:")
                        .append(adSlot.getChannelId())
                        .append(":")
                        .append(deal.getId())
                        .append(":")
                        .append(adtypeObj.getNumber())
                        .append(":")
                        .append(device.getOs().toLowerCase()).toString());
            if (mappingMetaData != null) {
                mediaRequest.setAdspacekey(mappingMetaData.getAdspaceKey());
                mediaRequest.setTest(Constant.Test.REAL);
                mediaRequest.setW(banner.getWidth());
                mediaRequest.setH(banner.getHeight());
            }
        }
        String supplierAdspaceKey = "";
        if(StringUtils.isEmpty(mediaRequest.getAdspacekey())){
            if (isSandbox) {// sandbox环境
                supplierAdspaceKey = "sandbox:TT:0:0";
                //模拟竞价，不计费
                mediaRequest.setTest(Constant.Test.SIMULATION);
            } else {
                supplierAdspaceKey = "TT:0:0";
                mediaRequest.setTest(Constant.Test.REAL);
            }
        }
        if (!"".equals(supplierAdspaceKey)) {
            MediaMappingMetaData mappingMetaData = CacheManager.getInstance().getMediaMapping(supplierAdspaceKey);
            if (mappingMetaData != null) {
                mediaRequest.setAdspacekey(mappingMetaData.getAdspaceKey());
                mediaRequest.setW(banner.getWidth());
                mediaRequest.setH(banner.getHeight());
            }else{
                return null; 
            }
        }
        mediaRequest.setBidfloor(adSlot.getBidFloor());
        //包名
        if(!StringUtils.isEmpty(app.getBundle())){
            mediaRequest.setBundle(app.getBundle());
        }
        //应用程序名称
        if(!StringUtils.isEmpty(app.getName())){
            mediaRequest.setName(app.getName());
        }
        if(!StringUtils.isEmpty(String.valueOf(deal.getId()))){
            mediaRequest.setDealid(String.valueOf(deal.getId()));
        }
        /****
         * 操作系统，今日头条API中为非必填参数可能为空
         */
        String os = device.getOs();
        if ("android".equalsIgnoreCase(os)){
            mediaRequest.setOs(Constant.OSType.ANDROID);
            mediaRequest.setDid(device.getDeviceId());
            mediaRequest.setDidmd5(device.getDeviceIdMd5());
            mediaRequest.setDpid(device.getAndroidId());
        } else if ("ios".equalsIgnoreCase(os)){
            mediaRequest.setOs(Constant.OSType.IOS);
            mediaRequest.setIfa(device.getDeviceId());
        }else{
            mediaRequest.setOs(Constant.OSType.UNKNOWN);
        }
        //设备运营商
        switch (device.getCarrier()) {
            case ToutiaoStatusCode.Carrier.CHINA_MOBILE:
                mediaRequest.setCarrier(Constant.Carrier.CHINA_MOBILE);
                break;
            case ToutiaoStatusCode.Carrier.CHINA_UNICOM:
                mediaRequest.setCarrier(Constant.Carrier.CHINA_UNICOM);
                break;
            case ToutiaoStatusCode.Carrier.CHINA_TELECOM:
                mediaRequest.setCarrier(Constant.Carrier.CHINA_TELECOM);
                break;
            default:
                mediaRequest.setCarrier(Constant.Carrier.UNKNOWN);
                break;
        }
        //连网方式
        switch (device.getConnectionType()) {
            case Honeycomb:
                mediaRequest.setConnectiontype(Constant.ConnectionType._3G);
                break;
            case WIFI:
                mediaRequest.setConnectiontype(Constant.ConnectionType.WIFI);
                break;
            case UNKNOWN:
                mediaRequest.setConnectiontype(Constant.ConnectionType.UNKNOWN);
                break;
            case NT_2G:
                mediaRequest.setConnectiontype(Constant.ConnectionType._2G);
                break;
            case NT_4G:
                mediaRequest.setConnectiontype(Constant.ConnectionType._4G);
                break;
            default:
                mediaRequest.setCarrier(Constant.ConnectionType.CELL);
                break;
        }
        //设备类型
        switch (device.getDeviceType()) {
            case PHONE:
                mediaRequest.setDevicetype(Constant.DeviceType.PHONE);
                break;
            default:
                mediaRequest.setCarrier(Constant.DeviceType.UNKNOWN);
                break;
        }
        //操作系统的版本
        if(!StringUtils.isEmpty(device.getOsv())){
            mediaRequest.setOsv(device.getOsv());
        }
        if(!StringUtils.isEmpty(device.getIp())){
            mediaRequest.setIp(device.getIp());
        }
        if(!StringUtils.isEmpty(device.getUa())){
            mediaRequest.setUa(device.getUa());
        }
        //设备型号
        if(!StringUtils.isEmpty(device.getModel())){
            mediaRequest.setModel(device.getModel());
        }
        if(!StringUtils.isEmpty(device.getMake())){
            mediaRequest.setMake(device.getMake());
        }
        /**
         * 赋值地理位置信息
         */
        if (ObjectUtils.isNotEmpty(geo)) {
            Geo.Builder vargeo = Geo.newBuilder();
            //经度
            vargeo.setLon((float)geo.getLon());
            //纬度
            vargeo.setLat((float)geo.getLat());
            mediaRequest.setGeoBuilder(vargeo);
        }
        mediaRequest.setType(Constant.MediaType.APP);
        logger.info("Toutiao convert mediaRequest is : {}", JSON.toJSONString(mediaRequest));
        return mediaRequest;
    }
    /**
     * 根据广告位的宽度和高度返回今日头条的广告类型，头条只会传1，2，6，如果有多个，就随机取一个
     * @param bidRequest
     * @return
     */
    private AdType getAdType(BidRequest bidRequest) {
        if (bidRequest.getAdslots(0).getAdTypeCount() < 1) {
            return null;
        }

        List<TOUTIAOAds.AdType> needAdTypes = new ArrayList<TOUTIAOAds.AdType>();

        List<TOUTIAOAds.AdType> adTypes = bidRequest.getAdslots(0).getAdTypeList();
        for (TOUTIAOAds.AdType adType : adTypes) {
            if (adType.getNumber() == 1 || adType.getNumber() == 2 ) {
                needAdTypes.add(adType);
            }
        }

        if (needAdTypes.size() < 1) {
            return null;
        }

        int index = StringUtil.random.nextInt(needAdTypes.size());
        return needAdTypes.get(index);
    }
    private int validateRequiredParam(TOUTIAOAds.BidRequest bidRequest) {
        if (ObjectUtils.isNotEmpty(bidRequest)) {
            if (StringUtils.isEmpty(bidRequest.getRequestId())) {
                logger.warn("RequestId is missing" );
                return Constant.StatusCode.BAD_REQUEST;
            }
            if (StringUtils.isEmpty(bidRequest.getApiVersion())) {
                logger.warn("{}:ApiVersion is missing",bidRequest.getRequestId());
                return Constant.StatusCode.BAD_REQUEST;
            }

            //不支持多条广告位的请求
            if (bidRequest.getAdslotsCount() != 1) {
                logger.warn("{}:AdslotsCount is size",bidRequest.getRequestId());
                return Constant.StatusCode.BAD_REQUEST;
            } else {
                TOUTIAOAds.AdSlot adSlot = bidRequest.getAdslots(0);
                if (StringUtils.isEmpty(adSlot.getId())) {
                    logger.warn("{}:adSlot.id is missing",bidRequest.getRequestId());
                    return Constant.StatusCode.BAD_REQUEST;
                }
                if (adSlot.getBannerCount() != 1) {
                    logger.warn("{}:adSlot.banner is missing",bidRequest.getRequestId());
                    return Constant.StatusCode.BAD_REQUEST;
                }
                if (adSlot.getAdTypeCount() == 0) {
                    logger.warn("{}:adSlot.AdType is missing",bidRequest.getRequestId());
                    return Constant.StatusCode.BAD_REQUEST;
                }
                //TODO AdSlot 对象的PMP的Deals对象集合 未判断DEAL.id的值不能为空
                TOUTIAOAds.Pmp.Deal deal = adSlot.getPmp().getDeals(0);
                if (deal == null) {
                    logger.warn("{}:Pmp.Deal is missing",bidRequest.getRequestId());
                    return Constant.StatusCode.BAD_REQUEST;
                }
                TOUTIAOAds.AdSlot.Banner banner = adSlot.getBanner(0);

                //广告位的尺寸，向第三方请求必填的参数
                if (banner.getHeight() < 0) {
                    logger.warn("{}:adSlot.Banner.Height is missing",bidRequest.getRequestId());
                    return Constant.StatusCode.BAD_REQUEST;
                }
                if (banner.getWidth() < 0) {
                    logger.warn("{}:adSlot.Banner.Width is missing",bidRequest.getRequestId());
                    return Constant.StatusCode.BAD_REQUEST;

                }
                if (banner.getPos() == null) {
                    logger.warn("{}:AdSlot.Banner.Pos is missing",bidRequest.getRequestId());
                    return Constant.StatusCode.BAD_REQUEST;

                }
            }

            TOUTIAOAds.App app = bidRequest.getApp();
            if (ObjectUtils.isEmpty(app)) {
                logger.warn("{}:App is missing",bidRequest.getRequestId());
                return Constant.StatusCode.BAD_REQUEST;
            } else {
                if (StringUtils.isEmpty(app.getId())) {
                    logger.warn("{}:App.id is missing",bidRequest.getRequestId());
                    return Constant.StatusCode.BAD_REQUEST;
                }
            }

            TOUTIAOAds.Device device = bidRequest.getDevice();
            if (ObjectUtils.isEmpty(device)) {
                logger.warn("{}:App is missing",bidRequest.getRequestId());
                return Constant.StatusCode.BAD_REQUEST;
            }
            return Constant.StatusCode.OK;
        }
        return Constant.StatusCode.BAD_REQUEST;
    }
    @Override
    public boolean packageMediaResponse(MediaBidMetaData mediaBidMetaData, HttpServletResponse resp) {
        TOUTIAOAds.BidResponse.Builder builder = TOUTIAOAds.BidResponse.newBuilder();
        TOUTIAOAds.BidRequest bidRequest = (TOUTIAOAds.BidRequest) mediaBidMetaData.getRequestObject();
        if (mediaBidMetaData != null && mediaBidMetaData.getMediaBidBuilder() != null) {
            MediaBid.Builder mediaBid = mediaBidMetaData.getMediaBidBuilder();
            try {
                if (mediaBid.getResponseBuilder() != null && mediaBid.getStatus() == Constant.StatusCode.OK) {
                    if(!StringUtils.isEmpty(mediaBidMetaData.getMediaBidBuilder().getResponseBuilder().getCrid())){
                        resp.setStatus(Constant.StatusCode.OK);
                        builder=convertToutiaoResponse(bidRequest, mediaBidMetaData.getMediaBidBuilder().getRequestBuilder(), mediaBidMetaData, Constant.StatusCode.OK);
                    } else {
                        resp.setStatus(Constant.StatusCode.NO_CONTENT);
                        builder=convertToutiaoResponse(bidRequest, mediaBidMetaData.getMediaBidBuilder().getRequestBuilder(), mediaBidMetaData, Constant.StatusCode.NO_CONTENT);
                    }
                } else if(mediaBid.getStatus() == Constant.StatusCode.NO_CONTENT) {
                    resp.setStatus(Constant.StatusCode.NO_CONTENT);
                    builder=convertToutiaoResponse(bidRequest, mediaBidMetaData.getMediaBidBuilder().getRequestBuilder(), mediaBidMetaData, Constant.StatusCode.NO_CONTENT);
                }else {
                    resp.setStatus(Constant.StatusCode.BAD_REQUEST);
                    builder=convertToutiaoResponse(bidRequest, mediaBidMetaData.getMediaBidBuilder().getRequestBuilder(), mediaBidMetaData, Constant.StatusCode.BAD_REQUEST);
                }
            } catch (Exception e) {
                logger.error(e.toString() + "_Status_" + Constant.StatusCode.BAD_REQUEST);
                return outputStreamWrite(builder, resp);
            }
        }
        return outputStreamWrite(builder, resp);
        
    }
    private boolean outputStreamWrite(TOUTIAOAds.BidResponse.Builder builder, HttpServletResponse resp)  {
        try {
            resp.setContentType("application/octet-stream;charset=UTF-8");
            resp.getOutputStream().write(builder.build().toByteArray());
        } catch (Exception e) {
            logger.error(e.toString() + "_Status_" + Constant.StatusCode.NO_CONTENT);
            return false;
        }
        return true;
    }
    private TOUTIAOAds.BidResponse.Builder convertToutiaoResponse(TOUTIAOAds.BidRequest bidRequest,Builder builder,MediaBidMetaData mediaBidMetaData,int code) {
        TOUTIAOAds.BidResponse.Builder bidResposeBuilder = TOUTIAOAds.BidResponse.newBuilder();
        bidResposeBuilder.setRequestId(bidRequest.getRequestId());
        if(Constant.StatusCode.OK == code){
            bidResposeBuilder.addSeatbids(getSeatBid(bidRequest, builder, mediaBidMetaData));
        } else {
            bidResposeBuilder.setErrorCode(code);
        }
        logger.info("Toutiao Response params is : {}", bidResposeBuilder.toString());
        return bidResposeBuilder;
    }
    private SeatBid getSeatBid(BidRequest bidRequest, Builder builder,MediaBidMetaData mediaBidMetaData) {
        TOUTIAOAds.SeatBid.Builder seatBidBuilder = TOUTIAOAds.SeatBid.newBuilder();
        TOUTIAOAds.MaterialMeta.Builder materialMetaBuilder = TOUTIAOAds.MaterialMeta.newBuilder();
        TOUTIAOAds.MaterialMeta.ExternalMeta.Builder externalMetaBuilder = TOUTIAOAds.MaterialMeta.ExternalMeta.newBuilder();
        TOUTIAOAds.MaterialMeta.ImageMeta.Builder imageMetaBuilder = TOUTIAOAds.MaterialMeta.ImageMeta.newBuilder();
        MediaResponse.Builder mediaResponse = mediaBidMetaData.getMediaBidBuilder().getResponseBuilder();
        TOUTIAOAds.Bid.Builder bidBuilder = TOUTIAOAds.Bid.newBuilder();
        bidBuilder.setId(mediaBidMetaData.getMediaBidBuilder().getImpid()); 
        bidBuilder.setAdid(!StringUtils.isEmpty(mediaResponse.getCrid()) ? Long.parseLong(mediaResponse.getCrid()):0);
        bidBuilder.setAdslotId(bidRequest.getAdslots(0).getId());
        bidBuilder.setPrice(mediaResponse.getPrice());
        
        for (AdType adtype : bidRequest.getAdslots(0).getAdTypeList()) {
            if(builder.getAdtype() == adtype.getNumber()){
                materialMetaBuilder.setAdType(adtype);
            }
        }
        imageMetaBuilder.setHeight(builder.getH());
        imageMetaBuilder.setWidth(builder.getW());
        imageMetaBuilder.setUrl(mediaResponse.getAdm().get(0));
        materialMetaBuilder.setImageBanner(imageMetaBuilder);

        //win的竞价成功通知
        materialMetaBuilder.setNurl(ToutiaoStatusCode.Url.URL.replace("{adspaceid}", builder.getAdspacekey()));
        //信息流落地页广告和详情页图文为必须返回
        materialMetaBuilder.setSource(StringUtils.isEmpty(mediaResponse.getDesc()) ? "" : mediaResponse.getDesc());
        materialMetaBuilder.setTitle(StringUtils.isEmpty(mediaResponse.getTitle()) ? "" : mediaResponse.getTitle());
        externalMetaBuilder.setUrl(mediaResponse.getLpgurl());
        materialMetaBuilder.setExternal(externalMetaBuilder);        
        for (String clk : mediaResponse.getMonitorBuilder().getClkurl()) {
            materialMetaBuilder.addClickUrl(clk);
        }
        for (com.madhouse.ssp.avro.Track imp : mediaResponse.getMonitorBuilder().getImpurl()) {
            materialMetaBuilder.addShowUrl(imp.getUrl());
        }
        bidBuilder.setCreative(materialMetaBuilder.build());
        seatBidBuilder.addAds(bidBuilder);
        return seatBidBuilder.build();
    }
    
}
