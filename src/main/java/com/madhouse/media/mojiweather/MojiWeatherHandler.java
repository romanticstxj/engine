package com.madhouse.media.mojiweather;

import java.net.URLDecoder;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.madhouse.ssp.avro.*;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.madhouse.cache.MediaBidMetaData;
import com.madhouse.media.MediaBaseHandler;
import com.madhouse.ssp.Constant;
import com.madhouse.util.ObjectUtils;
import com.madhouse.util.StringUtil;

public class MojiWeatherHandler extends MediaBaseHandler {
    private static final Set<Integer> mojiAdStyles = new HashSet<>();

    static {
        mojiAdStyles.add(1);
        mojiAdStyles.add(2);
        mojiAdStyles.add(4);
        mojiAdStyles.add(8);
        mojiAdStyles.add(16);
        mojiAdStyles.add(32);
        mojiAdStyles.add(64);
        mojiAdStyles.add(128);
        mojiAdStyles.add(256);
        mojiAdStyles.add(512);
        mojiAdStyles.add(1024);
        mojiAdStyles.add(2048);
        mojiAdStyles.add(4096);
        mojiAdStyles.add(8192);
    }

    @Override
    public boolean parseMediaRequest(HttpServletRequest req, MediaBidMetaData mediaBidMetaData, HttpServletResponse resp) {
        
        try {
            MojiWeatherBidRequest mojiWeatherBidRequest = new MojiWeatherBidRequest();
            BeanUtils.populate(mojiWeatherBidRequest, req.getParameterMap());

            int status = validateRequiredParam(mojiWeatherBidRequest);
            if (status == Constant.StatusCode.OK) {
                logger.info("MojiWeather Request params is : {}", JSON.toJSONString(mojiWeatherBidRequest));
                MediaRequest.Builder mediaRequest = conversionToPremiumMADDataModel(mojiWeatherBidRequest);
                if (mediaRequest != null) {
                    mediaBidMetaData.getMediaBidBuilder().setRequestBuilder(mediaRequest);
                    mediaBidMetaData.setRequestObject(mojiWeatherBidRequest);
                    return true;
                }
            } else {
                logger.warn("MojiWeather Request params is : {}", JSON.toJSONString(mojiWeatherBidRequest));
            }

            MojiWeatherResponse moWeatherResponse = convertToMojiWeatherResponse(Constant.StatusCode.BAD_REQUEST, mediaBidMetaData, mojiWeatherBidRequest);
            outputStreamWrite(resp, moWeatherResponse);

        } catch (Exception e) {
            logger.error(e.toString() + "_Status_" + Constant.StatusCode.BAD_REQUEST);
            resp.setStatus(Constant.StatusCode.BAD_REQUEST);
        }

        return false;
    }
    
