package com.madhouse.resource;

import com.madhouse.dsp.DSPBaseHandler;
import com.madhouse.kafkaclient.producer.KafkaProducer;
import com.madhouse.media.MediaBaseHandler;
import com.madhouse.ssp.LoggerUtil;
import sun.security.jca.GetInstance;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by WUJUNFENG on 2017/5/23.
 */
public class ResourceManager {
    private KafkaProducer kafkaProducer;
    private ConcurrentHashMap<Integer, DSPBaseHandler> dspBaseHandlerMap = new ConcurrentHashMap<Integer, DSPBaseHandler>();
    private ConcurrentHashMap<Integer, MediaBaseHandler> mediaBaseHandlerMap = new ConcurrentHashMap<Integer, MediaBaseHandler>();

    private static final ResourceManager resourceManager = new ResourceManager();
    private ResourceManager(){};
    public static ResourceManager getInstance() {
        return resourceManager;
    }

    public boolean init()
    {
        this.kafkaProducer = new KafkaProducer("", 1048576, 8, null);

        return this.kafkaProducer.start(LoggerUtil.getInstance());
    }

    public KafkaProducer getKafkaProducer() {
        return this.kafkaProducer;
    }

    public String getLocation(String ip) {
        return "";
    }

    public MediaBaseHandler getMediaHandler(int apiType) {
        return this.mediaBaseHandlerMap.get(apiType);
    }

    public DSPBaseHandler getDSPHandler(int apiType) {
        return this.dspBaseHandlerMap.get(apiType);
    }
}
