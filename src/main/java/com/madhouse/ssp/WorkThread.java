package com.madhouse.ssp;

import com.madhouse.cache.*;
import com.madhouse.dsp.DSPBaseHandler;
import com.madhouse.media.MediaBaseHandler;
import com.madhouse.resource.ResourceManager;
import com.madhouse.util.HttpUtil;
import com.madhouse.util.StringUtil;
import com.madhouse.util.Utility;
import com.madhouse.util.httpclient.MultiHttpClient;
import com.madhouse.util.httpclient.HttpClient;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by WUJUNFENG on 2017/5/23.
 */
public class WorkThread {
    private MultiHttpClient multiHttpClient = new MultiHttpClient();
    private CacheManager cacheManager = new CacheManager();
    private ResourceManager resourceManager = new ResourceManager();
    private HttpClient winNoticeHttpClient = new HttpClient();
    private ExecutorService winNoticeService = Executors.newCachedThreadPool();
    private final byte[] image = {  0x47, 0x49, 0x46, 0x38, 0x39, 0x61, 0x01, 0x00, 0x01, 0x00,
                                    (byte)0x80, 0x01, 0x00, 0x00, 0x00, 0x00, (byte)0xff, (byte)0xff, (byte)0xff, 0x21,
                                    (byte)0xf9, 0x04, 0x01, 0x00, 0x00, 0x01, 0x00, 0x2c, 0x00, 0x00,
                                    0x00, 0x00, 0x01, 0x00, 0x01, 0x00, 0x00, 0x02, 0x02, 0x4c, 0x01, 0x00, 0x3b};

    public boolean init() {
        return cacheManager.init() && resourceManager.init();
    }

    public void onImpression(HttpServletRequest req, HttpServletResponse resp) {
        try {
            PremiumMADDataModel.ImpressionTrack.Builder impressionTrack = PremiumMADDataModel.ImpressionTrack.newBuilder();
            impressionTrack.setTime(System.currentTimeMillis());
            impressionTrack.setIp(HttpUtil.getRealIp(req));
            impressionTrack.setUa(HttpUtil.getUserAgent(req));
            impressionTrack.setImpid(req.getParameter("impid"));
            impressionTrack.setMid(Long.parseLong(req.getParameter("mid")));
            impressionTrack.setPlcmtid(Long.parseLong(req.getParameter("plcmtid")));

            String[] exts = req.getParameter("ext").split(",");
            if (exts.length >= 3) {
                PremiumMADDataModel.ImpressionTrack.Ext.Builder ext = PremiumMADDataModel.ImpressionTrack.Ext.newBuilder();
                ext.setDspid(Long.parseLong(exts[0]));
                ext.setIncome(Integer.parseInt(exts[1]));
                ext.setCost(Integer.parseInt(exts[2]));
                impressionTrack.setExt(ext);
            }

            impressionTrack.setStatus(Constant.StatusCode.OK);
            LoggerUtil.getInstance().wirteImpressionTrackLog(this.resourceManager.getKafkaProducer(), impressionTrack.build());

            resp.getOutputStream().write(this.image);
            resp.setContentType("image/gif");
            resp.setContentLength(this.image.length);
            resp.setStatus(impressionTrack.getStatus());
        } catch (Exception ex) {
            System.err.println(ex.toString());
            resp.setStatus(Constant.StatusCode.BAD_REQUEST);
        }
    }

    public void onClick(HttpServletRequest req, HttpServletResponse resp) {
        try {
            PremiumMADDataModel.ClickTrack.Builder clickTrack = PremiumMADDataModel.ClickTrack.newBuilder();
            clickTrack.setTime(System.currentTimeMillis());
            clickTrack.setIp(HttpUtil.getRealIp(req));
            clickTrack.setUa(HttpUtil.getUserAgent(req));
            clickTrack.setImpid(req.getParameter("impid"));
            clickTrack.setMid(Long.parseLong(req.getParameter("mid")));
            clickTrack.setPlcmtid(Long.parseLong(req.getParameter("plcmtid")));

            String[] exts = req.getParameter("ext").split(",");
            if (exts.length >= 3) {
                PremiumMADDataModel.ClickTrack.Ext.Builder ext = PremiumMADDataModel.ClickTrack.Ext.newBuilder();
                ext.setDspid(Long.parseLong(exts[0]));
                ext.setIncome(Integer.parseInt(exts[1]));
                ext.setCost(Integer.parseInt(exts[2]));
                clickTrack.setExt(ext);
            }

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

            LoggerUtil.getInstance().writeClickTrackLog(this.resourceManager.getKafkaProducer(), clickTrack.build());
            resp.setStatus(clickTrack.getStatus());
        } catch (Exception ex) {
            System.err.println(ex.toString());
            resp.setStatus(Constant.StatusCode.BAD_REQUEST);
        }
    }

