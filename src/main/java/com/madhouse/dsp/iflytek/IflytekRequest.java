package com.madhouse.dsp.iflytek;

import java.io.Serializable;

public class IflytekRequest implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    //Y 必填
    private String adunitid;//必填 讯飞广告平台注册广告位 ID
    
    private String tramaterialtype;
    
    private String is_support_deeplink;
    
    private String devicetype;//必填 设备类型
    
    private String os;//Y 客户端操作系统的类型
    
    private String osv;//Y 操作系统版本号
    
    private String openudid;//Y OpenUDID
    
    private String adid;//Y Android ID
    
    private String imei;//Y Y for Android
    
    private String idfa;//Y iOS IDFA
    
    private String mac;//Y mac 地址
    
    private String aaid;//AAID(Advertising Id)
    
    private String duid;//Y for WP  Windows Phone 用户终端的 DUID
    
    private String density;//Y
    
    private String operator;//Y网络运营商，取值：“ 46000” (即中国移动) ，“ 46001”（即中国联通），“ 46003”（即中国电信）
    
    /**
     * 联网类型(0—未知，
     * 1—Ethernet， 2—wifi，
     * 3—蜂窝网络，未知代，
     * 4—， 2G， 5—蜂窝网
     * 络， 3G， 6—蜂窝网络，
     * 4G)
     * */
    private String net; //Y
    
    private String ip;//Y 客户端 ip
    
    private String ua;//Y User-Agent( 字 符 串 ,需 escape 转义)
    
    private String ts;//Y 发 送 请 求 时 的 本 地UNIX 时间戳(10 进制)
    
    private String adw;//Y 广告位的宽度
    
    private String adh;// Y 广告位的高度
    
    private String dvw;//Y 设备屏幕的宽度
    
    private String dvh;//Y 设备屏幕的高度
    
    private String orientation;// Y 横竖屏 0 – 竖屏;1– 横屏
    
    private String vendor;//Y  设备生产商n Apple
    
    private String model;//Y 设备型号
    
    private String lan;//Y 目前使用的语言-国家 zh-CN
    
    private String brk;//iOS 设备是否越狱或者 Android 设备是否ROOT。 1--是, 0--否/未知(默认)
    
    private String geo;//地理位置(经度, 纬度)
    
    private String ssid;// Wifi SSID
    
    private String isboot;//Y 是否开屏
    
    private String batch_cnt;//Y for native AD /请求批量下发广告的数量
    
    //private Csinfo csinfo;//Object， 基站信息
    //应用信息
    private String appid;//Y appid （ 由讯飞广告平台提提供）
    
    private String appname;//Y App Name（ 由讯飞广告平台提提供）
    
    private String pkgname;//Y APP 应用的包称（ 由讯飞广告平台提供）
    
    private String mkt;//应用商店的编号1--iOS AppStore 2--Google Play 3--91 Market
    
    private String mkt_sn;//app 在上述应用商店内的编号
    
    private String mkt_cat;//app 在上述应用商店内的分类编号
    
    private String mkt_tag;//app 在上述应用商店内的标签
    
    //用户信息
    private String tags;//用户标签
    
    private String context; //广告请求上下文
    
    //调试选项
    //private Debug debug;//调试开关
    
    public String getAdunitid() {
        return adunitid;
    }
    
    public void setAdunitid(String adunitid) {
        this.adunitid = adunitid;
    }
    
    public String getTramaterialtype() {
        return tramaterialtype;
    }
    
    public void setTramaterialtype(String tramaterialtype) {
        this.tramaterialtype = tramaterialtype;
    }
    
    public String getIs_support_deeplink() {
        return is_support_deeplink;
    }
    
    public void setIs_support_deeplink(String is_support_deeplink) {
        this.is_support_deeplink = is_support_deeplink;
    }
    
    public String getDevicetype() {
        return devicetype;
    }
    
    public void setDevicetype(String devicetype) {
        this.devicetype = devicetype;
    }
    
    public String getOs() {
        return os;
    }
    
    public void setOs(String os) {
        this.os = os;
    }
    
    public String getOsv() {
        return osv;
    }
    
    public void setOsv(String osv) {
        this.osv = osv;
    }
    
    public String getOpenudid() {
        return openudid;
    }
    
    public void setOpenudid(String openudid) {
        this.openudid = openudid;
    }
    
    public String getAdid() {
        return adid;
    }
    
    public void setAdid(String adid) {
        this.adid = adid;
    }
    
    public String getImei() {
        return imei;
    }
    
    public void setImei(String imei) {
        this.imei = imei;
    }
    
    public String getIdfa() {
        return idfa;
    }
    
    public void setIdfa(String idfa) {
        this.idfa = idfa;
    }
    
    public String getMac() {
        return mac;
    }
    
    public void setMac(String mac) {
        this.mac = mac;
    }
    
    public String getAaid() {
        return aaid;
    }
    
    public void setAaid(String aaid) {
        this.aaid = aaid;
    }
    
    public String getDuid() {
        return duid;
    }
    
    public void setDuid(String duid) {
        this.duid = duid;
    }
    
    public String getDensity() {
        return density;
    }
    
    public void setDensity(String density) {
        this.density = density;
    }
    
    public String getOperator() {
        return operator;
    }
    
    public void setOperator(String operator) {
        this.operator = operator;
    }
    
    public String getNet() {
        return net;
    }
    
    public void setNet(String net) {
        this.net = net;
    }
    
    public String getIp() {
        return ip;
    }
    
    public void setIp(String ip) {
        this.ip = ip;
    }
    
    public String getUa() {
        return ua;
    }
    
    public void setUa(String ua) {
        this.ua = ua;
    }
    
    public String getTs() {
        return ts;
    }
    
    public void setTs(String ts) {
        this.ts = ts;
    }
    
    public String getAdw() {
        return adw;
    }
    
    public void setAdw(String adw) {
        this.adw = adw;
    }
    
    public String getAdh() {
        return adh;
    }
    
    public void setAdh(String adh) {
        this.adh = adh;
    }
    
    public String getDvw() {
        return dvw;
    }
    
    public void setDvw(String dvw) {
        this.dvw = dvw;
    }
    
    public String getDvh() {
        return dvh;
    }
    
    public void setDvh(String dvh) {
        this.dvh = dvh;
    }
    
    public String getOrientation() {
        return orientation;
    }
    
    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }
    
    public String getVendor() {
        return vendor;
    }
    
    public void setVendor(String vendor) {
        this.vendor = vendor;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public String getLan() {
        return lan;
    }
    
    public void setLan(String lan) {
        this.lan = lan;
    }
    
    public String getBrk() {
        return brk;
    }
    
    public void setBrk(String brk) {
        this.brk = brk;
    }
    
    public String getGeo() {
        return geo;
    }
    
    public void setGeo(String geo) {
        this.geo = geo;
    }
    
    public String getSsid() {
        return ssid;
    }
    
    public void setSsid(String ssid) {
        this.ssid = ssid;
    }
    
    public String getIsboot() {
        return isboot;
    }
    
    public void setIsboot(String isboot) {
        this.isboot = isboot;
    }
    
    public String getBatch_cnt() {
        return batch_cnt;
    }
    
    public void setBatch_cnt(String batch_cnt) {
        this.batch_cnt = batch_cnt;
    }
    
    public String getAppid() {
        return appid;
    }
    
    public void setAppid(String appid) {
        this.appid = appid;
    }
    
    public String getAppname() {
        return appname;
    }
    
    public void setAppname(String appname) {
        this.appname = appname;
    }
    
    public String getPkgname() {
        return pkgname;
    }
    
    public void setPkgname(String pkgname) {
        this.pkgname = pkgname;
    }
    
    public String getMkt() {
        return mkt;
    }
    
    public void setMkt(String mkt) {
        this.mkt = mkt;
    }
    
    public String getMkt_sn() {
        return mkt_sn;
    }
    
    public void setMkt_sn(String mkt_sn) {
        this.mkt_sn = mkt_sn;
    }
    
    public String getMkt_cat() {
        return mkt_cat;
    }
    
    public void setMkt_cat(String mkt_cat) {
        this.mkt_cat = mkt_cat;
    }
    
    public String getMkt_tag() {
        return mkt_tag;
    }
    
    public void setMkt_tag(String mkt_tag) {
        this.mkt_tag = mkt_tag;
    }
    
    public String getTags() {
        return tags;
    }
    
    public void setTags(String tags) {
        this.tags = tags;
    }
    
    public String getContext() {
        return context;
    }
    
    public void setContext(String context) {
        this.context = context;
    }
    
    @Override
    public String toString() {
        return "IflytekRequestEntity [adunitid=" + adunitid + ", tramaterialtype=" + tramaterialtype + ", is_support_deeplink=" + is_support_deeplink + ", devicetype=" + devicetype + ", os=" + os
            + ", osv=" + osv + ", openudid=" + openudid + ", adid=" + adid + ", imei=" + imei + ", idfa=" + idfa + ", mac=" + mac + ", aaid=" + aaid + ", duid=" + duid + ", density=" + density
            + ", operator=" + operator + ", net=" + net + ", ip=" + ip + ", ua=" + ua + ", ts=" + ts + ", adw=" + adw + ", adh=" + adh + ", dvw=" + dvw + ", dvh=" + dvh + ", orientation="
            + orientation + ", vendor=" + vendor + ", model=" + model + ", lan=" + lan + ", brk=" + brk + ", geo=" + geo + ", ssid=" + ssid + ", isboot=" + isboot + ", batch_cnt=" + batch_cnt
            + ", appid=" + appid + ", appname=" + appname + ", pkgname=" + pkgname + ", mkt=" + mkt + ", mkt_sn=" + mkt_sn + ", mkt_cat=" + mkt_cat + ", mkt_tag=" + mkt_tag + ", tags=" + tags
            + ", context=" + context + "]";
    }
    
}