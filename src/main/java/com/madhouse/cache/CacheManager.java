package com.madhouse.cache;


import com.alibaba.fastjson.JSON;
import com.madhouse.resource.ResourceManager;
import com.madhouse.ssp.Constant;
import com.madhouse.ssp.LoggerUtil;
import com.madhouse.util.ObjectUtils;
import com.madhouse.util.StringUtil;
import com.madhouse.util.Utility;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
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

    private static Logger logger = LoggerUtil.getInstance().getPremiummadlogger();
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
        logger.info("load metadata cache begin.");

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

        if (this.redisMaster != null) {
            this.redisMaster.close();
        }

        if (this.redisSlave != null) {
            this.redisSlave.close();
        }

        logger.info("load metadata cache end.");
    }

    private Object loadMaterialMappingData() {
        ConcurrentHashMap<String, MaterialMetaData> var = new ConcurrentHashMap<String, MaterialMetaData>();

        Set<String> materialIds = this.redisSlave.smembers(Constant.CommonKey.ALL_MATERIAL);
        if (!ObjectUtils.isEmpty(materialIds)) {
            for (String materialId : materialIds) {
                String text = this.redisSlave.get(String.format(Constant.CommonKey.MATERIAL_META_DATA, materialId));
                if (!StringUtils.isEmpty(text)) {
                    MaterialMetaData mediaMetaData = JSON.parseObject(text, MaterialMetaData.class);
                    var.put(String.format(Constant.CommonKey.MATERIAL_MAPPING_DATA, mediaMetaData.getId(),mediaMetaData.getMaterialId(),mediaMetaData.getMediaId(),mediaMetaData.getAdspaceId()), mediaMetaData);
                }
            }
        }
        return var;
    }

    private ConcurrentHashMap<Long, MediaMetaData> loadMediaMetaData() {
        ConcurrentHashMap<Long, MediaMetaData> var = new ConcurrentHashMap<Long, MediaMetaData>();

        Set<String> mediaIds = this.redisSlave.smembers(Constant.CommonKey.ALL_MEDIA);
        if (!ObjectUtils.isEmpty(mediaIds)) {
            for (String mediaId : mediaIds) {
                String text = this.redisSlave.get(String.format(Constant.CommonKey.MEDIA_META_DATA, mediaId));
                if (!StringUtils.isEmpty(text)) {
                    MediaMetaData mediaMetaData = JSON.parseObject(text, MediaMetaData.class);
                    var.put(mediaMetaData.getId(), mediaMetaData);

                }
            }
        }
        return var;
    }

    private ConcurrentHashMap<String, PlcmtMetaData> loadPlcmtMetaData() {
        ConcurrentHashMap<String, PlcmtMetaData> var = new ConcurrentHashMap<String, PlcmtMetaData>();

        Set<String> plcmtIds = this.redisSlave.smembers(Constant.CommonKey.ALL_PLACEMENT);
        if (!ObjectUtils.isEmpty(plcmtIds)) {
            for (String plcmtId : plcmtIds) {
                String text = this.redisSlave.get(String.format(Constant.CommonKey.PLACEMENT_META_DATA, plcmtId));
                if (!StringUtils.isEmpty(text)) {
                    PlcmtMetaData metaData = JSON.parseObject(text, PlcmtMetaData.class);
                    var.put(String.valueOf(metaData.getId()), metaData);
                }
            }
        }
        return var;
    }

    private ConcurrentHashMap<Long, PolicyMetaData> loadPolicyMetaData() {
        ConcurrentHashMap<Long, PolicyMetaData> var = new ConcurrentHashMap<Long, PolicyMetaData>();

        Set<String> policyIds = this.redisSlave.smembers(Constant.CommonKey.ALL_POLICY);
        if (!ObjectUtils.isEmpty(policyIds)) {
            for (String policyId : policyIds) {
                String text = this.redisSlave.get(String.format(Constant.CommonKey.POLICY_META_DATA, policyId));
                if (!StringUtils.isEmpty(text)) {
                    PolicyMetaData metaData = JSON.parseObject(text, PolicyMetaData.class);
                    var.put(metaData.getId(), metaData);

                }
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

        Set<String> dspIds = this.redisSlave.smembers(Constant.CommonKey.ALL_DSP);
        if (!ObjectUtils.isEmpty(dspIds)) {
            for (String dspId : dspIds) {
                String text = this.redisSlave.get(String.format(Constant.CommonKey.DSP_META_DATA, dspId));
                if (!StringUtils.isEmpty(text)) {
                    DSPMetaData metaData = JSON.parseObject(text, DSPMetaData.class);
                    var.put(metaData.getId() , metaData);
                }
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

        ConcurrentHashMap<Long, PolicyMetaData> policyMetaDataMap = (ConcurrentHashMap<Long, PolicyMetaData>)metaData;

        for (Map.Entry entry : policyMetaDataMap.entrySet()) {
            PolicyMetaData policyMetaData = (PolicyMetaData)entry.getValue();

            //δ��ʼ�����ѽ���
            if (currentDate.compareTo(policyMetaData.getStartDate()) < 0 ||
                    (!StringUtils.isEmpty(policyMetaData.getEndDate()) && currentDate.compareTo(policyMetaData.getEndDate()) > 0)) {
                continue;
            }

            //�޽������ڡ�������������Ͷ��
            if (StringUtils.isEmpty(policyMetaData.getEndDate()) &&
                    policyMetaData.getControlType() == Constant.PolicyControlType.TOTAL &&
                    policyMetaData.getControlMethod() == Constant.PolicyControlMethod.AVERAGE) {
                continue;
            }

            //����Ч���λ����ЧDSP
            if (ObjectUtils.isEmpty(policyMetaData.getAdspaceInfoMap()) || ObjectUtils.isEmpty(policyMetaData.getDspInfoMap())) {
                continue;
            }

            this.redisMaster.set(String.format(Constant.CommonKey.POLICY_CONTORL_TOTAL, policyMetaData.getId()), "0", "NX");
            this.redisMaster.set(String.format(Constant.CommonKey.POLICY_CONTORL_DAILY, policyMetaData.getId(), currentDate), "0", "NX", "EX", 86400);

            if (!StringUtils.isEmpty(policyMetaData.getEndDate())) {
                int totalDays = Utility.dateDiff(StringUtil.toDate(policyMetaData.getEndDate()), StringUtil.toDate(currentDate)) + 1;
                this.redisMaster.expire(String.format(Constant.CommonKey.POLICY_CONTORL_TOTAL, policyMetaData.getId()), totalDays * 86400);
            }

            if (policyMetaData.getControlType() != Constant.PolicyControlType.NONE) {
                String totalCount = this.redisSlave.get(String.format(Constant.CommonKey.POLICY_CONTORL_TOTAL, policyMetaData.getId()));
                String dailyCount = this.redisSlave.get(String.format(Constant.CommonKey.POLICY_CONTORL_DAILY, policyMetaData.getId(), currentDate));

                if (!this.policyQuantityControl(policyMetaData, Long.parseLong(totalCount), Long.parseLong(dailyCount))) {
                    continue;
                }
            }

            //placement
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

    public boolean policyQuantityControl(PolicyMetaData policyMetaData, long totalCount, long dailyCount) {
        try {
            if (policyMetaData.getControlType() == Constant.PolicyControlType.NONE) {
                return true;
            }

            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());

            int weekDay = cal.get(Calendar.DAY_OF_WEEK) - 1;
            int currentHour = cal.get(Calendar.HOUR_OF_DAY);

            if (policyMetaData.getControlType() == Constant.PolicyControlType.TOTAL) {
                if (policyMetaData.getControlMethod() == Constant.PolicyControlMethod.AVERAGE) {
                    int pastDays = Utility.dateDiff(cal.getTime(), StringUtil.toDate(policyMetaData.getStartDate())) + 1;
                    int totalDays = Utility.dateDiff(StringUtil.toDate(policyMetaData.getEndDate()), StringUtil.toDate(policyMetaData.getStartDate())) + 1;

                    if (dailyCount >= ((double)policyMetaData.getMaxCount() * pastDays / totalDays)) {
                        return false;
                    }
                } else {
                    if (totalCount >= policyMetaData.getMaxCount()) {
                        return false;
                    }
                }
            } else {
                if (policyMetaData.getControlMethod() == Constant.PolicyControlMethod.AVERAGE) {
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

                    if (dailyCount >= ((double)policyMetaData.getMaxCount() * pastHours / totalHours)) {
                        return false;
                    }
                } else {
                    if (dailyCount >= policyMetaData.getMaxCount()) {
                        return false;
                    }
                }
            }
        } catch (Exception ex) {
            logger.error(ex.toString());
            return false;
        }

        return true;
    }
}