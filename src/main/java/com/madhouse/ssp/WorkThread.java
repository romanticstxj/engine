package com.madhouse.ssp;

import com.madhouse.cache.*;
import com.madhouse.dsp.DSPBaseHandler;
import com.madhouse.media.MediaBaseHandler;
import com.madhouse.resource.ResourceManager;
import com.madhouse.rtb.PremiumMADRTBProtocol;
import com.madhouse.util.HttpUtil;
import com.madhouse.util.SetUtil;
import com.madhouse.util.StringUtil;
import com.madhouse.util.Utility;
import com.madhouse.util.httpclient.MultiHttpClient;
import com.madhouse.util.httpclient.HttpClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by WUJUNFENG on 2017/5/23.
 */
public class WorkThread {
    private Jedis redisMaster = null;
    private Jedis redisSlave = null;
    private MultiHttpClient multiHttpClient = new MultiHttpClient();
    private HttpClient winNoticeHttpClient = new HttpClient();
    private ExecutorService winNoticeService = Executors.newCachedThreadPool();
    private final byte[] image = {  0x47, 0x49, 0x46, 0x38, 0x39, 0x61, 0x01, 0x00, 0x01, 0x00,
                                    (byte)0x80, 0x01, 0x00, 0x00, 0x00, 0x00, (byte)0xff, (byte)0xff, (byte)0xff, 0x21,
                                    (byte)0xf9, 0x04, 0x01, 0x00, 0x00, 0x01, 0x00, 0x2c, 0x00, 0x00,
                                    0x00, 0x00, 0x01, 0x00, 0x01, 0x00, 0x00, 0x02, 0x02, 0x4c, 0x01, 0x00, 0x3b};

    public boolean init() {
        return ResourceManager.getInstance().init() && CacheManager.getInstance().init();
    }

    public void onImpression(HttpServletRequest req, HttpServletResponse resp) {
        try {

            String impid = req.getParameter("impid");
            String mid = req.getParameter("mid");
            String plcmtid = req.getParameter("plcmtid");
            String policyid = req.getParameter("policyid");
            String ext = req.getParameter("ext");

            //args check
            if (StringUtils.isEmpty(impid) || StringUtils.isEmpty(mid) || StringUtils.isEmpty(plcmtid) || StringUtils.isEmpty(policyid) || StringUtils.isEmpty(ext)) {
                resp.setStatus(Constant.StatusCode.BAD_REQUEST);
                return;
            }

            //bid redis check
            String key = String.format(Constant.CommonKey.BID_RECORD, impid, mid, plcmtid, policyid);
            if (key.isEmpty()) {
                resp.setStatus(Constant.StatusCode.BAD_REQUEST);
                return;
            }

            PremiumMADDataModel.ImpressionTrack.Builder impressionTrack = PremiumMADDataModel.ImpressionTrack.newBuilder();

            impressionTrack.setTime(System.currentTimeMillis());
            impressionTrack.setIp(HttpUtil.getRealIp(req));
            impressionTrack.setUa(HttpUtil.getUserAgent(req));
            impressionTrack.setImpid(impid);
            impressionTrack.setMediaid(Long.parseLong(mid));
            impressionTrack.setAdspaceid(Long.parseLong(plcmtid));
            impressionTrack.setPolicyid(Long.parseLong(policyid));

            String[] exts = ext.split(",");
            if (exts.length >= 3) {
                PremiumMADDataModel.ImpressionTrack.Ext.Builder var1 = PremiumMADDataModel.ImpressionTrack.Ext.newBuilder();
                var1.setArgs(ext);
                var1.setDspid(Long.parseLong(exts[0]));
                var1.setIncome(Integer.parseInt(exts[1]));
                var1.setCost(Integer.parseInt(exts[2]));
                impressionTrack.setExt(var1);

                impressionTrack.setStatus(Constant.StatusCode.OK);
                LoggerUtil.getInstance().wirteImpressionTrackLog(ResourceManager.getInstance().getKafkaProducer(), impressionTrack.build());

                resp.getOutputStream().write(this.image);
                resp.setContentType("image/gif");
                resp.setContentLength(this.image.length);
                resp.setStatus(impressionTrack.getStatus());
                return;
            }
        } catch (Exception ex) {
            System.err.println(ex.toString());
        }

        resp.setStatus(Constant.StatusCode.BAD_REQUEST);
    }

