package com.madhouse.ssp;

import com.madhouse.cache.*;
import com.madhouse.dsp.DSPBaseHandler;
import com.madhouse.resource.ResourceManager;
import com.madhouse.ssp.avro.DSPBid;
import com.madhouse.ssp.avro.MediaBid;
import com.madhouse.ssp.avro.MediaRequest;
import com.madhouse.util.ObjectUtils;
import com.madhouse.util.SetUtil;
import com.madhouse.util.StringUtil;
import com.madhouse.util.Utility;
import com.madhouse.util.httpclient.HttpClient;
import com.madhouse.util.httpclient.MultiHttpClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.logging.log4j.Logger;
import redis.clients.jedis.Jedis;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by wujunfeng on 2018-01-04.
 */
public class BidHandler implements Runnable {
    private CountDownLatch latch;
    private MediaBid.Builder mediaBid;
    private MediaBidMetaData.BidMetaData bidMetaData;

    private MultiHttpClient multiHttpClient = new MultiHttpClient();
    private Map<Long, HttpClient> httpClientMap = new ConcurrentHashMap<>();
    private ExecutorService asyncExecutorService = Executors.newCachedThreadPool();

    private static Logger logger = LoggerUtil.getInstance().getPremiummadlogger();

    public boolean init(CountDownLatch latch, MediaBid.Builder mediaBid, MediaBidMetaData.BidMetaData bidMetaData) {
        this.latch = latch;
        this.mediaBid = mediaBid;
        this.bidMetaData = bidMetaData;
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

    @Override
    public void run() {
        Jedis redisMaster = ResourceManager.getInstance().getJedisPoolMaster().getResource();

        try {
            MediaRequest.Builder mediaRequest = this.mediaBid.getRequestBuilder();

            //get placement metadata
            PlcmtMetaData plcmtMetaData = CacheManager.getInstance().getPlcmtMetaData(mediaRequest.getAdspacekey());
            if (plcmtMetaData == null) {
                logger.error("media adspace[{}] mapping error.", mediaRequest.getAdspacekey());
                this.mediaBid.setStatus(Constant.StatusCode.NOT_ALLOWED);
                return;
            }

            //bidfloor, bidtype
            this.mediaBid.setBidfloor(plcmtMetaData.getBidFloor());
            this.mediaBid.setBidtype(plcmtMetaData.getBidType());

            MediaMetaData mediaMetaData = CacheManager.getInstance().getMediaMetaData(plcmtMetaData.getMediaId());
            if (mediaMetaData == null) {
                logger.error("get media[{}] metadata error.", plcmtMetaData.getMediaId());
                this.mediaBid.setStatus(Constant.StatusCode.NOT_ALLOWED);
                return;
            }

            //init mediaid, adspaceid, Type
            mediaRequest.setMediaid(mediaMetaData.getId());
            mediaRequest.setAdspaceid(plcmtMetaData.getId());
            mediaRequest.setType(mediaMetaData.getType());

            if (!mediaRequest.hasCategory()) {
                mediaRequest.setCategory(mediaMetaData.getCategory());
            }

            this.bidMetaData.setMediaMetaData(mediaMetaData);
            this.bidMetaData.setPlcmtMetaData(plcmtMetaData);

            //init user ip
            if (!mediaRequest.hasIp() || StringUtils.isEmpty(mediaRequest.getIp())) {
                mediaRequest.setIp(this.mediaBid.getIp());
            }

            //init user ua
            if (!mediaRequest.hasUa()) {
                mediaRequest.setUa(this.mediaBid.getUa());
            }

            //init location
            String location = ResourceManager.getInstance().getLocation(mediaRequest.getIp());
            if (StringUtils.isEmpty(location)) {
                logger.error("get user's location error.");
                this.mediaBid.setStatus(Constant.StatusCode.BAD_REQUEST);
                return;
            }

            this.mediaBid.setLocation(location);

            if (mediaMetaData.getStatus() <= 0 || plcmtMetaData.getStatus() <= 0) {
                logger.warn("media or adspace is not allowed.");
                return;
            }

            if (!mediaRequest.hasW() || mediaRequest.getW() <= 0 || !mediaRequest.hasH() || mediaRequest.getH() <= 0) {
                if (!ObjectUtils.isEmpty(plcmtMetaData.getSizes())) {
                    PlcmtMetaData.Size size = plcmtMetaData.getSizes().get(Utility.nextInt(plcmtMetaData.getSizes().size()));
                    mediaRequest.setW(size.getW());
                    mediaRequest.setH(size.getH());
                }
            }

            if (!CacheManager.getInstance().isMediaWhiteList(mediaMetaData.getId())) {
                String ip = mediaRequest.getIp();
                String ifa = StringUtil.toString(mediaRequest.getIfa()).toUpperCase();

                String didmd5 = StringUtil.toString(mediaRequest.getDidmd5()).toLowerCase();
                if (StringUtils.isEmpty(didmd5) && !StringUtils.isEmpty(mediaRequest.getDid())) {
                    didmd5 = StringUtil.getMD5(mediaRequest.getDid().toLowerCase());
                    mediaRequest.setDidmd5(didmd5);
                }

                String dpidmd5 = StringUtil.toString(mediaRequest.getDpidmd5()).toLowerCase();
                if (StringUtils.isEmpty(dpidmd5) && !StringUtils.isEmpty(mediaRequest.getDpid())) {
                    dpidmd5 = StringUtil.getMD5(mediaRequest.getDpid().toLowerCase());
                    mediaRequest.setDpidmd5(dpidmd5);
                }

                if (CacheManager.getInstance().isBlockedDevice(mediaRequest.getOs(), ip, ifa, didmd5, dpidmd5)) {
                    logger.error("[{}] device is blocked.", mediaRequest.getAdspacekey());
                    mediaBid.setStatus(Constant.StatusCode.BAD_REQUEST);
                    return;
                }
            }

            MediaBidMetaData.TrackingParam trackingParam = new MediaBidMetaData.TrackingParam();
            this.bidMetaData.setTrackingParam(trackingParam);

            trackingParam.setReqId(StringUtil.toString(mediaRequest.getBid()));
            trackingParam.setImpId(this.mediaBid.getImpid());
            trackingParam.setMediaId(plcmtMetaData.getMediaId());
            trackingParam.setAdspaceId(plcmtMetaData.getId());
            trackingParam.setLocation(this.mediaBid.getLocation());

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
                List<Long> policyList = this.policyTargeting(this.mediaBid, deliveryTypes[i]);
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
                    if (policyMetaData.getControlType() != Constant.PolicyControlType.NONE) {
                        if (!CacheManager.getInstance().checkPolicyBudget(policyMetaData)) {
                            policyMetaDatas.remove(selectedPolicy);
                            continue;
                        }
                    }

                    trackingParam.setPolicyId(policyMetaData.getId());

                    Map<Long, DSPBidMetaData> selectedDspList = new HashMap<>();
                    for (Map.Entry entry : policyMetaData.getDspInfoMap().entrySet()) {
                        PolicyMetaData.DSPInfo dspInfo = (PolicyMetaData.DSPInfo)entry.getValue();

                        DSPMetaData dspMetaData = CacheManager.getInstance().getDSPMetaData(dspInfo.getId());
                        if (dspInfo.getStatus() > 0 && dspMetaData != null && dspMetaData.getStatus() > 0) {

                            //QPS Control
                            if (dspMetaData.getMaxQPS() > 0) {
                                long tsec = System.currentTimeMillis() / 1000L;
                                String qpsControl = String.format(Constant.CommonKey.DSP_QPS_CONTROL, dspInfo.getId(), tsec);
                                redisMaster.set(qpsControl, "0", "NX", "EX", 60);
                                long totalCount = redisMaster.incr(qpsControl);
                                if (totalCount > dspMetaData.getMaxQPS() && policyMetaData.getDeliveryType() != Constant.DeliveryType.PDB) {
                                    logger.warn("out of dsp[{}] max qps.", dspInfo.getId());
                                    continue;
                                }
                            }

                            DSPBidMetaData dspBidMetaData = new DSPBidMetaData();
                            dspBidMetaData.setDspMetaData(dspMetaData);

                            DSPBid.Builder dspBidBuilder = DSPBid.newBuilder();
                            dspBidMetaData.setDspBidBuilder(dspBidBuilder);

                            DSPBaseHandler dspBaseHandler = ResourceManager.getInstance().getDSPHandler(dspMetaData.getApiType());
                            if (dspBaseHandler == null) {
                                logger.error("get dsp handler[id={} apitype={}] error.", dspMetaData.getId(), dspMetaData.getApiType());
                                continue;
                            }

                            dspBidMetaData.setDspBaseHandler(dspBaseHandler);
                            HttpRequestBase httpRequestBase = dspBaseHandler.packageRequest(mediaBid, mediaMetaData, plcmtMetaData, adBlockMetaData, policyMetaData, dspBidMetaData);
                            if (httpRequestBase != null) {
                                HttpClient httpClient = this.getHttpClient(dspMetaData.getId());
                                int timeout = dspMetaData.getTimeout() >= mediaMetaData.getTimeout() ? dspMetaData.getTimeout() : mediaMetaData.getTimeout();
                                httpClient.setHttpRequest(httpRequestBase, timeout);
                                this.multiHttpClient.addHttpClient(httpClient);
                                dspBidMetaData.setHttpClient(httpClient);
                                dspBidMetaData.setTimeout(timeout);
                                dspBidMetaData.setHttpRequestBase(httpRequestBase);
                                selectedDspList.put(dspInfo.getId(), dspBidMetaData);
                            }
                        }
                    }

                    if (this.multiHttpClient.isEmpty()) {
                        policyMetaDatas.remove(selectedPolicy);
                        continue;
                    }

                    CacheManager.getInstance().incrPolicyBudgetBy(policyMetaData, -1L);

                    if (this.multiHttpClient.execute()) {
                        List<DSPBidMetaData> bidderList = new ArrayList<>(selectedDspList.size());
                        for (Map.Entry entry : selectedDspList.entrySet()) {
                            DSPBidMetaData dspBidMetaData = (DSPBidMetaData)entry.getValue();
                            DSPBaseHandler dspBaseHandler = dspBidMetaData.getDspBaseHandler();
                            HttpResponse httpResponse = dspBidMetaData.getHttpClient().getResponse();

                            int executeTime = (int)dspBidMetaData.getHttpClient().getExecuteTime();
                            if (httpResponse != null && executeTime <= dspBidMetaData.getTimeout()) {
                                if (dspBaseHandler.parseResponse(httpResponse, dspBidMetaData)) {
                                    if (policyMetaData.getDeliveryType() != Constant.DeliveryType.RTB ||
                                            dspBidMetaData.getDspBidBuilder().getResponseBuilder().getPrice() > plcmtMetaData.getBidFloor()) {
                                        bidderList.add(dspBidMetaData);
                                    }
                                }
                            } else {
                                dspBidMetaData.getDspBidBuilder().setStatus(Constant.StatusCode.REQUEST_TIMEOUT);
                            }

                            dspBidMetaData.getDspBidBuilder().setExecutetime(executeTime);
                            logger.debug("dsp [id={}] execute time: {}ms", dspBidMetaData.getDspMetaData().getId(), executeTime);
                            dspBidMetaData.getHttpRequestBase().releaseConnection();
                        }

                        DSPBidMetaData winner = null;
                        if (!bidderList.isEmpty()) {
                            winner = this.selectWinner(plcmtMetaData, policyMetaData, bidderList);
                            if (winner != null) {
                                trackingParam.setDspId(winner.getDspMetaData().getId());
                                trackingParam.setDspCost(winner.getAuctionPriceInfo());
                                trackingParam.setCid(StringUtil.toString(winner.getDspBidBuilder().getResponseBuilder().getCid()));

                                winner.getDspBidBuilder().setWinner(1);

                                if (policyMetaData.getDeliveryType() == Constant.DeliveryType.RTB) {
                                    String url = winner.getDspBaseHandler().getWinNoticeUrl(winner);
                                    if (!StringUtils.isEmpty(url)) {
                                        final HttpGet httpGet = new HttpGet(url);
                                        final HttpClient httpClient = this.getHttpClient(winner.getDspMetaData().getId());
                                        this.asyncExecutorService.submit(new Runnable() {
                                            public void run() {
                                                httpClient.execute(httpGet, 150);
                                                httpGet.releaseConnection();
                                            }
                                        });
                                    }
                                }
                            }
                        }

                        DSPBid.Builder dspBid = null;
                        MaterialMetaData materialMetaData = null;

                        if (winner != null) {
                            dspBid = winner.getDspBidBuilder();
                            if (mediaMetaData.getMaterialAuditMode() != Constant.AuditMode.NONE) {
                                long dspId = winner.getDspMetaData().getId();
                                long mediaId = mediaMetaData.getId();
                                long adspaceId = plcmtMetaData.getId();
                                String crid = StringUtil.toString(winner.getDspBidBuilder().getResponseBuilder().getCrid());

                                materialMetaData = CacheManager.getInstance().getMaterialMetaData(dspId, crid, mediaId, 0);
                                if (materialMetaData == null) {
                                    materialMetaData = CacheManager.getInstance().getMaterialMetaData(dspId, crid, mediaId, adspaceId);
                                }

                                if (materialMetaData == null) {
                                    winner.getDspBidBuilder().setStatus(Constant.StatusCode.BAD_REQUEST);
                                    logger.warn("material status error.");
                                }
                            }
                        }

                        this.bidMetaData.setDspBid(dspBid);
                        this.bidMetaData.setMaterialMetaData(materialMetaData);

                        for (Map.Entry entry : selectedDspList.entrySet()) {
                            DSPBidMetaData dspBidMetaData = (DSPBidMetaData)entry.getValue();
                            LoggerUtil.getInstance().writeBidLog(ResourceManager.getInstance().getKafkaProducer(), dspBidMetaData.getDspBidBuilder());
                        }

                        return;
                    } else {
                        CacheManager.getInstance().incrPolicyBudgetBy(policyMetaData, 1L);
                    }

                    policyMetaDatas.remove(selectedPolicy);
                }
            }
        } catch (Exception e) {
            this.mediaBid.setStatus(Constant.StatusCode.INTERNAL_ERROR);
            logger.error(e.toString());
        } finally {
            if (redisMaster != null) {
                redisMaster.close();
            }

            if (this.latch != null) {
                this.latch.countDown();
            }
        }
    }

    private List<Long> policyTargeting(MediaBid.Builder mediaBid, int deliveryType) {
        List<Pair<Integer, List<String>>> targetInfo = new LinkedList<>();

        MediaRequest.Builder mediaRequest = mediaBid.getRequestBuilder();

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());

        //placement
        {
            List<String> info = new LinkedList<>();
            String adspaceKey = String.format("%d-%s", mediaRequest.getAdspaceid(), StringUtil.toString(mediaRequest.getDealid()));
            info.add(adspaceKey);
            targetInfo.add(Pair.of(Constant.TargetType.PLACEMENT, info));
        }

        //date
        {
            String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
            List<String> info = new LinkedList<>();
            info.add(currentDate);
            targetInfo.add(Pair.of(Constant.TargetType.DATE, info));
        }

        //week time
        {
            List<String> info = new LinkedList<>();

            int weekday = cal.get(Calendar.DAY_OF_WEEK) - 1;
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            info.add(String.format("%d-%02d", weekday, hour));
            targetInfo.add(Pair.of(Constant.TargetType.WEEKDAY_HOUR, info));
        }

        //location
        {
            List<String> info = new LinkedList<>();
            info.add(mediaBid.getLocation().subSequence(0, 4) + "000000");
            info.add(mediaBid.getLocation().subSequence(0, 6) + "0000");
            info.add(mediaBid.getLocation());
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
            if (policyMetaData != null && policyMetaData.getWeight() > 0) {
                policyMetaDatas.add(Pair.of(policyMetaData, policyMetaData.getWeight()));
            }
        }

        return policyMetaDatas;
    }

    private DSPBidMetaData selectWinner(PlcmtMetaData plcmtMetaData, PolicyMetaData policyMetaData, List<DSPBidMetaData> bidderList) {
        DSPBidMetaData winner = null;

        if (bidderList != null && !bidderList.isEmpty()) {
            if (policyMetaData.getDeliveryType() != Constant.DeliveryType.RTB) {
                DSPBidMetaData dspBidMetaData = bidderList.get(0);
                PolicyMetaData.AdspaceInfo adspaceInfo = policyMetaData.getAdspaceInfoMap().get(plcmtMetaData.getId());

                AuctionPriceInfo auctionInfo = new AuctionPriceInfo();
                auctionInfo.setBidType(adspaceInfo.getBidType());
                auctionInfo.setBidPrice(adspaceInfo.getBidFloor());
                dspBidMetaData.setAuctionPriceInfo(auctionInfo);

                winner = dspBidMetaData;
            } else {
                //price desc, executeTime asc
                bidderList.sort(new Comparator<DSPBidMetaData>() {
                    @Override
                    public int compare(DSPBidMetaData o1, DSPBidMetaData o2) {
                        DSPBid.Builder left = o1.getDspBidBuilder();
                        DSPBid.Builder right = o2.getDspBidBuilder();

                        if (left.getResponseBuilder().getPrice() > right.getResponseBuilder().getPrice()) {
                            return -1;
                        } else if (left.getResponseBuilder().getPrice() == right.getResponseBuilder().getPrice()) {
                            if (left.getExecutetime() < right.getExecutetime()) {
                                return -1;
                            } else {
                                return 1;
                            }
                        }

                        return 1;
                    }
                });

                int price = plcmtMetaData.getBidFloor();
                int maxPrice = bidderList.get(0).getDspBidBuilder().getResponseBuilder().getPrice();
                for (int i = 1; i < bidderList.size(); ++i) {
                    DSPBidMetaData dspBidMetaData = bidderList.get(i);
                    if (dspBidMetaData.getDspBidBuilder().getResponseBuilder().getPrice() < maxPrice) {
                        price = dspBidMetaData.getDspBidBuilder().getResponseBuilder().getPrice();
                        break;
                    }
                }

                DSPBidMetaData dspBidMetaData = bidderList.get(0);

                AuctionPriceInfo auctionInfo = new AuctionPriceInfo();
                auctionInfo.setBidType(plcmtMetaData.getBidType());
                auctionInfo.setBidPrice(price + 1);
                dspBidMetaData.setAuctionPriceInfo(auctionInfo);

                winner = dspBidMetaData;
            }
        }

        return winner;
    }
}