    public void onBid(HttpServletRequest req, HttpServletResponse resp) {

        int mediaApiType = this.getMediaApiType(req);
        if (mediaApiType <= 0) {
            resp.setStatus(Constant.StatusCode.BAD_REQUEST);
            return;
        }

        PremiumMADDataModel.MediaBid.Builder mediaBidBuilder = PremiumMADDataModel.MediaBid.newBuilder();

        //init mediaBid object
        mediaBidBuilder.setImpid(StringUtil.getUUID());
        mediaBidBuilder.setIp(HttpUtil.getRealIp(req));
        mediaBidBuilder.setUa(HttpUtil.getUserAgent(req));
        mediaBidBuilder.setTime(System.currentTimeMillis());
        mediaBidBuilder.setStatus(Constant.StatusCode.BAD_REQUEST);

        //parse media request
        MediaBaseHandler mediaBaseHandler = this.cacheManager.getMediaBaseHandler(mediaApiType);
        if (mediaBaseHandler.parseMediaRequest(req, mediaBidBuilder, resp)) {
            return;
        }

        mediaBidBuilder.setStatus(Constant.StatusCode.NO_CONTENT);

        PremiumMADDataModel.MediaBid.MediaRequest.Builder mediaRequestBuilder = mediaBidBuilder.getRequestBuilder();
        Pair<Long, Long> mediaInfo = this.cacheManager.mediaPlcmtMapping(mediaBidBuilder.getRequestBuilder().getAdspacekey());
        if (mediaInfo != null) {
            mediaRequestBuilder.setMid(mediaInfo.getLeft());
            mediaRequestBuilder.setPlcmtid(mediaInfo.getRight());
        } else {
            resp.setStatus(Constant.StatusCode.BAD_REQUEST);
            return;
        }

        MediaMetaData mediaMetaData = this.cacheManager.getMediaMetaData(mediaRequestBuilder.getMid());
        if (mediaMetaData == null) {
            resp.setStatus(Constant.StatusCode.BAD_REQUEST);
            return;
        }

        //init user ip
        if (!mediaRequestBuilder.hasIp()) {
            mediaRequestBuilder.setIp(mediaBidBuilder.getIp());
        }

        //init location
        String location = this.resourceManager.getLocation(mediaRequestBuilder.getIp());
        if (location == null) {
            resp.setStatus(Constant.StatusCode.BAD_REQUEST);
            return;
        }

        mediaRequestBuilder.setLocation(location);

        //get placement metadata
        PlcmtMetaData plcmtMetaData = this.cacheManager.getPlcmtMetaData(mediaRequestBuilder.getMid());
        if (plcmtMetaData == null) {
            resp.setStatus(Constant.StatusCode.BAD_REQUEST);
            return;
        }

        //init adtype, bidfloor, bidtype
        mediaRequestBuilder.setAdtype(plcmtMetaData.getType());
        mediaRequestBuilder.setBidfloor(plcmtMetaData.getBidfloor());
        mediaRequestBuilder.setBidtype(plcmtMetaData.getBidtype());

        if (!mediaRequestBuilder.hasW() || !mediaRequestBuilder.hasH()) {
            mediaRequestBuilder.setW(plcmtMetaData.getW());
            mediaRequestBuilder.setH(plcmtMetaData.getH());
        }

        //get block metadata
        long blockid = plcmtMetaData.getBlockid();
        AdBlockMetaData adBlockMetaData = null;
        if (blockid > 0) {
            adBlockMetaData = this.cacheManager.getAdBlockMetaData(blockid);
            if (adBlockMetaData == null) {
                this.internalError(resp, mediaBidBuilder, Constant.StatusCode.INTERNAL_ERROR);
                return;
            }
        }

        List<Long> policyList = this.policyTargeting(mediaRequestBuilder.build());
        if (policyList == null || policyList.isEmpty()) {
            this.internalError(resp, mediaBidBuilder, Constant.StatusCode.NO_CONTENT);
            return;
        }

        this.multiHttpClient.reset();
        PolicyMetaData policyMetaData = this.selectPolicy(policyList);
        Map<Long, Pair<BidMetaData, Pair<DSPMetaData, DSPBaseHandler>>> dspInfoList = new HashMap<Long, Pair<BidMetaData, Pair<DSPMetaData, DSPBaseHandler>>>();
        for (Map.Entry entry : policyMetaData.getDsplist().entrySet()) {
            long dspid = (Long)entry.getKey();
            DSPMetaData dspMetaData = this.cacheManager.getDSPMetaData(dspid);
            if (dspMetaData.isEnable()) {
                String tagid = this.cacheManager.dspPlcmtMapping(dspMetaData.getDspid(), mediaRequestBuilder.getMid(), mediaRequestBuilder.getPlcmtid());
                if (tagid == null) {
                    tagid = Long.toString(mediaRequestBuilder.getPlcmtid());
                }

                PremiumMADDataModel.DSPBid.Builder builder = PremiumMADDataModel.DSPBid.newBuilder();
                DSPBaseHandler dspBaseHandler = this.cacheManager.getDSPBaseHandler(dspMetaData.getApitype());
                HttpRequestBase httpRequestBase = dspBaseHandler.packageBidRequest(mediaBidBuilder, mediaMetaData, plcmtMetaData, adBlockMetaData, policyMetaData, dspMetaData, builder, tagid);
                if (httpRequestBase != null) {
                    BidMetaData bidMetaData = new BidMetaData();
                    bidMetaData.setBuilder(builder);
                    HttpClient httpClient = dspMetaData.getHttpClient();
                    httpClient.setHttpRequest(httpRequestBase, mediaMetaData.getTmax());
                    this.multiHttpClient.addHttpClient(httpClient);
                    dspInfoList.put(dspid, Pair.of(bidMetaData, Pair.of(dspMetaData, dspBaseHandler)));
                }
            }
        }

        if (this.multiHttpClient.execute()) {
            List<Pair<DSPMetaData, BidMetaData>> dspMetaDataList = new LinkedList<Pair<DSPMetaData, BidMetaData>>();
            for (Map.Entry entry : dspInfoList.entrySet()) {
                Pair<BidMetaData, Pair<DSPMetaData, DSPBaseHandler>> dspInfo = (Pair<BidMetaData, Pair<DSPMetaData, DSPBaseHandler>>)entry.getValue();
                BidMetaData bidMetaData = dspInfo.getLeft();
                PremiumMADDataModel.DSPBid.Builder builder = bidMetaData.getBuilder();
                DSPMetaData dspMetaData = dspInfo.getRight().getLeft();
                DSPBaseHandler dspBaseHandler = dspInfo.getRight().getRight();
                bidMetaData.setDspBaseHandler(dspBaseHandler);
                if (dspBaseHandler.parseBidResponse(dspMetaData.getHttpClient().getResp(), bidMetaData, builder)) {
                    if (policyMetaData.getTradingtype() != Constant.TradingType.RTB || bidMetaData.getPrice() >= plcmtMetaData.getBidfloor()) {
                        dspMetaDataList.add(Pair.of(dspMetaData, bidMetaData));
                    }
                }
            }

            if (!dspMetaDataList.isEmpty()) {
                Pair<DSPMetaData, Pair<BidMetaData, Integer>> winner = this.selectWinner(plcmtMetaData, policyMetaData, dspMetaDataList);
                if (winner != null) {
                    BidMetaData bidMetaData = winner.getRight().getLeft();
                    mediaBaseHandler.packageMediaResponse(bidMetaData.getBuilder(), resp, mediaBidBuilder);
                    if (policyMetaData.getTradingtype() == Constant.TradingType.RTB) {
                        String url = bidMetaData.getDspBaseHandler().getWinNoticeUrl(bidMetaData.getPrice(), winner.getLeft(), bidMetaData);
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
    }

    private List<Long> policyTargeting(PremiumMADDataModel.MediaBid.MediaRequest mediaRequest) {
        List<Long> policyList = new LinkedList<Long>();
        return policyList;
    }

    private void internalError(HttpServletResponse resp, PremiumMADDataModel.MediaBid.Builder mediaBidBuilder, int statusCode) {
        try {
            resp.setStatus(statusCode);
            mediaBidBuilder.setStatus(statusCode);
            LoggerUtil.getInstance().writeMediaLog(this.resourceManager.getKafkaProducer(), mediaBidBuilder.build());
        } catch (Exception ex) {

        }
    }

    private PolicyMetaData selectPolicy(List<Long> policyList) {

        List<PolicyMetaData> policyMetaDatas = new LinkedList<PolicyMetaData>();
        for (long policyId : policyList) {
            PolicyMetaData policyMetaData = this.cacheManager.getPolicyMetaData(policyId);
            if (policyMetaData != null) {
                policyMetaDatas.add(policyMetaData);
            }
        }

        policyMetaDatas.sort(new Comparator<PolicyMetaData>() {
            public int compare(PolicyMetaData o1, PolicyMetaData o2) {
                return (o1.getTradingtype() < o2.getTradingtype()) ? 1 : -1;
            }
        });

        int selectType = -1;
        List<Pair<PolicyMetaData, Integer>> policyMetaList = new LinkedList<Pair<PolicyMetaData, Integer>>();
        for (PolicyMetaData policyMetaData : policyMetaDatas) {
            if (selectType > 0 && selectType != policyMetaData.getTradingtype()) {
                break;
            } else {
                policyMetaList.add(Pair.of(policyMetaData, policyMetaData.getWeight()));
            }
        }

        return (Utility.randomWithWeights(policyMetaList));
    }

    private Pair<DSPMetaData, Pair<BidMetaData, Integer>> selectWinner(PlcmtMetaData plcmtMetaData,
                                                                   PolicyMetaData policyMetaData,
                                                                   List<Pair<DSPMetaData, BidMetaData>> dspMetaDataList) {
        Pair<DSPMetaData, Pair<BidMetaData, Integer>> winner = null;

        if (policyMetaData.getTradingtype() == Constant.TradingType.RTB) {
            dspMetaDataList.sort(new Comparator<Pair<DSPMetaData, BidMetaData>>() {
                public int compare(Pair<DSPMetaData, BidMetaData> o1, Pair<DSPMetaData, BidMetaData> o2) {
                    return o1.getRight().getPrice() > o2.getRight().getPrice() ? 1 : -1;
                }
            });

            int price = plcmtMetaData.getBidfloor();
            if (dspMetaDataList.size() >= 2) {
                BidMetaData bidMetaData = dspMetaDataList.get(1).getRight();
                price = bidMetaData.getPrice();
            }

            winner = Pair.of(dspMetaDataList.get(0).getLeft(), Pair.of(dspMetaDataList.get(0).getRight(), price + 1));
        } else {
            List<Pair<Pair<DSPMetaData, BidMetaData>, Integer>> dspList = new LinkedList<Pair<Pair<DSPMetaData, BidMetaData>, Integer>>();
            Map<Long, Integer> dsplist = policyMetaData.getDsplist();
            for (Pair<DSPMetaData, BidMetaData> entry : dspMetaDataList) {
                DSPMetaData dspMetaData = (DSPMetaData)entry.getLeft();
                int weight = dsplist.get(dspMetaData.getDspid());
                if (weight <= 0) {
                    weight = dspMetaData.getWeights();
                }

                if (weight > 0) {
                    dspList.add(Pair.of(Pair.of(dspMetaData, entry.getRight()), weight));
                }
            }

            if (!dspList.isEmpty()) {
                Pair<DSPMetaData, BidMetaData> selected = Utility.randomWithWeights(dspList);
                winner = Pair.of(selected.getLeft(), Pair.of(selected.getRight(), 0));
            }
        }

        return winner;
    }

    public int getMediaApiType(HttpServletRequest req) {
        try {
            int mediaApiType = this.cacheManager.getMediaApiType(req.getRequestURI());

            if (mediaApiType <= 0) {
                long mid = 0;
                String adspaceid = req.getParameter("adspaceid");
                if (adspaceid != null) {
                    Pair<Long, Long> plcmtInfo = this.cacheManager.mediaPlcmtMapping(adspaceid);
                    if (plcmtInfo != null) {
                        mid = plcmtInfo.getLeft();
                    }
                }

                if (mid <= 0) {
                    String pid = req.getParameter("pid");
                    if (pid != null) {
                        mid = Long.parseLong(pid);
                    }
                }

                MediaMetaData mediaMetaData = this.cacheManager.getMediaMetaData(mid);
                if (mediaMetaData != null) {
                    return mediaMetaData.getApitype();
                }
            } else {
                return mediaApiType;
            }
        } catch (Exception ex){
            System.err.println(ex.toString());
        }

        return -1;
    }
}


