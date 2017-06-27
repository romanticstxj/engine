package com.madhouse.cache;

import com.madhouse.dsp.DSPBaseHandler;
import com.madhouse.util.httpclient.HttpClient;

/**
 * Created by WUJUNFENG on 2017/5/23.
 */
public class DSPMetaData {
    private long id;
    private String bidUrl;
    private String wnUrl;
    private int maxQPS;
    private String token;
    private int apiType;
    private int status;
    private HttpClient httpClient = new HttpClient();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBidUrl() {
        return bidUrl;
    }

    public void setBidUrl(String bidUrl) {
        this.bidUrl = bidUrl;
    }

    public String getWnUrl() {
        return wnUrl;
    }

    public void setWnUrl(String wnUrl) {
        this.wnUrl = wnUrl;
    }

    public int getMaxQPS() {
        return maxQPS;
    }

    public void setMaxQPS(int maxQPS) {
        this.maxQPS = maxQPS;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getApiType() {
        return apiType;
    }

    public void setApiType(int apiType) {
        this.apiType = apiType;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }

    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }
}