    public void onClick(HttpServletRequest req, HttpServletResponse resp) {
        try {
            String impid = req.getParameter("impid");
            String mid = req.getParameter("mid");
            String plcmtid = req.getParameter("plcmtid");
            String policyid = req.getParameter("policyid");
            String ext = req.getParameter("ext");

            //args check
            if (StringUtils.isEmpty(impid) || StringUtils.isEmpty(mid) || StringUtils.isEmpty(plcmtid) || StringUtils.isEmpty(policyid) || StringUtils.isEmpty(ext)) {
                resp.setStatus(Constant.StatusCode.BAD_REQUEST);
                return;
            }

            //bid redis check
            String key = String.format(Constant.CommonKey.BID_RECORD, impid, mid, plcmtid, policyid);
            if (key.isEmpty()) {
                resp.setStatus(Constant.StatusCode.BAD_REQUEST);
                return;
            }

            PremiumMADDataModel.ClickTrack.Builder clickTrack = PremiumMADDataModel.ClickTrack.newBuilder();

            clickTrack.setTime(System.currentTimeMillis());
            clickTrack.setIp(HttpUtil.getRealIp(req));
            clickTrack.setUa(HttpUtil.getUserAgent(req));
            clickTrack.setImpid(impid);
            clickTrack.setMediaid(Long.parseLong(mid));
            clickTrack.setAdspaceid(Long.parseLong(plcmtid));
            clickTrack.setPolicyid(Long.parseLong(policyid));

            String[] exts = ext.split(",");
            if (exts.length >= 3) {
                PremiumMADDataModel.ClickTrack.Ext.Builder var1 = PremiumMADDataModel.ClickTrack.Ext.newBuilder();
                var1.setDspid(Long.parseLong(exts[0]));
                var1.setIncome(Integer.parseInt(exts[1]));
                var1.setCost(Integer.parseInt(exts[2]));
                clickTrack.setExt(var1);

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

                LoggerUtil.getInstance().writeClickTrackLog(ResourceManager.getInstance().getKafkaProducer(), clickTrack.build());
                resp.setStatus(clickTrack.getStatus());
                return;
            }
        } catch (Exception ex) {
            System.err.println(ex.toString());
        }

        resp.setStatus(Constant.StatusCode.BAD_REQUEST);
    }

