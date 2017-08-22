package com.madhouse.media.vamaker;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.util.UrlEncoded;

import com.alibaba.fastjson.JSON;
import com.madhouse.cache.CacheManager;
import com.madhouse.cache.MediaBidMetaData;
import com.madhouse.cache.MediaMappingMetaData;
import com.madhouse.media.MediaBaseHandler;
import com.madhouse.media.vamaker.VamakerRTB.VamRequest;
import com.madhouse.media.vamaker.VamakerRTB.VamResponse.Builder;
import com.madhouse.ssp.Constant;
import com.madhouse.ssp.avro.MediaBid;
import com.madhouse.ssp.avro.MediaRequest;
import com.madhouse.ssp.avro.MediaResponse;
import com.madhouse.ssp.avro.Track;
import com.madhouse.util.ObjectUtils;
import com.madhouse.util.StringUtil;

public class VamakerHandler extends MediaBaseHandler {
    
    @Override
    public boolean parseMediaRequest(HttpServletRequest req, MediaBidMetaData mediaBidMetaData, HttpServletResponse resp) {
        
        try {
            VamakerRTB.VamRequest bidRequest = VamakerRTB.VamRequest.parseFrom(IOUtils.toByteArray(req.getInputStream()));
            logger.info("VamakerBidRequest Request params is : {}",bidRequest.toString());
            int status = validateRequiredParam(bidRequest);
            if(Constant.StatusCode.OK == status){
                MediaRequest mediaRequest = conversionToPremiumMADDataModel(bidRequest);
                if(null != mediaRequest){
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
    
    private MediaRequest conversionToPremiumMADDataModel(VamRequest bidRequest) {
        MediaRequest.Builder mediaRequest = MediaRequest.newBuilder();
        mediaRequest.setBid(bidRequest.getId());
        VamakerRTB.VamRequest.Mobile mobile = bidRequest.getVamMobile();
        StringBuilder sb = new StringBuilder();
        sb.append("VAM:");
        sb.append(mobile.getAdspaceId()).append(":");
        if (VamakerStatusCode.Os.ANDROID == mobile.getOs()) {
            sb.append("android");
        } else if (VamakerStatusCode.Os.IOS == mobile.getOs()) {
            sb.append("ios");
        }
        logger.warn("crid not found:{}", sb.toString());
        
        MediaMappingMetaData mappingMetaData = CacheManager.getInstance().getMediaMapping(sb.toString());
        if (null != mappingMetaData) {
            mediaRequest.setAdspacekey(mappingMetaData.getAdspaceKey());
        }else{
            return null;
        }
        
        switch (mobile.getOs()) {
            case VamakerStatusCode.Os.ANDROID:
                mediaRequest.setOs(Constant.OSType.ANDROID);
                String imei = mobile.getImei();
                if (!StringUtils.isEmpty(imei)) {
                    mediaRequest.setDidmd5(imei);
                }
                String aid = mobile.getAid();
                if (!StringUtils.isEmpty(aid)) {
                    mediaRequest.setDpidmd5(aid);
                }
                String aaid = mobile.getAaid();
                if (!StringUtils.isEmpty(aaid)) {
                    mediaRequest.setIfa(aaid);
                }
                break;
            case VamakerStatusCode.Os.IOS:
                mediaRequest.setOs(Constant.OSType.IOS);
                
                String idfa = mobile.getIDFA();
                if (!StringUtils.isEmpty(idfa)) {
                    mediaRequest.setIfa(idfa);
                }
                String openUDID = mobile.getOpenUDID();
                if (!StringUtils.isEmpty(openUDID)) {
                    mediaRequest.setDpidmd5(openUDID);
                }
                break;
            default:
                mediaRequest.setOs(Constant.OSType.UNKNOWN);
                break;
        }
        String mac = mobile.getMac();
        if (!StringUtils.isEmpty(mac)) {
            mediaRequest.setMacmd5(mac);
        }
        
        mediaRequest.setW(mobile.getWidth());
        mediaRequest.setH(mobile.getHeight());
        mediaRequest.setBidfloor(mobile.getBidfloor());
        mediaRequest.setMake(StringUtil.toString(mobile.getBrand()));
        mediaRequest.setModel(StringUtil.toString(mobile.getModel()));
        mediaRequest.setBundle(StringUtil.toString(mobile.getPgn()));
        mediaRequest.setName(StringUtil.toString(mobile.getAppName()));
        mediaRequest.setCarrier(mobile.getOperateId());
        mediaRequest.setConnectiontype(mobile.getNetwork() > 5 ? mobile.getNetwork() : Constant.ConnectionType.CELL);
        mediaRequest.setDevicetype(Constant.DeviceType.UNKNOWN);
        VamakerRTB.VamRequest.Mobile.Point point = mobile.getCorner();
        if (point != null) {
            mediaRequest.setLon(point.getLongitude());
            mediaRequest.setLat(point.getLatitude());
        }
        String osv = mobile.getOsVersion();
        if (!StringUtils.isEmpty(osv)) {
            mediaRequest.setOsv(osv);
        }
        String ua = bidRequest.getUserAgent();
        if (!StringUtils.isEmpty(ua)) {
            mediaRequest.setUa(ua);
        }
        String ip = bidRequest.getIp();
        if (!StringUtils.isEmpty(ip)) {
            mediaRequest.setIp(ip);
        }
        mediaRequest.setType(Constant.MediaType.APP);
        logger.info("Vamakerrequest convert mediaRequest is : {}", JSON.toJSONString(mediaRequest));
        return mediaRequest.build();
    }

    private int validateRequiredParam(VamRequest bidRequest) {
        if(ObjectUtils.isNotEmpty(bidRequest)){
            String id = bidRequest.getId();
            if(StringUtils.isEmpty(id)){
                logger.debug("VamakerRTB.id is missing");
                return Constant.StatusCode.BAD_REQUEST;
            }
            VamakerRTB.VamRequest.Mobile vamMobile = bidRequest.getVamMobile();
            if (ObjectUtils.isEmpty(vamMobile)) {
                logger.debug("{},VamakerRTB.VamMobile is missing",id);
                return Constant.StatusCode.BAD_REQUEST;
            }  
            //0-其他,1-android，2-ios
            int os = vamMobile.getOs();
            switch (os) {
            case VamakerStatusCode.Os.ANDROID:
                String imei = vamMobile.getImei();
                String aid = vamMobile.getAid();
                if (StringUtils.isEmpty(imei) && StringUtils.isEmpty(aid)) {
                    logger.debug("{},VamakerRTB.VamMobile:imei,aid is missing",id);
                    return Constant.StatusCode.BAD_REQUEST;
                }
                break;
            case VamakerStatusCode.Os.IOS:
                String idfa = vamMobile.getIDFA();
                if (StringUtils.isEmpty(idfa)) {
                    logger.debug("{},VamakerRTB.VamMobile.idfa is missing",id);
                    return Constant.StatusCode.BAD_REQUEST;
                }
                break;
            }
            if (vamMobile.getAdspaceId() <= 0) {
                logger.debug("{},VamakerRTB.VamMobile.AdspaceId is missing",id);
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
                    VamakerRTB.VamResponse.Builder vamResponse = convertToVamakerResponse(mediaBidMetaData);
                    if(null != vamResponse){
                        resp.setContentType("application/octet-stream;charset=UTF-8");
                        resp.getOutputStream().write(vamResponse.build().toByteArray());
                        return true;
                    }
                } else {
                    resp.setStatus(mediaBid.getStatus());
                    return false;
                }
            }
        } catch (Exception e) {
            logger.error(e.toString() + "_Status_" + Constant.StatusCode.BAD_REQUEST);
            resp.setStatus(Constant.StatusCode.NO_CONTENT);
            return false;
        }
        resp.setStatus(Constant.StatusCode.NO_CONTENT);
        return false;
    }

    private Builder convertToVamakerResponse(MediaBidMetaData mediaBidMetaData) {
        VamakerRTB.VamResponse.Builder bidResposeBuilder = VamakerRTB.VamResponse.newBuilder();
        VamakerRTB.VamResponse.Bid.Builder bidBuilder = VamakerRTB.VamResponse.Bid.newBuilder();
        VamakerRTB.VamRequest bidRequest=(VamRequest)mediaBidMetaData.getRequestObject();
        MediaResponse.Builder mediaResponse = mediaBidMetaData.getMediaBidBuilder().getResponseBuilder();
        
        
        bidResposeBuilder.setId(bidRequest.getId());
        bidBuilder.setCrid(mediaResponse.getAdmid());
        bidBuilder.setPrice(bidRequest.getVamMobile().getBidfloor());
        
        
        VamakerRTB.VamResponse.Bid.Mobile.Builder mobileBuilder = VamakerRTB.VamResponse.Bid.Mobile.newBuilder();
        List<Track> imgtracking = mediaResponse.getMonitorBuilder().getImpurl();
        if (imgtracking != null && imgtracking.size() != 0) {
            for (Track track : imgtracking) {
                mobileBuilder.addShowUrls(track.getUrl());
            }
        }
        mobileBuilder.addClickUrls(UrlEncoded.encodeString(String.valueOf(mediaResponse.getLayout())));//第一个是landingpage，还要对整个urldecode，其余的都是建波
        List<String> thclkurls = mediaResponse.getMonitorBuilder().getClkurl();
        if (thclkurls != null && thclkurls.size() != 0) {
            for (String thclkurl : thclkurls) {
                mobileBuilder.addShowUrls(thclkurl);
            }
        }
        bidBuilder.setMobileBidding(mobileBuilder);
        bidResposeBuilder.addBid(bidBuilder);
        logger.info("VamaKer Response params is : {}", JSON.toJSONString(bidResposeBuilder));
        return bidResposeBuilder;
    }
    
}
