package com.madhouse.media.toutiao;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.googlecode.protobuf.format.JsonFormat;
import com.madhouse.cache.CacheManager;
import com.madhouse.cache.MediaBidMetaData;
import com.madhouse.cache.MediaMappingMetaData;
import com.madhouse.media.MediaBaseHandler;
import com.madhouse.media.toutiao.TOUTIAOAds.AdSlot.Banner;
import com.madhouse.media.toutiao.TOUTIAOAds.AdType;
import com.madhouse.media.toutiao.TOUTIAOAds.BidRequest;
import com.madhouse.media.toutiao.TOUTIAOAds.BidResponse;
import com.madhouse.ssp.Constant;
import com.madhouse.ssp.avro.Geo;
import com.madhouse.ssp.avro.MediaBid;
import com.madhouse.ssp.avro.MediaRequest;
import com.madhouse.ssp.avro.MediaResponse;
import com.madhouse.util.ObjectUtils;
import com.madhouse.util.StringUtil;
import com.madhouse.util.Utility;

public class ToutiaoHandler extends MediaBaseHandler {
    
    @Override
    public boolean parseMediaRequest(HttpServletRequest req, MediaBidMetaData mediaBidMetaData, HttpServletResponse resp) {
        
        try {
            TOUTIAOAds.BidRequest bidRequest = TOUTIAOAds.BidRequest.parseFrom(IOUtils.toByteArray(req.getInputStream()));
            if (bidRequest == null) {
                resp.setStatus(Constant.StatusCode.BAD_REQUEST);
                return false;
            }
            logger.info("Toutiao Request params is {} "+JsonFormat.printToString(bidRequest));
            
            
            List<MediaBid.Builder> mediaBids = conversionToPremiumMADDataModel(bidRequest);
            
            if (ObjectUtils.isEmpty(mediaBids)) {
            	resp.setStatus(Constant.StatusCode.NO_CONTENT);
                return false;
            }

            if (!this.checkRequestParam(mediaBids.get(0).getRequestBuilder())) {
            	resp.setStatus(Constant.StatusCode.NO_CONTENT);
                return false;
            }

            mediaBidMetaData.setMediaBids(mediaBids);
            mediaBidMetaData.setRequestObject(bidRequest);
            return true;
      
        } catch (Exception e) {
        	logger.error("Toutiao Exception:{}" ,e.toString());
        	resp.setStatus(Constant.StatusCode.NO_CONTENT);
        }
        return false;
        
            
    }
    private List<MediaBid.Builder> conversionToPremiumMADDataModel(BidRequest bidRequest) {
    	
    	List<MediaBid.Builder> mediaBids = new LinkedList<>();
    	
    	MediaRequest.Builder mediaRequest = MediaRequest.newBuilder();
        
        try {
        	if (ObjectUtils.isEmpty(bidRequest.getAdslots(0))) {
                return null;
            }
        	TOUTIAOAds.Device device = bidRequest.getDevice();
        	
            mediaRequest.setBid(StringUtil.toString(bidRequest.getRequestId()));
            mediaRequest.setIp(StringUtil.toString(device.getIp()));
            mediaRequest.setUa(StringUtil.toString(device.getUa()));
            mediaRequest.setMake(StringUtil.toString(device.getMake()));
            mediaRequest.setModel(StringUtil.toString(device.getModel()));
            mediaRequest.setOsv(StringUtil.toString(device.getOsv()));
            mediaRequest.setTest(Constant.Test.REAL);
        	
            //设备运营商
            mediaRequest.setCarrier(Constant.Carrier.UNKNOWN);
            if (!StringUtils.isEmpty(device.getCarrier())) {
                switch (device.getCarrier()) {
                    case ToutiaoConstant.Carrier.CHINA_MOBILE: {
                        mediaRequest.setCarrier(Constant.Carrier.CHINA_MOBILE);
                        break;
                    }

                    case ToutiaoConstant.Carrier.CHINA_UNICOM: {
                        mediaRequest.setCarrier(Constant.Carrier.CHINA_UNICOM);
                        break;
                    }

                    case ToutiaoConstant.Carrier.CHINA_TELECOM: {
                        mediaRequest.setCarrier(Constant.Carrier.CHINA_TELECOM);
                        break;
                    }
                }
            }
            
            //连网方式
            mediaRequest.setConnectiontype(Constant.ConnectionType.UNKNOWN);
            if (ObjectUtils.isNotEmpty(device.getConnectionType())) {
            	switch (device.getConnectionType()) {
                case Honeycomb:
                    mediaRequest.setConnectiontype(Constant.ConnectionType._3G);
                    break;
                case WIFI:
                    mediaRequest.setConnectiontype(Constant.ConnectionType.WIFI);
                    break;
                case UNKNOWN:
                    mediaRequest.setConnectiontype(Constant.ConnectionType.UNKNOWN);
                    break;
                case NT_2G:
                    mediaRequest.setConnectiontype(Constant.ConnectionType._2G);
                    break;
                case NT_4G:
                    mediaRequest.setConnectiontype(Constant.ConnectionType._4G);
                    break;
            	}
            }
            //设备类型
            mediaRequest.setDevicetype(Constant.DeviceType.UNKNOWN);
            if (device.getDeviceType() != null) {
	            switch (device.getDeviceType()) {
	                case PHONE:
	                    mediaRequest.setDevicetype(Constant.DeviceType.PHONE);
	                    break;
	                default:
	                    mediaRequest.setCarrier(Constant.DeviceType.UNKNOWN);
	                    break;
	            }
            }
            
            if (device.getGeo() != null) {
                TOUTIAOAds.Geo geo = device.getGeo();
                Geo.Builder builder = Geo.newBuilder();
                builder.setLat((float)geo.getLat());
                builder.setLon((float)geo.getLon());
                mediaRequest.setGeoBuilder(builder);
            }
            if (bidRequest.getApp() != null) {
                TOUTIAOAds.App app = bidRequest.getApp();
                mediaRequest.setName(StringUtil.toString(app.getName()));
                mediaRequest.setBundle(StringUtil.toString(app.getBundle()));
                mediaRequest.setType(Constant.MediaType.APP);
            }
            
            
        	for (TOUTIAOAds.AdSlot adSlot : bidRequest.getAdslotsList()){
        		MediaBid.Builder mediaBid = MediaBid.newBuilder();
                MediaRequest.Builder request = MediaRequest.newBuilder(mediaRequest);
        		
        		if (adSlot.getPmp() != null) {
                    if (!ObjectUtils.isEmpty(adSlot.getPmp().getDealsList())) {
                        int size = adSlot.getPmp().getDealsCount();
                        request.setDealid(String.valueOf((adSlot.getPmp().getDeals(Utility.nextInt(size)).getId())));
                    }
                }
            	
            	StringBuilder adspaceKey = new StringBuilder();
            	Integer adType = getAdType(bidRequest).getNumber();
            	request.setAdtype(adType);
                adspaceKey.append("TT:").append(adType).append(":").append(adSlot.getChannelId()).append(":");
                
                if (ToutiaoConstant.OSType.ANDROID.equalsIgnoreCase(device.getOs())){
                	adspaceKey.append(ToutiaoConstant.OSType.ANDROID);
                	request.setOs(Constant.OSType.ANDROID);
                    //mediaRequest.setDid(device.getDeviceId());
                	request.setDidmd5(device.getDeviceIdMd5());
                	request.setDpid(device.getAndroidId());
                	request.setDpid(device.getAndroidIdMd5());
                } else if (ToutiaoConstant.OSType.IOS.equalsIgnoreCase(device.getOs())){
                	adspaceKey.append(ToutiaoConstant.OSType.IOS);
                	request.setOs(Constant.OSType.IOS);
                	request.setIfa(device.getDeviceId());
                }
                MediaMappingMetaData mediaMappingMetaData = CacheManager.getInstance().getMediaMapping(adspaceKey.toString());
                if (mediaMappingMetaData == null) {
                    continue;
                }
        		
                request.setAdspacekey(mediaMappingMetaData.getAdspaceKey());
                
        		if (adSlot.getBanner(0) != null) {
                    Banner banner = adSlot.getBanner(0);
                    if(banner.hasWidth()){
                    	request.setW(banner.getWidth());
                    }
                    if(banner.hasHeight()){
                    	request.setH(banner.getHeight());
                    }
                }
        		mediaBid.setRequestBuilder(request);
                mediaBids.add(mediaBid);
        	}
            return mediaBids; 
            
		} catch (Exception e) {
			 logger.error("Toutiao Exception:{}" ,e.toString());
		}
		return null;
    }
    /**
     * 根据广告位的宽度和高度返回今日头条的广告类型，头条只会传1，2，20，如果有多个，就随机取一个
     * @param bidRequest
     * @return
     */
    private AdType getAdType(BidRequest bidRequest) {
        if (bidRequest.getAdslots(0).getAdTypeCount() < 1) {
            return null;
        }

        List<TOUTIAOAds.AdType> needAdTypes = new ArrayList<TOUTIAOAds.AdType>();

        List<TOUTIAOAds.AdType> adTypes = bidRequest.getAdslots(0).getAdTypeList();
        for (TOUTIAOAds.AdType adType : adTypes) {
            if (ToutiaoConstant.ADTYPE.contains(adType.getNumber())) {
                needAdTypes.add(adType);
            }
        }
        if (needAdTypes.size() < 1) {
            return null;
        }
        return needAdTypes.get(Utility.nextInt(needAdTypes.size()));
    }
    @Override
    public boolean packageMediaResponse(MediaBidMetaData mediaBidMetaData, HttpServletResponse resp) {
        
    	if (mediaBidMetaData != null && ObjectUtils.isNotEmpty(mediaBidMetaData.getMediaBids())) {
            TOUTIAOAds.BidRequest bidRequest = (TOUTIAOAds.BidRequest) mediaBidMetaData.getRequestObject();
            
    		TOUTIAOAds.BidResponse.Builder bidResposeBuilder = TOUTIAOAds.BidResponse.newBuilder();
            bidResposeBuilder.setRequestId(bidRequest.getRequestId());
    		
            for (MediaBid.Builder mediaBid : mediaBidMetaData.getMediaBids()) {
            	TOUTIAOAds.SeatBid.Builder seatBidBuilder = TOUTIAOAds.SeatBid.newBuilder();
                TOUTIAOAds.MaterialMeta.Builder materialMetaBuilder = TOUTIAOAds.MaterialMeta.newBuilder();
                TOUTIAOAds.MaterialMeta.ExternalMeta.Builder externalMetaBuilder = TOUTIAOAds.MaterialMeta.ExternalMeta.newBuilder();
                TOUTIAOAds.MaterialMeta.ImageMeta.Builder imageMetaBuilder = TOUTIAOAds.MaterialMeta.ImageMeta.newBuilder();
            	
            	if (mediaBid.getStatus() == Constant.StatusCode.OK) {
            		resp.setStatus(Constant.StatusCode.OK);
            		
            		MediaBidMetaData.BidMetaData bidMetaData = mediaBidMetaData.getBidMetaDataMap().get(mediaBid.getImpid());
					MediaResponse.Builder mediaResponse =mediaBid.getResponseBuilder();
            		TOUTIAOAds.Bid.Builder bidBuilder = TOUTIAOAds.Bid.newBuilder();
            		
                    bidBuilder.setId(mediaBid.getImpid()); 
                    bidBuilder.setAdid(Integer.parseInt(bidMetaData.getMaterialMetaData().getMediaQueryKey()));
                    bidBuilder.setAdslotId(bidRequest.getAdslots(0).getId());
                    bidBuilder.setPrice(mediaResponse.getPrice());

                    materialMetaBuilder.setAdType(AdType.valueOf(mediaBid.getRequestBuilder().getAdtype()));
                    imageMetaBuilder.setHeight(mediaBid.getRequestBuilder().getH());
                    imageMetaBuilder.setWidth(mediaBid.getRequestBuilder().getW());
                    imageMetaBuilder.setUrl(mediaResponse.getAdm().get(0));
                    imageMetaBuilder.setDescription(mediaResponse.getDesc());
                    
                    materialMetaBuilder.setImageBanner(imageMetaBuilder);

                    //win的竞价成功通知
                    materialMetaBuilder.setNurl(ToutiaoConstant.URL.replace("{adspaceid}", mediaBid.getRequestBuilder().getAdspacekey()));
                    //信息流落地页广告和详情页图文为必须返回
                    materialMetaBuilder.setSource(StringUtil.toString(mediaResponse.getDesc()));
                    materialMetaBuilder.setTitle(StringUtil.toString(mediaResponse.getTitle()));
                    externalMetaBuilder.setUrl(mediaResponse.getLpgurl());
                    materialMetaBuilder.setExternal(externalMetaBuilder);
                    
                    for (String clk : mediaResponse.getMonitorBuilder().getClkurl()) {
                        materialMetaBuilder.addClickUrl(clk);
                    }
                    for (com.madhouse.ssp.avro.Track imp : mediaResponse.getMonitorBuilder().getImpurl()) {
                        materialMetaBuilder.addShowUrl(imp.getUrl());
                    }
                    bidBuilder.setCreative(materialMetaBuilder.build());
                    seatBidBuilder.addAds(bidBuilder);
		 		}
            	bidResposeBuilder.addSeatbids(seatBidBuilder);
		 	}
            if(bidResposeBuilder.getSeatbidsCount() > 0){
            	return outputStreamWrite(bidResposeBuilder, resp);
            }
        }
		return false;
    }
    private boolean outputStreamWrite(TOUTIAOAds.BidResponse.Builder builder,  HttpServletResponse resp)  {
        try {
            resp.setContentType("application/octet-stream;charset=UTF-8");
            BidResponse responseBuiler = builder.build();
    		logger.info("Toutiao Response params is : {}", JsonFormat.printToString(responseBuiler));
            resp.getOutputStream().write(responseBuiler.toByteArray());
            return true;
        } catch (Exception e) {
            logger.error("Toutiao Exception:{}" ,e.toString());
            return false;
        }
    }
    
    
}
