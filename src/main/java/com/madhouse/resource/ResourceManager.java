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

import org.apache.commons.lang3.tuple.Pair;
import sun.security.jca.GetInstance;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by WUJUNFENG on 2017/5/23.
 */
public class ResourceManager {
    private KafkaProducer kafkaProducer;
    private ConcurrentHashMap<Integer, DSPBaseHandler> dspBaseHandlerMap = new ConcurrentHashMap<Integer, DSPBaseHandler>();
    private ConcurrentHashMap<Integer, MediaBaseHandler> mediaBaseHandlerMap = new ConcurrentHashMap<Integer, MediaBaseHandler>();
    private ConcurrentHashMap<String, Integer> mediaApiType = new ConcurrentHashMap<String, Integer>();
    private final ArrayList<Pair<Long, String>> iptables = this.loadLocations();
    private final Premiummad premiummad = JSON.parseObject(ObjectUtils.ReadFile(ResourceManager.class.getClassLoader().getResource("config.json").getPath()), Premiummad.class);

    private static final ResourceManager resourceManager = new ResourceManager();
    private ResourceManager(){};
    public static ResourceManager getInstance() {
        return resourceManager;
    }

    public ArrayList loadLocations() {
        try {
            ArrayList<Pair<Long, String>> iptables = new ArrayList<>();
            File file = new File(ResourceManager.class.getClassLoader().getResource("locations.dat").getPath());

            if (file.isFile() && file.exists()) {
                InputStreamReader reader = new InputStreamReader(new FileInputStream(file), "utf-8");
                BufferedReader bufferedReader = new BufferedReader(reader);

                String text = null;
                while((text = bufferedReader.readLine()) != null) {
                    String[] var1 = text.split(",");
                    if (var1.length >= 3) {
                        String[] var2 = var1[0].split(".");
                        if (var2.length >= 4) {
                            Long addr = (Long.parseLong(var2[0]) << 24) | (Long.parseLong(var2[1]) << 16 ) | (Long.parseLong(var2[2]) << 8) | Long.parseLong(var2[3]);
                            iptables.add(Pair.of(addr, var1[2]));
                        }
                    }
                }

                reader.close();
                return iptables.isEmpty() ? null : iptables;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean init()
    {
        this.kafkaProducer = new KafkaProducer("", 1048576, 8, null);
        this.loadLocations();

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
        if (this.iptables == null || this.iptables.isEmpty()) {
            return null;
        }

        String[] ips = ip.split(".");
        if (ips.length >= 4) {
            Long addr = (Long.parseLong(ips[0]) << 24) | (Long.parseLong(ips[1]) << 16 ) | (Long.parseLong(ips[2]) << 8) | Long.parseLong(ips[3]);

            int start = 0;
            int end = this.iptables.size();
            while (end - start > 1) {
                int mid = (start + end) / 2;
                if (this.iptables.get(mid).getLeft() > addr) {
                    end = mid;
                } else {
                    start = mid;
                }
            }

            return this.iptables.get(start).getRight();
        }

        return null;
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
