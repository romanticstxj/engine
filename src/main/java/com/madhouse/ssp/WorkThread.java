package com.madhouse.ssp;

import com.madhouse.cache.*;
import com.madhouse.dsp.DSPBaseHandler;
import com.madhouse.media.MediaBaseHandler;
import com.madhouse.resource.ResourceManager;
import com.madhouse.ssp.avro.*;
import com.madhouse.util.*;
import com.madhouse.util.httpclient.MultiHttpClient;
import com.madhouse.util.httpclient.HttpClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.logging.log4j.Logger;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.CRC32;


/**
 * Created by WUJUNFENG on 2017/5/23.
 */
public class WorkThread {
    private LinkedList<BidHandler> bidHandlers = new LinkedList<>();
    private ExecutorService asyncExecutorService = Executors.newCachedThreadPool();
    private static Logger logger = LoggerUtil.getInstance().getPremiummadlogger();

    private final byte[] image = {  0x47, 0x49, 0x46, 0x38, 0x39, 0x61, 0x01, 0x00, 0x01, 0x00,
                                    (byte)0x80, 0x01, 0x00, 0x00, 0x00, 0x00, (byte)0xff, (byte)0xff, (byte)0xff, 0x21,
                                    (byte)0xf9, 0x04, 0x01, 0x00, 0x00, 0x01, 0x00, 0x2c, 0x00, 0x00,
                                    0x00, 0x00, 0x01, 0x00, 0x01, 0x00, 0x00, 0x02, 0x02, 0x4c, 0x01, 0x00, 0x3b};

    public boolean init() {
        return true;
    }

    public void onImpression(HttpServletRequest req, HttpServletResponse resp) {
        Jedis redisMaster = ResourceManager.getInstance().getJedisPoolMaster().getResource();

        try {
            String impId = req.getParameter("_impid");
            String mediaId = req.getParameter("_mid");
            String plcmtId = req.getParameter("_spid");
            String location = req.getParameter("_loc");
            String ext = req.getParameter("_ext");
            String sign = req.getParameter("_sn");

            //args check
            if (StringUtils.isEmpty(impId) || StringUtils.isEmpty(mediaId) || StringUtils.isEmpty(plcmtId) ||
                    StringUtils.isEmpty(location) || StringUtils.isEmpty(ext) || StringUtils.isEmpty(sign)) {
                resp.setStatus(Constant.StatusCode.BAD_REQUEST);
                logger.warn("Tracking args[{}] check error.", req.getQueryString());
                return;
            }

            ext = new String(StringUtil.urlSafeBase64Decode(ext), "utf-8");
            StringBuilder sb = new StringBuilder()
                    .append(impId)
                    .append(mediaId)
                    .append(plcmtId)
                    .append(ext);

            CRC32 crc32 = new CRC32();
            crc32.update(sb.toString().getBytes("utf-8"));
            String[] exts = ext.split(",");
            if (Long.parseLong(sign) != crc32.getValue() || exts == null || exts.length < 6) {
                resp.setStatus(Constant.StatusCode.BAD_REQUEST);
                logger.warn("Tracking args[{}] check error.", req.getQueryString());
                return;
            }

            logger.info("Impression tracking: {}", req.getQueryString());

            String policyId = exts[0];
            String dspId = exts[1];

            ImpressionTrack.Builder impressionTrack = ImpressionTrack.newBuilder();
            impressionTrack.setTime(System.currentTimeMillis());
            impressionTrack.setIp(HttpUtil.getRealIp(req));
            impressionTrack.setUa(HttpUtil.getUserAgent(req));
            impressionTrack.setBid(StringUtil.toString(req.getParameter("_bid")));
            impressionTrack.setCid(StringUtil.toString(req.getParameter("_cid")));
            impressionTrack.setBidtime(IdWoker.getCreateTimeMillis(Long.parseLong(impId)));

            int invalidType = 0;
            int trackingExpiredTime = ResourceManager.getInstance().getConfiguration().getWebapp().getTrackingExpiredTime();
            if ((System.currentTimeMillis() - impressionTrack.getBidtime()) / 1000 > trackingExpiredTime) {
                invalidType |= Constant.InvalidType.EXPIRED;
            }

            long count = redisMaster.incr(String.format(Constant.CommonKey.IMP_RECORD, impId));
            if (count > 1) {
                invalidType |= Constant.InvalidType.DUPLICATE;
            }

            impressionTrack.setInvalid(invalidType);
            redisMaster.expire(String.format(Constant.CommonKey.IMP_RECORD, impId), 86400);

            impressionTrack.setImpid(impId);
            impressionTrack.setMediaid(Long.parseLong(mediaId));
            impressionTrack.setAdspaceid(Long.parseLong(plcmtId));
            impressionTrack.setPolicyid(Long.parseLong(policyId));
            impressionTrack.setLocation(location);
            impressionTrack.setExt(ext);
            impressionTrack.setDspid(Long.parseLong(dspId));

            if (Integer.parseInt(exts[2]) == Constant.BidType.CPM) {
                impressionTrack.setIncome(Integer.parseInt(exts[3]));
            } else {
                impressionTrack.setIncome(0);
            }

            if (Integer.parseInt(exts[4]) == Constant.BidType.CPM) {
                impressionTrack.setCost(Integer.parseInt(exts[5]));
            } else {
                impressionTrack.setCost(0);
            }

            impressionTrack.setStatus(Constant.StatusCode.OK);
            LoggerUtil.getInstance().wirteImpressionTrackLog(ResourceManager.getInstance().getKafkaProducer(), impressionTrack);

            resp.getOutputStream().write(this.image);
            resp.setContentType("image/gif");
            resp.setContentLength(this.image.length);
            resp.setStatus(impressionTrack.getStatus());

        } catch (Exception ex) {
            logger.error(ex.toString());
            resp.setStatus(Constant.StatusCode.BAD_REQUEST);
        } finally {
            if (redisMaster != null) {
                redisMaster.close();
            }
        }
    }

