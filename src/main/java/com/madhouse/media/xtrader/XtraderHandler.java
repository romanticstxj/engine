package com.madhouse.media.xtrader;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.madhouse.ssp.avro.*;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.madhouse.cache.CacheManager;
import com.madhouse.cache.MediaBidMetaData;
import com.madhouse.cache.MediaMappingMetaData;
import com.madhouse.media.MediaBaseHandler;
import com.madhouse.media.tencent.TencentStatusCode;
import com.madhouse.ssp.Constant;
import com.madhouse.ssp.avro.MediaResponse.Builder;
import com.madhouse.util.HttpUtil;
import com.madhouse.util.ObjectUtils;

public class XtraderHandler extends MediaBaseHandler {
    
    @Override
    public boolean parseMediaRequest(HttpServletRequest req, MediaBidMetaData mediaBidMetaData, HttpServletResponse resp) {
        boolean isSandbox = false;
        String env = req.getHeader("env");
        if (env != null && env.equals("sandbox")) {
            isSandbox = true;
        }
        try {
            req.setCharacterEncoding("UTF-8");
            String bytes = HttpUtil.getRequestPostBytes(req);
            XtraderBidRequest xtraderBidRequest = JSON.parseObject(bytes, XtraderBidRequest.class);
            logger.info("Xtrader Request params is : {}",JSON.toJSONString(xtraderBidRequest));
            int status = validateRequiredParam(xtraderBidRequest);
            if (Constant.StatusCode.OK != status){
                MediaRequest.Builder mediaRequest = conversionToPremiumMADDataModel(isSandbox, xtraderBidRequest);
                if(mediaRequest != null){
                    mediaBidMetaData.getMediaBidBuilder().setRequestBuilder(mediaRequest);
                    mediaBidMetaData.setRequestObject(xtraderBidRequest);
                    return true;
                }
            }
            resp.setStatus(Constant.StatusCode.BAD_REQUEST);
            return false;
        } catch (Exception e) {
            logger.error(e.toString() + "_Status_" + Constant.StatusCode.BAD_REQUEST);
            resp.setStatus(Constant.StatusCode.BAD_REQUEST);
            return false;
        }
    }
    
