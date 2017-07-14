package com.madhouse.ssp;

import com.madhouse.cache.*;
import com.madhouse.dsp.DSPBaseHandler;
import com.madhouse.media.MediaBaseHandler;
import com.madhouse.resource.ResourceManager;
import com.madhouse.rtb.PremiumMADRTBProtocol;
import com.madhouse.ssp.avro.*;
import com.madhouse.util.*;
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

            ImpressionTrack.Builder impressionTrack = ImpressionTrack.newBuilder();

            //bid redis check
            this.redisSlave = ResourceManager.getInstance().getJedisPoolSlave().getResource();
            String recordKey = String.format(Constant.CommonKey.BID_RECORD, impid, mid, plcmtid, policyid);
            if (this.redisSlave.exists(recordKey)) {
                impressionTrack.setValid(true);
            } else {
                impressionTrack.setValid(false);
            }

            impressionTrack.setTime(System.currentTimeMillis());
            impressionTrack.setIp(HttpUtil.getRealIp(req));
            impressionTrack.setUa(HttpUtil.getUserAgent(req));
            impressionTrack.setImpid(impid);
            impressionTrack.setMediaid(Long.parseLong(mid));
            impressionTrack.setAdspaceid(Long.parseLong(plcmtid));
            impressionTrack.setPolicyid(Long.parseLong(policyid));

            String[] exts = ext.split(",");
            if (exts.length >= 3) {
                impressionTrack.setExt(ext);
                impressionTrack.setDspid(Long.parseLong(exts[0]));
                impressionTrack.setIncome(Integer.parseInt(exts[1]));
                impressionTrack.setCost(Integer.parseInt(exts[2]));

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

            ClickTrack.Builder clickTrack = ClickTrack.newBuilder();

            //bid redis check
            this.redisSlave = ResourceManager.getInstance().getJedisPoolSlave().getResource();
            String recordKey = String.format(Constant.CommonKey.BID_RECORD, impid, mid, plcmtid, policyid);
            if (this.redisSlave.exists(recordKey)) {
                clickTrack.setValid(true);
            } else {
                clickTrack.setValid(false);
            }

            clickTrack.setTime(System.currentTimeMillis());
            clickTrack.setIp(HttpUtil.getRealIp(req));
            clickTrack.setUa(HttpUtil.getUserAgent(req));
            clickTrack.setImpid(impid);
            clickTrack.setMediaid(Long.parseLong(mid));
            clickTrack.setAdspaceid(Long.parseLong(plcmtid));
            clickTrack.setPolicyid(Long.parseLong(policyid));

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
        /*this.redisMaster = ResourceManager.getInstance().getJedisPoolMaster().getResource();
        this.redisSlave = ResourceManager.getInstance().getJedisPoolSlave().getResource();*/

        MediaBaseHandler mediaBaseHandler = ResourceManager.getInstance().getMediaApiType(req.getRequestURI());
        if (mediaBaseHandler == null) {
            resp.setStatus(Constant.StatusCode.BAD_REQUEST);
            return;
        }

        MediaBidMetaData mediaBidMetaData = new MediaBidMetaData();
        MediaBid.Builder mediaBidBuilder = MediaBid.newBuilder();

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
        MediaRequest.Builder mediaRequest = mediaBidBuilder.getRequestBuilder();

        //get placement metadata
        PlcmtMetaData plcmtMetaData = CacheManager.getInstance().getPlcmtMetaData(mediaRequest.getAdspacekey().toString());
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
        String location = ResourceManager.getInstance().getLocation(mediaRequest.getIp().toString());
        if (location == null) {
            resp.setStatus(Constant.StatusCode.BAD_REQUEST);
            return;
        }

        mediaBidBuilder.setLocation(location);

        //bidfloor, bidtype
        mediaBidBuilder.setBidfloor(plcmtMetaData.getBidFloor());
        mediaBidBuilder.setBidtype(plcmtMetaData.getBidType());

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

        int[] deliveryTypes = {Constant.DeliveryType.PDB, Constant.DeliveryType.PD, Constant.DeliveryType.RTB};

        for (int i = 0; i < deliveryTypes.length; ++i) {
            //policy targeting
            List<Long> policyList = this.policyTargeting(mediaBidBuilder, deliveryTypes[i]);
            if (policyList == null || policyList.isEmpty()) {
                continue;
            }

            //get policy detail
            List<Pair<PolicyMetaData, Integer>> policyMetaDatas = this.getPolicyMetaData(policyList);
            if (policyMetaDatas == null || policyMetaDatas.isEmpty()) {
                continue;
            }

            int selectedIndex = -1;
            while ((selectedIndex = Utility.randomWithWeights(policyMetaDatas)) != -1) {
                PolicyMetaData policyMetaData = policyMetaDatas.get(selectedIndex).getLeft();

                this.multiHttpClient.reset();

                Map<Long, DSPBidMetaData> selectedDspList = new HashMap<>();
                for (Map.Entry entry : policyMetaData.getDspInfoMap().entrySet()) {
                    PolicyMetaData.DSPInfo dspInfo = (PolicyMetaData.DSPInfo)entry.getValue();

                    DSPMetaData dspMetaData = CacheManager.getInstance().getDSPMetaData(dspInfo.getId());
                    if (dspInfo.getStatus() > 0 && dspMetaData != null && dspMetaData.getStatus() > 0) {

                        //QPS Contorl
                        String qpsControl = String.format(Constant.CommonKey.DSP_QPS_CONTROL, dspInfo.getId(), System.currentTimeMillis() / 1000);
                        this.redisMaster.set(qpsControl, "0", "NX", "EX", 3);
                        long totalCount = this.redisMaster.incrBy(qpsControl, 1);
                        if (totalCount >= dspMetaData.getMaxQPS()) {
                            continue;
                        }

                        DSPBidMetaData dspBidMetaData = new DSPBidMetaData();
                        dspBidMetaData.setDspMetaData(dspMetaData);

                        DSPBid.Builder dspBidBuilder = DSPBid.newBuilder();
                        dspBidMetaData.setDspBidBuilder(dspBidBuilder);

                        DSPBaseHandler dspBaseHandler = ResourceManager.getInstance().getDSPHandler(dspMetaData.getApiType());
                        dspBidMetaData.setDspBaseHandler(dspBaseHandler);

                        HttpRequestBase httpRequestBase = dspBaseHandler.packageBidRequest(mediaBidBuilder, mediaMetaData, plcmtMetaData, adBlockMetaData, policyMetaData, dspBidMetaData);
                        if (httpRequestBase != null) {
                            HttpClient httpClient = dspMetaData.getHttpClient();
                            httpClient.setHttpRequest(httpRequestBase, mediaMetaData.getTimeout());
                            this.multiHttpClient.addHttpClient(httpClient);
                            dspBidMetaData.setHttpRequestBase(httpRequestBase);
                            selectedDspList.put(dspInfo.getId(), dspBidMetaData);
                        }
                    }
                }

                if (!this.multiHttpClient.isEmpty() && this.multiHttpClient.execute()) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(new Date());

                    int weekDay = cal.get(Calendar.DAY_OF_WEEK) - 1;
                    int currentHour = cal.get(Calendar.HOUR_OF_DAY);
                    String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());

                    if (policyMetaData.getControlType() != Constant.PolicyControlType.NULL) {
                        if (policyMetaData.getControlType() == Constant.PolicyControlType.TOTAL) {
                            if (policyMetaData.getControlMethod() == Constant.PolicyControlMethod.AVERAGE) {
                                int pastDays = Utility.dateDiff(StringUtil.toDate(currentDate), StringUtil.toDate(policyMetaData.getStartDate())) + 1;
                                int totalDays = Utility.dateDiff(StringUtil.toDate(policyMetaData.getEndDate()), StringUtil.toDate(policyMetaData.getStartDate())) + 1;

                                long count = this.redisMaster.incr(String.format(Constant.CommonKey.POLICY_CONTORL_DAILY, policyMetaData.getId(), currentDate));
                                if (count >= ((double)policyMetaData.getMaxCount() * pastDays / totalDays)) {
                                    CacheManager.getInstance().blockPolicy(policyMetaData.getId());
                                }
                            } else {
                                long count = this.redisMaster.incr(String.format(Constant.CommonKey.POLICY_CONTORL_TOTAL, policyMetaData.getId()));
                                if (count >= policyMetaData.getMaxCount()) {
                                    CacheManager.getInstance().blockPolicy(policyMetaData.getId());
                                }
                            }
                        } else {
                            if (policyMetaData.getControlMethod() == Constant.PolicyControlMethod.AVERAGE) {
                                int pastHours = 0;
                                int totalHours = 0;
                                if (ObjectUtils.isEmpty(policyMetaData.getWeekDayHours())) {
                                    pastHours = currentHour + 1;
                                    totalHours = 24;
                                } else {
                                    List<Integer> hours = policyMetaData.getWeekDayHours().get(weekDay);
                                    for (int hour : hours) {
                                        if (hour <= currentHour) {
                                            pastHours += 1;
                                        } else {
                                            break;
                                        }
                                    }

                                    totalHours = hours.size();
                                }

                                long count = this.redisMaster.incr(String.format(Constant.CommonKey.POLICY_CONTORL_DAILY, policyMetaData.getId(), currentDate));
                                if (count >= ((double)policyMetaData.getMaxCount() * pastHours / totalHours)) {
                                    CacheManager.getInstance().blockPolicy(policyMetaData.getId());
                                }
                            } else {
                                long count = this.redisMaster.incr(String.format(Constant.CommonKey.POLICY_CONTORL_DAILY, policyMetaData.getId(), currentDate));
                                if (count >= policyMetaData.getMaxCount()) {
                                    CacheManager.getInstance().blockPolicy(policyMetaData.getId());
                                }
                            }

                        }
                    }

                    List<DSPBidMetaData> dspBidderList = new LinkedList<DSPBidMetaData>();
                    for (Map.Entry entry : selectedDspList.entrySet()) {
                        DSPBidMetaData dspBidMetaData = (DSPBidMetaData)entry.getValue();
                        DSPMetaData dspMetaData = dspBidMetaData.getDspMetaData();
                        DSPBaseHandler dspBaseHandler = dspBidMetaData.getDspBaseHandler();
                        HttpResponse httpResponse = dspMetaData.getHttpClient().getResp();
                        if (httpResponse != null) {
                            if (dspBaseHandler.parseBidResponse(httpResponse, dspBidMetaData)) {
                                if (policyMetaData.getDeliveryType() != Constant.DeliveryType.RTB || dspBidMetaData.getDspBidBuilder().getResponse().getPrice() >= plcmtMetaData.getBidFloor()) {
                                    dspBidderList.add(dspBidMetaData);
                                }
                            }
                        } else {
                            dspBidMetaData.getDspBidBuilder().setStatus(Constant.StatusCode.REQUEST_TIMEOUT);
                        }

                        dspBidMetaData.getHttpRequestBase().releaseConnection();
                    }

                    if (!dspBidderList.isEmpty()) {
                        Pair<DSPBidMetaData, Integer> winner = this.selectWinner(plcmtMetaData, policyMetaData, dspBidderList);
                        if (winner != null) {
                            DSPBidMetaData dspBidMetaData = winner.getLeft();
                            dspBidMetaData.getDspBidBuilder().setPrice(winner.getRight());

                            String recordKey = String.format(Constant.CommonKey.BID_RECORD, mediaBidBuilder.getImpid(), Long.toString(mediaMetaData.getId()), Long.toString(plcmtMetaData.getId()), Long.toString(policyMetaData.getId()));
                            this.redisMaster.set(recordKey, Long.toString(System.currentTimeMillis()), "nx", "ex", 86400);

                            if (this.createMediaResponse(dspBidMetaData.getDspBidBuilder(), mediaBidMetaData.getMediaBidBuilder())) {
                                mediaBaseHandler.packageMediaResponse(mediaBidMetaData, resp);
                                if (policyMetaData.getDeliveryType() == Constant.DeliveryType.RTB) {
                                    String url = dspBidMetaData.getDspBaseHandler().getWinNoticeUrl(dspBidMetaData);
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

                policyMetaDatas.remove(selectedIndex);
            }
        }
    }

    private boolean createMediaResponse(DSPBid.Builder dspBidBuilder, MediaBid.Builder mediaBidBuilder) {
        mediaBidBuilder.setStatus(Constant.StatusCode.NO_CONTENT);

        if (dspBidBuilder != null && dspBidBuilder.getStatus() == Constant.StatusCode.OK && dspBidBuilder.getResponse() != null) {
            DSPResponse dspResponse = dspBidBuilder.getResponse();

            try {
                MediaResponse.Builder mediaResponse = MediaResponse.newBuilder();

                mediaResponse.setDspid(dspBidBuilder.getDspid());
                mediaResponse.setAdmid(dspResponse.getAdmid());
                mediaResponse.setLayout(dspBidBuilder.getRequest().getLayout());
                mediaResponse.setTitle(dspResponse.getTitle());
                mediaResponse.setDesc(dspResponse.getDesc());
                mediaResponse.setIcon(dspResponse.getIcon());
                mediaResponse.setCover(dspResponse.getCover());
                mediaResponse.setAdm(dspResponse.getAdm());
                mediaResponse.setDealid(dspResponse.getDealid());
                mediaResponse.setDuration(dspResponse.getDuration());
                mediaResponse.setLpgurl(dspResponse.getLpgurl());
                mediaResponse.setActtype(dspResponse.getActtype());
                mediaResponse.setMonitorBuilder(Monitor.newBuilder(dspResponse.getMonitor()));

                mediaBidBuilder.setResponseBuilder(mediaResponse);
                mediaBidBuilder.setStatus(Constant.StatusCode.OK);

                return true;

            } catch (Exception ex) {
                System.err.println(ex.toString());
                mediaBidBuilder.setStatus(Constant.StatusCode.INTERNAL_ERROR);
            }
        } else {
            mediaBidBuilder.setStatus(Constant.StatusCode.INTERNAL_ERROR);
        }

        return false;
    }

    private List<Long> policyTargeting(MediaBid.Builder mediaBidBuilder, int deliveryType) {
        List<Pair<Integer, List<String>>> targetInfo = new LinkedList<>();

        MediaRequest mediaRequest = mediaBidBuilder.getRequest();

        //placement
        {
            List<String> info = new LinkedList<>();
            String adspaceKey = String.format("%d-%s", mediaRequest.getAdspaceid(), StringUtil.toString(mediaRequest.getDealid().toString()));
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
            targetInfo.add(Pair.of(Constant.TargetType.WEEK_HOUR, info));
        }

        //location
        {
            List<String> info = new LinkedList<>();
            info.add(mediaBidBuilder.getLocation().subSequence(0, 1) + "000000000");
            info.add(mediaBidBuilder.getLocation().subSequence(0, 4) + "000000");
            info.add(mediaBidBuilder.getLocation().subSequence(0, 6) + "0000");
            info.add(mediaBidBuilder.getLocation().toString());
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

    private void internalError(HttpServletResponse resp, MediaBid.Builder mediaBidBuilder, int statusCode) {
        try {
            resp.setStatus(statusCode);
            mediaBidBuilder.setStatus(statusCode);
            LoggerUtil.getInstance().writeMediaLog(ResourceManager.getInstance().getKafkaProducer(), mediaBidBuilder.build());
        } catch (Exception ex) {
            LoggerUtil.getInstance().getBaseLogger(Constant.TopicType.MEDIA_BID).info(mediaBidBuilder.build());
        }
    }

    private List<Pair<PolicyMetaData, Integer>> getPolicyMetaData(List<Long> policyList) {

        List<Pair<PolicyMetaData, Integer>> policyMetaDatas = new ArrayList<>(policyList.size());
        for (long policyId : policyList) {
            PolicyMetaData policyMetaData = CacheManager.getInstance().getPolicyMetaData(policyId);
            if (policyMetaData != null) {
                policyMetaDatas.add(Pair.of(policyMetaData, policyMetaData.getWeight()));
            }
        }

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

    private Pair<DSPBidMetaData, Integer> selectWinner(PlcmtMetaData plcmtMetaData,
                                                                   PolicyMetaData policyMetaData,
                                                                   List<DSPBidMetaData> dspBidderList) {
        Pair<DSPBidMetaData, Integer> winner = null;

        if (policyMetaData.getDeliveryType() != Constant.DeliveryType.RTB) {
            /*Map<DSPBidMetaData, Integer> selectedDspList = new HashMap<>();
            Map<Long, PolicyMetaData.DSPInfo> dspInfoMap = policyMetaData.getDspInfoMap();

            for (DSPBidMetaData dspBidMetaData : dspBidderList) {
                DSPMetaData dspMetaData = dspBidMetaData.getDspMetaData();
                int weight = dspInfoMap.get(dspMetaData.getId()).getWeight();
                if (weight > 0) {
                    selectedDspList.put(dspBidMetaData, weight);
                }
            }

            if (!selectedDspList.isEmpty()) {
                DSPBidMetaData dspBidMetaData = Utility.randomWithWeights(selectedDspList);
                winner = Pair.of(dspBidMetaData, policyMetaData.getAdspaceInfoMap().get(plcmtMetaData.getId()).getBidFloor());
            }*/

            winner = Pair.of(dspBidderList.get(0), policyMetaData.getAdspaceInfoMap().get(plcmtMetaData.getId()).getBidFloor());
        } else {
            dspBidderList.sort(new Comparator<DSPBidMetaData>() {
                @Override
                public int compare(DSPBidMetaData o1, DSPBidMetaData o2) {
                    return o1.getDspBidBuilder().getResponse().getPrice() > o2.getDspBidBuilder().getResponse().getPrice() ? 1 : -1;
                }
            });

            int price = plcmtMetaData.getBidFloor();
            if (dspBidderList.size() >= 2) {
                DSPBidMetaData dspBidMetaData = dspBidderList.get(1);
                price = dspBidMetaData.getDspBidBuilder().getResponse().getPrice();
            }

            winner = Pair.of(dspBidderList.get(0), price + 1);
        }

        return winner;
    }
}


