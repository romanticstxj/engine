package com.madhouse.media.baidu;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import com.googlecode.protobuf.format.JsonFormat;
import com.madhouse.cache.CacheManager;
import com.madhouse.cache.MediaBidMetaData;
import com.madhouse.cache.MediaMappingMetaData;
import com.madhouse.media.MediaBaseHandler;
import com.madhouse.media.baidu.Baidu.BidRequest;
import com.madhouse.media.baidu.Baidu.BidRequest.Device;
import com.madhouse.media.baidu.Baidu.BidRequest.Imp;
import com.madhouse.media.baidu.Baidu.BidRequest.Imp.Native.Asset;
import com.madhouse.media.baidu.Baidu.BidResponse.SeatBid.Bid.AdActionType;
import com.madhouse.ssp.Constant;
import com.madhouse.ssp.avro.Geo;
import com.madhouse.ssp.avro.MediaBid;
import com.madhouse.ssp.avro.MediaRequest;
import com.madhouse.ssp.avro.MediaRequest.Builder;
import com.madhouse.ssp.avro.MediaResponse;
import com.madhouse.ssp.avro.Track;
import com.madhouse.util.ObjectUtils;
import com.madhouse.util.StringUtil;
import com.madhouse.util.Utility;

public class BaiduHandler extends MediaBaseHandler {

	@Override
	public boolean parseMediaRequest(HttpServletRequest req,
			MediaBidMetaData mediaBidMetaData, HttpServletResponse resp) {
		try {
			Baidu.BidRequest bidRequest = Baidu.BidRequest.parseFrom(IOUtils.toByteArray(req.getInputStream()));
			logger.info("Baidu Request params is : {}",JsonFormat.printToString(bidRequest));
            mediaBidMetaData.setRequestObject(bidRequest);
            int status =  validateRequiredParam(bidRequest);
            if (Constant.StatusCode.OK == status) {
                MediaRequest.Builder mediaRequest = conversionToPremiumMADDataModel(bidRequest);
                if (mediaRequest != null) {
                    mediaBidMetaData.getMediaBidBuilder().setRequestBuilder(mediaRequest);
                    return true;
                }
            }
            resp.setStatus(Constant.StatusCode.NO_CONTENT);
        } catch (Exception e) {
            logger.error(e.toString());
            resp.setStatus(Constant.StatusCode.NO_CONTENT);
        }
        return false;
	}