    public void onClick(HttpServletRequest req, HttpServletResponse resp) {
        Jedis redisMaster = ResourceManager.getInstance().getJedisPoolMaster().getResource();

        try {
            String impId = req.getParameter("_impid");
            String mediaId = req.getParameter("_mid");
            String plcmtId = req.getParameter("_spid");
            String location = req.getParameter("_loc");
            String ext = req.getParameter("_ext");
            String sign = req.getParameter("_sn");

            //args check
            if (StringUtils.isEmpty(impId) || StringUtils.isEmpty(mediaId) || StringUtils.isEmpty(plcmtId) ||
                    StringUtils.isEmpty(location) || StringUtils.isEmpty(ext) || StringUtils.isEmpty(sign)) {
                resp.setStatus(Constant.StatusCode.BAD_REQUEST);
                logger.warn("Tracking args[{}] check error.", req.getQueryString());
                return;
            }

            ext = new String(StringUtil.urlSafeBase64Decode(ext), "utf-8");
            StringBuilder sb = new StringBuilder()
                    .append(impId)
                    .append(mediaId)
                    .append(plcmtId)
                    .append(ext);

            CRC32 crc32 = new CRC32();
            crc32.update(sb.toString().getBytes("utf-8"));
            String[] exts = ext.split(",");
            if (Long.parseLong(sign) != crc32.getValue() || exts == null || exts.length < 6) {
                resp.setStatus(Constant.StatusCode.BAD_REQUEST);
                logger.warn("Tracking args[{}] check error.", req.getQueryString());
                return;
            }

            logger.info("Click tracking: {}", req.getQueryString());

            String policyId = exts[0];
            String dspId = exts[1];

            ClickTrack.Builder clickTrack = ClickTrack.newBuilder();
            clickTrack.setTime(System.currentTimeMillis());
            clickTrack.setIp(HttpUtil.getRealIp(req));
            clickTrack.setUa(HttpUtil.getUserAgent(req));
            clickTrack.setBid(StringUtil.toString(req.getParameter("_bid")));
            clickTrack.setCid(StringUtil.toString(req.getParameter("_cid")));
            clickTrack.setBidtime(IdWoker.getCreateTimeMillis(Long.parseLong(impId)));

            int invalidType = 0;
            int trackingExpiredTime = ResourceManager.getInstance().getConfiguration().getWebapp().getTrackingExpiredTime();
            if ((System.currentTimeMillis() - clickTrack.getBidtime()) / 1000 > trackingExpiredTime) {
                invalidType |= Constant.InvalidType.EXPIRED;
            }

            long count = redisMaster.incr(String.format(Constant.CommonKey.CLK_RECORD, impId));
            if (count > 1) {
                invalidType |= Constant.InvalidType.DUPLICATE;
            }

            clickTrack.setInvalid(invalidType);
            redisMaster.expire(String.format(Constant.CommonKey.CLK_RECORD, impId), 86400);

            clickTrack.setImpid(impId);
            clickTrack.setMediaid(Long.parseLong(mediaId));
            clickTrack.setAdspaceid(Long.parseLong(plcmtId));
            clickTrack.setPolicyid(Long.parseLong(policyId));
            clickTrack.setLocation(location);
            clickTrack.setExt(ext);
            clickTrack.setDspid(Long.parseLong(dspId));

            if (Integer.parseInt(exts[2]) == Constant.BidType.CPC) {
                clickTrack.setIncome(Integer.parseInt(exts[3]));
            } else {
                clickTrack.setIncome(0);
            }

            if (Integer.parseInt(exts[4]) == Constant.BidType.CPC) {
                clickTrack.setCost(Integer.parseInt(exts[5]));
            } else {
                clickTrack.setCost(0);
            }

            clickTrack.setStatus(Constant.StatusCode.OK);
            LoggerUtil.getInstance().writeClickTrackLog(ResourceManager.getInstance().getKafkaProducer(), clickTrack);

            String url = req.getParameter("_url");
            if (!StringUtils.isEmpty(url)) {
                resp.setHeader("Location", URLDecoder.decode(url, "utf-8"));
                clickTrack.setStatus(Constant.StatusCode.REDIRECT);
            } else {
                resp.getOutputStream().write(this.image);
                resp.setContentType("image/gif");
                resp.setContentLength(this.image.length);
                clickTrack.setStatus(Constant.StatusCode.OK);
            }

            resp.setStatus(clickTrack.getStatus());
        } catch (Exception ex) {
            logger.error(ex.toString());
            resp.setStatus(Constant.StatusCode.BAD_REQUEST);
        } finally {
            if (redisMaster != null) {
                redisMaster.close();
            }
        }
    }

