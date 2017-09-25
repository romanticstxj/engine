package com.madhouse.media.oppo;

import java.util.List;

import org.codehaus.jackson.annotate.JsonProperty;

import com.alibaba.fastjson.annotation.JSONField;



public class OppoBidRequestTest{
	private String id;//必填。竞价请求中以为标识
    private List<Imp> imp;//必填。Imp数组对象，至少含一个对象
    private App app;//必填。应用详情对象
    private Device device;//承载曝光的用户设备信息对象
    private User user;//设备用户对象
    private Integer test;//标识测试模式1：测试 0：正式
    private Integer at;//拍卖类型1:First Price;2:Second Price Plus
    private Integer tmax;//交易最大超时毫秒值（默认100ms）
    private List<String> wseat;//（广告主，代理）白名单
    private List<String> bseat;//（广告主，代理）黑名单
    private List<String> badv;//广告商域名黑名单数组
	
    public class Imp{
    	private String id;//必填。
    	@JsonProperty("native")
    	@JSONField(name="native")
    	private Native natives;//若曝光为native类型时，必填
    	private Pmp pmp;//若曝光为pmp类型时，必填
    	private String tagid;//必填。广告位id
    	private long bidfloor;//底价，单位分/CPM
    	
    	public class Native{
    		private String request;
    		private String ver;
			
			public String getRequest() {
				return request;
			}
			public void setRequest(String request) {
				this.request = request;
			}
			public String getVer() {
				return ver;
			}
			public void setVer(String ver) {
				this.ver = ver;
			}
    	}
    	public class Pmp{
    		private List<Deal> delas;
    		public class Deal{
    			private String id;
				public String getId() {
					return id;
				}
				public void setId(String id) {
					this.id = id;
				}
    		}
			public List<Deal> getDelas() {
				return delas;
			}
			public void setDelas(List<Deal> delas) {
				this.delas = delas;
			}
    	}
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public Pmp getPmp() {
			return pmp;
		}
		public void setPmp(Pmp pmp) {
			this.pmp = pmp;
		}
		public String getTagid() {
			return tagid;
		}
		public void setTagid(String tagid) {
			this.tagid = tagid;
		}
		public long getBidfloor() {
			return bidfloor;
		}
		public void setBidfloor(long bidfloor) {
			this.bidfloor = bidfloor;
		}
		public Native getNatives() {
			return natives;
		}
		public void setNatives(Native natives) {
			this.natives = natives;
		}
		
    }
    public class App{
    	private String id;//竞价系统中定义的应用id
    	private String name;//应用名称
    	private String bundle;//平台 定义 的应用 的应用 唯一标示（Android中的包名，IOS中的numeric ID）
    	private String storeurl;//应用 商店 中该应用的安装地址 
    	private String ver;//应用版本
    	private Integer paid;//标识是否为付费应用，0=免费 ，1=付费版本
    	private String keywords;//应用关键词集合（逗号分隔）
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
		public String getBundle() {
			return bundle;
		}
		public void setBundle(String bundle) {
			this.bundle = bundle;
		}
		public String getStoreurl() {
			return storeurl;
		}
		public void setStoreurl(String storeurl) {
			this.storeurl = storeurl;
		}
		public String getVer() {
			return ver;
		}
		public void setVer(String ver) {
			this.ver = ver;
		}
		public Integer getPaid() {
			return paid;
		}
		public void setPaid(Integer paid) {
			this.paid = paid;
		}
		public String getKeywords() {
			return keywords;
		}
		public void setKeywords(String keywords) {
			this.keywords = keywords;
		}
    }
    public class Device{
    	private String ua;//浏览器用user agent串
    	private String ip;
    	private String ipv6;
    	private Integer devicetype;//设备类型
    	private String make;//设备制造商
    	private String model;//设备型号
    	private String os;//操作系统
    	private String osv;//操作系统版本
    	private Integer h;//高
    	private Integer w;//宽
    	private Integer ppi;//屏幕尺寸
    	private float pxratio;//物理像素和设备独立像素比例
    	private Integer js;//javascript支持 0=no，1=yes
    	private Integer geofetch;//标示是否支持banner中javascript代码获取位置信息：0=no,1=yes
    	private Integer connectiontype;//网络连接类型
    	private String didmd5;//硬件设备id（如 imei）
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
		public String getIpv6() {
			return ipv6;
		}
		public void setIpv6(String ipv6) {
			this.ipv6 = ipv6;
		}
		public Integer getDevicetype() {
			return devicetype;
		}
		public void setDevicetype(Integer devicetype) {
			this.devicetype = devicetype;
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
		public Integer getH() {
			return h;
		}
		public void setH(Integer h) {
			this.h = h;
		}
		public Integer getW() {
			return w;
		}
		public void setW(Integer w) {
			this.w = w;
		}
		public Integer getPpi() {
			return ppi;
		}
		public void setPpi(Integer ppi) {
			this.ppi = ppi;
		}
		public float getPxratio() {
			return pxratio;
		}
		public void setPxratio(float pxratio) {
			this.pxratio = pxratio;
		}
		public Integer getJs() {
			return js;
		}
		public void setJs(Integer js) {
			this.js = js;
		}
		public Integer getGeofetch() {
			return geofetch;
		}
		public void setGeofetch(Integer geofetch) {
			this.geofetch = geofetch;
		}
		public Integer getConnectiontype() {
			return connectiontype;
		}
		public void setConnectiontype(Integer connectiontype) {
			this.connectiontype = connectiontype;
		}
		public String getDidmd5() {
			return didmd5;
		}
		public void setDidmd5(String didmd5) {
			this.didmd5 = didmd5;
		}
    }
    public class User{
    	private String id;
    	private String gender;//性别“M”：男，“F”：女 ，“0”：未知
    	private String keywords;//关键词 兴趣等（逗号分隔）
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getGender() {
			return gender;
		}
		public void setGender(String gender) {
			this.gender = gender;
		}
		public String getKeywords() {
			return keywords;
		}
		public void setKeywords(String keywords) {
			this.keywords = keywords;
		}
    }
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
	public Integer getTest() {
		return test;
	}
	public void setTest(Integer test) {
		this.test = test;
	}
	public Integer getAt() {
		return at;
	}
	public void setAt(Integer at) {
		this.at = at;
	}
	public Integer getTmax() {
		return tmax;
	}
	public void setTmax(Integer tmax) {
		this.tmax = tmax;
	}
	public List<String> getWseat() {
		return wseat;
	}
	public void setWseat(List<String> wseat) {
		this.wseat = wseat;
	}
	public List<String> getBseat() {
		return bseat;
	}
	public void setBseat(List<String> bseat) {
		this.bseat = bseat;
	}
	public List<String> getBadv() {
		return badv;
	}
	public void setBadv(List<String> badv) {
		this.badv = badv;
	}
    
}
