package com.madhouse.dsp.madhouse;

import com.madhouse.cache.*;
import com.madhouse.cache.AdBlockMetaData;
import com.madhouse.dsp.DSPBaseHandler;
import com.madhouse.rtb.PremiumMADRTBProtocol;
import com.madhouse.rtb.PremiumMADRTBProtocol.*;
import com.madhouse.ssp.Constant;
import com.madhouse.ssp.avro.*;
import com.madhouse.util.AESUtil;
import com.madhouse.util.StringUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.util.UUID;

/**
 * Created by WUJUNFENG on 2017/5/22.
 */
public class MADMaxHandler extends DSPBaseHandler {
    @Override
    public HttpRequestBase packageBidRequest(MediaBid.Builder mediaBidBuilder, MediaMetaData mediaMetaData, PlcmtMetaData plcmtMetaData, AdBlockMetaData adBlockMetaData, PolicyMetaData policyMetaData, DSPMetaData dspMetaData, DSPBidMetaData dspBidMetaData) {
        return super.packageBidRequest(mediaBidBuilder, mediaMetaData, plcmtMetaData, adBlockMetaData, policyMetaData, dspMetaData, dspBidMetaData);
    }

    @Override
    public boolean parseBidResponse(HttpResponse httpResponse, DSPBidMetaData dspBidMetaData) {
        return super.parseBidResponse(httpResponse, dspBidMetaData);
    }
}
