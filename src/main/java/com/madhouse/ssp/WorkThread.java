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
            String impid = req.getParameter("impid");
            String mid = req.getParameter("mid");
            String plcmtid = req.getParameter("plcmtid");
            String policyid = req.getParameter("policyid");
            String location = req.getParameter("location");
            String ext = req.getParameter("ext");

            //args check
            if (StringUtils.isEmpty(impid) || StringUtils.isEmpty(mid) || StringUtils.isEmpty(plcmtid) ||
                    StringUtils.isEmpty(policyid) || StringUtils.isEmpty(location) || StringUtils.isEmpty(ext)) {
                resp.setStatus(Constant.StatusCode.BAD_REQUEST);
                return;
            }

            ImpressionTrack.Builder impressionTrack = ImpressionTrack.newBuilder();

            //bid redis check
            String recordKey = String.format(Constant.CommonKey.BID_RECORD, impid, mid, plcmtid, policyid);
            if (!redisMaster.exists(recordKey)) {
                impressionTrack.setInvalid(Constant.InvalidType.NO_REQUEST);
            }

            int expiredTime = ResourceManager.getInstance().getConfiguration().getWebapp().getExpiredTime();
            recordKey = String.format(Constant.CommonKey.IMP_RECORD, impid, mid, plcmtid, policyid);
            String ret = redisMaster.set(recordKey, "1", "NX", "EX", expiredTime);
            if (!StringUtils.isEmpty(ret) && ret.substring(0, 2).compareTo("OK") != 0) {
                impressionTrack.setInvalid(Constant.InvalidType.DUPLICATE);
            }

            impressionTrack.setTime(System.currentTimeMillis());
            impressionTrack.setIp(HttpUtil.getRealIp(req));
            impressionTrack.setUa(HttpUtil.getUserAgent(req));
            impressionTrack.setImpid(impid);
            impressionTrack.setMediaid(Long.parseLong(mid));
            impressionTrack.setAdspaceid(Long.parseLong(plcmtid));
            impressionTrack.setPolicyid(Long.parseLong(policyid));
            impressionTrack.setLocation(location);

            String[] exts = ext.split(",");
            if (exts.length >= 3) {
                impressionTrack.setExt(ext);
                impressionTrack.setDspid(Long.parseLong(exts[0]));
                impressionTrack.setIncome(Integer.parseInt(exts[1]));
                impressionTrack.setCost(Integer.parseInt(exts[2]));

                impressionTrack.setStatus(Constant.StatusCode.OK);
                LoggerUtil.getInstance().wirteImpressionTrackLog(ResourceManager.getInstance().getKafkaProducer(), impressionTrack);

                resp.getOutputStream().write(this.image);
                resp.setContentType("image/gif");
                resp.setContentLength(this.image.length);
                resp.setStatus(impressionTrack.getStatus());
            }
        } catch (Exception ex) {
            System.err.println(ex.toString());
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
            String impid = req.getParameter("impid");
            String mid = req.getParameter("mid");
            String plcmtid = req.getParameter("plcmtid");
            String policyid = req.getParameter("policyid");
            String location = req.getParameter("location");
            String ext = req.getParameter("ext");

            //args check
            if (StringUtils.isEmpty(impid) || StringUtils.isEmpty(mid) || StringUtils.isEmpty(plcmtid) ||
                    StringUtils.isEmpty(policyid) || StringUtils.isEmpty(location) || StringUtils.isEmpty(ext)) {
                resp.setStatus(Constant.StatusCode.BAD_REQUEST);
                return;
            }

            ClickTrack.Builder clickTrack = ClickTrack.newBuilder();

            //bid redis check
            String recordKey = String.format(Constant.CommonKey.BID_RECORD, impid, mid, plcmtid, policyid);
            if (!redisMaster.exists(recordKey)) {
                clickTrack.setInvalid(Constant.InvalidType.NO_REQUEST);
            }

            int expiredTime = ResourceManager.getInstance().getConfiguration().getWebapp().getExpiredTime();
            recordKey = String.format(Constant.CommonKey.CLK_RECORD, impid, mid, plcmtid, policyid);
            String ret = redisMaster.set(recordKey, "1", "NX", "EX", expiredTime);
            if (!StringUtils.isEmpty(ret) && ret.substring(0, 2).compareTo("OK") != 0) {
                clickTrack.setInvalid(Constant.InvalidType.DUPLICATE);
            }

            clickTrack.setTime(System.currentTimeMillis());
            clickTrack.setIp(HttpUtil.getRealIp(req));
            clickTrack.setUa(HttpUtil.getUserAgent(req));
            clickTrack.setImpid(impid);
            clickTrack.setMediaid(Long.parseLong(mid));
            clickTrack.setAdspaceid(Long.parseLong(plcmtid));
            clickTrack.setPolicyid(Long.parseLong(policyid));
            clickTrack.setLocation(location);

            String[] exts = ext.split(",");
            if (exts.length >= 3) {
                clickTrack.setExt(ext);
                clickTrack.setDspid(Long.parseLong(exts[0]));
                clickTrack.setIncome(Integer.parseInt(exts[1]));
                clickTrack.setCost(Integer.parseInt(exts[2]));

                String url = URLDecoder.decode(HttpUtil.getParameter(req, "url"), "utf-8");

                if (url.startsWith("http://") || url.startsWith("https://")) {
                    resp.setHeader("Location", url);
                    clickTrack.setStatus(Constant.StatusCode.REDIRECT);
                } else {
                    resp.getOutputStream().write(this.image);
                    resp.setContentType("image/gif");
                    resp.setContentLength(this.image.length);
                    resp.setStatus(Constant.StatusCode.OK);
                    clickTrack.setStatus(Constant.StatusCode.OK);
                }

                LoggerUtil.getInstance().writeClickTrackLog(ResourceManager.getInstance().getKafkaProducer(), clickTrack);
                resp.setStatus(clickTrack.getStatus());
            }
        } catch (Exception ex) {
            System.err.println(ex.toString());
            resp.setStatus(Constant.StatusCode.BAD_REQUEST);
        } finally {
            if (redisMaster != null) {
                redisMaster.close();
            }
        }
    }

    public void onBid(HttpServletRequest req, HttpServletResponse resp) {
        try {
            Jedis redisMaster = ResourceManager.getInstance().getJedisPoolMaster().getResource();
            Jedis redisSlave = ResourceManager.getInstance().getJedisPoolSlave().getResource();

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
            mediaRequest.setAdtype(plcmtMetaData.getAdType());

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
            trackingParam.setTime(System.currentTimeMillis());

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
                            String qpsControl = String.format(Constant.CommonKey.DSP_QPS_CONTROL, dspInfo.getId(), System.currentTimeMillis() / 1000);
                            redisMaster.set(qpsControl, "0", "NX", "EX", 3);
                            long totalCount = redisMaster.incrBy(qpsControl, 1);
                            if (totalCount >= dspMetaData.getMaxQPS()) {
                                continue;
                            }

                            DSPBidMetaData dspBidMetaData = new DSPBidMetaData();
                            dspBidMetaData.setDspMetaData(dspMetaData);

                            DSPBid.Builder dspBidBuilder = DSPBid.newBuilder();
                            dspBidMetaData.setDspBidBuilder(dspBidBuilder);

                            DSPBaseHandler dspBaseHandler = ResourceManager.getInstance().getDSPHandler(dspMetaData.getApiType());
                            if (dspBaseHandler == null) {
                                logger.error("get dsp handler [id=%d apitype=%d] error.", dspMetaData.getId(), dspMetaData.getApiType());
                                continue;
                            }

                            dspBidMetaData.setDspBaseHandler(dspBaseHandler);
                            HttpRequestBase httpRequestBase = dspBaseHandler.packageRequest(mediaBid, mediaMetaData, plcmtMetaData, adBlockMetaData, policyMetaData, dspBidMetaData);
                            if (httpRequestBase != null) {
                                HttpClient httpClient = this.getHttpClient(dspMetaData.getId());
                                httpClient.setHttpRequest(httpRequestBase, mediaMetaData.getTimeout());
                                this.multiHttpClient.addHttpClient(httpClient);
                                dspBidMetaData.setHttpClient(httpClient);
                                dspBidMetaData.setHttpRequestBase(httpRequestBase);
                                selectedDspList.put(dspInfo.getId(), dspBidMetaData);
                            }
                        }
                    }

                    if (!this.multiHttpClient.isEmpty() && this.multiHttpClient.execute()) {
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(new Date());
                        String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());

                        long totalCount = redisMaster.incr(String.format(Constant.CommonKey.POLICY_CONTORL_TOTAL, policyMetaData.getId()));
                        long dailyCount = redisMaster.incr(String.format(Constant.CommonKey.POLICY_CONTORL_DAILY, policyMetaData.getId(), currentDate));

                        if (!CacheManager.getInstance().policyQuantityControl(policyMetaData, totalCount, dailyCount)) {
                            CacheManager.getInstance().blockPolicy(policyMetaData.getId());
                        }

                        List<DSPBidMetaData> bidderList = new LinkedList<DSPBidMetaData>();
                        for (Map.Entry entry : selectedDspList.entrySet()) {
                            DSPBidMetaData dspBidMetaData = (DSPBidMetaData)entry.getValue();
                            DSPBaseHandler dspBaseHandler = dspBidMetaData.getDspBaseHandler();
                            HttpResponse httpResponse = dspBidMetaData.getHttpClient().getResponse();
                            if (httpResponse != null) {
                                if (dspBaseHandler.parseResponse(httpResponse, dspBidMetaData)) {
                                    if (policyMetaData.getDeliveryType() != Constant.DeliveryType.RTB || dspBidMetaData.getDspBidBuilder().getResponse().getPrice() >= plcmtMetaData.getBidFloor()) {
                                        bidderList.add(dspBidMetaData);
                                    }
                                }
                            } else {
                                dspBidMetaData.getDspBidBuilder().setStatus(Constant.StatusCode.REQUEST_TIMEOUT);
                            }

                            dspBidMetaData.getDspBidBuilder().setExecutetime((int)dspBidMetaData.getHttpClient().getExecuteTime());
                            dspBidMetaData.getHttpRequestBase().releaseConnection();
                        }

                        if (!bidderList.isEmpty()) {
                            DSPBidMetaData dspBidMetaData = this.selectWinner(plcmtMetaData, policyMetaData, bidderList);
                            if (dspBidMetaData != null) {
                                trackingParam.setDspCost(dspBidMetaData.getAuctionPriceInfo());
                                dspBidMetaData.getDspBidBuilder().setWinner(1);
                                int expiredTime = ResourceManager.getInstance().getConfiguration().getWebapp().getExpiredTime();
                                String recordKey = String.format(Constant.CommonKey.BID_RECORD, mediaBid.getImpid(), Long.toString(mediaMetaData.getId()), Long.toString(plcmtMetaData.getId()), Long.toString(policyMetaData.getId()));
                                redisMaster.set(recordKey, Long.toString(System.currentTimeMillis()), "NX", "EX", expiredTime);

                                if (mediaBaseHandler.packageResponse(mediaBidMetaData, resp, dspBidMetaData.getDspBidBuilder())) {
                                    if (policyMetaData.getDeliveryType() == Constant.DeliveryType.RTB) {
                                        String url = dspBidMetaData.getDspBaseHandler().getWinNoticeUrl(dspBidMetaData);
                                        if (!StringUtils.isEmpty(url)) {
                                            final HttpGet httpGet = new HttpGet(url);
                                            final HttpClient httpClient = this.getHttpClient(dspBidMetaData.getDspMetaData().getId());
                                            this.winNoticeService.submit(new Runnable() {
                                                public void run() {
                                                    httpClient.execute(httpGet, 150);
                                                    httpGet.releaseConnection();
                                                }
                                            });
                                        }
                                    }
                                }

                                return;
                            }
                        }
                    }

                    policyMetaDatas.remove(selectedPolicy);
                }
            }
        } catch (Exception ex) {
            resp.setStatus(Constant.StatusCode.NO_CONTENT);
        }
    }

    private List<Long> policyTargeting(MediaBid.Builder mediaBidBuilder, int deliveryType) {
        List<Pair<Integer, List<String>>> targetInfo = new LinkedList<>();

        MediaRequest mediaRequest = mediaBidBuilder.getRequest();

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
            int weekDay = cal.get(Calendar.DAY_OF_WEEK) - 1;
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            info.add(String.format("%d%02d", weekDay, hour));
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


