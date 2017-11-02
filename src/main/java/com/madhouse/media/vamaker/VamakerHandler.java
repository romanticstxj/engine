package com.madhouse.media.vamaker;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.googlecode.protobuf.format.JsonFormat;
import com.madhouse.ssp.avro.*;
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
import com.madhouse.util.ObjectUtils;
import com.madhouse.util.StringUtil;

public class VamakerHandler extends MediaBaseHandler {
    
    @Override
    public boolean parseMediaRequest(HttpServletRequest req, MediaBidMetaData mediaBidMetaData, HttpServletResponse resp) {
        
        try {
            VamakerRTB.VamRequest bidRequest = VamakerRTB.VamRequest.parseFrom(IOUtils.toByteArray(req.getInputStream()));
            logger.info("Vamaker Request params is : {}", JsonFormat.printToString(bidRequest));

            int status = validateRequiredParam(bidRequest);
            if (Constant.StatusCode.OK == status){
                MediaRequest.Builder mediaRequest = conversionToPremiumMADDataModel(bidRequest);
                if(null != mediaRequest){
                    mediaBidMetaData.getMediaBidBuilder().setRequestBuilder(mediaRequest);
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
    
    private MediaRequest.Builder conversionToPremiumMADDataModel(VamRequest bidRequest) {
        MediaRequest.Builder mediaRequest = MediaRequest.newBuilder();

        mediaRequest.setBid(bidRequest.getId());
        VamakerRTB.VamRequest.Mobile mobile = bidRequest.getVamMobile();
        StringBuilder sb = new StringBuilder();
        sb.append("VAM:").append(mobile.getAdspaceId());
        if (VamakerStatusCode.OSType.ANDROID == mobile.getOs()) {
            sb.append(":ANDROID");
        } else if (VamakerStatusCode.OSType.IOS == mobile.getOs()) {
            sb.append(":IOS");
        }

        MediaMappingMetaData mediaMappingMetaData = CacheManager.getInstance().getMediaMapping(sb.toString());
        if (mediaMappingMetaData == null) {
            return null;
        }
        mediaRequest.setAdtype(2);
        mediaRequest.setType(Constant.MediaType.APP);
        mediaRequest.setAdspacekey(mediaMappingMetaData.getAdspaceKey());
        mediaRequest.setName(mobile.getAppName());
        mediaRequest.setBundle(mobile.getPgn());
        mediaRequest.setModel(StringUtil.toString(mobile.getModel()));
        mediaRequest.setMake(StringUtil.toString(mobile.getBrand()));
        mediaRequest.setMacmd5(StringUtil.toString(mobile.getMac()));
        mediaRequest.setUa(StringUtil.toString(bidRequest.getUserAgent()));

        mediaRequest.setConnectiontype(Constant.ConnectionType.UNKNOWN);
        if (mobile.hasNetwork()) {
            mediaRequest.setConnectiontype(mobile.getNetwork());
        }

        mediaRequest.setCarrier(Constant.Carrier.UNKNOWN);
        if (mobile.hasOperateId()) {
            mediaRequest.setCarrier(mobile.getOperateId());
        }

        mediaRequest.setDevicetype(Constant.DeviceType.UNKNOWN);
        if (bidRequest.hasDeviceType()) {
            switch (bidRequest.getDeviceType()) {
                case PC: {
                    mediaRequest.setDevicetype(Constant.DeviceType.COMPUTER);
                    break;
                }

                case MOBILE: {
                    mediaRequest.setDevicetype(Constant.DeviceType.PHONE);
                    break;
                }

                case PAD: {
                    mediaRequest.setDevicetype(Constant.DeviceType.PAD);
                    break;
                }

                case SMART_TV: {
                    mediaRequest.setDevicetype(Constant.DeviceType.TV);
                    break;
                }
            }
        }

        mediaRequest.setOsv(StringUtil.toString(mobile.getOsVersion()));

        switch (mobile.getOs()) {
            case VamakerStatusCode.OSType.ANDROID: {
                mediaRequest.setOs(Constant.OSType.ANDROID);
                String imei = StringUtil.toString(mobile.getImei());
                if (imei.length() < 20) {
                    mediaRequest.setDid(imei);
                } else {
                    mediaRequest.setDidmd5(imei);
                }

                String aid = StringUtil.toString(mobile.getAid());
                if (aid.length() < 20) {
                    mediaRequest.setDpid(aid);
                } else {
                    mediaRequest.setDpidmd5(aid);
                }

                mediaRequest.setIfa(StringUtil.toString(mobile.getAaid()));
                break;
            }

            case VamakerStatusCode.OSType.IOS: {
                mediaRequest.setOs(Constant.OSType.IOS);
                mediaRequest.setIfa(StringUtil.toString(mobile.getIDFA()));
                break;
            }

            default: {
                mediaRequest.setOs(Constant.OSType.UNKNOWN);
                break;
            }
        }

        if (bidRequest.getPmpInfoCount() > 0) {
            mediaRequest.setDealid(Integer.toString(bidRequest.getPmpInfo(0).getDealId()));
        }

        if (mobile.hasCorner() && mobile.getCorner() != null) {
            Geo.Builder geo = Geo.newBuilder();
            geo.setLon(mobile.getCorner().getLongitude());
            geo.setLat(mobile.getCorner().getLatitude());
            mediaRequest.setGeoBuilder(geo);
        }

        if (mobile.hasWidth() && mobile.hasHeight()) {
            mediaRequest.setW(mobile.getWidth());
            mediaRequest.setH(mobile.getHeight());
        }

        mediaRequest.setBidfloor(mobile.getBidfloor());
        return mediaRequest;
    }

    private int validateRequiredParam(VamRequest bidRequest) {
        if(ObjectUtils.isNotEmpty(bidRequest)){
            if (StringUtils.isEmpty(bidRequest.getId())) {
                logger.warn("VamakerRTB.id is missing");
                return Constant.StatusCode.BAD_REQUEST;
            }

            if (StringUtils.isEmpty(bidRequest.getIp())) {
                logger.warn("VamakerRTB.ip is missing");
                return Constant.StatusCode.BAD_REQUEST;
            }

            if (StringUtils.isEmpty(bidRequest.getUserAgent())) {
                logger.warn("VamakerRTB.ua is missing");
                return Constant.StatusCode.BAD_REQUEST;
            }

            if (bidRequest.getVamMobile() == null) {
                logger.warn("VamakerRTB.VamMobile object is missing");
                return Constant.StatusCode.BAD_REQUEST;
            }

            VamRequest.Mobile mobile = bidRequest.getVamMobile();
            if (mobile.getAdspaceId() <= 0) {
                logger.warn("VamakerRTB.adspaceid is missing");
                return Constant.StatusCode.BAD_REQUEST;
            }

            if (mobile.getOs() == VamakerStatusCode.OSType.ANDROID) {
                if (StringUtils.isEmpty(mobile.getImei()) && StringUtils.isEmpty(mobile.getAid()) && StringUtils.isEmpty(mobile.getAaid())) {
                    logger.warn("VamakerRTB android device id is missing");
                    return Constant.StatusCode.BAD_REQUEST;
                }
            }

            if (mobile.getOs() == VamakerStatusCode.OSType.IOS) {
                if (StringUtils.isEmpty(mobile.getIDFA()) && StringUtils.isEmpty(mobile.getOpenUDID())) {
                    logger.warn("VamakerRTB ios device id is missing");
                    return Constant.StatusCode.BAD_REQUEST;
                }
            }

            if (StringUtils.isEmpty(mobile.getAppName()) || StringUtils.isEmpty(mobile.getPgn())) {
                logger.warn("VamakerRTB appname or pkgname is missing");
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
                if (mediaBid.hasResponseBuilder() && mediaBid.getStatus() == Constant.StatusCode.OK) {
                    VamakerRTB.VamResponse vamResponse = convertToVamakerResponse(mediaBidMetaData);
                    if(null != vamResponse){
                        resp.setContentType("application/octet-stream; charset=utf-8");
                        resp.getOutputStream().write(vamResponse.toByteArray());
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

    private VamakerRTB.VamResponse convertToVamakerResponse(MediaBidMetaData mediaBidMetaData) {
        if (mediaBidMetaData.getMaterialMetaData() == null) {
            return null;
        }

        VamakerRTB.VamResponse.Builder vamResponse = VamakerRTB.VamResponse.newBuilder();
        VamakerRTB.VamResponse.Bid.Builder bidBuilder = VamakerRTB.VamResponse.Bid.newBuilder();
        VamakerRTB.VamRequest bidRequest= (VamRequest)mediaBidMetaData.getRequestObject();

        MediaResponse.Builder mediaResponse = mediaBidMetaData.getMediaBidBuilder().getResponseBuilder();

        vamResponse.setId(bidRequest.getId());
        bidBuilder.setCrid(StringUtil.toString(mediaBidMetaData.getMaterialMetaData().getMediaQueryKey()));
        bidBuilder.setPrice(mediaResponse.getPrice());

        if (mediaResponse.hasDealid() && !StringUtils.isEmpty(mediaResponse.getDealid())) {
            bidBuilder.setDealId(Integer.parseInt(mediaResponse.getDealid()));
        }

        VamakerRTB.VamResponse.Bid.Mobile.Builder mobileBuilder = VamakerRTB.VamResponse.Bid.Mobile.newBuilder();
        List<Track> imgtracking = mediaResponse.getMonitorBuilder().getImpurl();
        if (imgtracking != null && imgtracking.size() > 0) {
            for (Track track : imgtracking) {
                mobileBuilder.addShowUrls(track.getUrl());
            }
        }

        mobileBuilder.addClickUrls(UrlEncoded.encodeString(String.valueOf(mediaResponse.getLpgurl())));//第一个是landingpage，还要对整个urldecode，其余的都是建波
        List<String> clkurls = mediaResponse.getMonitorBuilder().getClkurl();
        if (clkurls != null && clkurls.size() > 0) {
            for (String clkurl : clkurls) {
                mobileBuilder.addClickUrls(clkurl);
            }
        }

        bidBuilder.setMobileBidding(mobileBuilder);
        vamResponse.addBid(bidBuilder);

        try {
            VamakerRTB.VamResponse bidResponse = vamResponse.build();
            logger.info("VamaKer Response params is : {}", JsonFormat.printToString(bidResponse));
            return bidResponse;
        } catch (Exception e) {
            logger.error(e.toString());
            return null;
        }
    }
}
