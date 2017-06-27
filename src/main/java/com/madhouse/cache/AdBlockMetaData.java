package com.madhouse.cache;

import java.util.List;

/**
 * Created by WUJUNFENG on 2017/6/12.
 */
public class AdBlockMetaData {
    private long id;
    private List<String> badv;
    private List<String> bcat;
    private int status;

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
