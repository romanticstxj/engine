package com.madhouse.configuration;

public class Premiummad {
	private Redis redis;

	private WebApp webapp;

	private Kafka kafka;

	private MetaData metadata;

	public MetaData getMetadata() {
		return metadata;
	}

	public void setMetadata(MetaData metadata) {
		this.metadata = metadata;
	}

	public Redis getRedis() {
		return redis;
	}

	public void setRedis(Redis redis) {
		this.redis = redis;
	}

	public WebApp getWebapp() {
		return webapp;
	}

	public void setWebapp(WebApp webapp) {
		this.webapp = webapp;
	}

	public Kafka getKafka() {
		return kafka;
	}

	public void setKafka(Kafka kafka) {
		this.kafka = kafka;
	}

	public Premiummad() {
		super();
	}
}