    public void onBid(HttpServletRequest req, HttpServletResponse resp) {
        this.redisMaster = ResourceManager.getInstance().getJedisPoolMaster().getResource();
        this.redisSlave = ResourceManager.getInstance().getJedisPoolSlave().getResource();

        MediaBaseHandler mediaBaseHandler = ResourceManager.getInstance().getMediaApiType(req.getRequestURI());
        if (mediaBaseHandler == null) {
            resp.setStatus(Constant.StatusCode.BAD_REQUEST);
            return;
        }

        MediaBidMetaData mediaBidMetaData = new MediaBidMetaData();
        PremiumMADDataModel.MediaBid.Builder mediaBidBuilder = PremiumMADDataModel.MediaBid.newBuilder();

        //init mediaBid object
        mediaBidBuilder.setImpid(StringUtil.getUUID());
        mediaBidBuilder.setIp(HttpUtil.getRealIp(req));
        mediaBidBuilder.setUa(HttpUtil.getUserAgent(req));
        mediaBidBuilder.setTime(System.currentTimeMillis());
        mediaBidBuilder.setStatus(Constant.StatusCode.BAD_REQUEST);

        mediaBidMetaData.setMediaBidBuilder(mediaBidBuilder);

        //parse media request
        if (mediaBaseHandler != null) {
            if (!mediaBaseHandler.parseMediaRequest(req, mediaBidMetaData, resp)) {
                return;
            }
        }

        mediaBidBuilder.setStatus(Constant.StatusCode.NO_CONTENT);
        PremiumMADDataModel.MediaBid.MediaRequest.Builder mediaRequest = mediaBidBuilder.getRequestBuilder();

        //get placement metadata
        PlcmtMetaData plcmtMetaData = CacheManager.getInstance().getPlcmtMetaData(mediaRequest.getAdspacekey());
        if (plcmtMetaData == null) {
            resp.setStatus(Constant.StatusCode.BAD_REQUEST);
            return;
        }

        MediaMetaData mediaMetaData = CacheManager.getInstance().getMediaMetaData(plcmtMetaData.getMediaId());
        if (mediaMetaData == null) {
            resp.setStatus(Constant.StatusCode.BAD_REQUEST);
            return;
        }

        if (mediaMetaData.getStatus() <= 0 || plcmtMetaData.getStatus() <= 0) {
            resp.setStatus(Constant.StatusCode.NO_CONTENT);
            return;
        }

        //init mediaid, adspaceid
        mediaRequest.setMediaid(mediaMetaData.getId());
        mediaRequest.setAdspaceid(plcmtMetaData.getId());

        //init user ip
        if (!mediaRequest.hasIp()) {
            mediaRequest.setIp(mediaBidBuilder.getIp());
        }

        //init user ua
        if (!mediaRequest.hasUa()) {
            mediaRequest.setUa(mediaBidBuilder.getUa());
        }

        //init location
        String location = ResourceManager.getInstance().getLocation(mediaRequest.getIp());
        if (location == null) {
            resp.setStatus(Constant.StatusCode.BAD_REQUEST);
            return;
        }

        mediaRequest.setLocation(location);

        //bidfloor, bidtype
        mediaRequest.setBidfloor(plcmtMetaData.getBidFloor());
        mediaRequest.setBidtype(plcmtMetaData.getBidType());

        //get block metadata
        long blockid = plcmtMetaData.getBlockId();
        AdBlockMetaData adBlockMetaData = null;
        if (blockid > 0) {
            adBlockMetaData = CacheManager.getInstance().getAdBlockMetaData(blockid);
            if (adBlockMetaData == null) {
                this.internalError(resp, mediaBidBuilder, Constant.StatusCode.INTERNAL_ERROR);
                return;
            }
        }

        //policy targeting
        List<Long> policyList = this.policyTargeting(mediaRequest);
        if (policyList == null || policyList.isEmpty()) {
            this.internalError(resp, mediaBidBuilder, Constant.StatusCode.NO_CONTENT);
            return;
        }

        //get policy detail
        List<PolicyMetaData> policyMetaDatas = this.getPolicyMetaData(policyList);
        if (policyMetaDatas == null || policyMetaDatas.isEmpty()) {
            return;
        }

        PolicyMetaData policyMetaData = null;
        List<Pair<PolicyMetaData, Integer>> selectedPolicys = this.selectPolicy(policyMetaDatas);

        while ((policyMetaData = Utility.randomWithWeights(selectedPolicys)) != null) {
            this.multiHttpClient.reset();

            Map<Long, Pair<DSPMetaData, DSPBidMetaData>> selectedDspList = new HashMap<>();
            for (PolicyMetaData.DSPInfo dspInfo : policyMetaData.getDspInfoList()) {
                DSPMetaData dspMetaData = CacheManager.getInstance().getDSPMetaData(dspInfo.getId());
                if (dspInfo.getStatus() > 0 && dspMetaData.getStatus() > 0) {

                    //QPS Contorl
                    String qpsKey = String.format(Constant.CommonKey.DSP_QPS_CONTROL, dspInfo.getId(), System.currentTimeMillis() / 1000);

                    this.redisMaster.set(qpsKey, "0", "NX", "EX", 3);
                    long totalCount = this.redisMaster.incrBy(qpsKey, 1);
                    if (totalCount >= dspMetaData.getMaxQPS()) {
                        continue;
                    }

                    DSPBidMetaData dspBidMetaData = new DSPBidMetaData();

                    PremiumMADDataModel.DSPBid.Builder dspBidBuilder = PremiumMADDataModel.DSPBid.newBuilder();
                    dspBidMetaData.setDspBidBuilder(dspBidBuilder);

                    DSPBaseHandler dspBaseHandler = ResourceManager.getInstance().getDSPHandler(dspMetaData.getApiType());
                    dspBidMetaData.setDspBaseHandler(dspBaseHandler);

                    HttpRequestBase httpRequestBase = dspBaseHandler.packageBidRequest(mediaBidBuilder, mediaMetaData, plcmtMetaData, adBlockMetaData, policyMetaData, dspMetaData, dspBidMetaData);
                    if (httpRequestBase != null) {
                        HttpClient httpClient = dspMetaData.getHttpClient();
                        httpClient.setHttpRequest(httpRequestBase, mediaMetaData.getTimeout());
                        this.multiHttpClient.addHttpClient(httpClient);
                        dspBidMetaData.setHttpRequestBase(httpRequestBase);
                        selectedDspList.put(dspInfo.getId(), Pair.of(dspMetaData, dspBidMetaData));
                    }
                }
            }

            if (!this.multiHttpClient.isEmpty() && this.multiHttpClient.execute()) {
                if (policyMetaData.getControlType() != Constant.PolicyControlType.NULL) {
                    if (policyMetaData.getControlType() == Constant.PolicyControlType.TOTAL) {
                        String text = this.redisSlave.get(String.format(Constant.CommonKey.POLICY_CONTORL_TOTAL, policyMetaData.getId()));
                        long count = StringUtils.isEmpty(text) ? 0 : Long.parseLong(text);
                        if (count >= policyMetaData.getMaxCount()) {
                            CacheManager.getInstance().blockPolicy(policyMetaData.getId());
                        }
                    } else {
                        String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                        String text = this.redisSlave.get(String.format(Constant.CommonKey.POLICY_CONTORL_DAY, policyMetaData.getId(), currentDate));
                        long count = StringUtils.isEmpty(text) ? 0 : Long.parseLong(text);
                        if (count >= policyMetaData.getMaxCount()) {
                            CacheManager.getInstance().blockPolicy(policyMetaData.getId());
                        }
                    }
                }

                List<Pair<DSPMetaData, DSPBidMetaData>> bidDspList = new LinkedList<Pair<DSPMetaData, DSPBidMetaData>>();
                for (Map.Entry entry : selectedDspList.entrySet()) {
                    Pair<DSPMetaData, DSPBidMetaData> dspInfo = (Pair<DSPMetaData, DSPBidMetaData>)entry.getValue();
                    DSPMetaData dspMetaData = dspInfo.getLeft();
                    DSPBidMetaData dspBidMetaData = dspInfo.getRight();
                    DSPBaseHandler dspBaseHandler = dspBidMetaData.getDspBaseHandler();
                    HttpResponse httpResponse = dspMetaData.getHttpClient().getResp();
                    if (httpResponse != null) {
                        if (dspBaseHandler.parseBidResponse(httpResponse, dspBidMetaData)) {
                            if (policyMetaData.getDeliveryType() != Constant.DeliveryType.RTB || dspBidMetaData.getPrice() >= plcmtMetaData.getBidFloor()) {
                                bidDspList.add(Pair.of(dspMetaData, dspBidMetaData));
                            }
                        }
                    } else {
                        dspBidMetaData.getDspBidBuilder().setStatus(Constant.StatusCode.REQUEST_TIMEOUT);
                    }

                    dspBidMetaData.getHttpRequestBase().releaseConnection();
                }

                if (!bidDspList.isEmpty()) {
                    Pair<DSPMetaData, Pair<DSPBidMetaData, Integer>> winner = this.selectWinner(plcmtMetaData, policyMetaData, bidDspList);
                    if (winner != null) {
                        DSPBidMetaData dspBidMetaData = winner.getRight().getLeft();
                        if (this.packageMediaResponse(dspBidMetaData.getDspBidBuilder(), mediaBidMetaData.getMediaBidBuilder())) {
                            mediaBaseHandler.packageMediaResponse(mediaBidMetaData, resp);
                            if (policyMetaData.getDeliveryType() == Constant.DeliveryType.RTB) {
                                String url = dspBidMetaData.getDspBaseHandler().getWinNoticeUrl(dspBidMetaData.getPrice(), winner.getLeft(), dspBidMetaData);
                                final HttpGet httpGet = new HttpGet(url);
                                final HttpClient httpClient = this.winNoticeHttpClient;
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

                break;
            }

            selectedPolicys.remove(policyMetaData);
        }
    }

    private boolean packageMediaResponse(PremiumMADDataModel.DSPBid.Builder dspBidBuilder, PremiumMADDataModel.MediaBid.Builder mediaBidBuilder) {
        mediaBidBuilder.setStatus(Constant.StatusCode.NO_CONTENT);

        if (dspBidBuilder != null && dspBidBuilder.getStatus() == Constant.StatusCode.OK && dspBidBuilder.getResponseBuilder() != null) {
            PremiumMADRTBProtocol.BidResponse.Builder bidResponse = dspBidBuilder.getResponseBuilder();
            if (bidResponse.hasNbr() && bidResponse.getNbr() >= 0) {
                return false;
            }

            if (bidResponse.getSeatbidCount() > 0 && bidResponse.getSeatbid(0).getBidCount() > 0) {
                try {
                    PremiumMADRTBProtocol.BidResponse.SeatBid.Bid bid = bidResponse.getSeatbid(0).getBid(0);
                    PremiumMADDataModel.MediaBid.MediaResponse.Builder mediaResponse = PremiumMADDataModel.MediaBid.MediaResponse.newBuilder();

                    mediaResponse.setDspid(dspBidBuilder.getDspid());
                    mediaResponse.setAdmid(bid.getAdmid());
                    mediaResponse.setLayout(dspBidBuilder.getRequest().getLayout());

                    if (bid.getAdmCount() > 0) {
                        mediaResponse.addAllAdm(bid.getAdmList());
                    } else {
                        for (PremiumMADRTBProtocol.BidResponse.SeatBid.Bid.NativeResponse.Asset asset : bid.getAdmNative().getAssetsList()) {
                            if (asset.hasTitle()) {
                                mediaResponse.setTitle(asset.getTitle().getText());
                                continue;
                            }

                            if (asset.hasData()) {
                                mediaResponse.setDesc(asset.getData().getValue());
                                continue;
                            }

                            if (asset.hasImage()) {
                                if (asset.getImage().hasType()) {
                                    switch (asset.getImage().getType()) {
                                        case Constant.NativeImageType.MAIN: {
                                            mediaResponse.addAllAdm(asset.getImage().getUrlList());
                                            break;
                                        }

                                        case Constant.NativeImageType.ICON: {
                                            mediaResponse.setIcon(asset.getImage().getUrl(0));
                                            break;
                                        }

                                        case Constant.NativeImageType.COVER: {
                                            mediaResponse.setCover(asset.getImage().getUrl(0));
                                            break;
                                        }

                                        default: {
                                            break;
                                        }
                                    }
                                } else {
                                    mediaResponse.addAllAdm(asset.getImage().getUrlList());
                                }

                                continue;
                            }

                            if (asset.hasVideo()) {
                                mediaResponse.addAdm(asset.getVideo().getUrl());
                                mediaResponse.setDuration(asset.getVideo().getDuration());
                            }
                        }
                    }

                    mediaResponse.setLpgurl(bid.getLpgurl());
                    mediaResponse.setActtype(bid.getActtype());
                    PremiumMADRTBProtocol.BidResponse.SeatBid.Bid.Monitor monitor = bid.getMonitor();
                    mediaResponse.setMonitor(PremiumMADRTBProtocol.BidResponse.SeatBid.Bid.Monitor.newBuilder(monitor));

                    mediaBidBuilder.setResponse(mediaResponse);
                    mediaBidBuilder.setStatus(Constant.StatusCode.OK);

                    return true;

                } catch (Exception ex) {
                    System.err.println(ex.toString());
                    mediaBidBuilder.setStatus(Constant.StatusCode.INTERNAL_ERROR);
                    return false;
                }
            }
        }

        return false;
    }

    private List<Long> policyTargeting(PremiumMADDataModel.MediaBid.MediaRequest.Builder mediaRequest) {
        List<Pair<Integer, List<String>>> targetInfo = new LinkedList<>();

        //placement
        {
            List<String> info = new LinkedList<>();
            info.add(Long.toString(mediaRequest.getAdspaceid()));
            targetInfo.add(Pair.of(Constant.TargetType.PLACEMENT, info));
        }

        //week time
        {
            List<String> info = new LinkedList<>();
            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            int weekday = cal.get(Calendar.DAY_OF_WEEK) - 1;
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            info.add(String.format("%d%02d", weekday, hour));
            targetInfo.add(Pair.of(Constant.TargetType.WEEK_HOUR, info));
        }

        //location
        {
            List<String> info = new LinkedList<>();
            info.add(mediaRequest.getLocation().substring(0, 4) + "*");
            info.add(mediaRequest.getLocation().substring(0, 6) + "*");
            info.add(mediaRequest.getLocation());
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

        List<Set<Long>> targetPolicy = new LinkedList<>();
        for (Pair<Integer, List<String>> info : targetInfo) {
            List<Set<Long>> policys = new LinkedList<>();

            Set<Long> policy = CacheManager.getInstance().getPolicyTargetInfo(String.format(Constant.CommonKey.TARGET_KEY, info.getLeft(), ""));
            if (policy != null) {
                policys.add(policy);
            }

            for (String key : info.getRight()) {
                policy = CacheManager.getInstance().getPolicyTargetInfo(String.format(Constant.CommonKey.TARGET_KEY, info.getLeft(), key));
                if (policy != null) {
                    policys.add(policy);
                }
            }

            targetPolicy.add(SetUtil.multiSetUnion(policys));
        }

        return new LinkedList<>(SetUtil.setDiff(SetUtil.multiSetInter(targetPolicy), CacheManager.getInstance().getBlockedPolicy()));
    }

    private void internalError(HttpServletResponse resp, PremiumMADDataModel.MediaBid.Builder mediaBidBuilder, int statusCode) {
        try {
            resp.setStatus(statusCode);
            mediaBidBuilder.setStatus(statusCode);
            LoggerUtil.getInstance().writeMediaLog(ResourceManager.getInstance().getKafkaProducer(), mediaBidBuilder.build());
        } catch (Exception ex) {
            LoggerUtil.getInstance().getBaseLogger(Constant.TopicType.MEDIA_BID).info(mediaBidBuilder.build());
        }
    }

    private List<PolicyMetaData> getPolicyMetaData(List<Long> policyList) {

        List<PolicyMetaData> policyMetaDatas = new LinkedList<PolicyMetaData>();
        for (long policyId : policyList) {
            PolicyMetaData policyMetaData = CacheManager.getInstance().getPolicyMetaData(policyId);
            if (policyMetaData != null) {
                policyMetaDatas.add(policyMetaData);
            }
        }

        policyMetaDatas.sort(new Comparator<PolicyMetaData>() {
            public int compare(PolicyMetaData o1, PolicyMetaData o2) {
                return (o1.getDeliveryType() < o2.getDeliveryType()) ? 1 : -1;
            }
        });

        return policyMetaDatas;
    }

    private List<Pair<PolicyMetaData, Integer>> selectPolicy(List<PolicyMetaData> policyMetaDatas) {

        int selectType = -1;
        List<Pair<PolicyMetaData, Integer>> selectedPolicys = new LinkedList<>();
        for (PolicyMetaData policyMetaData : policyMetaDatas) {
            if (selectType > 0 && selectType != policyMetaData.getDeliveryType()) {
                break;
            } else {
                selectType = policyMetaData.getDeliveryType();
                selectedPolicys.add(Pair.of(policyMetaData, policyMetaData.getWeight()));
                policyMetaDatas.remove(policyMetaData);
            }
        }

        return selectedPolicys;
    }

    private Pair<DSPMetaData, Pair<DSPBidMetaData, Integer>> selectWinner(PlcmtMetaData plcmtMetaData,
                                                                   PolicyMetaData policyMetaData,
                                                                   List<Pair<DSPMetaData, DSPBidMetaData>> bidDspList) {
        Pair<DSPMetaData, Pair<DSPBidMetaData, Integer>> winner = null;

        if (policyMetaData.getDeliveryType() != Constant.DeliveryType.RTB) {
            List<Pair<Pair<DSPMetaData, DSPBidMetaData>, Integer>> selectedDspList = new LinkedList<Pair<Pair<DSPMetaData, DSPBidMetaData>, Integer>>();
            Map<Long, PolicyMetaData.DSPInfo> dspInfoMap = policyMetaData.getDspInfoMap();

            for (Pair<DSPMetaData, DSPBidMetaData> entry : bidDspList) {
                DSPMetaData dspMetaData = (DSPMetaData)entry.getLeft();
                int weight = dspInfoMap.get(dspMetaData.getId()).getWeight();
                if (weight > 0) {
                    selectedDspList.add(Pair.of(Pair.of(dspMetaData, entry.getRight()), weight));
                }
            }

            if (!selectedDspList.isEmpty()) {
                Pair<DSPMetaData, DSPBidMetaData> selected = Utility.randomWithWeights(selectedDspList);
                winner = Pair.of(selected.getLeft(), Pair.of(selected.getRight(), 0));
            }
        } else {
            bidDspList.sort(new Comparator<Pair<DSPMetaData, DSPBidMetaData>>() {
                public int compare(Pair<DSPMetaData, DSPBidMetaData> o1, Pair<DSPMetaData, DSPBidMetaData> o2) {
                    return o1.getRight().getPrice() > o2.getRight().getPrice() ? 1 : -1;
                }
            });

            int price = plcmtMetaData.getBidFloor();
            if (bidDspList.size() >= 2) {
                DSPBidMetaData dspBidMetaData = bidDspList.get(1).getRight();
                price = dspBidMetaData.getPrice();
            }

            winner = Pair.of(bidDspList.get(0).getLeft(), Pair.of(bidDspList.get(0).getRight(), price + 1));
        }

        return winner;
    }
}