    private BidHandler getBidHandler() {
        BidHandler handler = this.bidHandlers.pollLast();
        if (handler == null) {
            handler = new BidHandler();
        }

        return handler;
    }

    private void releaseBidHandler(BidHandler handler) {
        this.bidHandlers.add(handler);
    }

    public void onBid(HttpServletRequest req, HttpServletResponse resp) {
        try {
            long tstart = System.currentTimeMillis();

            //get media request handler
            MediaBaseHandler mediaBaseHandler = ResourceManager.getInstance().getMediaApiType(req.getRequestURI());
            if (mediaBaseHandler == null) {
                logger.error("get media handler error.");
                resp.setStatus(Constant.StatusCode.BAD_REQUEST);
                return;
            }

            MediaBidMetaData mediaBidMetaData = new MediaBidMetaData();

            //parse media request
            if (!mediaBaseHandler.parseRequest(req, mediaBidMetaData, resp)) {
                logger.error("parse media request error.");
                return;
            }

            List<BidHandler> handlers = new LinkedList<>();
            CountDownLatch latch = new CountDownLatch(mediaBidMetaData.getMediaBids().size());

            for (MediaBid.Builder mediaBid : mediaBidMetaData.getMediaBids()) {
                mediaBid.setImpid(ResourceManager.getInstance().nextId());
                mediaBid.setIp(HttpUtil.getRealIp(req));
                mediaBid.setUa(HttpUtil.getUserAgent(req));
                mediaBid.setTime(System.currentTimeMillis());
                mediaBid.setBidfloor(0);
                mediaBid.setBidtype(Constant.BidType.CPM);
                mediaBid.setLocation(Constant.LOCATION_UNKNOWN);
                mediaBid.setStatus(Constant.StatusCode.NO_CONTENT);

                MediaBidMetaData.BidMetaData bidMetaData = new MediaBidMetaData.BidMetaData();
                mediaBidMetaData.getBidMetaDataMap().put(mediaBid.getImpid(), bidMetaData);

                BidHandler handler = this.getBidHandler();
                if (handler.init(latch, mediaBid, bidMetaData)) {
                    this.asyncExecutorService.submit(handler);
                }

                handlers.add(handler);
            }

            latch.await();

            for (BidHandler handler : handlers) {
                this.releaseBidHandler(handler);
            }

            mediaBaseHandler.packageResponse(mediaBidMetaData, resp);
            logger.info("media bid cost time: {}ms", System.currentTimeMillis() - tstart);
        } catch (Exception ex) {
            resp.setStatus(Constant.StatusCode.NO_CONTENT);
            logger.error(ex.toString());
        }
    }
}