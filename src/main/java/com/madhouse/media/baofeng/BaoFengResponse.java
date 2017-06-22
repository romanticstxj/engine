package com.madhouse.media.baofeng;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;



public class BaoFengResponse {
    /**
     * 必须 响应的Bid Request的唯一ID
     */
    private String id;
    /**
     * 必须 广告标题
     */
    private String title;
    /**
     * 必须	广告描述
     */
    private String desc;
    /**
     * 可选	广告展现报数链接，url为地址，其中time的值为该链接报数时间。可以有多个。type_mma:0为不调用mma SDK报数，1为调用。mma包括秒针和admaster
     */
    private List<PV> pv;
    /**
     * 可选	点击报数地址，可以有多个。type_mma:0为不调用mma SDK报数，1为调用。mma包括秒针和admaster
     */
    private List<PV> click;
    /**
     * 必须	物料素材相关信息，其中携带参数：w=宽度 h=高度，src为物料地址
     */
    private Img img;
    /**
     * 必须	点击落地页
     */
    private String target;
    /**
     * 可选	APP应用名称
     */
    private String apkname;
    /**
     * 可选	0代表普通广告，1代表app下载  默认为0
     */
    private int ad_type;
    /**
     * 可选	安装包名称：ad_type=1时必填，用以判断本地是否已安装该应用；【只android端】
     */
    @JSONField(name = "package")
    private String packageAlias;
    /**
     * 可选	App的唯一id号：ad_type=1时必填，用以应用内下载app，只支持6和7广告位；【只ios端】
     */
    private String app_store_id;

    public String getId() {
        return id;
    }

    /**
     * 必须 响应的Bid Request的唯一ID
     */
    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    /**
     * 必须 广告标题
     */
    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * 必须	广告描述
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }

    public List<PV> getPv() {
        return pv;
    }

    /**
     * 可选	广告展现报数链接，url为地址，其中time的值为该链接报数时间。可以有多个。type_mma:0为不调用mma SDK报数，1为调用。mma包括秒针和admaster
     */
    public void setPv(List<PV> pv) {
        this.pv = pv;
    }

    public List<PV> getClick() {
        return click;
    }

    /**
     * 可选	点击报数地址，可以有多个。type_mma:0为不调用mma SDK报数，1为调用。mma包括秒针和admaster
     */
    public void setClick(List<PV> click) {
        this.click = click;
    }

    public Img getImg() {
        return img;
    }

    /**
     * 必须	物料素材相关信息，其中携带参数：w=宽度 h=高度，src为物料地址
     */
    public void setImg(Img img) {
        this.img = img;
    }

    public String getTarget() {
        return target;
    }

    /**
     * 必须	点击落地页
     */
    public void setTarget(String target) {
        this.target = target;
    }

    public String getApkname() {
        return apkname;
    }

    /**
     * 可选	APP应用名称
     */
    public void setApkname(String apkname) {
        this.apkname = apkname;
    }

    public int getAd_type() {
        return ad_type;
    }

    /**
     * 可选	0代表普通广告，1代表app下载  默认为0
     */
    public void setAd_type(int ad_type) {
        this.ad_type = ad_type;
    }

    public String getPackageAlias() {
        return packageAlias;
    }

    /**
     * 可选	安装包名称：ad_type=1时必填，用以判断本地是否已安装该应用；【只android端】
     */
    public void setPackageAlias(String packageAlias) {
        this.packageAlias = packageAlias;
    }

    public String getApp_store_id() {
        return app_store_id;
    }

    /**
     * 可选	App的唯一id号：ad_type=1时必填，用以应用内下载app，只支持6和7广告位；【只ios端】
     */
    public void setApp_store_id(String app_store_id) {
        this.app_store_id = app_store_id;
    }

    public class Img {
        private int w;
        private int h;
        private String src;

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

        public String getSrc() {
            return src;
        }

        public void setSrc(String src) {
            this.src = src;
        }
    }

    public class PV {
        private int type_mma;
        private String url;

        public PV() {
        }

        public PV(int type_mma, String url) {
            this.type_mma = type_mma;
            this.url = url;
        }

        public int getType_mma() {
            return type_mma;
        }

        public void setType_mma(int type_mma) {
            this.type_mma = type_mma;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

}

