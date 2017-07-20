package com.madhouse.resource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import com.madhouse.configuration.Redis;
import com.madhouse.dsp.madhouse.MADMaxHandler;
import com.madhouse.dsp.madrtb.MADRTBHandler;
import com.madhouse.dsp.proctergamble.ProcterGambleHandler;
import com.madhouse.dsp.vamaker.VamakerHandler;
import com.madhouse.ssp.Constant;
import com.madhouse.util.IPLocation;
import com.madhouse.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.alibaba.fastjson.JSON;
import com.madhouse.configuration.Bid;
import com.madhouse.configuration.Premiummad;
import com.madhouse.dsp.DSPBaseHandler;
import com.madhouse.kafkaclient.producer.KafkaProducer;
import com.madhouse.media.MediaBaseHandler;
import com.madhouse.ssp.LoggerUtil;
import com.madhouse.util.ObjectUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.JedisPool;

/**
 * Created by WUJUNFENG on 2017/5/23.
 */
public class ResourceManager {
    private KafkaProducer kafkaProducer;
    private JedisPool jedisPoolMaster;
    private JedisPool jedisPoolSlave;

    private ConcurrentHashMap<Integer, DSPBaseHandler> dspBaseHandlerMap = new ConcurrentHashMap<Integer, DSPBaseHandler>();
    private ConcurrentHashMap<String, MediaBaseHandler> mediaApiType = new ConcurrentHashMap<String, MediaBaseHandler>();

    private final IPLocation ipTables = new IPLocation(ResourceManager.class.getClassLoader().getResource("locations.dat").getPath());
    private final Premiummad premiummad = JSON.parseObject(StringUtil.readFile(ResourceManager.class.getClassLoader().getResource("config.json").getPath()), Premiummad.class);

    private static final ResourceManager resourceManager = new ResourceManager();
    private ResourceManager(){};
    public static ResourceManager getInstance() {
        return resourceManager;
    }

    public boolean init()
    {
        {
            this.dspBaseHandlerMap.clear();
            this.dspBaseHandlerMap.put(Constant.DSPApiType.MADRTB, new MADRTBHandler());
            this.dspBaseHandlerMap.put(Constant.DSPApiType.MADAPI, new MADMaxHandler());
            this.dspBaseHandlerMap.put(Constant.DSPApiType.PG, new ProcterGambleHandler());
            this.dspBaseHandlerMap.put(Constant.DSPApiType.VAMAKER, new VamakerHandler());
        }

        {
            GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
            poolConfig.setMinIdle(this.premiummad.getRedis().getMinIdle());
            poolConfig.setMaxIdle(this.premiummad.getRedis().getMaxIdle());
            poolConfig.setMaxTotal(this.premiummad.getRedis().getMaxTotal());

            {
                Redis.Config config = this.premiummad.getRedis().getMaster();
                this.jedisPoolMaster = new JedisPool(poolConfig, config.getHost(), config.getPort());
            }

            {
                Redis.Config config = this.premiummad.getRedis().getSlave();
                this.jedisPoolSlave = new JedisPool(poolConfig, config.getHost(), config.getPort());
            }
        }

        this.kafkaProducer = new KafkaProducer(this.premiummad.getKafka().getBrokers(), 1048576, 8, null);
        for (Bid bid : premiummad.getWebapp().getBids())
        {
			if (!StringUtils.isEmpty(bid.getClassName())) {
				try {
					this.mediaApiType.put(bid.getPath(),(MediaBaseHandler) Class.forName(bid.getClassName()).newInstance());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
        }

        return this.kafkaProducer.start(LoggerUtil.getInstance());
    }

    public KafkaProducer getKafkaProducer() {
        return this.kafkaProducer;
    }

    public Premiummad getPremiummad() {
        return this.premiummad;
    }

    public DSPBaseHandler getDSPHandler(int apiType) {
        return this.dspBaseHandlerMap.get(apiType);
    }

    public MediaBaseHandler getMediaApiType(String url) {
        return this.mediaApiType.get(url);
    }

    public JedisPool getJedisPoolMaster() {
        return jedisPoolMaster;
    }

    public void setJedisPoolMaster(JedisPool jedisPoolMaster) {
        this.jedisPoolMaster = jedisPoolMaster;
    }

    public JedisPool getJedisPoolSlave() {
        return jedisPoolSlave;
    }

    public void setJedisPoolSlave(JedisPool jedisPoolSlave) {
        this.jedisPoolSlave = jedisPoolSlave;
    }

    public void setKafkaProducer(KafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    public String getLocation(String ip) {
        return this.ipTables.getLocation(ip);
    }
}
