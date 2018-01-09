package com.madhouse.dsp.madhouse;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.madhouse.ssp.avro.*;

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
import com.madhouse.ssp.avro.DSPResponse.Builder;
import com.madhouse.util.ObjectUtils;
import com.madhouse.util.StringUtil;

/**
 * Created by WUJUNFENG on 2017/5/22.
 */
public class MADMaxHandler extends DSPBaseHandler {
    private static final int NO_BID = 405;
    private static final int REDIS_TIMEOUT = 407;
    private static final int HBASE_TIMEOUT = 408;

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
        List<String> tags = mediaRequest.getTags();
        String appname = mediaRequest.getName();
        String label ="";
        try {
            if (ua != null) {
                ua = URLEncoder.encode(ua, "UTF-8");
            }
            if (device != null) {
                device = URLEncoder.encode(device, "UTF-8");
            }
            if (tags != null && tags != null) {
                label = URLEncoder.encode(JSON.toJSONString(tags), "UTF-8");
            }
            if (appname != null) {
                appname = URLEncoder.encode(appname, "UTF-8");
            }
        } catch (UnsupportedEncodingException e) {
            logger.error(e.toString());
        }

        DSPMappingMetaData dspMappingMetaData = CacheManager.getInstance().getDSPMapping(dspBidMetaData.getDspMetaData().getId(), plcmtMetaData.getId());
        String adspaceId = Long.toString(plcmtMetaData.getId());
        if (dspMappingMetaData != null && !StringUtils.isEmpty(dspMappingMetaData.getMappingKey())) {
            adspaceId = dspMappingMetaData.getMappingKey();
        }

        String imei = mediaRequest.getDid();
        if (StringUtils.isEmpty(imei)) {
            imei = mediaRequest.getDidmd5();
        }

        String aid = mediaRequest.getDpid();
        if (StringUtils.isEmpty(aid)) {
            aid = mediaRequest.getDpidmd5();
        }

