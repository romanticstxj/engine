package com.madhouse.dsp;

/**
 * Created by WUJUNFENG on 2017/5/22.
 */

import com.madhouse.cache.*;
import com.madhouse.cache.AdBlockMetaData;
import com.madhouse.ssp.Constant;
import com.madhouse.ssp.PremiumMADDataModel;
import com.madhouse.util.AESUtil;
import com.madhouse.util.StringUtil;
import com.madhouse.rtb.PremiumMADRTBProtocol.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.util.EntityUtils;


public abstract class DSPBaseHandler {
    public abstract HttpRequestBase packageBidRequest(PremiumMADDataModel.MediaBid.Builder mediaBidBuilder, MediaMetaData mediaMetaData, PlcmtMetaData plcmtMetaData, AdBlockMetaData adBlockMetaData, PolicyMetaData policyMetaData, DSPMetaData dspMetaData, DSPBidMetaData dspBidMetaData);
    public abstract boolean parseBidResponse(HttpResponse httpResponse, DSPBidMetaData dspBidMetaData);
    public abstract String getWinNoticeUrl(int price, DSPMetaData dspMetaData, DSPBidMetaData dspBidMetaData);

    protected boolean packageDSPRequest(PremiumMADDataModel.MediaBid.Builder mediaBidBuilder, MediaMetaData mediaMetaData, PlcmtMetaData plcmtMetaData, AdBlockMetaData adBlockMetaData, PolicyMetaData policyMetaData, DSPMetaData dspMetaData, PremiumMADDataModel.DSPBid.Builder dspBidBuilder) {

        try {
            PremiumMADDataModel.MediaBid.MediaRequest mediaRequest = mediaBidBuilder.getRequest();

            PremiumMADDataModel.DSPBid.DSPRequest.Builder dspRequest = PremiumMADDataModel.DSPBid.DSPRequest.newBuilder()
                    .setId(StringUtil.getUUID())
                    .setImpid(mediaBidBuilder.getImpid())
                    .setAdtype(plcmtMetaData.getType())
                    .setLayout(plcmtMetaData.getLayout())
                    .setTagid(plcmtMetaData.getAdspaceKey())
                    .setDealid(policyMetaData.getDealid())
                    .setTest(mediaRequest.getTest())
                    .setBidfloor(policyMetaData.getBidfloor())
                    .setBidtype(policyMetaData.getBidtype())
                    .setTmax(mediaMetaData.getTimeout());

            dspBidBuilder.setDspid(dspMetaData.getDspid())
                    .setPolicyid(policyMetaData.getId())
                    .setDeliverytype(policyMetaData.getDeliverytype())
                    .setTime(System.currentTimeMillis())
                    .setRequest(dspRequest);

            return true;
        } catch (Exception ex) {
            System.err.println(ex.toString());
            return false;
        }
    }
}
