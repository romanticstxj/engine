package com.madhouse.media.autohome;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.madhouse.cache.CacheManager;
import com.madhouse.cache.MaterialMetaData;
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

public class AutoHomeHandler extends MediaBaseHandler {

	@Override
	public boolean parseMediaRequest(HttpServletRequest req,
			MediaBidMetaData mediaBidMetaData, HttpServletResponse resp) {
		try {
			String bytes = HttpUtil.getRequestPostBytes(req);
            if (!StringUtils.isEmpty(bytes)) {
            	AutoHomeBidRequest bidRequest = JSON.parseObject(bytes, AutoHomeBidRequest.class);
            	
            	if (bidRequest == null) {
            		outputStreamWrite(resp, bidRequest,null);
            		return false;
                }
            	logger.info("AutoHome Request params is : {}", JSON.toJSONString(bidRequest));
            	
            	if (ObjectUtils.isEmpty(bidRequest.getMobile())) {
            		outputStreamWrite(resp, bidRequest,null);
                    return false;
                }
                if(!bidRequest.getMobile().isIs_app()) {
                	outputStreamWrite(resp, bidRequest,null);
                    return false;
                }
            	
                Object[] mediaBids = this.conversionToPremiumMADData(bidRequest);
                if (ObjectUtils.isEmpty(mediaBids[0])) {
                	outputStreamWrite(resp, bidRequest,null);
                    return false;
                }
                List<MediaBid.Builder> listMediaBid = (List<MediaBid.Builder>) mediaBids[0];
                if (!this.checkRequestParam(listMediaBid.get(0).getRequestBuilder())) {
                	outputStreamWrite(resp, bidRequest,null);
                    return false;
                }
                
                mediaBidMetaData.setMediaBids(listMediaBid);
            	mediaBidMetaData.setRequestObject(new Object[]{bidRequest,mediaBids[1]});
            	return true;
            } else {
                resp.setStatus(Constant.StatusCode.BAD_REQUEST);
            }
        } catch (Exception e) {
            logger.error("autohome Exception:{}" , e.toString());
            resp.setStatus(Constant.StatusCode.INTERNAL_ERROR);
        }

        return false;
	}
	

