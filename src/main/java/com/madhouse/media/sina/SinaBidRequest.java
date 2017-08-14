package com.madhouse.media.sina;

import java.util.List;


public class SinaBidRequest{
    private String id;//请求id，由WAX系统产生
    private String dealid;//WAX系统给参与Prefred deal的dsp分配的dealId
    private String adid;//WAX系统给支持PDB的dsp分配的订单号
    private List<String> rule;//此次流量命中的在dmp系统中注册的ruleid
    private int at;//竞拍方式，“2”二阶竞价
    private List<Imp> imp;// 曝光对象，一次request可以包含多个imp
    private Device device;
    private User user;
    private App app;
    
    //imp对象
    public class Imp{
    	private String id;//曝光id
    	private String tagid;//广告位id
    	private int bidfloor;//底价，单位是分、前次曝光，即CPM
    	private String bidfloorcur;//底价货币单位，默认为RMB
    	private Banner banner;
    	private Video video;//暂不支持投放
    	private Feed feed;
    	private Repeat ext;
    	
    	public class Banner{
    		private int w;//广告位宽
    		private int h;//广告位高
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
    	}
    	
    	public class Video{
    		
    	}
    	
    	public class Feed{
		    private int type;//0默认值，支持所有类型		

			public int getType() {
				return type;
			}

			public void setType(int type) {
				this.type = type;
			}
		 
		}
    	public class Repeat{
			private int repeat;//本次请求曝光中，需要播放广告素材数量。暂时无具体意义，可忽略

			public int getRepeat() {
				return repeat;
			}

			public void setRepeat(int repeat) {
				this.repeat = repeat;
			}
			
		}
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
		public int getBidfloor() {
			return bidfloor;
		}
		public void setBidfloor(int bidfloor) {
			this.bidfloor = bidfloor;
		}
		public String getBidfloorcur() {
			return bidfloorcur;
		}
		public void setBidfloorcur(String bidfloorcur) {
			this.bidfloorcur = bidfloorcur;
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
		public Feed getFeed() {
			return feed;
		}
		public void setFeed(Feed feed) {
			this.feed = feed;
		}
		public Repeat getExt() {
			return ext;
		}
		public void setExt(Repeat ext) {
			this.ext = ext;
		}
		
    }
    public class Device{
    	private String ua;//浏览器用户代理描述字符串
    	private String ip;//ip地址
    	private Geo geo;//地理位置信息
    	private String model;//设备型号，格式“品牌_型号”，如“iphone_6”
    	private String os;//设备操作系统，如ios  Android
    	private String osv;//设备操作系统版本
    	/**
    	 * 网络连接类型
    	 * 0:未知
    	 * 1：以太网
    	 * 2：wifi
    	 * 3：蜂窝网络，未知代
    	 * 4：蜂窝网络2g
    	 * 5：蜂窝网络3g
    	 * 6：蜂窝网络4g
    	 */
    	private Integer connectionType;
    	/**
    	 * 46000：移动
    	 * 46001：联通
    	 * 46003：电信
    	 * 46020：铁通
    	 */
    	private String carrier;//运行商代码
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

		public Geo getGeo() {
			return geo;
		}

		public void setGeo(Geo geo) {
			this.geo = geo;
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

		public Integer getConnectionType() {
			return connectionType;
		}

		public void setConnectionType(Integer connectionType) {
			this.connectionType = connectionType;
		}

		public String getCarrier() {
			return carrier;
		}

		public void setCarrier(String carrier) {
			this.carrier = carrier;
		}

		public Ext getExt() {
			return ext;
		}

		public void setExt(Ext ext) {
			this.ext = ext;
		}

		public class Geo{
    		private float lat;//纬度
    		private float lon;//经度
    		private int type;//地理信息来源（1：GPS 2：ip）
    		private Ext ext;
    		
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
			public int getType() {
				return type;
			}
			public void setType(int type) {
				this.type = type;
			}
			
			public Ext getExt() {
				return ext;
			}
			public void setExt(Ext ext) {
				this.ext = ext;
			}

			public class Ext{
				private int accuracy;//地理信息精确度，单位为米

				public int getAccuracy() {
					return accuracy;
				}

				public void setAccuracy(int accuracy) {
					this.accuracy = accuracy;
				}
				
			}
    		
    	}
    	
    	public class Ext{
    		private String idfa;//ios的idfa字段
    		private String imei;//android
			public String getIdfa() {
				return idfa;
			}
			public void setIdfa(String idfa) {
				this.idfa = idfa;
			}
			public String getImei() {
				return imei;
			}
			public void setImei(String imei) {
				this.imei = imei;
			}
    	}
    	
    }
    public class User{
    	private String gender;//性别，M为男，F为女，为空表示未知
    	private int yob;//4位数字的出生年份：如988
    	private String id;//广告请求匹配dmp规则时，发送标识用户唯一标识的id
		public String getGender() {
			return gender;
		}
		public void setGender(String gender) {
			this.gender = gender;
		}
		public int getYob() {
			return yob;
		}
		public void setYob(int yob) {
			this.yob = yob;
		}
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
    }
    public class App{
    	private String id;//app在WAX中的编号
    	private String name;//app名称
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
    }
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDealid() {
		return dealid;
	}
	public void setDealid(String dealid) {
		this.dealid = dealid;
	}
	public String getAdid() {
		return adid;
	}
	public void setAdid(String adid) {
		this.adid = adid;
	}
	public List<String> getRule() {
		return rule;
	}
	public void setRule(List<String> rule) {
		this.rule = rule;
	}
	public int getAt() {
		return at;
	}
	public void setAt(int at) {
		this.at = at;
	}
	public List<Imp> getImp() {
		return imp;
	}
	public void setImp(List<Imp> imp) {
		this.imp = imp;
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
	
}
