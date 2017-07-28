package com.madhouse.dsp.vamaker;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.madhouse.ssp.avro.*;
import com.madhouse.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;
import com.madhouse.cache.AdBlockMetaData;
import com.madhouse.cache.CacheManager;
import com.madhouse.cache.DSPBidMetaData;
import com.madhouse.cache.DSPMappingMetaData;
import com.madhouse.cache.MediaMetaData;
import com.madhouse.cache.PlcmtMetaData;
import com.madhouse.cache.PolicyMetaData;
import com.madhouse.dsp.DSPBaseHandler;
import com.madhouse.ssp.Constant;
import com.madhouse.ssp.avro.MediaBid.Builder;
import com.madhouse.util.ObjectUtils;

public class VamakerHandler extends DSPBaseHandler {

    @Override
    public HttpRequestBase packageBidRequest(Builder mediaBidBuilder, MediaMetaData mediaMetaData, PlcmtMetaData plcmtMetaData, AdBlockMetaData adBlockMetaData, PolicyMetaData policyMetaData, DSPBidMetaData dspBidMetaData) {

        MediaRequest.Builder builder=  mediaBidBuilder.getRequestBuilder();
        String urlTemplate = dspBidMetaData.getDspMetaData().getBidUrl();
        if (urlTemplate.contains("?")) {
            urlTemplate += "&";
        } else {
            urlTemplate += "?";
        }
        StringBuilder sb = new StringBuilder(urlTemplate);
        DSPMappingMetaData dspMappingMetaData = CacheManager.getInstance().getDSPMapping(dspBidMetaData.getDspMetaData().getId(), plcmtMetaData.getId());
        String adspaceId = plcmtMetaData.getAdspaceKey();
        if (dspBidMetaData != null && !StringUtils.isEmpty(dspMappingMetaData.getMappingKey())) {
            adspaceId = dspMappingMetaData.getMappingKey();
        }
        sb.append("_a=").append(adspaceId);
        sb.append("&_t=24"); //fixed,没有这个参数取不到广告
        String pkgname = builder.getBundle();
        try {
            pkgname = URLEncoder.encode(pkgname, "UTF-8");
        } catch (Exception e) {
            logger.error("{},URLEncoder pkgname ",adspaceId);
            return null;
        }
        sb.append("&_pgn=").append(pkgname);
        int connectiontype = builder.getConnectiontype();
        if(connectiontype > 4){
            connectiontype=0;
        }
        sb.append("&_nt=").append(String.valueOf(connectiontype));
        sb.append("&_o=").append(String.valueOf(builder.getCarrier()));
        
        switch (builder.getOs()) {
            case Constant.OSType.IOS:
                sb.append("&_os=").append("2");
//              _os =2 时_idfa 必 填,_mc、_oid 选填
                if (StringUtils.isEmpty(builder.getIfa())) {
                    return null;
                }
                sb.append("&_idfa=").append(builder.getIfa());
                if (StringUtils.isNotEmpty(builder.getMac())) {
                    sb.append("&_mc=").append(builder.getMac());
                }
                if (StringUtils.isNotEmpty(builder.getDpid())) {
                    sb.append("&_oid=").append(builder.getDpid());
                }
                break;
            case Constant.OSType.ANDROID:
                sb.append("&_os=").append("1");
                
                //os=1 时参 数_imei 必填, _mc、_aid、 _aaid 选填\
                
                String imei = builder.getDid();
                if (StringUtils.isEmpty(imei)) {
                    return null;
                }
                sb.append("&_imei=").append(StringUtil.getMD5(imei));
                sb.append("&_aaid=").append(imei);
                if (StringUtils.isNotEmpty(builder.getMac())) {
                    sb.append("&_mc=").append(builder.getMac());
                }
                if (StringUtils.isNotEmpty(builder.getDpid())) {
                    sb.append("&_aid=").append(builder.getDpid());
                }
                break;
            default:
                sb.append("&_os=").append("0");
                break;
        };
        sb.append("&_osv=").append(builder.getOsv());
        //设备品牌，
        sb.append("&_dev=").append(StringUtils.isEmpty(builder.getMake())?"UNKNOWN":builder.getMake());
        //设备型号，
        sb.append("&_md=").append(StringUtils.isEmpty(builder.getModel())?"UNKNOWN":builder.getModel());
        sb.append("&_adw=").append(builder.getW() != null ? builder.getW() :String.valueOf(plcmtMetaData.getW()));
        sb.append("&_adh=").append(builder.getH() != null ? builder.getH() : String.valueOf(plcmtMetaData.getH()));
        
        if (StringUtils.isNotEmpty(builder.getLon().toString())) {
            sb.append("&_lon=").append(builder.getLon());
        }
        if (StringUtils.isNotEmpty(builder.getLat().toString())) {
            sb.append("&_lat=").append(builder.getLat());
        }

        if (StringUtils.isNotEmpty(builder.getCellmd5())) {
            sb.append("&_mpn=").append(builder.getCellmd5());
        }

        if (StringUtils.isNotEmpty(builder.getIp())) {
            sb.append("&_ip=").append(builder.getIp());
        }
        if(plcmtMetaData.isEnableHttps()){
            sb.append("&_sc=").append("0");
        }
        String str = sb.toString().replace(" ", "%20");
        logger.info("Vamaker request url:{}", str);// httpclient无法解析空格，需要把空格替换掉
        HttpGet httpGet = new HttpGet(str);
        return httpGet;
    }

    @Override
    public boolean parseBidResponse(HttpResponse httpResponse, DSPBidMetaData dspBidMetaData) {
        DSPResponse.Builder dspResponse = DSPResponse.newBuilder();
        try {
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode != 200) {
                logger.warn("vamaker no ad reson:{}", statusCode);
                return false;
            }
            String result = EntityUtils.toString(httpResponse.getEntity(),"utf-8");
            logger.info("Vamaker Response is:{}",result);
            VamakerResponse vamakerResponse = JSON.parseObject(result, VamakerResponse.class);
            dspResponse.setCid(vamakerResponse.getCid());
            dspResponse.setId(String.valueOf(dspBidMetaData.getDspBidBuilder().getRequest().getId()));
            dspResponse.setImpid(dspBidMetaData.getDspBidBuilder().getRequestBuilder().getImpid());
            dspResponse.setAdid(vamakerResponse.getAdid());
            dspResponse.setBidid(String.valueOf(dspBidMetaData.getDspMetaData().getId()));
            dspResponse.setLpgurl(vamakerResponse.getLp());
            dspResponse.setDesc(vamakerResponse.getDesc());
            dspResponse.setTitle(vamakerResponse.getTitle());
            dspResponse.setActtype(Constant.ActionType.OPEN_IN_APP);
            dspResponse.setAdm(vamakerResponse.getImg());
            Monitor.Builder monitor = Monitor.newBuilder();
            List<Track> tracks=new ArrayList<>();
            for (String track : vamakerResponse.getPm()) {
                tracks.add(new Track(0, track));
            }
            monitor.setImpurl(tracks);
            monitor.setClkurl(vamakerResponse.getCm());
            dspResponse.setMonitorBuilder(monitor);
            dspBidMetaData.getDspBidBuilder().setStatus(Constant.StatusCode.OK);
            dspBidMetaData.getDspBidBuilder().setResponse(dspResponse.build());
            return true;
        } catch (Exception e) {
            logger.error("Vamaker Response :{}", dspBidMetaData.getDspBidBuilder().toString());
            dspBidMetaData.getDspBidBuilder().setStatus(Constant.StatusCode.INTERNAL_ERROR);
            return false;
        }
    }
}
