package com.madhouse.configuration;

public class Bid {

	private String path;

	private String apiClass;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Bid(String path) {
		super();
		this.path = path;
	}

	public String getApiClass() {
		return apiClass;
	}

	public void setApiClass(String apiClass) {
		this.apiClass = apiClass;
	}

	public Bid() {
		super();
		// TODO 自动生成的构造函数存根
	}

}