package com.madhouse.media.mojiweather;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.madhouse.ssp.avro.*;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.madhouse.cache.MediaBidMetaData;
import com.madhouse.media.MediaBaseHandler;
import com.madhouse.ssp.Constant;
import com.madhouse.ssp.avro.MediaRequest.Builder;
import com.madhouse.util.ObjectUtils;
import com.madhouse.util.StringUtil;

public class MojiWeatherHandler extends MediaBaseHandler {
    
    @Override
    public boolean parseMediaRequest(HttpServletRequest req, MediaBidMetaData mediaBidMetaData, HttpServletResponse resp) {
        
        try {
            MojiWeatherBidRequest mojiWeatherBidRequest = JSON.parseObject(getRequestParamsString(req), MojiWeatherBidRequest.class);
            logger.info("MojiWeather Request params is : {}",JSON.toJSONString(mojiWeatherBidRequest));
            int status = validateRequiredParam(mojiWeatherBidRequest);
            if (status == Constant.StatusCode.OK) {
                MediaRequest mediaRequest = conversionToPremiumMADDataModel(mojiWeatherBidRequest); 
                mediaBidMetaData.getMediaBidBuilder().setRequest(mediaRequest);
                mediaBidMetaData.setRequestObject(mojiWeatherBidRequest);
                return true;
            } else {
                MojiWeatherResponse moWeatherResponse=convertToMojiWeatherResponse(Constant.StatusCode.BAD_REQUEST,mediaBidMetaData,(MojiWeatherBidRequest)mediaBidMetaData.getRequestObject());
                outputStreamWrite(resp, moWeatherResponse);
                return false;
            }
        } catch (Exception e) {
            logger.error(e.toString() + "_Status_" + Constant.StatusCode.BAD_REQUEST);
            resp.setStatus(Constant.StatusCode.BAD_REQUEST);
            return false;
        }
    }
    
