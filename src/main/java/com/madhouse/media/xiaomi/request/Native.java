package com.madhouse.media.xiaomi.request;

public class Native {
	private String request;
	private String id;
	private Integer w;
	private Integer h;
	private String ver;
	private Integer[] api;
	private Integer[] battr;

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

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

	public String getVer() {
		return ver;
	}

	public void setVer(String ver) {
		this.ver = ver;
	}

	public Integer[] getApi() {
		return api;
	}

	public void setApi(Integer[] api) {
		this.api = api;
	}

	public Integer[] getBattr() {
		return battr;
	}

	public void setBattr(Integer[] battr) {
		this.battr = battr;
	}

}
