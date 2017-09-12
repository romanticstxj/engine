package com.madhouse.cache;


import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
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
    private List<WeekdayHours> weekdayHoursList;
    private List<String> location;
    private List<Integer> os;
    private List<Integer> connectionType;

    private List<DSPInfo> dspInfoList;
    private List<AdspaceInfo> adspaceInfoList;

    private Map<Integer, List<Integer>> weekdayHoursMap = new HashMap<>();
    private Map<Long, DSPInfo> dspInfoMap = new HashMap<>();
    private Map<Long, AdspaceInfo> adspaceInfoMap = new HashMap<>();

    private int controlType;
    private long maxCount;
    private int controlMethod;

    private int status;

    public static class WeekdayHours {
        private int weekDay;
        private List<Integer> hours;

        public int getWeekDay() {
            return weekDay;
        }

        public void setWeekDay(int weekDay) {
            this.weekDay = weekDay;
        }

        public List<Integer> getHours() {
            return hours;
        }

        public void setHours(List<Integer> hours) {
            this.hours = hours;
        }
    }

    public static class AdspaceInfo {
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

    public static class DSPInfo {
        private long id;
        private int status;

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

    public Map<Integer, List<Integer>> getWeekdayHoursMap() {
        return weekdayHoursMap;
    }

    public void setWeekdayHoursMap(Map<Integer, List<Integer>> weekdayHoursMap) {
        this.weekdayHoursMap = weekdayHoursMap;
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

    public long getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(long maxCount) {
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

    public List<WeekdayHours> getWeekdayHoursList() {
        return weekdayHoursList;
    }

    public void setWeekdayHoursList(List<WeekdayHours> weekdayHoursList) {
        this.weekdayHoursList = weekdayHoursList;

        this.weekdayHoursMap.clear();
        for (WeekdayHours weekdayHours : this.weekdayHoursList) {
            this.weekdayHoursMap.put(weekdayHours.getWeekDay(), weekdayHours.getHours());
        }
    }

    public List<DSPInfo> getDspInfoList() {
        return dspInfoList;
    }

    public void setDspInfoList(List<DSPInfo> dspInfoList) {
        this.dspInfoList = dspInfoList;

        this.dspInfoMap.clear();
        for (DSPInfo dspInfo : this.dspInfoList) {
            this.dspInfoMap.put(dspInfo.getId(), dspInfo);
        }
    }

    public List<AdspaceInfo> getAdspaceInfoList() {
        return adspaceInfoList;
    }

    public void setAdspaceInfoList(List<AdspaceInfo> adspaceInfoList) {
        this.adspaceInfoList = adspaceInfoList;

        this.adspaceInfoMap.clear();
        for (AdspaceInfo adspaceInfo : this.adspaceInfoList) {
            this.adspaceInfoMap.put(adspaceInfo.getId(), adspaceInfo);
        }
    }
}
