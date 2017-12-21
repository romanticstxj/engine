package com.madhouse.cache;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.madhouse.util.EncryptUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.util.ConcurrentHashSet;

import redis.clients.jedis.Jedis;

import com.alibaba.fastjson.JSON;
import com.madhouse.resource.ResourceManager;
import com.madhouse.ssp.Constant;
import com.madhouse.ssp.LoggerUtil;
import com.madhouse.util.ObjectUtils;
import com.madhouse.util.StringUtil;
import com.madhouse.util.Utility;

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

    public class MetaData {
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
        //adspaceKey, mediaMappingMetaData
        private ConcurrentHashMap<String, MediaMappingMetaData> mediaMappingMetaDataMap = new ConcurrentHashMap<String, MediaMappingMetaData>();
        //adspaceId, <dspId, dspMappingMetaData>
        private ConcurrentHashMap<Long, ConcurrentHashMap<Long, DSPMappingMetaData>> dspMappingMetaDataMap = new ConcurrentHashMap<Long, ConcurrentHashMap<Long, DSPMappingMetaData>>();
        //targeting index
        private ConcurrentHashMap<String, HashSet<Long>> policyTargetMap = new ConcurrentHashMap<String, HashSet<Long>>();
        //dspid:material_key:media_id:adspaceid, materialMetaData
        private ConcurrentHashMap<String, MaterialMetaData> materialMetaDataMap = new ConcurrentHashMap<>();
        //blocked device
        private ConcurrentHashSet<String> blockedDeviceIP = new ConcurrentHashSet<>();
        private ConcurrentHashSet<String> blockedDeviceIFA = new ConcurrentHashSet<>();
        private ConcurrentHashSet<String> blockedDeviceDidmd5 = new ConcurrentHashSet<>();
        private ConcurrentHashSet<String> blockedDeviceDpidmd5 = new ConcurrentHashSet<>();

        public ConcurrentHashSet<String> getBlockedDeviceIP() {
            return blockedDeviceIP;
        }

        public void setBlockedDeviceIP(ConcurrentHashSet<String> blockedDeviceIP) {
            this.blockedDeviceIP = blockedDeviceIP;
        }

        public ConcurrentHashSet<String> getBlockedDeviceIFA() {
            return blockedDeviceIFA;
        }

        public void setBlockedDeviceIFA(ConcurrentHashSet<String> blockedDeviceIFA) {
            this.blockedDeviceIFA = blockedDeviceIFA;
        }

        public ConcurrentHashSet<String> getBlockedDeviceDidmd5() {
            return blockedDeviceDidmd5;
        }

        public void setBlockedDeviceDidmd5(ConcurrentHashSet<String> blockedDeviceDidmd5) {
            this.blockedDeviceDidmd5 = blockedDeviceDidmd5;
        }

        public ConcurrentHashSet<String> getBlockedDeviceDpidmd5() {
            return blockedDeviceDpidmd5;
        }

        public void setBlockedDeviceDpidmd5(ConcurrentHashSet<String> blockedDeviceDpidmd5) {
            this.blockedDeviceDpidmd5 = blockedDeviceDpidmd5;
        }

        public ConcurrentHashMap<Long, DSPMetaData> getDspMetaDataMap() {
            return dspMetaDataMap;
        }

        public void setDspMetaDataMap(ConcurrentHashMap<Long, DSPMetaData> dspMetaDataMap) {
            this.dspMetaDataMap = dspMetaDataMap;
        }

        public ConcurrentHashMap<Long, MediaMetaData> getMediaMetaDataMap() {
            return mediaMetaDataMap;
        }

        public void setMediaMetaDataMap(ConcurrentHashMap<Long, MediaMetaData> mediaMetaDataMap) {
            this.mediaMetaDataMap = mediaMetaDataMap;
        }

        public ConcurrentHashMap<String, PlcmtMetaData> getPlcmtMetaDataMap() {
            return plcmtMetaDataMap;
        }

        public void setPlcmtMetaDataMap(ConcurrentHashMap<String, PlcmtMetaData> plcmtMetaDataMap) {
            this.plcmtMetaDataMap = plcmtMetaDataMap;
        }

        public ConcurrentHashMap<Long, AdBlockMetaData> getAdBlockMetaDataMap() {
            return adBlockMetaDataMap;
        }

        public void setAdBlockMetaDataMap(ConcurrentHashMap<Long, AdBlockMetaData> adBlockMetaDataMap) {
            this.adBlockMetaDataMap = adBlockMetaDataMap;
        }

        public ConcurrentHashMap<Long, PolicyMetaData> getPolicyMetaDataMap() {
            return policyMetaDataMap;
        }

        public void setPolicyMetaDataMap(ConcurrentHashMap<Long, PolicyMetaData> policyMetaDataMap) {
            this.policyMetaDataMap = policyMetaDataMap;
        }

        public ConcurrentHashMap<String, MediaMappingMetaData> getMediaMappingMetaDataMap() {
            return mediaMappingMetaDataMap;
        }

        public void setMediaMappingMetaDataMap(ConcurrentHashMap<String, MediaMappingMetaData> mediaMappingMetaDataMap) {
            this.mediaMappingMetaDataMap = mediaMappingMetaDataMap;
        }

        public ConcurrentHashMap<Long, ConcurrentHashMap<Long, DSPMappingMetaData>> getDspMappingMetaDataMap() {
            return dspMappingMetaDataMap;
        }

        public void setDspMappingMetaDataMap(ConcurrentHashMap<Long, ConcurrentHashMap<Long, DSPMappingMetaData>> dspMappingMetaDataMap) {
            this.dspMappingMetaDataMap = dspMappingMetaDataMap;
        }

        public ConcurrentHashMap<String, HashSet<Long>> getPolicyTargetMap() {
            return policyTargetMap;
        }

        public void setPolicyTargetMap(ConcurrentHashMap<String, HashSet<Long>> policyTargetMap) {
            this.policyTargetMap = policyTargetMap;
        }

        public ConcurrentHashMap<String, MaterialMetaData> getMaterialMetaDataMap() {
            return materialMetaDataMap;
        }

        public void setMaterialMetaDataMap(ConcurrentHashMap<String, MaterialMetaData> materialMetaDataMap) {
            this.materialMetaDataMap = materialMetaDataMap;
        }
    }

    //blocked policy
    private ConcurrentHashSet<Long> blockedPolicy = new ConcurrentHashSet<>();
    //policy budget batch
    private Map<Long, AtomicLong> policyBudgetBatchMap = new ConcurrentHashMap<>();

    private Jedis redisMaster = null;
    private Jedis redisSlave = null;

    private MetaData metaData = new MetaData();

    public boolean init() {
        this.scheduledExecutor.scheduleAtFixedRate(this, 0, ResourceManager.getInstance().getConfiguration().getWebapp().getCacheExpiredTime(), TimeUnit.SECONDS);
        return true;
    }

    public HashSet<Long> getPolicyTargetInfo(String key) {
        return metaData.policyTargetMap.get(key);
    }

    public ConcurrentHashMap<String, HashSet<Long>> getPolicyTargetMap() {
        return metaData.policyTargetMap;
    }

    public MediaMappingMetaData getMediaMapping(String mappingKey) {
        return this.metaData.getMediaMappingMetaDataMap().get(mappingKey);
    }

    public DSPMappingMetaData getDSPMapping(long dspId, long adspaceId) {
        ConcurrentHashMap<Long, DSPMappingMetaData> var = this.metaData.getDspMappingMetaDataMap().get(adspaceId);
        if (var != null) {
            return var.get(dspId);
        }

        return null;
    }

    public void incrPolicyBudgetBy(PolicyMetaData policyMetaData, Long count) {
        AtomicLong policyBudgetBatch = this.policyBudgetBatchMap.get(policyMetaData.getId());
        if (policyBudgetBatch != null && count != null) {
            policyBudgetBatch.addAndGet(count);
        }
    }

    public boolean checkPolicyBudget(PolicyMetaData policyMetaData) {
        AtomicLong policyBudgetBatch = null;

        synchronized (this) {
            policyBudgetBatch = this.policyBudgetBatchMap.get(policyMetaData.getId());
            if (policyBudgetBatch == null) {
                policyBudgetBatch = new AtomicLong(0L);
                this.policyBudgetBatchMap.put(policyMetaData.getId(), policyBudgetBatch);
            }
        }

        synchronized (policyBudgetBatch) {
            if (policyBudgetBatch.get() > 0L) {
                return true;
            }

            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
            long budgetBatchSize = ResourceManager.getInstance().getConfiguration().getWebapp().getBudgetBatchSize();

            Jedis redisConn = null;

            try {
                redisConn = ResourceManager.getInstance().getJedisPoolMaster().getResource();
                Long totalCount = redisConn.incrBy(String.format(Constant.CommonKey.POLICY_CONTORL_TOTAL, policyMetaData.getId()), budgetBatchSize);
                Long dailyCount = redisConn.incrBy(String.format(Constant.CommonKey.POLICY_CONTORL_DAILY, policyMetaData.getId(), currentDate), budgetBatchSize);

                if (totalCount == null || dailyCount == null) {
                    CacheManager.getInstance().blockPolicy(policyMetaData.getId());
                    return false;
                }

                //<currentHourBudget, currentDayBudget>
                Pair<Long, Long> policyBudget = CacheManager.getInstance().getPolicyBudget(policyMetaData, totalCount, dailyCount);
                if (policyBudget == null || policyBudget.getLeft() + budgetBatchSize <= 0) {
                    CacheManager.getInstance().blockPolicy(policyMetaData.getId());

                    redisConn.decrBy(String.format(Constant.CommonKey.POLICY_CONTORL_TOTAL, policyMetaData.getId()), budgetBatchSize);
                    redisConn.decrBy(String.format(Constant.CommonKey.POLICY_CONTORL_DAILY, policyMetaData.getId(), currentDate), budgetBatchSize);
                    return false;
                }

                if (policyBudget.getLeft() >= 0) {
                    policyBudgetBatch.addAndGet(budgetBatchSize);
                } else {
                    policyBudgetBatch.addAndGet(budgetBatchSize + policyBudget.getLeft());
                    redisConn.incrBy(String.format(Constant.CommonKey.POLICY_CONTORL_TOTAL, policyMetaData.getId()), policyBudget.getLeft());
                    redisConn.incrBy(String.format(Constant.CommonKey.POLICY_CONTORL_DAILY, policyMetaData.getId(), currentDate), policyBudget.getLeft());
                }

                return true;
            } catch (Exception ex) {
                logger.error(ex.toString());
            } finally {
                if (redisConn != null) {
                    redisConn.close();
                }
            }
        }

        return false;
    }

    public MaterialMetaData getMaterialMetaData(long dspId, String materialId, long mediaId, long adspaceId) {
        String key = String.format(Constant.CommonKey.MATERIAL_MAPPING_DATA, dspId, materialId, mediaId, adspaceId);
        return this.metaData.getMaterialMetaDataMap().get(key);
    }

    public DSPMetaData getDSPMetaData(long id) {
        return this.metaData.getDspMetaDataMap().get(id);
    }

    public MediaMetaData getMediaMetaData(long id) {
        return this.metaData.getMediaMetaDataMap().get(id);
    }

    public PlcmtMetaData getPlcmtMetaData(String key) {
        return this.metaData.getPlcmtMetaDataMap().get(key);
    }

    public AdBlockMetaData getAdBlockMetaData(long id) {
        return this.metaData.getAdBlockMetaDataMap().get(id);
    }

    public PolicyMetaData getPolicyMetaData(long id) {
        return this.metaData.getPolicyMetaDataMap().get(id);
    }

    public void run() {
        logger.info("load metadata cache begin.");

        this.redisMaster = ResourceManager.getInstance().getJedisPoolMaster().getResource();
        this.redisSlave = ResourceManager.getInstance().getJedisPoolSlave().getResource();

        MetaData var = new MetaData();
        logger.debug("loading media metadata.");
        var.setMediaMetaDataMap(this.loadMediaMetaData());
        logger.debug("loading adspace metadata.");
        var.setPlcmtMetaDataMap(this.loadPlcmtMetaData());
        logger.debug("loading adblock metadata.");
        var.setAdBlockMetaDataMap(this.loadAdBlockMetaData());
        logger.debug("loading policy metadata.");
        var.setPolicyMetaDataMap(this.loadPolicyMetaData());
        logger.debug("loading dsp metadata.");
        var.setDspMetaDataMap(this.loadDSPMetaData());
        logger.debug("loading media mapping metadata.");
        var.setMediaMappingMetaDataMap(this.loadMediaMappingData());
        logger.debug("loading dsp mapping metadata.");
        var.setDspMappingMetaDataMap(this.loadDSPMappingData());
        logger.debug("loading material metadata.");
        var.setMaterialMetaDataMap(this.loadMaterialMappingData());
        logger.debug("loading blocked device metadata.");
        var.setBlockedDeviceIP(this.loadBlockedDeviceMetaData(Constant.CommonKey.ALL_BLOCKED_DEVICE_IP));
        var.setBlockedDeviceIFA(this.loadBlockedDeviceMetaData(Constant.CommonKey.ALL_BLOCKED_DEVICE_IFA));
        var.setBlockedDeviceDidmd5(this.loadBlockedDeviceMetaData(Constant.CommonKey.ALL_BLOCKED_DEVICE_DIDMD5));
        var.setBlockedDeviceDpidmd5(this.loadBlockedDeviceMetaData(Constant.CommonKey.ALL_BLOCKED_DEVICE_DPIDMD5));
        logger.debug("updating policy targeting metadata.");
        var.setPolicyTargetMap(this.updatePolicyTargetInfo(var.getPolicyMetaDataMap()));

        this.metaData = var;
        this.blockedPolicy.clear();

        if (this.redisMaster != null) {
            this.redisMaster.close();
        }

        if (this.redisSlave != null) {
            this.redisSlave.close();
        }

        logger.info("load metadata cache end.");
    }

    public boolean isBlockedDevice(int osType, String ip, String ifa, String didmd5, String dpidmd5) {
        if (!StringUtils.isEmpty(ip)) {
            if (!ObjectUtils.isEmpty(this.metaData.getBlockedDeviceIP()) &&
                    this.metaData.getBlockedDeviceIP().contains(ip)) {
                return true;
            }
        }

        switch (osType) {
            case Constant.OSType.ANDROID: {
                if (!StringUtils.isEmpty(didmd5)) {
                    if (!EncryptUtil.formatCheck(EncryptUtil.Type.MD5, didmd5)) {
                        return true;
                    }
                }

                if (!StringUtils.isEmpty(dpidmd5)) {
                    if (!EncryptUtil.formatCheck(EncryptUtil.Type.MD5, dpidmd5)) {
                        return true;
                    }
                }

                break;
            }

            case Constant.OSType.IOS: {
                if (!StringUtils.isEmpty(ifa)) {
                    if (!StringUtil.formatCheck("^[0-9A-F]{8}\\-[0-9A-F]{4}\\-[0-9A-F]{4}\\-[0-9A-F]{4}\\-[0-9A-F]{12}$", ifa)) {
                        return true;
                    }
                }

                break;
            }

            default: {
                if (!StringUtils.isEmpty(dpidmd5)) {
                    if (!EncryptUtil.formatCheck(EncryptUtil.Type.MD5, dpidmd5)) {
                        return true;
                    }
                }

                break;
            }
        }

        if (!StringUtils.isEmpty(didmd5)) {
            if (!ObjectUtils.isEmpty(this.metaData.getBlockedDeviceDidmd5()) &&
                    this.metaData.getBlockedDeviceDidmd5().contains(didmd5)) {
                return true;
            }
        }

        if (!StringUtils.isEmpty(dpidmd5)) {
            if (!ObjectUtils.isEmpty(this.metaData.getBlockedDeviceDpidmd5()) &&
                    this.metaData.getBlockedDeviceDpidmd5().contains(dpidmd5)) {
                return true;
            }
        }

        if (!StringUtils.isEmpty(ifa)) {
            if (!ObjectUtils.isEmpty(this.metaData.getBlockedDeviceIFA()) &&
                    this.metaData.getBlockedDeviceIFA().contains(ifa)) {
                return true;
            }
        }

        return false;
    }

    private ConcurrentHashSet<String> loadBlockedDeviceMetaData(String key) {
        ConcurrentHashSet<String> var = new ConcurrentHashSet<>();

        Set<String> blockedDevices = this.redisSlave.smembers(key);
        if (!ObjectUtils.isEmpty(blockedDevices)) {
            for (String str : blockedDevices) {
                var.add(str);
            }
        }

        return var;
    }

    private ConcurrentHashMap<String, MaterialMetaData> loadMaterialMappingData() {
        ConcurrentHashMap<String, MaterialMetaData> var = new ConcurrentHashMap<String, MaterialMetaData>();

        Set<String> materialIds = this.redisSlave.smembers(Constant.CommonKey.ALL_MATERIAL);
        if (!ObjectUtils.isEmpty(materialIds)) {
            for (String materialId : materialIds) {
                String text = this.redisSlave.get(String.format(Constant.CommonKey.MATERIAL_META_DATA, materialId));
                if (!StringUtils.isEmpty(text)) {
                    MaterialMetaData mediaMetaData = JSON.parseObject(text, MaterialMetaData.class);
                    var.put(String.format(Constant.CommonKey.MATERIAL_MAPPING_DATA, mediaMetaData.getDspId(), mediaMetaData.getMaterialId(), mediaMetaData.getMediaId(), mediaMetaData.getAdspaceId()), mediaMetaData);
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
                    var.put(String.valueOf(metaData.getAdspaceKey()), metaData);
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

    private ConcurrentHashMap<String, MediaMappingMetaData> loadMediaMappingData() {
        ConcurrentHashMap<String, MediaMappingMetaData> var = new ConcurrentHashMap<String, MediaMappingMetaData>();

        Set<String> adspaceIds = this.redisSlave.smembers(Constant.CommonKey.ALL_PLACEMENT);
        if (!ObjectUtils.isEmpty(adspaceIds)) {
            for (String adspaceId : adspaceIds) {
                String text = this.redisSlave.get(String.format(Constant.CommonKey.MEDIA_MAPPING_DATA, adspaceId));
                if (!StringUtils.isEmpty(text)) {
                    MediaMappingMetaData mediaMappingMetaData = JSON.parseObject(text, MediaMappingMetaData.class);
                    var.put(mediaMappingMetaData.getMappingKey(), mediaMappingMetaData);
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

    private ConcurrentHashMap<String, HashSet<Long>> updatePolicyTargetInfo(ConcurrentHashMap<Long, PolicyMetaData> policyMetaDataMap) {
        ConcurrentHashMap<String, HashSet<Long>> var = new ConcurrentHashMap<>();

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());

        for (Map.Entry entry : policyMetaDataMap.entrySet()) {
            PolicyMetaData policyMetaData = (PolicyMetaData)entry.getValue();

            if (currentDate.compareTo(policyMetaData.getStartDate()) < 0 ||
                    (!StringUtils.isEmpty(policyMetaData.getEndDate()) && currentDate.compareTo(policyMetaData.getEndDate()) > 0)) {
                continue;
            }

            if (StringUtils.isEmpty(policyMetaData.getEndDate()) &&
                    policyMetaData.getControlType() == Constant.PolicyControlType.TOTAL &&
                    policyMetaData.getControlMethod() == Constant.PolicyControlMethod.AVERAGE) {
                continue;
            }

            if (ObjectUtils.isEmpty(policyMetaData.getAdspaceInfoMap()) || ObjectUtils.isEmpty(policyMetaData.getDspInfoMap())) {
                continue;
            }

            if (policyMetaData.getControlType() != Constant.PolicyControlType.NONE) {
                if (policyMetaData.getMaxCount() <= 0) {
                    continue;
                }

                this.redisMaster.set(String.format(Constant.CommonKey.POLICY_CONTORL_TOTAL, policyMetaData.getId()), "0", "NX");
                this.redisMaster.set(String.format(Constant.CommonKey.POLICY_CONTORL_DAILY, policyMetaData.getId(), currentDate), "0", "NX", "EX", 86400);

                if (!StringUtils.isEmpty(policyMetaData.getEndDate())) {
                    int totalDays = Utility.dateDiff(StringUtil.toDate(policyMetaData.getEndDate()), StringUtil.toDate(currentDate)) + 1;
                    this.redisMaster.expire(String.format(Constant.CommonKey.POLICY_CONTORL_TOTAL, policyMetaData.getId()), (totalDays + 1) * 86400);
                }

                String totalCount = this.redisSlave.get(String.format(Constant.CommonKey.POLICY_CONTORL_TOTAL, policyMetaData.getId()));
                String dailyCount = this.redisSlave.get(String.format(Constant.CommonKey.POLICY_CONTORL_DAILY, policyMetaData.getId(), currentDate));

                Pair<Long, Long> budgetCount = this.getPolicyBudget(policyMetaData, Long.parseLong(totalCount), Long.parseLong(dailyCount));
                AtomicLong policyBudgetBatch = this.policyBudgetBatchMap.get(policyMetaData.getId());

                if (budgetCount == null || (budgetCount.getLeft() <= 0 && (policyBudgetBatch == null || policyBudgetBatch.get() <= 0))) {
                    continue;
                }
            }

            //placement
            for (Map.Entry entry1 : policyMetaData.getAdspaceInfoMap().entrySet()) {
                PolicyMetaData.AdspaceInfo adspaceInfo = (PolicyMetaData.AdspaceInfo)entry1.getValue();
                if (adspaceInfo.getStatus() > 0) {
                    String[] dealIds = StringUtil.toString(adspaceInfo.getDealId()).split(",");
                    for (int i = 0; i < dealIds.length; ++i) {
                        String adspaceKey = String.format("%d-%s", adspaceInfo.getId(), StringUtil.toString(dealIds[i]));
                        String key = String.format(Constant.CommonKey.TARGET_KEY, policyMetaData.getDeliveryType(), Constant.TargetType.PLACEMENT, adspaceKey);
                        HashSet<Long> var2 = var.get(key);
                        if (var2 == null) {
                            var2 = new HashSet<>();
                            var.put(key, var2);
                        }

                        var2.add(policyMetaData.getId());
                    }
                }
            }

            //date
            {
                String key = String.format(Constant.CommonKey.TARGET_KEY, policyMetaData.getDeliveryType(), Constant.TargetType.DATE, currentDate);
                HashSet<Long> var2 = var.get(key);
                if (var2 == null) {
                    var2 = new HashSet<>();
                    var.put(key, var2);
                }

                var2.add(policyMetaData.getId());
            }

            //weekdayHour
            if (!ObjectUtils.isEmpty(policyMetaData.getWeekdayHoursMap())) {
                Map<Integer, List<Integer>> weekHours = policyMetaData.getWeekdayHoursMap();
                for (Map.Entry entry1 : weekHours.entrySet()) {
                    List<Integer> hours = (List<Integer>)entry1.getValue();
                    for (int hour : hours) {
                        String key = String.format(Constant.CommonKey.TARGET_KEY, policyMetaData.getDeliveryType(), Constant.TargetType.WEEKDAY_HOUR, String.format("%d-%02d", (Integer)entry1.getKey(), hour));
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

    public Pair<Long, Long> getPolicyBudget(PolicyMetaData policyMetaData, long totalCount, long dailyCount) {
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());

            int weekday = cal.get(Calendar.DAY_OF_WEEK) - 1;
            int currentHour = cal.get(Calendar.HOUR_OF_DAY);

            if (policyMetaData.getControlMethod() == Constant.PolicyControlMethod.AVERAGE) {
                int pastHours = 0;
                int totalHours = 24;
                if (ObjectUtils.isEmpty(policyMetaData.getWeekdayHoursMap())) {
                    pastHours = currentHour + 1;
                } else {
                    List<Integer> hours = policyMetaData.getWeekdayHoursMap().get(weekday);
                    if (ObjectUtils.isEmpty(hours)) {
                        return null;
                    }

                    int start = 0;
                    int end = hours.size();
                    while (end - start > 1) {
                        int mid = (start + end) / 2;
                        if (hours.get(mid) <= currentHour) {
                            start = mid;
                        } else {
                            end = mid;
                        }
                    }

                    pastHours = start + 1;
                    totalHours = hours.size();
                }

                long dailyBudget = policyMetaData.getMaxCount();
                if (policyMetaData.getControlType() == Constant.PolicyControlType.TOTAL) {
                    int pastDays = Utility.dateDiff(cal.getTime(), StringUtil.toDate(policyMetaData.getStartDate())) + 1;
                    int totalDays = Utility.dateDiff(StringUtil.toDate(policyMetaData.getEndDate()), StringUtil.toDate(policyMetaData.getStartDate())) + 1;
                    dailyBudget = policyMetaData.getMaxCount() / totalDays;
                }

                return Pair.of((dailyBudget * pastHours / totalHours - dailyCount), dailyBudget - dailyCount);
            } else {
                if (policyMetaData.getControlType() == Constant.PolicyControlType.DAILY) {
                    return Pair.of(policyMetaData.getMaxCount() - dailyCount, policyMetaData.getMaxCount() - dailyCount);
                } else {
                    return Pair.of(policyMetaData.getMaxCount() - totalCount, policyMetaData.getMaxCount() - totalCount);
                }
            }
        } catch (Exception ex) {
            logger.error(ex.toString());
        }

        return null;
    }
}