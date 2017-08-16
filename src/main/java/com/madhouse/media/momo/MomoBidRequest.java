package com.madhouse.media.momo;

import java.util.List;

public class MomoBidRequest {

    /**
     * 必须	bid request的唯一id
     */
    private String id;
    /**
     * api版本号
     */
    private String version;
    /**
     * 必须	Impression对象数组，但仅包含一个Impression对象，描述广告位
     */
    private List<Impression> imp;
    /**
     * 必须	App对象，描述App信息
     */
    private User user;
    /**
     * 必须	Device对象，描述设备信息
     */
    private Device device;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Impression> getImp() {
		return imp;
	}

	public void setImp(List<Impression> imp) {
		this.imp = imp;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    @SuppressWarnings("unused")
    public class User {
        /**
         * 可选	性别 男：MALE  女：FEMALE
         */
        private String gender;
        /**
         * 可选	最小年龄
         */
        private int age_low;
        /**
         * 可选	最大年龄
         */
        private int age_high;
        
		public String getGender() {
			return gender;
		}
		public void setGender(String gender) {
			this.gender = gender;
		}
		public int getAge_low() {
			return age_low;
		}
		public void setAge_low(int age_low) {
			this.age_low = age_low;
		}
		public int getAge_high() {
			return age_high;
		}
		public void setAge_high(int age_high) {
			this.age_high = age_high;
		}

    }

    @SuppressWarnings("unused")
    public class Device {
    	/**
         * 可选	设备操作系统
         */
        private String os;
        /**
         * 可选	设备的IP地址
         */
        private String ip;
        /**
         * 可选       设备号：安卓系统为IMEI，ios为IDFA
         */
        private String did;
        /**
         * 可选       加密后的设备号
         */
        private String didmd5;
        /**
         * 可选	设备联网方式，（WIFI,CELL_UNKNOWN）
         */
        private String connection_type;
        /**
         * 可选       设备浏览器的User-Agent字符串
         */
        private String ua;
       
        private Geo geo;
        
        public class Geo {
        	/**
        	 * 可选  纬度
        	 */
        	private double lat;
        	/**
        	 * 可选  经度
        	 */
        	private double lon;
        	
			public double getLat() {
				return lat;
			}
			public void setLat(double lat) {
				this.lat = lat;
			}
			public double getLon() {
				return lon;
			}
			public void setLon(double lon) {
				this.lon = lon;
			}
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

		public String getConnection_type() {
			return connection_type;
		}

		public void setConnection_type(String connection_type) {
			this.connection_type = connection_type;
		}

		public Geo getGeo() {
			return geo;
		}

		public void setGeo(Geo geo) {
			this.geo = geo;
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

        public String getOs() {
            return os;
        }

        public void setOs(String os) {
            this.os = os;
        }

    }

    public class Impression {
        /**
         * 必须	Impression对象唯一id
         */
        private String id;
        /**
         * 必须      开屏样式类型（SPLASH_IMG,SPLASH_GIF,SPLASH_VIDEO ）
         */
        private String splash_format;
        /**
         * 必须	广告位的宽度（单位：像素）
         */
        private int w;
        /**
         * 必须	广告位的高度（单位：像素）
         */
        private int h;
        /**
         * 必须       开屏订单信息
         */
        private Campaign campaign;

        public String getSplash_format() {
			return splash_format;
		}

		public void setSplash_format(String splash_format) {
			this.splash_format = splash_format;
		}

		public Campaign getCampaign() {
			return campaign;
		}

		public void setCampaign(Campaign campaign) {
			this.campaign = campaign;
		}

		public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getW() {
            return w;
        }

        public void setW(int w) {
            this.w = w;
        }

        public int getH() {
            return h;
        }

        public void setH(int h) {
            this.h = h;
        }
        public class Campaign {
        	/**
        	 * 必须    开屏订单id
        	 */
        	private String campaign_id;
        	/**
        	 * 必须   投放开始时间：格式为YYYYMMDD
        	 */
        	private String campaign_begin_date;
        	/**
        	 * 必须  投放结束时间：格式为YYYYMMDD
        	 */
        	private String campaign_end_date;
        	
			public String getCampaign_id() {
				return campaign_id;
			}
			public void setCampaign_id(String campaign_id) {
				this.campaign_id = campaign_id;
			}
			public String getCampaign_begin_date() {
				return campaign_begin_date;
			}
			public void setCampaign_begin_date(String campaign_begin_date) {
				this.campaign_begin_date = campaign_begin_date;
			}
			public String getCampaign_end_date() {
				return campaign_end_date;
			}
			public void setCampaign_end_date(String campaign_end_date) {
				this.campaign_end_date = campaign_end_date;
			}
        	
        }
        
    }

}
