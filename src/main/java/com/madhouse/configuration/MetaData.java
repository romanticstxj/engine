package com.madhouse.configuration;

import java.util.List;

public class MetaData {
	private List<Url> requesturls;

	public List<Url> getRequesturls() {
		return requesturls;
	}

	public void setRequesturls(List<Url> requesturls) {
		this.requesturls = requesturls;
	}

	public MetaData(List<Url> requesturls) {
		super();
		this.requesturls = requesturls;
	}

	public MetaData() {
		super();
		// TODO 自动生成的构造函数存根
	}

}