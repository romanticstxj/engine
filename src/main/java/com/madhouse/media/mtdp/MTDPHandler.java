package com.madhouse.media.mtdp;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.madhouse.cache.CacheManager;
import com.madhouse.cache.MediaBidMetaData;
import com.madhouse.cache.MediaMappingMetaData;
import com.madhouse.cache.PlcmtMetaData;
import com.madhouse.media.MediaBaseHandler;
import com.madhouse.media.mtdp.DPAds.BidRequest;
import com.madhouse.ssp.Constant;
import com.madhouse.ssp.avro.MediaBid;
import com.madhouse.ssp.avro.MediaRequest;
import com.madhouse.ssp.avro.MediaRequest.Builder;
import com.madhouse.ssp.avro.MediaResponse;
import com.madhouse.ssp.avro.Track;
import com.madhouse.util.ObjectUtils;

public class MTDPHandler extends MediaBaseHandler {
    
    @Override
    public boolean parseMediaRequest(HttpServletRequest req, MediaBidMetaData mediaBidMetaData, HttpServletResponse resp) {
        boolean isSandbox = false;
        String env = req.getHeader("env");
        if (env != null && env.equals("sandbox")) {
            isSandbox = true;
        }
        try {
            DPAds.BidRequest bidRequest = DPAds.BidRequest.parseFrom(IOUtils.toByteArray(req.getInputStream()));
            logger.info("MTDP Request params is : {}", bidRequest.toString());
            int status = validateRequiredParam(bidRequest);
            if(Constant.StatusCode.OK == status){
                MediaRequest mediaRequest = conversionToPremiumMADDataModel(isSandbox,bidRequest);
                if(mediaRequest != null){
                    mediaBidMetaData.getMediaBidBuilder().setRequest(mediaRequest);
                    mediaBidMetaData.setRequestObject(bidRequest);
                    return true;
                }
            }
            resp.setStatus(Constant.StatusCode.NO_CONTENT);
            return false;
        } catch (Exception e) {
            logger.error(e.toString() + "_Status_" + Constant.StatusCode.BAD_REQUEST);
            resp.setStatus(Constant.StatusCode.NO_CONTENT);
            return false;
        }
    }
    
    private MediaRequest conversionToPremiumMADDataModel(boolean isSandbox, BidRequest bidRequest) throws UnsupportedEncodingException {
        MediaRequest.Builder mediaRequest = MediaRequest.newBuilder();
        DPAds.BidRequest.Imp imp = bidRequest.getImpList().get(0);
        DPAds.BidRequest.Imp.Banner banner = bidRequest.getImpList().get(0).getBanner();
        DPAds.BidRequest.Device device = bidRequest.getDevice();
        String adspaceKey = "";
        if (isSandbox) {//sandbox环境
            adspaceKey = new StringBuffer().append("sandbox:").append("DP:").append(imp.getSlotId()).append(":").append(bidRequest.getDevice().getOs().toLowerCase()).toString();
          //模拟竞价，
            mediaRequest.setTest(Constant.Test.SIMULATION);
        } else {
            adspaceKey = new StringBuffer().append("DP:").append(imp.getSlotId()).append(":").append(bidRequest.getDevice().getOs().toLowerCase()).toString();
            mediaRequest.setTest(Constant.Test.REAL);
        }
        MediaMappingMetaData mappingMetaData= CacheManager.getInstance().getMediaMapping(adspaceKey);
        if (mappingMetaData != null) {
            mediaRequest.setAdspacekey(mappingMetaData.getAdspaceKey());
        } else { 
            if (isSandbox) {//sandbox环境
                mappingMetaData = CacheManager.getInstance().getMediaMapping("sandbox:DP:0:0");
            } else {
                mappingMetaData = CacheManager.getInstance().getMediaMapping("DP:0:0");
            }
            if(mappingMetaData != null){
                mediaRequest.setAdspacekey(mappingMetaData.getAdspaceKey());
            }else{
                return null;
            }
        }
        mediaRequest.setDevicetype(Constant.DeviceType.UNKNOWN);
        mediaRequest.setBid(bidRequest.getId());
        
        mediaRequest.setW(banner.getW());
        
        mediaRequest.setH(banner.getH());
        
        mediaRequest.setBidfloor((new Double(imp.getBidfloor())).intValue());
        
        mediaRequest.setName(bidRequest.getApp().getName());   
        
        if(!StringUtils.isEmpty(device.getOsv())){
            mediaRequest.setOsv(device.getOsv());
        }
        if(!StringUtils.isEmpty(device.getMake())){
            mediaRequest.setMake(device.getMake());
        }
        if(!StringUtils.isEmpty(device.getModel())){
            mediaRequest.setModel(device.getModel());
        }
        /**
         * OS 或  Android
         */
        if ("ios".equalsIgnoreCase(device.getOs())) {
            mediaRequest.setOs(Constant.OSType.ANDROID);
            String dpidsha1 = device.getDpidsha1();
            if(!StringUtils.isEmpty(dpidsha1)){
                mediaRequest.setDid(URLEncoder.encode(dpidsha1, "UTF-8"));
            }
            String dpidmd5 = device.getDpidmd5();
            if(!StringUtils.isEmpty(dpidmd5)){
                mediaRequest.setDidmd5(URLEncoder.encode(dpidmd5, "UTF-8"));
            }
        } else if ("android".equalsIgnoreCase(device.getOs())) {
            mediaRequest.setOs(Constant.OSType.IOS);
            String idfa = device.getIdfa();
            if(!StringUtils.isEmpty(idfa)){
                mediaRequest.setIfa(idfa);
            }
        } else {
            mediaRequest.setOs(Constant.OSType.UNKNOWN);
        }
        mediaRequest.setCarrier(Constant.Carrier.UNKNOWN);
        mediaRequest.setDevicetype(Constant.DeviceType.UNKNOWN);
        mediaRequest.setConnectiontype(Constant.ConnectionType.UNKNOWN);
        
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
        
        DPAds.BidRequest.Site site = bidRequest.getSite();
        mediaRequest.setType(site !=null ? Constant.MediaType.APP : Constant.MediaType.SITE);
        logger.info("MTDPrequest convert mediaRequest is : {}", mediaRequest.toString());
        return mediaRequest.build();
    }

