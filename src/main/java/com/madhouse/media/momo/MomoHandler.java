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

import com.alibaba.fastjson.JSON;
import com.madhouse.cache.CacheManager;
import com.madhouse.cache.MediaBidMetaData;
import com.madhouse.cache.MediaMappingMetaData;
import com.madhouse.media.MediaBaseHandler;
import com.madhouse.media.momo.MomoBidRequest.Device;
import com.madhouse.media.momo.MomoBidRequest.Impression;
import com.madhouse.media.momo.MomoExchange.BidRequest;
import com.madhouse.media.momo.MomoExchange.BidResponse;
import com.madhouse.media.momo.MomoResponse.Bid;
import com.madhouse.ssp.Constant;
import com.madhouse.ssp.avro.MediaBid;
import com.madhouse.ssp.avro.MediaRequest;
import com.madhouse.ssp.avro.MediaRequest.Builder;
import com.madhouse.ssp.avro.MediaResponse;
import com.madhouse.ssp.avro.Track;
import com.madhouse.util.HttpUtil;
import com.madhouse.util.ObjectUtils;



public class MomoHandler extends MediaBaseHandler {
    
    @Override
    public boolean parseMediaRequest(HttpServletRequest req, MediaBidMetaData mediaBidMetaData, HttpServletResponse resp) {
        
        try {
          //开屏是json格式，信息流是protofu格式
            if(null != req.getContentType()){
                MediaRequest mediaRequest =new MediaRequest();
                if(req.getContentType().equalsIgnoreCase("application/json")){
                    String bytes = HttpUtil.getRequestPostBytes(req);
                    MomoBidRequest bidRequest = JSON.parseObject(bytes, MomoBidRequest.class);
                    logger.info("Momo Request params is : {}",JSON.toJSONString(bidRequest));
                    int status = validateParam(bidRequest);
                    if(Constant.StatusCode.OK == status){
                        mediaRequest = conversionToPremiumMADData(bidRequest);
                        if(mediaRequest != null){
                            mediaBidMetaData.getMediaBidBuilder().setRequest(mediaRequest);
                            mediaBidMetaData.setRequestObject(new Object[]{MomoStatusCode.Type.JSON,bidRequest});
                            return true;
                        }
                    }
                }else{
                    MomoExchange.BidRequest bidRequest = MomoExchange.BidRequest.parseFrom(IOUtils.toByteArray(req.getInputStream()));
                    logger.info("Momo Request params is : {}", bidRequest.toString());
                    int status = validateRequiredParam(bidRequest);
                    if(Constant.StatusCode.OK == status){
                        mediaRequest = conversionToPremiumMADDataModel(bidRequest);
                        if(mediaRequest != null){
                            mediaBidMetaData.getMediaBidBuilder().setRequest(mediaRequest);
                            mediaBidMetaData.setRequestObject(new Object[]{MomoStatusCode.Type.JSON,bidRequest});
                            return true;
                        }
                    }
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
    
   

    private int validateParam(MomoBidRequest bidRequest) {
        if (ObjectUtils.isNotEmpty(bidRequest)) {
            String id = bidRequest.getId();
            if (null == id){
                logger.debug("MomoBidRequest.id is null");
                return Constant.StatusCode.BAD_REQUEST;
            }
            
            if(null == bidRequest.getVersion()){
                logger.debug("{}:MomoBidRequest.Version is null",id);
                return Constant.StatusCode.BAD_REQUEST;
            }
            
            if(null == bidRequest.getImp() && bidRequest.getImp().size()==0){
                logger.debug("{}:MomoBidRequest.Imp is null",id);
                return Constant.StatusCode.BAD_REQUEST;
            }else{
                 MomoBidRequest.Impression imp = bidRequest.getImp().get(0);
                 if (ObjectUtils.isEmpty(imp)) {
                     logger.debug("{}:MomoBidRequest.Imp is null",id);
                     return Constant.StatusCode.BAD_REQUEST;
                 }
                 if (StringUtils.isEmpty(imp.getId())) {
                     logger.debug("{}:MomoBidRequest.Imp.id is null",id);
                     return Constant.StatusCode.BAD_REQUEST;
                 }
                 if (StringUtils.isEmpty(imp.getSplash_format())) {
                     logger.debug("{}:MomoBidRequest.Imp.Splash_format is null",id);
                     return Constant.StatusCode.BAD_REQUEST;
                 }
                 if(null == imp.getCampaign()){
                     logger.debug("{}:MomoBidRequest.Imp.campaign is null",id);
                     return Constant.StatusCode.BAD_REQUEST;
                 }else{
                     MomoBidRequest.Impression.Campaign campaign = imp.getCampaign();
                     if(null == campaign.getCampaign_id()){
                         logger.debug("{}:MomoBidRequest.Imp.campaign.id is null",id);
                         return Constant.StatusCode.BAD_REQUEST;
                     }
                     if(null == campaign.getCampaign_begin_date()){
                         logger.debug("{}:MomoBidRequest.Imp.campaign.begin_date is null",id);
                         return Constant.StatusCode.BAD_REQUEST;
                     }
                     if(null == campaign.getCampaign_end_date()){
                         logger.debug("{}:MomoBidRequest.Imp.campaign.end_date is null",id);
                         return Constant.StatusCode.BAD_REQUEST;
                     }
                 }
                 return Constant.StatusCode.OK;
            }
        }
        return  Constant.StatusCode.BAD_REQUEST;
    }
    
    private MediaRequest conversionToPremiumMADData(MomoBidRequest bidRequest) {
        MediaRequest.Builder mediaRequest = MediaRequest.newBuilder();
        mediaRequest.setBid(bidRequest.getId());
        
        Impression imp = bidRequest.getImp().get(0);
        Device device = bidRequest.getDevice();
        mediaRequest.setAdtype(6);//开屏
        mediaRequest.setW(imp.getW());
        mediaRequest.setH(imp.getH());
        
        String os = device.getOs();//"1"为iOS,"2"为安卓
        if(os.equals(MomoStatusCode.Os.OS_IOS)){//ios
            mediaRequest.setOs(Constant.OSType.IOS);
            mediaRequest.setDid(device.getDid());
            mediaRequest.setDidmd5(device.getDidmd5());
        }else if(os.equals(os.equals(MomoStatusCode.Os.OS_ANDROID))){//安卓
            mediaRequest.setOs(Constant.OSType.ANDROID);
            mediaRequest.setIfa(device.getDid());
        }
        //"WIFI" "CELL_UNKNOWN
        String connection = device.getConnection_type();
        if(connection.equals(MomoStatusCode.ConnectionType.WIFI)){
            mediaRequest.setConnectiontype(Constant.ConnectionType.WIFI);
        }else if(connection.equals(os.equals(MomoStatusCode.ConnectionType.CELL_UNKNOWN))){
            mediaRequest.setConnectiontype(Constant.ConnectionType.UNKNOWN);
        }
        String ua = device.getUa();
        if (!StringUtils.isEmpty(ua)) {
            mediaRequest.setUa(ua);
        }
        String ip = device.getIp();
        if (!StringUtils.isEmpty(ip)) {
            mediaRequest.setIp(ip);
        }
        if(ObjectUtils.isNotEmpty(device.getGeo().getLon()+"")){
            mediaRequest.setLat((float)device.getGeo().getLon());
        }
        if(ObjectUtils.isNotEmpty(device.getGeo().getLon()+"")){
            mediaRequest.setLon((float)device.getGeo().getLat());
        }
        mediaRequest.setCarrier(Constant.Carrier.UNKNOWN);
        mediaRequest.setType(Constant.MediaType.APP);
        mediaRequest.setDevicetype(Constant.DeviceType.UNKNOWN);
        //开屏样式（SPLASH_IMG，SPLASH_GIF，SPLASH_VIDEO）
        String adspaceKey = new StringBuffer().append("MM:").append(imp.getSplash_format()).append(":").append(os).toString();
        MediaMappingMetaData mappingMetaData = CacheManager.getInstance().getMediaMapping(adspaceKey);
        
        if (mappingMetaData != null) {
            mediaRequest.setAdspacekey(mappingMetaData.getAdspaceKey());
        } else {
            mappingMetaData = CacheManager.getInstance().getMediaMapping("MM:0:0");
            if(mappingMetaData != null){
                mediaRequest.setAdspacekey(mappingMetaData.getAdspaceKey());
            }else{
                return null;
            }
        }
        
        logger.info("Momorequest convert mediaRequest is : {}", mediaRequest.toString());
        return mediaRequest.build();
    }
    
    
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
        mediaRequest.setDevicetype(Constant.DeviceType.UNKNOWN);
        String campainType = getCampainType(imp.getNative());
        String adspaceKey = new StringBuffer().append("MM:").append(imp.getSlotid()).append(":").append(bidRequest.getDevice().getOs().toLowerCase()).append(":").append(campainType).toString();
        MediaMappingMetaData mappingMetaData = CacheManager.getInstance().getMediaMapping(adspaceKey);
        if (mappingMetaData != null) {
            mediaRequest.setAdspacekey(mappingMetaData.getAdspaceKey());
        } else {
            mappingMetaData = CacheManager.getInstance().getMediaMapping("MM:0:0");
            if(mappingMetaData != null){
                mediaRequest.setAdspacekey(mappingMetaData.getAdspaceKey());
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
        
        //操作系统的版本
        if(!StringUtils.isEmpty(device.getOsv())){
            mediaRequest.setOsv(device.getOsv());
        }
        if(!StringUtils.isEmpty(device.getMake())){
            mediaRequest.setMake(device.getMake());
        }
        if(!StringUtils.isEmpty(device.getModel())){
            mediaRequest.setModel(device.getModel());
        }
        if(!StringUtils.isEmpty(device.getMacmd5())){
            mediaRequest.setMacmd5(device.getMacmd5());
        }
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
        if(MomoStatusCode.Os.OS_IOS_P.equalsIgnoreCase(device.getOs().toLowerCase())){
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
        logger.info("Momorequest convert mediaRequest is : {}", mediaRequest.toString());
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
                    
                    Object[] objType = (Object[])mediaBidMetaData.getRequestObject();
                    
                    if(MomoStatusCode.Type.PROTOBUF.equals(objType[0])){
                        MomoExchange.BidResponse bidResponse = convertToMomoResponse(mediaBidMetaData,(BidRequest)objType[1]);
                        if(null != bidResponse){
                            resp.getOutputStream().write(bidResponse.toByteArray());
                            resp.setStatus(Constant.StatusCode.OK);
                            return true;
                        }
                    } else if (MomoStatusCode.Type.JSON.equals(objType[0])){
                        MomoResponse response= convertToMomoBidResponse(mediaBidMetaData,(MomoBidRequest)objType[1]);
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

    private MomoResponse convertToMomoBidResponse(MediaBidMetaData mediaBidMetaData, MomoBidRequest momoBidRequest) {
        
        MomoResponse momoBidResponse = new MomoResponse();
        MomoResponse.Bid bid = momoBidResponse.new Bid(); 
        MomoResponse.Bid.Image image = bid.new Image();
        MomoResponse.Bid.Gif gif = bid.new Gif();
        MomoResponse.Bid.Video video = bid.new Video();
        
        MediaResponse mediaResponse = mediaBidMetaData.getMediaBidBuilder().getResponse();
        
        
        bid.setImpid(momoBidRequest.getImp().get(0).getId());
        bid.setCrid(mediaResponse.getCrid());
        bid.setClick_url(mediaResponse.getLpgurl());
        
        List<Bid> bidList = new ArrayList<Bid>();
        List<String> impTrackers = new ArrayList<String>();
        for (Track track : mediaResponse.getMonitor().getImpurl()) {
            impTrackers.add(track.getUrl());
        }
        
        bid.setImptrackers(impTrackers);
        bid.setClicktrackers(mediaResponse.getMonitor().getClkurl());
        
        image.setUrl(mediaResponse.getAdm().get(0));
        
        if(mediaResponse.getAdm().get(0).contains(".gif")){
            gif.setUrl(mediaResponse.getCover());
        } else if(mediaResponse.getAdm().get(0).contains(".mp4")){
            video.setUrl(mediaResponse.getCover());
        }
        bidList.add(bid);
        momoBidResponse.setBid(bidList);
        momoBidResponse.setId(mediaBidMetaData.getMediaBidBuilder().getRequestBuilder().getBid());
        
        return momoBidResponse;
    }



    private BidResponse convertToMomoResponse(MediaBidMetaData mediaBidMetaData, MomoExchange.BidRequest bidRequest) {
        
        
        MediaResponse mediaResponse= mediaBidMetaData.getMediaBidBuilder().getResponse();
        Builder mediaRequest= mediaBidMetaData.getMediaBidBuilder().getRequestBuilder();
        
        MomoExchange.BidResponse.SeatBid.Bid.NativeCreative.Builder nativeCreativeBuilder = MomoExchange.BidResponse.SeatBid.Bid.NativeCreative.newBuilder();
        nativeCreativeBuilder.setTitle(mediaResponse.getTitle());
        nativeCreativeBuilder.setDesc(mediaResponse.getDesc());
        nativeCreativeBuilder.setLogo(MomoExchange.BidResponse.SeatBid.Bid.NativeCreative.Image.newBuilder());
        if(null != mediaResponse.getDuration() && mediaResponse.getDuration()>0){
            nativeCreativeBuilder.addVideo(MomoExchange.BidResponse.SeatBid.Bid.NativeCreative.Video.newBuilder().setUrl(mediaResponse.getAdm().get(0)));          //视频
            nativeCreativeBuilder.setCardTitle(mediaResponse.getTitle()); //无单独的行动卡标题 使用视频物料的标题
            nativeCreativeBuilder.setCardDesc(mediaResponse.getDesc());   //无单独的行动卡副标题 使用视频物料的副标题
            nativeCreativeBuilder.setLandingpageUrl(MomoExchange.BidResponse.SeatBid.Bid.Link.newBuilder().setUrl(mediaResponse.getAdm().get(0)));  //落地页
            if(bidRequest.getImpList().get(0).getNative().getNativeFormatList().contains(FEED_LANDING_PAGE_VIDEO)){
                nativeCreativeBuilder.setNativeFormat(FEED_LANDING_PAGE_VIDEO);
            }
        }else{
            MomoExchange.BidResponse.SeatBid.Bid.NativeCreative.Image.Builder imageBuilder = MomoExchange.BidResponse.SeatBid.Bid.NativeCreative.Image.newBuilder();
            imageBuilder.setHeight(mediaRequest.getH());
            imageBuilder.setWidth(mediaRequest.getW());
            imageBuilder.setUrl(mediaResponse.getAdm().get(0));
            nativeCreativeBuilder.addImage(imageBuilder);           //广告图片
            nativeCreativeBuilder.setLandingpageUrl(MomoExchange.BidResponse.SeatBid.Bid.Link.newBuilder().setUrl(mediaResponse.getAdm().get(0)));  //落地页
            if(bidRequest.getImpList().get(0).getNative().getNativeFormatList().contains(FEED_LANDING_PAGE_LARGE_IMG)){
                nativeCreativeBuilder.setNativeFormat(FEED_LANDING_PAGE_LARGE_IMG);
            }
        }
        
        
        MomoExchange.BidResponse.SeatBid.Bid.Builder bidBuilder = MomoExchange.BidResponse.SeatBid.Bid.newBuilder();
        bidBuilder.setId(mediaBidMetaData.getMediaBidBuilder().getImpid().toString());
        bidBuilder.setImpid(bidRequest.getImpList().get(0).getId());
        bidBuilder.setPrice(mediaRequest.getBidfloor());
        bidBuilder.setCid(mediaResponse.getCid());
        bidBuilder.setAdid(mediaResponse.getAdmid());   //广告位id
        bidBuilder.setCrid(mediaResponse.getCrid());  //物料id
        bidBuilder.addCat("");  //premiummad暂不支持 默认为空
        bidBuilder.setNativeCreative(nativeCreativeBuilder);
        /**
         *组装点击和展示监播url
         */
        List<Track> imgtracking = mediaResponse.getMonitor().getImpurl();
        if (imgtracking != null && imgtracking.size() != 0) {
            for (Track track : imgtracking) {
                bidBuilder.addClicktrackers(track.getUrl().toString());
            }
        }
        List<String> thclkurl = mediaResponse.getMonitor().getClkurl();
        if (thclkurl != null && thclkurl.size() != 0) {
            for (String thclk : thclkurl) {
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
