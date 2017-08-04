package com.madhouse.ssp;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.madhouse.configuration.Kafka;
import com.madhouse.kafkaclient.producer.KafkaProducer;
import com.madhouse.kafkaclient.util.KafkaCallback;
import com.madhouse.kafkaclient.util.KafkaMessage;
import com.madhouse.resource.ResourceManager;
import com.madhouse.ssp.avro.ClickTrack;
import com.madhouse.ssp.avro.DSPBid;
import com.madhouse.ssp.avro.ImpressionTrack;
import com.madhouse.ssp.avro.MediaBid;
import com.madhouse.ssp.avro.WinNotice;

/**
 * Created by WUJUNFENG on 2017/5/23.
 */
public class LoggerUtil extends KafkaCallback {
    private static final LoggerUtil logger = new LoggerUtil();
    
    private static final Logger premiumMadLogger = LogManager.getLogger("premiummad");
    
    private static ConcurrentHashMap<String, Logger> loggerBaseMap = new ConcurrentHashMap<String, Logger>();
    static{
        List<Kafka.Topic> list=  ResourceManager.getInstance().getConfiguration().getKafka().getTopics();
        for (Kafka.Topic topic : list) {
            loggerBaseMap.put(topic.getType(),LogManager.getLogger(topic.getType()));
        }
    }
    private LoggerUtil() {
    }

    public static LoggerUtil getInstance() {
        return logger;
    }

    public void writeBidLog(KafkaProducer kafkaProducer, DSPBid message) {
        kafkaProducer.sendMessage("adx_dsp", message.toString().getBytes());
    }

    public void writeMediaLog(KafkaProducer kafkaProducer, MediaBid message) {
        kafkaProducer.sendMessage("adx_media", message.toString().getBytes());
    }

    public void writeWinNoticeLog(KafkaProducer kafkaProducer, WinNotice message) {
        kafkaProducer.sendMessage("adx_wn", message.toString().getBytes());
    }

    public void wirteImpressionTrackLog(KafkaProducer kafkaProducer, ImpressionTrack message) {
        kafkaProducer.sendMessage("adx_imp", message.toString().getBytes());
    }

    public void writeClickTrackLog(KafkaProducer kafkaProducer, ClickTrack message) {
        kafkaProducer.sendMessage("adx_click", message.toString().getBytes());
    }

    @Override
    public void onCompletion(KafkaMessage message, Exception e) {
        super.onCompletion(message, e);
    }

    public static Logger getPremiummadlogger() {
        return premiumMadLogger;
    }

    public Logger getBaseLogger(String type) {
        return loggerBaseMap.get(type);
    }

    
    
}
