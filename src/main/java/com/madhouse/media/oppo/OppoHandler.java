package com.madhouse.media.oppo;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.madhouse.cache.CacheManager;
import com.madhouse.cache.MediaBidMetaData;
import com.madhouse.cache.MediaMappingMetaData;
import com.madhouse.media.MediaBaseHandler;
import com.madhouse.media.oppo.OppoNativeRequest.Asset;
import com.madhouse.ssp.Constant;
import com.madhouse.ssp.avro.MediaBid;
import com.madhouse.ssp.avro.MediaRequest;
import com.madhouse.ssp.avro.MediaResponse.Builder;
import com.madhouse.ssp.avro.Track;
import com.madhouse.util.HttpUtil;
import com.madhouse.util.ObjectUtils;

public class OppoHandler extends MediaBaseHandler {
    
    @Override
    public boolean parseMediaRequest(HttpServletRequest req, MediaBidMetaData mediaBidMetaData, HttpServletResponse resp) {
        try {
            req.setCharacterEncoding("UTF-8");
            String bytes = HttpUtil.getRequestPostBytes(req);
            OppoBidRequest oppoBidRequest = JSON.parseObject(bytes, OppoBidRequest.class);
            int status = validateRequiredParam(oppoBidRequest, resp);
            if (status == Constant.StatusCode.OK) {
                MediaRequest.Builder mediaRequest = conversionToPremiumMADDataModel(oppoBidRequest);
                if(mediaRequest != null){
                    mediaBidMetaData.getMediaBidBuilder().setRequestBuilder(mediaRequest);
                    mediaBidMetaData.setRequestObject(oppoBidRequest);
                    return true;
                }else{
                	status =Constant.StatusCode.BAD_REQUEST;
                }
            }
            OppoResponse oppoBidResponse = convertToOppoResponse(mediaBidMetaData,status,oppoBidRequest.getId(),oppoBidRequest.getImp().get(0).getId());
            outputStreamWrite(resp, oppoBidResponse);
            resp.setStatus(Constant.StatusCode.OK);
            return false;
        } catch (Exception e) {
            logger.error(e.toString() + "_Status_" + Constant.StatusCode.BAD_REQUEST);
            resp.setStatus(Constant.StatusCode.OK);
            return false;
        }
    }
    
