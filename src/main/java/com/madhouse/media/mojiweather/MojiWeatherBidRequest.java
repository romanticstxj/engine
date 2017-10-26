package com.madhouse.media.mojiweather;

public class MojiWeatherBidRequest {
    /**
     * 广告位ID,必填选项
     */
    private String adid;
    
    /**
     * 请求的唯一标识,必填项
     */
    private String sessionid;
    
    /**
     * 广告类型（1：文字链 2：Banner 3：开屏),必填项
     */
    private Integer adtype;
    
    /**
     * 广告样式,必填项
     */
    private Integer adstyle;
    
    /**
     * 行业级别,必填项
     * 1：高级行业（优先级为1）
     * 2：高级行业（优先级为1）＋中高级行业（优先级为2）
     * 3：高级行业（优先级为1）＋中高级行业 （优先级为3）＋ 中级行业（优先级为3）
     */
    private Integer tradelevel;
    
    /**
     * 应用包名(Andoird 是应用的PackageName，对于IOS 是Bundle Identifier),必填项
     */
    private String pkgname;
    
    /**
     * APP名称,必填项
     */
    private String appname;
    
    /**
     * 连网方式,必填项 (0：unknown  1：WiFi 2：2G  3：3G 4：4G)
     */
    private Integer net;
    
    /**
     * 运营商,必填项 (0：unknown 1：移动 2：联通  3：电信)
     */
    private Integer carrier;
    
    /**
     * 操作系统类型,必填项 (0：Android 1：iOS 2：Windows Phone 3：Others)
     */
    private Integer os;
    
    /**
     * 操作系统版本,必填项
     */
    private String osv;
    
    /**
     * 最低出价（实际价格*100),必填项
     */
    private Integer basic_price;
    
    /**
     * 设备品牌和型号,必填项
     */
    private String device;
    
    /**
     * 用户浏览器标识user-agent(接口文档上是必填项，但墨迹以要求为非必填)
     */
    private String ua;
    
    /**
     * 用户来源IP,必填项
     */
    private String ip;
    
    /**
     * Android 系统的设备号
     */
    private String imei;
    
    /**
     * 终端网卡的 MAC 地址(去除冒号分隔符保持大写)
     */
    private String wma;
    
    /**
     * 用户终端的Android ID
     */
    private String andid;
    
    /**
     * Android Advertiser ID
     */
    private String andaid;
    
    /**
     * 仅 iOS 6.0 以上系统的IDFA
     */
    private String idfa;
    
    /**
     * iOS 终端设备的 OpenUDID
     */
    private String openudid;
    
    /**
     * 非Andorid、iOS 操作系统设备的唯一标示码
     */
    private String unqid;
    
    /**
     * 屏幕方向 (1：水平  2：垂直)
     */
    private String scrro;
    
    /**
     * 屏幕宽度(adtype为3时必填)
     */
    private Integer scrwidth;
    
    /**
     * 屏幕高度(adtype为3时必填)
     */
    private Integer scrheight;
    
    /**
     * debug 模式(1：开启debug 模式 2：非debug 模式)
     */
    private Integer debug;
    
    /**
     * 地理位置精度
     */
    private String lon;
    
    /**
     * 地理位置纬度
     */
    private String lat;
    
    /**
     *广告支持的feed类型(3:右图左文 5：上文下图 6：上文下3图
     * 类型3尺寸：200*150 类型5尺寸：700*300 类型6尺寸：200*150
     * 多个类型分号隔开，如：3;5;6
     * )
     */
    private String feed_support_types;

    private String comment;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getOs() {
        return os;
    }

    public void setOs(Integer os) {
        this.os = os;
    }

    public String getAdid() {
        return adid;
    }

    public void setAdid(String adid) {
        this.adid = adid;
    }

    public String getSessionid() {
        return sessionid;
    }

    public void setSessionid(String sessionid) {
        this.sessionid = sessionid;
    }

    public Integer getAdtype() {
        return adtype;
    }

    public void setAdtype(Integer adtype) {
        this.adtype = adtype;
    }

    public Integer getAdstyle() {
        return adstyle;
    }

    public void setAdstyle(Integer adstyle) {
        this.adstyle = adstyle;
    }

    public Integer getTradelevel() {
        return tradelevel;
    }

    public void setTradelevel(Integer tradelevel) {
        this.tradelevel = tradelevel;
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

    public Integer getNet() {
        return net;
    }

    public void setNet(Integer net) {
        this.net = net;
    }

    public Integer getCarrier() {
        return carrier;
    }

    public void setCarrier(Integer carrier) {
        this.carrier = carrier;
    }

    public String getOsv() {
        return osv;
    }

    public void setOsv(String osv) {
        this.osv = osv;
    }

    public Integer getBasic_price() {
        return basic_price;
    }

    public void setBasic_price(Integer basic_price) {
        this.basic_price = basic_price;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
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

    public String getAndid() {
        return andid;
    }

    public void setAndid(String andid) {
        this.andid = andid;
    }

    public String getAndaid() {
        return andaid;
    }

    public void setAndaid(String andaid) {
        this.andaid = andaid;
    }

    public String getIdfa() {
        return idfa;
    }

    public void setIdfa(String idfa) {
        this.idfa = idfa;
    }

    public String getOpenudid() {
        return openudid;
    }

    public void setOpenudid(String openudid) {
        this.openudid = openudid;
    }

    public String getUnqid() {
        return unqid;
    }

    public void setUnqid(String unqid) {
        this.unqid = unqid;
    }

    public String getScrro() {
        return scrro;
    }

    public void setScrro(String scrro) {
        this.scrro = scrro;
    }

    public Integer getScrwidth() {
        return scrwidth;
    }

    public void setScrwidth(Integer scrwidth) {
        this.scrwidth = scrwidth;
    }

    public Integer getScrheight() {
        return scrheight;
    }

    public void setScrheight(Integer scrheight) {
        this.scrheight = scrheight;
    }

    public Integer getDebug() {
        return debug;
    }

    public void setDebug(Integer debug) {
        this.debug = debug;
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

    public String getFeed_support_types() {
        return feed_support_types;
    }

    public void setFeed_support_types(String feed_support_types) {
        this.feed_support_types = feed_support_types;
    }
}
