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
    
    public abstract HttpRequestBase packageBidRequest(MediaBid.Builder mediaBidBuilder, MediaMetaData mediaMetaData, PlcmtMetaData plcmtMetaData, AdBlockMetaData adBlockMetaData, PolicyMetaData policyMetaData, DSPBidMetaData dspBidMetaData);
    public abstract boolean parseBidResponse(HttpResponse httpResponse, DSPBidMetaData dspBidMetaData);

    public String getWinNoticeUrl(DSPBidMetaData dspBidMetaData) {
        return null;
    }

    protected final boolean createDSPRequest(MediaBid.Builder mediaBidBuilder, MediaMetaData mediaMetaData, PlcmtMetaData plcmtMetaData, AdBlockMetaData adBlockMetaData, PolicyMetaData policyMetaData, DSPMetaData dspMetaData, DSPBid.Builder dspBidBuilder) {

        try {
            MediaRequest mediaRequest = mediaBidBuilder.getRequest();

            DSPRequest.Builder dspRequest = DSPRequest.newBuilder()
                    .setId(StringUtil.getUUID())
                    .setImpid(mediaBidBuilder.getImpid())
                    .setAdtype(plcmtMetaData.getAdType())
                    .setLayout(plcmtMetaData.getLayout())
                    .setTagid(plcmtMetaData.getAdspaceKey())
                    .setDealid(policyMetaData.getDealId())
                    .setTest(mediaRequest.getTest())
                    .setBidfloor(policyMetaData.getAdspaceInfoMap().get(plcmtMetaData.getId()).getBidFloor())
                    .setBidtype(policyMetaData.getAdspaceInfoMap().get(plcmtMetaData.getId()).getBidType())
                    .setTmax(mediaMetaData.getTimeout());

            dspBidBuilder.setDspid(dspMetaData.getId())
                    .setPolicyid(policyMetaData.getId())
                    .setDeliverytype(policyMetaData.getDeliveryType())
                    .setTime(System.currentTimeMillis())
                    .setRequestBuilder(dspRequest);

            return true;
        } catch (Exception ex) {
            System.err.println(ex.toString());
            return false;
        }
    }
}