	private Object[] conversionToPremiumMADData(AutoHomeBidRequest bidRequest) {
		
		List<MediaBid.Builder> mediaBids = new LinkedList<>();
		Map<String, String> slotids = new ConcurrentHashMap<String, String>();
		
		MediaRequest.Builder mediaRequest = MediaRequest.newBuilder();

        try {
            if (ObjectUtils.isEmpty(bidRequest.getAdSlot())) {
                return null;
            }
            
            if (ObjectUtils.isEmpty(bidRequest.getMobile().getDevice())){
            	return null;
            }
            AutoHomeBidRequest.Mobile.Device device = bidRequest.getMobile().getDevice();
            
            if (StringUtils.isEmpty(device.getDeviceid())){
            	return null;
            }
            
            mediaRequest.setAdtype(2);
            
            mediaRequest.setIp(StringUtil.toString(bidRequest.getUser().getIp()));
            mediaRequest.setUa(StringUtil.toString(bidRequest.getUser().getUser_agent()));
            mediaRequest.setMake(StringUtil.toString(device.getDevicebrand()));
            mediaRequest.setModel(StringUtil.toString(device.getDevicemodel()));
            mediaRequest.setOsv(StringUtil.toString(device.getOs_version()));
            mediaRequest.setDevicetype(Constant.DeviceType.UNKNOWN);
            
            if (bidRequest.isIs_test()) {
                mediaRequest.setTest(Constant.Test.SIMULATION);
            } else {
                mediaRequest.setTest(Constant.Test.REAL);
            }
            
            
            
            mediaRequest.setCarrier(Constant.Carrier.UNKNOWN);
            if (device.getNetworkid() > 0) {
                switch (device.getNetworkid()) {
                    case AutoHomeConstant.Carrier.CHINA_MOBILE: {
                        mediaRequest.setCarrier(Constant.Carrier.CHINA_MOBILE);
                        break;
                    }

                    case AutoHomeConstant.Carrier.CHINA_UNICOM: {
                        mediaRequest.setCarrier(Constant.Carrier.CHINA_UNICOM);
                        break;
                    }

                    case AutoHomeConstant.Carrier.CHINA_TELECOM: {
                        mediaRequest.setCarrier(Constant.Carrier.CHINA_TELECOM);
                        break;
                    }
                }
            }
            mediaRequest.setConnectiontype(Constant.ConnectionType.UNKNOWN);
            if (device.getConn() > 0) {
                switch (device.getConn()) {
                    case AutoHomeConstant.ConnectionType.WIFI: {
                        mediaRequest.setConnectiontype(Constant.ConnectionType.WIFI);
                        break;
                    }
                    case AutoHomeConstant.ConnectionType._2G: {
                        mediaRequest.setConnectiontype(Constant.ConnectionType._2G);
                        break;
                    }
                    case AutoHomeConstant.ConnectionType._3G: {
                        mediaRequest.setConnectiontype(Constant.ConnectionType._3G);
                        break;
                    }

                    case AutoHomeConstant.ConnectionType._4G: {
                        mediaRequest.setConnectiontype(Constant.ConnectionType._4G);
                        break;
                    }
                }
            }
            if (device.getLat() >=0 && device.getLng() >=0) {
                Geo.Builder builder = Geo.newBuilder();
                builder.setLat(device.getLat());
                builder.setLon(device.getLng());
                mediaRequest.setGeoBuilder(builder);
            }
            if (bidRequest.getMobile() != null) {
                mediaRequest.setName("AutoHome");
                mediaRequest.setBundle(StringUtil.toString(bidRequest.getMobile().getPkgname()));
                mediaRequest.setType(Constant.MediaType.APP);
            }
            
            for(AutoHomeBidRequest.AdSlot adSlot :bidRequest.getAdSlot()){
            	MediaBid.Builder mediaBid = MediaBid.newBuilder();
                MediaRequest.Builder request = MediaRequest.newBuilder(mediaRequest);
            	
                StringBuilder adspaceKey = new StringBuilder();
                adspaceKey.append("AUTOHOME:").append(StringUtil.toString(adSlot.getSlotid()));
                request.setOs(Constant.OSType.UNKNOWN);
                String deviceId = decryptDeviceId(device.getDeviceid(), AutoHomeConstant.DEVICEIDKEY);
                
                if (device.getPm() == AutoHomeConstant.Os.ANDROID) {
                    adspaceKey.append(":ANDROID");
                    request.setOs(Constant.OSType.ANDROID);
                    request.setDid(StringUtil.toString(deviceId));
                } else if (device.getPm() == AutoHomeConstant.Os.IOS) {
                    adspaceKey.append(":IOS");
                    request.setOs(Constant.OSType.IOS);
                    
                    if (StringUtil.formatCheck("^[0-9A-F]{8}\\-[0-9A-F]{4}\\-[0-9A-F]{4}\\-[0-9A-F]{4}\\-[0-9A-F]{12}$", deviceId)) {
                    	request.setIfa(StringUtil.toString(device.getDeviceid()));
                    }else{
                    	request.setDpid(StringUtil.toString(deviceId));
                    }
                }
                
                MediaMappingMetaData mediaMappingMetaData = CacheManager.getInstance().getMediaMapping(adspaceKey.toString());
                if (mediaMappingMetaData == null) {
                    continue;
                }
                
                slotids.put(adSlot.getId(), adSlot.getSlotid());
                request.setAdspacekey(mediaMappingMetaData.getAdspaceKey());
                request.setBid(StringUtil.toString(adSlot.getId()));
                
                if (adSlot.getMin_cpm_price() != null) {
                	request.setBidfloor(adSlot.getMin_cpm_price().intValue());
                }
                
                if (adSlot.getBanner() != null) {
                	AutoHomeBidRequest.AdSlot.Banner banner = adSlot.getBanner();
                    if(banner.getWidth() != null){
                    	request.setW(banner.getWidth());
                    }
                    if(banner.getHeight() != null){
                    	request.setH(banner.getHeight());
                    }
                }
                mediaBid.setRequestBuilder(request);
                mediaBids.add(mediaBid);
            }
            return new Object[]{mediaBids,slotids};
		} catch (Exception e) {
			logger.error("autohome Exception:{}" , e.toString());
        }
		
        return null;
	}

