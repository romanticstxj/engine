package com.madhouse.dsp;

/**
 * Created by WUJUNFENG on 2017/5/22.
 */

import com.madhouse.cache.*;
import com.madhouse.ssp.Constant;
import com.madhouse.ssp.LoggerUtil;
import com.madhouse.ssp.avro.*;
import com.madhouse.util.AESUtil;
import com.madhouse.util.StringUtil;
import com.madhouse.rtb.PremiumMADRTBProtocol.*;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.Logger;


public abstract class DSPBaseHandler {
    
    @SuppressWarnings("static-access")
    public static Logger logger = LoggerUtil.getInstance().getPremiummadlogger();

    public final HttpRequestBase packageRequest(MediaBid.Builder mediaBid, MediaMetaData mediaMetaData, PlcmtMetaData plcmtMetaData, AdBlockMetaData adBlockMetaData, PolicyMetaData policyMetaData, DSPBidMetaData dspBidMetaData) {

        try {
            MediaRequest.Builder mediaRequest = mediaBid.getRequestBuilder();
            DSPRequest.Builder dspRequest = DSPRequest.newBuilder()
                    .setId(StringUtil.getUUID())
                    .setImpid(mediaBid.getImpid())
                    .setAdtype(plcmtMetaData.getAdType())
                    .setLayout(plcmtMetaData.getLayout())
                    .setTagid(plcmtMetaData.getAdspaceKey())
                    .setDealid(policyMetaData.getDealId())
                    .setTest(mediaRequest.getTest())
                    .setBidfloor(policyMetaData.getAdspaceInfoMap().get(plcmtMetaData.getId()).getBidFloor())
                    .setBidtype(policyMetaData.getAdspaceInfoMap().get(plcmtMetaData.getId()).getBidType())
                    .setTmax(mediaMetaData.getTimeout());

            DSPBid.Builder dspBid = dspBidMetaData.getDspBidBuilder();
            dspBid.setDspid(dspBidMetaData.getDspMetaData().getId())
                    .setPolicyid(policyMetaData.getId())
                    .setDeliverytype(policyMetaData.getDeliveryType())
                    .setTime(System.currentTimeMillis())
                    .setMediaid(mediaMetaData.getId())
                    .setAdspaceid(plcmtMetaData.getId())
                    .setRequestBuilder(dspRequest)
                    .setStatus(Constant.StatusCode.NO_CONTENT)
                    .setLocation(mediaBid.getLocation());

            return this.packageBidRequest(mediaBid, mediaMetaData, plcmtMetaData, adBlockMetaData, policyMetaData, dspBidMetaData);
        } catch (Exception e) {
            logger.error(e.toString());
            return null;
        }
    }

    public final boolean parseResponse(HttpResponse httpResponse, DSPBidMetaData dspBidMetaData) {
        return this.parseBidResponse(httpResponse, dspBidMetaData);
    }

    protected abstract HttpRequestBase packageBidRequest(MediaBid.Builder mediaBidBuilder, MediaMetaData mediaMetaData, PlcmtMetaData plcmtMetaData, AdBlockMetaData adBlockMetaData, PolicyMetaData policyMetaData, DSPBidMetaData dspBidMetaData);
    protected abstract boolean parseBidResponse(HttpResponse httpResponse, DSPBidMetaData dspBidMetaData);

    public String getWinNoticeUrl(DSPBidMetaData dspBidMetaData) {
        return null;
    }

}
