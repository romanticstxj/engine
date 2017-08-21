package com.madhouse.media.madhouse;

import java.util.LinkedList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.madhouse.cache.MediaBidMetaData;
import com.madhouse.media.MediaBaseHandler;
import com.madhouse.ssp.Constant;
import com.madhouse.ssp.avro.MediaBid;
import com.madhouse.ssp.avro.MediaRequest;
import com.madhouse.ssp.avro.MediaRequest.Builder;
import com.madhouse.ssp.avro.MediaResponse;
import com.madhouse.ssp.avro.Track;
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
            logger.info("PremiumMAD Request params is : {}",JSON.toJSONString(mediaRequest));
            int status =  validateRequiredParam(mediaRequest);
            if(Constant.StatusCode.OK == status){
                MediaRequest.Builder request = conversionToPremiumMADDataModel(mediaRequest);
                mediaBidMetaData.getMediaBidBuilder().setRequestBuilder(request);
                mediaBidMetaData.setRequestObject(request);
                return true;
            } else {
                PremiumMADResponse premiumMADResponse = new PremiumMADResponse();
                premiumMADResponse.setAdspaceid(mediaRequest.getAdspaceid());
                premiumMADResponse.setReturncode(String.valueOf(status));
                return outputStreamWrite(premiumMADResponse,resp);
            }
        } catch (Exception e) {
            logger.error(e.toString() + "_Status_" + Constant.StatusCode.BAD_REQUEST);
            resp.setStatus(Constant.StatusCode.BAD_REQUEST);
            return false;
        }
    }
    /** 
    * TODO (这里用一句话描述这个方法的作用)
    * @param madBidRequest
    * @return
    */
    private Builder conversionToPremiumMADDataModel(PremiumMADBidRequest madBidRequest) {
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
                mediaRequest.setDpid(madBidRequest.getOid());
                break;
            case PremiumMADStatusCode.PremiumMadOs.OS_WINDOWS_PHONE:
                mediaRequest.setOs(Constant.OSType.WINDOWS_PHONE);
                mediaRequest.setDpid(madBidRequest.getUid());
                break;
            default:
                mediaRequest.setOs(Constant.OSType.UNKNOWN);
                mediaRequest.setDpid(madBidRequest.getUid());
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
        String mac = madBidRequest.getWma();
        if(!StringUtils.isEmpty(mac)){
            mediaRequest.setOsv(mac);
        }
        //操作系统的版本
        if(!StringUtils.isEmpty(madBidRequest.getOsv())){
            mediaRequest.setOsv(madBidRequest.getOsv());
        }
        if(!StringUtils.isEmpty(madBidRequest.getIp())){
            mediaRequest.setIp(madBidRequest.getIp());
        }
        if(!StringUtils.isEmpty(madBidRequest.getUa())){
            mediaRequest.setUa(madBidRequest.getUa());
        }
        //设备型号
        
        if(!StringUtils.isEmpty(madBidRequest.getDevice())){
            mediaRequest.setModel(madBidRequest.getDevice());
        }
        // 手机号码。
        if(!StringUtils.isEmpty(madBidRequest.getCell())){
            mediaRequest.setCell(madBidRequest.getCell());
        }
        //MD5加密的手机号码。
        if(!StringUtils.isEmpty(madBidRequest.getMcell())){
            mediaRequest.setCellmd5(madBidRequest.getMcell());
        }
        //纬度
        if(!StringUtils.isEmpty(madBidRequest.getLat())){
            mediaRequest.setLat(Float.parseFloat(madBidRequest.getLat()));
        }
        //经度
        if(!StringUtils.isEmpty(madBidRequest.getLon())){
            mediaRequest.setLon(Float.parseFloat(madBidRequest.getLon()));
        }
        //PDB、PD模式的deal id
        if(!StringUtils.isEmpty(madBidRequest.getDealid())){
            mediaRequest.setDealid(madBidRequest.getDealid());
        }
        //投放的媒体形式
        if(!StringUtils.isEmpty(madBidRequest.getMedia())){
            mediaRequest.setType(Integer.parseInt(madBidRequest.getMedia()));
        }
        logger.info("PremiumMAD convert mediaRequest is : {}", JSON.toJSONString(mediaRequest));
        return mediaRequest;
    }
    private int validateRequiredParam(PremiumMADBidRequest mediaRequest) {

        if (ObjectUtils.isNotEmpty(mediaRequest)) {
            // 必填参数
            String bid = mediaRequest.getBid();
            if (StringUtils.isEmpty(bid)) {
                logger.debug("bid is missing");
                return Constant.StatusCode.BAD_REQUEST;
            }
            String adspaceid = mediaRequest.getAdspaceid();
            if (StringUtils.isEmpty(adspaceid)) {
                logger.debug("adspaceid is missing");
                return Constant.StatusCode.BAD_REQUEST;
            }
    
            String conn = mediaRequest.getConn();
            if (StringUtils.isEmpty(conn)) {
                logger.debug("{}:conn is missing", adspaceid);
                return Constant.StatusCode.BAD_REQUEST;
            }
            try {
                Integer.parseInt(conn); //是否合法
            } catch (Exception e) {
                logger.debug("{}:osv is not correct _Ex", adspaceid);
                return Constant.StatusCode.BAD_REQUEST;
            }
            String width = mediaRequest.getWidth();
            if (StringUtils.isEmpty(width)) {
                logger.debug("{}:width is missing", adspaceid);
                return Constant.StatusCode.BAD_REQUEST;
            }
            try {
                int w = Integer.parseInt(width); //是否合法
                if (w < 1) {
                    logger.debug("{}:width is not correct", adspaceid);
                    return Constant.StatusCode.BAD_REQUEST;
                }
            } catch (Exception e) {
                logger.debug("{}:width is not correct _Ex", adspaceid);
                return Constant.StatusCode.BAD_REQUEST;
            }
    
            String height = mediaRequest.getHeight();
            if (StringUtils.isEmpty(height)) {
                logger.debug("{}:height is missing", height);
                return Constant.StatusCode.BAD_REQUEST;
            }
    
            try {
                int h = Integer.parseInt(height); //是否合法
                if (h < 1) {
                    logger.debug("{}:width is not correct", adspaceid);
                    return Constant.StatusCode.BAD_REQUEST;
                }
            } catch (Exception e) {
                logger.debug("{}:height is not correct_Ex", adspaceid);
                return Constant.StatusCode.BAD_REQUEST;
            }
    
            String adtype = mediaRequest.getAdtype();
            if (StringUtils.isEmpty(adtype)) {
                logger.debug("{}:adtype is missing", adtype);
                return Constant.StatusCode.BAD_REQUEST;
            }
            try {
                Integer.parseInt(adtype); //adtype是数字
            } catch (Exception e) {
                logger.debug("{}:adtype is not correct", adtype);
                return Constant.StatusCode.BAD_REQUEST;
            }
    
            String pkgname = mediaRequest.getPkgname();
            if (StringUtils.isEmpty(pkgname)) {
                logger.debug("{}:pkgname is missing", adspaceid);
                return Constant.StatusCode.BAD_REQUEST;
            }
            String appname = mediaRequest.getAppname();
            if (StringUtils.isEmpty(appname)) {
                logger.debug("{}:appname is missing", adspaceid);
                return Constant.StatusCode.BAD_REQUEST;
            }
    
            String carrier = mediaRequest.getCarrier();
            if (StringUtils.isEmpty(carrier)) {
                logger.debug("{}:carrier is missing", adspaceid);
                return Constant.StatusCode.BAD_REQUEST;
            }
            try {
                Integer.parseInt(carrier); //是否合法
            } catch (Exception e) {
                logger.debug("{}:carrier is not correct", adspaceid);
                return Constant.StatusCode.BAD_REQUEST;
            }
            String os = mediaRequest.getOs();
            if (StringUtils.isEmpty(os)) {
                logger.debug("{}:os is missing", adspaceid);
                return Constant.StatusCode.BAD_REQUEST;
            }
            try {
                Integer.parseInt(os); //是否合法
            } catch (Exception e) {
                logger.debug("{}:os is not correct", adspaceid);
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
                        logger.debug("{}:android params is missing", adspaceid);
                        return Constant.StatusCode.BAD_REQUEST;
                    }
                    break;
                case PremiumMADStatusCode.PremiumMadOs.OS_IOS:// ios必填参数
                    //String wma_ios = mediaRequest.getWma();StringUtils.isEmpty(wma_ios) &&
                    String idfa = mediaRequest.getIdfa();
                    String oid = mediaRequest.getOid();
                    boolean flag_ios =  StringUtils.isEmpty(idfa) && StringUtils.isEmpty(oid);
                    if (flag_ios) {
                        logger.debug("{}:ios params is missing", adspaceid);
                        return Constant.StatusCode.BAD_REQUEST;
                    }
                    break;
            }
            String osv = mediaRequest.getOsv();
            if (StringUtils.isEmpty(osv)) {
                logger.debug("{}:osv is missing", adspaceid);
                return Constant.StatusCode.BAD_REQUEST;
            }
            String ua = mediaRequest.getUa();
            if (StringUtils.isEmpty(ua)) {
                logger.debug("{}:ua is missing", adspaceid);
                return Constant.StatusCode.BAD_REQUEST;
            }
            String ip = mediaRequest.getIp();
            if (StringUtils.isEmpty(ip)) {
                logger.debug("{}:ip is missing", adspaceid);
                return Constant.StatusCode.BAD_REQUEST;
            }
            String pid = mediaRequest.getPid();
            if (StringUtils.isEmpty(pid)) {
                logger.debug("{}:pid is missing", adspaceid);
                return Constant.StatusCode.BAD_REQUEST;
            }
            String media = mediaRequest.getMedia();
            if (StringUtils.isEmpty(media)) {
                logger.debug("{}:media is missing", adspaceid);
                return Constant.StatusCode.BAD_REQUEST;
            }
            String pcat = mediaRequest.getPcat();
            if (StringUtils.isEmpty(pcat)) {
                logger.debug("{}:pcat is missing", adspaceid);
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
        logger.debug(sb.toString());
        return false;
    }
    
    
    private PremiumMADResponse convertToPremiumMADResponse(MediaBidMetaData mediaBidMetaData, int status) {
        PremiumMADResponse premiumMADResponse = new PremiumMADResponse();
        if(Constant.StatusCode.OK == status){
            MediaResponse mediaResponse= mediaBidMetaData.getMediaBidBuilder().getResponse();
            premiumMADResponse.setReturncode(String.valueOf(Constant.StatusCode.OK));
            premiumMADResponse.setAdspaceid(mediaBidMetaData.getMediaBidBuilder().getRequest().getAdspacekey());
            premiumMADResponse.setBid(mediaBidMetaData.getMediaBidBuilder().getRequest().getBid());
            premiumMADResponse.setCid(mediaResponse.getCid());
            premiumMADResponse.setAdwidth(String.valueOf(mediaBidMetaData.getMediaBidBuilder().getRequest().getW()));
            premiumMADResponse.setAdheight(String.valueOf(mediaBidMetaData.getMediaBidBuilder().getRequest().getH()));
            
            if(mediaResponse.getDuration() > 0){
                premiumMADResponse.setDuration(String.valueOf(mediaResponse.getDuration()));
                premiumMADResponse.setIcon(mediaResponse.getIcon());
                premiumMADResponse.setCover(mediaResponse.getCover());
            }
            if (mediaResponse.getTitle() != null) {
                premiumMADResponse.setDisplaytitle(mediaResponse.getTitle());
            }

            if (mediaResponse.getDesc() != null) {
                premiumMADResponse.setDisplaytext(mediaResponse.getDesc());
            }

            if (mediaResponse.getAdm() != null && !mediaResponse.getAdm().isEmpty()) {
                premiumMADResponse.setImgurl( mediaResponse.getAdm().get(0));
            }

            premiumMADResponse.setAdm(mediaResponse.getAdm());

            premiumMADResponse.setClickurl(mediaResponse.getLpgurl());
            // 点击监播
            premiumMADResponse.setImgtracking(new LinkedList<>());
            for (Track track : mediaResponse.getMonitor().getImpurl()) {
                premiumMADResponse.getImgtracking().add(track.getUrl());
            }
            //点击监播地址
            premiumMADResponse.setThclkurl(mediaResponse.getMonitor().getClkurl());
            
            //品牌安全监测
            premiumMADResponse.setSecurl(mediaResponse.getMonitor().getSecurl());
            //PDB、PD模式的deal id
            premiumMADResponse.setDealid(StringUtils.isEmpty(mediaResponse.getDealid())?null:mediaResponse.getDealid());
            
        } else {
            premiumMADResponse.setAdspaceid(mediaBidMetaData.getMediaBidBuilder().getRequestBuilder().getAdspacekey());
            premiumMADResponse.setReturncode(String.valueOf(status));
        }
        logger.info("premiumMAD Response params is : {}", JSON.toJSONString(premiumMADResponse));
        return premiumMADResponse;
    }
}
