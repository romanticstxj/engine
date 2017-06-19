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
    private String startdate;
    private String enddate;
    private Map<Integer, List<Integer>> weekday;
    private List<Integer> ostype;
    private List<Integer> connectiontype;
    private List<Long> plcmtlist;
    //dspid, weight, floating
    private Map<Long, Pair<Integer, Float>> dsplist;

    public Map<Long, Pair<Integer, Float>> getDsplist() {
        return dsplist;
    }

    public float getFloating(long dspid) {
        Pair<Integer, Float> var = this.dsplist.get(dspid);
        if (var != null) {
            return var.getRight();
        }

        return -1;
    }

    public void setDsplist(Map<Long, Pair<Integer, Float>> dsplist) {
        this.dsplist = dsplist;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

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

    public String getDealid() {
        return dealid;
    }

    public void setDealid(String dealid) {
        this.dealid = dealid;
    }
}
