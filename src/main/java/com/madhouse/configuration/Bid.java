package com.madhouse.configuration;

public class Bid {

	private String path;

	private String className;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public Bid(String path) {
		super();
		this.path = path;
	}

	public Bid() {
		super();
		// TODO 自动生成的构造函数存根
	}

}