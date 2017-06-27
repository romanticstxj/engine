package com.madhouse.ssp;

import com.alibaba.fastjson.JSON;
import com.google.protobuf.GeneratedMessage;
import com.madhouse.configuration.Topic;
import com.madhouse.dsp.DSPBaseHandler;
import com.madhouse.kafkaclient.producer.KafkaProducer;
import com.madhouse.kafkaclient.util.KafkaCallback;
import com.madhouse.kafkaclient.util.KafkaMessage;
import com.madhouse.resource.ResourceManager;

import sun.security.jca.GetInstance;

import java.util.List;




import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by WUJUNFENG on 2017/5/23.
 */
public class LoggerUtil extends KafkaCallback {
    private static final LoggerUtil logger = new LoggerUtil();
    
    private static final Logger premiumMadLogger = LogManager.getLogger("premiummad");
    
    private static ConcurrentHashMap<String, Logger> loggerBaseMap = new ConcurrentHashMap<String, Logger>();
    static{
        List<Topic> list=  ResourceManager.getInstance().getPremiummad().getKafka().getTopics();
        for (Topic topic : list) {
            loggerBaseMap.put(topic.getType(),LogManager.getLogger(topic.getType()));
        }
    }
    private LoggerUtil() {
    }

    public static LoggerUtil getInstance() {
        return logger;
    }

    public void writeBidLog(KafkaProducer kafkaProducer, PremiumMADDataModel.DSPBid message) {
        kafkaProducer.sendMessage("adx_dsp", message.toByteArray());
    }

    public void writeMediaLog(KafkaProducer kafkaProducer, PremiumMADDataModel.MediaBid message) {
        kafkaProducer.sendMessage("adx_media", message.toByteArray());
    }

    public void writeWinNoticeLog(KafkaProducer kafkaProducer, PremiumMADDataModel.WinNotice message) {
        kafkaProducer.sendMessage("adx_wn", message.toByteArray());
    }

    public void wirteImpressionTrackLog(KafkaProducer kafkaProducer, PremiumMADDataModel.ImpressionTrack message) {
        kafkaProducer.sendMessage("adx_imp", message.toByteArray());
    }

    public void writeClickTrackLog(KafkaProducer kafkaProducer, PremiumMADDataModel.ClickTrack message) {
        kafkaProducer.sendMessage("adx_click", message.toByteArray());
    }

    @Override
    public void onSendError(List<KafkaMessage> messages) {
        for (KafkaMessage message :messages) {

        }
    }
    
    public static Logger getPremiummadlogger() {
        return premiumMadLogger;
    }

    public Logger getBaseLogger(String type) {
        return loggerBaseMap.get(type);
    }

    
    
}
