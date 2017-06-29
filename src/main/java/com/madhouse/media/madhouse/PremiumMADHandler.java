package com.madhouse.media.madhouse;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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

/**
 * Created by WUJUNFENG on 2017/5/23.
 */
public class PremiumMADHandler extends MediaBaseHandler {
    @Override
    public boolean parseMediaRequest(HttpServletRequest req, MediaBidMetaData mediaBidMetaData, HttpServletResponse resp) {
        
        PremiumMADBidRequest mediaRequest = new PremiumMADBidRequest();
        
        try {
            BeanUtils.populate(mediaRequest, req.getParameterMap());
            int status =  validateRequiredParam(mediaRequest);
            if(Constant.StatusCode.OK != status){
                PremiumMADResponse premiumMADResponse = new PremiumMADResponse();
                premiumMADResponse.setAdspaceid(mediaRequest.getAdspaceid());
                premiumMADResponse.setReturncode(String.valueOf(status));
                return outputStreamWrite(premiumMADResponse,resp);
            } else {
                MediaRequest request = conversionToPremiumMADDataModel(mediaRequest);
                mediaBidMetaData.getMediaBidBuilder().setRequest(request);
                mediaBidMetaData.setRequestObject(request);
                return true;
            }
        } catch (Exception e) {
            logger.error(e.toString() + "_Status_" + Constant.StatusCode.BAD_REQUEST);
            resp.setStatus(Constant.StatusCode.BAD_REQUEST);
            return false;
        }
    }
    private MediaRequest conversionToPremiumMADDataModel(PremiumMADBidRequest madBidRequest) {
        MediaRequest.Builder mediaRequest = MediaRequest.newBuilder();
        //广告请求流水号
        mediaRequest.setBid(madBidRequest.getBid());
        //广告位标识
        mediaRequest.setAdspacekey(madBidRequest.getAdspaceid());
        mediaRequest.setBundle(madBidRequest.getPkgname());
        //应用程序名称
        mediaRequest.setName(madBidRequest.getAppname());
        mediaRequest.setAdtype(Integer.parseInt(madBidRequest.getAdtype()));        
        mediaRequest.setW(Integer.parseInt(madBidRequest.getWidth()));
        mediaRequest.setH(Integer.parseInt(madBidRequest.getHeight()));
        //媒体类型
        mediaRequest.setCategory(Integer.parseInt(madBidRequest.getPcat()));
        switch (madBidRequest.getOs()) {
            case PremiumMADStatusCode.PremiumMadOs.OS_ANDROID:
                mediaRequest.setOs(Constant.OSType.ANDROID);
                mediaRequest.setDid(madBidRequest.getImei());
                mediaRequest.setDpid(madBidRequest.getAid());
                mediaRequest.setIfa(madBidRequest.getAaid());
                break;
            case PremiumMADStatusCode.PremiumMadOs.OS_IOS:
                mediaRequest.setOs(Constant.OSType.IOS);
                mediaRequest.setIfa(madBidRequest.getIdfa());
                break;
            case PremiumMADStatusCode.PremiumMadOs.OS_WINDOWS_PHONE:
                mediaRequest.setOs(Constant.OSType.WINDOWS_PHONE);
                break;
            default:
                mediaRequest.setOs(Constant.OSType.UNKNOWN);
                break;
        }
        /**
         * 1   移动
         * 2   联通
         * 3   电信
         */
        mediaRequest.setCarrier(Integer.parseInt(madBidRequest.getCarrier()));
        //连网方式
        mediaRequest.setConnectiontype(Integer.parseInt(madBidRequest.getConn()));
        //设备类型
        mediaRequest.setDevicetype(Integer.parseInt(madBidRequest.getDevicetype()));
       
        //操作系统的版本
        mediaRequest.setOsv(madBidRequest.getOsv());
        mediaRequest.setIp(madBidRequest.getIp());
        mediaRequest.setUa(madBidRequest.getUa());
        //设备型号
        
        if(StringUtils.isEmpty(madBidRequest.getDevice())){
            mediaRequest.setModel(madBidRequest.getDevice());
        }
        // 手机号码。
        if(StringUtils.isEmpty(madBidRequest.getCell())){
            mediaRequest.setCell(madBidRequest.getCell());
        }
        //MD5加密的手机号码。
        if(StringUtils.isEmpty(madBidRequest.getMcell())){
            mediaRequest.setCellmd5(madBidRequest.getMcell());
        }
        //经度
        if(StringUtils.isEmpty(madBidRequest.getLon())){
            mediaRequest.setLon(Float.parseFloat(madBidRequest.getLon()));
        }
        //纬度
        if(StringUtils.isEmpty(madBidRequest.getLat())){
            mediaRequest.setLat(Float.parseFloat(madBidRequest.getLat()));
        }
        logger.info("PremiumMAD Request params is : {}", JSON.toJSONString(mediaRequest));
        return mediaRequest.build();
    }
    private int validateRequiredParam(PremiumMADBidRequest mediaRequest) {

        if (ObjectUtils.isEmpty(mediaRequest)) {
            // 必填参数
            String bid = mediaRequest.getBid();
            if (StringUtils.isEmpty(bid)) {
                logger.info("bid is missing");
                return Constant.StatusCode.BAD_REQUEST;
            }
            String adspaceid = mediaRequest.getAdspaceid();
            if (StringUtils.isEmpty(adspaceid)) {
                logger.info("adspaceid is missing");
                return Constant.StatusCode.BAD_REQUEST;
            }
    
            String conn = mediaRequest.getConn();
            if (StringUtils.isEmpty(conn)) {
                logger.info("{}:conn is missing", adspaceid);
                return Constant.StatusCode.BAD_REQUEST;
            }
            try {
                Integer.parseInt(conn); //是否合法
            } catch (Exception e) {
                logger.info("{}:osv is not correct _Ex", adspaceid);
                return Constant.StatusCode.BAD_REQUEST;
            }
            String width = mediaRequest.getWidth();
            if (StringUtils.isEmpty(width)) {
                logger.info("{}:width is missing", adspaceid);
                return Constant.StatusCode.BAD_REQUEST;
            }
            try {
                int w = Integer.parseInt(width); //是否合法
                if (w < 1) {
                    logger.info("{}:width is not correct", adspaceid);
                    return Constant.StatusCode.BAD_REQUEST;
                }
            } catch (Exception e) {
                logger.info("{}:width is not correct _Ex", adspaceid);
                return Constant.StatusCode.BAD_REQUEST;
            }
    
            String height = mediaRequest.getHeight();
            if (StringUtils.isEmpty(height)) {
                logger.info("{}:height is missing", height);
                return Constant.StatusCode.BAD_REQUEST;
            }
    
            try {
                int h = Integer.parseInt(height); //是否合法
                if (h < 1) {
                    logger.info("{}:width is not correct", adspaceid);
                    return Constant.StatusCode.BAD_REQUEST;
                }
            } catch (Exception e) {
                logger.info("{}:height is not correct_Ex", adspaceid);
                return Constant.StatusCode.BAD_REQUEST;
            }
    
            String adtype = mediaRequest.getAdtype();
            if (StringUtils.isEmpty(adtype)) {
                logger.info("{}:adtype is missing", adtype);
                return Constant.StatusCode.BAD_REQUEST;
            }
            try {
                Integer.parseInt(adtype); //adtype是数字
            } catch (Exception e) {
                logger.info("{}:adtype is not correct", adtype);
                return Constant.StatusCode.BAD_REQUEST;
            }
    
            String pkgname = mediaRequest.getPkgname();
            if (StringUtils.isEmpty(pkgname)) {
                logger.info("{}:pkgname is missing", adspaceid);
                return Constant.StatusCode.BAD_REQUEST;
            }
            String appname = mediaRequest.getAppname();
            if (StringUtils.isEmpty(appname)) {
                logger.info("{}:appname is missing", adspaceid);
                return Constant.StatusCode.BAD_REQUEST;
            }
    
            String carrier = mediaRequest.getCarrier();
            if (StringUtils.isEmpty(carrier)) {
                logger.info("{}:carrier is missing", adspaceid);
                return Constant.StatusCode.BAD_REQUEST;
            }
            try {
                Integer.parseInt(carrier); //是否合法
            } catch (Exception e) {
                logger.info("{}:carrier is not correct", adspaceid);
                return Constant.StatusCode.BAD_REQUEST;
            }
            String os = mediaRequest.getOs();
            if (StringUtils.isEmpty(os)) {
                logger.info("{}:os is missing", adspaceid);
                return Constant.StatusCode.BAD_REQUEST;
            }
            try {
                Integer.parseInt(os); //是否合法
            } catch (Exception e) {
                logger.info("{}:os is not correct", adspaceid);
                return Constant.StatusCode.BAD_REQUEST;
            }
            switch (os) {
                case PremiumMADStatusCode.PremiumMadOs.OS_ANDROID:// android必填参数
                    String imei = mediaRequest.getImei();
                    //String wma = mediaRequest.getWma();&& StringUtils.isEmpty(wma)
                    String aid = mediaRequest.getAid();
                    String aaid = mediaRequest.getAaid();
                    boolean flag = StringUtils.isEmpty(imei)  && StringUtils.isEmpty(aid) && StringUtils.isEmpty(aaid) ;
                    if (flag) {
                        logger.info("{}:android params is missing", adspaceid);
                        return Constant.StatusCode.BAD_REQUEST;
                    }
                    break;
                case PremiumMADStatusCode.PremiumMadOs.OS_IOS:// ios必填参数
                    //String wma_ios = mediaRequest.getWma();StringUtils.isEmpty(wma_ios) &&
                    String idfa = mediaRequest.getIdfa();
                    String oid = mediaRequest.getOid();
                    boolean flag_ios =  StringUtils.isEmpty(idfa) && StringUtils.isEmpty(oid);
                    if (flag_ios) {
                        logger.info("{}:ios params is missing", adspaceid);
                        return Constant.StatusCode.BAD_REQUEST;
                    }
                    break;
            }
            String osv = mediaRequest.getOsv();
            if (StringUtils.isEmpty(osv)) {
                logger.info("{}:osv is missing", adspaceid);
                return Constant.StatusCode.BAD_REQUEST;
            }
            try {
                Integer.parseInt(osv); //是否合法
            } catch (Exception e) {
                logger.info("{}:osv is not correct _EX", adspaceid);
                return Constant.StatusCode.BAD_REQUEST;
            }
            String ua = mediaRequest.getUa();
            if (StringUtils.isEmpty(ua)) {
                logger.info("{}:ua is missing", adspaceid);
                return Constant.StatusCode.BAD_REQUEST;
            }
            String ip = mediaRequest.getIp();
            if (StringUtils.isEmpty(ip)) {
                logger.info("{}:ip is missing", adspaceid);
                return Constant.StatusCode.BAD_REQUEST;
            }
            String pid = mediaRequest.getPid();
            if (StringUtils.isEmpty(pid)) {
                logger.info("{}:pid is missing", adspaceid);
                return Constant.StatusCode.BAD_REQUEST;
            }
            String media = mediaRequest.getMedia();
            if (StringUtils.isEmpty(media)) {
                logger.info("{}:media is missing", adspaceid);
                return Constant.StatusCode.BAD_REQUEST;
            }
            String pcat = mediaRequest.getPcat();
            if (StringUtils.isEmpty(pcat)) {
                logger.info("{}:pcat is missing", adspaceid);
                return Constant.StatusCode.BAD_REQUEST;
            }
            return Constant.StatusCode.OK;
        }
        return Constant.StatusCode.BAD_REQUEST;
    }
    @Override
    public boolean packageMediaResponse(MediaBidMetaData mediaBidMetaData, HttpServletResponse resp) {
        resp.setStatus(Constant.StatusCode.OK);
        if (mediaBidMetaData != null && mediaBidMetaData.getMediaBidBuilder() != null) {
            MediaBid.Builder mediaBid = mediaBidMetaData.getMediaBidBuilder();
            PremiumMADResponse premiumMADResponse=new PremiumMADResponse();
            if (mediaBid.getResponseBuilder() != null && mediaBid.getStatus() == Constant.StatusCode.OK) {
                premiumMADResponse = convertToPremiumMADResponse(mediaBidMetaData,Constant.StatusCode.OK);
                if (premiumMADResponse != null) {
                    outputStreamWrite(premiumMADResponse,resp);
                    logger.debug(Constant.StatusCode.OK);
                    return true;
                }
            } else if (mediaBid.getStatus() == Constant.StatusCode.NO_CONTENT){
                premiumMADResponse = convertToPremiumMADResponse(mediaBidMetaData,PremiumMADStatusCode.StatusCode.CODE_405);
                return outputStreamWrite(premiumMADResponse,resp);
            } else if (mediaBid.getStatus() == Constant.StatusCode.REQUEST_TIMEOUT){
                premiumMADResponse = convertToPremiumMADResponse(mediaBidMetaData,PremiumMADStatusCode.StatusCode.CODE_502);
                return outputStreamWrite(premiumMADResponse,resp);
            } else if (mediaBid.getStatus() == Constant.StatusCode.INTERNAL_ERROR){
                premiumMADResponse = convertToPremiumMADResponse(mediaBidMetaData,PremiumMADStatusCode.StatusCode.CODE_501);
                return outputStreamWrite(premiumMADResponse,resp);
            }
            
        }
        return true;
    }
    private boolean outputStreamWrite(PremiumMADResponse premiumMADResponse, HttpServletResponse resp)  {
        StringBuilder sb = new StringBuilder();
        sb.append("{")
            .append("\"")
            .append(premiumMADResponse.getAdspaceid())
            .append("\":")
            .append(JSON.toJSONString(premiumMADResponse))
            .append("}");
        try {
            resp.getOutputStream().write(sb.toString().getBytes("utf-8"));
        } catch (Exception e) {
            logger.error(e.toString() + "_Status_" + Constant.StatusCode.NO_CONTENT);
            return false;
        }
        logger.info(sb.toString());
        return true;
    }
    
    
    private PremiumMADResponse convertToPremiumMADResponse(MediaBidMetaData mediaBidMetaData, int status) {
        PremiumMADResponse premiumMADResponse = new PremiumMADResponse();
        if(Constant.StatusCode.OK == status){
            MediaResponse mediaResponse= mediaBidMetaData.getMediaBidBuilder().getResponse();
            premiumMADResponse.setReturncode(String.valueOf(Constant.StatusCode.OK));
            premiumMADResponse.setAdspaceid(mediaBidMetaData.getMediaBidBuilder().getRequest().getAdspacekey().toString());
            premiumMADResponse.setBid(mediaBidMetaData.getMediaBidBuilder().getRequest().getBid().toString());
            premiumMADResponse.setCid(mediaResponse.getCid().toString());
            premiumMADResponse.setAdwidth(String.valueOf(mediaBidMetaData.getMediaBidBuilder().getRequest().getW()));
            premiumMADResponse.setAdheight(String.valueOf(mediaBidMetaData.getMediaBidBuilder().getRequest().getH()));
            
            if(mediaResponse.getDuration() > 0){
                premiumMADResponse.setDuration(String.valueOf(mediaResponse.getDuration()));
                premiumMADResponse.setIcon(mediaResponse.getIcon().toString());
                premiumMADResponse.setCover(mediaResponse.getCover().toString());
            }
            if (mediaResponse.getTitle() != null) {
                premiumMADResponse.setDisplaytitle(mediaResponse.getTitle().toString());
            }

            if (mediaResponse.getDesc() != null) {
                premiumMADResponse.setDisplaytext(mediaResponse.getDesc().toString());
            }

            if (mediaResponse.getAdm() != null && !mediaResponse.getAdm().isEmpty()) {
                premiumMADResponse.setImgurl( mediaResponse.getAdm().get(0).toString());
            }

            premiumMADResponse.setAdm(new LinkedList<>());
            for (CharSequence adm : mediaResponse.getAdm()) {
                premiumMADResponse.getAdm().add(adm.toString());
            }

            premiumMADResponse.setClickurl(mediaResponse.getLpgurl().toString());
            // 点击监播
            premiumMADResponse.setImgtracking(new LinkedList<>());
            for (Track track : mediaResponse.getMonitor().getImpurl()) {
                premiumMADResponse.getImgtracking().add(track.getUrl().toString());
            }
            //点击监播地址
            premiumMADResponse.setThclkurl(new LinkedList<>());
            for (CharSequence url : mediaResponse.getMonitor().getClkurl()) {
                premiumMADResponse.getThclkurl().add(url.toString());
            }
            //品牌安全监测
            premiumMADResponse.setSecurl(new LinkedList<>());
            for (CharSequence url : mediaResponse.getMonitor().getSecurl()) {
                premiumMADResponse.getSecurl().add(url.toString());
            }
        } else {
            premiumMADResponse.setAdspaceid(mediaBidMetaData.getMediaBidBuilder().getRequest().getAdspacekey().toString());
            premiumMADResponse.setReturncode(String.valueOf(status));
        }
        logger.info("premiumMAD Response params is : {}", JSON.toJSONString(premiumMADResponse));
        return premiumMADResponse;
    }
}
