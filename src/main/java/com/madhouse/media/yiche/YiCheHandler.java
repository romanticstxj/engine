package com.madhouse.media.yiche;

import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.madhouse.cache.CacheManager;
import com.madhouse.cache.MediaBidMetaData;
import com.madhouse.cache.MediaMappingMetaData;
import com.madhouse.media.MediaBaseHandler;
import com.madhouse.ssp.Constant;
import com.madhouse.ssp.avro.Geo;
import com.madhouse.ssp.avro.MediaBid;
import com.madhouse.ssp.avro.MediaRequest;
import com.madhouse.ssp.avro.MediaResponse;
import com.madhouse.ssp.avro.Monitor;
import com.madhouse.ssp.avro.Track;
import com.madhouse.util.HttpUtil;
import com.madhouse.util.ObjectUtils;
import com.madhouse.util.StringUtil;
import com.madhouse.util.Utility;

public class YiCheHandler extends MediaBaseHandler {

	@Override
	public boolean parseMediaRequest(HttpServletRequest req,
			MediaBidMetaData mediaBidMetaData, HttpServletResponse resp) {
		try {
            String bytes = HttpUtil.getRequestPostBytes(req);
            if (!StringUtils.isEmpty(bytes)) {
            	
            	YiCheBidRequest bidRequest = JSON.parseObject(bytes, YiCheBidRequest.class);
            	
            	resp.setStatus(Constant.StatusCode.NO_CONTENT);
            	if (bidRequest == null) {
                    return false;
                }
            	logger.info("YiChe Request params is : {}", JSON.toJSONString(bidRequest));
            	
            	if (bidRequest.getApp() == null) {
                    return false;
                }
            	
            	MediaRequest.Builder mediaRequest = this.conversionToPremiumMADData(bidRequest);
            	if (mediaRequest == null) {
            		return false;
            	}

            	if (!this.checkRequestParam(mediaRequest)) {
            		return false;
            	}

            	mediaBidMetaData.getMediaBidBuilder().setRequestBuilder(mediaRequest);
            	mediaBidMetaData.setRequestObject(bidRequest);
            	return true;
            } else {
            	resp.setStatus(Constant.StatusCode.NO_CONTENT);
            }
		} catch (Exception e) {
			logger.error("yiche Exception:{}" ,e.toString());
			resp.setStatus(Constant.StatusCode.NO_CONTENT);
		}
		return false;
	}

	private MediaRequest.Builder conversionToPremiumMADData(YiCheBidRequest bidRequest) {
		MediaRequest.Builder mediaRequest = MediaRequest.newBuilder();

		try {
		
			if (ObjectUtils.isEmpty(bidRequest.getImp())) {
                return null;
            }
			
			YiCheBidRequest.Imp imp = bidRequest.getImp().get(0);
			YiCheBidRequest.Device device = bidRequest.getDevice();
			YiCheBidRequest.User user = bidRequest.getUser();
			
			if (imp.getPmp() != null) {
                if (!ObjectUtils.isEmpty(imp.getPmp().getDeals())) {
                    int size = imp.getPmp().getDeals().size();
                    mediaRequest.setDealid(StringUtil.toString(imp.getPmp().getDeals().get(Utility.nextInt(size)).getId()));
                }
            }
			StringBuilder adspaceKey = new StringBuilder();
            adspaceKey.append("YICHE:").append(StringUtil.toString(imp.getTagid()));;
			
			
            if(YiCheConstant.OsType.ANDROID == device.getOs()){
            	adspaceKey.append(":ANDROID");
                mediaRequest.setOs(Constant.OSType.ANDROID);
                mediaRequest.setDidmd5(StringUtil.toString(device.getDidmd5()));
                mediaRequest.setDid(user.getId());
            } else if (YiCheConstant.OsType.IOS == device.getOs()) {
                adspaceKey.append(":IOS");
                mediaRequest.setOs(Constant.OSType.IOS);
                mediaRequest.setDpidmd5(user.getId().toLowerCase());
            }
            MediaMappingMetaData mediaMappingMetaData = CacheManager.getInstance().getMediaMapping(adspaceKey.toString());
            if (mediaMappingMetaData == null) {
                return null;
            }
            
            
            mediaRequest.setAdtype(2);
            mediaRequest.setAdspacekey(mediaMappingMetaData.getAdspaceKey());
            mediaRequest.setBid(StringUtil.toString(bidRequest.getId()));
            mediaRequest.setIp(StringUtil.toString(device.getIp()));
            String ua = StringEscapeUtils.escapeJava(device.getUa());
            mediaRequest.setUa(StringUtils.isEmpty(ua) ? "YICHE" : ua);
            mediaRequest.setMake(StringUtil.toString(device.getMake()));
            mediaRequest.setOsv(StringUtil.toString(StringUtils.isEmpty(device.getOsv()) ? "1.0.0" : device.getOsv() ));
            mediaRequest.setMacmd5(StringUtil.toString(device.getMacmd5()));
            mediaRequest.setTest(Constant.Test.REAL);
            
            
            mediaRequest.setCarrier(Constant.Carrier.UNKNOWN);
            mediaRequest.setConnectiontype(Constant.ConnectionType.UNKNOWN);
            if (device.getConnectiontype() != null) {
                switch (device.getConnectiontype()) {
                    case YiCheConstant.ConnectionType.OTHERS: {
                        mediaRequest.setConnectiontype(Constant.ConnectionType.ETHERNET);
                        break;
                    }

                    case YiCheConstant.ConnectionType.WIFI: {
                        mediaRequest.setConnectiontype(Constant.ConnectionType.WIFI);
                        break;
                    }

                    case YiCheConstant.ConnectionType._2G: {
                        mediaRequest.setConnectiontype(Constant.ConnectionType._2G);
                        break;
                    }

                    case YiCheConstant.ConnectionType._3G: {
                        mediaRequest.setConnectiontype(Constant.ConnectionType._3G);
                        break;
                    }

                    case YiCheConstant.ConnectionType._4G: {
                        mediaRequest.setConnectiontype(Constant.ConnectionType._4G);
                        break;
                    }
                }
            }
            mediaRequest.setDevicetype(Constant.DeviceType.UNKNOWN);
            if (device.getDevicetype() != null) {
                switch (device.getDevicetype()) {

                    case YiCheConstant.DeviceType.PHONE: {
                        mediaRequest.setDevicetype(Constant.DeviceType.PHONE);
                        break;
                    }

                    case YiCheConstant.DeviceType.PAD: {
                        mediaRequest.setDevicetype(Constant.DeviceType.PAD);
                        break;
                    }
                }
            }
            
            
            mediaRequest.setType(Constant.MediaType.APP);
            mediaRequest.setName("yiche");
            mediaRequest.setBundle("com.yiche.yicheadx");
            Geo.Builder builder = Geo.newBuilder();
            builder.setLat(0);
            builder.setLon(0);
            mediaRequest.setGeoBuilder(builder);
            
            if (imp.getBanner() != null) {
                YiCheBidRequest.Imp.Banner banner = imp.getBanner();
                if(banner.getW() != null){
                	mediaRequest.setW(banner.getW());
                }
                if(banner.getH() != null){
                	mediaRequest.setH(banner.getH());
                }
            }
			
            return mediaRequest;
        } catch (Exception e) {
            logger.error("yiche Exception:{}" ,e.toString());
        }

        return null;
	}

