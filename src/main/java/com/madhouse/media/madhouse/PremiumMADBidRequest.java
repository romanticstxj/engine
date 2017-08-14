package com.madhouse.media.madhouse;

import java.util.List;

public class PremiumMADBidRequest {
    /**
     * Bid Request ID 广告请求流水号必须唯一
     */
    private String bid;
    /**
     * 广告位标识
     */
    private String adspaceid;
    /**
     * 广告类型 1文字链，2 Banner，3 图形文字链，4 全屏，5 插页，6 开屏
     */
    private String adtype;
    /**
     * android的packageName  ios的bundle Identifier
     */
    private String pkgname;
    /**
     * 应用名称  urlEncode
     */
    private String appname;
    /**
     * 联网方式
     * 0 Unknown
     * 1 WiFi
     * 2 2G
     * 3 3G
     * 4 4G
     */
    private String conn;
    /**
     * 运营商
     * 0 Unknown
     * 1 移动
     * 2 联通
     * 3 电信
     */
    private String carrier;
    /**
     * 操作系统类型
     * 0 android
     * 1 ios
     * 2 windows phone
     * 3 others
     */
    private String os;
    /**
     * 操作系统版本
     */
    private String osv;
    /**
     * android 操作系统设备号
     * os=0 时参 数 imei、 wma、aid、 aaid 不能同 时为空
     */
    private String imei;
    /**
     * MAC地址 去掉冒号保持大写
     * os =0 时参 数 imei、 wma、aid、 aaid 不能同 时为空；
     * os =1 时 wma、idfa、 oid 不能同 时为空
     */
    private String wma;
    /**
     * android ID
     * os =0 时参 数 imei、 wma、aid、 aaid 不能同 时为空
     */
    private String aid;
    /**
     * android advertiser ID
     * os =0 时参 数 imei、 wma、aid、 aaid 不能同 时为空
     */
    private String aaid;
    /**
     * ios 6.0 +的idfa
     * os =1 时 wma、idfa、 oid 不能同 时为空
     */
    private String idfa;
    /**
     * ios 设备的 openUDID
     * os =1 时 wma、idfa、 oid 不能同 时为空
     */
    private String oid;
    /**
     * 非android ios 系统的设备唯一标识码
     */
    private String uid;
    /**
     * 设备的品牌和型号 urlEncode编码
     */
    private String device;
    /**
     *设备类型
     *  值   描述
     *  1   Phone
     *  2   Pad
     *  3   BOX
     *  4   TV
     *  11  Computer
     */
    private String devicetype;
    /**
     * 用户终端浏览器标识
     */
    private String ua;
    /**
     * 用户来源IP
     */
    private String ip;
    /**
     * 广告位的宽度
     */
    private String width;
    /**
     * 广告位的高度
     */
    private String height;
    /**
     * 媒体自己的publishID
     */
    private String pid;
    /**
     * 媒体类型ID
     */
    private String pcat;
    /**
     * 投放媒体形式，默认全部媒体  1：手机应用  2：手机网站
     */
    private String media;
    /**
     * 调试模式 1：调试模式  0：非调试模式
     */
    private String debug;
    /**
     * 屏幕密度
     */
    private String density;
    /**
     * 手机所在位置的经度
     */
    private String lon;
    /**
     * 手机所在位置的纬度
     */
    private String lat;
    /**
     * 手机号码
     */
    private String cell;
    /**
     * MD5加密后的手机号码
     */
    private String mcell;
    /**
     * 广告请求时所携带的标签code，UrlEncode编码
     * */
    private List<String> label;
    
    /**
     * PDB、PD模式的deal id
     */
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
    public String getAdspaceid() {
        return adspaceid;
    }
    public void setAdspaceid(String adspaceid) {
        this.adspaceid = adspaceid;
    }
    public String getAdtype() {
        return adtype;
    }
    public void setAdtype(String adtype) {
        this.adtype = adtype;
    }
    public String getPkgname() {
        return pkgname;
    }
    public void setPkgname(String pkgname) {
        this.pkgname = pkgname;
    }
    public String getAppname() {
        return appname;
    }
    public void setAppname(String appname) {
        this.appname = appname;
    }
    public String getConn() {
        return conn;
    }
    public void setConn(String conn) {
        this.conn = conn;
    }
    public String getCarrier() {
        return carrier;
    }
    public void setCarrier(String carrier) {
        this.carrier = carrier;
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
    public String getImei() {
        return imei;
    }
    public void setImei(String imei) {
        this.imei = imei;
    }
    public String getWma() {
        return wma;
    }
    public void setWma(String wma) {
        this.wma = wma;
    }
    public String getAid() {
        return aid;
    }
    public void setAid(String aid) {
        this.aid = aid;
    }
    public String getAaid() {
        return aaid;
    }
    public void setAaid(String aaid) {
        this.aaid = aaid;
    }
    public String getIdfa() {
        return idfa;
    }
    public void setIdfa(String idfa) {
        this.idfa = idfa;
    }
    public String getOid() {
        return oid;
    }
    public void setOid(String oid) {
        this.oid = oid;
    }
    public String getUid() {
        return uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }
    public String getDevice() {
        return device;
    }
    public void setDevice(String device) {
        this.device = device;
    }
    public String getDevicetype() {
        return devicetype;
    }
    public void setDevicetype(String devicetype) {
        this.devicetype = devicetype;
    }
    public String getUa() {
        return ua;
    }
    public void setUa(String ua) {
        this.ua = ua;
    }
    public String getIp() {
        return ip;
    }
    public void setIp(String ip) {
        this.ip = ip;
    }
    public String getWidth() {
        return width;
    }
    public void setWidth(String width) {
        this.width = width;
    }
    public String getHeight() {
        return height;
    }
    public void setHeight(String height) {
        this.height = height;
    }
    public String getPid() {
        return pid;
    }
    public void setPid(String pid) {
        this.pid = pid;
    }
    public String getPcat() {
        return pcat;
    }
    public void setPcat(String pcat) {
        this.pcat = pcat;
    }
    public String getMedia() {
        return media;
    }
    public void setMedia(String media) {
        this.media = media;
    }
    public String getDebug() {
        return debug;
    }
    public void setDebug(String debug) {
        this.debug = debug;
    }
    public String getDensity() {
        return density;
    }
    public void setDensity(String density) {
        this.density = density;
    }
    public String getLon() {
        return lon;
    }
    public void setLon(String lon) {
        this.lon = lon;
    }
    public String getLat() {
        return lat;
    }
    public void setLat(String lat) {
        this.lat = lat;
    }
    public String getCell() {
        return cell;
    }
    public void setCell(String cell) {
        this.cell = cell;
    }
    public String getMcell() {
        return mcell;
    }
    public void setMcell(String mcell) {
        this.mcell = mcell;
    }
    public List<String> getLabel() {
        return label;
    }
    public void setLabel(List<String> label) {
        this.label = label;
    }
    
}
