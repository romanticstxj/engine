package com.madhouse.resource;

import com.madhouse.kafkaclient.producer.KafkaProducer;
import com.madhouse.ssp.LoggerUtil;

/**
 * Created by WUJUNFENG on 2017/5/23.
 */
public class ResourceManager {
    private KafkaProducer kafkaProducer;

    public ResourceManager() {
        this.kafkaProducer = new KafkaProducer("", 1048576, 8, null);
    }

    public boolean init()
    {
        return this.kafkaProducer.start(LoggerUtil.getInstance());
    }

    public KafkaProducer getKafkaProducer() {
        return this.kafkaProducer;
    }

    public String getLocation(String ip) {
        return "";
    }
}
