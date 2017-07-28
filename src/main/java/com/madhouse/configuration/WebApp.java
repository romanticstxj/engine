package com.madhouse.configuration;

import java.util.List;

public class WebApp {
	private String domain;

	private String impression;

	private String click;

	private int port;

	private Boolean gZipOn;

	private List<Bid> bids;

	private int expiredTime;

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public Boolean getgZipOn() {
		return gZipOn;
	}

	public void setgZipOn(Boolean gZipOn) {
		this.gZipOn = gZipOn;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public List<Bid> getBids() {
		return bids;
	}

	public void setBids(List<Bid> bids) {
		this.bids = bids;
	}

	public String getImpression() {
		return impression;
	}

	public void setImpression(String impression) {
		this.impression = impression;
	}

	public String getClick() {
		return click;
	}

	public void setClick(String click) {
		this.click = click;
	}

	public int getExpiredTime() {
		return expiredTime;
	}

	public void setExpiredTime(int expiredTime) {
		this.expiredTime = expiredTime;
	}
}