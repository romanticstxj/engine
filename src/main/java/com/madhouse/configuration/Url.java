package com.madhouse.configuration;

public class Url {
	private String type;
	private String url;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Url(String type, String url) {
		super();
		this.type = type;
		this.url = url;
	}

	public Url() {
		super();
		// TODO 自动生成的构造函数存根
	}

}