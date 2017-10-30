package com.madhouse.media.madhouse;

import java.util.List;

public class PremiumMADResponse {
    
    private Integer returncode;
    
    //广告位ID。
    private String adspaceid;
    
    //标识广告来源
    private String source;

    //请求ID
    private String bid;

    //Bid ID
    private String bidid;

    //广告活动ID
    private String cid;
        
    //广告创意或素材ID
    private String crid;
    
    private Integer adwidth;
    
    private Integer adheight;
    
    private Integer adtype;
    
    //视频接口必返回 视频素材的时长，单位为秒。
    private Integer duration;
    
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
    
    //  广告的文字内容； 
    private String displaycontent;
    
    //展示监播
    private List<String> imgtracking;
    
    //点击地址
    private List<String> thclkurl;
    
    //品牌安全监测
    private List<String> securl;

    //宏替换参数
    private List<String> exts;

    //PDB、PD模式的deal id
    private String dealid;
    
    public String getDealid() {
        return dealid;
    }

    public void setDealid(String dealid) {
        this.dealid = dealid;
    }

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

    public Integer getReturncode() {
        return returncode;
    }

    public void setReturncode(Integer returncode) {
        this.returncode = returncode;
    }

    public Integer getAdwidth() {
        return adwidth;
    }

    public void setAdwidth(Integer adwidth) {
        this.adwidth = adwidth;
    }

    public Integer getAdheight() {
        return adheight;
    }

    public void setAdheight(Integer adheight) {
        this.adheight = adheight;
    }

    public Integer getAdtype() {
        return adtype;
    }

    public void setAdtype(Integer adtype) {
        this.adtype = adtype;
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

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
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

    public String getBidid() {
        return bidid;
    }

    public void setBidid(String bidid) {
        this.bidid = bidid;
    }

    public String getDisplaycontent() {
        return displaycontent;
    }

    public void setDisplaycontent(String displaycontent) {
        this.displaycontent = displaycontent;
    }

    public String getCrid() {
        return crid;
    }

    public void setCrid(String crid) {
        this.crid = crid;
    }

    public List<String> getExts() {
        return exts;
    }

    public void setExts(List<String> exts) {
        this.exts = exts;
    }
}
