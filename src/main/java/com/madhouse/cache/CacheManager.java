package com.madhouse.cache;


import com.alibaba.fastjson.JSON;
import com.madhouse.resource.ResourceManager;
import com.madhouse.ssp.Constant;
import com.madhouse.util.StringUtil;
import kafka.tools.StateChangeLogMerger;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.util.ConcurrentHashSet;
import redis.clients.jedis.Jedis;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
    //metadata
    private ConcurrentHashMap<Long, DSPMetaData> dspMetaDataMap = new ConcurrentHashMap<Long, DSPMetaData>();
    private ConcurrentHashMap<Long, MediaMetaData> mediaMetaDataMap = new ConcurrentHashMap<Long, MediaMetaData>();
    private ConcurrentHashMap<String, PlcmtMetaData> plcmtMetaDataMap = new ConcurrentHashMap<String, PlcmtMetaData>();
    private ConcurrentHashMap<Long, AdBlockMetaData> adBlockMetaDataMap = new ConcurrentHashMap<Long, AdBlockMetaData>();
    private ConcurrentHashMap<Long, PolicyMetaData> policyMetaDataMap = new ConcurrentHashMap<Long, PolicyMetaData>();

    //adspaceId, mediaMappingMetaData
    private ConcurrentHashMap<Long, MediaMappingMetaData> mediaMappingMetaDataMap = new ConcurrentHashMap<Long, MediaMappingMetaData>();
    //dspid, <adspaceId, dspMappingMetaData>
    private ConcurrentHashMap<Long, ConcurrentHashMap<Long, DSPMappingMetaData>> dspMappingMetaDataMap = new ConcurrentHashMap<Long, ConcurrentHashMap<Long, DSPMappingMetaData>>();
    //targeting index
    private ConcurrentHashMap<String, HashSet<Long>> policyTargetMap = new ConcurrentHashMap<String, HashSet<Long>>();
    //blocked policy
    private ConcurrentHashSet<Long> blockedPolicy = new ConcurrentHashSet<>();

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

    public void run() {
        this.redisMaster = ResourceManager.getInstance().getJedisPoolMaster().getResource();
        this.redisSlave = ResourceManager.getInstance().getJedisPoolSlave().getResource();

        this.mediaMetaDataMap = this.loadMediaMetaData();
        this.plcmtMetaDataMap = this.loadPlcmtMetaData();
        this.adBlockMetaDataMap = this.loadAdBlockMetaData();
        this.policyMetaDataMap = this.loadPolicyMetaData();
        this.dspMetaDataMap = this.loadDSPMetaData();
        this.mediaMappingMetaDataMap = this.loadMediaMappingData();
        this.dspMappingMetaDataMap = this.loadDSPMappingData();
        this.policyTargetMap = this.updatePolicyTargetInfo();
    }

    private ConcurrentHashMap<Long, MediaMetaData> loadMediaMetaData() {
        ConcurrentHashMap<Long, MediaMetaData> var = new ConcurrentHashMap<Long, MediaMetaData>();

        String text = this.redisSlave.get(Constant.CommonKey.MEDIA_META_DATA);
        if (!StringUtils.isEmpty(text)) {
            List<MediaMetaData> mediaMetaDatas = JSON.parseArray(text, MediaMetaData.class);
            for (MediaMetaData mediaMetaData : mediaMetaDatas) {
                if (mediaMetaData.getStatus() >= 0) {
                    var.put(mediaMetaData.getId(), mediaMetaData);
                }
            }
        }

        return var;
    }

    private ConcurrentHashMap<String, PlcmtMetaData> loadPlcmtMetaData() {
        ConcurrentHashMap<String, PlcmtMetaData> var = new ConcurrentHashMap<String, PlcmtMetaData>();

        String text = this.redisSlave.get(Constant.CommonKey.PLACEMENT_META_DATA);
        if (!StringUtils.isEmpty(text)) {
            List<PlcmtMetaData> plcmtMetaDatas = JSON.parseArray(text, PlcmtMetaData.class);
            for (PlcmtMetaData plcmtMetaData : plcmtMetaDatas) {
                if (plcmtMetaData.getStatus() >= 0) {
                    var.put(plcmtMetaData.getAdspaceKey(), plcmtMetaData);
                }
            }
        }

        return var;
    }

    private ConcurrentHashMap<Long, PolicyMetaData> loadPolicyMetaData() {
        ConcurrentHashMap<Long, PolicyMetaData> var = new ConcurrentHashMap<Long, PolicyMetaData>();

        String text = this.redisSlave.get(Constant.CommonKey.POLICY_META_DATA);
        if (!StringUtils.isEmpty(text)) {
            List<PolicyMetaData> policyMetaDatas = JSON.parseArray(text, PolicyMetaData.class);
            for (PolicyMetaData policyMetaData : policyMetaDatas) {
                if (policyMetaData.getStatus() >= 0) {
                    var.put(policyMetaData.getId(), policyMetaData);
                }
            }
        }

        return var;
    }

    private ConcurrentHashMap<Long, AdBlockMetaData> loadAdBlockMetaData() {
        ConcurrentHashMap<Long, AdBlockMetaData> var = new ConcurrentHashMap<Long, AdBlockMetaData>();

        String text = this.redisSlave.get(Constant.CommonKey.ADBLOCK_META_DATA);
        if (!StringUtils.isEmpty(text)) {
            List<AdBlockMetaData> adBlockMetaDatas = JSON.parseArray(text, AdBlockMetaData.class);
            for (AdBlockMetaData adBlockMetaData : adBlockMetaDatas) {
                if (adBlockMetaData.getStatus() >= 0) {
                    var.put(adBlockMetaData.getId(), adBlockMetaData);
                }
            }
        }

        return var;
    }

    private ConcurrentHashMap<Long, DSPMetaData> loadDSPMetaData() {
        ConcurrentHashMap<Long, DSPMetaData> var = new ConcurrentHashMap<Long, DSPMetaData>();

        String text = this.redisSlave.get(Constant.CommonKey.DSP_META_DATA);
        if (!StringUtils.isEmpty(text)) {
            List<DSPMetaData> dspMetaDatas = JSON.parseArray(text, DSPMetaData.class);
            for (DSPMetaData dspMetaData : dspMetaDatas) {
                if (dspMetaData.getStatus() >= 0) {
                    var.put(dspMetaData.getId(), dspMetaData);
                }
            }
        }

        return var;
    }

    private ConcurrentHashMap<Long, MediaMappingMetaData> loadMediaMappingData() {
        ConcurrentHashMap<Long, MediaMappingMetaData> var = new ConcurrentHashMap<Long, MediaMappingMetaData>();

        String text = this.redisSlave.get(Constant.CommonKey.MEDIA_MAPPING_DATA);
        if (!StringUtils.isEmpty(text)) {
            List<MediaMappingMetaData> mediaMappingMetaDatas = JSON.parseArray(text, MediaMappingMetaData.class);
            for (MediaMappingMetaData mediaMappingMetaData : mediaMappingMetaDatas) {
                var.put(mediaMappingMetaData.getAdspaceId(), mediaMappingMetaData);
            }
        }

        return var;
    }

    private ConcurrentHashMap<Long, ConcurrentHashMap<Long, DSPMappingMetaData>> loadDSPMappingData() {
        ConcurrentHashMap<Long, ConcurrentHashMap<Long, DSPMappingMetaData>> var1 = new ConcurrentHashMap<Long, ConcurrentHashMap<Long, DSPMappingMetaData>>();

        String text = this.redisSlave.get(Constant.CommonKey.DSP_MAPPING_DATA);
        if (!StringUtils.isEmpty(text)) {
            List<DSPMappingMetaData> dspMappingMetaDatas = JSON.parseArray(text, DSPMappingMetaData.class);
            for (DSPMappingMetaData dspMappingMetaData : dspMappingMetaDatas) {
                ConcurrentHashMap<Long, DSPMappingMetaData> var2 = var1.get(dspMappingMetaData.getDspId());

                if (var2 == null) {
                    var2 = new ConcurrentHashMap<>();
                    var1.put(dspMappingMetaData.getDspId(), var2);
                }

                var2.put(dspMappingMetaData.getAdspaceId(), dspMappingMetaData);
            }
        }

        return var1;
    }

    private ConcurrentHashMap<String, HashSet<Long>> updatePolicyTargetInfo() {
        ConcurrentHashMap<String, HashSet<Long>> var = new ConcurrentHashMap<>();
        String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        for (Map.Entry entry : this.policyMetaDataMap.entrySet()) {
            PolicyMetaData policyMetaData = (PolicyMetaData)entry.getValue();

            if (policyMetaData.getControlType() != Constant.PolicyControlType.NULL) {
                if (policyMetaData.getControlType() == Constant.PolicyControlType.TOTAL) {
                    String text = this.redisSlave.get(String.format(Constant.CommonKey.POLICY_CONTORL_TOTAL, policyMetaData.getId()));
                    long count = StringUtils.isEmpty(text) ? 0 : Long.parseLong(text);
                    if (count >= policyMetaData.getMaxCount()) {
                        continue;
                    }
                } else {
                    String text = this.redisSlave.get(String.format(Constant.CommonKey.POLICY_CONTORL_DAY, policyMetaData.getId(), currentDate));
                    long count = StringUtils.isEmpty(text) ? 0 : Long.parseLong(text);
                    if (count >= policyMetaData.getMaxCount()) {
                        continue;
                    }
                }
            }

            if (currentDate.compareTo(policyMetaData.getStartDate()) >= 0 && currentDate.compareTo(policyMetaData.getEndDate()) <= 0) {
                //placement
                if (policyMetaData.getAdspaceList() != null && !policyMetaData.getAdspaceList().isEmpty()) {
                    for (PolicyMetaData.PlcmtInfo plcmtInfo : policyMetaData.getAdspaceList()) {
                        if (plcmtInfo.getStatus() > 0) {
                            String key = String.format(Constant.CommonKey.TARGET_KEY, Constant.TargetType.LOCATION, Long.toString(plcmtInfo.getId()));
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
                if (policyMetaData.getWeekHours() != null) {
                    Map<Integer, List<Integer>> weekHours = policyMetaData.getWeekHours();
                    for (Map.Entry entry1 : weekHours.entrySet()) {
                        int week = (Integer)entry1.getKey();
                        List<Integer> hours = (List<Integer>)entry1.getValue();
                        for (int hour : hours) {
                            String key = String.format(Constant.CommonKey.TARGET_KEY, Constant.TargetType.WEEK_HOUR, String.format("%d%02d", week, hour));
                            HashSet<Long> var2 = var.get(key);
                            if (var2 == null) {
                                var2 = new HashSet<>();
                                var.put(key, var2);
                            }

                            var2.add(policyMetaData.getId());
                        }
                    }
                } else {
                    String key = String.format(Constant.CommonKey.TARGET_KEY, Constant.TargetType.WEEK_HOUR, "");
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
                        String key = String.format(Constant.CommonKey.TARGET_KEY, Constant.TargetType.LOCATION, location);
                        HashSet<Long> var2 = var.get(key);
                        if (var2 == null) {
                            var2 = new HashSet<>();
                            var.put(key, var2);
                        }

                        var2.add(policyMetaData.getId());
                    }
                } else {
                    String key = String.format(Constant.CommonKey.TARGET_KEY, Constant.TargetType.LOCATION, "");
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
                        String key = String.format(Constant.CommonKey.TARGET_KEY, Constant.TargetType.OS, Integer.toString(os));
                        HashSet<Long> var2 = var.get(key);
                        if (var2 == null) {
                            var2 = new HashSet<>();
                            var.put(key, var2);
                        }

                        var2.add(policyMetaData.getId());
                    }
                } else {
                    String key = String.format(Constant.CommonKey.TARGET_KEY, Constant.TargetType.OS, "");
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
                        String key = String.format(Constant.CommonKey.TARGET_KEY, Constant.TargetType.CONNECTION_TYPE, Integer.toString(connectionType));
                        HashSet<Long> var2 = var.get(key);
                        if (var2 == null) {
                            var2 = new HashSet<>();
                            var.put(key, var2);
                        }

                        var2.add(policyMetaData.getId());
                    }
                } else {
                    String key = String.format(Constant.CommonKey.TARGET_KEY, Constant.TargetType.CONNECTION_TYPE, "");
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