    private MediaRequest conversionToPremiumMADDataModel(MojiWeatherBidRequest mojiWeatherBidRequest) {
        MediaRequest.Builder mediaRequest = MediaRequest.newBuilder();
        //广告请求流水号
        mediaRequest.setAdspacekey(mojiWeatherBidRequest.getAdid());
        //每次请求的唯一标识
        mediaRequest.setBid(mojiWeatherBidRequest.getSessionid());
        mediaRequest.setBundle(mojiWeatherBidRequest.getPkgname());
        mediaRequest.setName(mojiWeatherBidRequest.getAppname());
        mediaRequest.setBidfloor(mojiWeatherBidRequest.getBasic_price() == null ? 0 : mojiWeatherBidRequest.getBasic_price());
        mediaRequest.setAdtype(2);   
        mediaRequest.setDevicetype(Constant.DeviceType.UNKNOWN);
        mediaRequest.setCarrier(mojiWeatherBidRequest.getCarrier());
        switch (mojiWeatherBidRequest.getOs()) {
            case MojiWeatherStatusCode.MojiWeatherOs.OS_ANDROID:
                mediaRequest.setOs(Constant.OSType.ANDROID);
                mediaRequest.setDid(mojiWeatherBidRequest.getImei());
                mediaRequest.setDpid(mojiWeatherBidRequest.getAndid());
                mediaRequest.setIfa(mojiWeatherBidRequest.getAndaid());
                break;
            case MojiWeatherStatusCode.MojiWeatherOs.OS_IOS:
                mediaRequest.setOs(Constant.OSType.IOS);
                mediaRequest.setIfa(mojiWeatherBidRequest.getIdfa());
                mediaRequest.setDpid(mojiWeatherBidRequest.getOpenudid());
                break;
            case MojiWeatherStatusCode.MojiWeatherOs.OS_WINDOWS_PHONE:
                mediaRequest.setOs(Constant.OSType.WINDOWS_PHONE);
                mediaRequest.setDpid(mojiWeatherBidRequest.getUnqid());
                break;
            case MojiWeatherStatusCode.MojiWeatherOs.OS_OTHERS:
                mediaRequest.setOs(Constant.OSType.UNKNOWN);
                mediaRequest.setDpid(mojiWeatherBidRequest.getUnqid());
                break;
        };
        if(!StringUtils.isEmpty(String.valueOf(mojiWeatherBidRequest.getNet()))){
            mediaRequest.setConnectiontype(mojiWeatherBidRequest.getNet());
        }else {
            mediaRequest.setConnectiontype(Constant.ConnectionType.CELL);
        }
        //操作系统的版本
        if(!StringUtils.isEmpty(mojiWeatherBidRequest.getOsv())){
            mediaRequest.setOsv(mojiWeatherBidRequest.getOsv());
        }
        if(!StringUtils.isEmpty(mojiWeatherBidRequest.getIp())){
            mediaRequest.setIp(mojiWeatherBidRequest.getIp());
        }
        if(!StringUtils.isEmpty(mojiWeatherBidRequest.getUa())){
            mediaRequest.setUa(mojiWeatherBidRequest.getUa());
        }
        if (mojiWeatherBidRequest.getAdtype() == 3) {
            mediaRequest.setW(mojiWeatherBidRequest.getScrwidth());
            mediaRequest.setH(mojiWeatherBidRequest.getScrheight());
        } else {
            mediaRequest.setW(0);
            mediaRequest.setH(0);
        }
        if (!StringUtils.isEmpty(mojiWeatherBidRequest.getWma())) {
            mediaRequest.setMac(mojiWeatherBidRequest.getWma());
        }
        if (!StringUtils.isEmpty(mojiWeatherBidRequest.getDevice())) {
            mediaRequest.setModel(mojiWeatherBidRequest.getDevice());
        }

        if(!StringUtils.isEmpty(mojiWeatherBidRequest.getLon()) && !StringUtils.isEmpty(mojiWeatherBidRequest.getLat())){
            Geo.Builder geo = Geo.newBuilder();
            //经度
            geo.setLon(Float.parseFloat(mojiWeatherBidRequest.getLon()));
            //纬度
            geo.setLat(Float.parseFloat(mojiWeatherBidRequest.getLat()));
            mediaRequest.setGeoBuilder(geo);
        }

        mediaRequest.setType(Constant.MediaType.APP);
        logger.info("mojiWeather convert mediaRequest is :{}", JSON.toJSONString(mediaRequest));
        return mediaRequest.build();
    }

