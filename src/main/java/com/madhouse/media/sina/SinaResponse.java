package com.madhouse.media.sina;

import java.util.List;


public class SinaResponse {
    private String id;// 请求ID
    private String bidid;// DSP给出的该次竞价的ID
    private String dealid;//request dealid
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

    public String getDealid() {
		return dealid;
	}

	public void setDealid(String dealid) {
		this.dealid = dealid;
	}

	public List<Seatbid> getSeatbid() {
        return seatbid;
    }

    public void setSeatbid(List<Seatbid> seatbid) {
        this.seatbid = seatbid;
    }

    public class Seatbid {
        private List<Bid> bid;// 针对单次曝光的出价

        public List<Bid> getBid() {
            return bid;
        }

        public void setBid(List<Bid> bid) {
            this.bid = bid;
        }

        public class Bid {
            private String id;// DSP对该次出价分配的ID
            private String impid;// Bid Request中对应的曝光ID
            private float price;// DSP出价，单位是分/千次曝光，即CPM
            private String nurl;// win notice url
            private String adm;// 广告物料(博文样式返回的mid)
            private String crid;// DSP系统中的创意ID
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

            public Ext getExt() {
                return ext;
            }

            public void setExt(Ext ext) {
                this.ext = ext;
            }

            public class Ext {
                private String landingid;// 点击目标URL。上传物料时点击链接可以带上宏${LANDING_ID},wax在做点击跳转时会拿此字段替换宏
                private List<String> pm;// 曝光监测URL，可以有多个注意是json数组
                private List<String> cm;// 点击监测URL，可以有多个注意是json数组
                private List<String> vm;// 视频监测url，可多个。支持才c2s的监测方式发送数据

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

				public String getLandingid() {
					return landingid;
				}

				public void setLandingid(String landingid) {
					this.landingid = landingid;
				}

				public List<String> getVm() {
					return vm;
				}

				public void setVm(List<String> vm) {
					this.vm = vm;
				}

            }
        }
    }

}