	@Override
	public boolean packageMediaResponse(MediaBidMetaData mediaBidMetaData,
			HttpServletResponse resp) {
		try {
			 if (mediaBidMetaData != null && mediaBidMetaData.getMediaBidBuilder() != null && mediaBidMetaData.getMaterialMetaData() != null) {
				 if (mediaBidMetaData.getMediaBidBuilder().getStatus() == Constant.StatusCode.OK) {
					 MediaBid.Builder mediaBid = mediaBidMetaData.getMediaBidBuilder();
					 MediaResponse.Builder mediaResponse = mediaBid.getResponseBuilder();
					 YiCheBidRequest bidRequest = (YiCheBidRequest)mediaBidMetaData.getRequestObject();
					 
					 YiCheBidResponse bidResponse = new YiCheBidResponse();
					 bidResponse.setId(bidRequest.getId());
					 bidResponse.setBidid(mediaBid.getImpid());
					 
					 YiCheBidResponse.SeatBid.Bid bid = new YiCheBidResponse.SeatBid.Bid();
					 
					 bid.setId(mediaBid.getImpid());
					 bid.setImpid(StringUtil.toString(bidRequest.getImp().get(0).getId()));
					 bid.setAdid(StringUtil.toString(mediaBidMetaData.getMaterialMetaData().getMediaQueryKey()));
					 bid.setDealid(StringUtil.toString(mediaBid.getRequestBuilder().getDealid()));
					 
					 Monitor.Builder monitor = mediaResponse.getMonitorBuilder();
					 if (monitor != null) {
						 if (ObjectUtils.isNotEmpty(monitor.getImpurl())) {
							 bid.setDurl(new LinkedList<>());
							 List<Track> listTrack = monitor.getImpurl();
							 bid.getDurl().add(monitor.getImpurl().get(0).getUrl());
							 bid.getDurl().add(monitor.getImpurl().get(listTrack.size()-1).getUrl());
						 }
						 if (ObjectUtils.isNotEmpty(monitor.getClkurl())) {
							 List<String> clkUrls = monitor.getClkurl();
							 bid.setCurl(StringUtil.toString(clkUrls.get(clkUrls.size()-1) + "&_url=" +mediaResponse.getLpgurl()));
						 }
					 }
					 
					 YiCheBidResponse.SeatBid seatBid = new YiCheBidResponse.SeatBid();
					 bidResponse.setSeatbid(new LinkedList<>());
					 
					 seatBid.setBid(new LinkedList<>());
					 seatBid.getBid().add(bid);
					 
					 bidResponse.getSeatbid().add(seatBid);
					 return outputStreamWrite(resp, bidResponse);
					 
				 }
			 }
		} catch (Exception e) {
			logger.error("yiche Exception:{}" ,e.toString());
		}
		
		 return outputStreamWrite(resp,null);
	}

	private boolean outputStreamWrite(HttpServletResponse resp, YiCheBidResponse bidResponse) {
		try {
            if (bidResponse != null) {
                resp.setStatus(Constant.StatusCode.OK);
                resp.setHeader("Content-Type", "application/json; charset=utf-8");

                String response = JSON.toJSONString(bidResponse);
                logger.info("yiche Response is: {}", response);

                resp.getOutputStream().write(response.getBytes());
                return true;
            } else {
                resp.setStatus(Constant.StatusCode.NO_CONTENT);
            }
        } catch (Exception e) {
            logger.error("yiche Exception:{}" , e.toString());
            resp.setStatus(Constant.StatusCode.NO_CONTENT);
        }
		
        return false;
	}

}
