package com.madhouse.configuration;

import java.util.List;

public class WebApp {
	private String domain;
	private String impression;
	private String click;
	private int wokerId;
	private int port;
	private Boolean gZipOn;
	private List<Bid> bids;
	private int expiredTime;
	private String resourcePath;
	private int minIdle;
	private int maxIdle;
	private int maxTotal;
	private int slowDownCount;

	public String getResourcePath() {
		return resourcePath;
	}

	public void setResourcePath(String resourcePath) {
		this.resourcePath = resourcePath;
	}

	public int getMinIdle() {
		return minIdle;
	}

	public void setMinIdle(int minIdle) {
		this.minIdle = minIdle;
	}

	public int getMaxIdle() {
		return maxIdle;
	}

	public void setMaxIdle(int maxIdle) {
		this.maxIdle = maxIdle;
	}

	public int getMaxTotal() {
		return maxTotal;
	}

	public void setMaxTotal(int maxTotal) {
		this.maxTotal = maxTotal;
	}

	public int getWokerId() {
		return wokerId;
	}

	public void setWokerId(int wokerId) {
		this.wokerId = wokerId;
	}

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

	public int getSlowDownCount() {
		return slowDownCount;
	}

	public void setSlowDownCount(int slowDownCount) {
		this.slowDownCount = slowDownCount;
	}
}