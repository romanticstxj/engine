package com.madhouse.cache;

import com.madhouse.ssp.Constant;

import java.util.List;

/**
 * Created by WUJUNFENG on 2017/6/9.
 */
public class PlcmtMetaData {
    private long id;
    private int w;
    private int h;
    private int type;
    private int icon;
    private int cover;
    private int title;
    private int desc;
    private int maxduration;
    private int minduration;
    private int linearity;
    private int startdelay;
    private List<String> mimes;
    private int blockid;
    private int bidtype;
    private int bidfloor;
    private int layout;

    public int getLayout() {
        return layout;
    }

    public void setLayout(int layout) {
        this.layout = layout;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getCover() {
        return cover;
    }

    public void setCover(int cover) {
        this.cover = cover;
    }

    public int getTitle() {
        return title;
    }

    public void setTitle(int title) {
        this.title = title;
    }

    public int getDesc() {
        return desc;
    }

    public void setDesc(int desc) {
        this.desc = desc;
    }

    public int getMaxduration() {
        return maxduration;
    }

    public void setMaxduration(int maxduration) {
        this.maxduration = maxduration;
    }

    public int getMinduration() {
        return minduration;
    }

    public void setMinduration(int minduration) {
        this.minduration = minduration;
    }

    public List<String> getMimes() {
        return mimes;
    }

    public void setMimes(List<String> mimes) {
        this.mimes = mimes;
    }

    public int getBlockid() {
        return blockid;
    }

    public void setBlockid(int blockid) {
        this.blockid = blockid;
    }

    public int getBidtype() {
        return bidtype;
    }

    public void setBidtype(int bidtype) {
        this.bidtype = bidtype;
    }

    public int getBidfloor() {
        return bidfloor;
    }

    public void setBidfloor(int bidfloor) {
        this.bidfloor = bidfloor;
    }

    public int getLinearity() {
        return linearity;
    }

    public void setLinearity(int linearity) {
        this.linearity = linearity;
    }

    public int getStartdelay() {
        return startdelay;
    }

    public void setStartdelay(int startdelay) {
        this.startdelay = startdelay;
    }
}