	private Builder conversionToPremiumMADDataModel(BidRequest bidRequest) {
		MediaRequest.Builder mediaRequest = MediaRequest.newBuilder();
		
		
		Baidu.BidRequest.Device device = bidRequest.getDevice();
		Baidu.BidRequest.Imp imp = bidRequest.getImpList().get(0);
		Baidu.BidRequest.App app = bidRequest.getApp();
		Baidu.BidRequest.Imp.Pmp pmp = imp.getPmp();
		
		
		//广告请求流水号
        mediaRequest.setBid(bidRequest.getId());
        //应用程序名称
        mediaRequest.setName(StringUtil.toString(app.getName()));
        mediaRequest.setBundle(StringUtil.toString(app.getBundle()));
        mediaRequest.setAdtype(2);
        mediaRequest.setMake(StringUtil.toString(device.getMake()));
        mediaRequest.setModel(StringUtil.toString(device.getModel()));
        mediaRequest.setOsv(StringUtil.toString(device.getOsv()));
        if(device.hasIp()){
            mediaRequest.setIp(device.getIp());
        }
        if(device.hasUa()){
        	mediaRequest.setUa(device.getUa());
        }
        mediaRequest.setType(bidRequest.hasSite()? Constant.MediaType.SITE : Constant.MediaType.APP);
        
        mediaRequest.setMac(!StringUtils.isEmpty(device.getMac()) ? device.getMac() : 
        	!StringUtils.isEmpty(device.getMacmd5()) ? device.getMacmd5() : 
        		!StringUtils.isEmpty(device.getMacsha1()) ? device.getMacsha1() : "");
        
        if(pmp!=null && pmp.getDealsList().size() >0 ){
        	int size = Utility.nextInt(pmp.getDealsList().size());
        	if(pmp.getDealsList().get(size).hasBidfloor()){
        		mediaRequest.setBidfloor((int)pmp.getDealsList().get(size).getBidfloor());
        	}
        	if(pmp.getDealsList().get(size).hasId()){
        		mediaRequest.setDealid(StringUtil.toString(pmp.getDealsList().get(size).getId()));
        	}
        }
        
        mediaRequest.setConnectiontype(Constant.ConnectionType.UNKNOWN);
        if(device.hasConnectiontype()){
	        switch (device.getConnectiontype()) {
				case CONNECTION_UNKNOWN:
					mediaRequest.setConnectiontype(Constant.ConnectionType.UNKNOWN);
					break;
				case ETHERNET:
					mediaRequest.setConnectiontype(Constant.ConnectionType.ETHERNET);	
					break;
				case WIFI:
					mediaRequest.setConnectiontype(Constant.ConnectionType.WIFI);
					break;
				case CELL_UNKNOWN:
					mediaRequest.setConnectiontype(Constant.ConnectionType.CELL);
					break;
				case CELL_2G:
					mediaRequest.setConnectiontype(Constant.ConnectionType._2G);
					break;
				case CELL_3G:
					mediaRequest.setConnectiontype(Constant.ConnectionType._3G);
					break;
				case CELL_4G:
					mediaRequest.setConnectiontype(Constant.ConnectionType._4G);
					break;
			}
        }
        mediaRequest.setCarrier(Constant.Carrier.UNKNOWN);
        if(device.hasCarrier()){
        	switch (device.getCarrier()) {
				case BaiduStatusCode.Carrier.CHINA_MOBILE:
					mediaRequest.setCarrier(Constant.Carrier.CHINA_MOBILE);
					break;
				case BaiduStatusCode.Carrier.CHINA_TELECOM:
					mediaRequest.setCarrier(Constant.Carrier.CHINA_TELECOM);
					break;
				case BaiduStatusCode.Carrier.CHINA_UNICOM:
					mediaRequest.setCarrier(Constant.Carrier.CHINA_UNICOM);
					break;
        	}
        }
        mediaRequest.setDevicetype(Constant.DeviceType.UNKNOWN);
        if(device.hasDevicetype()){
        	switch (device.getDevicetype()) {
				case MOBILE:
				case PHONE:
					mediaRequest.setDevicetype(Constant.DeviceType.PHONE);
					break;
				case PERSONAL_COMPUTER:
					mediaRequest.setDevicetype(Constant.DeviceType.COMPUTER);
					break;
				case CONNECTED_TV:
					mediaRequest.setDevicetype(Constant.DeviceType.TV);
					break;
				case SET_TOP_BOX:
					mediaRequest.setDevicetype(Constant.DeviceType.BOX);
					break;
        	}
    	}
        if(device.hasGeo()){
    		Geo.Builder builder = Geo.newBuilder();
    		Baidu.BidRequest.Geo geo  =device.getGeo();
            builder.setLat((float)geo.getLat());
            builder.setLon((float)geo.getLon());
            mediaRequest.setGeoBuilder(builder);
        }
        StringBuilder adspaceKey = new StringBuilder();
        adspaceKey.append("BAIDUADX:").append(imp.getId()).append(":");
        String os = device.getOs();
        if(os.equalsIgnoreCase(BaiduStatusCode.OSType.IOS)){
        	adspaceKey.append(BaiduStatusCode.OSType.IOS);
        	String ifa = !StringUtils.isEmpty(device.getIdfa()) ? device.getIdfa() : 
				 !StringUtils.isEmpty(device.getIdfamd5()) ? device.getIdfamd5() : 
				 !StringUtils.isEmpty(device.getIdfasha1()) ? device.getIdfasha1() : "";
        	mediaRequest.setIfa(ifa);
        	mediaRequest.setOs(Constant.OSType.IOS);
        } else if (os.equalsIgnoreCase(BaiduStatusCode.OSType.ANDROID)){
        	adspaceKey.append(BaiduStatusCode.OSType.ANDROID);
        	mediaRequest.setDid(StringUtil.toString(device.getDid()));
        	mediaRequest.setDidmd5(!StringUtils.isEmpty(device.getDidmd5()) ? device.getDidmd5() : 
				 !StringUtils.isEmpty(device.getDidsha1()) ? device.getDidsha1() : "");
        	
        	mediaRequest.setDpid(StringUtil.toString(device.getDpid()));
        	mediaRequest.setDpidmd5(!StringUtils.isEmpty(device.getDpidmd5()) ? device.getDpidmd5() : 
				 !StringUtils.isEmpty(device.getDpidsha1()) ? device.getDpidsha1() : "");
        	mediaRequest.setOs(Constant.OSType.ANDROID);
        }
        if(imp.hasBanner() && imp.getBanner().hasW() && imp.getBanner().hasH()){
        	mediaRequest.setW(imp.getBanner().getW());
        	mediaRequest.setH(imp.getBanner().getH());
        }else if(imp.hasVideo() && imp.getVideo().hasW() && imp.getVideo().hasH()){
			mediaRequest.setW(imp.getVideo().getW());
			mediaRequest.setH(imp.getVideo().getH());
        }else if(imp.hasNative()){
        	for (Asset asset : imp.getNative().getAssetsList()) {
        		if(asset.hasImg()){
        			mediaRequest.setW(asset.getImg().getW());
        			mediaRequest.setH(asset.getImg().getH());
        		}
			}
        }
        MediaMappingMetaData mediaMappingMetaData = CacheManager.getInstance().getMediaMapping(adspaceKey.toString());
        if (mediaMappingMetaData == null) {
            return null;
        }
        mediaRequest.setAdspacekey(mediaMappingMetaData.getAdspaceKey());
		return mediaRequest;
	}

