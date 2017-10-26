package com.madhouse.media.mojiweather;

public class MojiWeatherResponse {
    private int code;

    private data data;

    @SuppressWarnings("unused")
    public static class data{

        private String adid;

        private String sessionid;

        private Integer price;

        private Integer chargingtype;

        private String imgurl;

        private String iconurl;

        private String clickurl;

        private String clktrack;

        private String imptrack;

        private String urlSeparator;

        private String adwidth;

        private String adheight;

        private String adtitle;

        private String adtext;

        private Integer adtype;

        private Integer adstyle;

        private Integer feed_type;

        private String show_date;
        /**
         * 1 图片 2 视频 
         */
        private Integer type;
        /**
         * 视频封面图片地址 （710*396 视频封面） 
         */
        private String vedioimg;
        /**
         * 视频广告的地址 
         */
        private String vediourl;
        /**
         * 播放时长 单位秒 
         */
        private Integer vedioPlaytime;

        public Integer getVedioPlaytime() {
            return vedioPlaytime;
        }

        public void setVedioPlaytime(Integer vedioPlaytime) {
            this.vedioPlaytime = vedioPlaytime;
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

        public Integer getPrice() {
            return price;
        }

        public void setPrice(Integer price) {
            this.price = price;
        }

        public Integer getChargingtype() {
            return chargingtype;
        }

        public void setChargingtype(Integer chargingtype) {
            this.chargingtype = chargingtype;
        }

        public String getImgurl() {
            return imgurl;
        }
        public void setImgurl(String imgurl) {
            this.imgurl = imgurl;
        }
        public String getIconurl() {
            return iconurl;
        }
        public void setIconurl(String iconurl) {
            this.iconurl = iconurl;
        }
        public String getClickurl() {
            return clickurl;
        }
        public void setClickurl(String clickurl) {
            this.clickurl = clickurl;
        }
        public String getClktrack() {
            return clktrack;
        }
        public void setClktrack(String clktrack) {
            this.clktrack = clktrack;
        }
        public String getImptrack() {
            return imptrack;
        }
        public void setImptrack(String imptrack) {
            this.imptrack = imptrack;
        }
        public String getUrlSeparator() {
            return urlSeparator;
        }
        public void setUrlSeparator(String urlSeparator) {
            this.urlSeparator = urlSeparator;
        }
        public String getAdwidth() {
            return adwidth;
        }
        public void setAdwidth(String adwidth) {
            this.adwidth = adwidth;
        }
        public String getAdheight() {
            return adheight;
        }
        public void setAdheight(String adheight) {
            this.adheight = adheight;
        }
        public String getAdtitle() {
            return adtitle;
        }
        public void setAdtitle(String adtitle) {
            this.adtitle = adtitle;
        }
        public String getAdtext() {
            return adtext;
        }
        public void setAdtext(String adtext) {
            this.adtext = adtext;
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

        public Integer getFeed_type() {
            return feed_type;
        }

        public void setFeed_type(Integer feed_type) {
            this.feed_type = feed_type;
        }

        public String getShow_date() {
            return show_date;
        }
        public void setShow_date(String show_date) {
            this.show_date = show_date;
        }

        public Integer getType() {
            return type;
        }

        public void setType(Integer type) {
            this.type = type;
        }

        public String getVedioimg() {
            return vedioimg;
        }
        public void setVedioimg(String vedioimg) {
            this.vedioimg = vedioimg;
        }
        public String getVediourl() {
            return vediourl;
        }
        public void setVediourl(String vediourl) {
            this.vediourl = vediourl;
        }
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public data getData() {
        return data;
    }

    public void setData(data data) {
        this.data = data;
    }
    
}
