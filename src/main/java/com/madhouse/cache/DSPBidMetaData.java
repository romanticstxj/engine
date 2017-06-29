package com.madhouse.cache;

import com.madhouse.dsp.DSPBaseHandler;
import com.madhouse.ssp.avro.*;
import org.apache.http.client.methods.HttpRequestBase;

/**
 * Created by WUJUNFENG on 2017/6/15.
 */
public class DSPBidMetaData {
    private DSPMetaData dspMetaData;
    private DSPBid.Builder dspBidBuilder;
    private DSPBaseHandler dspBaseHandler;
    private HttpRequestBase httpRequestBase;

    public HttpRequestBase getHttpRequestBase() {
        return httpRequestBase;
    }

    public void setHttpRequestBase(HttpRequestBase httpRequestBase) {
        this.httpRequestBase = httpRequestBase;
    }

    public DSPBid.Builder getDspBidBuilder() {
        return dspBidBuilder;
    }

    public void setDspBidBuilder(DSPBid.Builder dspBidBuilder) {
        this.dspBidBuilder = dspBidBuilder;
    }

    public DSPBaseHandler getDspBaseHandler() {
        return dspBaseHandler;
    }

    public void setDspBaseHandler(DSPBaseHandler dspBaseHandler) {
        this.dspBaseHandler = dspBaseHandler;
    }

    public DSPMetaData getDspMetaData() {
        return dspMetaData;
    }

    public void setDspMetaData(DSPMetaData dspMetaData) {
        this.dspMetaData = dspMetaData;
    }
}