        sb.append("adspaceid=").append(adspaceId)
                .append("&adtype=").append(plcmtMetaData.getAdType())
                .append("&width=").append(mediaRequest.getW())
                .append("&height=").append(mediaRequest.getH())
                .append("&pkgname=").append(StringUtil.toString(mediaRequest.getBundle()))
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
                .append("&lon=").append(StringUtil.toString(mediaRequest.hasGeoBuilder() ? mediaRequest.getGeoBuilder().getLon().toString() : ""))
                .append("&lat=").append(StringUtil.toString(mediaRequest.hasGeoBuilder() ? mediaRequest.getGeoBuilder().getLat().toString() : ""))
                .append("&cell=").append(StringUtil.toString(mediaRequest.getCell()))
                .append("&mcell=").append(StringUtil.toString(mediaRequest.getCellmd5()))
                .append("&dealid=").append(StringUtil.toString(policyMetaData.getDealId()))
                .append("&label=").append(StringUtil.toString(label));
        switch (mediaRequest.getOs()) {
            case Constant.OSType.ANDROID:
                sb.append("&os=").append(PremiumMADStatusCode.PremiumMadOs.OS_ANDROID)
                   .append("&imei=").append(StringUtil.toString(imei))
                   .append("&aid=").append(StringUtil.toString(aid))
                   .append("&aaid=").append(StringUtil.toString(mediaRequest.getIfa()));
                break;
            case Constant.OSType.IOS:
                sb.append("&os=").append(PremiumMADStatusCode.PremiumMadOs.OS_IOS)
                    .append("&idfa=").append(StringUtil.toString(mediaRequest.getIfa()))
                    .append("&oid=").append(StringUtil.toString(mediaRequest.getDpidmd5()));
                break;
            case Constant.OSType.WINDOWS_PHONE:
                sb.append("&os=").append(PremiumMADStatusCode.PremiumMadOs.OS_WINDOWS_PHONE)
                .append("&uid=").append(StringUtil.toString(mediaRequest.getDpidmd5()));
                break;
            default:
                sb.append("&os=").append(PremiumMADStatusCode.PremiumMadOs.OS_OTHERS)
                .append("&uid=").append(StringUtil.toString(mediaRequest.getDpidmd5()));
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
            switch (httpResponse.getStatusLine().getStatusCode()){
                case HttpServletResponse.SC_OK : {
                    PremiumMADResponse madResponse = JSON.parseObject(JSON.parseObject(ObjectUtils.toEntityString(httpResponse.getEntity())).getString(dspBidMetaData.getDspBidBuilder().getRequestBuilder().getTagid()), PremiumMADResponse.class);
                    if (madResponse != null && madResponse.getReturncode() != null) {
                        int returnCode = madResponse.getReturncode();
                        if (returnCode == HttpServletResponse.SC_OK) {
                            DSPResponse.Builder response= convertMADMaxResponse(madResponse,dspBidMetaData);
                            dspBidMetaData.getDspBidBuilder().setStatus(Constant.StatusCode.OK);
                            dspBidMetaData.getDspBidBuilder().setResponseBuilder(response);

                            return true;
                        } else {
                            dspBidMetaData.getDspBidBuilder().setStatus(Constant.StatusCode.BAD_REQUEST);
                        }
                    } else {
                        dspBidMetaData.getDspBidBuilder().setStatus(Constant.StatusCode.BAD_REQUEST);
                    }

                    break;
                }

                case NO_BID: {
                    dspBidMetaData.getDspBidBuilder().setStatus(Constant.StatusCode.NO_CONTENT);
                    break;
                }

                case REDIS_TIMEOUT:
                case HBASE_TIMEOUT: {
                    dspBidMetaData.getDspBidBuilder().setStatus(Constant.StatusCode.REQUEST_TIMEOUT);
                    break;
                }
                
                default: {
                    dspBidMetaData.getDspBidBuilder().setStatus(Constant.StatusCode.BAD_REQUEST);
                    break;
                }
            }

            return false;
        } catch (Exception e) {
            logger.error(e.toString() + "MADMax_Response_error" + dspBidMetaData.getDspBidBuilder().toString());
            dspBidMetaData.getDspBidBuilder().setStatus(Constant.StatusCode.INTERNAL_ERROR);
            return false;
        }
    }

    private Builder convertMADMaxResponse(PremiumMADResponse madResponse, DSPBidMetaData dspBidMetaData) {
        DSPRequest.Builder dspRequest = dspBidMetaData.getDspBidBuilder().getRequestBuilder();

        DSPResponse.Builder dspResponse = DSPResponse.newBuilder();
        dspResponse.setCid(madResponse.getCid());
        dspResponse.setId(dspRequest.getId());
        dspResponse.setBidid(StringUtil.toString(madResponse.getBidid()));
        dspResponse.setImpid(dspRequest.getImpid());
        dspResponse.setIcon(madResponse.getIcon());
        dspResponse.setCover(madResponse.getCover());
        dspResponse.setTitle(madResponse.getDisplaytitle());
        dspResponse.setDesc(madResponse.getDisplaytext());
        dspResponse.setContent(madResponse.getDisplaycontent());
        dspResponse.setDuration(madResponse.getDuration() != null ? madResponse.getDuration() : 0);
        dspResponse.setActtype(Constant.ActionType.OPEN_IN_APP);
        dspResponse.setAdm(madResponse.getAdm());
        dspResponse.setLpgurl(madResponse.getClickurl());
        dspResponse.setDealid(madResponse.getDealid());
        dspResponse.setCrid(madResponse.getCrid());
        Monitor.Builder monitor = Monitor.newBuilder();
        List<Track> tracks=new ArrayList<>();
        for (String track : madResponse.getImgtracking()) {
            tracks.add(new Track(0, track));
        }
        monitor.setImpurl(tracks);
        monitor.setClkurl(madResponse.getThclkurl());
        monitor.setSecurl(madResponse.getSecurl());
        monitor.setExts(madResponse.getExts());

        dspResponse.setMonitorBuilder(monitor);
        logger.info("MADMax Response is:{}", dspResponse.build().toString());
        return dspResponse;
    }
    

}
