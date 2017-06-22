package com.madhouse.configuration;

public class Topic {
	private String type;
	private String value;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Topic(String type, String value) {
		super();
		this.type = type;
		this.value = value;
	}

	public Topic() {
		super();
		// TODO 自动生成的构造函数存根
	}

}