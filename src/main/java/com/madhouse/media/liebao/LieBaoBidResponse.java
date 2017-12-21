package com.madhouse.media.liebao;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

@SuppressWarnings("unused")
public class LieBaoBidResponse {
    private String id;//string optional 对应的bidrequest id
    private List<Seatbid> seatbid;//Object array required Setabid对象，支持一个
    private String bidid;//String recommend Bidder 响应 ID，可用于日志跟踪
    private String cur;//string required 货币标示，ISO­4217标准，支持一种国内：CNY 海外：USD
    private Object ext;//object optional 扩展字段

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Seatbid> getSeatbid() {
        return seatbid;
    }

    public void setSeatbid(List<Seatbid> seatbid) {
        this.seatbid = seatbid;
    }

    public String getBidid() {
        return bidid;
    }

    public void setBidid(String bidid) {
        this.bidid = bidid;
    }

    public String getCur() {
        return cur;
    }

    public void setCur(String cur) {
        this.cur = cur;
    }

    public Object getExt() {
        return ext;
    }

    public void setExt(Object ext) {
        this.ext = ext;
    }

    public static class Seatbid {
        private List<Bid> bid;//Object array required 与impression对应
        private String seat;//string optional Bidder seat name
        private Integer group;//int optional 0—独赢 1­组赢，暂只支持0
        private Object ext;//object optional 扩展字段

        public List<Bid> getBid() {
            return bid;
        }

        public void setBid(List<Bid> bid) {
            this.bid = bid;
        }

        public String getSeat() {
            return seat;
        }

        public void setSeat(String seat) {
            this.seat = seat;
        }

        public Integer getGroup() {
            return group;
        }

        public void setGroup(Integer group) {
            this.group = group;
        }

        public Object getExt() {
            return ext;
        }

        public void setExt(Object ext) {
            this.ext = ext;
        }

        @SuppressWarnings("unused")
        public static class Bid {
            private String id;//string optional Bidder定义的ID，用于日志跟踪
            private String impid;//string optional 对应imp对象中的id
            private float price;//float required 竞价价格，单位：元/千次展现
            private String adid;//string optional 广告ID
            private String bundle;//string required 包名
            private List<String> addomain;//String array optional 广告域名，用于黑名单检查
            private String adm;//string required 广告描述
            private List<String> cat;//String array Optional 创意分类，暂不支持
            private List<Integer> attr;//Int array optional 创意属性，暂不支持
            private String dealid;//string optional 私有交易ID
            private Integer w;//int optional 创意宽
            private Integer h;//int optional 创意高
            private String nurl;//String recommend Win url
            private Object ext;//object optional 扩展字段

            // VAST协议 暂不实现
            @JSONField(serialize = false)
            private AdmNative admNative;

            // 猎豹：雅儿(153783658) 2017-12-11 15:23:06 国内的字段应该是banner  没有iab
            @JSONField(serialize = false)
            private AdmBanner admBanner;
            // 辅助字段
            @JSONField(serialize = false)
            private LieBaoBidRequest bidRequest;

            public LieBaoBidRequest getBidRequest() {
                return bidRequest;
            }

