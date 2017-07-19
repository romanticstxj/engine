package com.madhouse.media.xtrader;

import java.util.List;

/**
 * Created by hamlin on 16-8-1.
 */
public class XtraderBidRequest {
    private String id;// 请求ID
    private List<Imp> imp;// 曝光对象，一次request可以包含多个imp
    private Site site;// 媒体站点对象
    private Device device;//device	object	设备对象
    private User user;//	object	用户对象
    private App app;// 应用对象（内含移动应用的信息）
    private List<String> wseat;//array of string	DSP允许竞价的seat白名单

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

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
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

    public App getApp() {
        return app;
    }

    public void setApp(App app) {
        this.app = app;
    }

    public List<String> getWseat() {
        return wseat;
    }

    public void setWseat(List<String> wseat) {
        this.wseat = wseat;
    }

    public class Imp {
        private String id;// 曝光 ID
        private String tagid;// 广告位ID
        private float bidfloor;// 底价,单位是分/千次曝光,即CPM
        private Banner banner;// banner类型的广告位
        private Video video;// video类型的广告位
        private Nativead nativead;// 原生类型的广告位，NativeAd定义详见文档 AdExchange RTB原生广告接口文档
        private Pmp pmp;// pmp字段，deal相关的参数，参看pmp字段的说明
        private Ext ext;// 扩展字段,参看imp.ext字段的说明

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

        public float getBidfloor() {
            return bidfloor;
        }

        public void setBidfloor(float bidfloor) {
            this.bidfloor = bidfloor;
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

        public Nativead getNativead() {
            return nativead;
        }

        public void setNativead(Nativead nativead) {
            this.nativead = nativead;
        }

        public Pmp getPmp() {
            return pmp;
        }

        public void setPmp(Pmp pmp) {
            this.pmp = pmp;
        }

        public Ext getExt() {
            return ext;
        }

        public void setExt(Ext ext) {
            this.ext = ext;
        }


        public class Nativead {

        }

        public class Banner {
            private Integer w;// 广告位宽度
            private Integer h;// 广告位高度
            private Integer pos;// 广告位位置,兼容openRTB2.2中6.5表格关于广告位置的规定,见附录C  展示位置
            private List<String> mimes;// 允许投放的物料类型["image/png","application/x-shockwave-flash","text/html"]

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

            public Integer getPos() {
                return pos;
            }

            public void setPos(Integer pos) {
                this.pos = pos;
            }

            public List<String> getMimes() {
                return mimes;
            }

            public void setMimes(List<String> mimes) {
                this.mimes = mimes;
            }
        }


        public class Ext {
            private Integer showtype;// 展示类型,灵集对广告展示形式的一种分类,具体见附录B 展示类型
            private Integer has_winnotice;// 该字段表示客户端是否支持发送winnotice(nurl字段)以及支持曝光的条数：1表示会发送winnotice并且支持多条曝光,0表示不会发送winnotice并且只支持一条曝光.
            /*
                integer	该字段表示是否支持异步点击监测(cm),以及dsp点击监测是否需要302跳转到落地页。
                if has_winnotice=0,都不支持异步的点击监测(cm),只支持ldp字段。
                has_clickthrough=1表示ldp字段dsp返回点击监测url必须302 redirect到广告落地页
                has_clickthrough=0表示ldp字段只返回dsp点击监测url，不用302 redirect到落地页
             */
            private Integer has_clickthrough;
            private Integer action_type;// 媒体资源位置支持的交互类型：1.支持网页打开类+下载类广告 2.只支持打开类广告 3.只支持下载类广告

            public Integer getShowtype() {
                return showtype;
            }

            public void setShowtype(Integer showtype) {
                this.showtype = showtype;
            }

            public Integer getHas_winnotice() {
                return has_winnotice;
            }

            public void setHas_winnotice(Integer has_winnotice) {
                this.has_winnotice = has_winnotice;
            }

            public Integer getHas_clickthrough() {
                return has_clickthrough;
            }

            public void setHas_clickthrough(Integer has_clickthrough) {
                this.has_clickthrough = has_clickthrough;
            }

            public Integer getAction_type() {
                return action_type;
            }

            public void setAction_type(Integer action_type) {
                this.action_type = action_type;
            }
        }
    }

