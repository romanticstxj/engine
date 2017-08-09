package com.madhouse.cache;


import com.alibaba.fastjson.JSON;
import com.madhouse.resource.ResourceManager;
import com.madhouse.ssp.Constant;
import com.madhouse.util.ObjectUtils;
import com.madhouse.util.StringUtil;
import com.madhouse.util.Utility;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.util.ConcurrentHashSet;

import redis.clients.jedis.Jedis;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

/**
 * Created by WUJUNFENG on 2017/5/23.
 */
public class CacheManager implements Runnable {
    private CacheManager(){};
    private static CacheManager cacheManager = new CacheManager();
    public static CacheManager getInstance() {
        return cacheManager;
    }
    private ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();

    //dsp metadata
    private ConcurrentHashMap<Long, DSPMetaData> dspMetaDataMap = new ConcurrentHashMap<Long, DSPMetaData>();
    //media metadata
    private ConcurrentHashMap<Long, MediaMetaData> mediaMetaDataMap = new ConcurrentHashMap<Long, MediaMetaData>();
    //placement metadata
    private ConcurrentHashMap<String, PlcmtMetaData> plcmtMetaDataMap = new ConcurrentHashMap<String, PlcmtMetaData>();
    //adblock metadata
    private ConcurrentHashMap<Long, AdBlockMetaData> adBlockMetaDataMap = new ConcurrentHashMap<Long, AdBlockMetaData>();
    //policy metadata
    private ConcurrentHashMap<Long, PolicyMetaData> policyMetaDataMap = new ConcurrentHashMap<Long, PolicyMetaData>();
    //adspaceId, mediaMappingMetaData
    private ConcurrentHashMap<Long, MediaMappingMetaData> mediaMappingMetaDataMap = new ConcurrentHashMap<Long, MediaMappingMetaData>();
    //adspaceId, <dspId, dspMappingMetaData>
    private ConcurrentHashMap<Long, ConcurrentHashMap<Long, DSPMappingMetaData>> dspMappingMetaDataMap = new ConcurrentHashMap<Long, ConcurrentHashMap<Long, DSPMappingMetaData>>();
    //targeting index
    private ConcurrentHashMap<String, HashSet<Long>> policyTargetMap = new ConcurrentHashMap<String, HashSet<Long>>();
    //blocked policy
    private ConcurrentHashSet<Long> blockedPolicy = new ConcurrentHashSet<>();
    //dspid:material_key:media_id:adspaceid, materialMetaData
    private ConcurrentHashMap<String, MaterialMetaData> materialMetaDataMap = new ConcurrentHashMap<>();

    private Jedis redisMaster = null;
    private Jedis redisSlave = null;

    public boolean init() {
        this.scheduledExecutor.scheduleAtFixedRate(this, 0, 180, TimeUnit.SECONDS);
        return true;
    }

    public HashSet<Long> getPolicyTargetInfo(String key) {
        return policyTargetMap.get(key);
    }

    public ConcurrentHashMap<String, HashSet<Long>> getPolicyTargetMap() {
        return policyTargetMap;
    }

    public void setPolicyTargetMap(ConcurrentHashMap<String, HashSet<Long>> policyTargetMap) {
        this.policyTargetMap = policyTargetMap;
    }

    public MediaMappingMetaData getMediaMapping(long adspaceId) {
        return this.mediaMappingMetaDataMap.get(adspaceId);
    }

    public DSPMappingMetaData getDSPMapping(long dspId, long adspaceId) {
        ConcurrentHashMap<Long, DSPMappingMetaData> var = this.dspMappingMetaDataMap.get(adspaceId);
        if (var != null) {
            return var.get(dspId);
        }

        return null;
    }

