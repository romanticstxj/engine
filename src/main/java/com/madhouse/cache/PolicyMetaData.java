package com.madhouse.cache;


import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;

/**
 * Created by WUJUNFENG on 2017/6/12.
 */
public class PolicyMetaData {
    private int tradingtype;
    private long id;
    private int weight;
    private String dealid;
    private int bidfloor;
    private int bidtype;

    //targeting info
    private String startdate;
    private String enddate;
    private Map<Integer, List<Integer>> weekday;
    private List<Integer> ostype;
    private List<Integer> connectiontype;

    private List<Long> plcmtlist;
    //dspid, weight
    private Map<Long, Integer> dsplist;

    private int controltype;
    private int maxcount;

    public int getTradingtype() {
        return tradingtype;
    }

    public void setTradingtype(int tradingtype) {
        this.tradingtype = tradingtype;
    }

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

    public String getDealid() {
        return dealid;
    }

    public void setDealid(String dealid) {
        this.dealid = dealid;
    }

    public int getBidfloor() {
        return bidfloor;
    }

    public void setBidfloor(int bidfloor) {
        this.bidfloor = bidfloor;
    }

    public int getBidtype() {
        return bidtype;
    }

    public void setBidtype(int bidtype) {
        this.bidtype = bidtype;
    }

    public String getStartdate() {
        return startdate;
    }

    public void setStartdate(String startdate) {
        this.startdate = startdate;
    }

    public String getEnddate() {
        return enddate;
    }

    public void setEnddate(String enddate) {
        this.enddate = enddate;
    }

    public Map<Integer, List<Integer>> getWeekday() {
        return weekday;
    }

    public void setWeekday(Map<Integer, List<Integer>> weekday) {
        this.weekday = weekday;
    }

    public List<Integer> getOstype() {
        return ostype;
    }

    public void setOstype(List<Integer> ostype) {
        this.ostype = ostype;
    }

    public List<Integer> getConnectiontype() {
        return connectiontype;
    }

    public void setConnectiontype(List<Integer> connectiontype) {
        this.connectiontype = connectiontype;
    }

    public List<Long> getPlcmtlist() {
        return plcmtlist;
    }

    public void setPlcmtlist(List<Long> plcmtlist) {
        this.plcmtlist = plcmtlist;
    }

    public Map<Long, Integer> getDsplist() {
        return dsplist;
    }

    public void setDsplist(Map<Long, Integer> dsplist) {
        this.dsplist = dsplist;
    }
}
