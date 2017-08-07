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
	private Map<String, String> kafkasMap = new HashMap<>();

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
	
	public String getkafka(String type) {
        return this.kafkasMap.get(type);
    }

	public void setTopics(List<Topic> topics) {
		this.topics = topics;
		this.topicsMap.clear();
		for (Topic topic : topics) {
			this.topicsMap.put(topic.getType(), topic.getTopic());
			this.kafkasMap.put(topic.getTopic(), topic.getType());
		}
	}
}