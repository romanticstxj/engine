package com.madhouse.media.toutiao;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.googlecode.protobuf.format.JsonFormat;
import com.madhouse.cache.CacheManager;
import com.madhouse.cache.MediaBidMetaData;
import com.madhouse.cache.MediaMappingMetaData;
import com.madhouse.media.MediaBaseHandler;
import com.madhouse.media.baidu.Baidu;
import com.madhouse.media.fengxing.FXBidRequest;
import com.madhouse.media.toutiao.TOUTIAOAds.AdSlot.Banner;
import com.madhouse.media.toutiao.TOUTIAOAds.AdType;
import com.madhouse.media.toutiao.TOUTIAOAds.App;
import com.madhouse.media.toutiao.TOUTIAOAds.BidRequest;
import com.madhouse.media.toutiao.TOUTIAOAds.BidResponse;
import com.madhouse.media.toutiao.TOUTIAOAds.SeatBid;
import com.madhouse.ssp.Constant;
import com.madhouse.ssp.avro.Geo;
import com.madhouse.ssp.avro.MediaBid;
import com.madhouse.ssp.avro.MediaRequest;
import com.madhouse.ssp.avro.MediaRequest.Builder;
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
            
            TOUTIAOAds.BidResponse.Builder builder=convertToutiaoResponse(bidRequest, mediaBidMetaData.getMediaBidBuilder().getRequestBuilder(), mediaBidMetaData, Constant.StatusCode.BAD_REQUEST);
            
            MediaRequest.Builder mediaRequest = conversionToPremiumMADDataModel(bidRequest);
            
            if (mediaRequest == null) {
            	outputStreamWrite(builder, resp);
                return false;
            }

            if (!this.checkRequestParam(mediaRequest)) {
            	outputStreamWrite(builder, resp);
                return false;
            }

            mediaBidMetaData.getMediaBidBuilder().setRequestBuilder(mediaRequest);
            mediaBidMetaData.setRequestObject(bidRequest);
            return true;
      
        } catch (Exception e) {
        	logger.error(e.toString());
        	resp.setStatus(Constant.StatusCode.INTERNAL_ERROR);
        }
        return false;
        
            
    }
    private MediaRequest.Builder conversionToPremiumMADDataModel(BidRequest bidRequest) {
        MediaRequest.Builder mediaRequest = MediaRequest.newBuilder();
        
        try {
        	if (ObjectUtils.isEmpty(bidRequest.getAdslots(0))) {
                return null;
            }
        	TOUTIAOAds.AdSlot adSlot = bidRequest.getAdslots(0);
        	TOUTIAOAds.Device device = bidRequest.getDevice();
        	
        	
        	if (adSlot.getPmp() != null) {
                if (!ObjectUtils.isEmpty(adSlot.getPmp().getDealsList())) {
                    int size = adSlot.getPmp().getDealsCount();
                    mediaRequest.setDealid(String.valueOf((adSlot.getPmp().getDeals(Utility.nextInt(size)).getId())));
                }
            }
        	
        	StringBuilder adspaceKey = new StringBuilder();
            adspaceKey.append("TT:").append(getAdType(bidRequest).getNumber()).append(":").append(adSlot.getChannelId());
            
            if (ToutiaoConstant.OSType.ANDROID.equalsIgnoreCase(device.getOs())){
            	adspaceKey.append(ToutiaoConstant.OSType.ANDROID);
                mediaRequest.setOs(Constant.OSType.ANDROID);
                //mediaRequest.setDid(device.getDeviceId());
                mediaRequest.setDidmd5(device.getDeviceIdMd5());
                mediaRequest.setDpid(device.getAndroidId());
                mediaRequest.setDpid(device.getAndroidIdMd5());
            } else if (ToutiaoConstant.OSType.IOS.equalsIgnoreCase(device.getOs())){
            	adspaceKey.append(ToutiaoConstant.OSType.IOS);
                mediaRequest.setOs(Constant.OSType.IOS);
                mediaRequest.setIfa(device.getDeviceId());
            }
            MediaMappingMetaData mediaMappingMetaData = CacheManager.getInstance().getMediaMapping(adspaceKey.toString());
            if (mediaMappingMetaData == null) {
                return null;
            }
            mediaRequest.setAdtype(2);
            mediaRequest.setAdspacekey(mediaMappingMetaData.getAdspaceKey());
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
            if (adSlot.getBanner(0) != null) {
                Banner banner = adSlot.getBanner(0);
                if(banner.hasWidth()){
                	mediaRequest.setW(banner.getWidth());
                }
                if(banner.hasHeight()){
                	mediaRequest.setH(banner.getHeight());
                }
            }
            
            return mediaRequest; 
            
		} catch (Exception e) {
			 logger.error(e.toString());
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
        TOUTIAOAds.BidResponse.Builder builder = TOUTIAOAds.BidResponse.newBuilder();
        TOUTIAOAds.BidRequest bidRequest = (TOUTIAOAds.BidRequest) mediaBidMetaData.getRequestObject();
        if (mediaBidMetaData != null && mediaBidMetaData.getMediaBidBuilder() != null) {
            MediaBid.Builder mediaBid = mediaBidMetaData.getMediaBidBuilder();
            try {
                if (mediaBid.getResponseBuilder() != null && mediaBid.getStatus() == Constant.StatusCode.OK) {
                    resp.setStatus(Constant.StatusCode.OK);
                    builder=convertToutiaoResponse(bidRequest, mediaBidMetaData.getMediaBidBuilder().getRequestBuilder(), mediaBidMetaData, Constant.StatusCode.OK);
                } else if(mediaBid.getStatus() == Constant.StatusCode.NO_CONTENT) {
                    resp.setStatus(Constant.StatusCode.NO_CONTENT);
                    builder=convertToutiaoResponse(bidRequest, mediaBidMetaData.getMediaBidBuilder().getRequestBuilder(), mediaBidMetaData, Constant.StatusCode.NO_CONTENT);
                }else {
                    resp.setStatus(Constant.StatusCode.BAD_REQUEST);
                    builder=convertToutiaoResponse(bidRequest, mediaBidMetaData.getMediaBidBuilder().getRequestBuilder(), mediaBidMetaData, Constant.StatusCode.BAD_REQUEST);
                }
            } catch (Exception e) {
                logger.error(e.toString() + "_Status_" + Constant.StatusCode.BAD_REQUEST);
                return outputStreamWrite(builder, resp);
            }
        }
        return outputStreamWrite(builder, resp);
        
    }
    private boolean outputStreamWrite(TOUTIAOAds.BidResponse.Builder builder, HttpServletResponse resp)  {
        try {
            resp.setContentType("application/octet-stream;charset=UTF-8");
            BidResponse responseBuiler = builder.build();
    		logger.info("Baidu.BidResponse Response params is : {}", JsonFormat.printToString(responseBuiler));
            resp.getOutputStream().write(responseBuiler.toByteArray());
            return true;
        } catch (Exception e) {
            logger.error(e.toString() + "_Status_" + Constant.StatusCode.NO_CONTENT);
            return false;
        }
    }
    private TOUTIAOAds.BidResponse.Builder convertToutiaoResponse(TOUTIAOAds.BidRequest bidRequest,Builder builder,MediaBidMetaData mediaBidMetaData,int code) {
        TOUTIAOAds.BidResponse.Builder bidResposeBuilder = TOUTIAOAds.BidResponse.newBuilder();
        bidResposeBuilder.setRequestId(bidRequest.getRequestId());
        if(Constant.StatusCode.OK == code){
            bidResposeBuilder.addSeatbids(getSeatBid(bidRequest, builder, mediaBidMetaData));
        } else {
            bidResposeBuilder.setErrorCode(code);
        }
        return bidResposeBuilder;
    }
    private SeatBid getSeatBid(BidRequest bidRequest, Builder builder,MediaBidMetaData mediaBidMetaData) {
        TOUTIAOAds.SeatBid.Builder seatBidBuilder = TOUTIAOAds.SeatBid.newBuilder();
        TOUTIAOAds.MaterialMeta.Builder materialMetaBuilder = TOUTIAOAds.MaterialMeta.newBuilder();
        TOUTIAOAds.MaterialMeta.ExternalMeta.Builder externalMetaBuilder = TOUTIAOAds.MaterialMeta.ExternalMeta.newBuilder();
        TOUTIAOAds.MaterialMeta.ImageMeta.Builder imageMetaBuilder = TOUTIAOAds.MaterialMeta.ImageMeta.newBuilder();
        MediaResponse.Builder mediaResponse = mediaBidMetaData.getMediaBidBuilder().getResponseBuilder();
        TOUTIAOAds.Bid.Builder bidBuilder = TOUTIAOAds.Bid.newBuilder();
        bidBuilder.setId(mediaBidMetaData.getMediaBidBuilder().getImpid()); 
        bidBuilder.setAdid(Integer.parseInt(mediaBidMetaData.getMaterialMetaData().getMediaQueryKey()));
        bidBuilder.setAdslotId(bidRequest.getAdslots(0).getId());
        bidBuilder.setPrice(mediaResponse.getPrice());
        
        for (AdType adtype : bidRequest.getAdslots(0).getAdTypeList()) {
            if(builder.getAdtype() == adtype.getNumber()){
                materialMetaBuilder.setAdType(adtype);
            }
        }
        imageMetaBuilder.setHeight(builder.getH());
        imageMetaBuilder.setWidth(builder.getW());
        imageMetaBuilder.setUrl(mediaResponse.getAdm().get(0));
        imageMetaBuilder.setDescription(mediaResponse.getDesc());
        
        materialMetaBuilder.setImageBanner(imageMetaBuilder);

        //win的竞价成功通知
        materialMetaBuilder.setNurl(ToutiaoConstant.URL.replace("{adspaceid}", builder.getAdspacekey()));
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
        return seatBidBuilder.build();
    }
    
}
