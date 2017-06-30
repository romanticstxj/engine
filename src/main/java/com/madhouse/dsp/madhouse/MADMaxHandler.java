package com.madhouse.dsp.madhouse;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;

import com.madhouse.cache.AdBlockMetaData;
import com.madhouse.cache.DSPBidMetaData;
import com.madhouse.cache.DSPMetaData;
import com.madhouse.cache.MediaMetaData;
import com.madhouse.cache.PlcmtMetaData;
import com.madhouse.cache.PolicyMetaData;
import com.madhouse.dsp.DSPBaseHandler;
import com.madhouse.media.madhouse.PremiumMADStatusCode;
import com.madhouse.ssp.Constant;
import com.madhouse.ssp.avro.MediaBid;
import com.madhouse.ssp.avro.MediaRequest;
import com.madhouse.util.StringUtil;

/**
 * Created by WUJUNFENG on 2017/5/22.
 */
public class MADMaxHandler extends DSPBaseHandler {
    @Override
    public HttpRequestBase packageBidRequest(MediaBid.Builder mediaBidBuilder, MediaMetaData mediaMetaData, PlcmtMetaData plcmtMetaData, AdBlockMetaData adBlockMetaData, PolicyMetaData policyMetaData, DSPMetaData dspMetaData, DSPBidMetaData dspBidMetaData) {
        MediaRequest.Builder builder=  mediaBidBuilder.getRequestBuilder();
        String urlTemplate = null;
        if(!dspBidMetaData.getDspMetaData().getBidUrl().startsWith("?")){
            urlTemplate=urlTemplate+"?";
        }
        StringBuilder sb = new StringBuilder(urlTemplate);
        // url编码
        String ua = builder.getUa().toString();
        String device = builder.getModel().toString();
        String pkgname = builder.getBundle().toString();
        String appname = builder.getName().toString();
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
        sb.append("adspaceid=").append(plcmtMetaData.getAdspaceKey())
                .append("&adtype=").append(StringUtil.validateString(builder.getAdtype().toString()))
                .append("&width=").append(builder.getW() != null ? builder.getW() : plcmtMetaData.getBanner().getW())
                .append("&height=").append(builder.getH() != null ? builder.getH() : plcmtMetaData.getBanner().getH())
                .append("&pkgname=").append(StringUtil.validateString(pkgname))
                .append("&conn=").append(StringUtil.validateString(builder.getConnectiontype().toString()))
                .append("&carrier=").append(StringUtil.validateString(builder.getCarrier().toString()))
                .append("&device=").append(StringUtil.validateString(device))
                .append("&bid=").append(StringUtil.validateString(builder.getBid().toString()))
                .append("&appname=").append(StringUtil.validateString(appname))
                .append("&apitype=4")
                .append("&pcat=").append(StringUtil.validateString(builder.getCategory().toString()))
                .append("&osv=").append(StringUtil.validateString(builder.getOsv().toString()))
                .append("&wma=").append(StringUtil.validateString(builder.getMac().toString()))
                .append("&ua=").append(StringUtil.validateString(ua))
                .append("&ip=").append(StringUtil.validateString(builder.getIp().toString()))
                .append("&pid=").append(StringUtil.validateString(builder.getMediaid().toString()))
                .append("&density=").append(StringUtil.validateString(String.valueOf(mediaMetaData.getType())))
                .append("&media=").append(StringUtil.validateString(builder.getDpid().toString()))
                .append("&lon=").append(StringUtil.validateString(builder.getLon().toString()))
                .append("&lat=").append(StringUtil.validateString(builder.getLat().toString()))
                .append("&cell=").append(StringUtil.validateString(builder.getCell().toString()))
                .append("&mcell=").append(StringUtil.validateString(builder.getCellmd5().toString()));
        switch (builder.getOs()) {
            case Constant.OSType.ANDROID:
                sb.append("&os=").append(PremiumMADStatusCode.PremiumMadOs.OS_ANDROID)
                   .append("&imei=").append(StringUtil.validateString(builder.getDid().toString()))
                   .append("&aid=").append(StringUtil.validateString(builder.getDpid().toString()))
                   .append("&aaid=").append(StringUtil.validateString(builder.getIfa().toString()));
                break;
            case Constant.OSType.IOS:
                sb.append("&os=").append(PremiumMADStatusCode.PremiumMadOs.OS_IOS)
                    .append("&idfa=").append(StringUtil.validateString(builder.getIfa().toString()))
                    .append("&oid=").append(StringUtil.validateString(builder.getDpid().toString()));
                break;
            case Constant.OSType.WINDOWS_PHONE:
                sb.append("&os=").append(PremiumMADStatusCode.PremiumMadOs.OS_WINDOWS_PHONE)
                .append("&uid=").append(StringUtil.validateString(builder.getDpid().toString()));
                break;
            default:
                sb.append("&os=").append(PremiumMADStatusCode.PremiumMadOs.OS_OTHERS)
                .append("&uid=").append(StringUtil.validateString(builder.getDpid().toString()));
                break;
        }
        String str = sb.toString().replace(" ", "%20");
        logger.info("request url:{}", str);// httpclient无法解析空格，需要把空格替换掉
        HttpGet httpGet = new HttpGet(str);
        return httpGet;
        
    }
    @Override
    public boolean parseBidResponse(HttpResponse httpResponse, DSPBidMetaData dspBidMetaData) {
        return super.parseBidResponse(httpResponse, dspBidMetaData);
    }
    

}
