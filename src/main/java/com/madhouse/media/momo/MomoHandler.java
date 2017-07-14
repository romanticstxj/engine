package com.madhouse.media.momo;

import static com.madhouse.media.momo.MomoExchange.NativeFormat.FEED_LANDING_PAGE_LARGE_IMG;
import static com.madhouse.media.momo.MomoExchange.NativeFormat.FEED_LANDING_PAGE_VIDEO;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.madhouse.cache.CacheManager;
import com.madhouse.cache.MediaBidMetaData;
import com.madhouse.cache.PlcmtMetaData;
import com.madhouse.media.MediaBaseHandler;
import com.madhouse.media.momo.MomoExchange.BidRequest;
import com.madhouse.media.momo.MomoExchange.BidResponse;
import com.madhouse.ssp.Constant;
import com.madhouse.ssp.avro.MediaBid;
import com.madhouse.ssp.avro.MediaRequest;
import com.madhouse.ssp.avro.MediaResponse;
import com.madhouse.ssp.avro.Track;
import com.madhouse.util.ObjectUtils;



public class MomoHandler extends MediaBaseHandler {
    
    @Override
    public boolean parseMediaRequest(HttpServletRequest req, MediaBidMetaData mediaBidMetaData, HttpServletResponse resp) {
        
        try {
            MomoExchange.BidRequest bidRequest = MomoExchange.BidRequest.parseFrom(IOUtils.toByteArray(req.getInputStream()));
            logger.debug("debug Request params is : {}", bidRequest.toString());
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
    
    /** 
    * TODO (这里用一句话描述这个方法的作用)
    * @param bidRequest
    * @return
    */
    private MediaRequest conversionToPremiumMADDataModel(BidRequest bidRequest) {
        MediaRequest.Builder mediaRequest = MediaRequest.newBuilder();
        
        MomoExchange.BidRequest.Imp imp = bidRequest.getImpList().get(0);
        
        mediaRequest.setBid(bidRequest.getId());
        
        /**
         *  陌陌使用的字段 联调测试使用非必填
         * */
        if(bidRequest.getIsTest()){
            mediaRequest.setTest(Constant.Test.REAL);
        }else{
            mediaRequest.setTest(Constant.Test.SIMULATION);
        }

        String campainType = getCampainType(imp.getNative());
        String adspaceKey = new StringBuffer().append("MM:").append(imp.getSlotid()).append(":").append(bidRequest.getDevice().getOs().toLowerCase()).append(":").append(campainType).toString();
        PlcmtMetaData plcmtMetaData = CacheManager.getInstance().getPlcmtMetaData(adspaceKey);
        if (plcmtMetaData != null) {
            mediaRequest.setAdspacekey(plcmtMetaData.getAdspaceKey());
        } else {
            plcmtMetaData = CacheManager.getInstance().getPlcmtMetaData("MM:0:0");
            if(plcmtMetaData != null){
                mediaRequest.setAdspacekey(plcmtMetaData.getAdspaceKey());
            }else{
                return null;
            }
        }
        mediaRequest.setBidfloor((new Double(imp.getBidfloor() * 100)).intValue());
        MomoExchange.BidRequest.App app = bidRequest.getApp();
        if(!ObjectUtils.isEmpty(app)) {
            mediaRequest.setBundle(app.getBundle());
            mediaRequest.setName(app.getName());
        }
       
        
        MomoExchange.BidRequest.Device device =  bidRequest.getDevice();
        
        
        mediaRequest.setOsv(device.getOsv());
        mediaRequest.setMake(device.getMake());
        mediaRequest.setModel(device.getModel());
        mediaRequest.setMacmd5(device.getMacmd5());
        
        
        
        String ua = device.getUa();
        if (!StringUtils.isEmpty(ua)) {
            mediaRequest.setUa(ua);
        }
        String ip = device.getIp();
        if (!StringUtils.isEmpty(ip)) {
            mediaRequest.setIp(ip);
        }
        if(ObjectUtils.isNotEmpty(device.getGeo().getLon())){
            mediaRequest.setLat((float)device.getGeo().getLon());
        }
        if(ObjectUtils.isNotEmpty(device.getGeo().getLon())){
            mediaRequest.setLon((float)device.getGeo().getLat());
        }
        
        mediaRequest.setCarrier(Constant.Carrier.UNKNOWN);
        
        switch (device.getConnectiontype()) {
            case CONNECTION_UNKNOWN:
            case CELL_UNKNOWN:
            case ETHERNET:
                mediaRequest.setConnectiontype(Constant.ConnectionType.ETHERNET);
                break;
            case WIFI:
                mediaRequest.setConnectiontype(Constant.ConnectionType.WIFI);
                break;
            case CELL_2G:
                mediaRequest.setConnectiontype(Constant.ConnectionType._2G);
                break;
            case CELL_3G:
                mediaRequest.setConnectiontype(Constant.ConnectionType._3G);
                break;
            case CELL_4G:
                mediaRequest.setConnectiontype(Constant.ConnectionType._4G);
                break;
        }
        if("ios".equalsIgnoreCase(device.getOs().toLowerCase())){
            mediaRequest.setOs(Constant.OSType.IOS);
            String did = device.getDid();
            if(!StringUtils.isEmpty(did)){
                mediaRequest.setDid(did);
            }
            String didmd5 = device.getDidmd5();
            if(!StringUtils.isEmpty(didmd5)){
                mediaRequest.setDidmd5(didmd5);
            }
        }else{
            mediaRequest.setOs(Constant.OSType.ANDROID);
            String did = device.getDid();
            if(!StringUtils.isEmpty(did)){
                mediaRequest.setIfa(did);
            }
        }
        mediaRequest.setType(Constant.MediaType.APP);
        return mediaRequest.build();
    }

    private int validateRequiredParam(BidRequest bidRequest) {
        if(ObjectUtils.isNotEmpty(bidRequest)){
            /**
             *  陌陌请求的唯一ID标识
             */
            if (StringUtils.isEmpty(bidRequest.getId())) {
                logger.debug("MomoExchange.bidRequest.id is missing");
                return Constant.StatusCode.BAD_REQUEST;
            }
            /**
             *  陌陌请求协议版本
             */
            if (StringUtils.isEmpty(bidRequest.getVersion())) {
                logger.debug("MomoExchange.bidRequest.version is missing");
                return Constant.StatusCode.BAD_REQUEST;
            }
            /**
             *  Impression信息字段处理
             * */
            List<MomoExchange.BidRequest.Imp> impList = bidRequest.getImpList();
            if (impList == null || impList.size() < 1) {
                logger.debug("MomoExchange.bidRequest.Imp is missing");
                return Constant.StatusCode.BAD_REQUEST;
            }
            MomoExchange.BidRequest.Imp imp = impList.get(0);
            if(StringUtils.isEmpty(imp.getId())){
                logger.debug("MomoExchange.bidRequest.Imp[0].id is missing");
                return Constant.StatusCode.BAD_REQUEST;
            }
            if(ObjectUtils.isEmpty(imp.getNative())){
                logger.debug("MomoExchange.bidRequest.Imp[0].Native is missing");
                return Constant.StatusCode.BAD_REQUEST;
            }else{
               MomoExchange.BidRequest.Imp.Native aNative =  imp.getNative();
               if(!aNative.getNativeFormatList().contains(FEED_LANDING_PAGE_LARGE_IMG)&&
                       !aNative.getNativeFormatList().contains(FEED_LANDING_PAGE_VIDEO)){
                   logger.debug("MomoExchange.bidRequest.Imp[0].Native is [img,video] is missing");
                   return Constant.StatusCode.BAD_REQUEST;
               }
            }
            if(StringUtils.isEmpty(imp.getSlotid())){
                logger.debug("MomoExchange.bidRequest.Imp[0].Slotidis missing");
                return Constant.StatusCode.BAD_REQUEST;
            }else{
                String campainType = getCampainType(imp.getNative());
                if(StringUtils.isEmpty(campainType)){
                    logger.debug("MomoExchange.bidRequest.Imp[0].Native.Type is missing");
                    return Constant.StatusCode.BAD_REQUEST;
                }
            }
            MomoExchange.BidRequest.Device device =  bidRequest.getDevice();
            if(ObjectUtils.isEmpty(device)){
                logger.debug("MomoExchange.bidRequest.Device is missing");
                return Constant.StatusCode.BAD_REQUEST;
            }
            return Constant.StatusCode.OK;
        }
        return Constant.StatusCode.BAD_REQUEST;
    }
    private String getCampainType(MomoExchange.BidRequest.Imp.Native aNative){
        Random random = new Random();
        if(aNative.getNativeFormatList().contains(FEED_LANDING_PAGE_LARGE_IMG) && aNative.getNativeFormatList().contains(FEED_LANDING_PAGE_VIDEO)){
               List<MomoExchange.NativeFormat> list = new ArrayList<>();
               list.add(FEED_LANDING_PAGE_LARGE_IMG);
               list.add(FEED_LANDING_PAGE_VIDEO);
               int i = random.nextInt(2);
               switch (list.get(i)){
                   case FEED_LANDING_PAGE_LARGE_IMG:
                       return  "img";
                   case FEED_LANDING_PAGE_VIDEO:
                       return "video";
               }
               return "";
        }else if(aNative.getNativeFormatList().contains(FEED_LANDING_PAGE_LARGE_IMG) && !aNative.getNativeFormatList().contains(FEED_LANDING_PAGE_VIDEO)){
            return "img";
        }else if(!aNative.getNativeFormatList().contains(FEED_LANDING_PAGE_LARGE_IMG) && aNative.getNativeFormatList().contains(FEED_LANDING_PAGE_VIDEO)){
            return "video";
        }
        return "";
    }
    @Override
    public boolean packageMediaResponse(MediaBidMetaData mediaBidMetaData, HttpServletResponse resp) {
        try {
            if (mediaBidMetaData != null && mediaBidMetaData.getMediaBidBuilder() != null) {
                MediaBid.Builder mediaBid = mediaBidMetaData.getMediaBidBuilder();
                if (mediaBid.getResponseBuilder() != null && mediaBid.getStatus() == Constant.StatusCode.OK) {
                    MomoExchange.BidResponse bidResponse = convertToMomoResponse(mediaBidMetaData);
                    if(null != bidResponse){
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

    private BidResponse convertToMomoResponse(MediaBidMetaData mediaBidMetaData) {
        
        
        MediaResponse mediaResponse= mediaBidMetaData.getMediaBidBuilder().getResponse();
        MediaRequest mediaRequest= mediaBidMetaData.getMediaBidBuilder().getRequest();
        MomoExchange.BidRequest bidRequest = (BidRequest)mediaBidMetaData.getRequestObject();
        
        MomoExchange.BidResponse.SeatBid.Bid.NativeCreative.Builder nativeCreativeBuilder = MomoExchange.BidResponse.SeatBid.Bid.NativeCreative.newBuilder();
        nativeCreativeBuilder.setTitle(mediaResponse.getTitle().toString());
        nativeCreativeBuilder.setDesc(mediaResponse.getDesc().toString());
        nativeCreativeBuilder.setLogo(MomoExchange.BidResponse.SeatBid.Bid.NativeCreative.Image.newBuilder());
        if(null != mediaResponse.getDuration() && mediaResponse.getDuration()>0){
            nativeCreativeBuilder.addVideo(MomoExchange.BidResponse.SeatBid.Bid.NativeCreative.Video.newBuilder().setUrl(mediaResponse.getAdm().get(0).toString()));          //视频
            nativeCreativeBuilder.setCardTitle(mediaResponse.getTitle().toString()); //无单独的行动卡标题 使用视频物料的标题
            nativeCreativeBuilder.setCardDesc(mediaResponse.getDesc().toString());   //无单独的行动卡副标题 使用视频物料的副标题
            nativeCreativeBuilder.setLandingpageUrl(MomoExchange.BidResponse.SeatBid.Bid.Link.newBuilder().setUrl(mediaResponse.getAdm().get(0).toString()));  //落地页
            if(bidRequest.getImpList().get(0).getNative().getNativeFormatList().contains(FEED_LANDING_PAGE_VIDEO)){
                nativeCreativeBuilder.setNativeFormat(FEED_LANDING_PAGE_VIDEO);
            }
        }else{
            MomoExchange.BidResponse.SeatBid.Bid.NativeCreative.Image.Builder imageBuilder = MomoExchange.BidResponse.SeatBid.Bid.NativeCreative.Image.newBuilder();
            imageBuilder.setHeight(mediaRequest.getH());
            imageBuilder.setWidth(mediaRequest.getW());
            imageBuilder.setUrl(mediaResponse.getAdm().get(0).toString());
            nativeCreativeBuilder.addImage(imageBuilder);           //广告图片
            nativeCreativeBuilder.setLandingpageUrl(MomoExchange.BidResponse.SeatBid.Bid.Link.newBuilder().setUrl(mediaResponse.getAdm().get(0).toString()));  //落地页
            if(bidRequest.getImpList().get(0).getNative().getNativeFormatList().contains(FEED_LANDING_PAGE_LARGE_IMG)){
                nativeCreativeBuilder.setNativeFormat(FEED_LANDING_PAGE_LARGE_IMG);
            }
        }
        
        
        MomoExchange.BidResponse.SeatBid.Bid.Builder bidBuilder = MomoExchange.BidResponse.SeatBid.Bid.newBuilder();
        bidBuilder.setId(mediaBidMetaData.getMediaBidBuilder().getImpid().toString());
        bidBuilder.setImpid(bidRequest.getImpList().get(0).getId());
        bidBuilder.setPrice(mediaBidMetaData.getMediaBidBuilder().getRequest().getBidfloor());
        bidBuilder.setCid(mediaResponse.getCid().toString());
        bidBuilder.setAdid(mediaResponse.getAdmid().toString());   //广告位id
        bidBuilder.setCrid(mediaResponse.getCrid().toString());  //物料id
        bidBuilder.addCat("");  //premiummad暂不支持 默认为空
        bidBuilder.setNativeCreative(nativeCreativeBuilder);
        /**
         *组装点击和展示监播url
         */
        List<Track> imgtracking = mediaResponse.getMonitor().getImpurl();
        if (imgtracking != null && imgtracking.size() != 0) {
            for (Track track : imgtracking) {
                bidBuilder.addClicktrackers(track.toString());
            }
        }
        List<CharSequence> thclkurl = mediaResponse.getMonitor().getClkurl();
        if (thclkurl != null && thclkurl.size() != 0) {
            for (CharSequence thclk : thclkurl) {
                bidBuilder.addImptrackers(thclk.toString());
            }
        }
        
        MomoExchange.BidResponse.SeatBid.Builder seatBidBuilder = MomoExchange.BidResponse.SeatBid.newBuilder();
        seatBidBuilder.setSeat("premiummad");
        seatBidBuilder.addBid(bidBuilder);
        
        MomoExchange.BidResponse.Builder bidResposeBuilder = MomoExchange.BidResponse.newBuilder();
        bidResposeBuilder.setId(bidRequest.getId());
        bidResposeBuilder.addSeatbid(seatBidBuilder);
        
        logger.info("MoMO Response params is : {}", bidResposeBuilder.toString());
        return bidResposeBuilder.build();
    }
    
}