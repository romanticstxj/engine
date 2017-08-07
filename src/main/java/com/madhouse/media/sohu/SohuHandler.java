package com.madhouse.media.sohu;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.madhouse.cache.CacheManager;
import com.madhouse.cache.MediaBidMetaData;
import com.madhouse.cache.PlcmtMetaData;
import com.madhouse.media.MediaBaseHandler;
import com.madhouse.media.sohu.SohuRTB.Request;
import com.madhouse.media.sohu.SohuRTB.Request.Device;
import com.madhouse.media.sohu.SohuRTB.Request.Impression;
import com.madhouse.media.sohu.SohuRTB.Response.Builder;
import com.madhouse.ssp.Constant;
import com.madhouse.ssp.avro.MediaBid;
import com.madhouse.ssp.avro.MediaRequest;
import com.madhouse.ssp.avro.MediaResponse;
import com.madhouse.ssp.avro.Track;
import com.madhouse.util.ObjectUtils;

public class SohuHandler extends MediaBaseHandler {
    
    @Override
    public boolean parseMediaRequest(HttpServletRequest req, MediaBidMetaData mediaBidMetaData, HttpServletResponse resp) {
        
        try {
            SohuRTB.Request bidRequest = SohuRTB.Request.parseFrom(IOUtils.toByteArray(req.getInputStream()));
            int status =  validateRequiredParam(bidRequest);
            if(status == Constant.StatusCode.OK){
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
    private int validateRequiredParam(Request bidRequest) {
        
        if (ObjectUtils.isEmpty(bidRequest)) {
            logger.debug("bidRequest is missing");
            return Constant.StatusCode.BAD_REQUEST;
        }
        String bid = bidRequest.getBidid();
        if (StringUtils.isEmpty(bid)) {
            logger.debug("bid is missing");
            return Constant.StatusCode.BAD_REQUEST;
        }
        if (ObjectUtils.isEmpty(bidRequest.getImpression(0))) {
            logger.debug("{},bidRequest.Impression is missing",bid);
            return Constant.StatusCode.BAD_REQUEST;
        }
        if (ObjectUtils.isEmpty(bidRequest.getImpression(0).getPid())) {
            logger.debug("{},bidRequest.Impression.pid is missing",bid);
            return Constant.StatusCode.BAD_REQUEST;
        }
        if (ObjectUtils.isEmpty(bidRequest.getDevice())) {
            logger.debug("{},bidRequest.Device is missing",bid);
            return Constant.StatusCode.BAD_REQUEST;
        }
        return Constant.StatusCode.OK;
    }
    private MediaRequest conversionToPremiumMADDataModel(Request bidRequest) {
        MediaRequest.Builder mediaRequest = MediaRequest.newBuilder();
        mediaRequest.setBid(bidRequest.getBidid());
        //是否为测试流量，0 为非测试，1 为测试 
        mediaRequest.setTest(bidRequest.getIsTest() == 0 ? Constant.Test.REAL : Constant.Test.PING);
        Impression impression =bidRequest.getImpression(0);
        Device device = bidRequest.getDevice();
        //曝光底价，CPM 计，单位为人民币分 
        mediaRequest.setBidfloor(impression.getBidFloor());
        if(impression.getBanner() !=null && ! impression.getBanner().equals("")){
            mediaRequest.setW(impression.getBanner().getWidth());
            mediaRequest.setH(impression.getBanner().getHeight());
        }else{
            mediaRequest.setW(impression.getVideo().getWidth());
            mediaRequest.setH(impression.getVideo().getHeight());
        }
        String ip = device.getIp();
        if (!StringUtils.isEmpty(ip)) {
            mediaRequest.setIp(ip);
        }
        String ua =device.getUa();
        if (!StringUtils.isEmpty(ua)) {
            mediaRequest.setUa(ua);
        }
        mediaRequest.setCarrier(Constant.Carrier.UNKNOWN);
        mediaRequest.setDevicetype(Constant.DeviceType.UNKNOWN);
        //网络类型(不区分大小写)：2G，3G，4G，WIFI 
        switch (device.getNetType()) {
            case SohuStatusCode.ConnectionType._2G:
                mediaRequest.setConnectiontype(Constant.ConnectionType._2G);
                break;
            case SohuStatusCode.ConnectionType._3G:
                mediaRequest.setConnectiontype(Constant.ConnectionType._3G);
                break;
            case SohuStatusCode.ConnectionType._4G:
                mediaRequest.setConnectiontype(Constant.ConnectionType._4G);
                break;
            case SohuStatusCode.ConnectionType.WIFI:
                mediaRequest.setConnectiontype(Constant.ConnectionType.WIFI);
                break;
            default:
                mediaRequest.setConnectiontype(Constant.ConnectionType.UNKNOWN);
                break;
        }
        //搜狐广告位不区分ios和安卓，所以这里写死.数据库里配置的广告位也都是安卓的
        if(device.getType().equalsIgnoreCase(SohuStatusCode.Devicetype.MOBILE)){
            switch (device.getMobileType()) {
                case SohuStatusCode.Os.OS_IPHONE:
                case SohuStatusCode.Os.OS_IPAD:
                    mediaRequest.setOs(Constant.OSType.IOS);
                    break;
                case SohuStatusCode.Os.OS_ANDROIDPAD:
                case SohuStatusCode.Os.OS_ANDROIDPHONE:
                    mediaRequest.setOs(Constant.OSType.ANDROID);
                    break;
                default:
                    mediaRequest.setOs(Constant.OSType.ANDROID);
                    break;
            }
            mediaRequest.setType(Constant.MediaType.APP);
        }else{
            mediaRequest.setOs(Constant.OSType.ANDROID);
            mediaRequest.setType(Constant.MediaType.SITE);
        }
        String imei =device.getImei();
        if (!StringUtils.isEmpty(imei)) {
            mediaRequest.setDidmd5(imei);
        }
        String mac =device.getMac();
        if (!StringUtils.isEmpty(mac)) {
            mediaRequest.setMacmd5(imei);
        }
        String idfa = bidRequest.getDevice().getIdfa();
        if (!StringUtils.isEmpty(idfa)) {
            mediaRequest.setDpid(idfa);
        }
        String androidId =bidRequest.getDevice().getAndroidID();
        if (!StringUtils.isEmpty(androidId)) {
            mediaRequest.setDpid(androidId);
        }
        String openUDID = bidRequest.getDevice().getOpenUDID();
        if (!StringUtils.isEmpty(openUDID)) {
            mediaRequest.setDpid(openUDID);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("SH:").append(bidRequest.getImpression(0).getPid()).append(":").append(SohuStatusCode.Os.ANDROID);
        PlcmtMetaData plcmtMetaData = CacheManager.getInstance().getPlcmtMetaData(sb.toString());
        if (plcmtMetaData != null) {
            mediaRequest.setAdspacekey(plcmtMetaData.getAdspaceKey());
        } else {
            return null;
        }
        return mediaRequest.build();
    }
    @Override
    public boolean packageMediaResponse(MediaBidMetaData mediaBidMetaData, HttpServletResponse resp) {
        try {
            if (mediaBidMetaData != null && mediaBidMetaData.getMediaBidBuilder() != null) {
                MediaBid.Builder mediaBid = mediaBidMetaData.getMediaBidBuilder();
                if (mediaBid.getResponseBuilder() != null && mediaBid.getStatus() == Constant.StatusCode.OK) {
                    SohuRTB.Response.Builder  bidResponse = convertToSohuResponse(mediaBidMetaData);
                    if(null != bidResponse){
                        resp.setContentType("application/octet-stream");
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
    private Builder convertToSohuResponse(MediaBidMetaData mediaBidMetaData) {
        SohuRTB.Response.Builder bidResponseBuiler = SohuRTB.Response.newBuilder();
        SohuRTB.Request bidRequest = (Request) mediaBidMetaData.getRequestObject();
        MediaResponse mediaResponse = mediaBidMetaData.getMediaBidBuilder().getResponse();
        bidResponseBuiler.setBidid(bidRequest.getBidid());
        bidResponseBuiler.setVersion(bidRequest.getVersion());
        SohuRTB.Response.SeatBid.Builder seatBuilder =SohuRTB.Response.SeatBid.newBuilder();  
        seatBuilder.setIdx(bidRequest.getImpression(0).getIdx());
        //bid对象
        SohuRTB.Response.Bid.Builder bidBuilder = SohuRTB.Response.Bid.newBuilder();
        bidBuilder.setAdurl(mediaResponse.getAdm().get(0));
        
        //exchange 自己的展示和点击监播
        List<Track> tracks= mediaResponse.getMonitor().getImpurl();
        if (tracks != null && tracks.size() > 0) {
            if (tracks.size() >= 2) {
                bidBuilder.setDisplayPara(tracks.get(0).getUrl());
            }
            bidBuilder.setExt1(tracks.get(tracks.size() - 1).getUrl());//取最后一个（exchange自己的建波）
        }
        
        //如果有落地页就取值，如果没有，判断thclkurl的大小，如果size=2，第一条设置为ClickMonitor，第二条设置为ext2，；如果size=1，则只设置为ext2的值
        List<String> list = mediaResponse.getMonitor().getClkurl();
        if (list != null) {
            if (mediaResponse.getLpgurl() != null && mediaResponse.getLpgurl() != "") {
                bidBuilder.setClickPara(mediaResponse.getLpgurl());//落地页地址
            } else {
                if (list.size() >=2){
                    bidBuilder.setClickPara(list.get(0));
                }
            }
            
            if (list.size() >= 0) {
                bidBuilder.setExt2(list.get(list.size() - 1));
            }
        }
        seatBuilder.addBid(bidBuilder);
        bidResponseBuiler.addSeatbid(seatBuilder);
        return bidResponseBuiler;
    }
    
}