    private MediaRequest.Builder conversionToPremiumMADDataModel(boolean isSandbox, XtraderBidRequest xtraderBidRequest) {
        MediaRequest.Builder mediaRequest = MediaRequest.newBuilder();
        
        XtraderBidRequest.App app = xtraderBidRequest.getApp();
        XtraderBidRequest.Device device = xtraderBidRequest.getDevice();
        XtraderBidRequest.Imp imp = xtraderBidRequest.getImp().get(0);
        
        mediaRequest.setBid(xtraderBidRequest.getId());
        
        mediaRequest.setW(imp.getBanner().getW());
        mediaRequest.setH(imp.getBanner().getH());
        
        String os = device.getOs();
        if (XtraderStatusCode.Os.OS_IOS.equalsIgnoreCase(os)) {
            mediaRequest.setOs(Constant.OSType.IOS);
            mediaRequest.setIfa(device.getExt().getIdfa());
            mediaRequest.setDpidmd5(device.getDpidmd5());
        } else if (XtraderStatusCode.Os.OS_ANDROID.equalsIgnoreCase(os)) {
            mediaRequest.setOs(Constant.OSType.ANDROID);
            mediaRequest.setDpidmd5(device.getDpidmd5());
            mediaRequest.setDidmd5(device.getDidmd5());
        }
        mediaRequest.setBundle(app.getBundle());
        mediaRequest.setName(app.getName());
        mediaRequest.setDevicetype(Constant.DeviceType.UNKNOWN);
        Integer showtype = imp.getExt().getShowtype();
        String adspaceKey = null;
        if (isSandbox) {//sandbox环境
            adspaceKey = "sandbox:" + "LJ:" + showtype + ":" + device.getOs().toLowerCase();
            mediaRequest.setTest(Constant.Test.SIMULATION);
        } else {
            adspaceKey = "LJ:" + showtype + ":" + device.getOs().toLowerCase();
            mediaRequest.setTest(Constant.Test.REAL);
        }
        MediaMappingMetaData mappingMetaData = CacheManager.getInstance().getMediaMapping(adspaceKey);
        if (mappingMetaData != null) {
            mediaRequest.setAdspacekey(mappingMetaData.getAdspaceKey());
        } else {
            if (isSandbox) {//sandbox环境
                mappingMetaData = CacheManager.getInstance().getMediaMapping("sandbox:LJ:0:0");
            } else {
                mappingMetaData = CacheManager.getInstance().getMediaMapping("LJ:0:0");
            }
            if(mappingMetaData != null){
                mediaRequest.setAdspacekey(mappingMetaData.getAdspaceKey());
            }else{
                return null;
            }
        }
        // xtrader 网络连接类型，和OpenRTB一致：0—未知，1—Ethernet，2—wifi，3—蜂窝网络，未知代，4—蜂窝网络，2G，5—蜂窝网络，3G，6—蜂窝网络，4G。
        if(null != device.getConnectiontype()){
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
        String carrier = device.getCarrier();
        switch (carrier) {
            case XtraderStatusCode.Carrier.CHINA_MOBILE:
                mediaRequest.setCarrier(Constant.Carrier.CHINA_MOBILE);
                break;
            case XtraderStatusCode.Carrier.CHINA_TELECOM:
                mediaRequest.setCarrier(Constant.Carrier.CHINA_TELECOM);
                break;
            case XtraderStatusCode.Carrier.CHINA_UNICOM:
                mediaRequest.setCarrier(Constant.Carrier.CHINA_UNICOM);
                break;
            default:
                mediaRequest.setCarrier(Constant.Carrier.UNKNOWN);
                break;
        }
        if (!StringUtils.isEmpty(device.getOsv())) {
            mediaRequest.setOsv(device.getOsv());
        }
        if(!StringUtils.isEmpty(device.getExt().getMac())){
            mediaRequest.setMac(device.getExt().getMac());
        }
        if(!StringUtils.isEmpty(device.getExt().getMacmd5())){
            mediaRequest.setMacmd5(device.getExt().getMacmd5());
        }
        if (!StringUtils.isEmpty(device.getMake())) {
            mediaRequest.setMake(device.getMake());
        }
        if (!StringUtils.isEmpty(device.getModel())) {
            mediaRequest.setModel(device.getModel());
        }
        if (!StringUtils.isEmpty(device.getUa())) {
            mediaRequest.setUa(device.getUa());
        }
        if (!StringUtils.isEmpty(device.getIp())) {
            mediaRequest.setIp(device.getIp());
        }

        if (!StringUtils.isEmpty(device.getGeo().getLat()+"") && !StringUtils.isEmpty(device.getGeo().getLon()+"")) {
            Geo.Builder geo = Geo.newBuilder();
            geo.setLat(device.getGeo().getLat());
            geo.setLon(device.getGeo().getLon());
            mediaRequest.setGeoBuilder(geo);
        }

        mediaRequest.setType(ObjectUtils.isEmpty(xtraderBidRequest.getSite()) ? Constant.MediaType.APP : Constant.MediaType.SITE);
        return mediaRequest;
    }

    private int validateRequiredParam(XtraderBidRequest xtraderBidRequest) {
        if(ObjectUtils.isNotEmpty(xtraderBidRequest)){
            String id = xtraderBidRequest.getId();
            if(StringUtils.isEmpty(id)){
                logger.warn("{}:xtraderBidRequest.id is null",id);
                return Constant.StatusCode.BAD_REQUEST;
            }
            XtraderBidRequest.App app = xtraderBidRequest.getApp();
            if(ObjectUtils.isEmpty(app)){
                logger.warn("{}:xtraderBidRequest.app is null",id);
                return Constant.StatusCode.BAD_REQUEST;
            }
            
            XtraderBidRequest.Device device = xtraderBidRequest.getDevice();
            if(ObjectUtils.isEmpty(device)){
                logger.warn("{}:xtraderBidRequest.device is null",id);
                return Constant.StatusCode.BAD_REQUEST;
            }
            if(StringUtils.isEmpty(device.getOs()) && XtraderStatusCode.Os.OS_ANDROID != device.getOs() && XtraderStatusCode.Os.OS_IOS != device.getOs()){
                logger.warn("{}:xtraderBidRequest.device.id is null",id);
                return Constant.StatusCode.BAD_REQUEST;
            }
            XtraderBidRequest.Imp imp = xtraderBidRequest.getImp().get(0);
            if(ObjectUtils.isEmpty(imp)){
                logger.warn("{}:xtraderBidRequest.imp is null",id);
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
                    XtraderResponse  bidResponse = convertToXtraderResponse(mediaBidMetaData);
                    if(null != bidResponse){
                        resp.setHeader("Content-Type", "application/json; charset=utf-8");
                        resp.getOutputStream().write(JSON.toJSONString(bidResponse).getBytes());
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

    private XtraderResponse convertToXtraderResponse(MediaBidMetaData mediaBidMetaData) {
        XtraderBidRequest xtraderRequest = (XtraderBidRequest) mediaBidMetaData.getRequestObject();
        Builder mediaResponse = mediaBidMetaData.getMediaBidBuilder().getResponseBuilder();
        XtraderResponse response = new XtraderResponse();  
        XtraderResponse.Seatbid seatbid = response.new Seatbid();
        XtraderResponse.Seatbid.Bid bid = seatbid.new Bid();
        XtraderResponse.Seatbid.Bid.Ext ext = bid.new Ext();
        
        ext.setLdp(mediaResponse.getLpgurl());
        for (Track track : mediaResponse.getMonitorBuilder().getImpurl()) {
            ext.getPm().add(track.getUrl());
        }
        ext.setCm(mediaResponse.getMonitorBuilder().getClkurl());
        bid.setExt(ext);
        bid.setAdm(mediaResponse.getAdm().get(0));
        String impid = xtraderRequest.getImp().get(0).getId();
        bid.setId(mediaBidMetaData.getMediaBidBuilder().getImpid());
        bid.setImpid(impid);
        bid.setNurl("");
        bid.setCrid("");
        bid.setPrice(mediaResponse.getPrice());
        ArrayList<XtraderResponse.Seatbid.Bid> bidList = new ArrayList<>(1);
        bidList.add(bid);
        seatbid.setBid(bidList);

        ArrayList<XtraderResponse.Seatbid> seatbids = new ArrayList<>(1);
        seatbids.add(seatbid);
        response.setId(xtraderRequest.getId());
        response.setBidid(mediaBidMetaData.getMediaBidBuilder().getImpid());
        response.setSeatbid(seatbids);
        logger.info("Xtrader Response params is : {}", response.toString());
        return response;
    }
    
}
