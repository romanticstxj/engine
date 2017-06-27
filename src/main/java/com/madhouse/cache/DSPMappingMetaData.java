package com.madhouse.cache;

/**
 * Created by WUJUNFENG on 2017/6/22.
 */
public class DSPMappingMetaData {
    private long adspaceId;
    private long dspId;
    private String mappingKey;
    private String dspMediaId;
    private int os;

    public long getAdspaceId() {
        return adspaceId;
    }

    public void setAdspaceId(long adspaceId) {
        this.adspaceId = adspaceId;
    }

    public long getDspId() {
        return dspId;
    }

    public void setDspId(long dspId) {
        this.dspId = dspId;
    }

    public String getMappingKey() {
        return mappingKey;
    }

    public void setMappingKey(String mappingKey) {
        this.mappingKey = mappingKey;
    }

    public String getDspMediaId() {
        return dspMediaId;
    }

    public void setDspMediaId(String dspMediaId) {
        this.dspMediaId = dspMediaId;
    }

    public int getOs() {
        return os;
    }

    public void setOs(int os) {
        this.os = os;
    }
}