    /**
     * 获取request中native对象中的assets 和ver
     * @param oppoBidRequest
     * @return
     */
    private OppoNativeRequest getRequestNative(String nativeStr){
    	OppoNativeRequest oppoNativeRequest=null;
		try {
			Map nativeLast = JSON.parseObject(nativeStr);  
			 for (Object obj : nativeLast.keySet()){  
				 if(obj.equals("native")){
		            String natives =nativeLast.get(obj).toString();
		            oppoNativeRequest = JSON.parseObject(natives, OppoNativeRequest.class);
				 }
			 } 
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 return oppoNativeRequest;
    	
    }
    
    
    private MediaRequest.Builder conversionToPremiumMADDataModel(OppoBidRequest oppoBidRequest) {
        MediaRequest.Builder mediaRequest = MediaRequest.newBuilder();
        
        OppoBidRequest.App app = oppoBidRequest.getApp();
        OppoBidRequest.Device device = oppoBidRequest.getDevice();
        OppoBidRequest.Imp imp = oppoBidRequest.getImp().get(0);
//        OppoBidRequest.Imp.Pmp pmp =oppoBidRequest.getImp().get(0).getPmp();
        
        OppoNativeRequest oppoNativeRequest = getRequestNative(oppoBidRequest.getImp().get(0).getNatives().getRequest());
        
        // 广告请求唯一id
        mediaRequest.setBid(oppoBidRequest.getId());
        mediaRequest.setAdtype(2);
        mediaRequest.setName(app.getName());
        mediaRequest.setBundle(app.getBundle());
        mediaRequest.setBidfloor(Integer.parseInt(imp.getBidfloor()+""));
        mediaRequest.setDevicetype(Constant.DeviceType.UNKNOWN);
        mediaRequest.setType(Constant.MediaType.APP);
        
        StringBuilder sb = new StringBuilder();
        sb.append("OPPO:");
        //广告位id
        sb.append(imp.getTagid()).append(":");
        // 操作系统的类型
        String os = device.getOs(); 
        if (OppoStatusCode.Os.OS_ANDROID.equalsIgnoreCase(os)) {
            sb.append(OppoStatusCode.Os.OS_ANDROID);
            mediaRequest.setDid(device.getDidmd5());
            mediaRequest.setOs(Constant.OSType.ANDROID);
        } else if(OppoStatusCode.Os.OS_IOS.equalsIgnoreCase(os)){
            sb.append(OppoStatusCode.Os.OS_IOS);
            mediaRequest.setIfa(device.getDidmd5());
            mediaRequest.setOs(Constant.OSType.IOS);
        }
        
        //0—未知，1—Ethernet，2—wifi，3—蜂窝网络，未知代，4—蜂窝网络，2G，5—蜂窝网络，3G，6—蜂窝网络，4G。
        switch (device.getConnectiontype()) {
            case OppoStatusCode.ConnectionType.UNKNOWN:
                mediaRequest.setConnectiontype(Constant.ConnectionType.ETHERNET);
                break;
            case OppoStatusCode.ConnectionType.WIFI:
                mediaRequest.setConnectiontype(Constant.ConnectionType.WIFI);
                break;
            case OppoStatusCode.ConnectionType._2G:
                mediaRequest.setConnectiontype(Constant.ConnectionType._2G);
                break;
            case OppoStatusCode.ConnectionType._3G:
                mediaRequest.setConnectiontype(Constant.ConnectionType._3G);
                break;
            case OppoStatusCode.ConnectionType._4G:
                mediaRequest.setConnectiontype(Constant.ConnectionType._4G);
                break;
            case OppoStatusCode.ConnectionType.Ethernet:
                mediaRequest.setConnectiontype(Constant.ConnectionType.ETHERNET);
                break;
            default:
                mediaRequest.setConnectiontype(Constant.ConnectionType.CELL);
                break;
        }
        
        if(!StringUtils.isEmpty(device.getIp())){
            mediaRequest.setIp(device.getIp());
        }
        if(!StringUtils.isEmpty(device.getUa())){
            mediaRequest.setUa(device.getUa());
        }
        if(!StringUtils.isEmpty(device.getOsv())){
        	mediaRequest.setOsv(device.getOsv());
        }
        if(!StringUtils.isEmpty(device.getMake())){
            mediaRequest.setMake(device.getMake());
        }
        if(!StringUtils.isEmpty(device.getModel())){
            mediaRequest.setModel(device.getModel()); 
        }
        if(null !=device.getH()){
            mediaRequest.setH(device.getH());
        }
        if(null != device.getW()){
            mediaRequest.setW(device.getW());
        }
        
        MediaMappingMetaData mappingMetaData = CacheManager.getInstance().getMediaMapping(sb.toString());
        if (mappingMetaData != null) {
            mediaRequest.setAdspacekey(mappingMetaData.getAdspaceKey());
        } else {
            mappingMetaData = CacheManager.getInstance().getMediaMapping("OPPO:0:0");
            if(mappingMetaData != null){
                mediaRequest.setAdspacekey(mappingMetaData.getAdspaceKey());
            }else{
                return null;
            }
        }
        logger.info("OPPO convert mediaRequest is : {}", JSON.toJSONString(mediaRequest));
        return mediaRequest;
    }

    private int validateRequiredParam(OppoBidRequest oppoBidRequest, HttpServletResponse resp) {
        if (ObjectUtils.isNotEmpty(oppoBidRequest)) {
            String id = oppoBidRequest.getId();
            if (StringUtils.isNotEmpty(id)) {
                if (ObjectUtils.isEmpty(oppoBidRequest.getDevice())) {
                    logger.warn("oppoBidRequest.Device is null");
                    return Constant.StatusCode.BAD_REQUEST;
                }
                if (ObjectUtils.isEmpty(oppoBidRequest.getDevice().getOs())) {
                    logger.warn("{}:oppoBidRequest.Device.os is null",id);
                    return Constant.StatusCode.BAD_REQUEST;
                }
                if (ObjectUtils.isEmpty(oppoBidRequest.getImp().get(0))) {
                    logger.warn("oppoBidRequest.Imp[0] is null");
                    return Constant.StatusCode.BAD_REQUEST;
                }
                if (ObjectUtils.isEmpty(oppoBidRequest.getApp())) {
                    logger.warn("oppoBidRequest.App is null");
                    return Constant.StatusCode.BAD_REQUEST;
                }
                return Constant.StatusCode.OK;
            }
            logger.warn("oppoBidRequest.id is null");
        }
        return Constant.StatusCode.BAD_REQUEST;
    }

    @Override
    public boolean packageMediaResponse(MediaBidMetaData mediaBidMetaData, HttpServletResponse resp) {
        try {
            if (mediaBidMetaData != null && mediaBidMetaData.getMediaBidBuilder() != null) {
                MediaBid.Builder mediaBid = mediaBidMetaData.getMediaBidBuilder();
                OppoResponse result =null;
                if (mediaBid.getResponseBuilder() != null && mediaBid.getStatus() == Constant.StatusCode.OK) {
                    result = convertToOppoResponse(mediaBidMetaData,mediaBid.getStatus(),null,null);
                } else {
                	result = convertToOppoResponse(mediaBidMetaData,Constant.StatusCode.NO_CONTENT,null,null);
                }
                outputStreamWrite(resp, result);
            }
        } catch (Exception e) {
            logger.error(e.toString() + "_Status_" + Constant.StatusCode.BAD_REQUEST);
            resp.setStatus(Constant.StatusCode.BAD_REQUEST);
            return false;
        }
        resp.setStatus(Constant.StatusCode.BAD_REQUEST);
        return false;
    }
    
    
    private boolean outputStreamWrite(HttpServletResponse resp, OppoResponse oppoResponse)  {
        try {
        	if (oppoResponse != null) {
                resp.setHeader("Content-Type", "application/json; charset=utf-8");
                resp.getOutputStream().write(JSON.toJSONString(oppoResponse).getBytes("utf-8"));
                resp.setStatus(Constant.StatusCode.OK);
                logger.warn("_Status_" + Constant.StatusCode.OK);
            }
        } catch (Exception e) {
            logger.error(e.toString() + "_Status_" + Constant.StatusCode.NO_CONTENT);
            return false;
        }
        logger.info("Tencent outputStreamWrite is:{}",oppoResponse.toString());
        return true;
    }

    private OppoResponse convertToOppoResponse(MediaBidMetaData mediaBidMetaData,int status,String requestId,String bidid) {
    	//response DSP对象
    	Builder mediaResponse= mediaBidMetaData.getMediaBidBuilder().getResponseBuilder();
    	OppoResponse response = new OppoResponse();
    	List<OppoResponse.SeatBid.Bid> bids = new ArrayList<>(1);
    	List<OppoResponse.SeatBid> seatbids = new ArrayList<>(1);
    	OppoResponse.SeatBid seatBid = response.new SeatBid();
    	OppoResponse.SeatBid.Bid bid = seatBid.new Bid();
    	//Oppo Bidresponse
    	
    	if(Constant.StatusCode.OK != status){
    		response.setNbr(2);
    		response.setId(requestId);//竞价请求id
	    	response.setBidid(bidid);//竞价者生成的id唯一标识
    	}else{
	    	//request请求对象
	    	OppoBidRequest oppoBidRequest =(OppoBidRequest)mediaBidMetaData.getRequestObject();
	    	//判断native模式，还是pmp模式曝光
	    	if(null != oppoBidRequest && null != oppoBidRequest.getImp().get(0) && null !=oppoBidRequest.getImp().get(0).getNatives() && null !=oppoBidRequest.getImp().get(0).getNatives().getRequest()){
	    		OppoNativeRequest oppoNativeRequest = getRequestNative(oppoBidRequest.getImp().get(0).getNatives().getRequest());
	    		OppoNativeResponse oppoNativeResponse = new OppoNativeResponse();
	        	if(null != oppoNativeRequest && null !=oppoNativeRequest.getAssets() && oppoNativeRequest.getAssets().size() > 0){
	        		List<com.madhouse.media.oppo.OppoNativeResponse.Asset> assetNativeResponseList =new ArrayList<OppoNativeResponse.Asset>();
	        		
	        		for(Asset assetNativeRequest:oppoNativeRequest.getAssets()){
	        			OppoNativeResponse.Asset assetResponse = oppoNativeResponse.new Asset();
	        			int h=0;
	        			int w=0;
	        			
	        			if(null !=assetNativeRequest.getTitle()){
	        				OppoNativeResponse.Asset.Title titleResponse = assetResponse.new Title();
	        				titleResponse.setText(assetNativeRequest.getTitle().getLen());
	        				assetResponse.setTitle(titleResponse);
	        			}
	        			if(null !=assetNativeRequest.getImg()){
	        				OppoNativeResponse.Asset.Img imgResponse = assetResponse.new Img();
	        				imgResponse.setH(assetNativeRequest.getImg().getH());
	        				imgResponse.setW(assetNativeRequest.getImg().getW());
	        				imgResponse.setUrl(mediaResponse.getAdm().get(0));//物料url
	        				assetResponse.setImg(imgResponse);
	        				h =assetNativeRequest.getImg().getH();
	        				w=assetNativeRequest.getImg().getW();
	        			}
	        			if(null !=assetNativeRequest.getData()){
	        				OppoNativeResponse.Asset.Data dataResponse = assetResponse.new Data();
	        				dataResponse.setValue(mediaResponse.getTitle());//指定类型的数据内容
	        				assetResponse.setData(dataResponse);
	        			}
	        			if(null !=assetNativeRequest.getSpecificFeeds()){
	        				OppoNativeResponse.Asset.SpecificFeeds specificFeeds = assetResponse.new SpecificFeeds();
	        				if(h*w ==640*320 && null !=mediaResponse.getAdm() && mediaResponse.getAdm().size()==1){
	        					specificFeeds.setFormateType(1);//信息流大图
	        				}else if(h*w ==320*210 && null !=mediaResponse.getAdm() && mediaResponse.getAdm().size()==1){
	        					specificFeeds.setFormateType(2);//信息流小图
	        				}else if(h*w ==640*210 && null !=mediaResponse.getAdm() && mediaResponse.getAdm().size()==3){
	        					specificFeeds.setFormateType(3);//信息流多图
	        				}
	        				if(null != mediaResponse.getAdm()){
	        					specificFeeds.setImageUrls(mediaResponse.getAdm());
	        				}
	        				assetResponse.setSpecificFeeds(specificFeeds);
	        			}
	        			assetNativeResponseList.add(assetResponse);
	        		}
	        		oppoNativeResponse.setAssets(assetNativeResponseList);
	        		//Link 对象:落地页和点击监测
	    			OppoNativeResponse.Link linkResponse = oppoNativeResponse.new Link();
	    			linkResponse.setUrl(mediaResponse.getLpgurl());
	    			linkResponse.setClicktrackers(mediaResponse.getMonitorBuilder().getClkurl());
	        		oppoNativeResponse.setLint(linkResponse);
	        		//展示监测
	        		List<String> imptrackers = new ArrayList<String>();
	        		for (Track track : mediaResponse.getMonitorBuilder().getImpurl()) {
	        			imptrackers.add(track.getUrl());
	                } 
	        		oppoNativeResponse.setImptrackers(imptrackers);
	        		oppoNativeResponse.setVer("1.1");
	        	}
	        	bid.setAdm(JSON.toJSONString(oppoNativeResponse).toString());
	        	
	    	}else if(null != oppoBidRequest && null != oppoBidRequest.getImp().get(0) && null !=oppoBidRequest.getImp().get(0).getPmp()){
	    		if(null != oppoBidRequest.getImp().get(0).getPmp().getDelas() && oppoBidRequest.getImp().get(0).getPmp().getDelas().size() >0){
	    			bid.setDealid(oppoBidRequest.getImp().get(0).getPmp().getDelas().get(0).getId());
	    		}
	    	}
	    	
	    	//设置点击和展示监测:如果asset对象中有，以asset为主，如果没有，则以bid对象中为主
			bid.setClicktrackers(mediaResponse.getMonitorBuilder().getClkurl());
			List<String> imptrackers = new ArrayList<String>();
			for (Track track : mediaResponse.getMonitorBuilder().getImpurl()) {
				imptrackers.add(track.getUrl());
	        } 
			bid.setImptrackers(imptrackers);
			
	    	//seatBid中的bid对象
	    	bid.setId(mediaBidMetaData.getMediaBidBuilder().getImpid());
	    	bid.setImpid(oppoBidRequest.getImp().get(0).getId());
	    	bid.setPrice(mediaResponse.getPrice());
	    	bid.setAdid(mediaResponse.getCid());//预加载的广告id(dsp广告活动id)
	    	
	    	//设置List值，组装到response中
	    	bids.add(bid);
	    	seatBid.setBid(bids);
	    	seatbids.add(seatBid);
	    	response.setSeatbid(seatbids);
	    	
	    	response.setId(oppoBidRequest.getId());//竞价请求id
	    	response.setBidid(mediaBidMetaData.getMediaBidBuilder().getImpid());//竞价者生成的id唯一标识
    	}
    	logger.info("OPPO Response params is : {}", JSON.toJSONString(response));
        return response;
    }
    
}
