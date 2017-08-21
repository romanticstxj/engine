package com.madhouse.dsp.madhouse;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;

import com.alibaba.fastjson.JSON;
import com.madhouse.cache.AdBlockMetaData;
import com.madhouse.cache.CacheManager;
import com.madhouse.cache.DSPBidMetaData;
import com.madhouse.cache.DSPMappingMetaData;
import com.madhouse.cache.MediaMetaData;
import com.madhouse.cache.PlcmtMetaData;
import com.madhouse.cache.PolicyMetaData;
import com.madhouse.dsp.DSPBaseHandler;
import com.madhouse.media.madhouse.PremiumMADResponse;
import com.madhouse.media.madhouse.PremiumMADStatusCode;
import com.madhouse.ssp.Constant;
import com.madhouse.ssp.avro.DSPResponse;
import com.madhouse.ssp.avro.DSPResponse.Builder;
import com.madhouse.ssp.avro.MediaBid;
import com.madhouse.ssp.avro.MediaRequest;
import com.madhouse.ssp.avro.Monitor;
import com.madhouse.ssp.avro.Track;
import com.madhouse.util.ObjectUtils;
import com.madhouse.util.StringUtil;

/**
 * Created by WUJUNFENG on 2017/5/22.
 */
public class MADMaxHandler extends DSPBaseHandler {
    @Override
    public HttpRequestBase packageBidRequest(MediaBid.Builder mediaBidBuilder, MediaMetaData mediaMetaData, PlcmtMetaData plcmtMetaData, AdBlockMetaData adBlockMetaData, PolicyMetaData policyMetaData, DSPBidMetaData dspBidMetaData) {

        MediaRequest.Builder mediaRequest =  mediaBidBuilder.getRequestBuilder();
        String urlTemplate = dspBidMetaData.getDspMetaData().getBidUrl();

        if (urlTemplate.contains("?")) {
            urlTemplate += "&";
        } else {
            urlTemplate += "?";
        }

        StringBuilder sb = new StringBuilder(urlTemplate);
        // url编码
        String ua = mediaRequest.getUa();
        String device = mediaRequest.getModel();
        String pkgname = mediaRequest.getBundle();
        String appname = mediaMetaData.getName();
        try {
            if (ua != null) {
                ua = URLEncoder.encode(ua, "UTF-8");
            }
            if (device != null) {
                device = URLEncoder.encode(device, "UTF-8");
            }
            if (pkgname != null) {
                pkgname = URLEncoder.encode(pkgname, "UTF-8");
            }
            if (appname != null) {
                appname = URLEncoder.encode(appname, "UTF-8");
            }
        } catch (UnsupportedEncodingException e) {
            logger.error(e.toString());
        }

        DSPMappingMetaData dspMappingMetaData = CacheManager.getInstance().getDSPMapping(dspBidMetaData.getDspMetaData().getId(), plcmtMetaData.getId());
        String adspaceId = plcmtMetaData.getAdspaceKey();
        if (dspMappingMetaData != null && !StringUtils.isEmpty(dspMappingMetaData.getMappingKey())) {
            adspaceId = dspMappingMetaData.getMappingKey();
        }

        sb.append("adspaceid=").append(adspaceId)
                .append("&adtype=").append(plcmtMetaData.getAdType())
                .append("&width=").append(plcmtMetaData.getW())
                .append("&height=").append(plcmtMetaData.getH())
                .append("&pkgname=").append(StringUtil.toString(pkgname))
                .append("&conn=").append(StringUtil.toString(mediaRequest.getConnectiontype().toString()))
                .append("&carrier=").append(StringUtil.toString(mediaRequest.getCarrier().toString()))
                .append("&device=").append(StringUtil.toString(device))
                .append("&bid=").append(StringUtil.toString(dspBidMetaData.getDspBidBuilder().getRequestBuilder().getId()))
                .append("&appname=").append(appname)
                .append("&apitype=4")
                .append("&pcat=").append(mediaMetaData.getCategory())
                .append("&osv=").append(StringUtil.toString(mediaRequest.getOsv()))
                .append("&wma=").append(StringUtil.toString(mediaRequest.getMac()))
                .append("&ua=").append(StringUtil.toString(ua))
                .append("&ip=").append(StringUtil.toString(mediaRequest.getIp()))
                .append("&pid=").append(mediaRequest.getMediaid())
                .append("&density=").append(StringUtil.toString(String.valueOf(mediaMetaData.getType())))
                .append("&media=").append(mediaMetaData.getType())
                .append("&lon=").append(StringUtil.toString(mediaRequest.getLon().toString()))
                .append("&lat=").append(StringUtil.toString(mediaRequest.getLat().toString()))
                .append("&cell=").append(StringUtil.toString(mediaRequest.getCell()))
                .append("&mcell=").append(StringUtil.toString(mediaRequest.getCellmd5()))
                .append("&dealid=").append(StringUtil.toString(policyMetaData.getDealId()));
        switch (mediaRequest.getOs()) {
            case Constant.OSType.ANDROID:
                sb.append("&os=").append(PremiumMADStatusCode.PremiumMadOs.OS_ANDROID)
                   .append("&imei=").append(StringUtil.toString(mediaRequest.getDid()))
                   .append("&aid=").append(StringUtil.toString(mediaRequest.getDpid()))
                   .append("&aaid=").append(StringUtil.toString(mediaRequest.getIfa()));
                break;
            case Constant.OSType.IOS:
                sb.append("&os=").append(PremiumMADStatusCode.PremiumMadOs.OS_IOS)
                    .append("&idfa=").append(StringUtil.toString(mediaRequest.getIfa()))
                    .append("&oid=").append(StringUtil.toString(mediaRequest.getDpid()));
                break;
            case Constant.OSType.WINDOWS_PHONE:
                sb.append("&os=").append(PremiumMADStatusCode.PremiumMadOs.OS_WINDOWS_PHONE)
                .append("&uid=").append(StringUtil.toString(mediaRequest.getDpid()));
                break;
            default:
                sb.append("&os=").append(PremiumMADStatusCode.PremiumMadOs.OS_OTHERS)
                .append("&uid=").append(StringUtil.toString(mediaRequest.getDpid()));
                break;
        }
        String str = sb.toString().replace(" ", "%20");
        logger.info("MADMax request url:{}", str);// httpclient无法解析空格，需要把空格替换掉
        HttpGet httpGet = new HttpGet(str);
        
        return httpGet;
        
    }
    @Override
    public boolean parseBidResponse(HttpResponse httpResponse, DSPBidMetaData dspBidMetaData) {
        try {
            PremiumMADResponse madResponse = JSON.parseObject(JSON.parseObject(ObjectUtils.toEntityString(httpResponse.getEntity())).getString(dspBidMetaData.getDspBidBuilder().getRequestBuilder().getTagid()), PremiumMADResponse.class);
            switch (httpResponse.getStatusLine().getStatusCode()){
                case HttpServletResponse.SC_OK ://200
                case HttpServletResponse.SC_METHOD_NOT_ALLOWED :    //405
                case HttpServletResponse.SC_PROXY_AUTHENTICATION_REQUIRED : //407
                case HttpServletResponse.SC_REQUEST_TIMEOUT : //408
                    if (madResponse == null && madResponse.getReturncode() == null &&
                            madResponse.getReturncode() == String.valueOf(HttpServletResponse.SC_METHOD_NOT_ALLOWED ) &&
                            madResponse.getReturncode() == String.valueOf(HttpServletResponse.SC_PROXY_AUTHENTICATION_REQUIRED)) {
                        dspBidMetaData.getDspBidBuilder().setStatus(Constant.StatusCode.BAD_REQUEST);
                    } else if (madResponse.getReturncode() == String.valueOf(HttpServletResponse.SC_REQUEST_TIMEOUT)){
                        dspBidMetaData.getDspBidBuilder().setStatus(Constant.StatusCode.REQUEST_TIMEOUT);
                    } else if (madResponse.getReturncode() == String.valueOf(HttpServletResponse.SC_INTERNAL_SERVER_ERROR )){
                        dspBidMetaData.getDspBidBuilder().setStatus(Constant.StatusCode.INTERNAL_ERROR);
                    }  else if (madResponse.getReturncode() == String.valueOf(HttpServletResponse.SC_OK)){
                        DSPResponse.Builder response= convertMADMaxResponse(madResponse,dspBidMetaData);
                        dspBidMetaData.getDspBidBuilder().setStatus(Constant.StatusCode.OK);
                        dspBidMetaData.getDspBidBuilder().setResponse(response.build());
                        return true;
                    }
                    break;
                case HttpServletResponse.SC_INTERNAL_SERVER_ERROR : //500
                    dspBidMetaData.getDspBidBuilder().setStatus(Constant.StatusCode.INTERNAL_ERROR);
                    break;   
            }
            return false;
        } catch (Exception e) {
            logger.error("MADMax Response :{}", dspBidMetaData.getDspBidBuilder().toString());
            dspBidMetaData.getDspBidBuilder().setStatus(Constant.StatusCode.INTERNAL_ERROR);
            return false;
        }
    }
    private Builder convertMADMaxResponse(PremiumMADResponse madResponse, DSPBidMetaData dspBidMetaData) {
        DSPResponse.Builder dspResponse = DSPResponse.newBuilder();
        dspResponse.setCid(madResponse.getCid());
        dspResponse.setId(String.valueOf(dspBidMetaData.getDspBidBuilder().getRequestBuilder().getId()));
        dspResponse.setBidid(madResponse.getBid());
        dspResponse.setImpid(dspBidMetaData.getDspBidBuilder().getRequestBuilder().getImpid().toString());
        dspResponse.setAdmid(madResponse.getAdspaceid());
        dspResponse.setIcon(madResponse.getIcon());
        dspResponse.setCover(madResponse.getCover());
        dspResponse.setTitle(madResponse.getDisplaytitle());
        dspResponse.setDesc(madResponse.getDisplaytext());
        dspResponse.setDuration(Integer.parseInt(madResponse.getDuration().toString()));
        dspResponse.setActtype(Constant.ActionType.OPEN_IN_APP);
        dspResponse.setAdm(madResponse.getAdm());
        dspResponse.setLpgurl(madResponse.getClickurl());
        dspResponse.setDealid(madResponse.getDealid());
        Monitor.Builder monitor = Monitor.newBuilder();
        List<Track> tracks=new ArrayList<>();
        for (String track : madResponse.getImgtracking()) {
            tracks.add(new Track(0, track));
        }
        monitor.setImpurl(tracks);
        monitor.setClkurl(madResponse.getThclkurl());
        monitor.setSecurl(madResponse.getSecurl());
        dspResponse.setMonitorBuilder(monitor);
        return dspResponse;
    }
    

}
