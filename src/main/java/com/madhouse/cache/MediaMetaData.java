package com.madhouse.cache;

import com.madhouse.media.MediaBaseHandler;

import java.util.Map;

/**
 * Created by WUJUNFENG on 2017/5/23.
 */
public class MediaMetaData {
    private long mid;
    private int tmax;
    private int mtype;
    private int mcat;
    private String name;
    private boolean https;
    private int apitype;

    public boolean isHttps() {
        return https;
    }

    public void setHttps(boolean https) {
        this.https = https;
    }

    public int getApitype() {
        return apitype;
    }

    public void setApitype(int apitype) {
        this.apitype = apitype;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getMid() {
        return mid;
    }

    public void setMid(long mid) {
        this.mid = mid;
    }

    public int getTmax() {
        return tmax;
    }

    public void setTmax(int tmax) {
        this.tmax = tmax;
    }

    public int getMtype() {
        return mtype;
    }

    public void setMtype(int mtype) {
        this.mtype = mtype;
    }

    public int getMcat() {
        return mcat;
    }

    public void setMcat(int mcat) {
        this.mcat = mcat;
    }
}
