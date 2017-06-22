package com.madhouse.resource;

import com.alibaba.fastjson.JSON;
import com.madhouse.configuration.Bid;
import com.madhouse.configuration.Premiummad;
import com.madhouse.dsp.DSPBaseHandler;
import com.madhouse.kafkaclient.producer.KafkaProducer;
import com.madhouse.media.MediaBaseHandler;
import com.madhouse.ssp.LoggerUtil;
import com.madhouse.util.ObjectUtils;
import com.madhouse.util.StringUtil;

import sun.security.jca.GetInstance;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Created by WUJUNFENG on 2017/5/23.
 */
public class ResourceManager {
    private KafkaProducer kafkaProducer;
    private ConcurrentHashMap<Integer, DSPBaseHandler> dspBaseHandlerMap = new ConcurrentHashMap<Integer, DSPBaseHandler>();
    private ConcurrentHashMap<Integer, MediaBaseHandler> mediaBaseHandlerMap = new ConcurrentHashMap<Integer, MediaBaseHandler>();
    private ConcurrentHashMap<String, Integer> mediaApiType = new ConcurrentHashMap<String, Integer>();
    private ConcurrentLinkedDeque<pai> locations = new ConcurrentLinkedDeque<>();

    private final Premiummad premiummad = JSON.parseObject(ObjectUtils.ReadFile(ResourceManager.class.getClassLoader().getResource("config.json").getPath()), Premiummad.class);

    private static final ResourceManager resourceManager = new ResourceManager();
    private ResourceManager(){};
    public static ResourceManager getInstance() {
        return resourceManager;
    }

    public boolean init()
    {
        this.kafkaProducer = new KafkaProducer("", 1048576, 8, null);
        
        
        for (Bid bid : premiummad.getWebapp().getBids())
        {
            this.mediaApiType.put(bid.getPath(),bid.getType());
            try
            {
                if(!StringUtil.isEmpty(bid.getApiClass())){
                    this.mediaBaseHandlerMap.put(bid.getType(), (MediaBaseHandler)Class.forName(bid.getApiClass()).newInstance());
                }
            }catch (Exception e)
            {
                // TODO 自动生成的 catch 块
                e.printStackTrace();
            }
        }

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

    public Premiummad getPremiummad()
    {
        return premiummad;
    }
    public DSPBaseHandler getDSPHandler(int apiType) {
        return this.dspBaseHandlerMap.get(apiType);
    }
    public int getMediaApiType(String url) {
        return this.mediaApiType.get(url);
    }
}
