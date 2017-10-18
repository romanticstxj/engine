package com.madhouse.media.madhouse;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.http.HttpStatus;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.madhouse.cache.MediaBidMetaData;
import com.madhouse.media.MediaBaseHandler;
import com.madhouse.ssp.Constant;
import com.madhouse.ssp.avro.Geo;
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
        PremiumMADRequest mediaRequest = new PremiumMADRequest();
        PremiumMADResponse premiumMADResponse = new PremiumMADResponse();
        try {
            BeanUtils.populate(mediaRequest, req.getParameterMap());
            logger.info("PremiumMAD Request params is : {}",JSON.toJSONString(mediaRequest));
            int status =  validateRequiredParam(mediaRequest);
            premiumMADResponse.setAdspaceid(mediaRequest.getAdspaceid());
            premiumMADResponse.setReturncode(HttpStatus.NOT_ACCEPTABLE_406);
            if(Constant.StatusCode.OK == status){
                MediaRequest.Builder request = conversionToPremiumMADDataModel(mediaRequest);
                if(request != null){
                    mediaBidMetaData.getMediaBidBuilder().setRequestBuilder(request);
                    mediaBidMetaData.setRequestObject(request);
                    return true;
                }
            }
            return outputStreamWrite(premiumMADResponse,resp);
        } catch (Exception e) {
            logger.error(e.toString() + "_Status_" + Constant.StatusCode.BAD_REQUEST);
            resp.setStatus(Constant.StatusCode.BAD_REQUEST);
            return outputStreamWrite(premiumMADResponse,resp);
        }
    }
    /** 
    * TODO (这里用一句话描述这个方法的作用)
    * @param madBidRequest
    * @return
    */
    private Builder conversionToPremiumMADDataModel(PremiumMADRequest madBidRequest) {
        MediaRequest.Builder mediaRequest = MediaRequest.newBuilder();
        //广告请求流水号
        mediaRequest.setBid(madBidRequest.getBid());
        //广告位标识
        mediaRequest.setAdspacekey(madBidRequest.getAdspaceid());
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

                if (madBidRequest.getImei().length() > 20) {
                    mediaRequest.setDidmd5(madBidRequest.getImei());
                } else {
                    mediaRequest.setDid(madBidRequest.getImei());
                }

                if (!StringUtils.isEmpty(madBidRequest.getAid()) && madBidRequest.getAid().length() > 20) {
                    mediaRequest.setDpidmd5(madBidRequest.getAid());
                } else {
                    mediaRequest.setDpid(madBidRequest.getAid());
                }

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
        if (StringUtils.isEmpty(madBidRequest.getDevicetype())) {
            mediaRequest.setDevicetype(Constant.DeviceType.UNKNOWN);
        } else {
            mediaRequest.setDevicetype(Integer.parseInt(madBidRequest.getDevicetype()));
        }

        String mac = madBidRequest.getWma();
        if(!StringUtils.isEmpty(mac)){
            mediaRequest.setMac(mac);
        }
        //操作系统的版本
        if(!StringUtils.isEmpty(madBidRequest.getOsv())){
            mediaRequest.setOsv(madBidRequest.getOsv());
        }
        if(!StringUtils.isEmpty(madBidRequest.getIp())){
            mediaRequest.setIp(madBidRequest.getIp());
        }
        // 手机号码。
        if(!StringUtils.isEmpty(madBidRequest.getCell())){
            mediaRequest.setCell(madBidRequest.getCell());
        }
        //MD5加密的手机号码。
        if(!StringUtils.isEmpty(madBidRequest.getMcell())){
            mediaRequest.setCellmd5(madBidRequest.getMcell());
        }

        if(!StringUtils.isEmpty(madBidRequest.getLat()) && !StringUtils.isEmpty(madBidRequest.getLon())) {
            Geo.Builder geo = Geo.newBuilder();
            //纬度
            geo.setLat(Float.parseFloat(madBidRequest.getLat()));
            //经度
            geo.setLon(Float.parseFloat(madBidRequest.getLon()));
            mediaRequest.setGeoBuilder(geo);
        }

        //PDB、PD模式的deal id
        if(!StringUtils.isEmpty(madBidRequest.getDealid())){
            mediaRequest.setDealid(madBidRequest.getDealid());
        }
        
        if(!StringUtils.isEmpty(madBidRequest.getLabel())){
            try{
                String labels = URLDecoder.decode(madBidRequest.getLabel());
                JSONArray jsonArray = JSON.parseArray(labels);
                List<String> list = new ArrayList<>();
                for(int i=0;i<jsonArray.size();i++){
                    String label = jsonArray.getString(i);
                    list.add(label);
                }
                mediaRequest.setTags(list);
            } catch(Exception e){
                logger.warn("{}:Label parsing error :",madBidRequest.getLabel());
                return null;
            }
        }
        //设备型号
        if(!StringUtils.isEmpty(madBidRequest.getDevice())){
            try{
                String device = URLDecoder.decode(madBidRequest.getDevice());
                mediaRequest.setMake(device);
                mediaRequest.setModel(device);
            } catch(Exception e){
                logger.warn("{}:Device parsing error :",madBidRequest.getDevice());
                return null;
            }
        }
        if(!StringUtils.isEmpty(madBidRequest.getUa())){
            try{
                String ua = URLDecoder.decode(madBidRequest.getUa(), "utf-8");
                mediaRequest.setUa(ua);
            } catch(Exception e){
                logger.warn("{}:ua parsing error :",madBidRequest.getUa());
                return null;
            }
        } else {
            mediaRequest.setUa("");
        }

        if(!StringUtils.isEmpty(madBidRequest.getAppname())){
            try{
                String appName = URLDecoder.decode(madBidRequest.getAppname());
                mediaRequest.setBundle(appName);
            } catch(Exception e){
                logger.warn("{}:ua appName error :",madBidRequest.getAppname());
                return null;
            }
        }
        logger.info("PremiumMAD convert mediaRequest is : {}", JSON.toJSONString(mediaRequest));
        return mediaRequest;
    }
    private int validateRequiredParam(PremiumMADRequest mediaRequest) {

        if (ObjectUtils.isNotEmpty(mediaRequest)) {
            // 必填参数
            String bid = mediaRequest.getBid();
            if (StringUtils.isEmpty(bid)) {
                logger.warn("bid is missing");
                return Constant.StatusCode.BAD_REQUEST;
            }
            String adspaceid = mediaRequest.getAdspaceid();
            if (StringUtils.isEmpty(adspaceid)) {
                logger.warn("adspaceid is missing");
                return Constant.StatusCode.BAD_REQUEST;
            }
            /*String devicetype = mediaRequest.getDevicetype();
            if (StringUtils.isEmpty(devicetype)) {
                logger.warn("{}:devicetype is missing", adspaceid);
                return Constant.StatusCode.BAD_REQUEST;
            }
            try {
                Integer.parseInt(devicetype); //是否合法
            } catch (Exception e) {
                logger.warn("{}:devicetype is not correct _Ex", adspaceid);
                return Constant.StatusCode.BAD_REQUEST;
            }*/
            String conn = mediaRequest.getConn();
            if (StringUtils.isEmpty(conn)) {
                logger.warn("{}:conn is missing", adspaceid);
                return Constant.StatusCode.BAD_REQUEST;
            }
            try {
                if (conn.equals("unknown")) {
                    mediaRequest.setConn("0");
                    logger.warn("{}:conn is not correct", adspaceid);
                } else {
                    Integer.parseInt(conn); //是否合法
                }
            } catch (Exception e) {
                logger.warn("{}:conn is not correct", adspaceid);
                return Constant.StatusCode.BAD_REQUEST;
            }
            String width = mediaRequest.getWidth();
            if (StringUtils.isEmpty(width)) {
                logger.warn("{}:width is missing", adspaceid);
                return Constant.StatusCode.BAD_REQUEST;
            }
            try {
                int w = Integer.parseInt(width); //是否合法
                if (w < 1) {
                    logger.warn("{}:width is not correct", adspaceid);
                    return Constant.StatusCode.BAD_REQUEST;
                }
            } catch (Exception e) {
                logger.warn("{}:width is not correct _Ex", adspaceid);
                return Constant.StatusCode.BAD_REQUEST;
            }
    
            String height = mediaRequest.getHeight();
            if (StringUtils.isEmpty(height)) {
                logger.warn("{}:height is missing", height);
                return Constant.StatusCode.BAD_REQUEST;
            }
    
            try {
                int h = Integer.parseInt(height); //是否合法
                if (h < 1) {
                    logger.warn("{}:width is not correct", adspaceid);
                    return Constant.StatusCode.BAD_REQUEST;
                }
            } catch (Exception e) {
                logger.warn("{}:height is not correct_Ex", adspaceid);
                return Constant.StatusCode.BAD_REQUEST;
            }
    
            String adtype = mediaRequest.getAdtype();
            if (StringUtils.isEmpty(adtype)) {
                logger.warn("{}:adtype is missing", adtype);
                return Constant.StatusCode.BAD_REQUEST;
            }
            try {
                Integer.parseInt(adtype); //adtype是数字
            } catch (Exception e) {
                logger.warn("{}:adtype is not correct", adtype);
                return Constant.StatusCode.BAD_REQUEST;
            }
    
            String pkgname = mediaRequest.getPkgname();
            if (StringUtils.isEmpty(pkgname)) {
                logger.warn("{}:pkgname is missing", adspaceid);
                return Constant.StatusCode.BAD_REQUEST;
            }
            String appname = mediaRequest.getAppname();
            if (StringUtils.isEmpty(appname)) {
                logger.warn("{}:appname is missing", adspaceid);
                return Constant.StatusCode.BAD_REQUEST;
            }
    
            String carrier = mediaRequest.getCarrier();
            if (StringUtils.isEmpty(carrier)) {
                logger.warn("{}:carrier is missing", adspaceid);
                return Constant.StatusCode.BAD_REQUEST;
            }
            try {
                if (carrier.equals("unknown")) {
                    mediaRequest.setCarrier("0");
                    logger.warn("{}:carrier is not correct", adspaceid);
                } else {
                    Integer.parseInt(carrier); //是否合法
                }
            } catch (Exception e) {
                logger.warn("{}:carrier is not correct", adspaceid);
                return Constant.StatusCode.BAD_REQUEST;
            }
            String os = mediaRequest.getOs();
            if (StringUtils.isEmpty(os)) {
                logger.warn("{}:os is missing", adspaceid);
                return Constant.StatusCode.BAD_REQUEST;
            }
            try {
                Integer.parseInt(os); //是否合法
            } catch (Exception e) {
                logger.warn("{}:os is not correct", adspaceid);
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
                        logger.warn("{}:android params is missing", adspaceid);
                        return Constant.StatusCode.BAD_REQUEST;
                    }
                    break;
                case PremiumMADStatusCode.PremiumMadOs.OS_IOS:// ios必填参数
                    //String wma_ios = mediaRequest.getWma();StringUtils.isEmpty(wma_ios) &&
                    String idfa = mediaRequest.getIdfa();
                    String oid = mediaRequest.getOid();
                    boolean flag_ios =  StringUtils.isEmpty(idfa) && StringUtils.isEmpty(oid);
                    if (flag_ios) {
                        logger.warn("{}:ios params is missing", adspaceid);
                        return Constant.StatusCode.BAD_REQUEST;
                    }
                    break;
            }
            String osv = mediaRequest.getOsv();
            if (StringUtils.isEmpty(osv)) {
                logger.warn("{}:osv is missing", adspaceid);
                return Constant.StatusCode.BAD_REQUEST;
            }
            String ua = mediaRequest.getUa();
            if (StringUtils.isEmpty(ua)) {
                logger.warn("{}:ua is missing", adspaceid);
                //return Constant.StatusCode.BAD_REQUEST;
            }
            String ip = mediaRequest.getIp();
            if (StringUtils.isEmpty(ip)) {
                logger.warn("{}:ip is missing", adspaceid);
                return Constant.StatusCode.BAD_REQUEST;
            }
            String pid = mediaRequest.getPid();
            if (StringUtils.isEmpty(pid)) {
                logger.warn("{}:pid is missing", adspaceid);
                return Constant.StatusCode.BAD_REQUEST;
            }
            String pcat = mediaRequest.getPcat();
            if (StringUtils.isEmpty(pcat)) {
                logger.warn("{}:pcat is missing", adspaceid);
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
            if (mediaBid.hasResponseBuilder() && mediaBid.getStatus() == Constant.StatusCode.OK) {
                premiumMADResponse = convertToPremiumMADResponse(mediaBidMetaData,Constant.StatusCode.OK);
                if (premiumMADResponse != null) {
                    outputStreamWrite(premiumMADResponse,resp);
                    return true;
                }
            } else if (mediaBid.getStatus() == Constant.StatusCode.REQUEST_TIMEOUT){
                premiumMADResponse = convertToPremiumMADResponse(mediaBidMetaData,PremiumMADStatusCode.StatusCode.CODE_502);
                return outputStreamWrite(premiumMADResponse,resp);
            } else if (mediaBid.getStatus() == Constant.StatusCode.INTERNAL_ERROR){
                premiumMADResponse = convertToPremiumMADResponse(mediaBidMetaData,PremiumMADStatusCode.StatusCode.CODE_501);
                return outputStreamWrite(premiumMADResponse,resp);
            } else {
                premiumMADResponse = convertToPremiumMADResponse(mediaBidMetaData,PremiumMADStatusCode.StatusCode.CODE_405);
                return outputStreamWrite(premiumMADResponse,resp);
            } 
            
        }
        return false;
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
            resp.setStatus(premiumMADResponse.getReturncode());
            resp.getOutputStream().write(sb.toString().getBytes("utf-8"));
            resp.setHeader("Content-Type", "application/json; charset=utf-8");
        } catch (Exception e) {
            logger.error(e.toString() + "_Status_" + Constant.StatusCode.NO_CONTENT);
            return false;
        }
        logger.info("PremiumMAD Response:"+sb.toString());
        return false;
    }
    
    
    private PremiumMADResponse convertToPremiumMADResponse(MediaBidMetaData mediaBidMetaData, int status) {
        PremiumMADResponse premiumMADResponse = new PremiumMADResponse();
        if(Constant.StatusCode.OK == status){
            MediaRequest.Builder mediaRequest = mediaBidMetaData.getMediaBidBuilder().getRequestBuilder();
            MediaResponse.Builder mediaResponse= mediaBidMetaData.getMediaBidBuilder().getResponseBuilder();

            premiumMADResponse.setReturncode(Constant.StatusCode.OK);
            premiumMADResponse.setAdtype(mediaRequest.getAdtype());
            premiumMADResponse.setAdspaceid(mediaRequest.getAdspacekey());
            premiumMADResponse.setBid(mediaRequest.getBid());
            premiumMADResponse.setBidid(mediaBidMetaData.getMediaBidBuilder().getImpid());
            premiumMADResponse.setCid(mediaResponse.getCid());
            premiumMADResponse.setCrid(mediaResponse.getCrid());
            premiumMADResponse.setAdwidth(mediaRequest.getW());
            premiumMADResponse.setAdheight(mediaRequest.getH());
            
            premiumMADResponse.setIcon(mediaResponse.getIcon());
            premiumMADResponse.setCover(mediaResponse.getCover());
            
            if(mediaResponse.getDuration() > 0){
                premiumMADResponse.setDuration(mediaResponse.getDuration());
            }

            if (mediaResponse.getTitle() != null) {
                premiumMADResponse.setDisplaytitle(mediaResponse.getTitle());
            }

            if (mediaResponse.getDesc() != null) {
                premiumMADResponse.setDisplaytext(mediaResponse.getDesc());
            }
            
            if (!StringUtils.isEmpty(mediaResponse.getContent())) {
                premiumMADResponse.setDisplaycontent(mediaResponse.getContent());
            }

            if (mediaResponse.getAdm() != null && !mediaResponse.getAdm().isEmpty()) {
                premiumMADResponse.setImgurl( mediaResponse.getAdm().get(0));
            }

            premiumMADResponse.setAdm(mediaResponse.getAdm());

            premiumMADResponse.setClickurl(mediaResponse.getLpgurl());
            // 点击监播
            premiumMADResponse.setImgtracking(new LinkedList<>());
            for (Track track : mediaResponse.getMonitorBuilder().getImpurl()) {
                premiumMADResponse.getImgtracking().add(track.getUrl());
            }
            //点击监播地址
            premiumMADResponse.setThclkurl(mediaResponse.getMonitorBuilder().getClkurl());
            
            //品牌安全监测
            premiumMADResponse.setSecurl(mediaResponse.getMonitorBuilder().getSecurl());
            //PDB、PD模式的deal id
            premiumMADResponse.setDealid(StringUtils.isEmpty(mediaRequest.getDealid()) ? null : mediaRequest.getDealid());
            
        } else {
            premiumMADResponse.setAdspaceid(mediaBidMetaData.getMediaBidBuilder().getRequestBuilder().getAdspacekey());
            premiumMADResponse.setReturncode(status);
        }
        return premiumMADResponse;
    }
}
