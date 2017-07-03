package com.madhouse.media.madhouse;

import java.util.List;

public class PremiumMADResponse {
    
    private String returncode;
    
    //广告位ID。
    private String adspaceid;
    
    //标识广告来源
    private String source;
    
    private String bid;
    
    private String cid;
    
    private String adwidth;
    
    private String adheight;
    
    private String adtype;
    
    //视频接口必返回 视频素材的时长，单位为秒。
    private String duration;
    
    //信息流广告icon。
    private String icon;
    
    //视频广告或视频信息流广告封面。
    private String cover;
    
    //广告物料地址。
    private String imgurl;
    
    //广告物料地址，可以存在多个物料。
    private List<String> adm;
    
    //点击跳转链接。
    private String clickurl;
    
    //广告的标题。
    private String displaytitle;
    
    // 广告的文字描述。
    private String displaytext;
    
    //展示监播
    private List<String> imgtracking;
    
    //点击地址
    private List<String> thclkurl;
    
    //品牌安全监测
    private List<String> securl;
    
    
    
    public String getBid() {
        return bid;
    }

    public void setBid(String bid) {
        this.bid = bid;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getAdwidth() {
        return adwidth;
    }

    public void setAdwidth(String adwidth) {
        this.adwidth = adwidth;
    }

    public String getAdheight() {
        return adheight;
    }

    public void setAdheight(String adheight) {
        this.adheight = adheight;
    }

    public String getAdtype() {
        return adtype;
    }

    public void setAdtype(String adtype) {
        this.adtype = adtype;
    }

    public String getReturncode() {
        return returncode;
    }
    
    public void setReturncode(String returncode) {
        this.returncode = returncode;
    }
    
    public String getAdspaceid() {
        return adspaceid;
    }
    
    public void setAdspaceid(String adspaceid) {
        this.adspaceid = adspaceid;
    }
    
    public String getSource() {
        return source;
    }
    
    public void setSource(String source) {
        this.source = source;
    }
    
    public String getDuration() {
        return duration;
    }
    
    public void setDuration(String duration) {
        this.duration = duration;
    }
    
    public String getIcon() {
        return icon;
    }
    
    public void setIcon(String icon) {
        this.icon = icon;
    }
    
    public String getCover() {
        return cover;
    }
    
    public void setCover(String cover) {
        this.cover = cover;
    }
    
    public String getImgurl() {
        return imgurl;
    }
    
    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }
    
    public List<String> getAdm() {
        return adm;
    }
    
    public void setAdm(List<String> adm) {
        this.adm = adm;
    }
    
    public String getClickurl() {
        return clickurl;
    }
    
    public void setClickurl(String clickurl) {
        this.clickurl = clickurl;
    }
    
    public String getDisplaytitle() {
        return displaytitle;
    }
    
    public void setDisplaytitle(String displaytitle) {
        this.displaytitle = displaytitle;
    }
    
    public String getDisplaytext() {
        return displaytext;
    }
    
    public void setDisplaytext(String displaytext) {
        this.displaytext = displaytext;
    }
    
    public List<String> getImgtracking() {
        return imgtracking;
    }
    
    public void setImgtracking(List<String> imgtracking) {
        this.imgtracking = imgtracking;
    }
    
    public List<String> getThclkurl() {
        return thclkurl;
    }
    
    public void setThclkurl(List<String> thclkurl) {
        this.thclkurl = thclkurl;
    }
    
    public List<String> getSecurl() {
        return securl;
    }
    
    public void setSecurl(List<String> securl) {
        this.securl = securl;
    }
}
