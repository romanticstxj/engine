package com.madhouse.media.baofeng;

public class BaoFengBidRequest {

	/**
	 * 必须 bid request的唯一id
	 */
	private String id;
	/**
	 * 必须 Impression对象数组，但仅包含一个Impression对象，描述广告位
	 */
	private Impression imp;
	/**
	 * 必须 App对象，描述App信息
	 */
	private App app;
	/**
	 * 必须 Device对象，描述设备信息
	 */
	private Device device;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Impression getImp() {
		return imp;
	}

	public void setImp(Impression imp) {
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

	@SuppressWarnings("unused")
	public class App {
		/**
		 * 必须 App唯一id
		 */

		private String id;
		/**
		 * 可选 App名称
		 */
		private String name;
		/**
		 * 可选 App版本
		 */
		private String ver;

		/**
		 * 可选 App类型 参考附录4.2
		 */

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

		public String getVer() {
			return ver;
		}

		public void setVer(String ver) {
			this.ver = ver;
		}

	}

	@SuppressWarnings("unused")
	public class Device {
		/**
		 * required DeviceID的SHA1哈希值，IMEI或Mac地址
		 */
		private String id;
		/**
		 * required IMEI（android）或IDFA（iphone）
		 */
		private String dpid;
		/**
		 * 设备浏览器的User-Agent字符串
		 */
		private String ua;
		/**
		 * 可选 设备的IP地址
		 */
		private String ip;

		/**
		 * 可选 设备使用的运营商，使用MCC+MNC参考http://en.wikipedia.org/wiki/
		 * Mobile_Network_Code 例如移动：46000 联通46001
		 */
		private String carrier;

		/**
		 * 可选 设备型号
		 */
		private String model;
		/**
		 * 可选 设备操作系统
		 */
		private String os;
		/**
		 * 可选 设备操作系统版本号
		 */
		private String osv;
		/**
		 * 可选 设备联网方式，参考附录4.3
		 */
		private int connectiontype;
		/**
		 * 可选 设备类型，参考附录4.4
		 */
		private int devicetype;
		/**
		 * 可选 设备经纬度，用维度经度使用逗号分隔，例如：38.04165,114.50884
		 */
		private String mac;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getDpid() {
			return dpid;
		}

		public void setDpid(String dpid) {
			this.dpid = dpid;
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

		public String getCarrier() {
			return carrier;
		}

		public void setCarrier(String carrier) {
			this.carrier = carrier;
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

		public int getConnectiontype() {
			return connectiontype;
		}

		public void setConnectiontype(int connectiontype) {
			this.connectiontype = connectiontype;
		}

		public int getDevicetype() {
			return devicetype;
		}

		public void setDevicetype(int devicetype) {
			this.devicetype = devicetype;
		}

		public String getMac() {
			return mac;
		}

		public void setMac(String mac) {
			this.mac = mac;
		}
	}

	public class Impression {
		/**
		 * 必须 Impression对象唯一id
		 */
		private String id;
		/**
		 * 必须 广告位的宽度（单位：像素）
		 */
		private int w;
		/**
		 * 必须 广告位的高度（单位：像素）
		 */
		private int h;
		/**
		 * 可选 广告位置 参考附录4.1 目前暂时只有6,7两种广告位，
		 * 广告位为7时，可以没有click监测链接和跳转目标链接。为6时需要跳转目标链接。
		 */
		private int pos;

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

		public int getPos() {
			return pos;
		}

		public void setPos(int pos) {
			this.pos = pos;
		}
	}

}