    private MediaRequest.Builder conversionToPremiumMADDataModel(MojiWeatherBidRequest mojiWeatherBidRequest) {
        MediaRequest.Builder mediaRequest = MediaRequest.newBuilder();

        try {
            //每次请求的唯一标识
            mediaRequest.setBid(mojiWeatherBidRequest.getSessionid());
            //广告位Key
            mediaRequest.setAdspacekey(mojiWeatherBidRequest.getAdid());
            mediaRequest.setType(Constant.MediaType.APP);
            mediaRequest.setBundle(mojiWeatherBidRequest.getPkgname());
            mediaRequest.setName(URLDecoder.decode(mojiWeatherBidRequest.getAppname()));
            mediaRequest.setBidfloor(mojiWeatherBidRequest.getBasic_price() == null ? 0 : mojiWeatherBidRequest.getBasic_price());
            mediaRequest.setAdtype(2);
            mediaRequest.setDevicetype(Constant.DeviceType.UNKNOWN);
            mediaRequest.setCarrier(mojiWeatherBidRequest.getCarrier());
            mediaRequest.setCategory(17);

            if (mojiWeatherBidRequest.getDebug() != null && mojiWeatherBidRequest.getDebug() == 1) {
                mediaRequest.setTest(Constant.Test.SIMULATION);
            }
            
            switch (mojiWeatherBidRequest.getOs()) {
                case MojiWeather.OSType.ANDROID:
                    mediaRequest.setOs(Constant.OSType.ANDROID);
                    mediaRequest.setDid(StringUtil.toString(mojiWeatherBidRequest.getImei()));
                    mediaRequest.setDpid(StringUtil.toString(mojiWeatherBidRequest.getAndid()));
                    mediaRequest.setIfa(StringUtil.toString(mojiWeatherBidRequest.getAndaid()));
                    break;
                case MojiWeather.OSType.IOS:
                    mediaRequest.setOs(Constant.OSType.IOS);
                    mediaRequest.setIfa(StringUtil.toString(mojiWeatherBidRequest.getIdfa()));
                    mediaRequest.setDpid(StringUtil.toString(mojiWeatherBidRequest.getOpenudid()));
                    break;
                case MojiWeather.OSType.WINDOWS_PHONE:
                    mediaRequest.setOs(Constant.OSType.WINDOWS_PHONE);
                    mediaRequest.setDpid(StringUtil.toString(mojiWeatherBidRequest.getUnqid()));
                    break;
                case MojiWeather.OSType.OTHERS:
                    mediaRequest.setOs(Constant.OSType.UNKNOWN);
                    mediaRequest.setDpid(StringUtil.toString(mojiWeatherBidRequest.getUnqid()));
                    break;
            };

            mediaRequest.setOsv(mojiWeatherBidRequest.getOsv());
            mediaRequest.setConnectiontype(mojiWeatherBidRequest.getNet());
            mediaRequest.setIp(mojiWeatherBidRequest.getIp());
            mediaRequest.setUa(URLDecoder.decode(mojiWeatherBidRequest.getUa()));
            mediaRequest.setDealid(StringUtil.toString(mojiWeatherBidRequest.getComment()));

            mediaRequest.setW(0);
            mediaRequest.setH(0);

            if (mojiWeatherBidRequest.getAdtype() == 3) {
                mediaRequest.setW(mojiWeatherBidRequest.getScrwidth());
                mediaRequest.setH(mojiWeatherBidRequest.getScrheight());
            }

            mediaRequest.setMac(StringUtil.toString(mojiWeatherBidRequest.getWma()));
            mediaRequest.setModel(URLDecoder.decode(mojiWeatherBidRequest.getDevice()));

            if (!StringUtils.isEmpty(mojiWeatherBidRequest.getLon()) && !StringUtils.isEmpty(mojiWeatherBidRequest.getLat())){
                Geo.Builder geo = Geo.newBuilder();
                //经度
                geo.setLon(Float.parseFloat(mojiWeatherBidRequest.getLon()));
                //纬度
                geo.setLat(Float.parseFloat(mojiWeatherBidRequest.getLat()));
                mediaRequest.setGeoBuilder(geo);
            }

            logger.info("mojiWeather convert mediaRequest is :{}", JSON.toJSONString(mediaRequest));
        } catch (Exception e) {
            logger.error(e.toString());
            return null;
        }

        return mediaRequest;
    }

