package com.madhouse.ssp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecordBase;
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
            loggerBaseMap.put(topic.getType(),LogManager.getLogger(topic.getTopic()));
        }
    }
    private LoggerUtil() {
    }

    public static LoggerUtil getInstance() {
        return logger;
    }

    public void writeBidLog(KafkaProducer kafkaProducer, DSPBid.Builder message) {
        try {
            sendMessage(kafkaProducer, getAvroBytes(message.build()),ResourceManager.getInstance().getConfiguration().getKafka().getTopic(Constant.KafkaTopicType.KAFKA_DSP_BID));
        } catch (Exception e) {
            premiumMadLogger.error(e.toString());
        }
    }

    public void writeMediaLog(KafkaProducer kafkaProducer, MediaBid.Builder message) {
        try {
            sendMessage(kafkaProducer, getAvroBytes(message.build()),ResourceManager.getInstance().getConfiguration().getKafka().getTopic(Constant.KafkaTopicType.KAFKA_MEDIA_BID));
        } catch (Exception e) {
            premiumMadLogger.error(e.toString());
        }
    }

    public void writeWinNoticeLog(KafkaProducer kafkaProducer, WinNotice.Builder message) {
        try {
            sendMessage(kafkaProducer, getAvroBytes(message.build()),ResourceManager.getInstance().getConfiguration().getKafka().getTopic(Constant.KafkaTopicType.KAFKA_WIN_NOTICE));
        } catch (Exception e) {
            premiumMadLogger.error(e.toString());
        }
    }

    public void wirteImpressionTrackLog(KafkaProducer kafkaProducer, ImpressionTrack.Builder message) {
        try {
            sendMessage(kafkaProducer, getAvroBytes(message.build()),ResourceManager.getInstance().getConfiguration().getKafka().getTopic(Constant.KafkaTopicType.KAFKA_IMPRESSION));
        } catch (Exception e) {
            premiumMadLogger.error(e.toString());
        }
    }

    public void writeClickTrackLog(KafkaProducer kafkaProducer, ClickTrack.Builder message) {
        try {
            sendMessage(kafkaProducer, getAvroBytes(message.build()),ResourceManager.getInstance().getConfiguration().getKafka().getTopic(Constant.KafkaTopicType.KAFKA_CLICK));
        } catch (Exception e) {
            premiumMadLogger.error(e.toString());
        }
    }

    @Override
    public void onCompletion(KafkaMessage message, Exception e) {
        try {
            if(null != message && null !=e){
                String kafkaType = ResourceManager.getInstance().getConfiguration().getKafka().getkafka(message.topic);
                String information = null;
                Decoder decoder= DecoderFactory.get().binaryDecoder(message.message,null);
                switch (kafkaType) {
                    case Constant.KafkaTopicType.KAFKA_CLICK:
                        DatumReader<ClickTrack> clickTrackReader= new SpecificDatumReader<ClickTrack>(ClickTrack.class);
                        ClickTrack clickTrack = clickTrackReader.read(null,decoder);
                        information = clickTrack.toString();
                        break;
                    case Constant.KafkaTopicType.KAFKA_DSP_BID:
                        DatumReader<DSPBid> dspBidReader = new SpecificDatumReader<DSPBid>(DSPBid.class);
                        DSPBid dspBid = dspBidReader.read(null,decoder);
                        information = dspBid.toString();
                        break;
                    case Constant.KafkaTopicType.KAFKA_IMPRESSION:
                        DatumReader<ImpressionTrack> impressionTrackReader = new SpecificDatumReader<ImpressionTrack>(ImpressionTrack.class);
                        ImpressionTrack impressionTrack = impressionTrackReader.read(null,decoder);
                        information = impressionTrack.toString();
                        break;
                    case Constant.KafkaTopicType.KAFKA_MEDIA_BID:
                        DatumReader<MediaBid> mediaBidReader = new SpecificDatumReader<MediaBid>(MediaBid.class);
                        MediaBid mediaBid = mediaBidReader.read(null,decoder);
                        information = mediaBid.toString();
                        break;
                    case Constant.KafkaTopicType.KAFKA_WIN_NOTICE:
                        DatumReader<WinNotice> readerwinNoticeReader = new SpecificDatumReader<WinNotice>(WinNotice.class);
                        WinNotice winNotice = readerwinNoticeReader.read(null,decoder);
                        information = winNotice.toString();
                        break;
                }
                if(null != information){
                    Logger logger= loggerBaseMap.get(kafkaType);
                    logger.info(information);
                }
                premiumMadLogger.error(e.toString());
            }
            super.onCompletion(message, e);
        } catch (Exception e1) {
            premiumMadLogger.error(e1.toString());
        }
    }

    public static Logger getPremiummadlogger() {
        return premiumMadLogger;
    }

    public Logger getBaseLogger(String type) {
        return loggerBaseMap.get(type);
    }
    
    private void sendMessage(KafkaProducer kafkaProducer,byte[] message,String topic ) {
        kafkaProducer.sendMessage(topic, message);
    }
    public byte[] getAvroBytes(SpecificRecordBase record) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        SpecificDatumWriter writer = new SpecificDatumWriter(record.getSchema());
        Encoder encoder = EncoderFactory.get().binaryEncoder(out, null);
        writer.write(record, encoder);
        encoder.flush();
        out.close();
        return out.toByteArray();
    }
}