    public class Site {
        private String name;// 媒体网站名称
        private String page;// 当前页面URL
        private String ref;// referrer URL
        private Content content;// 视频的内容相关信息。只有视频贴片类型的广告位才会有这个字段，参见site.content对象描述
        private List<String> cat;// 广告位内容分类，兼容IAB分类，符合openRTB 2.2表格6.1的分类方法。对应的编号和中英文对照表见附录 Content Category

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPage() {
            return page;
        }

        public void setPage(String page) {
            this.page = page;
        }

        public String getRef() {
            return ref;
        }

        public void setRef(String ref) {
            this.ref = ref;
        }

        public Content getContent() {
            return content;
        }

        public void setContent(Content content) {
            this.content = content;
        }

        public List<String> getCat() {
            return cat;
        }

        public void setCat(List<String> cat) {
            this.cat = cat;
        }

        public class Content {
            private String title;// 视频标题名称
            private String keywords;// 视频标签关键字，如果是多个关键字，则使用英文逗号分隔
            private Ext ext;// 参见site.content.ext描述

            public class Ext {
                private String channel;// 视频的频道ID，例如"1"。注：网站的频道字典可以线下获取，将来我们会增加这个字典的映射获取接口
                private String cs;// 二级频道ID
                private Integer copyright;// 版权信息 1---有版权 2---版权信息未知
                private Integer quality;// 流量质量 1---流量质量保障 2---流量质量未知

                public String getChannel() {
                    return channel;
                }

                public void setChannel(String channel) {
                    this.channel = channel;
                }

                public String getCs() {
                    return cs;
                }

                public void setCs(String cs) {
                    this.cs = cs;
                }

                public Integer getCopyright() {
                    return copyright;
                }

                public void setCopyright(Integer copyright) {
                    this.copyright = copyright;
                }

                public Integer getQuality() {
                    return quality;
                }

                public void setQuality(Integer quality) {
                    this.quality = quality;
                }
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public String getKeywords() {
                return keywords;
            }

            public void setKeywords(String keywords) {
                this.keywords = keywords;
            }

            public Ext getExt() {
                return ext;
            }

            public void setExt(Ext ext) {
                this.ext = ext;
            }
            
        }
    }

    public class Device {
        private String ua;// user agent(Browser user agent string)
        private String ip;// IP
        private Geo geo;// 设备的当前地理位置信息,参见geo对象详细字段描述。
        private String didmd5;// 使用MD5哈希的Device ID(对应Android系统MMA字段的IMEI值,iOS系统api限制获取不到该值)
        private String dpidsha1;// 使用SHA1哈希的Device ID值(对应Android系统MMA字段的IMEI值,iOS系统api限制获取不到该值)
        private String dpidmd5;// 使用MD5哈希的平台相关ID,不同的系统会传不同的值，具体对应的移动端MMA字段： Android系统会传--Android ID，iOS系统会传--openudid，Windows Phone系统会传--DUID
        private String make;// 设备生产商，如"Apple"
        private String model;// 设备型号,如"iPhone"
        private String os;// 操作系统 "0-Android"/"1-iOS"/"2-WP"("Windows Phone")/"3-Others" (忽略大小写)
        private String osv;// 操作系统版本号，如"4.1"
        private String carrier;// 运营商的ID，参见OpenRTB协议及http://en.wikipedia.org/wiki/Mobile_Network_Code。
        private String language;// 目前使用的国家—语言,如"zh_CN"
        private Integer js;// 是否启用Javascript，1—启用（默认值），0—未启用
        private Integer connectiontype;// 网络连接类型，和OpenRTB一致：0—未知，1—Ethernet，2—wifi，3—蜂窝网络，未知代，4—蜂窝网络，2G，5—蜂窝网络，3G，6—蜂窝网络，4G。
        private Integer devicetype;// 设备类型，和0—手机，1—平板，2—PC，3—互联网电视。
        private Ext ext;// 扩展信息，参见device.ext详细字解释。

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

        public Geo getGeo() {
            return geo;
        }

        public void setGeo(Geo geo) {
            this.geo = geo;
        }

        public String getDidmd5() {
            return didmd5;
        }

        public void setDidmd5(String didmd5) {
            this.didmd5 = didmd5;
        }

        public String getDpidsha1() {
            return dpidsha1;
        }

        public void setDpidsha1(String dpidsha1) {
            this.dpidsha1 = dpidsha1;
        }

