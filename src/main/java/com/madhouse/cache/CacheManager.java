package com.madhouse.cache;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by WUJUNFENG on 2017/5/23.
 */
public class CacheManager implements Runnable {
    private ConcurrentHashMap<Long, DSPMetaData> dspMetaDataMap = new ConcurrentHashMap<Long, DSPMetaData>();
    private ConcurrentHashMap<Long, MediaMetaData> mediaMetaDataMap = new ConcurrentHashMap<Long, MediaMetaData>();
    private ConcurrentHashMap<String, PlcmtMetaData> plcmtMetaDataMap = new ConcurrentHashMap<String, PlcmtMetaData>();
    private ConcurrentHashMap<Long, AdBlockMetaData> adBlockMetaDataMap = new ConcurrentHashMap<Long, AdBlockMetaData>();
    private ConcurrentHashMap<Long, PolicyMetaData> policyMetaDataMap = new ConcurrentHashMap<Long, PolicyMetaData>();
    //adspaceId, MediaMappingMetaData
    private ConcurrentHashMap<Long, MediaMappingMetaData> mediaMappingMetaDataMap = new ConcurrentHashMap<Long, MediaMappingMetaData>();
    //dspid, <adspaceId, DSPMappingMetaData>
    private ConcurrentHashMap<Long, ConcurrentHashMap<Long, DSPMappingMetaData>> dspMappingMetaDataMap = new ConcurrentHashMap<Long, ConcurrentHashMap<Long, DSPMappingMetaData>>();

    
    
    private CacheManager(){};
    private static CacheManager cacheManager = new CacheManager();
    public static CacheManager getInstance() {
        return cacheManager;
    }

    public boolean init() {
       
       

        return true;
    }

    public MediaMappingMetaData getMediaMapping(long adspaceId) {
        return this.mediaMappingMetaDataMap.get(adspaceId);
    }

    public DSPMappingMetaData getDSPMapping(long dspid, long adspaceId) {
        ConcurrentHashMap<Long, DSPMappingMetaData> var = this.dspMappingMetaDataMap.get(dspid);
        if (var != null) {
            return var.get(adspaceId);
        }

        return null;
    }

    public DSPMetaData getDSPMetaData(long id) {
        return this.dspMetaDataMap.get(id);
    }

    public MediaMetaData getMediaMetaData(long id) {
        return this.mediaMetaDataMap.get(id);
    }

    public PlcmtMetaData getPlcmtMetaData(String key) {
        return this.plcmtMetaDataMap.get(key);
    }

    public AdBlockMetaData getAdBlockMetaData(long id) {
        return this.adBlockMetaDataMap.get(id);
    }

    public PolicyMetaData getPolicyMetaData(long id) {
        return this.policyMetaDataMap.get(id);
    }

    public String getLocation(String ip) {
        return "";
    }
    public void run() {

    }
}