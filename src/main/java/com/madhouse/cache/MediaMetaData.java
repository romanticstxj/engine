package com.madhouse.cache;

import com.madhouse.media.MediaBaseHandler;

import java.util.Map;

/**
 * Created by WUJUNFENG on 2017/5/23.
 */
public class MediaMetaData {
    private long mediaId;
    private String name;
    private int category;
    private int type;
    private int apiType;
    private int status;
    private int timeout;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getMediaId() {
        return mediaId;
    }

    public void setMediaId(long mediaId) {
        this.mediaId = mediaId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
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

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
