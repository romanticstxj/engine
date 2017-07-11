package com.madhouse.cache;

import java.util.List;
import java.util.Map;

/**
 * Created by WUJUNFENG on 2017/7/10.
 */
public class MaterialMetaData {
    public class Monitor {
        public class Track {
            private int startDelay;
            private String url;

            public int getStartDelay() {
                return startDelay;
            }

            public void setStartDelay(int startDelay) {
                this.startDelay = startDelay;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }
        }

        private List<Track> impUrls;
        private List<String> clkUrls;
        private List<String> secUrls;

        public List<Track> getImpUrls() {
            return impUrls;
        }

        public void setImpUrls(List<Track> impUrls) {
            this.impUrls = impUrls;
        }

        public List<String> getClkUrls() {
            return clkUrls;
        }

        public void setClkUrls(List<String> clkUrls) {
            this.clkUrls = clkUrls;
        }

        public List<String> getSecUrls() {
            return secUrls;
        }

        public void setSecUrls(List<String> secUrls) {
            this.secUrls = secUrls;
        }
    }

    public class MaterialMedia {
        private String id;
        private String url;
        private int status;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }

    private long dspId;
    private String materialId;
    private String name;
    private int adType;
    private String dealId;
    private String startDate;
    private String endDate;
    private int w;
    private int h;
    private String icon;
    private String cover;
    private String title;
    private String desc;
    private List<String> adm;
    private int duration;
    private int actType;
    private String lpgUrl;
    private Monitor monitor;
    private Map<Long, MaterialMedia> materialMediaMap;

    public long getDspId() {
        return dspId;
    }

    public void setDspId(long dspId) {
        this.dspId = dspId;
    }

    public String getMaterialId() {
        return materialId;
    }

    public void setMaterialId(String materialId) {
        this.materialId = materialId;
    }

    public int getAdType() {
        return adType;
    }

    public void setAdType(int adType) {
        this.adType = adType;
    }

    public String getDealId() {
        return dealId;
    }

    public void setDealId(String dealId) {
        this.dealId = dealId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public List<String> getAdm() {
        return adm;
    }

    public void setAdm(List<String> adm) {
        this.adm = adm;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getActType() {
        return actType;
    }

    public void setActType(int actType) {
        this.actType = actType;
    }

    public Monitor getMonitor() {
        return monitor;
    }

    public void setMonitor(Monitor monitor) {
        this.monitor = monitor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<Long, MaterialMedia> getMaterialMediaMap() {
        return materialMediaMap;
    }

    public void setMaterialMediaMap(Map<Long, MaterialMedia> materialMediaMap) {
        this.materialMediaMap = materialMediaMap;
    }

    public String getLpgUrl() {
        return lpgUrl;
    }

    public void setLpgUrl(String lpgUrl) {
        this.lpgUrl = lpgUrl;
    }
}

