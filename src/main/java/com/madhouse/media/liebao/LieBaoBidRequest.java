package com.madhouse.media.liebao;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@SuppressWarnings({"unused", "SpellCheckingInspection"})
public class LieBaoBidRequest {
    private String id;//string required 请求唯一ID，由ADX生成
    private List<Imp> imp;//object array required 广告位曝光的imp对象列表
    private Object site;//object recommended 站点流量，暂无
    private App app;//object recommended 移动APP流量属性
    private Device device;//object recommended 用户设备信息
    private User user;//object recommended 受众信息
    private Integer test;//Integer optional 0—生产模式 1—测试模式 默认0
    private Integer at;//Integer optional 1—GFP 2—GSP
    private Integer tmax;//Integer optional 目前均为150，单位毫秒
    private List<String> cur;//String array optional 货币目前只支持一种 ISO­4217标准国内为CNY海外为USD
    private List<String> bcat;//string array optional 广告行业黑名单
    private List<String> badv;//string array optional 域名黑名单，例如：(eg: test.com)
    private Object ext;//object optional 扩展字段
    // 这个字段是为了辅助response构建创建的
    private float admType;
    // 由于assets有多个，这里记录一下使用的assetsid，方便在构建response时使用
    private int selectedAssetsId;

    public int getSelectedAssetsId() {
        return selectedAssetsId;
    }

    public void setSelectedAssetsId(int selectedAssetsId) {
        this.selectedAssetsId = selectedAssetsId;
    }

    public float getAdmType() {
        return admType;
    }