	@Override
	public boolean packageMediaResponse(MediaBidMetaData mediaBidMetaData,
			HttpServletResponse resp) {
		AutoHomeBidResponse bidResponse= new AutoHomeBidResponse();
		Object[] objType = (Object[])mediaBidMetaData.getRequestObject();
		AutoHomeBidRequest bidRequest = (AutoHomeBidRequest) objType[0];
		Map<String,String> slotids = (Map<String, String>) objType[1];
		try {
			if (mediaBidMetaData != null && ObjectUtils.isNotEmpty(mediaBidMetaData.getMediaBids())) {
				
				bidResponse.setId(bidRequest.getId());
            	bidResponse.setVersion(bidRequest.getVersion());
            	bidResponse.setIs_cm(false);
            	bidResponse.setAds(new LinkedList<AutoHomeBidResponse.Ads>());
				
				for (MediaBid.Builder mediaBid : mediaBidMetaData.getMediaBids()) {
					if (mediaBid.getStatus() == Constant.StatusCode.OK) {
						MediaBidMetaData.BidMetaData bidMetaData = mediaBidMetaData.getBidMetaDataMap().get(mediaBid.getImpid());
						MaterialMetaData materialMetaData = bidMetaData.getMaterialMetaData();
						MediaResponse.Builder mediaResponse = mediaBid.getResponseBuilder();
						
						AutoHomeBidResponse.Ads ads= new AutoHomeBidResponse.Ads();
						ads.setId(StringUtil.toString(mediaBid.getRequestBuilder().getBid()));
						ads.setSlotid(StringUtil.toString(slotids.get(mediaBid.getRequestBuilder().getBid())));
						ads.setMax_cpm_price(bidRequest.getAdSlot().get(0).getMin_cpm_price().intValue());
						ads.setCreative_id(Long.valueOf(materialMetaData.getMediaQueryKey()));
						ads.setAdvertiser_id(materialMetaData.getAdvertiserId());
						ads.setWidth(mediaBid.getRequestBuilder().getW());
						ads.setHeight(mediaBid.getRequestBuilder().getH());
						ads.setTemplateId(100002);
						Monitor.Builder monitor = mediaResponse.getMonitorBuilder();
						
						
						if (monitor != null) {
							AutoHomeBidResponse.Ads.Adsnippet adsnippet = new AutoHomeBidResponse.Ads.Adsnippet();
		                	adsnippet.setPv(new ArrayList<String>());
		                	for (Track impurl : mediaResponse.getMonitorBuilder().getImpurl()) {
		                		adsnippet.getPv().add(impurl.getUrl());
							}
		                	if (ObjectUtils.isNotEmpty(monitor.getClkurl())) {
		                		List<String> clkUrls = monitor.getClkurl();
		                		adsnippet.setLink(StringUtil.toString(clkUrls.get(clkUrls.size()-1)));
		                		if(!StringUtils.isEmpty(mediaResponse.getLpgurl())){
		                			adsnippet.setLink(adsnippet.getLink() + "&_url=" + URLEncoder.encode(mediaResponse.getLpgurl(), "utf-8"));
								}	
							}
		                	List<AutoHomeBidResponse.Ads.Adsnippet.Content> listContent = new ArrayList<AutoHomeBidResponse.Ads.Adsnippet.Content>();
		                	AutoHomeBidResponse.Ads.Adsnippet.Content contentBimg= new AutoHomeBidResponse.Ads.Adsnippet.Content();
		                	contentBimg.setSrc(mediaResponse.getAdm().get(0));
		                	contentBimg.setType(AutoHomeConstant.ContentType.BIMG);
		                	if(!StringUtils.isEmpty(mediaResponse.getIcon())){
		                		AutoHomeBidResponse.Ads.Adsnippet.Content contentSimg= new AutoHomeBidResponse.Ads.Adsnippet.Content();
		                		contentSimg.setSrc(mediaResponse.getIcon());
		                		contentSimg.setType(AutoHomeConstant.ContentType.SIMG);
		                    	listContent.add(contentSimg);
		                	}
		                	if(!StringUtils.isEmpty(mediaResponse.getTitle())){
		                		AutoHomeBidResponse.Ads.Adsnippet.Content contentTest= new AutoHomeBidResponse.Ads.Adsnippet.Content();
		                		contentTest.setSrc(mediaResponse.getTitle());
		                		contentTest.setType(AutoHomeConstant.ContentType.TEXT);
		                    	listContent.add(contentTest);
		                	}
		                	listContent.add(contentBimg);
		                	adsnippet.setContent(listContent);
		                	ads.setAdsnippet(adsnippet); 
		                	bidResponse.getAds().add(ads);
						}
					}
				}
				if (bidResponse.getAds().size() > 0) {
                    return outputStreamWrite(resp, bidRequest , bidResponse);
                }
				
				
            }
		} catch (Exception e) {
            logger.error("autohome Exception:{}" , e.toString());
        }
        return outputStreamWrite(resp, bidRequest , null);
		
		
	}
	private boolean outputStreamWrite(HttpServletResponse resp,
			AutoHomeBidRequest bidRequest, AutoHomeBidResponse bidResponse) {
    	try {
    		if(ObjectUtils.isEmpty(bidResponse)){
    			bidResponse = new AutoHomeBidResponse();
    		}
			bidResponse.setId(bidRequest.getId());
			bidResponse.setVersion(bidRequest.getVersion());
			bidResponse.setIs_cm(false);
			
            resp.setStatus(Constant.StatusCode.OK);
            resp.setHeader("Content-Type", "application/json; charset=utf-8");
            
            String response = JSON.toJSONString(bidResponse);
            logger.info("AutoHome Response is: {}", response);
            
            resp.getOutputStream().write(response.getBytes());
            return true;
        } catch (Exception e) {
            logger.error(e.toString());
            resp.setStatus(Constant.StatusCode.INTERNAL_ERROR);
        }
        return false;
	}

	public static String decryptDeviceId(String text, String key) {
		if (StringUtils.isEmpty(text)) {
			return null;
		}
		byte[] data = StringUtil.base64Decode(text);
		int len = data.length - 4;

		byte[] md5 = DigestUtils.md5(key);
		byte[] deviceId = new byte[len];

		for (int i = 0; i < len; ++i) {
			deviceId[i] = (byte) (data[i] ^ md5[i % 16]);
		}

		byte[] md5sum = DigestUtils.md5(deviceId);
		for (int i = 0; i < 4; ++i) {
			if (md5sum[i] != data[len + i]) {
				return null;
			}
		}

		return new String(deviceId);
	}
}
