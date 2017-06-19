package com.madhouse.cache;

import com.madhouse.dsp.DSPBaseHandler;
import com.madhouse.media.MediaBaseHandler;
import org.apache.commons.lang3.tuple.Pair;

import javax.print.attribute.standard.Media;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by WUJUNFENG on 2017/5/23.
 */
public class CacheManager {
    private Map<String, Integer> mediaApiType = new HashMap<String, Integer>();
    private Map<Long, DSPMetaData> dspMetaDataMap = new HashMap<Long, DSPMetaData>();
    private Map<Long, MediaMetaData> mediaMetaDataMap = new HashMap<Long, MediaMetaData>();
    private Map<Long, PlcmtMetaData> plcmtMetaDataMap = new HashMap<Long, PlcmtMetaData>();
    private Map<Long, AdBlockMetaData> adBlockMetaDataMap = new HashMap<Long, AdBlockMetaData>();
    private Map<Long, PolicyMetaData> policyMetaDataMap = new HashMap<Long, PolicyMetaData>();
    private Map<Integer, DSPBaseHandler> dspBaseHandlerMap = new HashMap<Integer, DSPBaseHandler>();
    //apitype, handler
    private Map<Integer, MediaBaseHandler> mediaBaseHandlerMap = new HashMap<Integer, MediaBaseHandler>();
    //adspacekey, mediaid, plcmtid
    private Map<String, Pair<Long, Long>> mediaPlcmtMappingMap = new HashMap<String, Pair<Long, Long>>();
    //dspid, mediaid, plcmtid, dspplcmtid
    private Map<Long, Map<Long, Map<Long, String>>> dspPlcmtMappingMap = new HashMap<Long, Map<Long, Map<Long, String>>>();

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

    public int getMediaApiType(String url) {
        return this.mediaApiType.get(url);
    }

    public DSPMetaData getDSPMetaData(long id) {
        return this.dspMetaDataMap.get(id);
    }

    public MediaMetaData getMediaMetaData(long id) {
        return this.mediaMetaDataMap.get(id);
    }

    public PlcmtMetaData getPlcmtMetaData(long id) {
        return this.plcmtMetaDataMap.get(id);
    }

    public AdBlockMetaData getAdBlockMetaData(long id) {
        return this.adBlockMetaDataMap.get(id);
    }

    public PolicyMetaData getPolicyMetaData(long id) {
        return this.policyMetaDataMap.get(id);
    }

    public DSPBaseHandler getDSPBaseHandler(int apitype) {
        return this.dspBaseHandlerMap.get(apitype);
    }

    public MediaBaseHandler getMediaBaseHandler(int apitype) {
        return this.mediaBaseHandlerMap.get(apitype);
    }

    public Pair<Long, Long> mediaPlcmtMapping(String id) {
        return this.mediaPlcmtMappingMap.get(id);
    }

    public String dspPlcmtMapping(long dspid, long mid, long plcmtid) {
        Map<Long, Map<Long, String>> var1 = this.dspPlcmtMappingMap.get(dspid);
        if (var1 != null) {
            Map<Long, String> var2 = var1.get(mid);
            if (var2 != null) {
                return var2.get(plcmtid);
            }
        }

        return null;
    }

    public String getLocation(String ip) {
        return "";
    }
}