    public void setAdmType(float admType) {
        this.admType = admType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Imp> getImp() {
        return imp;
    }

    public void setImp(List<Imp> imp) {
        this.imp = imp;
    }

    public Object getSite() {
        return site;
    }

    public void setSite(Object site) {
        this.site = site;
    }

    public App getApp() {
        return app;
    }

    public void setApp(App app) {
        this.app = app;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getTest() {
        return test;
    }

    public void setTest(Integer test) {
        this.test = test;
    }

    public Integer getAt() {
        return at;
    }

    public void setAt(Integer at) {
        this.at = at;
    }

    public Integer getTmax() {
        return tmax;
    }

    public void setTmax(Integer tmax) {
        this.tmax = tmax;
    }

    public List<String> getCur() {
        return cur;
    }

    public void setCur(List<String> cur) {
        this.cur = cur;
    }

    public List<String> getBcat() {
        return bcat;
    }

    public void setBcat(List<String> bcat) {
        this.bcat = bcat;
    }

    public List<String> getBadv() {
        return badv;
    }

    public void setBadv(List<String> badv) {
        this.badv = badv;
    }

    public Object getExt() {
        return ext;
    }

    public void setExt(Object ext) {
        this.ext = ext;
    }

    @SuppressWarnings("unused")
    public static class Imp {
        private String id;//string required 曝光ID，通常从1开始
        private String tagid;//string optional 广告位标识ID
        private Float bidfloor;//Float required 竞价底价， 单位：中国地区CNY（元），其他国家USD(美元)，默认 0.01/CPM
        private String bidfloorcur;//string optional 货币，使用ISO­4217标准，暂支持一种 国内：CNY 海外：USD 单位：元/千次展现
        @JSONField(name = "native")
        private Native nativeObject;//object optional Native对象
        private Banner banner;//object optional Banner对象
        private Video video;//object optional 视频对象
        private Pmp pmp;//object optional 私有交易
        private Object ext;//object optional 扩展字段

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTagid() {
            return tagid;
        }

        public void setTagid(String tagid) {
            this.tagid = tagid;
        }

        public Float getBidfloor() {
            return bidfloor;
        }

        public void setBidfloor(Float bidfloor) {
            this.bidfloor = bidfloor;
        }

        public String getBidfloorcur() {
            return bidfloorcur;
        }

        public void setBidfloorcur(String bidfloorcur) {
            this.bidfloorcur = bidfloorcur;
        }

        public Native getNativeObject() {
            return nativeObject;
        }

        public void setNativeObject(Native nativeObject) {
            this.nativeObject = nativeObject;
        }

        public Banner getBanner() {
            return banner;
        }

        public void setBanner(Banner banner) {
            this.banner = banner;
        }

        public Video getVideo() {
            return video;
        }

        public void setVideo(Video video) {
            this.video = video;
        }

        public Pmp getPmp() {
            return pmp;
        }

        public void setPmp(Pmp pmp) {
            this.pmp = pmp;
        }

        public Object getExt() {
            return ext;
        }

        public void setExt(Object ext) {
            this.ext = ext;
        }

        public static class Pmp {
            @JSONField(name = "private_auction")
            private Integer privateAuction;//Integer optional 标识在Deal对象中指明的席位的竞拍合格标准， 0标识接受所有竞拍， 1标识 竞拍受deals属性中描述的规则的限制
            private List<Deal> deals;//object array optional 一组Deal对象， 用于传输适用于本次展 示的交易信息
            private Object ext;//object optional 扩展字段

            public Integer getPrivateAuction() {
                return privateAuction;
            }

            public void setPrivateAuction(Integer privateAuction) {
                this.privateAuction = privateAuction;
            }

            public List<Deal> getDeals() {
                return deals;
            }

            public void setDeals(List<Deal> deals) {
                this.deals = deals;
            }

            public Object getExt() {
                return ext;
            }

            public void setExt(Object ext) {
                this.ext = ext;
            }

            public static class Deal {
                private String id;//string required 直接交易的唯一ID
                private Float bidfloor;//Float optional 竞价底价， 单位：中国地区CNY（元），其他国家USD(美元)，默认 0.01/CPM
                private String bidfloorcur;//string optional 货币，使用ISO­4217标准，暂支持一种国内：CNY海外：USD单位：元/千次展现
                private Integer at;//Integer optional 1—GFP 2—GSP
                private Object ext;//object optional 扩展字段

                public String getId() {
                    return id;
                }

                public void setId(String id) {
                    this.id = id;
                }

                public Float getBidfloor() {
                    return bidfloor;
                }

                public void setBidfloor(Float bidfloor) {
                    this.bidfloor = bidfloor;
                }

                public String getBidfloorcur() {
                    return bidfloorcur;
                }

                public void setBidfloorcur(String bidfloorcur) {
                    this.bidfloorcur = bidfloorcur;
                }

                public Integer getAt() {
                    return at;
                }

                public void setAt(Integer at) {
                    this.at = at;
                }

                public Object getExt() {
                    return ext;
                }

                public void setExt(Object ext) {
                    this.ext = ext;
                }
            }
        }

        public static class Video {
            private List<String> mimes;//string array required 支持的视频格式（video/mp4,video/x­ms­wmv）
            private Integer minduration;//Integer optional 视频最短时长（单位：秒）
            private Integer maxduration;//Integer optional 视频最长时长（单位：秒）
            private List<Integer> protocols;//Integer array required 支持的vast video协议，见附录4.7
            private Integer w;//Integer required 播放器宽（广告位尺寸）
            private Integer h;//Integer required 播放器高（广告位尺寸）
            private Integer skip;//Integer optional 播放器是否允许视频可跳过（1=Yes， 0= No）
            private Integer pos;//Integer optional 广告在屏幕上曝光位置，见附录4.9
            private Integer linearity;//Integer required 视频广告以linear 或 nonlinear方式展现，见附录4.10

            public List<String> getMimes() {
                return mimes;
            }

            public void setMimes(List<String> mimes) {
                this.mimes = mimes;
            }

            public Integer getMinduration() {
                return minduration;
            }

            public void setMinduration(Integer minduration) {
                this.minduration = minduration;
            }

            public Integer getMaxduration() {
                return maxduration;
            }

            public void setMaxduration(Integer maxduration) {
                this.maxduration = maxduration;
            }

            public List<Integer> getProtocols() {
                return protocols;
            }

            public void setProtocols(List<Integer> protocols) {
                this.protocols = protocols;
            }

            public Integer getW() {
                return w;
            }

            public void setW(Integer w) {
                this.w = w;
            }

            public Integer getH() {
                return h;
            }

            public void setH(Integer h) {
                this.h = h;
            }

            public Integer getSkip() {
                return skip;
            }

            public void setSkip(Integer skip) {
                this.skip = skip;
            }

            public Integer getPos() {
                return pos;
            }

            public void setPos(Integer pos) {
                this.pos = pos;
            }

            public Integer getLinearity() {
                return linearity;
            }

            public void setLinearity(Integer linearity) {
                this.linearity = linearity;
            }
        }

        public static class Banner {
            private Integer w;//Integer recommended 展示的宽度：像素
            private Integer h;//Integer recommended展示的高度：像素
            private Integer wmin;//Integer optional 展示宽度的最小值：像素
            private Integer hmin;//Integer optional 展示高度的最小值：像素
            private List<String> mimes;//string array optional 支持的mime­type (image/jpeg)
            private Object ext;//object optional 扩展字段

            public Integer getW() {
                return w;
            }

            public void setW(Integer w) {
                this.w = w;
            }

            public Integer getH() {
                return h;
            }

            public void setH(Integer h) {
                this.h = h;
            }

            public Integer getWmin() {
                return wmin;
            }

            public void setWmin(Integer wmin) {
                this.wmin = wmin;
            }

            public Integer getHmin() {
                return hmin;
            }

            public void setHmin(Integer hmin) {
                this.hmin = hmin;
            }

            public List<String> getMimes() {
                return mimes;
            }

            public void setMimes(List<String> mimes) {
                this.mimes = mimes;
            }

            public Object getExt() {
                return ext;
            }

            public void setExt(Object ext) {
                this.ext = ext;
            }
        }

        public static class Native {
            private String ver;//String optional 版本
            private String request;//string required 请求信息,遵循NATIVE请求接口
            private Object ext;//object optional 扩展字段

            /**
             * 将请求中的request字符串JSON，转换成LieBaoNative对象
             * @return 返回LieBaoNative对象
             */
            public LieBaoNative getRequestNativeObject() {
                if (StringUtils.isNotEmpty(request)) {
                    return JSON.parseObject(request, LieBaoNative.class);
                }
                return null;
            }

            public String getVer() {
                return ver;
            }

            public void setVer(String ver) {
                this.ver = ver;
            }

            public String getRequest() {
                return request;
            }

            public void setRequest(String request) {
                this.request = request;
            }

            public Object getExt() {
                return ext;
            }

            public void setExt(Object ext) {
                this.ext = ext;
            }
            @SuppressWarnings("unused")
            public static class LieBaoNative {
                @JSONField(name = "native")
                private NativeTopLevel nativeTopLevel;

                public NativeTopLevel getNativeTopLevel() {
                    return nativeTopLevel;
                }

                public void setNativeTopLevel(NativeTopLevel nativeTopLevel) {
                    this.nativeTopLevel = nativeTopLevel;
                }

                public static class NativeTopLevel {
                    private List<Assets> assets;//Object array required 请求位置的信息

                    public List<Assets> getAssets() {
                        return assets;
                    }

                    public void setAssets(List<Assets> assets) {
                        this.assets = assets;
                    }

                    public static class Assets {
                        private Integer id;//Integer optional 位置ID，
                        private Integer required;//Integer optional 1—需要此asset 0—不需要
                        private Img img;//object optional 图片属性，用于样式区别
                        private Title title;//object optional 标题属性
                        private Data data;//object optional 描述属性

                        public Integer getId() {
                            return id;
                        }

                        public void setId(Integer id) {
                            this.id = id;
                        }

                        public Integer getRequired() {
                            return required;
                        }

                        public void setRequired(Integer required) {
                            this.required = required;
                        }

                        public Img getImg() {
                            return img;
                        }

                        public void setImg(Img img) {
                            this.img = img;
                        }

                        public Title getTitle() {
                            return title;
                        }

                        public void setTitle(Title title) {
                            this.title = title;
                        }

                        public Data getData() {
                            return data;
                        }

                        public void setData(Data data) {
                            this.data = data;
                        }

                        @SuppressWarnings("SpellCheckingInspection")
                        public static class Img {
                            private Integer type;//Integer optional 图片类型，见附录4.3
                            private Integer w;//Integer optional 图片宽，见附录4.4
                            private Integer h;//Integer optional 图片高，见附录4.4
                            private Integer wmin;//Integer optional 图片最小宽
                            private Integer hmin;//Integer optional 图片最小高
                            private List<String> mimes;//String array optional 支持的MIME type，目前只支持 image/jpeg
                            private Object ext;//object optional 扩展字段

                            public Integer getType() {
                                return type;
                            }

                            public void setType(Integer type) {
                                this.type = type;
                            }

                            public Integer getW() {
                                return w;
                            }

                            public void setW(Integer w) {
                                this.w = w;
                            }

                            public Integer getH() {
                                return h;
                            }

                            public void setH(Integer h) {
                                this.h = h;
                            }

                            public Integer getWmin() {
                                return wmin;
                            }

                            public void setWmin(Integer wmin) {
                                this.wmin = wmin;
                            }

                            public Integer getHmin() {
                                return hmin;
                            }

                            public void setHmin(Integer hmin) {
                                this.hmin = hmin;
                            }

                            public List<String> getMimes() {
                                return mimes;
                            }

                            public void setMimes(List<String> mimes) {
                                this.mimes = mimes;
                            }

                            public Object getExt() {
                                return ext;
                            }

                            public void setExt(Object ext) {
                                this.ext = ext;
                            }
                        }

                        public static class Title {
                            private Integer len;//Integer optional 小于等于100字符

                            public Integer getLen() {
                                return len;
                            }

                            public void setLen(Integer len) {
                                this.len = len;
                            }
                        }

                        public static class Data {
                            private Integer type;//Integer optional 见附录4.5
                            private Integer len;//Integer optional 长度小于等于500

                            public Integer getType() {
                                return type;
                            }

                            public void setType(Integer type) {
                                this.type = type;
                            }

                            public Integer getLen() {
                                return len;
                            }

                            public void setLen(Integer len) {
                                this.len = len;
                            }
                        }
                    }
                }
            }
        }
    }

    public static class App {
        private String id;//string recommend 平台设置的APPID
        private String name;//string optional 应用名称
        private String bundle;//string optional 包名
        private String domain;//string optional App对应的域名
        private String storeurl;//string optional 应用商店地址
        private List<String> cat;//string array optional 对应类别 暂为空
        private List<String> sectioncat;//String array optional 应用的当前部分的分类
        private String ver;//string optional 版本号
        private Integer paid;//Integer optional 0—免费 1—付费
        private Publisher publisher;//object optional 流量信息
        private String keywords;//string optional App关键词描述
        private Object ext;//object optional 保留扩展字段

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getBundle() {
            return bundle;
        }

        public void setBundle(String bundle) {
            this.bundle = bundle;
        }

        public String getDomain() {
            return domain;
        }

        public void setDomain(String domain) {
            this.domain = domain;
        }

        public String getStoreurl() {
            return storeurl;
        }

        public void setStoreurl(String storeurl) {
            this.storeurl = storeurl;
        }

        public List<String> getCat() {
            return cat;
        }

        public void setCat(List<String> cat) {
            this.cat = cat;
        }

        public List<String> getSectioncat() {
            return sectioncat;
        }

        public void setSectioncat(List<String> sectioncat) {
            this.sectioncat = sectioncat;
        }

        public String getVer() {
            return ver;
        }

        public void setVer(String ver) {
            this.ver = ver;
        }

        public Integer getPaid() {
            return paid;
        }

        public void setPaid(Integer paid) {
            this.paid = paid;
        }

        public Publisher getPublisher() {
            return publisher;
        }

        public void setPublisher(Publisher publisher) {
            this.publisher = publisher;
        }

        public String getKeywords() {
            return keywords;
        }

        public void setKeywords(String keywords) {
            this.keywords = keywords;
        }

        public Object getExt() {
            return ext;
        }

        public void setExt(Object ext) {
            this.ext = ext;
        }

        public static class Publisher {
            private String id;//string recommend 平台设置的publisherid
            private String name;//string optional publisher名称
            private String cat;//string optional 对应类别，暂为空
            private String domain;//string optional Publisher最高层次的域名
            private Object ext;//object optional 扩展字段

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getCat() {
                return cat;
            }

            public void setCat(String cat) {
                this.cat = cat;
            }

            public String getDomain() {
                return domain;
            }

            public void setDomain(String domain) {
                this.domain = domain;
            }

            public Object getExt() {
                return ext;
            }

            public void setExt(Object ext) {
                this.ext = ext;
            }
        }
    }

    @SuppressWarnings("unused")
    public static class Device {
        private String ua;//string recommend Browser User Agent
        private Geo geo;//object recommend 设备位置信息
        private String ip;//string recommend IPv4
        private Integer devicetype;//Integer optional 设备类型，见附录4.2设备类型
        private String make;//string optional 设备厂商
        private String model;//string optional 设备型号
        private String os;//string optional 操作系统 (ios android)
        private String osv;//string optional 操作系统版本(4.1.1)
        private Integer w;//Integer optional 屏幕宽：像素
        private Integer h;//Integer optional 屏幕高：像素
        private Integer ppi;//Integer optional 每英寸上的像素数
        private Integer js;//Integer optional 是否支持js， 0­不支持 1­支持 默认0
        private String language;//string optional 语言简称，ISO­639­1­alpha­2 标准
        private String carrier;//string optional 运营商，例如：VERIZON
        private Integer connectiontype;//Integer optional 联网方式 见附录4.1 联网方式
        private String dpidmd5;//string recommend 安卓ID或IDFA 的MD5值
        private String ifa;//string recommend IOS设备为IDFA，中国android设备为安卓id，国外android设备为gaid
        private String imei;//string recommend 手机imei，明文，仅对国内流量生效
        private Object ext;//object optional 扩展字段

        public String getUa() {
            return ua;
        }

        public void setUa(String ua) {
            this.ua = ua;
        }

        public Geo getGeo() {
            return geo;
        }

        public void setGeo(Geo geo) {
            this.geo = geo;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public Integer getDevicetype() {
            return devicetype;
        }

        public void setDevicetype(Integer devicetype) {
            this.devicetype = devicetype;
        }

        public String getMake() {
            return make;
        }

        public void setMake(String make) {
            this.make = make;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
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

        public Integer getW() {
            return w;
        }

        public void setW(Integer w) {
            this.w = w;
        }

        public Integer getH() {
            return h;
        }

        public void setH(Integer h) {
            this.h = h;
        }

        public Integer getPpi() {
            return ppi;
        }

        public void setPpi(Integer ppi) {
            this.ppi = ppi;
        }

        public Integer getJs() {
            return js;
        }

        public void setJs(Integer js) {
            this.js = js;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public String getCarrier() {
            return carrier;
        }

        public void setCarrier(String carrier) {
            this.carrier = carrier;
        }

        public Integer getConnectiontype() {
            return connectiontype;
        }

        public void setConnectiontype(Integer connectiontype) {
            this.connectiontype = connectiontype;
        }

        public String getDpidmd5() {
            return dpidmd5;
        }

        public void setDpidmd5(String dpidmd5) {
            this.dpidmd5 = dpidmd5;
        }

        public String getIfa() {
            return ifa;
        }

        public void setIfa(String ifa) {
            this.ifa = ifa;
        }

        public String getImei() {
            return imei;
        }

        public void setImei(String imei) {
            this.imei = imei;
        }

        public Object getExt() {
            return ext;
        }

        public void setExt(Object ext) {
            this.ext = ext;
        }

        public static class Geo {
            private Double lat;//double optional 纬度 暂不支持
            private Double lon;//Double optional 经度 暂不支持
            private String country;//string optional 国家，SO­3166­1­alpha­3 标准
            private Integer type;//Integer optional 定位类型 1—GPS 2­­­IP，现在只支持IP
            private String region;//string optional 地区，使用ISO­3166­2 标准 暂不支持
            private String city;//string optional 城市 暂不支持
            private Integer utcoffset;//Integer optional 时区
            private Object ext;//object optional 扩展字段

            public Double getLat() {
                return lat;
            }

            public void setLat(Double lat) {
                this.lat = lat;
            }

            public Double getLon() {
                return lon;
            }

            public void setLon(Double lon) {
                this.lon = lon;
            }

            public String getCountry() {
                return country;
            }

            public void setCountry(String country) {
                this.country = country;
            }

            public Integer getType() {
                return type;
            }

            public void setType(Integer type) {
                this.type = type;
            }

            public String getRegion() {
                return region;
            }

            public void setRegion(String region) {
                this.region = region;
            }

            public String getCity() {
                return city;
            }

            public void setCity(String city) {
                this.city = city;
            }

            public Integer getUtcoffset() {
                return utcoffset;
            }

            public void setUtcoffset(Integer utcoffset) {
                this.utcoffset = utcoffset;
            }

            public Object getExt() {
                return ext;
            }

            public void setExt(Object ext) {
                this.ext = ext;
            }
        }
    }

    public static class User {
        private String buyerid;//string recommended 买方提供的ID，暂无
        private Integer yob;//Integer optional 4位出生年 暂无
        private String gender;//string optional 性别 M—男 F—女 O—未知
        private String keywords;//String optional 兴趣，逗号分隔 暂无
        private Object ext;//object optional 扩展字段

        public String getBuyerid() {
            return buyerid;
        }

        public void setBuyerid(String buyerid) {
            this.buyerid = buyerid;
        }

        public Integer getYob() {
            return yob;
        }

        public void setYob(Integer yob) {
            this.yob = yob;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getKeywords() {
            return keywords;
        }

        public void setKeywords(String keywords) {
            this.keywords = keywords;
        }

        public Object getExt() {
            return ext;
        }

        public void setExt(Object ext) {
            this.ext = ext;
        }
    }
}

