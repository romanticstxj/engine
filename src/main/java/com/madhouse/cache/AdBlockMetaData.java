package com.madhouse.cache;

import java.util.List;

/**
 * Created by WUJUNFENG on 2017/6/12.
 */
public class AdBlockMetaData {
    private long id;
    private List<String> badv;
    private List<String> bcat;

    public List<String> getBadv() {
        return badv;
    }

    public void setBadv(List<String> badv) {
        this.badv = badv;
    }

    public List<String> getBcat() {
        return bcat;
    }

    public void setBcat(List<String> bcat) {
        this.bcat = bcat;
    }
}