        public String getDpidmd5() {
            return dpidmd5;
        }

        public void setDpidmd5(String dpidmd5) {
            this.dpidmd5 = dpidmd5;
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

        public String getCarrier() {
            return carrier;
        }

        public void setCarrier(String carrier) {
            this.carrier = carrier;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public Integer getJs() {
            return js;
        }

        public void setJs(Integer js) {
            this.js = js;
        }

        public Integer getConnectiontype() {
            return connectiontype;
        }

        public void setConnectiontype(Integer connectiontype) {
            this.connectiontype = connectiontype;
        }

        public Integer getDevicetype() {
            return devicetype;
        }

        public void setDevicetype(Integer devicetype) {
            this.devicetype = devicetype;
        }

        public Ext getExt() {
            return ext;
        }

        public void setExt(Ext ext) {
            this.ext = ext;
        }


        public class Ext {
            private String idfa;// 对应的移动端MMA字段：iOS的IDFA字段(iOS系统 osv>=6时会传该字段，传的是原始值未经过md5 sum)，如："1E2DFA89-496A-47FD-9941-DF1FC4E6484A"
            private String idfamd5;// 对应的移动端MMA字段：iOS的IDFA字段取MD5值，如："40C7084B4845EEBCE9D07B8A18A055FC"
            private String mac;// 去除分隔符”:”(保持大写)的MAC地址取MD5摘要,eg:3D8A278F33E4F97181DF1EAEFE500D05
            private String macmd5;// 保留分隔符”:”(保持大写)的MAC地址取MD5摘要,eg:DC7D41E352D13D60765414D53F40BC25
            private String macsha1;// MAC地址取sha1摘要
            private String ssid;// WIFI的
            private Integer w;// 设备的屏幕宽度，以像素为单位
            private Integer h;// 设备的屏幕高度，以像素为单位
            private Integer brk;// 设备是否越狱，1—已启用（默认），0—未启用。
            private Integer ts;// 发送请求时的本地UNIX时间戳（秒数，10进制）
            private Integer interstitial;// 是否使用全屏/互动方式来展现广告。1—是，0—否（默认值）。

            public String getIdfa() {
                return idfa;
            }

            public void setIdfa(String idfa) {
                this.idfa = idfa;
            }

            public String getIdfamd5() {
                return idfamd5;
            }

            public void setIdfamd5(String idfamd5) {
                this.idfamd5 = idfamd5;
            }

            public String getMac() {
                return mac;
            }

            public void setMac(String mac) {
                this.mac = mac;
            }

            public String getMacmd5() {
                return macmd5;
            }

            public void setMacmd5(String macmd5) {
                this.macmd5 = macmd5;
            }

            public String getMacsha1() {
                return macsha1;
            }

            public void setMacsha1(String macsha1) {
                this.macsha1 = macsha1;
            }

            public String getSsid() {
                return ssid;
            }

            public void setSsid(String ssid) {
                this.ssid = ssid;
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

            public Integer getBrk() {
                return brk;
            }

            public void setBrk(Integer brk) {
                this.brk = brk;
            }

            public Integer getTs() {
                return ts;
            }

            public void setTs(Integer ts) {
                this.ts = ts;
            }

            public Integer getInterstitial() {
                return interstitial;
            }

            public void setInterstitial(Integer interstitial) {
                this.interstitial = interstitial;
            }
        }
    }

    public class Geo {
        private float lat;// 纬度（-90~90）
        private float lon;// 经度（-180~180）
        private Ext ext;// 扩展属性，参见geo.ext详细字段解释。

        public float getLat() {
            return lat;
        }

        public void setLat(float lat) {
            this.lat = lat;
        }

        public float getLon() {
            return lon;
        }

        public void setLon(float lon) {
            this.lon = lon;
        }

        public Ext getExt() {
            return ext;
        }

        public void setExt(Ext ext) {
            this.ext = ext;
        }

        public class Ext {
            private Integer accuracy;// GPS的精确度，单位为米。如：100表示精确度为100米。

            public Integer getAccuracy() {
                return accuracy;
            }

            public void setAccuracy(Integer accuracy) {
                this.accuracy = accuracy;
            }
        }

    }

    public class User {
        private String id;// Xtrader用户ID(即灵集域的cookie id)
        private Ext ext;// 扩展属性，包括DMP信息，参见 user.ext说明

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Ext getExt() {
            return ext;
        }

        public void setExt(Ext ext) {
            this.ext = ext;
        }

        public class Ext {
            private List<String> models;// 灵集的DMP标签, 形式是字符串标签组成的数组，例如["10000", "10008"]，具体的标签说明参见 灵集DMP标签说明

            public List<String> getModels() {
                return models;
            }

            public void setModels(List<String> models) {
                this.models = models;
            }
        }
    }

    public class App {
        private String name;// App的名称
        private String bundle;// APP应用的包名称或bundleID
        private Site.Content content;// 视频的内容相关信息。只有视频贴片类型的广告位才会有这个字段，同site.content对象
        private List<String> cat;// 广告位内容分类，兼容IAB分类，符合openRTB 2.2表格6.1的分类方法。对应的编号和中英文对照表见附录 Content Category
        private Ext ext;// 扩展信息,参见app.ext详细字段解释。

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


        public Site.Content getContent() {
            return content;
        }

        public void setContent(Site.Content content) {
            this.content = content;
        }

        public List<String> getCat() {
            return cat;
        }

        public void setCat(List<String> cat) {
            this.cat = cat;
        }

        public Ext getExt() {
            return ext;
        }

        public void setExt(Ext ext) {
            this.ext = ext;
        }

        public class Ext {
            private String sdk;// 投放SDK的版本，例如“91_v1”
            private Integer market;// 应用商店，1—iOS Appstore，2—Google Play，3—91Market。

            public String getSdk() {
                return sdk;
            }

            public void setSdk(String sdk) {
                this.sdk = sdk;
            }

            public Integer getMarket() {
                return market;
            }

            public void setMarket(Integer market) {
                this.market = market;
            }
        }
    }

    public class Video {
        private List<String> mimes;// 支持播放的视频格式，目前支持： video/x-flv，application/x-shockwave-flash
        private Integer linearity;// 广告展现样式，1为in-stream, 2为overlay。"In-stream" or "linear" video refers to pre-roll, post-roll, or mid-roll video ads where the user is forced to watch ad in order to see the video content. “Overlay” or “non-linear” refer to ads that are shown on top of the video content.
        private Integer minduration;// 视频广告最短播放时长，单位是秒
        private Integer maxduration;// 视频广告最长播放时长，单位是秒
        private Integer w;// 广告位宽度
        private Integer h;// 广告位高度
        private Integer pos;// 广告位位置,兼容openRTB2.2中6.5表格关于广告位置的规定,见附录C 展示位置

        public List<String> getMimes() {
            return mimes;
        }

        public void setMimes(List<String> mimes) {
            this.mimes = mimes;
        }

        public Integer getLinearity() {
            return linearity;
        }

        public void setLinearity(Integer linearity) {
            this.linearity = linearity;
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

        public Integer getPos() {
            return pos;
        }

        public void setPos(Integer pos) {
            this.pos = pos;
        }
    }

    public class Pmp {
        private List<Deals> deals;// 符合exchange系统的deal条件的deals对象数组，见deals对象说明
        private Integer private_auction;//1表示只接受deal竞价；0表示在deal竞价失败的时候，可以接受公开竞价

        public class Deals {
            private String id;// 符合条件的deal的id
            private Integer bidfloor;// Exchange系统中deal的价格
            private List<String> wseat;// 可参与deal的广告主名称列表
            private Integer at;// 竞价的方式，目前都是1，即第一竞价法。最高的deal获得竞价成功。价格为最高的deal的价格。

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public Integer getBidfloor() {
                return bidfloor;
            }

            public void setBidfloor(Integer bidfloor) {
                this.bidfloor = bidfloor;
            }

            public List<String> getWseat() {
                return wseat;
            }

            public void setWseat(List<String> wseat) {
                this.wseat = wseat;
            }

            public Integer getAt() {
                return at;
            }

            public void setAt(Integer at) {
                this.at = at;
            }
        }

        public List<Deals> getDeals() {
            return deals;
        }

        public void setDeals(List<Deals> deals) {
            this.deals = deals;
        }

        public Integer getPrivate_auction() {
            return private_auction;
        }

        public void setPrivate_auction(Integer private_auction) {
            this.private_auction = private_auction;
        }
        
    }
}
