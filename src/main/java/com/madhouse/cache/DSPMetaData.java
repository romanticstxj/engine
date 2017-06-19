package com.madhouse.cache;

import com.madhouse.dsp.DSPBaseHandler;
import com.madhouse.util.httpclient.HttpClient;

/**
 * Created by WUJUNFENG on 2017/5/23.
 */
public class DSPMetaData {
    private long dspid;
    private String bidurl;
    private boolean enable;
    private int weights;
    private int floating;
    private String token;
    private int apitype;

    public int getApitype() {
        return apitype;
    }

    public void setApitype(int apitype) {
        this.apitype = apitype;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getWeights() {
        return weights;
    }

    public void setWeights(int weights) {
        this.weights = weights;
    }

    public int getFloating() {
        return floating;
    }

    public void setFloating(int floating) {
        this.floating = floating;
    }

    private HttpClient httpClient = new HttpClient();

    public long getDspid() {
        return dspid;
    }

    public void setDspid(long dspid) {
        this.dspid = dspid;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getBidurl() {
        return bidurl;
    }

    public void setBidurl(String bidurl) {
        this.bidurl = bidurl;
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }

    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }
}
