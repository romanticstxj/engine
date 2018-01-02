package com.madhouse.media.autohome;

import java.util.List;

public class AutoHomeBidRequest{
	
	private String id;//竞价ID
	
	private String version;//竞价协议版本

	private List<AdSlot> adSlot;//广告位信眉
	
	private List<Site> site;//站点信息
	
	private Mobile mobile;//设备信息
	
	private boolean is_test;//是否为测试请求 (true 是 false 否）如果为测试请求广告不会展现也不会计费

	private User user;//用户信息
	
	public class AdSlot{
		
		private String id;//曝光id (标识每个广告位每一次曝光）
		private String slotid;//广告位ID/
		private Integer min_cpm_price;//广告位底价（分/CPM )
		private String deal_type;//交易类型（PDB PD PA RTB )
		private int slot_visibility;//广告位相对位置0 :无数据； 1-5 :第1-5屏；6 :第6屏及以外
		private List<Integer> excluded_ad_category;//禁止的行业类目
		private Banner banner;//Banner类型广告位
		private Video video;//Video类型广告位
		
		public class Banner{
			private Integer width;//广告位宽
			private Integer height;//广告位高
			private List<Integer> view_type;//广告位展现形式
			private List<Integer> templateId;//广告位支持的模板
			
			public Integer getWidth() {
				return width;
			}
			public void setWidth(Integer width) {
				this.width = width;
			}
			public Integer getHeight() {
				return height;
			}
			public void setHeight(Integer height) {
				this.height = height;
			}
			public List<Integer> getView_type() {
				return view_type;
			}
			public void setView_type(List<Integer> view_type) {
				this.view_type = view_type;
			}
			public List<Integer> getTemplateId() {
				return templateId;
			}
			public void setTemplateId(List<Integer> templateId) {
				this.templateId = templateId;
			}
			
		}
		
		//预留字段
		public class Video{
			
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getSlotid() {
			return slotid;
		}

		public void setSlotid(String slotid) {
			this.slotid = slotid;
		}

		public Integer getMin_cpm_price() {
			return min_cpm_price;
		}

		public void setMin_cpm_price(Integer min_cpm_price) {
			this.min_cpm_price = min_cpm_price;
		}

		public String getDeal_type() {
			return deal_type;
		}

		public void setDeal_type(String deal_type) {
			this.deal_type = deal_type;
		}

		public int getSlot_visibility() {
			return slot_visibility;
		}

		public void setSlot_visibility(int slot_visibility) {
			this.slot_visibility = slot_visibility;
		}

		public List<Integer> getExcluded_ad_category() {
			return excluded_ad_category;
		}

		public void setExcluded_ad_category(List<Integer> excluded_ad_category) {
			this.excluded_ad_category = excluded_ad_category;
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
		
	}
	public class Site{
		private String url;//当前页面url
		private String ref;//Referrer url
		
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
		public String getRef() {
			return ref;
		}
		public void setRef(String ref) {
			this.ref = ref;
		}
		
		
	}
	public class Mobile{
		private boolean is_app;//标识该次广告请求是否来自app!
		private String pkgname;//包名
		private Device device;//设备信息
		
		public class Device{
			
			private String devicebrand;//设备品牌
			private String devicemodel;//设备型号
			private int pm;//l-ios,2-android,3-other
			private String os_version;//操作系统版本
			private int conn;//联网方式0-未知1-wifi 2:-2g3:-3g 4:-4g
			private int networkid;//0-代表获取不到运营商7012-中国移动70121-中国电信70123-中国联通
			private int lng;//经度e 1000,000
			private int lat;//维度 e 1000,000
			private int screen_width;//屏幕宽，取设备物理像素
			private int screen_hight;//屏幕高。取设备物理像素，高度始终大于宽度
			private int screen_orientation;//设备横竖屏。可能取值：0, iphone 4s屏幕正对自己，home键靠下。90,顺时针旋转90度。 180 270l
			private float screen_density;//屏幕密度，一个逻辑像素等于几个实际像素；（浮点数
			private String deviceid;//设备id ,ios :优先idfa如果获取不到就 用 openuuid android:imei加密后的值加密算法见2.3.2设 备ID加密
			
			public String getDevicebrand() {
				return devicebrand;
			}
			public void setDevicebrand(String devicebrand) {
				this.devicebrand = devicebrand;
			}
			public String getDevicemodel() {
				return devicemodel;
			}
			public void setDevicemodel(String devicemodel) {
				this.devicemodel = devicemodel;
			}
			public int getPm() {
				return pm;
			}
			public void setPm(int pm) {
				this.pm = pm;
			}
			public String getOs_version() {
				return os_version;
			}
			public void setOs_version(String os_version) {
				this.os_version = os_version;
			}
			public int getConn() {
				return conn;
			}
			public void setConn(int conn) {
				this.conn = conn;
			}
			public int getNetworkid() {
				return networkid;
			}
			public void setNetworkid(int networkid) {
				this.networkid = networkid;
			}
			public int getLng() {
				return lng;
			}
			public void setLng(int lng) {
				this.lng = lng;
			}
			public int getLat() {
				return lat;
			}
			public void setLat(int lat) {
				this.lat = lat;
			}
			public int getScreen_width() {
				return screen_width;
			}
			public void setScreen_width(int screen_width) {
				this.screen_width = screen_width;
			}
			public int getScreen_hight() {
				return screen_hight;
			}
			public void setScreen_hight(int screen_hight) {
				this.screen_hight = screen_hight;
			}
			public int getScreen_orientation() {
				return screen_orientation;
			}
			public void setScreen_orientation(int screen_orientation) {
				this.screen_orientation = screen_orientation;
			}
			public float getScreen_density() {
				return screen_density;
			}
			public void setScreen_density(float screen_density) {
				this.screen_density = screen_density;
			}
			public String getDeviceid() {
				return deviceid;
			}
			public void setDeviceid(String deviceid) {
				this.deviceid = deviceid;
			}
		
			
		}

		public boolean isIs_app() {
			return is_app;
		}

		public void setIs_app(boolean is_app) {
			this.is_app = is_app;
		}

		public String getPkgname() {
			return pkgname;
		}

		public void setPkgname(String pkgname) {
			this.pkgname = pkgname;
		}

		public Device getDevice() {
			return device;
		}

		public void setDevice(Device device) {
			this.device = device;
		}
		
	}
	public class User{
		private String id;//ADX侧用户Id
		private String ip;//用户ip
		private String user_agent;//
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getIp() {
			return ip;
		}
		public void setIp(String ip) {
			this.ip = ip;
		}
		public String getUser_agent() {
			return user_agent;
		}
		public void setUser_agent(String user_agent) {
			this.user_agent = user_agent;
		}
	}
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
	public List<AdSlot> getAdSlot() {
		return adSlot;
	}
	public void setAdSlot(List<AdSlot> adSlot) {
		this.adSlot = adSlot;
	}
	public List<Site> getSite() {
		return site;
	}
	public void setSite(List<Site> site) {
		this.site = site;
	}
	public Mobile getMobile() {
		return mobile;
	}
	public void setMobile(Mobile mobile) {
		this.mobile = mobile;
	}
	public boolean isIs_test() {
		return is_test;
	}
	public void setIs_test(boolean is_test) {
		this.is_test = is_test;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	

}