            public void setBidRequest(LieBaoBidRequest bidRequest) {
                this.bidRequest = bidRequest;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getImpid() {
                return impid;
            }

            public void setImpid(String impid) {
                this.impid = impid;
            }

            public float getPrice() {
                return price;
            }

            public void setPrice(float price) {
                this.price = price;
            }

            public String getAdid() {
                return adid;
            }

            public void setAdid(String adid) {
                this.adid = adid;
            }

            public String getBundle() {
                return bundle;
            }

            public void setBundle(String bundle) {
                this.bundle = bundle;
            }

            public List<String> getAddomain() {
                return addomain;
            }

            public void setAddomain(List<String> addomain) {
                this.addomain = addomain;
            }

            // 目前只对接banner 开屏。
            public String getAdm() {
                if (bidRequest.getImp().get(0).getNativeObject() != null) {
                    return JSON.toJSONString(admNative);
                } else if (bidRequest.getImp().get(0).getBanner() != null) {
                    return JSON.toJSONString(admBanner);
                } else if (bidRequest.getImp().get(0).getVideo() != null) {
                    return JSON.toJSONString("admVideo");
                }
                return adm;
            }

            public void setAdm(String adm) {
                this.adm = adm;
            }

            public List<String> getCat() {
                return cat;
            }

            public void setCat(List<String> cat) {
                this.cat = cat;
            }

            public List<Integer> getAttr() {
                return attr;
            }

            public void setAttr(List<Integer> attr) {
                this.attr = attr;
            }

            public String getDealid() {
                return dealid;
            }

            public void setDealid(String dealid) {
                this.dealid = dealid;
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

            public String getNurl() {
                return nurl;
            }

            public void setNurl(String nurl) {
                this.nurl = nurl;
            }

            public Object getExt() {
                return ext;
            }

            public void setExt(Object ext) {
                this.ext = ext;
            }

            public AdmNative getAdmNative() {
                return admNative;
            }

            public void setAdmNative(AdmNative admNative) {
                this.admNative = admNative;
            }

            public AdmBanner getAdmBanner() {
                return admBanner;
            }

            public void setAdmBanner(AdmBanner admBanner) {
                this.admBanner = admBanner;
            }

            public static class AdmBanner {
                private Banner banner;//object required TOP LEVEL

                public Banner getBanner() {
                    return banner;
                }

                public void setBanner(Banner banner) {
                    this.banner = banner;
                }

                public static class Banner {
                    private List<String> imptrackers;//String array required 展现跟踪链接
                    private Link link;//object required 点击跳转
                    private Img img;//object required 广告素材

                    public List<String> getImptrackers() {
                        return imptrackers;
                    }

                    public void setImptrackers(List<String> imptrackers) {
                        this.imptrackers = imptrackers;
                    }

                    public Link getLink() {
                        return link;
                    }

                    public void setLink(Link link) {
                        this.link = link;
                    }

                    public Img getImg() {
                        return img;
                    }

                    public void setImg(Img img) {
                        this.img = img;
                    }

                    public static class Link {
                        private List<String> clicktrackers;//String array optional 点击跟踪链接
                        private String url;//string required 跳转URL

                        public List<String> getClicktrackers() {
                            return clicktrackers;
                        }

                        public void setClicktrackers(List<String> clicktrackers) {
                            this.clicktrackers = clicktrackers;
                        }

                        public String getUrl() {
                            return url;
                        }

                        public void setUrl(String url) {
                            this.url = url;
                        }
                    }

                    public static class Img {
                        private String url;//string required 图片下载地址
                        private Integer w;//int required 图片宽，见附录4.4
                        private Integer h;//int required 图片高，见附录4.4

                        public String getUrl() {
                            return url;
                        }

                        public void setUrl(String url) {
                            this.url = url;
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
                    }
                }
            }

            public static class AdmNative {
                @JSONField(name = "native")
                private ResponseNative responseNative;

                public ResponseNative getResponseNative() {
                    return responseNative;
                }

                public void setResponseNative(ResponseNative responseNative) {
                    this.responseNative = responseNative;
                }

                public static class ResponseNative {
                    private List<String> imptrackers;//String array required 展现跟踪链接
                    private Link link;//object required 点击跳转
                    private List<Assets> assets;//array required 广告素材

                    public List<String> getImptrackers() {
                        return imptrackers;
                    }

                    public void setImptrackers(List<String> imptrackers) {
                        this.imptrackers = imptrackers;
                    }

                    public Link getLink() {
                        return link;
                    }

                    public void setLink(Link link) {
                        this.link = link;
                    }

                    public List<Assets> getAssets() {
                        return assets;
                    }

                    public void setAssets(List<Assets> assets) {
                        this.assets = assets;
                    }

                    public static class Link {
                        private List<String> clicktrackers;//String array optional 点击跟踪链接
                        private String url;//string required 跳转URL
                        private String landurl;//string optional h5落地页url
                        private String fallback;//string optional 用于deeplink广告，如果存在该字段，且不为空，则会认为广告需要做deeplink跳转，且deeplink广告必须提供真实有效包名，否则会被过滤

                        public List<String> getClicktrackers() {
                            return clicktrackers;
                        }

                        public void setClicktrackers(List<String> clicktrackers) {
                            this.clicktrackers = clicktrackers;
                        }

                        public String getUrl() {
                            return url;
                        }

                        public void setUrl(String url) {
                            this.url = url;
                        }

                        public String getLandurl() {
                            return landurl;
                        }

                        public void setLandurl(String landurl) {
                            this.landurl = landurl;
                        }

                        public String getFallback() {
                            return fallback;
                        }

                        public void setFallback(String fallback) {
                            this.fallback = fallback;
                        }
                    }

                    public static class Assets {
                        private Integer id;//Int required 请求时的assets id
                        private Title title;//Json Object optional 标题
                        private Data data;//Json Object optional 描述
                        private Link link;//Json Object optional 跳转地址
                        private Img img;//Json Object optional 图片地址

                        public Integer getId() {
                            return id;
                        }

                        public void setId(Integer id) {
                            this.id = id;
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

                        public Link getLink() {
                            return link;
                        }

                        public void setLink(Link link) {
                            this.link = link;
                        }

                        public Img getImg() {
                            return img;
                        }

                        public void setImg(Img img) {
                            this.img = img;
                        }

                        public static class Title {
                            private String text;//string required 标题描述

                            public String getText() {
                                return text;
                            }

                            public void setText(String text) {
                                this.text = text;
                            }
                        }

                        public static class Data {
                            private String label;//string required 数据类型，目前只有描述”desc” 见附录有4.6
                            private String value;// string required 描述

                            public String getLabel() {
                                return label;
                            }

                            public void setLabel(String label) {
                                this.label = label;
                            }

                            public String getValue() {
                                return value;
                            }

                            public void setValue(String value) {
                                this.value = value;
                            }
                        }

                        public static class Link {
                            private String url;//string optional 跳转URL，存在替换外层url
                            private String landurl;//string optional h5落地页url，存在替换外层landurl
                            private List<String> clicktrackers;//string array optional 点击跟踪，存在替换外层clicktrackers
                            private String fallback;//string optional 用于deeplink广告，如果存在该字段，且不为空，则会认为广告需要做deeplink跳转，且deeplink广告必须提供真实有效包名，否则会被过滤

                            public String getUrl() {
                                return url;
                            }

                            public void setUrl(String url) {
                                this.url = url;
                            }

                            public String getLandurl() {
                                return landurl;
                            }

                            public void setLandurl(String landurl) {
                                this.landurl = landurl;
                            }

                            public List<String> getClicktrackers() {
                                return clicktrackers;
                            }

                            public void setClicktrackers(List<String> clicktrackers) {
                                this.clicktrackers = clicktrackers;
                            }

                            public String getFallback() {
                                return fallback;
                            }

                            public void setFallback(String fallback) {
                                this.fallback = fallback;
                            }
                        }

                        public static class Img {
                            private String url;//string required 图片下载地址
                            private Integer w;//int required 图片宽，见附录4.4
                            private Integer h;//int required 图片高，见附录4.4

                            public String getUrl() {
                                return url;
                            }

                            public void setUrl(String url) {
                                this.url = url;
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
                        }
                    }
                }
            }
        }
    }
}
