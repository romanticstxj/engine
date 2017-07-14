package com.madhouse.configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Kafka {
	public class Topic {
		private String type;
		private String topic;

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getTopic() {
			return topic;
		}

		public void setTopic(String topic) {
			this.topic = topic;
		}
	}

	private String brokers;
	private List<Topic> topics;
	private Map<String, String> topicsMap = new HashMap<>();

	public String getBrokers() {
		return brokers;
	}

	public void setBrokers(String brokers) {
		this.brokers = brokers;
	}

	public List<Topic> getTopics() {
		return topics;
	}

	public String getTopic(String type) {
		return this.topicsMap.get(type);
	}

	public void setTopics(List<Topic> topics) {
		this.topics = topics;
		this.topicsMap.clear();
		for (Topic topic : topics) {
			this.topicsMap.put(topic.getType(), topic.getTopic());
		}
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