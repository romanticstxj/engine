package com.madhouse.cache;

import com.madhouse.dsp.DSPBaseHandler;
import com.madhouse.ssp.avro.*;
import com.madhouse.util.httpclient.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;

/**
 * Created by WUJUNFENG on 2017/6/15.
 */
public class DSPBidMetaData {
    private DSPMetaData dspMetaData;
    private DSPBid.Builder dspBidBuilder;
    private DSPBaseHandler dspBaseHandler;

    private HttpClient httpClient;
    private HttpRequestBase httpRequestBase;

    private AuctionPriceInfo auctionPriceInfo;

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

    public AuctionPriceInfo getAuctionPriceInfo() {
        return auctionPriceInfo;
    }

    public void setAuctionPriceInfo(AuctionPriceInfo auctionPriceInfo) {
        this.auctionPriceInfo = auctionPriceInfo;
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }

    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }
}
