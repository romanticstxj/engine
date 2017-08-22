package com.madhouse.media.sina;


import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.madhouse.cache.CacheManager;
import com.madhouse.cache.MediaBidMetaData;
import com.madhouse.cache.MediaMappingMetaData;
import com.madhouse.media.MediaBaseHandler;
import com.madhouse.media.sina.SinaBidRequest.Device.Geo;
import com.madhouse.ssp.Constant;
import com.madhouse.ssp.avro.MediaBid;
import com.madhouse.ssp.avro.MediaRequest;
import com.madhouse.ssp.avro.MediaResponse;
import com.madhouse.ssp.avro.MediaResponse.Builder;
import com.madhouse.ssp.avro.Track;
import com.madhouse.util.HttpUtil;
import com.madhouse.util.ObjectUtils;

public class SinaHandler extends MediaBaseHandler {
    
    @Override
    public boolean parseMediaRequest(HttpServletRequest req, MediaBidMetaData mediaBidMetaData, HttpServletResponse resp) {
        try {
            req.setCharacterEncoding("UTF-8");
            String bytes = HttpUtil.getRequestPostBytes(req);
            SinaBidRequest sinaBidRequest = JSON.parseObject(bytes, SinaBidRequest.class);
            int status = validateRequiredParam(sinaBidRequest, resp);
            if (status == Constant.StatusCode.OK) {
                MediaRequest mediaRequest = conversionToPremiumMADDataModel(sinaBidRequest);
                if(mediaRequest != null){
                    mediaBidMetaData.getMediaBidBuilder().setRequest(mediaRequest);
                    mediaBidMetaData.setRequestObject(sinaBidRequest);
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
    
    private MediaRequest conversionToPremiumMADDataModel(SinaBidRequest sinaBidRequest) {
        MediaRequest.Builder mediaRequest = MediaRequest.newBuilder();
        
        SinaBidRequest.App app = sinaBidRequest.getApp();
        SinaBidRequest.Device device = sinaBidRequest.getDevice();
        SinaBidRequest.Imp imp = sinaBidRequest.getImp().get(0);
            
        // 广告请求唯一id
        mediaRequest.setBid(sinaBidRequest.getId());
        
        mediaRequest.setW(imp.getBanner().getW());
        
        mediaRequest.setH(imp.getBanner().getH());
        
        if(null !=imp.getBanner()){
            mediaRequest.setAdtype(2);
        }else if(null !=imp.getFeed()){
            mediaRequest.setAdtype(3);
        }
        
        mediaRequest.setName(app.getName());
        
        StringBuilder sb = new StringBuilder();
        sb.append("SINA:");
        //广告位id
        sb.append(imp.getTagid()).append(":");
        // 操作系统的类型
        String os = device.getOs(); 
        if (SinaStatusCode.Os.OS_ANDROID.equals(os)) {
            sb.append(SinaStatusCode.Os.OS_ANDROID);
            mediaRequest.setDid(device.getExt().getImei());
            
        } else if(SinaStatusCode.Os.OS_IOS.equals(os)){
            sb.append(SinaStatusCode.Os.OS_IOS);
            mediaRequest.setIfa(device.getExt().getIdfa());
            
        }
        MediaMappingMetaData mappingMetaData = CacheManager.getInstance().getMediaMapping(sb.toString());
        if (mappingMetaData != null) {
            mediaRequest.setAdspacekey(mappingMetaData.getAdspaceKey());
        } else {
            mappingMetaData = CacheManager.getInstance().getMediaMapping("SINA:0:0");
            if(mappingMetaData != null){
                mediaRequest.setAdspacekey(mappingMetaData.getAdspaceKey());
            }else{
                return null;
            }
        }
        //0—未知，1—Ethernet，2—wifi，3—蜂窝网络，未知代，4—蜂窝网络，2G，5—蜂窝网络，3G，6—蜂窝网络，4G。
        switch (device.getConnectionType()) {
            case SinaStatusCode.ConnectionType.UNKNOWN:
                mediaRequest.setConnectiontype(Constant.ConnectionType.ETHERNET);
                break;
            case SinaStatusCode.ConnectionType.WIFI:
                mediaRequest.setConnectiontype(Constant.ConnectionType.WIFI);
                break;
            case SinaStatusCode.ConnectionType._2G:
                mediaRequest.setConnectiontype(Constant.ConnectionType._2G);
                break;
            case SinaStatusCode.ConnectionType._3G:
                mediaRequest.setConnectiontype(Constant.ConnectionType._3G);
                break;
            case SinaStatusCode.ConnectionType._4G:
                mediaRequest.setConnectiontype(Constant.ConnectionType._4G);
                break;
            case SinaStatusCode.ConnectionType.Ethernet:
                mediaRequest.setConnectiontype(Constant.ConnectionType.ETHERNET);
                break;
            default:
                mediaRequest.setConnectiontype(Constant.ConnectionType.CELL);
                break;
        }
        
        /*
            46000：移动
            46001：联通
            46003：电信
            46020：铁通
         */
        String carrier = device.getCarrier();
        carrier = StringUtils.isEmpty(carrier) ? "" : carrier;
        switch (carrier) {
            case SinaStatusCode.Carrier.CHINA_MOBILE:
                mediaRequest.setCarrier(Constant.Carrier.CHINA_MOBILE);
                break;
            case SinaStatusCode.Carrier.CHINA_TELECOM:
                mediaRequest.setCarrier(Constant.Carrier.CHINA_TELECOM);
                break;
            case SinaStatusCode.Carrier.CHINA_UNICOM:
                mediaRequest.setCarrier(Constant.Carrier.CHINA_UNICOM);
                break;
            default:
                mediaRequest.setCarrier(Constant.Carrier.UNKNOWN);
                break;
        }
        if(!StringUtils.isEmpty(device.getIp())){
            mediaRequest.setIp(device.getIp());
        }
        if(!StringUtils.isEmpty(device.getUa())){
            mediaRequest.setUa(device.getUa());
        }
        if(!StringUtils.isEmpty(device.getOsv())){
            mediaRequest.setMake(device.getOsv()); 
        }
        if(!StringUtils.isEmpty(device.getModel())){
            mediaRequest.setModel(device.getModel()); 
        }
        mediaRequest.setBidfloor(imp.getBidfloor());
        Geo geo = device.getGeo();
        if (geo != null) {
            if(ObjectUtils.isNotEmpty(geo.getLat()+"")){
                mediaRequest.setLat((float)geo.getLat());
            }
            if(ObjectUtils.isNotEmpty(geo.getLon()+"")){
                mediaRequest.setLon((float)geo.getLon());
            }
        }
        mediaRequest.setDevicetype(Constant.DeviceType.UNKNOWN);
        mediaRequest.setType(Constant.MediaType.APP);
        return mediaRequest.build();
    }

    private int validateRequiredParam(SinaBidRequest sinaBidRequest, HttpServletResponse resp) {
        if (ObjectUtils.isNotEmpty(sinaBidRequest)) {
            String id = sinaBidRequest.getId();
            if (StringUtils.isNotEmpty(id)) {
                if (ObjectUtils.isEmpty(sinaBidRequest.getDevice())) {
                    logger.debug("sinaBidRequest.Device is null");
                    return Constant.StatusCode.BAD_REQUEST;
                }
                if (ObjectUtils.isEmpty(sinaBidRequest.getDevice().getOs())) {
                    logger.debug("{}:sinaBidRequest.Device.os is null",id);
                    return Constant.StatusCode.BAD_REQUEST;
                }
                if (ObjectUtils.isEmpty(sinaBidRequest.getImp().get(0))) {
                    logger.debug("sinaBidRequest.Imp[0] is null");
                    return Constant.StatusCode.BAD_REQUEST;
                }
                if (ObjectUtils.isEmpty(sinaBidRequest.getApp())) {
                    logger.debug("sinaBidRequest.App is null");
                    return Constant.StatusCode.BAD_REQUEST;
                }
                return Constant.StatusCode.OK;
            }
            logger.debug("baoFengBidRequest.id is null");
        }
        return Constant.StatusCode.BAD_REQUEST;
    }

    @Override
    public boolean packageMediaResponse(MediaBidMetaData mediaBidMetaData, HttpServletResponse resp) {
        try {
            if (mediaBidMetaData != null && mediaBidMetaData.getMediaBidBuilder() != null) {
                MediaBid.Builder mediaBid = mediaBidMetaData.getMediaBidBuilder();
                if (mediaBid.getResponseBuilder() != null && mediaBid.getStatus() == Constant.StatusCode.OK) {
                    SinaResponse result = convertToSinaResponse(mediaBidMetaData);
                    if (result != null) {
                        resp.setHeader("Content-Type", "application/json; charset=utf-8");
                        resp.getOutputStream().write(JSON.toJSONString(result).getBytes("utf-8"));
                        resp.setStatus(Constant.StatusCode.OK);
                        logger.debug("_Status_" + Constant.StatusCode.OK);
                        return true;
                    }
                } else {
                    resp.setStatus(mediaBid.getStatus());
                    return false;
                }
            }
        } catch (Exception e) {
            logger.error(e.toString() + "_Status_" + Constant.StatusCode.BAD_REQUEST);
            resp.setStatus(Constant.StatusCode.BAD_REQUEST);
            return false;
        }
        resp.setStatus(Constant.StatusCode.BAD_REQUEST);
        return false;
    }

    private SinaResponse convertToSinaResponse(MediaBidMetaData mediaBidMetaData) {
        SinaResponse response=new SinaResponse();
        
        SinaBidRequest sinaRequest = (SinaBidRequest) mediaBidMetaData.getRequestObject();

        Builder mediaResponse= mediaBidMetaData.getMediaBidBuilder().getResponseBuilder();
        SinaResponse.Seatbid seatbid = response.new Seatbid();
        SinaResponse.Seatbid.Bid bid = seatbid.new Bid();
        SinaResponse.Seatbid.Bid.Ext ext = bid.new Ext();
        
        ext.setLandingid(mediaResponse.getLpgurl());
        for (Track track : mediaResponse.getMonitorBuilder().getImpurl()) {
            ext.getPm().add(track.getUrl());
        } 
        
        ext.setCm(mediaResponse.getMonitorBuilder().getClkurl());
        
        bid.setExt(ext);
        bid.setAdm(mediaResponse.getAdm().get(0));
        // TODO DSP对该次出价分配的ID
        String impid = sinaRequest.getImp().get(0).getId();
        bid.setId(impid);
        bid.setImpid(impid);
        bid.setNurl("");
        bid.setPrice((float) sinaRequest.getImp().get(0).getBidfloor());
        bid.setCrid(mediaResponse.getCrid());
        
        List<SinaResponse.Seatbid.Bid> bidList = new ArrayList<>(1);
        bidList.add(bid);
        seatbid.setBid(bidList);

        List<SinaResponse.Seatbid> seatbids = new ArrayList<>(1);
        seatbids.add(seatbid);

        response.setId(sinaRequest.getId());
        response.setBidid(mediaBidMetaData.getMediaBidBuilder().getImpid());
        response.setSeatbid(seatbids);

        return response;
    }
    
}