    private int validateRequiredParam(MojiWeatherBidRequest mojiWeatherBidRequest) {
        if (ObjectUtils.isEmpty(mojiWeatherBidRequest)) {
            return Constant.StatusCode.BAD_REQUEST;
        }

        if (StringUtils.isEmpty(mojiWeatherBidRequest.getAdid())) {
            logger.warn("adid is missing");
            return Constant.StatusCode.BAD_REQUEST;
        }

        if (mojiWeatherBidRequest.getAdid().length() != 16) {
            logger.warn("adid is not correct.");
            return Constant.StatusCode.BAD_REQUEST;
        }

        String adid = mojiWeatherBidRequest.getAdid();
        if (StringUtils.isEmpty(mojiWeatherBidRequest.getSessionid())) {
            logger.warn("{}:sessionid is missing", adid);
            return Constant.StatusCode.BAD_REQUEST;
        }

        if (mojiWeatherBidRequest.getAdtype() == null) {
            logger.warn("{}:adtype is missing", adid);
            return Constant.StatusCode.BAD_REQUEST;
        }

        if (mojiWeatherBidRequest.getAdtype() != 2 && mojiWeatherBidRequest.getAdtype() != 3) {
            logger.warn("{}:adtype is not correct.", adid);
            return Constant.StatusCode.BAD_REQUEST;
        }

        /*if (mojiWeatherBidRequest.getTradelevel() == null) {
            logger.warn("{}:tradelevel is missing", adid);
            return Constant.StatusCode.BAD_REQUEST;
        }

        if (mojiWeatherBidRequest.getTradelevel() < 1 || mojiWeatherBidRequest.getAdtype() > 3) {
            logger.warn("{}:tradelevel is not correct.", adid);
            return Constant.StatusCode.BAD_REQUEST;
        }*/

        if (StringUtils.isEmpty(mojiWeatherBidRequest.getPkgname())) {
            logger.warn("{}:pkgname is missing", adid);
            return Constant.StatusCode.BAD_REQUEST;
        }

        if (StringUtils.isEmpty(mojiWeatherBidRequest.getAppname())) {
            logger.warn("{}:appname is missing", adid);
            return Constant.StatusCode.BAD_REQUEST;
        }

        if (mojiWeatherBidRequest.getNet() == null) {
            logger.warn("{}:net is missing", adid);
            return Constant.StatusCode.BAD_REQUEST;
        }

        if (mojiWeatherBidRequest.getNet() < 0 || mojiWeatherBidRequest.getNet() > 4) {
            logger.warn("{}:net is not correct.", adid);
            return Constant.StatusCode.BAD_REQUEST;
        }

        if (mojiWeatherBidRequest.getCarrier() == null) {
            logger.warn("{}:carrier is missing", adid);
            return Constant.StatusCode.BAD_REQUEST;
        }

        if (mojiWeatherBidRequest.getCarrier() < 0 || mojiWeatherBidRequest.getCarrier() > 3) {
            logger.warn("{}:carrier is not correct.", adid);
            return Constant.StatusCode.BAD_REQUEST;
        }

        if (mojiWeatherBidRequest.getOs() == null) {
            logger.warn("{}:os is missing", adid);
            return Constant.StatusCode.BAD_REQUEST;
        }

        if (mojiWeatherBidRequest.getOs() < 0 || mojiWeatherBidRequest.getOs() > 3) {
            logger.warn("{}:os is not correct.", adid);
            return Constant.StatusCode.BAD_REQUEST;
        }

        if (StringUtils.isEmpty(mojiWeatherBidRequest.getOsv())) {
            logger.warn("{}:osv is missing", adid);
            return Constant.StatusCode.BAD_REQUEST;
        }

        if (StringUtils.isEmpty(mojiWeatherBidRequest.getIp())) {
            logger.warn("{}:ip is missing", adid);
            return Constant.StatusCode.BAD_REQUEST;
        }
        if (mojiWeatherBidRequest.getBasic_price() == null ) {
            logger.warn("{}:Basic_price is missing", adid);
            return Constant.StatusCode.BAD_REQUEST;
        }
        
        if (StringUtils.isEmpty(mojiWeatherBidRequest.getUa())) {
            logger.warn("{}:up is missing", adid);
            return Constant.StatusCode.BAD_REQUEST;
        }

        if (StringUtils.isEmpty(mojiWeatherBidRequest.getDevice())) {
            logger.warn("{}:device is missing", adid);
            return Constant.StatusCode.BAD_REQUEST;
        }

        if (mojiWeatherBidRequest.getAdtype() == 3) {
        	if (mojiWeatherBidRequest.getScrwidth() == null|| mojiWeatherBidRequest.getScrheight()  == null) {
                logger.warn("{}:scrwidth and scrheight is missing", adid);
                return Constant.StatusCode.BAD_REQUEST;
            }
            if (mojiWeatherBidRequest.getScrwidth() <= 0 || mojiWeatherBidRequest.getScrheight() <= 0) {
                logger.warn("{}:scrwidth or scrheight is missing", adid);
                return Constant.StatusCode.BAD_REQUEST;
            }
        } else {
        	if (mojiWeatherBidRequest.getAdstyle() == null) {
                logger.warn("{}:adstyle is missing", adid);
                return Constant.StatusCode.BAD_REQUEST;
            }

            if (!mojiAdStyles.contains(mojiWeatherBidRequest.getAdstyle())) {
                logger.warn("{}:adstyle is not correct.", adid);
                return Constant.StatusCode.BAD_REQUEST;
            }
        }

        if (mojiWeatherBidRequest.getOs() == MojiWeather.OSType.ANDROID) {
            if (StringUtils.isEmpty(mojiWeatherBidRequest.getImei()) && StringUtils.isEmpty(mojiWeatherBidRequest.getAndid()) && StringUtils.isEmpty(mojiWeatherBidRequest.getAndaid())) {
                logger.warn("{}:android device id is missing.", adid);
                return Constant.StatusCode.BAD_REQUEST;
            }
        }

        if (mojiWeatherBidRequest.getOs() == MojiWeather.OSType.IOS) {
            if (StringUtils.isEmpty(mojiWeatherBidRequest.getIdfa()) &&
                    StringUtils.isEmpty(mojiWeatherBidRequest.getOpenudid())) {
                logger.warn("{}:ios device id is missing.", adid);
                return Constant.StatusCode.BAD_REQUEST;
            }
        }

        if (mojiWeatherBidRequest.getOs() == MojiWeather.OSType.WINDOWS_PHONE ||
                mojiWeatherBidRequest.getOs() == MojiWeather.OSType.OTHERS) {
            if (StringUtils.isEmpty(mojiWeatherBidRequest.getUnqid())) {
                logger.warn("{}:device id is missing.", adid);
                return Constant.StatusCode.BAD_REQUEST;
            }
        }

        return Constant.StatusCode.OK;
    }

