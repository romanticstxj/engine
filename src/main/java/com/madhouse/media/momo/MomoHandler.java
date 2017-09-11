package com.madhouse.media.momo;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.madhouse.ssp.avro.*;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.madhouse.cache.CacheManager;
import com.madhouse.cache.MediaBidMetaData;
import com.madhouse.cache.MediaMappingMetaData;
import com.madhouse.media.MediaBaseHandler;
import com.madhouse.media.momo.MomoBidRequest.Device;
import com.madhouse.media.momo.MomoBidRequest.Impression;
import com.madhouse.media.momo.MomoBidRequest.Impression.Campaign;
import com.madhouse.media.momo.MomoExchange.BidRequest;
import com.madhouse.media.momo.MomoExchange.BidResponse;
import com.madhouse.media.momo.MomoExchange.BidResponse.SeatBid.Bid.NativeCreative;
import com.madhouse.media.momo.MomoResponse.Bid;
import com.madhouse.ssp.Constant;
import com.madhouse.ssp.avro.MediaRequest.Builder;
import com.madhouse.util.HttpUtil;
import com.madhouse.util.ObjectUtils;
public class MomoHandler extends MediaBaseHandler {
    
    @Override
    public boolean parseMediaRequest(HttpServletRequest req, MediaBidMetaData mediaBidMetaData, HttpServletResponse resp) {
        
        try {
          //开屏是json格式，信息流是protofu格式
            if(null != req.getContentType()){
                MediaRequest.Builder mediaRequest =MediaRequest.newBuilder();
                if(req.getContentType().equalsIgnoreCase("application/json")){
                    String bytes = HttpUtil.getRequestPostBytes(req);
                    MomoBidRequest bidRequest = JSON.parseObject(bytes, MomoBidRequest.class);
                    logger.info("Momo Request params is : {}",JSON.toJSONString(bidRequest));
                    int status = validateParam(bidRequest);
                    if(Constant.StatusCode.OK == status){
                        mediaRequest = conversionToPremiumMADData(bidRequest);
                        if(mediaRequest != null){
                            mediaBidMetaData.getMediaBidBuilder().setRequestBuilder(mediaRequest);
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
                            mediaBidMetaData.getMediaBidBuilder().setRequestBuilder(mediaRequest);
                            mediaBidMetaData.setRequestObject(new Object[]{MomoStatusCode.Type.PROTOBUF,bidRequest});
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
                logger.warn("MomoBidRequest.id is null");
                return Constant.StatusCode.BAD_REQUEST;
            }
            
            if(null == bidRequest.getVersion()){
                logger.warn("{}:MomoBidRequest.Version is null",id);
                return Constant.StatusCode.BAD_REQUEST;
            }
            
            if(null == bidRequest.getImp() && bidRequest.getImp().size()==0){
                logger.warn("{}:MomoBidRequest.Imp is null",id);
                return Constant.StatusCode.BAD_REQUEST;
            }else{
                 MomoBidRequest.Impression imp = bidRequest.getImp().get(0);
                 if (ObjectUtils.isEmpty(imp)) {
                     logger.warn("{}:MomoBidRequest.Imp is null",id);
                     return Constant.StatusCode.BAD_REQUEST;
                 }
                 if (StringUtils.isEmpty(imp.getId())) {
                     logger.warn("{}:MomoBidRequest.Imp.id is null",id);
                     return Constant.StatusCode.BAD_REQUEST;
                 }
                 if (StringUtils.isEmpty(imp.getSplash_format())) {
                     logger.warn("{}:MomoBidRequest.Imp.Splash_format is null",id);
                     return Constant.StatusCode.BAD_REQUEST;
                 }
                 if(null == imp.getCampaign()){
                     logger.warn("{}:MomoBidRequest.Imp.campaign is null",id);
                     return Constant.StatusCode.BAD_REQUEST;
                 }else{
                     MomoBidRequest.Impression.Campaign campaign = imp.getCampaign();
                     if(null == campaign.getCampaign_id()){
                         logger.warn("{}:MomoBidRequest.Imp.campaign.id is null",id);
                         return Constant.StatusCode.BAD_REQUEST;
                     }
                     if(null == campaign.getCampaign_begin_date()){
                         logger.warn("{}:MomoBidRequest.Imp.campaign.begin_date is null",id);
                         return Constant.StatusCode.BAD_REQUEST;
                     }
                     if(null == campaign.getCampaign_end_date()){
                         logger.warn("{}:MomoBidRequest.Imp.campaign.end_date is null",id);
                         return Constant.StatusCode.BAD_REQUEST;
                     }
                 }
                 return Constant.StatusCode.OK;
            }
        }
        return  Constant.StatusCode.BAD_REQUEST;
    }
    
    private MediaRequest.Builder conversionToPremiumMADData(MomoBidRequest bidRequest) {
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
            mediaRequest.setIfa(device.getDid());
        }else if(os.equals(os.equals(MomoStatusCode.Os.OS_ANDROID))){//安卓
            mediaRequest.setOs(Constant.OSType.ANDROID);
            mediaRequest.setDid(device.getDid());
            mediaRequest.setDidmd5(device.getDidmd5());
        }
        //"WIFI" "CELL_UNKNOWN
        String connection = device.getConnection_type();
        Campaign campaign = imp.getCampaign();
        if(campaign != null){
            String campaignId = campaign.getCampaign_id();
            mediaRequest.setDealid(campaignId);
        }
        if(!StringUtils.isEmpty(connection) && connection.equals(MomoStatusCode.ConnectionType.WIFI)){
            mediaRequest.setConnectiontype(Constant.ConnectionType.WIFI);
        }else{
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
        MomoBidRequest.Device.Geo geos = device.getGeo();
        Geo.Builder geo = Geo.newBuilder();
        if(null != geos){
            if(ObjectUtils.isNotEmpty(geos.getLon()+"")){
                geo.setLat((float)geos.getLon());
            }
            if(ObjectUtils.isNotEmpty(geos.getLon()+"")){
                geo.setLon((float)geos.getLat());
            }
        }
        mediaRequest.setGeoBuilder(geo);
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
        
        logger.info("Momorequest convert mediaRequest is : {}", JSON.toJSONString(mediaRequest));
        return mediaRequest;
    }
    
    
    private MediaRequest.Builder conversionToPremiumMADDataModel(MomoExchange.BidRequest bidRequest) {
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
       
        /**
         * 设置dealid
         */
        if(null !=imp.getPmp() && null !=imp.getPmp().getDealsList() && imp.getPmp().getDealsList().size() >0){
            MomoExchange.BidRequest.Imp.Pmp.Deal deal = imp.getPmp().getDeals(0);
            mediaRequest.setDealid(deal.getId());
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

        if(ObjectUtils.isNotEmpty(device.getGeo())) {
            Geo.Builder geo = Geo.newBuilder();
            geo.setLat((float)device.getGeo().getLon());
            geo.setLon((float)device.getGeo().getLat());
            mediaRequest.setGeoBuilder(geo);
        }
        mediaRequest.setAdtype(2);
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
        return mediaRequest;
    }

    private int validateRequiredParam(BidRequest bidRequest) {
        if(ObjectUtils.isNotEmpty(bidRequest)){
            /**
             *  陌陌请求的唯一ID标识
             */
            if (StringUtils.isEmpty(bidRequest.getId())) {
                logger.warn("MomoExchange.bidRequest.id is missing");
                return Constant.StatusCode.BAD_REQUEST;
            }
            /**
             *  陌陌请求协议版本
             */
            if (StringUtils.isEmpty(bidRequest.getVersion())) {
                logger.warn("MomoExchange.bidRequest.version is missing");
                return Constant.StatusCode.BAD_REQUEST;
            }
            /**
             *  Impression信息字段处理
             * */
            List<MomoExchange.BidRequest.Imp> impList = bidRequest.getImpList();
            if (impList == null || impList.size() < 1) {
                logger.warn("MomoExchange.bidRequest.Imp is missing");
                return Constant.StatusCode.BAD_REQUEST;
            }
            MomoExchange.BidRequest.Imp imp = impList.get(0);
            if(StringUtils.isEmpty(imp.getId())){
                logger.warn("MomoExchange.bidRequest.Imp[0].id is missing");
                return Constant.StatusCode.BAD_REQUEST;
            }
            if(ObjectUtils.isEmpty(imp.getNative())){
                logger.warn("MomoExchange.bidRequest.Imp[0].Native is missing");
                return Constant.StatusCode.BAD_REQUEST;
            }else{
               MomoExchange.BidRequest.Imp.Native aNative =  imp.getNative();
               if(!aNative.getNativeFormatList().contains(MomoNativeTypeEnums.FEED_LANDING_PAGE_LARGE_IMG.getCode())&&
                   !aNative.getNativeFormatList().contains(MomoNativeTypeEnums.FEED_LANDING_PAGE_VIDEO.getCode())&&
                   !aNative.getNativeFormatList().contains(MomoNativeTypeEnums.FEED_LANDING_PAGE_SMALL_IMG.getCode())&&
                   !aNative.getNativeFormatList().contains(MomoNativeTypeEnums.NEARBY_LANDING_PAGE_NO_IMG.getCode())&&
                   !aNative.getNativeFormatList().contains(MomoNativeTypeEnums.FEED_LANDING_PAGE_SQUARE_IMG.getCode())){
                   logger.warn("MomoExchange.bidRequest.Imp[0].Native is [img,video] is missing");
                   return Constant.StatusCode.BAD_REQUEST;
               }
            }
            if(StringUtils.isEmpty(imp.getSlotid())){
                logger.warn("MomoExchange.bidRequest.Imp[0].Slotidis missing");
                return Constant.StatusCode.BAD_REQUEST;
            }else{
                String campainType = getCampainType(imp.getNative());
                if(StringUtils.isEmpty(campainType)){
                    logger.warn("MomoExchange.bidRequest.Imp[0].Native.Type is missing");
                    return Constant.StatusCode.BAD_REQUEST;
                }
            }
            MomoExchange.BidRequest.Device device =  bidRequest.getDevice();
            if(ObjectUtils.isEmpty(device)){
                logger.warn("MomoExchange.bidRequest.Device is missing");
                return Constant.StatusCode.BAD_REQUEST;
            }
            return Constant.StatusCode.OK;
        }
        return Constant.StatusCode.BAD_REQUEST;
    }
    private String getCampainType(MomoExchange.BidRequest.Imp.Native aNative){
        Random random = new Random();
        if(null != aNative.getNativeFormatList() && aNative.getNativeFormatList().size() >0){
            if(aNative.getNativeFormatList().size() >1){
                List<String> list = new ArrayList<String>();
                for(int i=0;i < aNative.getNativeFormatList().size();i++){
                    list.add(aNative.getNativeFormatList().get(i));
                }
                int i = random.nextInt(aNative.getNativeFormatList().size()-1);
                switch (list.get(i)){
                    case "FEED_LANDING_PAGE_LARGE_IMG":
                        return  "img";
                    case "FEED_LANDING_PAGE_VIDEO":
                        return "video";
                    case "FEED_LANDING_PAGE_SMALL_IMG":
                        return  "smallImg";
                    case "NEARBY_LANDING_PAGE_NO_IMG":
                        return "noImg";
                    case "FEED_LANDING_PAGE_SQUARE_IMG":
                        return  "squareImg";
                }
                return "";
            }else if(aNative.getNativeFormatList().size() == 1){
                
                if(aNative.getNativeFormatList().contains(MomoNativeTypeEnums.FEED_LANDING_PAGE_LARGE_IMG.getCode())){
                    return "img";
                }else if(aNative.getNativeFormatList().contains(MomoNativeTypeEnums.FEED_LANDING_PAGE_SMALL_IMG.getCode())){
                    return  "smallImg";
                }else if(aNative.getNativeFormatList().contains(MomoNativeTypeEnums.NEARBY_LANDING_PAGE_NO_IMG.getCode())){
                     return "noImg";
                }else if(aNative.getNativeFormatList().contains(MomoNativeTypeEnums.FEED_LANDING_PAGE_SQUARE_IMG.getCode())){
                     return  "squareImg";
                }else if(aNative.getNativeFormatList().contains(MomoNativeTypeEnums.FEED_LANDING_PAGE_VIDEO.getCode())){
                     return "video";
                }
            }
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
                        MomoExchange.BidResponse bidResponse = convertToMomoResponse(mediaBidMetaData,(MomoExchange.BidRequest)objType[1]);
                        if(null != bidResponse){
                            resp.setContentType("application/octet-stream;charset=UTF-8");
                            resp.getOutputStream().write(bidResponse.toByteArray());
                            resp.setStatus(Constant.StatusCode.OK);
                            return true;
                        }
                    } else if (MomoStatusCode.Type.JSON.equals(objType[0])){
                        MomoResponse response= convertToMomoBidResponse(mediaBidMetaData,(MomoBidRequest)objType[1]);
                        if(null != response){
                            resp.setHeader("Content-Type", "application/json; charset=utf-8");
                            resp.getOutputStream().write(JSON.toJSONString(response).getBytes());
                            resp.setStatus(Constant.StatusCode.OK);
                            return true;
                        }
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
    //开屏
    private MomoResponse convertToMomoBidResponse(MediaBidMetaData mediaBidMetaData, MomoBidRequest objType) {
        
        MomoResponse momoBidResponse = new MomoResponse();
        MomoResponse.Bid bid = momoBidResponse.new Bid(); 
        MomoResponse.Bid.Image image = bid.new Image();
        MomoResponse.Bid.Gif gif = bid.new Gif();
        MomoResponse.Bid.Video video = bid.new Video();
        
        MediaResponse.Builder mediaResponse = mediaBidMetaData.getMediaBidBuilder().getResponseBuilder();
        
        
        bid.setImpid(objType.getImp().get(0).getId());
        bid.setCrid(mediaResponse.getCrid());
        bid.setClick_url(mediaResponse.getLpgurl());
        
        List<Bid> bidList = new ArrayList<Bid>();
        List<String> impTrackers = new ArrayList<String>();
        for (Track track : mediaResponse.getMonitorBuilder().getImpurl()) {
            impTrackers.add(track.getUrl());
        }
        
        bid.setImptrackers(impTrackers);
        bid.setClicktrackers(mediaResponse.getMonitorBuilder().getClkurl());
        
        String splashFormat = objType.getImp().get(0).getSplash_format();
        if(splashFormat.equals("SPLASH_IMG")){
            image.setUrl(mediaResponse.getAdm().get(0));
        }else if(splashFormat.equals("SPLASH_GIF")){
            image.setUrl(mediaResponse.getCover());
            gif.setUrl(mediaResponse.getAdm().get(0));
            bid.setGif(gif);
        }else if(splashFormat.equals("SPLASH_VIDEO")){
            image.setUrl(mediaResponse.getCover());
            video.setUrl(mediaResponse.getAdm().get(0));
            bid.setVideo(video);
        }
        bid.setImage(image);
        
        bidList.add(bid);
        momoBidResponse.setBid(bidList);
        momoBidResponse.setId(mediaBidMetaData.getMediaBidBuilder().getRequestBuilder().getBid());
        
        logger.info("MoMO Response params is : {}", JSON.toJSONString(momoBidResponse));
        return momoBidResponse;
    }



    private BidResponse convertToMomoResponse(MediaBidMetaData mediaBidMetaData, MomoExchange.BidRequest bidRequest) {
        
        
        MediaResponse.Builder mediaResponse= mediaBidMetaData.getMediaBidBuilder().getResponseBuilder();
        MediaRequest.Builder mediaRequest= mediaBidMetaData.getMediaBidBuilder().getRequestBuilder();
        
        MomoExchange.BidResponse.SeatBid.Bid.Builder bidBuilder = MomoExchange.BidResponse.SeatBid.Bid.newBuilder();
        bidBuilder.setId(mediaBidMetaData.getMediaBidBuilder().getImpid().toString());
        bidBuilder.setImpid(bidRequest.getImpList().get(0).getId());
        bidBuilder.setPrice(mediaRequest.getBidfloor());
        bidBuilder.setCid(mediaResponse.getCid());
        bidBuilder.setAdid(mediaResponse.getCid());   //广告位id
        bidBuilder.setCrid(!StringUtils.isEmpty(mediaResponse.getCrid()) ? mediaResponse.getCrid() : "");  //物料id
        bidBuilder.addCat("");  //premiummad暂不支持 默认为空
        //bidBuilder.setNativeCreative(nativeCreativeBuilder);
        
        bidBuilder.setNativeCreative(getNativeCreative(bidRequest,mediaResponse,mediaRequest));
        
        /**
         * 设置dealid
         */
        if(null !=bidRequest.getImp(0) && null !=bidRequest.getImp(0).getPmp().getDealsList() && bidRequest.getImp(0).getPmp().getDealsList().size() >0){
            MomoExchange.BidRequest.Imp.Pmp.Deal deal = bidRequest.getImp(0).getPmp().getDeals(0);
             bidBuilder.setDealid(deal.getId());
        }
        
        /**
         *组装点击和展示监播url
         */
        List<Track> imgtracking = mediaResponse.getMonitorBuilder().getImpurl();
        if (imgtracking != null && imgtracking.size() != 0) {
            for (Track track : imgtracking) {
                bidBuilder.addClicktrackers(track.getUrl().toString());
            }
        }
        List<String> thclkurl = mediaResponse.getMonitorBuilder().getClkurl();
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
        bidResposeBuilder.setBidid(mediaBidMetaData.getMediaBidBuilder().getImpid());
        
        logger.info("MoMO Response params is : {}", bidResposeBuilder.toString());
        return bidResposeBuilder.build();
    }


    private NativeCreative.Builder getNativeCreative(BidRequest bidRequest, MediaResponse.Builder mediaResponse, Builder mediaRequest) {
        
        MomoExchange.BidResponse.SeatBid.Bid.NativeCreative.Builder nativeCreativeBuilder = MomoExchange.BidResponse.SeatBid.Bid.NativeCreative.newBuilder();
        nativeCreativeBuilder.setTitle(mediaResponse.getTitle());
        nativeCreativeBuilder.setDesc(mediaResponse.getDesc());
        nativeCreativeBuilder.setLogo(MomoExchange.BidResponse.SeatBid.Bid.NativeCreative.Image.newBuilder());
    
        String url = mediaResponse.getAdm().get(0);
        String suffix = url.substring(url.lastIndexOf(".")+1,url.length()).toLowerCase();
        if (ImageTypeEnums.contains(suffix)) {//广告图片
            nativeCreativeBuilder.setLandingpageUrl(getLink(mediaResponse));  //落地页
            if(bidRequest.getImpList().get(0).getNative().getNativeFormatList().contains(MomoNativeTypeEnums.FEED_LANDING_PAGE_LARGE_IMG.getCode())){//大图样式落地页
                nativeCreativeBuilder.setNativeFormat(MomoNativeTypeEnums.FEED_LANDING_PAGE_LARGE_IMG.getCode());
                nativeCreativeBuilder.addImage(getImage(mediaRequest,mediaResponse));
                
            }else if(bidRequest.getImpList().get(0).getNative().getNativeFormatList().contains(MomoNativeTypeEnums.FEED_LANDING_PAGE_SQUARE_IMG.getCode())){//单图样式落地页
                 nativeCreativeBuilder.setNativeFormat(MomoNativeTypeEnums.FEED_LANDING_PAGE_SQUARE_IMG.getCode());
                 nativeCreativeBuilder.addImage(getImage(mediaRequest,mediaResponse));    
                 
            }else if(bidRequest.getImpList().get(0).getNative().getNativeFormatList().contains(MomoNativeTypeEnums.FEED_LANDING_PAGE_SMALL_IMG.getCode())){//三图样式落地页
                nativeCreativeBuilder.setNativeFormat(MomoNativeTypeEnums.FEED_LANDING_PAGE_SMALL_IMG.getCode());
                if(null != mediaResponse.getAdm() && mediaResponse.getAdm().size() >0){
                    for(int i=0;i < mediaResponse.getAdm().size();i++){
                        nativeCreativeBuilder.addImage(getImageList(mediaRequest,mediaResponse,i));   //传多个物料
                    }
                }
                
            }else if(bidRequest.getImpList().get(0).getNative().getNativeFormatList().contains(MomoNativeTypeEnums.NEARBY_LANDING_PAGE_NO_IMG.getCode())){//图标样式落地页
                nativeCreativeBuilder.setNativeFormat(MomoNativeTypeEnums.NEARBY_LANDING_PAGE_NO_IMG.getCode());
                nativeCreativeBuilder.addImage(getImage(mediaRequest,mediaResponse));    
            }
        }else if (VideoTypeEnums.contains(suffix)) {//视频
           nativeCreativeBuilder.addVideo(getVideo(mediaRequest,mediaResponse)); 
           /*nativeCreativeBuilder.setCardTitle(jsonEntity.getExt().getTitle());
           nativeCreativeBuilder.setCardDesc(jsonEntity.getExt().getDesc()); 
           nativeCreativeBuilder.addDisplayLabels(jsonEntity.getExt().getLabels().get(0));*/
           nativeCreativeBuilder.setCardImage(moMoVideoGetImage(mediaRequest,mediaResponse));
           nativeCreativeBuilder.setLandingpageUrl(getLink(mediaResponse));  //落地页
            
            if(bidRequest.getImpList().get(0).getNative().getNativeFormatList().contains(MomoNativeTypeEnums.FEED_LANDING_PAGE_VIDEO.getCode())){//横版视频落地页
                nativeCreativeBuilder.setNativeFormat(MomoNativeTypeEnums.FEED_LANDING_PAGE_VIDEO.getCode());
            }
        }
        return nativeCreativeBuilder;
        
        
    }
    private MomoExchange.BidResponse.SeatBid.Bid.Link.Builder getLink(MediaResponse.Builder builder) {
        MomoExchange.BidResponse.SeatBid.Bid.Link.Builder linkBuilder = MomoExchange.BidResponse.SeatBid.Bid.Link.newBuilder();
        linkBuilder.setUrl(builder.getLpgurl());
        return linkBuilder;
    }
    
    /**
     * 单图
     * @param jsonEntity
     * @return
     */
    private MomoExchange.BidResponse.SeatBid.Bid.NativeCreative.Image.Builder getImage(MediaRequest.Builder requestBuilder,MediaResponse.Builder responseBuilder) {
        MomoExchange.BidResponse.SeatBid.Bid.NativeCreative.Image.Builder imageBuilder = MomoExchange.BidResponse.SeatBid.Bid.NativeCreative.Image.newBuilder();
        imageBuilder.setHeight(requestBuilder.getH());
        imageBuilder.setWidth(requestBuilder.getW());
        imageBuilder.setUrl(responseBuilder.getAdm().get(0));

        return imageBuilder;
    }

    /**
     * 多图
     * @param jsonEntity
     * @return
     */
    private MomoExchange.BidResponse.SeatBid.Bid.NativeCreative.Image.Builder getImageList(MediaRequest.Builder requestBuilder,MediaResponse.Builder responseBuilder,int i) {
            MomoExchange.BidResponse.SeatBid.Bid.NativeCreative.Image.Builder imageBuilder = MomoExchange.BidResponse.SeatBid.Bid.NativeCreative.Image.newBuilder();
            imageBuilder.setHeight(requestBuilder.getH());
            imageBuilder.setWidth(requestBuilder.getW());
            imageBuilder.setUrl(responseBuilder.getAdm().get(i));
        return imageBuilder;
    }

    private MomoExchange.BidResponse.SeatBid.Bid.NativeCreative.Video.Builder getVideo(MediaRequest.Builder requestBuilder,MediaResponse.Builder responseBuilder) {
        MomoExchange.BidResponse.SeatBid.Bid.NativeCreative.Video.Builder videoBuilder = MomoExchange.BidResponse.SeatBid.Bid.NativeCreative.Video.newBuilder();
        videoBuilder.setUrl(responseBuilder.getAdm().get(0));
        
        MomoExchange.BidResponse.SeatBid.Bid.NativeCreative.Image.Builder imageBuilder = MomoExchange.BidResponse.SeatBid.Bid.NativeCreative.Image.newBuilder();
        imageBuilder.setHeight(requestBuilder.getH());
        imageBuilder.setWidth(requestBuilder.getW());
        imageBuilder.setUrl(responseBuilder.getCover());
        
        videoBuilder.setCoverImg(imageBuilder);
        return videoBuilder;
    }
    
    /**
     * momo 视频专用
     * @param jsonEntity
     * @return
     */
    private MomoExchange.BidResponse.SeatBid.Bid.NativeCreative.Image.Builder moMoVideoGetImage(MediaRequest.Builder requestBuilder,MediaResponse.Builder responseBuilder) {
        MomoExchange.BidResponse.SeatBid.Bid.NativeCreative.Image.Builder imageBuilder = MomoExchange.BidResponse.SeatBid.Bid.NativeCreative.Image.newBuilder();
        /*imageBuilder.setUrl(jsonEntity.getExt().getAdm().get(0));
        imageBuilder.setWidth(jsonEntity.getExt().getWidth());
        imageBuilder.setHeight(jsonEntity.getExt().getHeigh());*/
        return imageBuilder;
    }

}