    public MaterialMetaData getMaterialMetaData(long dspId, String materialId, long mediaId, long adspaceId) {
        String key = String.format(Constant.CommonKey.MATERIAL_MAPPING_DATA, dspId, materialId, mediaId, adspaceId);
        return this.materialMetaDataMap.get(key);
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

    public void run() {
        this.redisMaster = ResourceManager.getInstance().getJedisPoolMaster().getResource();
        this.redisSlave = ResourceManager.getInstance().getJedisPoolSlave().getResource();

        Object mediaMetaData = this.loadMediaMetaData();
        Object plcmtMetaData = this.loadPlcmtMetaData();
        Object adBlockMetaData = this.loadAdBlockMetaData();
        Object policyMetaData = this.loadPolicyMetaData();
        Object dspMetaData = this.loadDSPMetaData();
        Object mediaMappingData = this.loadMediaMappingData();
        Object dspMappingData = this.loadDSPMappingData();
        Object materialMetaData = this.loadMaterialMappingData();
        Object policyTargetInfo = this.updatePolicyTargetInfo(policyMetaData);

        this.mediaMetaDataMap = (ConcurrentHashMap<Long, MediaMetaData>)mediaMetaData;
        this.plcmtMetaDataMap = (ConcurrentHashMap<String, PlcmtMetaData>)plcmtMetaData;
        this.adBlockMetaDataMap = (ConcurrentHashMap<Long, AdBlockMetaData>)adBlockMetaData;
        this.policyMetaDataMap = (ConcurrentHashMap<Long, PolicyMetaData>)policyMetaData;
        this.dspMetaDataMap = (ConcurrentHashMap<Long, DSPMetaData>)dspMetaData;
        this.mediaMappingMetaDataMap = (ConcurrentHashMap<Long, MediaMappingMetaData>)mediaMappingData;
        this.dspMappingMetaDataMap = (ConcurrentHashMap<Long, ConcurrentHashMap<Long, DSPMappingMetaData>>)dspMappingData;
        this.materialMetaDataMap = (ConcurrentHashMap<String, MaterialMetaData>)materialMetaData;
        this.policyTargetMap = (ConcurrentHashMap<String, HashSet<Long>>)policyTargetInfo;

        this.blockedPolicy.clear();
    }

    private Object loadMaterialMappingData() {
        ConcurrentHashMap<String, MaterialMetaData> var = new ConcurrentHashMap<String, MaterialMetaData>();
        Set<String> set = this.redisSlave.smembers(Constant.CommonKey.ALL_MATERIAL);
        for (String mediaId : set) {
            String text = this.redisSlave.get(String.format(Constant.CommonKey.MATERIAL_META_DATA,mediaId));
            if (!StringUtils.isEmpty(text)) {
                MaterialMetaData mediaMetaData = JSON.parseObject(text, MaterialMetaData.class);
                var.put(String.format(Constant.CommonKey.MATERIAL_MAPPING_DATA, mediaMetaData.getId(),mediaMetaData.getMaterialId(),mediaMetaData.getMediaId(),mediaMetaData.getAdspaceId()), mediaMetaData);
            }
        }
        return var;
    }

    private ConcurrentHashMap<Long, MediaMetaData> loadMediaMetaData() {
        ConcurrentHashMap<Long, MediaMetaData> var = new ConcurrentHashMap<Long, MediaMetaData>();
        Set<String> set = this.redisSlave.smembers(Constant.CommonKey.ALL_MEDIA);
        for (String mediaId : set) {
            String text = this.redisSlave.get(String.format(Constant.CommonKey.MEDIA_META_DATA,mediaId));
            if (!StringUtils.isEmpty(text)) {
                MediaMetaData mediaMetaData = JSON.parseObject(text, MediaMetaData.class);
                var.put(mediaMetaData.getId(), mediaMetaData);
                
            }
        }
        return var;
    }

    private ConcurrentHashMap<String, PlcmtMetaData> loadPlcmtMetaData() {
        ConcurrentHashMap<String, PlcmtMetaData> var = new ConcurrentHashMap<String, PlcmtMetaData>();

        Set<String> set = this.redisSlave.smembers(Constant.CommonKey.ALL_PLACEMENT);
        for (String id : set) {
            String text = this.redisSlave.get(String.format(Constant.CommonKey.PLACEMENT_META_DATA, id));
            if (!StringUtils.isEmpty(text)) {
                PlcmtMetaData metaData = JSON.parseObject(text, PlcmtMetaData.class);
                var.put(String.valueOf(metaData.getId()), metaData);
            }
        }
        return var;
    }

    private ConcurrentHashMap<Long, PolicyMetaData> loadPolicyMetaData() {
        ConcurrentHashMap<Long, PolicyMetaData> var = new ConcurrentHashMap<Long, PolicyMetaData>();
        Set<String> set = this.redisSlave.smembers(Constant.CommonKey.ALL_POLICY);
        for (String mediaId : set) {
            String text = this.redisSlave.get(String.format(Constant.CommonKey.POLICY_META_DATA,mediaId));
            if (!StringUtils.isEmpty(text)) {
                PolicyMetaData metaData = JSON.parseObject(text, PolicyMetaData.class);
                var.put(metaData.getId(), metaData);
                
            }
        }
        return var;
    }

    private ConcurrentHashMap<Long, AdBlockMetaData> loadAdBlockMetaData() {
        ConcurrentHashMap<Long, AdBlockMetaData> var = new ConcurrentHashMap<Long, AdBlockMetaData>();

        Set<String> adblockIds = this.redisSlave.smembers(Constant.CommonKey.ALL_ADBLOCK);
        if (!ObjectUtils.isEmpty(adblockIds)) {
            for (String adblockId : adblockIds) {
                String text = this.redisSlave.get(String.format(Constant.CommonKey.ADBLOCK_META_DATA, adblockId));
                if (!StringUtils.isEmpty(text)) {
                    List<AdBlockMetaData> adBlockMetaDatas = JSON.parseArray(text, AdBlockMetaData.class);
                    for (AdBlockMetaData adBlockMetaData : adBlockMetaDatas) {
                        if (adBlockMetaData.getStatus() >= 0) {
                            var.put(adBlockMetaData.getId(), adBlockMetaData);
                        }
                    }
                }
            }
        }
        return var;
    }

    private ConcurrentHashMap<Long, DSPMetaData> loadDSPMetaData() {
        ConcurrentHashMap<Long, DSPMetaData> var = new ConcurrentHashMap<Long, DSPMetaData>();
        Set<String> set = this.redisSlave.smembers(Constant.CommonKey.ALL_DSP);
        for (String mediaId : set) {
            String text = this.redisSlave.get(String.format(Constant.CommonKey.DSP_META_DATA,mediaId));
            if (!StringUtils.isEmpty(text)) {
                DSPMetaData metaData = JSON.parseObject(text, DSPMetaData.class);
                var.put(metaData.getId() , metaData);
            }
        }
        return var;
    }

    private ConcurrentHashMap<Long, MediaMappingMetaData> loadMediaMappingData() {
        ConcurrentHashMap<Long, MediaMappingMetaData> var = new ConcurrentHashMap<Long, MediaMappingMetaData>();

        Set<String> adspaceIds = this.redisSlave.smembers(Constant.CommonKey.ALL_PLACEMENT);
        if (!ObjectUtils.isEmpty(adspaceIds)) {
            for (String adspaceId : adspaceIds) {
                String text = this.redisSlave.get(String.format(Constant.CommonKey.MEDIA_MAPPING_DATA, adspaceId));
                if (!StringUtils.isEmpty(text)) {
                    MediaMappingMetaData mediaMappingMetaData = JSON.parseObject(text, MediaMappingMetaData.class);
                    var.put(mediaMappingMetaData.getAdspaceId(), mediaMappingMetaData);
                }
            }
        }

        return var;
    }

    private ConcurrentHashMap<Long, ConcurrentHashMap<Long, DSPMappingMetaData>> loadDSPMappingData() {
        ConcurrentHashMap<Long, ConcurrentHashMap<Long, DSPMappingMetaData>> var1 = new ConcurrentHashMap<Long, ConcurrentHashMap<Long, DSPMappingMetaData>>();

        Set<String> adspaceIds = this.redisSlave.smembers(Constant.CommonKey.ALL_PLACEMENT);
        Set<String> dspIds = this.redisSlave.smembers(Constant.CommonKey.ALL_DSP);

        if (!ObjectUtils.isEmpty(adspaceIds) && !ObjectUtils.isEmpty(dspIds)) {
            for (String adspaceId : adspaceIds) {
                for (String dspId : dspIds) {
                    String text = this.redisSlave.get(String.format(Constant.CommonKey.DSP_MAPPING_DATA, adspaceId, dspId));
                    if (!StringUtils.isEmpty(text)) {
                        DSPMappingMetaData dspMappingMetaData = JSON.parseObject(text, DSPMappingMetaData.class);
                        ConcurrentHashMap<Long, DSPMappingMetaData> var2 = var1.get(dspMappingMetaData.getAdspaceId());

                        if (var2 == null) {
                            var2 = new ConcurrentHashMap<>();
                            var1.put(dspMappingMetaData.getAdspaceId(), var2);
                        }

                        var2.put(dspMappingMetaData.getDspId(), dspMappingMetaData);
                    }
                }
            }
        }

        return var1;
    }

    private ConcurrentHashMap<String, HashSet<Long>> updatePolicyTargetInfo(Object metaData) {
        ConcurrentHashMap<String, HashSet<Long>> var = new ConcurrentHashMap<>();

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
        int weekDay = cal.get(Calendar.DAY_OF_WEEK) - 1;
        int currentHour = cal.get(Calendar.HOUR_OF_DAY);

        ConcurrentHashMap<Long, PolicyMetaData> policyMetaDataMap = (ConcurrentHashMap<Long, PolicyMetaData>)metaData;

        for (Map.Entry entry : policyMetaDataMap.entrySet()) {
            PolicyMetaData policyMetaData = (PolicyMetaData)entry.getValue();

            if (policyMetaData.getControlType() != Constant.PolicyControlType.NONE) {
                if (policyMetaData.getControlType() == Constant.PolicyControlType.DAILY) {
                    if (policyMetaData.getControlMethod() == Constant.PolicyControlMethod.FAST) {
                        String text = this.redisSlave.get(String.format(Constant.CommonKey.POLICY_CONTORL_DAILY, policyMetaData.getId(), currentDate));
                        long count = StringUtils.isEmpty(text) ? 0 : Long.parseLong(text);
                        if (count >= policyMetaData.getMaxCount()) {
                            continue;
                        }
                    } else {
                        int pastHours = 0;
                        int totalHours = 0;
                        if (ObjectUtils.isEmpty(policyMetaData.getWeekDayHours())) {
                            pastHours = currentHour + 1;
                            totalHours = 24;
                        } else {
                            List<Integer> hours = policyMetaData.getWeekDayHours().get(weekDay);
                            for (int hour : hours) {
                                if (hour <= currentHour) {
                                    pastHours += 1;
                                } else {
                                    break;
                                }
                            }

                            totalHours = hours.size();
                        }

                        String text = this.redisSlave.get(String.format(Constant.CommonKey.POLICY_CONTORL_DAILY, policyMetaData.getId(), currentDate));
                        long count = StringUtils.isEmpty(text) ? 0 : Long.parseLong(text);
                        if (count >= ((double)policyMetaData.getMaxCount() * pastHours / totalHours)) {
                            continue;
                        }
                    }
                } else {
                    if (policyMetaData.getControlMethod() == Constant.PolicyControlMethod.FAST) {
                        String text = this.redisSlave.get(String.format(Constant.CommonKey.POLICY_CONTORL_TOTAL, policyMetaData.getId()));
                        long count = StringUtils.isEmpty(text) ? 0 : Long.parseLong(text);
                        if (count >= policyMetaData.getMaxCount()) {
                            continue;
                        }
                    } else {
                        if (StringUtils.isEmpty(policyMetaData.getEndDate())) {
                            continue;
                        }

                        int pastDays = Utility.dateDiff(StringUtil.toDate(currentDate), StringUtil.toDate(policyMetaData.getStartDate())) + 1;
                        int totalDays = Utility.dateDiff(StringUtil.toDate(policyMetaData.getEndDate()), StringUtil.toDate(policyMetaData.getStartDate())) + 1;

                        String text = this.redisSlave.get(String.format(Constant.CommonKey.POLICY_CONTORL_DAILY, policyMetaData.getId(), currentDate));
                        long count = StringUtils.isEmpty(text) ? 0 : Long.parseLong(text);
                        if (count >= ((double)policyMetaData.getMaxCount() * pastDays / totalDays)) {
                            continue;
                        }
                    }
                }
            }

            if (currentDate.compareTo(policyMetaData.getStartDate()) >= 0 && (StringUtils.isEmpty(policyMetaData.getEndDate()) || currentDate.compareTo(policyMetaData.getEndDate()) <= 0)) {
                //placement
                if (policyMetaData.getAdspaceInfoMap() != null && !policyMetaData.getAdspaceInfoMap().isEmpty()) {
                    for (Map.Entry entry1 : policyMetaData.getAdspaceInfoMap().entrySet()) {
                        PolicyMetaData.AdspaceInfo adspaceInfo = (PolicyMetaData.AdspaceInfo)entry1.getValue();
                        if (adspaceInfo.getStatus() > 0) {
                            String adspaceKey = String.format("%d-%s", adspaceInfo.getId(), StringUtil.toString(adspaceInfo.getDealId()));
                            String key = String.format(Constant.CommonKey.TARGET_KEY, policyMetaData.getDeliveryType(), Constant.TargetType.PLACEMENT, adspaceKey);
                            HashSet<Long> var2 = var.get(key);
                            if (var2 == null) {
                                var2 = new HashSet<>();
                                var.put(key, var2);
                            }

                            var2.add(policyMetaData.getId());
                        }
                    }
                } else {
                    continue;
                }

                //weekhour
                if (!ObjectUtils.isEmpty(policyMetaData.getWeekDayHours())) {
                    Map<Integer, List<Integer>> weekHours = policyMetaData.getWeekDayHours();
                    for (Map.Entry entry1 : weekHours.entrySet()) {
                        List<Integer> hours = (List<Integer>)entry1.getValue();
                        for (int hour : hours) {
                            String key = String.format(Constant.CommonKey.TARGET_KEY, policyMetaData.getDeliveryType(), Constant.TargetType.WEEKDAY_HOUR, String.format("%d%02d", (Integer)entry1.getKey(), hour));
                            HashSet<Long> var2 = var.get(key);
                            if (var2 == null) {
                                var2 = new HashSet<>();
                                var.put(key, var2);
                            }

                            var2.add(policyMetaData.getId());
                        }
                    }
                } else {
                    String key = String.format(Constant.CommonKey.TARGET_KEY, policyMetaData.getDeliveryType(), Constant.TargetType.WEEKDAY_HOUR, "");
                    HashSet<Long> var2 = var.get(key);
                    if (var2 == null) {
                        var2 = new HashSet<>();
                        var.put(key, var2);
                    }

                    var2.add(policyMetaData.getId());
                }

                //location
                if (policyMetaData.getLocation() != null && !policyMetaData.getLocation().isEmpty()) {
                    for (String location : policyMetaData.getLocation()) {
                        String key = String.format(Constant.CommonKey.TARGET_KEY, policyMetaData.getDeliveryType(), Constant.TargetType.LOCATION, location);
                        HashSet<Long> var2 = var.get(key);
                        if (var2 == null) {
                            var2 = new HashSet<>();
                            var.put(key, var2);
                        }

                        var2.add(policyMetaData.getId());
                    }
                } else {
                    String key = String.format(Constant.CommonKey.TARGET_KEY, policyMetaData.getDeliveryType(), Constant.TargetType.LOCATION, "");
                    HashSet<Long> var2 = var.get(key);
                    if (var2 == null) {
                        var2 = new HashSet<>();
                        var.put(key, var2);
                    }

                    var2.add(policyMetaData.getId());
                }

                //os
                if (policyMetaData.getOs() != null && !policyMetaData.getOs().isEmpty()) {
                    for (int os : policyMetaData.getOs()) {
                        String key = String.format(Constant.CommonKey.TARGET_KEY, policyMetaData.getDeliveryType(), Constant.TargetType.OS, Integer.toString(os));
                        HashSet<Long> var2 = var.get(key);
                        if (var2 == null) {
                            var2 = new HashSet<>();
                            var.put(key, var2);
                        }

                        var2.add(policyMetaData.getId());
                    }
                } else {
                    String key = String.format(Constant.CommonKey.TARGET_KEY, policyMetaData.getDeliveryType(), Constant.TargetType.OS, "");
                    HashSet<Long> var2 = var.get(key);
                    if (var2 == null) {
                        var2 = new HashSet<>();
                        var.put(key, var2);
                    }

                    var2.add(policyMetaData.getId());
                }

                //connection type
                if (policyMetaData.getConnectionType() != null && !policyMetaData.getConnectionType().isEmpty()) {
                    for (int connectionType : policyMetaData.getConnectionType()) {
                        String key = String.format(Constant.CommonKey.TARGET_KEY, policyMetaData.getDeliveryType(), Constant.TargetType.CONNECTION_TYPE, Integer.toString(connectionType));
                        HashSet<Long> var2 = var.get(key);
                        if (var2 == null) {
                            var2 = new HashSet<>();
                            var.put(key, var2);
                        }

                        var2.add(policyMetaData.getId());
                    }
                } else {
                    String key = String.format(Constant.CommonKey.TARGET_KEY, policyMetaData.getDeliveryType(), Constant.TargetType.CONNECTION_TYPE, "");
                    HashSet<Long> var2 = var.get(key);
                    if (var2 == null) {
                        var2 = new HashSet<>();
                        var.put(key, var2);
                    }

                    var2.add(policyMetaData.getId());
                }
            }
        }

        return var;
    }

    public void blockPolicy(long id) {
        this.blockedPolicy.add(id);
    }

    public ConcurrentHashSet<Long> getBlockedPolicy() {
        return blockedPolicy;
    }

    public void setBlockedPolicy(ConcurrentHashSet<Long> blockedPolicy) {
        this.blockedPolicy = blockedPolicy;
    }
}