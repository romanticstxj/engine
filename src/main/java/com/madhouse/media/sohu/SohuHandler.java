package com.madhouse.media.sohu;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.madhouse.cache.CacheManager;
import com.madhouse.cache.MediaBidMetaData;
import com.madhouse.cache.MediaMappingMetaData;
import com.madhouse.media.MediaBaseHandler;
import com.madhouse.media.sohu.SohuRTB.Request;
import com.madhouse.media.sohu.SohuRTB.Request.Device;
import com.madhouse.media.sohu.SohuRTB.Request.Impression;
import com.madhouse.ssp.Constant;
import com.madhouse.ssp.avro.MediaBid;
import com.madhouse.ssp.avro.MediaRequest;
import com.madhouse.ssp.avro.MediaResponse;
import com.madhouse.ssp.avro.Track;
import com.madhouse.util.ObjectUtils;
import com.madhouse.util.StringUtil;

public class SohuHandler extends MediaBaseHandler {
    
    @Override
    public boolean parseMediaRequest(HttpServletRequest req, MediaBidMetaData mediaBidMetaData, HttpServletResponse resp) {
        
        try {
            SohuRTB.Request bidRequest = SohuRTB.Request.parseFrom(IOUtils.toByteArray(req.getInputStream()));
            logger.info("Sohu Request params is : {}", bidRequest.toString());
            mediaBidMetaData.setRequestObject(bidRequest);
            int status =  validateRequiredParam(bidRequest);
            if(status == Constant.StatusCode.OK){
                MediaRequest.Builder mediaRequest = conversionToPremiumMADDataModel(bidRequest);
                if(mediaRequest != null){
                    mediaBidMetaData.getMediaBidBuilder().setRequestBuilder(mediaRequest);
                    return true;
                }
            }
        } catch (Exception e) {
            logger.error(e.toString() + "_Status_" + Constant.StatusCode.NO_CONTENT);
        }
        convertToSohuResponse(mediaBidMetaData,Constant.StatusCode.NO_CONTENT,resp);
        return false;
    }
    private int 嗯嗯(Request bidRequest) {
        if (ObjectUtils.isEmpty(bidRequest)) {
            logger.warn("bidRequest is missing");
            return Constant.StatusCode.BAD_REQUEST;
        }
        String bid = bidRequest.getBidid();
        if (StringUtils.isEmpty(bid)) {
            logger.warn("bid is missing");
            return Constant.StatusCode.BAD_REQUEST;
        }
        if (!bidRequest.hasVersion()) {
            logger.warn("version is missing");
            return Constant.StatusCode.BAD_REQUEST;
        }
        if (ObjectUtils.isEmpty(bidRequest.getImpression(0))) {
            logger.warn("{},bidRequest.Impression is missing",bid);
            return Constant.StatusCode.BAD_REQUEST;
        }
        if (!bidRequest.getImpression(0).hasIdx()) {
            logger.warn("{},bidRequest.Impression.Idx is missing",bid);
            return Constant.StatusCode.BAD_REQUEST;
        }
        if (ObjectUtils.isEmpty(bidRequest.getImpression(0).getPid())) {
            logger.warn("{},bidRequest.Impression.pid is missing",bid);
            return Constant.StatusCode.BAD_REQUEST;
        }
        if (ObjectUtils.isEmpty(bidRequest.getDevice())) {
            logger.warn("{},bidRequest.Device is missing",bid);
            return Constant.StatusCode.BAD_REQUEST;
        }
        return Constant.StatusCode.OK ;
    }
    private MediaRequest.Builder conversionToPremiumMADDataModel(Request bidRequest) {
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
        String campaignId = impression.getCampaignId();
        if(!StringUtils.isEmpty(campaignId)){
            mediaRequest.setDealid(campaignId);
        }
        String ip = device.getIp();
        if (!StringUtils.isEmpty(ip)) {
            mediaRequest.setIp(ip);
        }
        String ua =device.getUa();
        if (!StringUtils.isEmpty(ua)) {
            mediaRequest.setUa(ua);
        }
        mediaRequest.setAdtype(2);
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
            mediaRequest.setType(Constant.MediaType.SITE);
        }
        String imei =device.getImei();
        if (!StringUtils.isEmpty(imei)) {
        	mediaRequest.setOs(Constant.OSType.ANDROID);
            mediaRequest.setDidmd5(imei);
        }
        String mac =device.getMac();
        if (!StringUtils.isEmpty(mac)) {
            mediaRequest.setMacmd5(mac);
        }
        String idfa = bidRequest.getDevice().getIdfa();
        if (!StringUtils.isEmpty(idfa)) {
        	mediaRequest.setOs(Constant.OSType.IOS);
            mediaRequest.setDpid(idfa);
        }
        String androidId =bidRequest.getDevice().getAndroidID();
        if (!StringUtils.isEmpty(androidId)) {
        	mediaRequest.setOs(Constant.OSType.ANDROID);
            mediaRequest.setDpid(androidId);
        }
        String openUDID = bidRequest.getDevice().getOpenUDID();
        if (!StringUtils.isEmpty(openUDID)) {
        	mediaRequest.setOs(Constant.OSType.IOS);
            mediaRequest.setDpid(openUDID);
        }
        
