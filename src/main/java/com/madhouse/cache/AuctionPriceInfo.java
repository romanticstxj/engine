package com.madhouse.cache;

import sun.dc.pr.PRError;

/**
 * Created by WUJUNFENG on 2017/8/16.
 */
public class AuctionPriceInfo {
    private int bidType;
    private int bidPrice;

    public int getBidPrice() {
        return bidPrice;
    }

    public void setBidPrice(int bidPrice) {
        this.bidPrice = bidPrice;
    }

    public int getBidType() {
        return bidType;
    }

    public void setBidType(int bidType) {
        this.bidType = bidType;
    }
}
