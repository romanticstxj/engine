package com.madhouse.resource;

import java.util.concurrent.ConcurrentHashMap;

import com.madhouse.configuration.Redis;
import com.madhouse.dsp.madhouse.MADMaxHandler;
import com.madhouse.dsp.madrtb.MADRTBHandler;
import com.madhouse.dsp.proctergamble.ProcterGambleHandler;
import com.madhouse.dsp.reachMax.ReachMaxHandler;
import com.madhouse.dsp.vamaker.VamakerHandler;
import com.madhouse.ssp.Constant;
import com.madhouse.util.IPLocation;
import com.madhouse.util.StringUtil;

import com.madhouse.util.httpclient.HttpClient;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.madhouse.configuration.Bid;
import com.madhouse.configuration.Configuration;
import com.madhouse.dsp.DSPBaseHandler;
import com.madhouse.kafkaclient.producer.KafkaProducer;
import com.madhouse.media.MediaBaseHandler;
import com.madhouse.ssp.LoggerUtil;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import redis.clients.jedis.JedisPool;

/**
 * Created by WUJUNFENG on 2017/5/23.
 */
public class ResourceManager {
    private KafkaProducer kafkaProducer;
    private JedisPool jedisPoolMaster;
    private JedisPool jedisPoolSlave;

    private ConcurrentHashMap<Long, HttpClient> httpClientMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Integer, DSPBaseHandler> dspBaseHandlerMap = new ConcurrentHashMap<Integer, DSPBaseHandler>();
    private ConcurrentHashMap<String, MediaBaseHandler> mediaApiType = new ConcurrentHashMap<String, MediaBaseHandler>();

    private final IPLocation ipTables = new IPLocation(ResourceManager.class.getClassLoader().getResource("locations.dat").getPath());
    private final Configuration configuration = JSON.parseObject(StringUtil.readFile(ResourceManager.class.getResourceAsStream("/config.json")), Configuration.class);

    private ResourceManager(){};
    public static ResourceManager getInstance() {
        return resourceManager;
    }
    private static final ResourceManager resourceManager = new ResourceManager();

    public boolean init() {
        {
            this.dspBaseHandlerMap.clear();
            this.dspBaseHandlerMap.put(Constant.DSPApiType.MADRTB, new MADRTBHandler());
            this.dspBaseHandlerMap.put(Constant.DSPApiType.MADAPI, new MADMaxHandler());
            this.dspBaseHandlerMap.put(Constant.DSPApiType.PG, new ProcterGambleHandler());
            this.dspBaseHandlerMap.put(Constant.DSPApiType.VAMAKER, new VamakerHandler());
            this.dspBaseHandlerMap.put(Constant.DSPApiType.REACHMAX, new ReachMaxHandler());
        }

        {
            GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
            poolConfig.setMinIdle(this.configuration.getRedis().getMinIdle());
            poolConfig.setMaxIdle(this.configuration.getRedis().getMaxIdle());
            poolConfig.setMaxTotal(this.configuration.getRedis().getMaxTotal());

            {
                Redis.Config config = this.configuration.getRedis().getMaster();
                this.jedisPoolMaster = new JedisPool(poolConfig, config.getHost(), config.getPort());
            }

            {
                Redis.Config config = this.configuration.getRedis().getSlave();
                this.jedisPoolSlave = new JedisPool(poolConfig, config.getHost(), config.getPort());
            }
        }

        this.kafkaProducer = new KafkaProducer(this.configuration.getKafka().getBrokers(), 8, null);

        for (Bid bid : configuration.getWebapp().getBids()) {
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

    public Configuration getConfiguration() {
        return configuration;
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

    public HttpClient getHttpClient(long dspId) {
        HttpClient client = this.httpClientMap.get(dspId);

        if (client == null) {
            synchronized (this) {
                client = new HttpClient();
                this.httpClientMap.put(dspId, client);
            }
        }

        return client;
    }
}
