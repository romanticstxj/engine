package com.madhouse.media.fengxing;

import com.alibaba.fastjson.JSON;
import com.madhouse.cache.CacheManager;
import com.madhouse.cache.MaterialMetaData;
import com.madhouse.cache.MediaBidMetaData;
import com.madhouse.cache.MediaMappingMetaData;
import com.madhouse.media.MediaBaseHandler;
import com.madhouse.ssp.Constant;
import com.madhouse.ssp.avro.*;
import com.madhouse.util.*;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by wujunfeng on 2017-11-06.
 */
public class FengXingHandler extends MediaBaseHandler {
    @Override
    public boolean parseMediaRequest(HttpServletRequest req, MediaBidMetaData mediaBidMetaData, HttpServletResponse resp) {
        try {
            String bytes = HttpUtil.getRequestPostBytes(req);
            if (!StringUtils.isEmpty(bytes)) {
                FXBidRequest bidRequest = JSON.parseObject(bytes, FXBidRequest.class);
                if (bidRequest == null) {
                    resp.setStatus(Constant.StatusCode.BAD_REQUEST);
                    return false;
                }

                logger.info("FUNADX Request params is : {}", JSON.toJSONString(bidRequest));

                if (bidRequest.getIsping() != null && bidRequest.getIsping() == 1) {
                    outputStreamWrite(resp, null);
                    return false;
                }

                MediaRequest.Builder mediaRequest = this.conversionToPremiumMADData(bidRequest);
                if (mediaRequest == null) {
                    outputStreamWrite(resp, null);
                    return false;
                }

                if (!BidValidateUtil.validateMediaRequest(mediaRequest,logger)) {
                    outputStreamWrite(resp, null);
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

    private boolean outputStreamWrite(HttpServletResponse resp, FXBidResponse bidResponse) {
        try {
            if (bidResponse != null) {
                resp.setStatus(Constant.StatusCode.OK);
                resp.setHeader("Content-Type", "application/json; charset=utf-8");

                String response = JSON.toJSONString(bidResponse);
                logger.info("FUNADX Response is: {}", response);

                resp.getOutputStream().write(response.getBytes());
                return true;
            } else {
                resp.setStatus(Constant.StatusCode.NO_CONTENT);
            }
        } catch (Exception e) {
            logger.error(e.toString());
            resp.setStatus(Constant.StatusCode.INTERNAL_ERROR);
        }

        return false;
    }

    private MediaRequest.Builder conversionToPremiumMADData(FXBidRequest bidRequest) {
        MediaRequest.Builder mediaRequest = MediaRequest.newBuilder();

        try {
            if (ObjectUtils.isEmpty(bidRequest.getImp())) {
                return null;
            }

            FXBidRequest.Impression impression = bidRequest.getImp().get(0);
            FXBidRequest.Device device = bidRequest.getDevice();
            String os = StringUtil.toString(device.getOs()).toUpperCase();
            
            if (impression.getPmp() != null) {
                if (!ObjectUtils.isEmpty(impression.getPmp().getDeals())) {
                    int size = impression.getPmp().getDeals().size();
                    mediaRequest.setDealid(StringUtil.toString(impression.getPmp().getDeals().get(Utility.nextInt(size)).getId()));
                }
            }
            
            StringBuilder adspaceKey = new StringBuilder();
            adspaceKey.append("FUNADX:");
            if(!StringUtils.isEmpty(FXConstant.TagId.get(impression.getTagid()))){
            	String dealId = mediaRequest.getDealid();
            	if(!StringUtils.isEmpty(dealId) && dealId.length() >=2){
            		adspaceKey.append(dealId.substring(0,2)).append(":");
            	}
            }
            adspaceKey.append(StringUtil.toString(impression.getTagid()));
            mediaRequest.setOs(Constant.OSType.UNKNOWN);
            if (os.equals("ANDROID")) {
                adspaceKey.append(":ANDROID");
                mediaRequest.setOs(Constant.OSType.ANDROID);
            } else if (os.equals("IOS")) {
                adspaceKey.append(":IOS");
                mediaRequest.setOs(Constant.OSType.IOS);
            }

            MediaMappingMetaData mediaMappingMetaData = CacheManager.getInstance().getMediaMapping(adspaceKey.toString());
            if (mediaMappingMetaData == null) {
                return null;
            }

            mediaRequest.setAdtype(2);
            mediaRequest.setAdspacekey(mediaMappingMetaData.getAdspaceKey());
            mediaRequest.setBid(StringUtil.toString(bidRequest.getId()));
            mediaRequest.setIp(StringUtil.toString(device.getIp()));
            mediaRequest.setUa(StringUtil.toString(device.getUa()));
            mediaRequest.setDid(StringUtil.toString(device.getDid()));
            mediaRequest.setDidmd5(StringUtil.toString(device.getDidmd5()));
            mediaRequest.setDpid(StringUtil.toString(device.getDpid()));
            mediaRequest.setDpidmd5(StringUtil.toString(device.getDpidmd5()));
            mediaRequest.setMake(StringUtil.toString(device.getMake()));
            mediaRequest.setModel(StringUtil.toString(device.getModel()));
            mediaRequest.setOsv(StringUtil.toString(device.getOsv()));

            if (bidRequest.getIstest() != null && bidRequest.getIstest() == 1) {
                mediaRequest.setTest(Constant.Test.SIMULATION);
            } else {
                mediaRequest.setTest(Constant.Test.REAL);
            }
            
            mediaRequest.setCarrier(Constant.Carrier.UNKNOWN);
            if (!StringUtils.isEmpty(device.getCarrier())) {
                switch (device.getCarrier()) {
                    case FXConstant.Carrier.CHINA_MOBILE: {
                        mediaRequest.setCarrier(Constant.Carrier.CHINA_MOBILE);
                        break;
                    }

                    case FXConstant.Carrier.CHINA_UNICOM: {
                        mediaRequest.setCarrier(Constant.Carrier.CHINA_UNICOM);
                        break;
                    }

                    case FXConstant.Carrier.CHINA_TELECOM: {
                        mediaRequest.setCarrier(Constant.Carrier.CHINA_TELECOM);
                        break;
                    }
                }
            }

            if (impression.getBidfloor() != null) {
                mediaRequest.setBidfloor(impression.getBidfloor().intValue());
            }

            mediaRequest.setConnectiontype(Constant.ConnectionType.UNKNOWN);
            if (device.getConnectiontype() != null) {
                switch (device.getConnectiontype()) {
                    case FXConstant.ConnectionType.ETHERNET: {
                        mediaRequest.setConnectiontype(Constant.ConnectionType.ETHERNET);
                        break;
                    }

                    case FXConstant.ConnectionType.WIFI: {
                        mediaRequest.setConnectiontype(Constant.ConnectionType.WIFI);
                        break;
                    }

                    case FXConstant.ConnectionType.CELL: {
                        mediaRequest.setConnectiontype(Constant.ConnectionType.CELL);
                        break;
                    }

                    case FXConstant.ConnectionType._2G: {
                        mediaRequest.setConnectiontype(Constant.ConnectionType._2G);
                        break;
                    }

                    case FXConstant.ConnectionType._3G: {
                        mediaRequest.setConnectiontype(Constant.ConnectionType._3G);
                        break;
                    }

                    case FXConstant.ConnectionType._4G: {
                        mediaRequest.setConnectiontype(Constant.ConnectionType._4G);
                        break;
                    }
                }
            }

            mediaRequest.setDevicetype(Constant.DeviceType.UNKNOWN);
            if (device.getDevicetype() != null) {
                switch (device.getDevicetype()) {
                    case FXConstant.DeviceType.PHONE: {
                        mediaRequest.setDevicetype(Constant.DeviceType.PHONE);
                        break;
                    }

                    case FXConstant.DeviceType.PAD: {
                        mediaRequest.setDevicetype(Constant.DeviceType.PAD);
                        break;
                    }

                    case FXConstant.DeviceType.PC: {
                        mediaRequest.setDevicetype(Constant.DeviceType.COMPUTER);
                        break;
                    }

                    case FXConstant.DeviceType.TV: {
                        mediaRequest.setDevicetype(Constant.DeviceType.TV);
                        break;
                    }
                }
            }

            if (device.getExt() != null) {
                FXBidRequest.Device.Ext ext = device.getExt();
                mediaRequest.setIfa(StringUtil.toString(ext.getIdfa()));
                mediaRequest.setMac(StringUtil.toString(ext.getMac()));
                mediaRequest.setMacmd5(StringUtil.toString(ext.getMacmd5()));
            }

            if (device.getGeo() != null) {
                FXBidRequest.Device.Geo geo = device.getGeo();
                Geo.Builder builder = Geo.newBuilder();
                builder.setLat(geo.getLat());
                builder.setLon(geo.getLon());
                mediaRequest.setGeoBuilder(builder);
            }

            if (bidRequest.getApp() != null) {
                FXBidRequest.App app = bidRequest.getApp();
                mediaRequest.setName(StringUtil.toString(app.getName()));
                mediaRequest.setBundle(StringUtil.toString(app.getBundle()));
                mediaRequest.setType(Constant.MediaType.APP);
            }

            if (bidRequest.getSite() != null) {
                FXBidRequest.Site site = bidRequest.getSite();
                mediaRequest.setName(StringUtil.toString(site.getName()));
                mediaRequest.setBundle("com.fengxing.funadx");
                mediaRequest.setType(Constant.MediaType.SITE);
            }

            if (impression.getBanner() != null) {
                FXBidRequest.Impression.Banner banner = impression.getBanner();
                if(banner.getW() != null){
                	mediaRequest.setW(banner.getW());
                }
                if(banner.getH() != null){
                	mediaRequest.setH(banner.getH());
                }
            }

            if (impression.getVideo() != null) {
                FXBidRequest.Impression.Video video = impression.getVideo();
                if(video.getW() != null){
                	mediaRequest.setW(video.getW());
                }
                if(video.getH() != null){
                	mediaRequest.setH(video.getH());
                }
            }
            if (impression.getVideo() != null && impression.getBanner() != null) {
            	logger.warn("Video and Banner Cannot exist at the same time");
                return null;
            }

            return mediaRequest;
        } catch (Exception e) {
            logger.error(e.toString());
        }

        return null;
    }

    @Override
    public boolean packageMediaResponse(MediaBidMetaData mediaBidMetaData, HttpServletResponse resp) {
        try {
            if (mediaBidMetaData != null && mediaBidMetaData.getMediaBidBuilder() != null && mediaBidMetaData.getMaterialMetaData() != null) {
                if (mediaBidMetaData.getMediaBidBuilder().getStatus() == Constant.StatusCode.OK) {
                    FXBidRequest bidRequest = (FXBidRequest)mediaBidMetaData.getRequestObject();
                    MediaBid.Builder mediaBid = mediaBidMetaData.getMediaBidBuilder();
                    MediaResponse.Builder mediaResponse = mediaBid.getResponseBuilder();

                    FXBidResponse bidResponse = new FXBidResponse();
                    bidResponse.setId(bidRequest.getId());
                    bidResponse.setBidid(mediaBid.getImpid());

                    FXBidResponse.SeatBid seatBid = new FXBidResponse.SeatBid();
                    bidResponse.setSeatbid(new LinkedList<>());
                    bidResponse.getSeatbid().add(seatBid);

                    FXBidResponse.SeatBid.Bid bid = new FXBidResponse.SeatBid.Bid();
                    seatBid.setBid(new LinkedList<>());
                    seatBid.getBid().add(bid);

                    bid.setId(mediaBid.getImpid());
                    bid.setImpid(StringUtil.toString(bidRequest.getImp().get(0).getId()));
                    bid.setAdm(mediaResponse.getAdm().get(0));
                    bid.setCrid(StringUtil.toString(mediaBidMetaData.getMaterialMetaData().getMediaQueryKey()));
                    bid.setPrice(mediaResponse.getPrice() != null ? mediaResponse.getPrice().floatValue() : 0);
                    
                    FXBidResponse.SeatBid.Bid.Ext ext = new FXBidResponse.SeatBid.Bid.Ext();
                    ext.setLdp(StringUtil.toString(mediaResponse.getLpgurl()));
                    ext.setTitle(StringUtil.toString(mediaResponse.getTitle()));
                    ext.setDescription(StringUtil.toString(mediaResponse.getDesc()));

                    Monitor.Builder monitor = mediaResponse.getMonitorBuilder();
                    if (monitor != null) {
                        if (ObjectUtils.isNotEmpty(monitor.getClkurl())) {
                            List<String> clkurls = new LinkedList<>();
                            for (int i = 0; i < monitor.getClkurl().size(); ++i) {
                                clkurls.add(monitor.getClkurl().get(i));
                            }
                            ext.setCm(clkurls);
                        }

                        if (ObjectUtils.isNotEmpty(monitor.getImpurl())) {
                            List<FXBidResponse.SeatBid.Bid.Ext.PM> impurls = new LinkedList<>();
                            for (int i = 0; i < monitor.getImpurl().size(); ++i) {
                                FXBidResponse.SeatBid.Bid.Ext.PM pm = new FXBidResponse.SeatBid.Bid.Ext.PM();
                                pm.setPoint(0);
                                pm.setUrl(monitor.getImpurl().get(i).getUrl());
                                impurls.add(pm);
                            }
                            ext.setPm(impurls);
                        }
                    }
                    
                    bid.setExt(ext);
                    return outputStreamWrite(resp, bidResponse);
                }
            }
        } catch (Exception e) {
            logger.error(e.toString());
        }

        return outputStreamWrite(resp, null);
    }
}
