package com.madhouse.media.fengxing;

import java.util.List;

/**
 * Created by wujunfeng on 2017-11-06.
 */
public class FXBidRequest {
    private String id;
    private String version;
    private Integer istest;
    private Integer isping;
    private List<Impression> imp;
    private Site site;
    private App app;
    private Device device;
    private User user;
    private Ext ext;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Integer getIstest() {
        return istest;
    }

    public void setIstest(Integer istest) {
        this.istest = istest;
    }

    public Integer getIsping() {
        return isping;
    }

    public void setIsping(Integer isping) {
        this.isping = isping;
    }

    public List<Impression> getImp() {
        return imp;
    }

    public void setImp(List<Impression> imp) {
        this.imp = imp;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
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

    public Ext getExt() {
        return ext;
    }

    public void setExt(Ext ext) {
        this.ext = ext;
    }

    public static class Impression {
        private String id;
        private String tagid;
        private Float bidfloor;
        private Banner banner;
        private Video video;
        private PMP pmp;

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

        public PMP getPmp() {
            return pmp;
        }

        public void setPmp(PMP pmp) {
            this.pmp = pmp;
        }

        public static class Banner {
            private Integer w;
            private Integer h;
            private Integer isratio;
            private Integer wmax;
            private Integer hmax;
            private Integer wmin;
            private Integer hmin;
            private List<String> mimes;

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

            public Integer getIsratio() {
                return isratio;
            }

            public void setIsratio(Integer isratio) {
                this.isratio = isratio;
            }

            public Integer getWmax() {
                return wmax;
            }

            public void setWmax(Integer wmax) {
                this.wmax = wmax;
            }

            public Integer getHmax() {
                return hmax;
            }

            public void setHmax(Integer hmax) {
                this.hmax = hmax;
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
        }

        public static class Video {
            private Integer w;
            private Integer h;
            private Integer isratio;
            private Integer wmax;
            private Integer hmax;
            private Integer wmin;
            private Integer hmin;
            private List<String> mimes;
            private Integer minduration;
            private Integer maxduration;
            private Integer linearity;

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

            public Integer getIsratio() {
                return isratio;
            }

            public void setIsratio(Integer isratio) {
                this.isratio = isratio;
            }

            public Integer getWmax() {
                return wmax;
            }

            public void setWmax(Integer wmax) {
                this.wmax = wmax;
            }

            public Integer getHmax() {
                return hmax;
            }

            public void setHmax(Integer hmax) {
                this.hmax = hmax;
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

            public Integer getLinearity() {
                return linearity;
            }

            public void setLinearity(Integer linearity) {
                this.linearity = linearity;
            }
        }

        public static class PMP {
            private List<Deal> deals;

            public List<Deal> getDeals() {
                return deals;
            }

            public void setDeals(List<Deal> deals) {
                this.deals = deals;
            }

            public static class Deal {
                private String id;

                public String getId() {
                    return id;
                }

                public void setId(String id) {
                    this.id = id;
                }
            }
        }
    }

    public static class Site {
        private String name;
        private String domain;
        private List<String> cat;
        private String page;
        private String ref;
        private Content content;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDomain() {
            return domain;
        }

        public void setDomain(String domain) {
            this.domain = domain;
        }

        public List<String> getCat() {
            return cat;
        }

        public void setCat(List<String> cat) {
            this.cat = cat;
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

        public static class Content {
            private String channel;

            public String getChannel() {
                return channel;
            }

            public void setChannel(String channel) {
                this.channel = channel;
            }
        }
    }

    public static class Device {
        private String ua;
        private String ip;
        private String did;
        private String didmd5;
        private String dpid;
        private String dpidmd5;
        private String make;
        private String model;
        private String os;
        private String osv;
        private String carrier;
        private String language;
        private Integer devicetype;
        private Integer connectiontype;
        private Geo geo;
        private Ext ext;

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

        public String getDid() {
            return did;
        }

        public void setDid(String did) {
            this.did = did;
        }

        public String getDidmd5() {
            return didmd5;
        }

        public void setDidmd5(String didmd5) {
            this.didmd5 = didmd5;
        }

        public String getDpid() {
            return dpid;
        }

        public void setDpid(String dpid) {
            this.dpid = dpid;
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

        public Integer getDevicetype() {
            return devicetype;
        }

        public void setDevicetype(Integer devicetype) {
            this.devicetype = devicetype;
        }

        public Integer getConnectiontype() {
            return connectiontype;
        }

        public void setConnectiontype(Integer connectiontype) {
            this.connectiontype = connectiontype;
        }

        public Geo getGeo() {
            return geo;
        }

        public void setGeo(Geo geo) {
            this.geo = geo;
        }

        public Ext getExt() {
            return ext;
        }

        public void setExt(Ext ext) {
            this.ext = ext;
        }

        public static class Geo {
            private Float lat;
            private Float lon;

            public Float getLat() {
                return lat;
            }

            public void setLat(Float lat) {
                this.lat = lat;
            }

            public Float getLon() {
                return lon;
            }

            public void setLon(Float lon) {
                this.lon = lon;
            }
        }

        public static class Ext {
            private String idfa;
            private String mac;
            private String macmd5;
            private String ssid;
            private Integer w;
            private Integer h;
            private Integer brk;
            private Integer interative;

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

            public String getMacmd5() {
                return macmd5;
            }

            public void setMacmd5(String macmd5) {
                this.macmd5 = macmd5;
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

            public Integer getInterative() {
                return interative;
            }

            public void setInterative(Integer interative) {
                this.interative = interative;
            }
        }
    }

    public static class User {
        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    public static class App {
        private String name;
        private String bundle;
        private List<String> cat;
        private Content content;

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

        public List<String> getCat() {
            return cat;
        }

        public void setCat(List<String> cat) {
            this.cat = cat;
        }

        public Content getContent() {
            return content;
        }

        public void setContent(Content content) {
            this.content = content;
        }

        public static class Content {
            private String id;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public static class Ext {
                private String channel;

                public String getChannel() {
                    return channel;
                }

                public void setChannel(String channel) {
                    this.channel = channel;
                }
            }
        }
    }

    public static class Ext {
        private String serialid;

        public String getSerialid() {
            return serialid;
        }

        public void setSerialid(String serialid) {
            this.serialid = serialid;
        }
    }
}