    @Override
    public boolean packageMediaResponse(MediaBidMetaData mediaBidMetaData, HttpServletResponse resp) {
        if (mediaBidMetaData != null && mediaBidMetaData.getMediaBidBuilder() != null) {
            MediaBid.Builder mediaBid = mediaBidMetaData.getMediaBidBuilder();
            MojiWeatherBidRequest mojiWeatherBidRequest = (MojiWeatherBidRequest)mediaBidMetaData.getRequestObject();

            MojiWeatherResponse mojiWeatherResponse = new MojiWeatherResponse();
            mojiWeatherResponse = convertToMojiWeatherResponse(mediaBid.getStatus(), mediaBidMetaData, mojiWeatherBidRequest);

            outputStreamWrite(resp, mojiWeatherResponse);
            logger.info("MojiWeather Response params is : {}", JSON.toJSONString(mojiWeatherResponse));

            return true;
        }

        resp.setStatus(Constant.StatusCode.INTERNAL_ERROR);
        return false;
    }

    private boolean outputStreamWrite(HttpServletResponse resp, MojiWeatherResponse mojiWeatherResponse)  {
        try {
            resp.setStatus(Constant.StatusCode.OK);
            resp.setHeader("Content-Type", "application/json; charset=utf-8");
            resp.getOutputStream().write(JSON.toJSONString(mojiWeatherResponse).getBytes());
        } catch (Exception e) {
            logger.error(e.toString() + "_Status_" + Constant.StatusCode.NO_CONTENT);
            return false;
        }
        return true;
    }