	private int validateRequiredParam(BidRequest bidRequest) {
		 if (ObjectUtils.isNotEmpty(bidRequest)) {
			 String id = bidRequest.getId();
			 if (StringUtils.isEmpty(id)) {
                logger.warn("Baidu.bidRequest.id is missing");
                return Constant.StatusCode.BAD_REQUEST;
			 }
			 Imp imp = bidRequest.getImp(0);
			 if (ObjectUtils.isEmpty(imp)) {
                logger.warn("[{}],Baidu.bidRequest.imp is missing",id);
                return Constant.StatusCode.BAD_REQUEST;
			 }
			 String impId = imp.getId();
			 if (StringUtils.isEmpty(impId)) {
                logger.warn("[{}],Baidu.bidRequest.impId is missing",id);
                return Constant.StatusCode.BAD_REQUEST;
			 }
			 if (!bidRequest.hasApp()) {
	            logger.warn("[{}],Baidu.bidRequest.App is missing",id);
	            return Constant.StatusCode.BAD_REQUEST;
			 }
			 if (StringUtils.isEmpty(bidRequest.getApp().getName())) {
	            logger.warn("[{}],Baidu.bidRequest.App.Name is missing",id);
	            return Constant.StatusCode.BAD_REQUEST;
			 }
			 if (StringUtils.isEmpty(bidRequest.getApp().getBundle())) {
	            logger.warn("[{}],Baidu.bidRequest.App.Bundle is missing",id);
	            return Constant.StatusCode.BAD_REQUEST;
			 }
			 if (!bidRequest.hasDevice()) {
	            logger.warn("[{}],Baidu.bidRequest.Device is missing",id);
            	return Constant.StatusCode.BAD_REQUEST;
			 }
			 Device device = bidRequest.getDevice();
			 String os = device.getOs();
			 if (StringUtils.isEmpty(os)) {
	            logger.warn("[{}],Baidu.bidRequest.Device.os is missing",id);
	            return Constant.StatusCode.BAD_REQUEST;
			 }
			 if (!device.hasOsv()) {
                logger.warn("[{}],osv is missing.", id);
                return Constant.StatusCode.BAD_REQUEST;
			 }
			 if (!device.hasUa()) {
                logger.warn("[{}],ua is missing.", id);
                return Constant.StatusCode.BAD_REQUEST;
			 }
			 if (!device.hasIp()) {
                logger.warn("[{}],ip is missing.", id);
                return Constant.StatusCode.BAD_REQUEST;
			 }
			 if(os.equalsIgnoreCase(BaiduStatusCode.OSType.IOS)){
				 String ifa = !StringUtils.isEmpty(device.getIdfa()) ? device.getIdfa() : 
					 !StringUtils.isEmpty(device.getIdfamd5()) ? device.getIdfamd5() : 
					 !StringUtils.isEmpty(device.getIdfasha1()) ? device.getIdfasha1() : "";
				 if(StringUtils.isEmpty(ifa)){
						 logger.warn("{},Baidu.bidRequest.Device.os.IOS.ifa or aid is missing",id);
						 return Constant.StatusCode.BAD_REQUEST;
				 }	 
			 } else if(os.equalsIgnoreCase(BaiduStatusCode.OSType.ANDROID)){
				 String imei = !StringUtils.isEmpty(device.getDid()) ? device.getDid() : 
					 !StringUtils.isEmpty(device.getDidmd5()) ? device.getDidmd5() : 
					 !StringUtils.isEmpty(device.getDidsha1()) ? device.getDidsha1() : "" ;
				 String dpid = !StringUtils.isEmpty(device.getDpid()) ? device.getDpid() : 
					 !StringUtils.isEmpty(device.getDpidmd5()) ? device.getDpidmd5() : 
					 !StringUtils.isEmpty(device.getDpidsha1()) ? device.getDpidsha1() : "";
				 if(StringUtils.isEmpty(imei) && StringUtils.isEmpty(dpid)){
					 logger.warn("{},Baidu.bidRequest.Device.os.ANDROID.ifa or aid is missing",id);
					 return Constant.StatusCode.BAD_REQUEST;
				 }
			 }
			 return Constant.StatusCode.OK;
		 }
		 return Constant.StatusCode.BAD_REQUEST;
	}

	
	@Override
	public boolean packageMediaResponse(MediaBidMetaData mediaBidMetaData,
			HttpServletResponse resp) {
		try {
            if (mediaBidMetaData != null && mediaBidMetaData.getMediaBidBuilder() != null && mediaBidMetaData.getPlcmtMetaData() != null) {
                if (mediaBidMetaData.getMediaBidBuilder().getStatus() == Constant.StatusCode.OK) {
                	Baidu.BidResponse.Builder bidResponse = convertToBaiduResponse(mediaBidMetaData);
                	if(bidResponse != null){
                		Baidu.BidResponse responseBuiler = bidResponse.build();
                		logger.info("Baidu.BidResponse Response params is : {}", JsonFormat.printToString(responseBuiler));
                		resp.setContentType("application/octet-stream;charset=UTF-8");
            			resp.getOutputStream().write(responseBuiler.toByteArray());
            			resp.setStatus(Constant.StatusCode.OK);
            			return true;
                	}
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

	private com.madhouse.media.baidu.Baidu.BidResponse.Builder convertToBaiduResponse(
			MediaBidMetaData mediaBidMetaData) {
		
		Baidu.BidResponse.Builder bidResponse = Baidu.BidResponse.newBuilder();
		Baidu.BidRequest bidRequest = (BidRequest) mediaBidMetaData.getRequestObject();
    	MediaBid.Builder mediaBid = mediaBidMetaData.getMediaBidBuilder();
    	MediaResponse.Builder mediaResponse = mediaBid.getResponseBuilder();
    	
    	bidResponse.setId(bidRequest.getId());
    	bidResponse.setBidid(mediaBid.getImpid());
    	
    	Baidu.BidResponse.SeatBid.Builder seatBid = Baidu.BidResponse.SeatBid.newBuilder();
    	
    	Baidu.BidResponse.SeatBid.Bid.Builder bid = Baidu.BidResponse.SeatBid.Bid.newBuilder();
    	bid.setId(mediaBid.getImpid());
    	bid.setImpid(bidRequest.getImp(0).getId());
    	bid.setPrice(mediaResponse.getPrice() != null ? mediaResponse.getPrice() : 0);
    	bid.setBidtype(mediaBidMetaData.getPlcmtMetaData().getBidType()-1);
    	bid.setCrid(mediaResponse.getCrid());
    	bid.setCid(mediaResponse.getCid());
    	bid.setAction(AdActionType.IN_APP_WEBVIEW);
    	if(mediaBid.getRequestBuilder().hasDealid()){
    		bid.setDealid(mediaBid.getRequestBuilder().getDealid());
    	}
    	
    	switch (mediaBidMetaData.getPlcmtMetaData().getAdType()) {
        	case Constant.PlcmtType.BANNER : {
        		Baidu.BidResponse.SeatBid.Bid.Adm.Builder adm = Baidu.BidResponse.SeatBid.Bid.Adm.newBuilder();
        		adm.setAsseturl(mediaResponse.getAdm().get(0));
        		adm.setW(mediaBid.getRequestBuilder().getW());
        		adm.setH(mediaBid.getRequestBuilder().getH());
        		adm.setLandingpage(mediaResponse.getLpgurl());
        		adm.setTitle(mediaResponse.getTitle());
        		adm.setDesc(mediaResponse.getDesc());
        		for (Track track : mediaResponse.getMonitorBuilder().getImpurl()) {
        			adm.addImptrackers(track.getUrl());
				}
        		for (String clk : mediaResponse.getMonitorBuilder().getClkurl()) {
        			adm.addClicktrackers(clk.toString());
				}
        		bid.setAdm(adm);
        		break;
        	}
        	case Constant.PlcmtType.VIDEO : {
        		return null;
        	}
        	case Constant.PlcmtType.NATIVE : {
        		Baidu.BidResponse.SeatBid.Bid.AdmNative.Builder admNative = Baidu.BidResponse.SeatBid.Bid.AdmNative.newBuilder();
        		Baidu.BidRequest.Imp.Native requestNative = bidRequest.getImp(0).getNative();
        		
        		Baidu.BidResponse.SeatBid.Bid.AdmNative.Link.Builder link = Baidu.BidResponse.SeatBid.Bid.AdmNative.Link.newBuilder();
        		link.setUrl(mediaResponse.getLpgurl());
        		for (String clk : mediaResponse.getMonitorBuilder().getClkurl()) {
        			link.addClicktrackers(clk.toString());
				}
        		link.setAction(AdActionType.IN_APP_WEBVIEW);
        		admNative.setLink(link);
        		for (Track track : mediaResponse.getMonitorBuilder().getImpurl()) {
        			admNative.addImptrackers(track.getUrl());
				}
        		for (Asset requestAsset : requestNative.getAssetsList()) {
        			Baidu.BidResponse.SeatBid.Bid.AdmNative.Asset.Builder asset = Baidu.BidResponse.SeatBid.Bid.AdmNative.Asset.newBuilder();
        			asset.setId(requestAsset.getId());
        			asset.setRequired(requestAsset.getRequired());
        			if(requestAsset.hasTitle()){
        				Baidu.BidResponse.SeatBid.Bid.AdmNative.Title.Builder title = Baidu.BidResponse.SeatBid.Bid.AdmNative.Title.newBuilder();
                		title.setText(mediaResponse.getTitle());
                		asset.setTitle(title);
                		admNative.addAssets(asset);
                		continue;
        			}
        			if(requestAsset.hasData()){
        				Baidu.BidResponse.SeatBid.Bid.AdmNative.Data.Builder data = Baidu.BidResponse.SeatBid.Bid.AdmNative.Data.newBuilder();
                		data.setValue(mediaResponse.getDesc());
                		asset.setData(data);
                		admNative.addAssets(asset);
                		continue;
        			}
        			if(requestAsset.hasImg()){
        				Baidu.BidResponse.SeatBid.Bid.AdmNative.Image.Builder image = Baidu.BidResponse.SeatBid.Bid.AdmNative.Image.newBuilder();
                		image.setUrl(mediaResponse.getAdm().get(0));
                		image.setW(mediaBid.getRequestBuilder().getW());
                		image.setH(mediaBid.getRequestBuilder().getH());
                		asset.setImg(image);
                		admNative.addAssets(asset);
                		continue;
        			}
        		}
        		bid.setAdmnative(admNative);
        	}
    	}
    	seatBid.addBid(bid);
    	return bidResponse.addSeatbid(seatBid);
	}

}