    private int validateRequiredParam(MojiWeatherBidRequest mojiWeatherBidRequest) {
        if (!ObjectUtils.isEmpty(mojiWeatherBidRequest)) {
            /**
             * 判断所有必填参数
             */
            String adid = mojiWeatherBidRequest.getAdid();
            if (StringUtils.isEmpty(adid)) {
                logger.debug("adid is missing");
                return Constant.StatusCode.BAD_REQUEST;
            }
            if (StringUtils.isEmpty(mojiWeatherBidRequest.getSessionid())) {
                logger.debug("{}:Sessionid is missing", adid);
                return Constant.StatusCode.BAD_REQUEST;
            }
            if (mojiWeatherBidRequest.getAdtype() == null || "" == mojiWeatherBidRequest.getAdtype().toString()) {
                logger.debug("{}:Adtype is missing", adid);
                return Constant.StatusCode.BAD_REQUEST;
            } else if (mojiWeatherBidRequest.getAdtype() < 1 || mojiWeatherBidRequest.getAdtype() > 3) {
                logger.debug("{}:Adtype content is missing", adid);
                return Constant.StatusCode.BAD_REQUEST;
            }
            if (mojiWeatherBidRequest.getTradelevel() == null) {
                logger.debug("{}:Tradelevel is missing", adid);
                return Constant.StatusCode.BAD_REQUEST;
            } else if (mojiWeatherBidRequest.getTradelevel() < 1 || mojiWeatherBidRequest.getTradelevel() > 3) {
                logger.debug("{}:Tradelevel content is missing", adid);
                return Constant.StatusCode.BAD_REQUEST;
            }
            if (StringUtils.isEmpty(mojiWeatherBidRequest.getPkgname())) {
                logger.debug("{}:Pkgname is missing", adid);
                return Constant.StatusCode.BAD_REQUEST;
            }
            if (StringUtils.isEmpty(mojiWeatherBidRequest.getAppname())) {
                logger.debug("{}:Appname is missing", adid);
                return Constant.StatusCode.BAD_REQUEST;
            }
            if (mojiWeatherBidRequest.getNet() == null) {
                logger.debug("{}:Net is missing", adid);
                return Constant.StatusCode.BAD_REQUEST;
            } else if (mojiWeatherBidRequest.getNet() < 0 || mojiWeatherBidRequest.getNet() > 4) {
                logger.debug("{}:Net content is missing", adid);
                return Constant.StatusCode.BAD_REQUEST;
            }
            
            if (mojiWeatherBidRequest.getCarrier() == null) {
                logger.debug("{}:Carrier is missing", adid);
                return Constant.StatusCode.BAD_REQUEST;
            } else if (mojiWeatherBidRequest.getCarrier() < 0 || mojiWeatherBidRequest.getCarrier() > 3) {
                logger.debug("{}:Carrier content is missing", adid);
                return Constant.StatusCode.BAD_REQUEST;
            }
            if (StringUtils.isEmpty(mojiWeatherBidRequest.getOs())) {
                logger.debug("{}:Os is missing", adid);
                return Constant.StatusCode.BAD_REQUEST;
            }
            if (StringUtils.isEmpty(mojiWeatherBidRequest.getOsv())) {
                logger.debug("{}:Osv is missing", adid);
                return Constant.StatusCode.BAD_REQUEST;
            }
            if (mojiWeatherBidRequest.getBasic_price() == null) {
                logger.debug("{}:Basic_price is missing", adid);
                return Constant.StatusCode.BAD_REQUEST;
            }
            if (StringUtils.isEmpty(mojiWeatherBidRequest.getDevice())) {
                logger.debug("{}:Device is missing", adid);
                return Constant.StatusCode.BAD_REQUEST;
            }
            if (StringUtils.isEmpty(mojiWeatherBidRequest.getIp())) {
                logger.debug("{}:Ip is missing", adid);
                return Constant.StatusCode.BAD_REQUEST;
            }
            if (mojiWeatherBidRequest.getAdtype() == 3) {
                if (mojiWeatherBidRequest.getScrwidth() == 0) {
                    logger.debug("{}:Adtype_3 Scrwidth is missing", adid);
                    return Constant.StatusCode.BAD_REQUEST;
                }
                if (mojiWeatherBidRequest.getScrheight() == 0) {
                    logger.debug("{}:Adtype_3 Scrheight is missing", adid);
                    return Constant.StatusCode.BAD_REQUEST;
                }
            } else {
                if (mojiWeatherBidRequest.getAdstyle() == null) {
                    logger.debug("{}:Adstyle is missing", adid);
                    return Constant.StatusCode.BAD_REQUEST;
                } else if (!MojiWeatherADStyle.contains(mojiWeatherBidRequest.getAdstyle())) {
                    logger.debug("{}:Adstyle content is missing", adid);
                    return Constant.StatusCode.BAD_REQUEST;
                }
            }
            if(!StringUtils.isEmpty(mojiWeatherBidRequest.getFeed_Support_Types())){
                if (!hasFeedType(mojiWeatherBidRequest.getFeed_Support_Types())) {
                    logger.debug("{}:Feed_Support_Types content is missing", adid);
                    return Constant.StatusCode.BAD_REQUEST;
                }
            }
            return Constant.StatusCode.OK;
        }
        return Constant.StatusCode.BAD_REQUEST;
    }
    