    private MojiWeatherResponse convertToMojiWeatherResponse(int status, MediaBidMetaData mediaBidMetaData, MojiWeatherBidRequest mojiWeatherRequest) {
        MojiWeatherResponse moWeatherBidResponse = new MojiWeatherResponse();
        MojiWeatherResponse.data data = new MojiWeatherResponse.data();

        data.setAdid(StringUtil.toString(mojiWeatherRequest.getAdid()));
        data.setSessionid(StringUtil.toString(mojiWeatherRequest.getSessionid()));

        if (mojiWeatherRequest.getAdtype() != null) {
            data.setAdtype(mojiWeatherRequest.getAdtype());
        }

        if (mojiWeatherRequest.getAdstyle() != null) {
            data.setAdstyle(mojiWeatherRequest.getAdstyle());
        }

        if (status != Constant.StatusCode.OK) {
            switch (status) {
                case Constant.StatusCode.BAD_REQUEST: {
                    if (!StringUtils.isEmpty(mojiWeatherRequest.getAdid()) && mojiWeatherRequest.getAdid().length() != 16) {
                        moWeatherBidResponse.setCode(MojiWeather.StatusCode.CODE_401);
                    } else {
                        moWeatherBidResponse.setCode(MojiWeather.StatusCode.CODE_402);
                    }

                    break;
                }

                case Constant.StatusCode.NO_CONTENT: {
                    moWeatherBidResponse.setCode(MojiWeather.StatusCode.CODE_400);
                    break;
                }

                default: {
                    moWeatherBidResponse.setCode(MojiWeather.StatusCode.CODE_500);
                    break;
                }
            }

            moWeatherBidResponse.setData(data);
            return moWeatherBidResponse;
        }

        MediaRequest.Builder mediaRequest = mediaBidMetaData.getMediaBidBuilder().getRequestBuilder();
        MediaResponse.Builder mediaResponse= mediaBidMetaData.getMediaBidBuilder().getResponseBuilder();
        moWeatherBidResponse.setCode(MojiWeather.StatusCode.CODE_200);
        data.setPrice(mediaResponse.getPrice());
        data.setChargingtype(1);
        data.setUrlSeparator("###");

        if (mediaResponse.getDuration() != null && mediaResponse.getDuration() > 0) {
            data.setType(2);
            data.setVedioimg(StringUtil.toString(mediaResponse.getCover()));
            data.setVedioPlaytime(mediaResponse.getDuration());

            if (ObjectUtils.isNotEmpty(mediaResponse.getAdm())) {
                data.setVediourl(mediaResponse.getAdm().get(0));
            }
        } else {
            data.setType(1);
            if (ObjectUtils.isNotEmpty(mediaResponse.getAdm())) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < mediaResponse.getAdm().size() - 1; ++i) {
                    sb.append(mediaResponse.getAdm().get(i)).append(data.getUrlSeparator());
                }
                sb.append(mediaResponse.getAdm().get(mediaResponse.getAdm().size() - 1));
                data.setImgurl(sb.toString());
            }
        }
        
        data.setAdwidth(mediaRequest.getW());
        data.setAdheight(mediaRequest.getH());

        if (!StringUtils.isEmpty(mojiWeatherRequest.getFeed_support_types())) {
            String[] feeds = mojiWeatherRequest.getFeed_support_types().split(";");

            Set<Integer> feedTypes = new HashSet<>();
            for (int i = 0; i < feeds.length; ++i) {
                if (StringUtil.isNumeric(feeds[i])) {
                    feedTypes.add(Integer.parseInt(feeds[i]));
                }
            }

            String size = String.format("%dx%d", mediaRequest.getW(), mediaRequest.getH());
            switch (size) {
                case "200x130": {
                    if (mediaResponse.getAdm().size() > 1) {
                        if (feedTypes.contains(MojiWeather.FeedType.FEED_TYPE_6)) {
                            data.setFeed_type(MojiWeather.FeedType.FEED_TYPE_6);
                        }
                    } else {
                        if (feedTypes.contains(MojiWeather.FeedType.FEED_TYPE_3)) {
                            data.setFeed_type(MojiWeather.FeedType.FEED_TYPE_3);
                        }
                    }

                    break;
                }

                case "700x300": {
                    if (feedTypes.contains(MojiWeather.FeedType.FEED_TYPE_5)) {
                        data.setFeed_type(MojiWeather.FeedType.FEED_TYPE_5);
                    }

                    break;
                }

                default: {
                    break;
                }
            }
        }

        data.setAdtitle(StringUtil.toString(mediaResponse.getTitle()));
        data.setAdtext(StringUtil.toString(mediaResponse.getDesc()));
        data.setClickurl(StringUtil.toString(mediaResponse.getLpgurl()));
        
        if (mediaResponse.getMonitorBuilder() != null) {
            if (ObjectUtils.isNotEmpty(mediaResponse.getMonitorBuilder().getImpurl())) {
                List<Track> imps = mediaResponse.getMonitorBuilder().getImpurl();
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < imps.size() - 1; ++i) {
                    sb.append(imps.get(i).getUrl()).append(data.getUrlSeparator());
                }
                sb.append(imps.get(imps.size() - 1).getUrl());
                data.setImptrack(sb.toString());
            }

            if (ObjectUtils.isNotEmpty(mediaResponse.getMonitorBuilder().getClkurl())) {
                List<String> clks = mediaResponse.getMonitorBuilder().getClkurl();
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < clks.size() - 1; ++i) {
                    sb.append(clks.get(i)).append(data.getUrlSeparator());
                }
                sb.append(clks.get(clks.size() - 1));
                data.setClktrack(sb.toString());
            }
        }

        moWeatherBidResponse.setData(data);
        return moWeatherBidResponse;
    }
    
}
