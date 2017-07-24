package com.madhouse.dsp.iflytek;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.madhouse.cache.AdBlockMetaData;
import com.madhouse.cache.CacheManager;
import com.madhouse.cache.DSPBidMetaData;
import com.madhouse.cache.DSPMappingMetaData;
import com.madhouse.cache.MediaMetaData;
import com.madhouse.cache.PlcmtMetaData;
import com.madhouse.cache.PolicyMetaData;
import com.madhouse.dsp.DSPBaseHandler;
import com.madhouse.ssp.Constant;
import com.madhouse.ssp.avro.DSPResponse;
import com.madhouse.ssp.avro.MediaRequest;
import com.madhouse.ssp.avro.Monitor;
import com.madhouse.ssp.avro.Track;
import com.madhouse.ssp.avro.MediaBid.Builder;

public class IflytekHandler extends DSPBaseHandler {
    
    @Override
    public HttpRequestBase packageBidRequest(Builder mediaBidBuilder, MediaMetaData mediaMetaData, PlcmtMetaData plcmtMetaData, AdBlockMetaData adBlockMetaData, PolicyMetaData policyMetaData,
        DSPBidMetaData dspBidMetaData) {
        IflytekRequest iflytek = new IflytekRequest();
        MediaRequest.Builder builder=  mediaBidBuilder.getRequestBuilder();
        HttpPost request = new HttpPost(dspBidMetaData.getDspMetaData().getBidUrl());
        try {
            DSPMappingMetaData dspMappingMetaData = CacheManager.getInstance().getDSPMapping(dspBidMetaData.getDspMetaData().getId(), plcmtMetaData.getId());
            
            String adspaceId = plcmtMetaData.getAdspaceKey();
            if (dspBidMetaData != null && !StringUtils.isEmpty(dspMappingMetaData.getMappingKey())) {
                adspaceId = dspMappingMetaData.getMappingKey();
            }
            iflytek.setAdunitid(adspaceId);
            iflytek.setAppid(adspaceId);
            
            switch (builder.getOs()) {
            case Constant.OSType.ANDROID:
                iflytek.setOs(IflytekStatusCode.Os.OS_ANDROID);
                iflytek.setAdid(builder.getDpid());
                iflytek.setAaid(builder.getIfa());
                iflytek.setImei(builder.getDid());
                break;
            case Constant.OSType.IOS:
                iflytek.setOs(IflytekStatusCode.Os.OS_IOS);
                iflytek.setOpenudid(builder.getDpid());
                iflytek.setIdfa(builder.getIfa());
                break;
            case Constant.OSType.WINDOWS_PHONE:
                iflytek.setOs(IflytekStatusCode.Os.OS_WINDOWS_PHONE);
                break;
            case Constant.OSType.UNKNOWN:
                iflytek.setOs(IflytekStatusCode.Os.OS_OTHERS);
                break;
            default:
                iflytek.setOs(IflytekStatusCode.Os.OS_OTHERS);
                break;
            }
            
            iflytek.setOsv(builder.getOsv());
            iflytek.setDensity("1");
            switch (builder.getCarrier()) {
                case Constant.Carrier.CHINA_MOBILE:
                    iflytek.setOperator(IflytekStatusCode.Carrier.CHINA_MOBILE);
                    break;
                case Constant.Carrier.CHINA_UNICOM:
                    iflytek.setOperator(IflytekStatusCode.Carrier.CHINA_UNICOM);
                    break;
                case Constant.Carrier.CHINA_TELECOM:
                    iflytek.setOperator(IflytekStatusCode.Carrier.CHINA_TELECOM);
                    break;
                default:
                    break;
            }
            //联网类型(0—未知，
            //1—Ethernet，2—wifi，
            //3—蜂窝网络，未知代， 4—，2G，5—蜂窝网络，3G，6—蜂窝网络，4G) 
            switch (builder.getConnectiontype()) {
                case Constant.ConnectionType.UNKNOWN:
                    iflytek.setNet(IflytekStatusCode.ConnectionType.UNKNOWN);
                    break;
                case Constant.ConnectionType.ETHERNET:
                    iflytek.setNet(IflytekStatusCode.ConnectionType.ETHERNET);
                    break;
                case Constant.ConnectionType._2G:
                    iflytek.setNet(IflytekStatusCode.ConnectionType._2G);
                    break;
                case Constant.ConnectionType._3G:
                    iflytek.setNet(IflytekStatusCode.ConnectionType._3G);
                    break;
                case Constant.ConnectionType._4G:
                    iflytek.setNet(IflytekStatusCode.ConnectionType._4G);
                    break;
                default:
                    iflytek.setNet(IflytekStatusCode.ConnectionType.UNKNOWN);
                    break;
                }
            iflytek.setIp(builder.getIp());
            if (!StringUtils.isEmpty(builder.getUa())) {
                iflytek.setUa(StringEscapeUtils.escapeJava(builder.getUa()));
            }
            Long date = new Date().getTime();
            iflytek.setTs(date.toString());
            
            iflytek.setAdw(builder.getW() > 0 ? String.valueOf(builder.getW()) :String.valueOf(plcmtMetaData.getW()));
            iflytek.setAdh(builder.getH() > 0 ? String.valueOf(builder.getH()) : String.valueOf(plcmtMetaData.getH()));
            iflytek.setDvh("480");
            iflytek.setDvw("320");
            //横竖屏
            iflytek.setOrientation("0");
            //设备生产商 
            iflytek.setVendor(builder.getMake());
            //设备型号 
            iflytek.setModel(builder.getModel());
            //目前使用的语言
            iflytek.setLan("zh-CN");
            //设备类型  Y       -1-未知 
            //0   - phone 
            //1   - pad 
            //2   - pc 
            //3   - tv 
            //4   - wap 
            switch (builder.getDevicetype()) {
                case Constant.DeviceType.UNKNOWN:
                    iflytek.setDevicetype(IflytekStatusCode.DeviceType.UNKNOWN);
                    break;
                case Constant.DeviceType.PHONE:
                    iflytek.setDevicetype(IflytekStatusCode.DeviceType.PHONE);
                    break;
                case Constant.DeviceType.PAD:
                    iflytek.setDevicetype(IflytekStatusCode.DeviceType.PAD);
                    break;
                case Constant.DeviceType.TV:
                    iflytek.setDevicetype(IflytekStatusCode.DeviceType.TV);
                    break;
                case Constant.DeviceType.COMPUTER:
                    iflytek.setDevicetype(IflytekStatusCode.DeviceType.PC);
                    break;
                default:
                    iflytek.setDevicetype(IflytekStatusCode.DeviceType.UNKNOWN);
                    break;
            }
            //是否开屏
            iflytek.setIsboot("0");
            //请求批量下发广告的数量
            iflytek.setBatch_cnt("1");
            //下游需要的普通广告的物料格式(只对普通广告生效)，在物料下发时候一次请求只会下发其中的一种格式的物料 json 或者 html 
            iflytek.setTramaterialtype("json");
            // mac 地址
            iflytek.setMac(builder.getMac());
            
            iflytek.setAppname(builder.getName());
            iflytek.setPkgname(builder.getBundle());
            if (!checkParameter(iflytek)) {
                return null;
            }
            request.setHeader(HTTP.CONTENT_TYPE, HTTP.OCTET_STREAM_TYPE);
            request.setHeader("X-protocol-ver", IflytekStatusCode.X_PROTOCOL_VER);
            request.setHeader("Accept-Encoding", "none");
            if(plcmtMetaData.isEnableHttps()){
                request.setHeader("X-USING-HTTPS", "YES");
            }
            String json = JSONObject.toJSONString(iflytek);
            logger.info("Iflytek request params:{}",json);
            StringEntity entity  = new StringEntity(json);
            request.setEntity(entity);
        } catch (Exception e) {
            logger.error("Iflytek request error：{}",e.toString());
            return null;
        }
        return request;
    }
    
