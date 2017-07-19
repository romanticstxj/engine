package com.madhouse.media.xtrader;

import java.util.List;

/**
 * Created by hamlin on 16-8-1.
 */
public class XtraderResponse {
    private String id;// 请求ID
    private String bidid;// DSP给出的该次竞价的ID
    private List<Seatbid> seatbid;// DSP出价

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBidid() {
        return bidid;
    }

    public void setBidid(String bidid) {
        this.bidid = bidid;
    }

    public List<Seatbid> getSeatbid() {
        return seatbid;
    }

    public void setSeatbid(List<Seatbid> seatbid) {
        this.seatbid = seatbid;
    }

    public class Seatbid {
        private List<Bid> bid;// 针对单次曝光的出价
        private String seat;// 有灵集平台提供给DSP的本次出价的seat ID

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

        public class Bid {
            private String id;// DSP对该次出价分配的ID
            private String impid;// Bid Request中对应的曝光ID
            private float price;// DSP出价，单位是分/千次曝光，即CPM
            private String nurl;// win notice url
            private String adm;// 广告物料URL。如果是动态创意，这个字段存放的是创意的HTML标签，标签中支持三种宏替换，%%CLICK_URL_ESC%%（encode的Exchange的点击监测地址）、%%CLICK_URL_UNESC%%(未encode的Exchange点击监测地址)和%%WINNING_PRICE%%（竞价最终价格）。
            private String crid;// DSP系统中的创意ID，对于后审核的创意(即动态创意)，这个字段可以留作历史查证。
            private List<String> pvm;// 曝光监测URL，可以有多个。注意是json数组(@Deprecated,以后版本会删除这个字段，请使用ext.pm 目前版本两个都支持)
            private String clickm;//点击目标URL，可以包括点击监测串。(@Deprecated,以后版本会删除这个字段，请使用ext.ldp) 目前版本两个都支持
            private String dealid;// Dsp参加的deal的id
            private Ext ext;// 扩展字段

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

            public String getNurl() {
                return nurl;
            }

            public void setNurl(String nurl) {
                this.nurl = nurl;
            }

            public String getAdm() {
                return adm;
            }

            public void setAdm(String adm) {
                this.adm = adm;
            }

            public String getCrid() {
                return crid;
            }

            public void setCrid(String crid) {
                this.crid = crid;
            }

            public List<String> getPvm() {
                return pvm;
            }

            public void setPvm(List<String> pvm) {
                this.pvm = pvm;
            }

            public String getClickm() {
                return clickm;
            }

            public void setClickm(String clickm) {
                this.clickm = clickm;
            }

            public String getDealid() {
                return dealid;
            }

            public void setDealid(String dealid) {
                this.dealid = dealid;
            }

            public Ext getExt() {
                return ext;
            }

            public void setExt(Ext ext) {
                this.ext = ext;
            }

            public class Ext {
                private String ldp;// 点击目标URL。这个字段是新的用来替代clickm的，所以请保证clickm和ldp两个字段只有一个填写。如果两个都填写，我们会优先取ldp。
                private List<String> pm;// 曝光监测URL，可以有多个注意是json数组
                private List<String> cm;// 点击监测URL，可以有多个注意是json数组
                private String type;// 物料的类型，包括png，gif，jpg，swf，flv，c和x。具体参见 Exchange物料类型 注意：c和x类型物料必须指定类型，其他物料可以不用指定这个字段。

                public String getLdp() {
                    return ldp;
                }

                public void setLdp(String ldp) {
                    this.ldp = ldp;
                }

                public List<String> getPm() {
                    return pm;
                }

                public void setPm(List<String> pm) {
                    this.pm = pm;
                }

                public List<String> getCm() {
                    return cm;
                }

                public void setCm(List<String> cm) {
                    this.cm = cm;
                }

                public String getType() {
                    return type;
                }

                public void setType(String type) {
                    this.type = type;
                }
            }
        }
    }


}
