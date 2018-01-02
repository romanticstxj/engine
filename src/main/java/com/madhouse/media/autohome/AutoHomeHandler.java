package com.madhouse.media.autohome;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.madhouse.cache.CacheManager;
import com.madhouse.cache.MediaBidMetaData;
import com.madhouse.cache.MediaMappingMetaData;
import com.madhouse.media.MediaBaseHandler;
import com.madhouse.ssp.Constant;
import com.madhouse.ssp.avro.Geo;
import com.madhouse.ssp.avro.MediaRequest;
import com.madhouse.ssp.avro.MediaResponse;
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
            		return false;
                }
            	logger.info("AutoHome Request params is : {}", JSON.toJSONString(bidRequest));
            	
            	if (ObjectUtils.isEmpty(bidRequest.getMobile())) {
                    return false;
                }
                if(!bidRequest.getMobile().isIs_app()) {
                    return false;
                }
            	
            	MediaRequest.Builder mediaRequest = this.conversionToPremiumMADData(bidRequest);
                if (mediaRequest == null) {
                    outputStreamWrite(resp, bidRequest,null);
                    return false;
                }
                if (!this.checkRequestParam(mediaRequest)) {
                    outputStreamWrite(resp, bidRequest,null);
                    return false;
                }
                mediaBidMetaData.getMediaBidBuilder().setRequestBuilder(mediaRequest);
                mediaBidMetaData.setRequestObject(bidRequest);
                return true;
            } else {
                resp.setStatus(Constant.StatusCode.BAD_REQUEST);
            }
        } catch (Exception e) {
            logger.error(e.toString());
            resp.setStatus(Constant.StatusCode.INTERNAL_ERROR);
        }

        return false;
	}
	

	private MediaRequest.Builder conversionToPremiumMADData(AutoHomeBidRequest bidRequest) {
		
		MediaRequest.Builder mediaRequest = MediaRequest.newBuilder();

        try {
            if (ObjectUtils.isEmpty(bidRequest.getAdSlot())) {
                return null;
            }
            AutoHomeBidRequest.AdSlot adSlot= bidRequest.getAdSlot().get(0);
            if (ObjectUtils.isEmpty(bidRequest.getMobile().getDevice())){
            	return null;
            }
            AutoHomeBidRequest.Mobile.Device device = bidRequest.getMobile().getDevice();
            
            if (StringUtils.isEmpty(device.getDeviceid())){
            	return null;
            }
            StringBuilder adspaceKey = new StringBuilder();
            adspaceKey.append("AUTOHOME:").append(StringUtil.toString(adSlot.getSlotid()));
            mediaRequest.setOs(Constant.OSType.UNKNOWN);
            String deviceId = decryptDeviceId(device.getDeviceid(), AutoHomeConstant.DEVICEIDKEY);
            
            if (device.getPm() == AutoHomeConstant.Os.ANDROID) {
                adspaceKey.append(":ANDROID");
                mediaRequest.setOs(Constant.OSType.ANDROID);
                mediaRequest.setDid(StringUtil.toString(deviceId));
            } else if (device.getPm() == AutoHomeConstant.Os.IOS) {
                adspaceKey.append(":IOS");
                mediaRequest.setOs(Constant.OSType.IOS);
                
                if (StringUtil.formatCheck("^[0-9A-F]{8}\\-[0-9A-F]{4}\\-[0-9A-F]{4}\\-[0-9A-F]{4}\\-[0-9A-F]{12}$", deviceId)) {
                	mediaRequest.setIfa(StringUtil.toString(device.getDeviceid()));
                }else{
                	mediaRequest.setDpid(StringUtil.toString(deviceId));
                }
            }
            
            MediaMappingMetaData mediaMappingMetaData = CacheManager.getInstance().getMediaMapping(adspaceKey.toString());
            if (mediaMappingMetaData == null) {
                return null;
            }
            mediaRequest.setAdtype(2);
            mediaRequest.setAdspacekey(mediaMappingMetaData.getAdspaceKey());
            mediaRequest.setBid(StringUtil.toString(bidRequest.getId()));
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
            
            if (adSlot.getMin_cpm_price() != null) {
                mediaRequest.setBidfloor(adSlot.getMin_cpm_price().intValue());
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
            if (adSlot.getBanner() != null) {
            	AutoHomeBidRequest.AdSlot.Banner banner = adSlot.getBanner();
                if(banner.getWidth() != null){
                	mediaRequest.setW(banner.getWidth());
                }
                if(banner.getHeight() != null){
                	mediaRequest.setH(banner.getHeight());
                }
            } 
            return mediaRequest;
		} catch (Exception e) {
			logger.error(e.toString());
        }
		
        return null;
	}

	@Override
	public boolean packageMediaResponse(MediaBidMetaData mediaBidMetaData,
			HttpServletResponse resp) {
		AutoHomeBidResponse bidResponse= new AutoHomeBidResponse();
		AutoHomeBidRequest bidRequest = (AutoHomeBidRequest) mediaBidMetaData.getRequestObject();
		try {
            if (mediaBidMetaData != null && mediaBidMetaData.getMediaBidBuilder() != null && mediaBidMetaData.getMaterialMetaData() != null) {
                if (mediaBidMetaData.getMediaBidBuilder().getStatus() == Constant.StatusCode.OK) {
                    MediaResponse.Builder mediaResponse = mediaBidMetaData.getMediaBidBuilder().getResponseBuilder();
                	
                	bidResponse.setId(bidRequest.getId());
                	bidResponse.setVersion(bidRequest.getVersion());
                	bidResponse.setIs_cm(false);
                	bidResponse.setAds(new ArrayList<AutoHomeBidResponse.Ads>());
                	
                	
                	AutoHomeBidResponse.Ads ads= new AutoHomeBidResponse.Ads();
                	ads.setId(bidRequest.getAdSlot().get(0).getId());
                	ads.setSlotid(bidRequest.getAdSlot().get(0).getSlotid());
                	ads.setMax_cpm_price(bidRequest.getAdSlot().get(0).getMin_cpm_price().intValue());
                	ads.setCreative_id(Long.valueOf(mediaBidMetaData.getMaterialMetaData().getMediaQueryKey()));
                	ads.setAdvertiser_id(mediaBidMetaData.getMaterialMetaData().getAdvertiserId());
                	ads.setWidth(mediaBidMetaData.getMediaBidBuilder().getRequestBuilder().getW());
                	ads.setHeight(mediaBidMetaData.getMediaBidBuilder().getRequestBuilder().getH());
                	ads.setTemplateId(100002);
                	
                	
                	AutoHomeBidResponse.Ads.Adsnippet adsnippet = new AutoHomeBidResponse.Ads.Adsnippet();
                	adsnippet.setPv(new ArrayList<String>());
                	for (Track impurl : mediaResponse.getMonitorBuilder().getImpurl()) {
                		adsnippet.getPv().add(impurl.getUrl());
					}
                	List<String> listClkurl = mediaResponse.getMonitorBuilder().getClkurl();
                	adsnippet.setLink(StringUtil.toString(listClkurl.get(listClkurl.size()-1)+"&_url=" + URLEncoder.encode(mediaResponse.getLpgurl(), "utf-8")));
                	
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
		} catch (Exception e) {
            logger.error(e.toString());
        }
        return outputStreamWrite(resp, bidRequest , bidResponse);
		
		
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
		  if(StringUtils.isEmpty(text)){
			  return null;
		  }
		  byte[] data = StringUtil.base64Decode(text);
		  int len = data.length - 4;
			
		  byte[] md5 = DigestUtils.md5(key);
		  byte[] deviceId = new byte[len];
			
		  for (int i = 0; i < len; ++i) {
			  deviceId[i] = (byte)(data[i] ^ md5[i % 16]);
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