    /** 
    * TODO (这里用一句话描述这个覆盖方法的作用)
    * @param httpResponse
    * @param dspBidMetaData
    * @return
    */
    @Override
    public boolean parseBidResponse(HttpResponse httpResponse, DSPBidMetaData dspBidMetaData) {
        DSPResponse.Builder dspResponse = DSPResponse.newBuilder();
        try {
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                logger.warn("Iflytek no ad reson:{}", statusCode);
                return false;
            }
            String result = EntityUtils.toString(httpResponse.getEntity(),"utf-8");
            IflytekResponse iflytekResponse = JSON.parseObject(result, IflytekResponse.class);
            if (StringUtils.isEmpty(iflytekResponse.getRc())
                    || !iflytekResponse.getRc().equals(IflytekStatusCode.IFLYTEK_SUCESS_CODE)) {
                logger.warn("iflytek return the ad is 'no fill':{}", iflytekResponse.getRc());
                return false;
            }
            dspResponse.setId(String.valueOf(dspBidMetaData.getDspBidBuilder().getRequest().getId()));
            dspResponse.setImpid(dspBidMetaData.getDspBidBuilder().getRequestBuilder().getImpid());
            String[] imprurl = null;
            String[] thclkurl = null;
            if (null != iflytekResponse.getBatch_ma() && iflytekResponse.getBatch_ma().size() > 0) {
                dspResponse.setLpgurl(iflytekResponse.getBatch_ma().get(0).getLanding_url());
                dspResponse.setTitle(iflytekResponse.getBatch_ma().get(0).getTitle());
                dspResponse.setDesc(iflytekResponse.getBatch_ma().get(0).getSub_title());
                List<String> list = new ArrayList<String>();
                list.add(iflytekResponse.getBatch_ma().get(0).getImage());
                dspResponse.setAdm(list);
                imprurl = iflytekResponse.getBatch_ma().get(0).getImpr_url();
                thclkurl = iflytekResponse.getBatch_ma().get(0).getClick_url();
            } else {
                dspResponse.setLpgurl(iflytekResponse.getLanding_url());
                imprurl = iflytekResponse.getImpr_url();
                thclkurl = iflytekResponse.getClick_url();
            }
            Monitor.Builder monitor = Monitor.newBuilder();
            if (imprurl != null && imprurl.length > 0) {
                List<Track> tracks=new ArrayList<>();
                for (String track : imprurl) {
                    tracks.add(new Track(0, track));
                }
                monitor.setImpurl(tracks);
            }
            if (thclkurl != null && thclkurl.length > 0) {
                for (String thcl : thclkurl) {
                    monitor.getClkurl().add(thcl);
                }
            }
            dspResponse.setMonitorBuilder(monitor);
            dspBidMetaData.getDspBidBuilder().setStatus(Constant.StatusCode.OK);
            dspBidMetaData.getDspBidBuilder().setResponse(dspResponse.build());
            logger.info("Iflytek Response is:{}",result);
            return true;
        } catch (Exception e) {
            logger.error("Iflytek Response :{}", dspBidMetaData.getDspBidBuilder().toString());
            dspBidMetaData.getDspBidBuilder().setStatus(Constant.StatusCode.INTERNAL_ERROR);
            return false;
        }
    }
    public boolean checkParameter(IflytekRequest iflytek) {
        StringBuffer msg = new StringBuffer();
        boolean istrue = true;
        msg.append("iflytek's error -->Missing request parameters：");
        if (StringUtils.isEmpty(iflytek.getAdunitid())) {
            msg.append("adunitid=" + iflytek.getAdunitid() + ";");
            istrue = false;
        }
        if (StringUtils.isEmpty(iflytek.getDevicetype())) {
            msg.append("devicetype=" + iflytek.getDevicetype() + ";");
            istrue = false;
        }
        if (StringUtils.isEmpty(iflytek.getOs())) {
            msg.append("os=" + iflytek.getOs() + ";");
            istrue = false;
        }
        if (StringUtils.isEmpty(iflytek.getOsv())) {
            msg.append("osv=" + iflytek.getOsv() + ";");
            istrue = false;
        }
        if (iflytek.getOs() == "iOS" && StringUtils.isEmpty(iflytek.getOpenudid())) {
            msg.append("openudid=" + iflytek.getOpenudid() + ";");
            istrue = false;
        }
        if (iflytek.getOs() == "Android" && StringUtils.isEmpty(iflytek.getAdid())) {
            msg.append("adid=" + iflytek.getAdid() + ";");
            istrue = false;
        }
        if (iflytek.getOs() == "Android" && StringUtils.isEmpty(iflytek.getImei())) {
            msg.append("imei=" + iflytek.getImei() + ";");
            istrue = false;
        }
        if (iflytek.getOs() == "iOS" && StringUtils.isEmpty(iflytek.getIdfa())) {
            msg.append("idfa=" + iflytek.getIdfa() + ";");
            istrue = false;
        }
        if (StringUtils.isEmpty(iflytek.getMac())) {
            msg.append("mac=" + iflytek.getMac() + ";");
            istrue = false;
        }
        if (iflytek.getOs() == "WP" && StringUtils.isEmpty(iflytek.getDuid())) {
            msg.append("duid=" + iflytek.getDuid() + ";");
            istrue = false;
        }
        if (StringUtils.isEmpty(iflytek.getDensity())) {
            msg.append("density=" + iflytek.getDensity() + ";");
            istrue = false;
        }
        if (StringUtils.isEmpty(iflytek.getOperator())) {
            msg.append("operator=" + iflytek.getOperator() + ";");
            istrue = false;
        }
        if (StringUtils.isEmpty(iflytek.getNet())) {
            msg.append("net=" + iflytek.getNet() + ";");
            istrue = false;
        }
        if (StringUtils.isEmpty(iflytek.getIp())) {
            msg.append("ip=" + iflytek.getIp() + ";");
            istrue = false;
        }
        if (StringUtils.isEmpty(iflytek.getUa())) {
            msg.append("ua=" + iflytek.getUa() + ";");
            istrue = false;
        }
        if (StringUtils.isEmpty(iflytek.getTs())) {
            msg.append("ts=" + iflytek.getTs() + ";");
            istrue = false;
        }
        if (StringUtils.isEmpty(iflytek.getAdw())) {
            msg.append("adw=" + iflytek.getAdw() + ";");
            istrue = false;
        }
        if (StringUtils.isEmpty(iflytek.getAdh())) {
            msg.append("adh=" + iflytek.getAdh() + ";");
            istrue = false;
        }
        if (StringUtils.isEmpty(iflytek.getDvw())) {
            msg.append("dvw=" + iflytek.getDvw() + ";");
            istrue = false;
        }
        if (StringUtils.isEmpty(iflytek.getDvh())) {
            msg.append("dvh=" + iflytek.getDvh() + ";");
            istrue = false;
        }
        if (StringUtils.isEmpty(iflytek.getOrientation())) {
            msg.append("orientation=" + iflytek.getOrientation() + ";");
            istrue = false;
        }
        if (StringUtils.isEmpty(iflytek.getVendor())) {
            msg.append("vendor=" + iflytek.getVendor() + ";");
            istrue = false;
        }
        if (StringUtils.isEmpty(iflytek.getModel())) {
            msg.append("model=" + iflytek.getModel() + ";");
            istrue = false;
        }
        if (StringUtils.isEmpty(iflytek.getLan())) {
            msg.append("lan=" + iflytek.getLan() + ";");
            istrue = false;
        }
        if (StringUtils.isEmpty(iflytek.getIsboot())) {
            msg.append("isboot=" + iflytek.getIsboot() + ";");
            istrue = false;
        }
        if (StringUtils.isEmpty(iflytek.getAppid())) {
            msg.append("appid=" + iflytek.getAppid() + ";");
            istrue = false;
        }
        if (StringUtils.isEmpty(iflytek.getAppname())) {
            msg.append("appname=" + iflytek.getAppname() + ";");
            istrue = false;
        }
        if (StringUtils.isEmpty(iflytek.getPkgname())) {
            msg.append("pkgname=" + iflytek.getPkgname() + ";");
            istrue = false;
        }
        if (!istrue) {
             logger.error(msg.toString());
        }
        return istrue;
    }
    
}
