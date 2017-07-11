package com.madhouse.cache;

import com.madhouse.media.MediaBaseHandler;

import java.util.Map;

/**
 * Created by WUJUNFENG on 2017/5/23.
 */
public class MediaMetaData {
    private long id;
    private String name;
    private int category;
    //app, site
    private int type;
    private int advertiserAuditMode;
    private int materialAuditMode;
    private int apiType;
    private int status;
    private int timeout;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public int getAdvertiserAuditMode() {
        return advertiserAuditMode;
    }

    public void setAdvertiserAuditMode(int advertiserAuditMode) {
        this.advertiserAuditMode = advertiserAuditMode;
    }

    public int getMaterialAuditMode() {
        return materialAuditMode;
    }

    public void setMaterialAuditMode(int materialAuditMode) {
        this.materialAuditMode = materialAuditMode;
    }
}
