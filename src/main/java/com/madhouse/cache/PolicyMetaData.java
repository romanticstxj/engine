package com.madhouse.cache;


import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;

/**
 * Created by WUJUNFENG on 2017/6/12.
 */
public class PolicyMetaData {
    private long id;
    private int deliveryType;
    private int weight;
    private String dealId;

    private String startDate;
    private String endDate;

    //targeting info
    private Map<Integer, List<Integer>> weekHours;
    private List<String> location;
    private List<Integer> os;
    private List<Integer> connectionType;

    private Map<Long, DSPInfo> dspInfoMap;
    private Map<Long, AdspaceInfo> adspaceInfoMap;

    private int controlType;
    private int maxCount;
    private int controlMethod;

    private int status;

    public class AdspaceInfo {
        private long id;
        private int status;
        private int bidType;
        private int bidFloor;
        private String dealId;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getBidType() {
            return bidType;
        }

        public void setBidType(int bidType) {
            this.bidType = bidType;
        }

        public int getBidFloor() {
            return bidFloor;
        }

        public void setBidFloor(int bidFloor) {
            this.bidFloor = bidFloor;
        }

        public String getDealId() {
            return dealId;
        }

        public void setDealId(String dealId) {
            this.dealId = dealId;
        }
    }

    public class DSPInfo {
        private long id;
        private int weight;
        private int status;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(int deliveryType) {
        this.deliveryType = deliveryType;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
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

    public Map<Integer, List<Integer>> getWeekHours() {
        return weekHours;
    }

    public void setWeekHours(Map<Integer, List<Integer>> weekHours) {
        this.weekHours = weekHours;
    }

    public List<String> getLocation() {
        return location;
    }

    public void setLocation(List<String> location) {
        this.location = location;
    }

    public List<Integer> getOs() {
        return os;
    }

    public void setOs(List<Integer> os) {
        this.os = os;
    }

    public List<Integer> getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(List<Integer> connectionType) {
        this.connectionType = connectionType;
    }

    public int getControlType() {
        return controlType;
    }

    public void setControlType(int controlType) {
        this.controlType = controlType;
    }

    public int getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }

    public Map<Long, DSPInfo> getDspInfoMap() {
        return dspInfoMap;
    }

    public void setDspInfoMap(Map<Long, DSPInfo> dspInfoMap) {
        this.dspInfoMap = dspInfoMap;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Map<Long, AdspaceInfo> getAdspaceInfoMap() {
        return adspaceInfoMap;
    }

    public void setAdspaceInfoMap(Map<Long, AdspaceInfo> adspaceInfoMap) {
        this.adspaceInfoMap = adspaceInfoMap;
    }

    public int getControlMethod() {
        return controlMethod;
    }

    public void setControlMethod(int controlMethod) {
        this.controlMethod = controlMethod;
    }
}
