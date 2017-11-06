package com.madhouse.media.fengxing;

import java.util.List;

/**
 * Created by wujunfeng on 2017-11-06.
 */
public class FXBidResponse {
    private String id;
    private String bidid;
    private SeatBid seatBid;

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

    public SeatBid getSeatBid() {
        return seatBid;
    }

    public void setSeatBid(SeatBid seatBid) {
        this.seatBid = seatBid;
    }

    public static class SeatBid {
        private List<Bid> bid;

        public List<Bid> getBid() {
            return bid;
        }

        public void setBid(List<Bid> bid) {
            this.bid = bid;
        }

        public static class Bid {
            private String id;
            private String impid;
            private String nurl;
            private String adm;
            private Float price;
            private String crid;

            public static class Ext {
                private String lpg;
                private List<PM> pm;
                private List<String> cm;
                private List<CM_Ext> cm_ext;
                private String title;
                private String description;

                public String getLpg() {
                    return lpg;
                }

                public void setLpg(String lpg) {
                    this.lpg = lpg;
                }

                public List<PM> getPm() {
                    return pm;
                }

                public void setPm(List<PM> pm) {
                    this.pm = pm;
                }

                public List<String> getCm() {
                    return cm;
                }

                public void setCm(List<String> cm) {
                    this.cm = cm;
                }

                public List<CM_Ext> getCm_ext() {
                    return cm_ext;
                }

                public void setCm_ext(List<CM_Ext> cm_ext) {
                    this.cm_ext = cm_ext;
                }

                public String getTitle() {
                    return title;
                }

                public void setTitle(String title) {
                    this.title = title;
                }

                public String getDescription() {
                    return description;
                }

                public void setDescription(String description) {
                    this.description = description;
                }

                public static class PM {
                    private Integer point;
                    private String url;
                    private String provider;

                    public Integer getPoint() {
                        return point;
                    }

                    public void setPoint(Integer point) {
                        this.point = point;
                    }

                    public String getUrl() {
                        return url;
                    }

                    public void setUrl(String url) {
                        this.url = url;
                    }

                    public String getProvider() {
                        return provider;
                    }

                    public void setProvider(String provider) {
                        this.provider = provider;
                    }
                }

                public static class CM_Ext {
                    private String provider;

                    public String getProvider() {
                        return provider;
                    }

                    public void setProvider(String provider) {
                        this.provider = provider;
                    }
                }
            }


        }
    }
}