    private int validateRequiredParam(BidRequest bidRequest) {
        if(ObjectUtils.isNotEmpty(bidRequest)){
            if (StringUtils.isEmpty(bidRequest.getId())){
                logger.debug("DPAds.bidRequest.id is missing");
                return Constant.StatusCode.BAD_REQUEST;
            }
            List<DPAds.BidRequest.Imp> impList = bidRequest.getImpList();
            if (impList == null || impList.size() < 1){
                logger.debug("DPAds.bidRequest.Imp is missing");
                return Constant.StatusCode.BAD_REQUEST;
            }
            DPAds.BidRequest.Imp imp = impList.get(0);
            if (StringUtils.isEmpty(imp.getId())){
                logger.debug("DPAds.bidRequest.Imp.Id is missing");
                return Constant.StatusCode.BAD_REQUEST;
            }
            DPAds.BidRequest.Imp.Banner banner = imp.getBanner();
            if (banner == null){
                logger.debug("DPAds.bidRequest.Imp.Banner is missing");
                return Constant.StatusCode.BAD_REQUEST;
            }
            DPAds.BidRequest.Imp.Native aNative = imp.getNative();
            if (aNative == null){
                logger.debug("DPAds.bidRequest.Imp.Native is missing");
                return Constant.StatusCode.BAD_REQUEST;
            }
            if (StringUtils.isEmpty(imp.getSlotId())){
                logger.debug("DPAds.bidRequest.Imp.SlotId is missing");
                return Constant.StatusCode.BAD_REQUEST;
            }
            DPAds.BidRequest.Imp.Native.Requestobj requestobj = aNative.getRequestobj();
            if (requestobj == null){
                logger.debug("DPAds.bidRequest.Imp.Native.Requestobj is missing");
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
                    DPAds.BidResponse bidResponse = convertToMTDPResponse(mediaBidMetaData);
                    if(null != bidResponse){
                        resp.getOutputStream().write(bidResponse.toByteArray());
                        resp.setStatus(Constant.StatusCode.OK);
                        return true;
                    }
                }
                resp.setStatus(mediaBid.getStatus());
                return false;
            }
        } catch (Exception e) {
            logger.error(e.toString() + "_Status_" + Constant.StatusCode.NO_CONTENT);
            resp.setStatus(Constant.StatusCode.NO_CONTENT);
            return false;
        }
        resp.setStatus(Constant.StatusCode.NO_CONTENT);
        return false;
    }

    private DPAds.BidResponse convertToMTDPResponse(MediaBidMetaData mediaBidMetaData) {
        MediaResponse mediaResponse= mediaBidMetaData.getMediaBidBuilder().getResponse();
        Builder mediaRequest= mediaBidMetaData.getMediaBidBuilder().getRequestBuilder();
        
        DPAds.BidRequest bidRequest = (DPAds.BidRequest) mediaBidMetaData.getRequestObject();
        
        DPAds.BidResponse.SeatBid.Builder seatbid = DPAds.BidResponse.SeatBid.newBuilder();
        DPAds.BidResponse.Bid.Builder bid = DPAds.BidResponse.Bid.newBuilder();
        
        
        bid.setId(mediaBidMetaData.getMediaBidBuilder().getImpid());
        bid.setImpid(bidRequest.getImp(0).getId());
        if (bidRequest != null &&  bidRequest.getImp(0).getBidfloor() != 0) {
            bid.setPrice((float)bidRequest.getImp(0).getBidfloor());
        } else {
            bid.setPrice(0.01f);
        }
        
        //bid.setAdid(mediaResponse.geta);
        bid.setCid(mediaResponse.getCid());
        bid.setCrid(mediaResponse.getCrid());
        bid.setSlotId(bidRequest.getImp(0).getSlotId());
        bid.addLandingmacro(mediaResponse.getLpgurl());
        List<Track> imgtracking = mediaResponse.getMonitor().getImpurl();
        if (imgtracking != null && imgtracking.size() != 0) {
            for (Track track : imgtracking) {
                bid.getImpmacroList().add(track.getUrl());
            }
        }
        List<String> thclkurl = mediaResponse.getMonitor().getClkurl();
        if (thclkurl != null && thclkurl.size() != 0) {
            for (String thclk : thclkurl) {
                bid.getClickmacroList().add(thclk);
            }
        }
        seatbid.addBid(bid);
        // 投标人id
        seatbid.setSeat("madhouse");
        DPAds.BidResponse bidResponse = DPAds.BidResponse.newBuilder().setId(mediaRequest.getBid()).addSeatbid(seatbid).build();
        
        logger.info("MTDP Response params is : {}", bidResponse.toString());
        return bidResponse;
    }
    
}
