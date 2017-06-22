package com.madhouse.configuration;

import java.util.List;

public class Kafka {
	private String brokers;
	private List<Topic> topics;

	public String getBrokers() {
		return brokers;
	}

	public void setBrokers(String brokers) {
		this.brokers = brokers;
	}

	public List<Topic> getTopics() {
		return topics;
	}

	public void setTopics(List<Topic> topics) {
		this.topics = topics;
	}

	public Kafka(String brokers, List<Topic> topics) {
		super();
		this.brokers = brokers;
		this.topics = topics;
	}

	public Kafka() {
		super();
		// TODO 自动生成的构造函数存根
	}

}