    private String getRequestParamsString(HttpServletRequest req) {
        Map<String, String[]> map = req.getParameterMap();
        Map<String, String> params = new HashMap<>(50);// 保存request中的参数，过滤掉为null的参数,并将key转成小写
        for (Map.Entry<String, String[]> entry : map.entrySet()) {
            if (entry.getValue() != null && entry.getValue().length >= 1 && entry.getValue()[0] != null && !entry.getValue()[0].equals("")) {
                params.put(entry.getKey(), entry.getValue()[0]);
            }
        }
        
        return JSON.toJSONString(params);
    }
    private boolean hasFeedType(String feedtype){
        boolean isfeedtype = true;
        String[] splfeedtype = feedtype.split(";");
        for(int i = 0; i < splfeedtype.length; i++){
            if(!splfeedtype[i].equalsIgnoreCase("3") && !splfeedtype[i].equalsIgnoreCase("5")&&!splfeedtype[i].equalsIgnoreCase("6")){
                isfeedtype = false;
                break;
            }
        }
        return isfeedtype;
    }
    @Override
    public boolean packageMediaResponse(MediaBidMetaData mediaBidMetaData, HttpServletResponse resp) {
        resp.setStatus(Constant.StatusCode.OK);
        if (mediaBidMetaData != null && mediaBidMetaData.getMediaBidBuilder() != null) {
            MediaBid.Builder mediaBid = mediaBidMetaData.getMediaBidBuilder();
            MojiWeatherResponse moWeatherResponse =new MojiWeatherResponse();
            if (mediaBid.getResponseBuilder() != null && mediaBid.getStatus() == Constant.StatusCode.OK) {
                moWeatherResponse=convertToMojiWeatherResponse(Constant.StatusCode.OK,mediaBidMetaData,(MojiWeatherBidRequest)mediaBidMetaData.getRequestObject());
            } else if( mediaBid.getStatus() == Constant.StatusCode.NO_CONTENT){
                moWeatherResponse=convertToMojiWeatherResponse(MojiWeatherStatusCode.StatusCode.CODE_400,mediaBidMetaData,(MojiWeatherBidRequest)mediaBidMetaData.getRequestObject());
            } else if( mediaBid.getStatus() == Constant.StatusCode.BAD_REQUEST){
                moWeatherResponse=convertToMojiWeatherResponse(MojiWeatherStatusCode.StatusCode.CODE_402,mediaBidMetaData,(MojiWeatherBidRequest)mediaBidMetaData.getRequestObject());
            } else {
                moWeatherResponse=convertToMojiWeatherResponse(MojiWeatherStatusCode.StatusCode.CODE_501,mediaBidMetaData,(MojiWeatherBidRequest)mediaBidMetaData.getRequestObject());
            }
            logger.info("MojiWeather Response params is : {}", JSON.toJSONString(moWeatherResponse));
            outputStreamWrite(resp,moWeatherResponse);
            return true;
        }
        return false;
    }
    private boolean outputStreamWrite(HttpServletResponse resp, MojiWeatherResponse moWeatherResponse)  {
        try {
            resp.setHeader("Content-Type", "application/json; charset=utf-8");
            resp.getOutputStream().write(JSON.toJSONString(moWeatherResponse).getBytes());
        } catch (Exception e) {
            logger.error(e.toString() + "_Status_" + Constant.StatusCode.NO_CONTENT);
            return false;
        }
        logger.debug("outputStreamWrite is:{}",moWeatherResponse.toString());
        return true;
    }
    private MojiWeatherResponse convertToMojiWeatherResponse(int status, MediaBidMetaData mediaBidMetaData, MojiWeatherBidRequest moWeatherRequest) {
        
        MojiWeatherResponse moWeatherBidResponse = new MojiWeatherResponse();
        MojiWeatherResponse.data data = new MojiWeatherResponse.data();
        if(!StringUtils.isEmpty(moWeatherRequest.getAdid())){
            data.setAdid(moWeatherRequest.getAdid());
        }
        if(!StringUtils.isEmpty(moWeatherRequest.getAdid())){
            data.setSessionid(moWeatherRequest.getSessionid());
        }
        if(!StringUtils.isEmpty(String.valueOf(moWeatherRequest.getAdtype()))){
            data.setAdtype(String.valueOf(moWeatherRequest.getAdtype()));
        }
        if(!StringUtils.isEmpty(String.valueOf(moWeatherRequest.getAdstyle()))){
            data.setAdstyle(String.valueOf(moWeatherRequest.getAdstyle()));
        }
        
        if(status == Constant.StatusCode.OK){
            MediaResponse.Builder mediaResponse= mediaBidMetaData.getMediaBidBuilder().getResponseBuilder();
            MojiWeatherBidRequest bidRequest= (MojiWeatherBidRequest)mediaBidMetaData.getRequestObject();
            Builder mediaRequest= mediaBidMetaData.getMediaBidBuilder().getRequestBuilder();
            moWeatherBidResponse.setCode(Constant.StatusCode.OK);
            //在底价上加一分
            data.setPrice(String.valueOf(bidRequest.getBasic_price()+1));
            data.setChargingtype("1");
            
            //判断是否信息流，2视频 或1 图文
            if(null != mediaResponse.getDuration() && mediaResponse.getDuration()>0){
                data.setType("2");
                if(null!=mediaResponse.getCover()){
                    data.setVedioimg(mediaResponse.getCover());
                }
                data.setVedioPlaytime(mediaResponse.getDuration());
                if(null!=mediaResponse.getAdm() && mediaResponse.getAdm().size()>0){
                    if(null!=mediaResponse.getAdm().get(0)){
                        data.setVediourl(mediaResponse.getAdm().get(0));
                    }
                }
            }else{
                if(null!=mediaResponse.getAdm() && mediaResponse.getAdm().size()>0){
                    if(null!=mediaResponse.getAdm().get(0)){
                        data.setImgurl(mediaResponse.getAdm().get(0));
                    }
                }
                data.setType("1");
            }
            data.setClickurl(mediaResponse.getLpgurl() != null ? mediaResponse.getLpgurl() : "");
            data.setAdwidth(StringUtil.toString(mediaRequest.getW().toString()));
            data.setAdheight(StringUtil.toString(mediaRequest.getH().toString()));
            data.setAdtitle(StringUtil.toString(mediaResponse.getTitle()));
            data.setAdtext(StringUtil.toString(mediaResponse.getDesc()));
            data.setUrlSeparator(";");
            //点击监播
            StringBuffer sb_clk = new StringBuffer();
            for (Track clk : mediaResponse.getMonitorBuilder().getImpurl()) {
                sb_clk.append(clk.getUrl()).append(data.getUrlSeparator());
            }
            if (sb_clk.length() > 1) {
                sb_clk = sb_clk.delete(sb_clk.length()-data.getUrlSeparator().length(),sb_clk.length());
            }
            data.setClktrack(sb_clk.toString());

            //展示监播
            StringBuffer sb_imp = new StringBuffer();
            for (String imp : mediaResponse.getMonitorBuilder().getClkurl()) {
                sb_imp.append(imp).append(data.getUrlSeparator());
            }
            if (sb_imp.length() > 1) {
                sb_imp = sb_imp.delete(sb_imp.length() - data.getUrlSeparator().length(),sb_imp.length());
            }
            data.setImptrack(sb_imp.toString());
            
        } else {
            moWeatherBidResponse.setCode(status);
            if(StringUtils.isNotEmpty(moWeatherRequest.getFeed_Support_Types())){
                /*data.setFeed_type(AdspaceCache.getSupplierAdspaceKey(new StringBuffer().append(moWeatherRequest.getAdid())
                        .append(":").append(moWeatherRequest.getOs()).toString()));*/
            }
        }
        moWeatherBidResponse.setData(data);
        return moWeatherBidResponse;
    }
    
}
