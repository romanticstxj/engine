package com.madhouse.cache;

import com.madhouse.dsp.DSPBaseHandler;
import com.madhouse.ssp.PremiumMADDataModel;

/**
 * Created by WUJUNFENG on 2017/6/15.
 */
public class BidMetaData {
    private String id;
    private String impid;
    private String bidid;
    private String adid;
    private String admid;
    private String winurl;
    private int price;
    private PremiumMADDataModel.DSPBid.Builder builder;
    private DSPBaseHandler dspBaseHandler;

    public PremiumMADDataModel.DSPBid.Builder getBuilder() {
        return builder;
    }

    public void setBuilder(PremiumMADDataModel.DSPBid.Builder builder) {
        this.builder = builder;
    }

    public DSPBaseHandler getDspBaseHandler() {
        return dspBaseHandler;
    }

    public void setDspBaseHandler(DSPBaseHandler dspBaseHandler) {
        this.dspBaseHandler = dspBaseHandler;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImpid() {
        return impid;
    }

    public void setImpid(String impid) {
        this.impid = impid;
    }

    public String getBidid() {
        return bidid;
    }

    public void setBidid(String bidid) {
        this.bidid = bidid;
    }

    public String getAdid() {
        return adid;
    }

    public void setAdid(String adid) {
        this.adid = adid;
    }

    public String getAdmid() {
        return admid;
    }

    public void setAdmid(String admid) {
        this.admid = admid;
    }

    public String getWinurl() {
        return winurl;
    }

    public void setWinurl(String winurl) {
        this.winurl = winurl;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
