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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.CRC32;


/**
 * Created by WUJUNFENG on 2017/5/23.
 */
public class WorkThread {
    private static Logger logger = LoggerUtil.getInstance().getPremiummadlogger();

    private MultiHttpClient multiHttpClient = new MultiHttpClient();
    private Map<Long, HttpClient> httpClientMap = new ConcurrentHashMap<>();

    private ExecutorService winNoticeService = Executors.newCachedThreadPool();

    private final byte[] image = {  0x47, 0x49, 0x46, 0x38, 0x39, 0x61, 0x01, 0x00, 0x01, 0x00,
                                    (byte)0x80, 0x01, 0x00, 0x00, 0x00, 0x00, (byte)0xff, (byte)0xff, (byte)0xff, 0x21,
                                    (byte)0xf9, 0x04, 0x01, 0x00, 0x00, 0x01, 0x00, 0x2c, 0x00, 0x00,
                                    0x00, 0x00, 0x01, 0x00, 0x01, 0x00, 0x00, 0x02, 0x02, 0x4c, 0x01, 0x00, 0x3b};

    public boolean init() {
        return true;
    }

    public HttpClient getHttpClient(long dspId) {
        HttpClient client = this.httpClientMap.get(dspId);
        if (client == null) {
            client = new HttpClient();
            this.httpClientMap.put(dspId, client);
        }

        return client;
    }

