package com.madhouse.media.yiche;

import java.util.List;

public class YiCheBidRequest {
	
	private String id;//唯一竞价请求标识，将uuid采用32位小写md5加密方式传输，由交易平台提供。
	private List<Imp> imp;//Imp对象的数组，表示所有的曝光机会。必须至少有一个Imp对象。 site object recommended 通过一个Site对象描述网站的相关信息，只适用于网站。
	private Site site;//通过一个Site对象描述网站的相关信息，只适用于网站。
	private Device device;//通过一个Device对象描述的将展示曝光的用户设备 
	private User user;//object recommended 通过一个User对象描述的关于设备使用人的详细信息，也就是广告的受众。
	private App app;//通过一个App对象描述App的相关信息，如果支持的内容是一个非浏览器应用程序(通常是在移动的)，而不是网站，则应该包括这个对象。投标请求不能同时包含应用程序和站点对象。
	private String date;//预投放时间设置，用于测试和验证未来某一天投放的物料是否正常。时间格式： YYYY-MM-DD
	
	public static class Imp{
		
		private String id;//在当前竞价请求中的曝光机会的唯一标识符，将uuid采用32位小写md5加密方式传输。
		private Banner banner;//用于描述一个Banner对象；如果这次曝光是一个横幅曝光机会的话，必选。交易前由交易平台提供说明文档。 
		private String tagid;//广告位唯一标识将采用媒体-频道-广告位的名称作为标识，采用32位小写md5加密方式传输。交易前由交易平台提供说明文档。
		private Pmp pmp;//一个Pmp对象，用来描述此次曝光机会的私有交易市场信息。 
		private int area;//媒体属性标识，描述tagid所属页面的省份ID（如湖北省车市页广告位将标识湖北省ID，其他媒体属性标识逻辑相同），不适用此属性的页面默认值为0，全部有效值见《易车网区域标准库》。
		private int city;//媒体属性标识，描述tagid所属页面的城市ID，不适用此属性的页面默认值为0，全部有效值见《易车网区域标准库》。
		private int brand;//媒体属性标识，描述tagid所属页面的品牌ID，不适用此属性的页面默认值为0，全部有效值见《易车网品牌标准库》。
		private int model;//媒体属性标识，描述tagid所属页面的品牌ID，不适用此属性的页面默认值为0，全部有效值见《易车网品牌标准库》。 
		private List<Integer> keyword;//媒体属性标识，描述tagid所属页面的车型ID，不适用此属性的页面默认
		private List<Integer> temp;//广告位 广告位 所支持的物料模版ID列表，供DSP使用，全部有效值见《易车网广告位模版标准库》，必填项。
		private String sku;//广告位所在订单售卖库存的计量单位，库存售卖方式含有：省份、城市、品牌、车型、模版、关键字。在交易时，采用32位小写md5加密方式传输。交易前由交易平台提供《易车网广告业务OpenAPI服务》说明文档，并且要求DSP方根据sku上传物料。
		
		public static class Banner{
			private Integer w;//广告位宽度。 非banner广告位，如文字链、信息流等广告位的宽度可传0。
			private Integer h;//广告位高度。非banner广告位，如文字链、信息流等广告位的高度可传0。
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
		public static class Pmp{
			private List<Deal> deals;//一个Deal对象数组用来描述特定交易信息，如PDB交易信息
			
			public static class Deal{
				private String id;//交易编号，这里也是订单编号，示例：AO00000000000000

				public String getId() {
					return id;
				}

				public void setId(String id) {
					this.id = id;
				}
				
			}

			public List<Deal> getDeals() {
				return deals;
			}

			public void setDeals(List<Deal> deals) {
				this.deals = deals;
			}

			
			
		}
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public Banner getBanner() {
			return banner;
		}
		public void setBanner(Banner banner) {
			this.banner = banner;
		}
		public String getTagid() {
			return tagid;
		}
		public void setTagid(String tagid) {
			this.tagid = tagid;
		}
		public Pmp getPmp() {
			return pmp;
		}
		public void setPmp(Pmp pmp) {
			this.pmp = pmp;
		}
		public int getArea() {
			return area;
		}
		public void setArea(int area) {
			this.area = area;
		}
		public int getCity() {
			return city;
		}
		public void setCity(int city) {
			this.city = city;
		}
		public int getBrand() {
			return brand;
		}
		public void setBrand(int brand) {
			this.brand = brand;
		}
		public int getModel() {
			return model;
		}
		public void setModel(int model) {
			this.model = model;
		}
		public List<Integer> getKeyword() {
			return keyword;
		}
		public void setKeyword(List<Integer> keyword) {
			this.keyword = keyword;
		}
		public List<Integer> getTemp() {
			return temp;
		}
		public void setTemp(List<Integer> temp) {
			this.temp = temp;
		}
		public String getSku() {
			return sku;
		}
		public void setSku(String sku) {
			this.sku = sku;
		}
		
		
	}
	public static class Site{
		private Integer id;//描述当前网站媒体ID，由《易车网SSP资源库》提供。示例：255（易车网wap）、180（易车网pc）。
		private String page;//当前网站页面URL，即请求host或referer信息，对于URL敏感字符需做urlencode编码。 
		private List<String> cat;//描述当前网站的IAB内容类别数组。请参考OpenRTB API 5.1引用列表。示例：IAB2 、IABx。
		public Integer getId() {
			return id;
		}
		public void setId(Integer id) {
			this.id = id;
		}
		public String getPage() {
			return page;
		}
		public void setPage(String page) {
			this.page = page;
		}
		public List<String> getCat() {
			return cat;
		}
		public void setCat(List<String> cat) {
			this.cat = cat;
		}
		
	}
	
