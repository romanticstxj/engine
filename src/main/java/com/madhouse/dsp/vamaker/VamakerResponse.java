package com.madhouse.dsp.vamaker;

import java.util.List;

public class VamakerResponse {
    private String adid;
    
    private String adtype;
    
    private List<String> img;
    
    private String title;
    
    private String desc;
    
    private String lp;
    
    private List<String> pm;
    
    private List<String> cm;
    
    private String cid;
    
    public String getAdid() {
        return adid;
    }
    
    public void setAdid(String adid) {
        this.adid = adid;
    }
    
    public String getAdtype() {
        return adtype;
    }
    
    public void setAdtype(String adtype) {
        this.adtype = adtype;
    }
    
    public List<String> getImg() {
        return img;
    }
    
    public void setImg(List<String> img) {
        this.img = img;
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
    
    public String getLp() {
        return lp;
    }
    
    public void setLp(String lp) {
        this.lp = lp;
    }
    
    public List<String> getPm() {
        return pm;
    }
    
    public void setPm(List<String> pm) {
        this.pm = pm;
    }
    
    public List<String> getCm() {
        return cm;
    }
    
    public void setCm(List<String> cm) {
        this.cm = cm;
    }
    
    public String getCid() {
        return cid;
    }
    
    public void setCid(String cid) {
        this.cid = cid;
    }
    
}