    public void onImpression(HttpServletRequest req, HttpServletResponse resp) {
        Jedis redisMaster = ResourceManager.getInstance().getJedisPoolMaster().getResource();

        try {
            String impId = req.getParameter("_impid");
            String mediaId = req.getParameter("_mid");
            String plcmtId = req.getParameter("_pid");
            String location = req.getParameter("_loc");
            String ext = req.getParameter("_ext");
            String bidTime = req.getParameter("_ts");
            String sign = req.getParameter("_sn");

            //args check
            if (StringUtils.isEmpty(impId) || StringUtils.isEmpty(mediaId) || StringUtils.isEmpty(plcmtId) ||
                    StringUtils.isEmpty(location) || StringUtils.isEmpty(ext) || StringUtils.isEmpty(sign)) {
                resp.setStatus(Constant.StatusCode.BAD_REQUEST);
                return;
            }

            ext = new String(StringUtil.urlSafeBase64Decode(ext), "utf-8");
            StringBuilder sb = new StringBuilder()
                    .append(impId)
                    .append(mediaId)
                    .append(plcmtId)
                    .append(ext)
                    .append(bidTime);

            CRC32 crc32 = new CRC32();
            crc32.update(sb.toString().getBytes("utf-8"));
            String[] exts = ext.split(",");
            if (Long.parseLong(sign) != crc32.getValue() || exts == null || exts.length < 6) {
                resp.setStatus(Constant.StatusCode.BAD_REQUEST);
                return;
            }

            String policyId = exts[0];
            String dspId = exts[1];

            ImpressionTrack.Builder impressionTrack = ImpressionTrack.newBuilder();
            int expiredTime = ResourceManager.getInstance().getConfiguration().getWebapp().getExpiredTime();
            if ((System.currentTimeMillis() - Long.parseLong(bidTime)) / 1000 > expiredTime) {
                impressionTrack.setInvalid(Constant.InvalidType.EXPIRED);
            }

            String ret = redisMaster.set(String.format(Constant.CommonKey.IMP_RECORD, impId), "1", "NX", "EX", expiredTime);
            if (StringUtils.isEmpty(ret) || ret.compareTo("OK") != 0) {
                impressionTrack.setInvalid(Constant.InvalidType.DUPLICATE);
            }

            impressionTrack.setTime(System.currentTimeMillis());
            impressionTrack.setIp(HttpUtil.getRealIp(req));
            impressionTrack.setUa(HttpUtil.getUserAgent(req));
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
            String plcmtId = req.getParameter("_pid");
            String location = req.getParameter("_loc");
            String ext = req.getParameter("_ext");
            String bidTime = req.getParameter("_ts");
            String sign = req.getParameter("_sn");

            //args check
            if (StringUtils.isEmpty(impId) || StringUtils.isEmpty(mediaId) || StringUtils.isEmpty(plcmtId) ||
                    StringUtils.isEmpty(location) || StringUtils.isEmpty(ext) || StringUtils.isEmpty(sign)) {
                resp.setStatus(Constant.StatusCode.BAD_REQUEST);
                return;
            }

            ext = new String(StringUtil.urlSafeBase64Decode(ext), "utf-8");
            StringBuilder sb = new StringBuilder()
                    .append(impId)
                    .append(mediaId)
                    .append(plcmtId)
                    .append(ext)
                    .append(bidTime);

            CRC32 crc32 = new CRC32();
            crc32.update(sb.toString().getBytes("utf-8"));
            String[] exts = ext.split(",");
            if (Long.parseLong(sign) != crc32.getValue() || exts == null || exts.length < 6) {
                resp.setStatus(Constant.StatusCode.BAD_REQUEST);
                return;
            }

            String policyId = exts[0];
            String dspId = exts[1];

            ClickTrack.Builder clickTrack = ClickTrack.newBuilder();
            int expiredTime = ResourceManager.getInstance().getConfiguration().getWebapp().getExpiredTime();
            if ((System.currentTimeMillis() - Long.parseLong(bidTime)) / 1000 > expiredTime) {
                clickTrack.setInvalid(Constant.InvalidType.EXPIRED);
            }

            String ret = redisMaster.set(String.format(Constant.CommonKey.CLK_RECORD, impId), "1", "NX", "EX", expiredTime);
            if (StringUtils.isEmpty(ret) || ret.compareTo("OK") != 0) {
                clickTrack.setInvalid(Constant.InvalidType.DUPLICATE);
            }

            clickTrack.setTime(System.currentTimeMillis());
            clickTrack.setIp(HttpUtil.getRealIp(req));
            clickTrack.setUa(HttpUtil.getUserAgent(req));
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

    public void onBid(HttpServletRequest req, HttpServletResponse resp) {

        long t1 = System.currentTimeMillis();
        Jedis redisMaster = ResourceManager.getInstance().getJedisPoolMaster().getResource();

        try {
            //get media request handler
            MediaBaseHandler mediaBaseHandler = ResourceManager.getInstance().getMediaApiType(req.getRequestURI());
            if (mediaBaseHandler == null) {
                logger.error("get media hanlder error.");
                resp.setStatus(Constant.StatusCode.BAD_REQUEST);
                return;
            }

            MediaBidMetaData mediaBidMetaData = new MediaBidMetaData();
            MediaBid.Builder mediaBid = MediaBid.newBuilder();

            //init mediaBid object
            mediaBid.setImpid(StringUtil.getUUID());
            mediaBid.setIp(HttpUtil.getRealIp(req));
            mediaBid.setUa(HttpUtil.getUserAgent(req));
            mediaBid.setTime(System.currentTimeMillis());
            mediaBid.setStatus(Constant.StatusCode.BAD_REQUEST);

            mediaBidMetaData.setMediaBidBuilder(mediaBid);

            //parse media request
            if (!mediaBaseHandler.parseRequest(req, mediaBidMetaData, resp)) {
                logger.error("parse media request error.");
                return;
            }

            mediaBid.setStatus(Constant.StatusCode.NO_CONTENT);
            MediaRequest.Builder mediaRequest = mediaBid.getRequestBuilder();

            //get placement metadata
            PlcmtMetaData plcmtMetaData = CacheManager.getInstance().getPlcmtMetaData(mediaRequest.getAdspacekey());
            if (plcmtMetaData == null) {
                logger.error("media adspace mapping error.");
                resp.setStatus(Constant.StatusCode.NOT_ALLOWED);
                return;
            }

            MediaMetaData mediaMetaData = CacheManager.getInstance().getMediaMetaData(plcmtMetaData.getMediaId());
            if (mediaMetaData == null) {
                logger.error("get media metadata error.");
                resp.setStatus(Constant.StatusCode.NOT_ALLOWED);
                return;
            }

            if (mediaMetaData.getStatus() <= 0 || plcmtMetaData.getStatus() <= 0) {
                logger.warn("media or adspace is not allowed.");
                resp.setStatus(Constant.StatusCode.NOT_ALLOWED);
                return;
            }

            //init mediaid, adspaceid
            mediaRequest.setMediaid(mediaMetaData.getId());
            mediaRequest.setAdspaceid(plcmtMetaData.getId());

            //init user ip
            if (!mediaRequest.hasIp()) {
                mediaRequest.setIp(mediaBid.getIp());
            }

            //init user ua
            if (!mediaRequest.hasUa()) {
                mediaRequest.setUa(mediaBid.getUa());
            }

            //init location
            String location = ResourceManager.getInstance().getLocation(mediaRequest.getIp());
            if (StringUtils.isEmpty(location)) {
                logger.error("get user's location error.");
                resp.setStatus(Constant.StatusCode.NOT_ALLOWED);
                return;
            }

            mediaBid.setLocation(location);
            //bidfloor, bidtype
            mediaBid.setBidfloor(plcmtMetaData.getBidFloor());
            mediaBid.setBidtype(plcmtMetaData.getBidType());

            MediaBidMetaData.TrackingParam trackingParam = new MediaBidMetaData.TrackingParam();
            mediaBidMetaData.setTrackingParam(trackingParam);

            trackingParam.setImpId(mediaBid.getImpid());
            trackingParam.setMediaId(plcmtMetaData.getMediaId());
            trackingParam.setAdspaceId(plcmtMetaData.getId());
            trackingParam.setLocation(mediaBid.getLocation());
            trackingParam.setBidTime(System.currentTimeMillis());

            AuctionPriceInfo mediaIncome = new AuctionPriceInfo();
            mediaIncome.setBidPrice(plcmtMetaData.getBidFloor());
            mediaIncome.setBidType(plcmtMetaData.getBidType());
            trackingParam.setMediaIncome(mediaIncome);

            //get block metadata
            long adBlockId = plcmtMetaData.getBlockId();
            AdBlockMetaData adBlockMetaData = null;
            if (adBlockId > 0) {
                adBlockMetaData = CacheManager.getInstance().getAdBlockMetaData(adBlockId);
            }

            int[] deliveryTypes = {Constant.DeliveryType.PDB, Constant.DeliveryType.PD, Constant.DeliveryType.RTB};

            for (int i = 0; i < deliveryTypes.length; ++i) {
                //policy targeting
                List<Long> policyList = this.policyTargeting(mediaBid, deliveryTypes[i]);
                if (policyList == null || policyList.isEmpty()) {
                    continue;
                }

                //get policy detail
                Set<Pair<PolicyMetaData, Integer>> policyMetaDatas = this.getPolicyMetaData(policyList);
                if (policyMetaDatas == null || policyMetaDatas.isEmpty()) {
                    continue;
                }

                Pair<PolicyMetaData, Integer> selectedPolicy = null;
                while ((selectedPolicy = Utility.randomWithWeights(policyMetaDatas)) != null) {
                    this.multiHttpClient.reset();

                    PolicyMetaData policyMetaData = selectedPolicy.getLeft();
                    trackingParam.setPolicyId(policyMetaData.getId());

                    Map<Long, DSPBidMetaData> selectedDspList = new HashMap<>();
                    for (Map.Entry entry : policyMetaData.getDspInfoMap().entrySet()) {
                        PolicyMetaData.DSPInfo dspInfo = (PolicyMetaData.DSPInfo)entry.getValue();

                        DSPMetaData dspMetaData = CacheManager.getInstance().getDSPMetaData(dspInfo.getId());
                        if (dspInfo.getStatus() > 0 && dspMetaData != null && dspMetaData.getStatus() > 0) {

                            //QPS Contorl
                            if (dspMetaData.getMaxQPS() > 0) {
                                String qpsControl = String.format(Constant.CommonKey.DSP_QPS_CONTROL, dspInfo.getId(), System.currentTimeMillis() / 1000);
                                redisMaster.set(qpsControl, "0", "NX", "EX", 3);
                                long totalCount = redisMaster.incrBy(qpsControl, 1);
                                if (totalCount > dspMetaData.getMaxQPS()) {
                                    logger.warn("out of dsp [id=%d] max qps.", dspInfo.getId());
                                    continue;
                                }
                            }

                            DSPBidMetaData dspBidMetaData = new DSPBidMetaData();
                            dspBidMetaData.setDspMetaData(dspMetaData);

                            DSPBid.Builder dspBidBuilder = DSPBid.newBuilder();
                            dspBidMetaData.setDspBidBuilder(dspBidBuilder);

                            DSPBaseHandler dspBaseHandler = ResourceManager.getInstance().getDSPHandler(dspMetaData.getApiType());
                            if (dspBaseHandler == null) {
                                logger.error("get dsp handler [id={} apitype={}] error.", dspMetaData.getId(), dspMetaData.getApiType());
                                continue;
                            }

                            dspBidMetaData.setDspBaseHandler(dspBaseHandler);
                            HttpRequestBase httpRequestBase = dspBaseHandler.packageRequest(mediaBid, mediaMetaData, plcmtMetaData, adBlockMetaData, policyMetaData, dspBidMetaData);
                            if (httpRequestBase != null) {
                                HttpClient httpClient = this.getHttpClient(dspMetaData.getId());
                                int timeout = dspMetaData.getTimeout() > 0 ? dspMetaData.getTimeout() : mediaMetaData.getTimeout();
                                httpClient.setHttpRequest(httpRequestBase, timeout);
                                this.multiHttpClient.addHttpClient(httpClient);
                                dspBidMetaData.setHttpClient(httpClient);
                                dspBidMetaData.setHttpRequestBase(httpRequestBase);
                                selectedDspList.put(dspInfo.getId(), dspBidMetaData);
                            }
                        }
                    }

                    if (!this.multiHttpClient.isEmpty() && this.multiHttpClient.execute()) {
                        if (policyMetaData.getControlType() != Constant.PolicyControlType.NONE) {
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(new Date());
                            String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());

                            long totalCount = redisMaster.incr(String.format(Constant.CommonKey.POLICY_CONTORL_TOTAL, policyMetaData.getId()));
                            long dailyCount = redisMaster.incr(String.format(Constant.CommonKey.POLICY_CONTORL_DAILY, policyMetaData.getId(), currentDate));

                            if (!CacheManager.getInstance().policyQuantityControl(policyMetaData, totalCount, dailyCount)) {
                                CacheManager.getInstance().blockPolicy(policyMetaData.getId());
                            }
                        }

                        List<DSPBidMetaData> bidderList = new LinkedList<DSPBidMetaData>();
                        for (Map.Entry entry : selectedDspList.entrySet()) {
                            DSPBidMetaData dspBidMetaData = (DSPBidMetaData)entry.getValue();
                            DSPBaseHandler dspBaseHandler = dspBidMetaData.getDspBaseHandler();
                            HttpResponse httpResponse = dspBidMetaData.getHttpClient().getResponse();
                            if (httpResponse != null) {
                                if (dspBaseHandler.parseResponse(httpResponse, dspBidMetaData)) {
                                    if (policyMetaData.getDeliveryType() != Constant.DeliveryType.RTB ||
                                            dspBidMetaData.getDspBidBuilder().getResponse().getPrice() > plcmtMetaData.getBidFloor()) {
                                        bidderList.add(dspBidMetaData);
                                    }
                                }
                            } else {
                                dspBidMetaData.getDspBidBuilder().setStatus(Constant.StatusCode.REQUEST_TIMEOUT);
                            }

                            int executeTime = (int)dspBidMetaData.getHttpClient().getExecuteTime();
                            dspBidMetaData.getDspBidBuilder().setExecutetime(executeTime);
                            logger.debug("DSP[id={}] execute time: {}ms", dspBidMetaData.getDspMetaData().getId(), executeTime);
                            dspBidMetaData.getHttpRequestBase().releaseConnection();
                        }

                        DSPBidMetaData winner = null;
                        if (!bidderList.isEmpty()) {
                            winner = this.selectWinner(plcmtMetaData, policyMetaData, bidderList);
                            if (winner != null) {
                                trackingParam.setDspId(winner.getDspMetaData().getId());
                                trackingParam.setDspCost(winner.getAuctionPriceInfo());
                                winner.getDspBidBuilder().setWinner(1);

/*
                                int expiredTime = ResourceManager.getInstance().getConfiguration().getWebapp().getExpiredTime();
                                String recordKey = String.format(Constant.CommonKey.BID_RECORD, mediaBid.getImpid(), Long.toString(mediaMetaData.getId()), Long.toString(plcmtMetaData.getId()), Long.toString(policyMetaData.getId()));
                                redisMaster.set(recordKey, Long.toString(System.currentTimeMillis()), "NX", "EX", expiredTime);
*/

                                if (policyMetaData.getDeliveryType() == Constant.DeliveryType.RTB) {
                                    String url = winner.getDspBaseHandler().getWinNoticeUrl(winner);
                                    if (!StringUtils.isEmpty(url)) {
                                        final HttpGet httpGet = new HttpGet(url);
                                        final HttpClient httpClient = this.getHttpClient(winner.getDspMetaData().getId());
                                        this.winNoticeService.submit(new Runnable() {
                                            public void run() {
                                                httpClient.execute(httpGet, 150);
                                                httpGet.releaseConnection();
                                            }
                                        });
                                    }
                                }
                            }
                        }

                        for (Map.Entry entry : selectedDspList.entrySet()) {
                            DSPBidMetaData dspBidMetaData = (DSPBidMetaData)entry.getValue();
                            LoggerUtil.getInstance().writeBidLog(ResourceManager.getInstance().getKafkaProducer(), dspBidMetaData.getDspBidBuilder());
                        }

                        mediaBaseHandler.packageResponse(mediaBidMetaData, resp, (winner != null) ? winner.getDspBidBuilder() : null);
                        return;
                    }

                    policyMetaDatas.remove(selectedPolicy);
                }
            }

            mediaBaseHandler.packageResponse(mediaBidMetaData, resp, null);
        } catch (Exception ex) {
            resp.setStatus(Constant.StatusCode.NO_CONTENT);
            logger.error(ex.toString());
        } finally {
            if (redisMaster != null) {
                redisMaster.close();
            }

            logger.debug("media bid cost time: {}ms", System.currentTimeMillis() - t1);
        }
    }

    private List<Long> policyTargeting(MediaBid.Builder mediaBidBuilder, int deliveryType) {
        List<Pair<Integer, List<String>>> targetInfo = new LinkedList<>();

        MediaRequest.Builder mediaRequest = mediaBidBuilder.getRequestBuilder();

        //placement
        {
            List<String> info = new LinkedList<>();
            String adspaceKey = String.format("%d-%s", mediaRequest.getAdspaceid(), StringUtil.toString(mediaRequest.getDealid()));
            info.add(adspaceKey);
            targetInfo.add(Pair.of(Constant.TargetType.PLACEMENT, info));
        }

        //week time
        {
            List<String> info = new LinkedList<>();
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            int weekday = cal.get(Calendar.DAY_OF_WEEK) - 1;
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            info.add(String.format("%d-%02d", weekday, hour));
            targetInfo.add(Pair.of(Constant.TargetType.WEEKDAY_HOUR, info));
        }

        //location
        {
            List<String> info = new LinkedList<>();
            info.add(mediaBidBuilder.getLocation().subSequence(0, 1) + "000000000");
            info.add(mediaBidBuilder.getLocation().subSequence(0, 4) + "000000");
            info.add(mediaBidBuilder.getLocation().subSequence(0, 6) + "0000");
            info.add(mediaBidBuilder.getLocation());
            targetInfo.add(Pair.of(Constant.TargetType.LOCATION, info));
        }

        //os
        {
            List<String> info = new LinkedList<>();
            info.add(Integer.toString(mediaRequest.getOs()));
            targetInfo.add(Pair.of(Constant.TargetType.OS, info));
        }

        //connection type
        {
            List<String> info = new LinkedList<>();
            info.add(Integer.toString(mediaRequest.getConnectiontype()));
            targetInfo.add(Pair.of(Constant.TargetType.CONNECTION_TYPE, info));
        }

        List<Collection<Long>> targetPolicy = new LinkedList<>();
        for (Pair<Integer, List<String>> info : targetInfo) {
            List<Collection<Long>> policys = new LinkedList<>();

            Set<Long> policy = CacheManager.getInstance().getPolicyTargetInfo(String.format(Constant.CommonKey.TARGET_KEY, deliveryType, info.getLeft(), ""));
            if (policy != null) {
                policys.add(policy);
            }

            for (String key : info.getRight()) {
                policy = CacheManager.getInstance().getPolicyTargetInfo(String.format(Constant.CommonKey.TARGET_KEY, deliveryType, info.getLeft(), key));
                if (policy != null) {
                    policys.add(policy);
                }
            }

            targetPolicy.add(SetUtil.multiSetUnion(policys));
        }

        return new LinkedList<>(SetUtil.setDiff(SetUtil.multiSetInter(targetPolicy), CacheManager.getInstance().getBlockedPolicy()));
    }

    private Set<Pair<PolicyMetaData, Integer>> getPolicyMetaData(List<Long> policyList) {

        Set<Pair<PolicyMetaData, Integer>> policyMetaDatas = new HashSet<>(policyList.size());
        for (long policyId : policyList) {
            PolicyMetaData policyMetaData = CacheManager.getInstance().getPolicyMetaData(policyId);
            if (policyMetaData != null) {
                policyMetaDatas.add(Pair.of(policyMetaData, policyMetaData.getWeight()));
            }
        }

        return policyMetaDatas;
    }

    private DSPBidMetaData selectWinner(PlcmtMetaData plcmtMetaData, PolicyMetaData policyMetaData, List<DSPBidMetaData> bidderList) {
        DSPBidMetaData winner = null;

        if (policyMetaData.getDeliveryType() != Constant.DeliveryType.RTB) {
            DSPBidMetaData dspBidMetaData = bidderList.get(0);
            PolicyMetaData.AdspaceInfo adspaceInfo = policyMetaData.getAdspaceInfoMap().get(plcmtMetaData.getId());

            AuctionPriceInfo auctionInfo = new AuctionPriceInfo();
            auctionInfo.setBidType(adspaceInfo.getBidType());
            auctionInfo.setBidPrice(adspaceInfo.getBidFloor());
            dspBidMetaData.setAuctionPriceInfo(auctionInfo);

            winner = dspBidMetaData;
        } else {
            bidderList.sort(new Comparator<DSPBidMetaData>() {
                @Override
                public int compare(DSPBidMetaData o1, DSPBidMetaData o2) {
                    return o1.getDspBidBuilder().getResponse().getPrice() > o2.getDspBidBuilder().getResponse().getPrice() ? 1 : -1;
                }
            });
            
            int price = plcmtMetaData.getBidFloor();
            if (bidderList.size() >= 2) {
                DSPBidMetaData dspBidMetaData = bidderList.get(1);
                price = dspBidMetaData.getDspBidBuilder().getResponse().getPrice();
            }

            DSPBidMetaData dspBidMetaData = bidderList.get(0);

            AuctionPriceInfo auctionInfo = new AuctionPriceInfo();
            auctionInfo.setBidType(plcmtMetaData.getBidType());
            auctionInfo.setBidPrice(price + 1);
            dspBidMetaData.setAuctionPriceInfo(auctionInfo);

            winner = dspBidMetaData;
        }

        return winner;
    }
}