        if(mediaRequest.hasOs()){
        	StringBuilder adspaceKey = new StringBuilder();
        	adspaceKey.append("SOHU:").append(bidRequest.getImpression(0).getPid()).append(":");
        	if(mediaRequest.getOs().equals(Constant.OSType.IOS)){
        		adspaceKey.append(SohuStatusCode.Os.IOS);
        	} else {
        		adspaceKey.append(SohuStatusCode.Os.ANDROID);
        	}
        	
            
        	adspaceKey.append(":"+mediaRequest.getW()+":"+mediaRequest.getH());;
            MediaMappingMetaData mappingMetaData = CacheManager.getInstance().getMediaMapping(adspaceKey.toString());
            if (mappingMetaData != null) {
                mediaRequest.setAdspacekey(mappingMetaData.getAdspaceKey());
            } else {
                return null;
            }
        } else {
        	return null;
        }
        
        return mediaRequest;
    }
    @Override
    public boolean packageMediaResponse(MediaBidMetaData mediaBidMetaData, HttpServletResponse resp) {
        try {
            if (mediaBidMetaData != null && mediaBidMetaData.getMediaBidBuilder() != null) {
                MediaBid.Builder mediaBid = mediaBidMetaData.getMediaBidBuilder();
                if (mediaBid.hasResponseBuilder() && mediaBid.getStatus() == Constant.StatusCode.OK) {
                    convertToSohuResponse(mediaBidMetaData,Constant.StatusCode.OK,resp);
                    return true;
                }
            } 
        } catch (Exception e) {
            logger.error(e.toString() + "_Status_" + Constant.StatusCode.NO_CONTENT);
        }
        convertToSohuResponse(mediaBidMetaData,Constant.StatusCode.NO_CONTENT,resp);
        return false;
    }
    private void convertToSohuResponse(MediaBidMetaData mediaBidMetaData,int status, HttpServletResponse resp) {
    	SohuRTB.Response.Builder bidResponseBuiler = SohuRTB.Response.newBuilder();
        SohuRTB.Request bidRequest = (Request) mediaBidMetaData.getRequestObject();
        bidResponseBuiler.setBidid(bidRequest.getBidid());
        bidResponseBuiler.setVersion(bidRequest.getVersion());
        
    	if(status == Constant.StatusCode.OK){
    		MediaResponse.Builder mediaResponse = mediaBidMetaData.getMediaBidBuilder().getResponseBuilder();
            SohuRTB.Response.SeatBid.Builder seatBuilder =SohuRTB.Response.SeatBid.newBuilder();  
            seatBuilder.setIdx(bidRequest.getImpression(0).getIdx());
            //bid对象
            SohuRTB.Response.Bid.Builder bidBuilder = SohuRTB.Response.Bid.newBuilder();
            //在底价上加一分
            bidBuilder.setPrice(mediaResponse.getPrice());
            
            bidBuilder.setAdurl(mediaResponse.getAdm().get(0));
            
            //ssp自己的展示和点击监播
            List<Track> tracks= mediaResponse.getMonitorBuilder().getImpurl();
            if(!tracks.isEmpty()){
            	String impUrl = tracks.get(tracks.size()-1).getUrl();
            	bidBuilder.setDisplayPara(impUrl.substring(impUrl.lastIndexOf("?")+1, impUrl.length()));
            }
            
            List<String> clkUrls= mediaResponse.getMonitorBuilder().getClkurl();
            if(!clkUrls.isEmpty()){
            	String clkUrl = clkUrls.get(clkUrls.size()-1).toString();
            	bidBuilder.setClickPara(clkUrl.substring(clkUrl.lastIndexOf("?")+1, clkUrl.length()));
            }
            
            List<String> exts = mediaResponse.getMonitorBuilder().getExts();
            if (!ObjectUtils.isEmpty(exts)) {
                bidBuilder.setExt1(exts.size() >= 1 ? StringUtil.toString(exts.get(0)) : "");
                bidBuilder.setExt2(exts.size() >= 2 ? StringUtil.toString(exts.get(1)) : "");
                bidBuilder.setExt3(exts.size() >= 3 ? StringUtil.toString(exts.get(2)) : "");
                
            }
            seatBuilder.addBid(bidBuilder);
            bidResponseBuiler.addSeatbid(seatBuilder);
            logger.info("sohu Response params is : {}", bidResponseBuiler.toString());
    	}
    	resp.setContentType("application/octet-stream;charset=UTF-8");
        try {
			resp.getOutputStream().write(bidResponseBuiler.build().toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
        resp.setStatus(Constant.StatusCode.OK);
    }
}