	public static class Device{
		private String ip;//用户设备IP地址。 A.B.C.D(4段点分)，如124.34.56.78，客户端IP。
		private String ua;//设备的User Agent信息，需escape转义，如： Mozilla%2F5.0 (Linux%3BAndroid4.0.4%3BGT-I9220% 20Build%2FIMM76D ) 
		private String didmd5;//硬件设备ID，如IMEI，32 位大写 md5md5md5值。
		private String macmd5;//32 位设备macmacmac地址的大写 的大写 md5md5md5值。参数填写 值。参数填写 值。参数填写 值。参数填写 规则同上。 规则同上。	
		private Integer devicetype;//设备类型 Devicetype _Enum{ 0–Others, 1–Phone, 2–Pad }
		private Integer os;//设备操作系统下面枚举值中的一个， PC 、MacMac 等桌面流量或 等桌面流量或 无法识别的流量归类到 无法识别的流量归类到 无法识别的流量归类到 无法识别的流量归类到 无法识别的流量归类到 无法识别的流量归类到 0,
		//下面枚举值中的一个： 下面枚举值中的一个： 下面枚举值中的一个： 下面枚举值中的一个： 下面枚举值中的一个： 下面枚举值中的一个：
		/*0–Others,
		1–iOS,
		2–Android,
		3–WP
		}枚举值：1、2，可帮助DSP区分投放素材。 */
		private String osv;//操作系统的版本号，如：iOS 系统：9.1 An-droid系统：5.0.2 其他系统：API v1版本不单独解析 。
		private String carrier;//运营商识别码。 识别码。 识别码。 应该是原始参数和 原始参数和md5参数 中的一项 。 
		private String make;//设备制造(如苹果)。
		private Integer connectiontype;//网络连接类型如果无法识别的终端归类到0，下面枚举值中的一个：
		/*Connectiontype _Enum{
		0-Others,
		1-Wifi,
		2-2G,
		3-3G,
		4-4G,
		5-5G*/
		
		public String getIp() {
			return ip;
		}
		public void setIp(String ip) {
			this.ip = ip;
		}
		public String getUa() {
			return ua;
		}
		public void setUa(String ua) {
			this.ua = ua;
		}
		public String getDidmd5() {
			return didmd5;
		}
		public void setDidmd5(String didmd5) {
			this.didmd5 = didmd5;
		}
		public String getMacmd5() {
			return macmd5;
		}
		public void setMacmd5(String macmd5) {
			this.macmd5 = macmd5;
		}
		public Integer getDevicetype() {
			return devicetype;
		}
		public void setDevicetype(Integer devicetype) {
			this.devicetype = devicetype;
		}
		public Integer getOs() {
			return os;
		}
		public void setOs(Integer os) {
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
		public String getMake() {
			return make;
		}
		public void setMake(String make) {
			this.make = make;
		}
		public Integer getConnectiontype() {
			return connectiontype;
		}
		public void setConnectiontype(Integer connectiontype) {
			this.connectiontype = connectiontype;
		}
		
		
	
	}
	
	public static class User{
		private String id ;//交易时，为用户分配的标识ID。
		//1、易车网PC、WAP站用户唯一标识，采用浏览器cookie，通过cookie mapping加密后传输。对于易车网内部DSP用户，请参考《易车网DMP广告服务》； 
		//2、易车网iOS用户唯一标识，采用设备idfv的md5，md5为32位小写； 3、易车网Android用户唯一标识，直接采用设备deviceId，中间不做任何处理；

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}
	
		
	}
	public static class App{
		private String id;//描述当前app的媒体ID，由《易车网SSP资源库》提供。示例：1030（汽车报价大全APP）、1019（易车网app）。
		private String cat;//描述当前app的IAB内容类别数组。请参考OpenRTB API 5.1引用列表。示例：IAB2 、IABx。
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getCat() {
			return cat;
		}
		public void setCat(String cat) {
			this.cat = cat;
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
	public Site getSite() {
		return site;
	}
	public void setSite(Site site) {
		this.site = site;
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
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}

	
}
