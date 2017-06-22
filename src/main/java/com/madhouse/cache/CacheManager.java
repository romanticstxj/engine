package com.madhouse.cache;

import com.madhouse.dsp.DSPBaseHandler;
import com.madhouse.media.MediaBaseHandler;
import org.apache.commons.lang3.tuple.Pair;

import javax.print.attribute.standard.Media;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

/**
 * Created by WUJUNFENG on 2017/5/23.
 */
public class CacheManager implements Runnable {
    private ConcurrentHashMap<String, Integer> mediaApiType = new ConcurrentHashMap<String, Integer>();
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
        this.mediaApiType.put("/adcall/bidrequest", 0);
        this.mediaApiType.put("/client/bidrequest", 0);
        this.mediaApiType.put("/motv/bidrequest", 0);
        this.mediaApiType.put("/adcall/xtrader/bidrequest", 1);
        this.mediaApiType.put("/adcall/dianping/bidrequest", 1);
        this.mediaApiType.put("/adcall/xiaomi/bidrequest", 1);
        this.mediaApiType.put("/adcall/moweather/bidrequest", 1);
        this.mediaApiType.put("/adcall/baofeng/bidrequest", 1);
        this.mediaApiType.put("/adcall/toutiao/bidrequest", 1);
        this.mediaApiType.put("/adcall/momo/bidrequest", 1);
        this.mediaApiType.put("/adcall/vam/bidrequest", 1);

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

    public int getMediaApiType(String url) {
        return this.mediaApiType.get(url);
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
