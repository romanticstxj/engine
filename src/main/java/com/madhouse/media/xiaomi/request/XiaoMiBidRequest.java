package com.madhouse.media.xiaomi.request;

public class XiaoMiBidRequest {

	private String id;
	private Integer test = 0;
	private Integer at = 2;
	private Integer tmax = 200;
	private String[] bcat;
	private String[] badv;

	private Imp[] imp;
	private Site site;
	private App app;
	private Device device;
	private User user;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String[] getBcat() {
		return bcat;
	}

	public void setBcat(String[] bcat) {
		this.bcat = bcat;
	}

	public String[] getBadv() {
		return badv;
	}

	public void setBadv(String[] badv) {
		this.badv = badv;
	}

	public Imp[] getImp() {
		return imp;
	}

	public void setImp(Imp[] imp) {
		this.imp = imp;
	}

	public Site getSite() {
		return site;
	}

	public void setSite(Site site) {
		this.site = site;
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

}
