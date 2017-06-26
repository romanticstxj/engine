package com.madhouse.ssp;

import com.madhouse.cache.*;
import com.madhouse.configuration.Bid;
import com.madhouse.dsp.DSPBaseHandler;
import com.madhouse.media.MediaBaseHandler;
import com.madhouse.resource.ResourceManager;
import com.madhouse.rtb.PremiumMADRTBProtocol;
import com.madhouse.util.HttpUtil;
import com.madhouse.util.StringUtil;
import com.madhouse.util.Utility;
import com.madhouse.util.httpclient.MultiHttpClient;
import com.madhouse.util.httpclient.HttpClient;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by WUJUNFENG on 2017/5/23.
 */
public class WorkThread {
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
            if (StringUtil.isEmpty(impid) || StringUtil.isEmpty(mid) || StringUtil.isEmpty(plcmtid) || StringUtil.isEmpty(policyid) || StringUtil.isEmpty(ext)) {
                resp.setStatus(Constant.StatusCode.BAD_REQUEST);
                return;
            }

            //bid redis check
            String key = String.format(Constant.RedisKey.BID_RECORD, impid, mid, plcmtid, policyid);
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
            if (StringUtil.isEmpty(impid) || StringUtil.isEmpty(mid) || StringUtil.isEmpty(plcmtid) || StringUtil.isEmpty(policyid) || StringUtil.isEmpty(ext)) {
                resp.setStatus(Constant.StatusCode.BAD_REQUEST);
                return;
            }

            //bid redis check
            String key = String.format(Constant.RedisKey.BID_RECORD, impid, mid, plcmtid, policyid);
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

        MediaBaseHandler mediaBaseHandler = ResourceManager.getInstance().getMediaApiType(req.getRequestURI());
        if (mediaBaseHandler == null) {
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

        MediaBidMetaData mediaBidMetaData = new MediaBidMetaData();
        mediaBidMetaData.setMediaBidBuilder(mediaBidBuilder);

        //parse media request
        //MediaBaseHandler mediaBaseHandler = ResourceManager.getInstance().getMediaHandler(mediaApiType);
        if (mediaBaseHandler != null) {
            if (!mediaBaseHandler.parseMediaRequest(req, mediaBidMetaData, resp)) {
                resp.setStatus(Constant.StatusCode.BAD_REQUEST);
                return;
            }
        }

        mediaBidBuilder.setStatus(Constant.StatusCode.NO_CONTENT);

        PremiumMADDataModel.MediaBid.MediaRequest.Builder mediaRequestBuilder = mediaBidBuilder.getRequestBuilder();

        //get placement metadata
        PlcmtMetaData plcmtMetaData = CacheManager.getInstance().getPlcmtMetaData(mediaRequestBuilder.getAdspacekey());
        if (plcmtMetaData == null) {
            resp.setStatus(Constant.StatusCode.BAD_REQUEST);
            return;
        }

        MediaMetaData mediaMetaData = CacheManager.getInstance().getMediaMetaData(plcmtMetaData.getMediaId());
        if (mediaMetaData == null) {
            resp.setStatus(Constant.StatusCode.BAD_REQUEST);
            return;
        }

        //init user ip
        if (!mediaRequestBuilder.hasIp()) {
            mediaRequestBuilder.setIp(mediaBidBuilder.getIp());
        }

        //init location
        String location = ResourceManager.getInstance().getLocation(mediaRequestBuilder.getIp());
        if (location == null) {
            resp.setStatus(Constant.StatusCode.BAD_REQUEST);
            return;
        }

        mediaRequestBuilder.setLocation(location);

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
            adBlockMetaData = CacheManager.getInstance().getAdBlockMetaData(blockid);
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
        Map<Long, Pair<DSPMetaData, DSPBidMetaData>> dspInfoList = new HashMap<>();
        for (Map.Entry entry : policyMetaData.getDsplist().entrySet()) {
            long dspid = (Long)entry.getKey();
            DSPMetaData dspMetaData = CacheManager.getInstance().getDSPMetaData(dspid);
            if (dspMetaData.isEnable()) {
                DSPBidMetaData dspBidMetaData = new DSPBidMetaData();
                PremiumMADDataModel.DSPBid.Builder builder = PremiumMADDataModel.DSPBid.newBuilder();
                dspBidMetaData.setDspBidBuilder(builder);
                DSPBaseHandler dspBaseHandler = ResourceManager.getInstance().getDSPHandler(dspMetaData.getApitype());
                dspBidMetaData.setDspBaseHandler(dspBaseHandler);
                HttpRequestBase httpRequestBase = dspBaseHandler.packageBidRequest(mediaBidBuilder, mediaMetaData, plcmtMetaData, adBlockMetaData, policyMetaData, dspMetaData, dspBidMetaData);
                if (httpRequestBase != null) {
                    HttpClient httpClient = dspMetaData.getHttpClient();
                    httpClient.setHttpRequest(httpRequestBase, mediaMetaData.getTimeout());
                    this.multiHttpClient.addHttpClient(httpClient);
                    dspBidMetaData.setHttpRequestBase(httpRequestBase);
                    dspInfoList.put(dspid, Pair.of(dspMetaData, dspBidMetaData));
                }
            }
        }

        if (this.multiHttpClient.execute()) {
            List<Pair<DSPMetaData, DSPBidMetaData>> dspMetaDataList = new LinkedList<Pair<DSPMetaData, DSPBidMetaData>>();
            for (Map.Entry entry : dspInfoList.entrySet()) {
                Pair<DSPMetaData, DSPBidMetaData> dspInfo = (Pair<DSPMetaData, DSPBidMetaData>)entry.getValue();
                DSPMetaData dspMetaData = dspInfo.getLeft();
                DSPBidMetaData dspBidMetaData = dspInfo.getRight();
                DSPBaseHandler dspBaseHandler = dspBidMetaData.getDspBaseHandler();
                HttpResponse httpResponse = dspMetaData.getHttpClient().getResp();
                if (httpResponse != null) {
                    if (dspBaseHandler.parseBidResponse(httpResponse, dspBidMetaData)) {
                        if (policyMetaData.getDeliverytype() != Constant.DeliveryType.RTB || dspBidMetaData.getPrice() >= plcmtMetaData.getBidfloor()) {
                            dspMetaDataList.add(Pair.of(dspMetaData, dspBidMetaData));
                        }
                    }
                } else {
                    dspBidMetaData.getDspBidBuilder().setStatus(Constant.StatusCode.REQUEST_TIMEOUT);
                }

                dspBidMetaData.getHttpRequestBase().releaseConnection();
            }

            if (!dspMetaDataList.isEmpty()) {
                Pair<DSPMetaData, Pair<DSPBidMetaData, Integer>> winner = this.selectWinner(plcmtMetaData, policyMetaData, dspMetaDataList);
                if (winner != null) {
                    DSPBidMetaData dspBidMetaData = winner.getRight().getLeft();
                    if (this.packageMediaResponse(dspBidMetaData.getDspBidBuilder(), mediaBidMetaData.getMediaBidBuilder())) {
                        mediaBaseHandler.packageMediaResponse(mediaBidMetaData, resp);
                        if (policyMetaData.getDeliverytype() == Constant.DeliveryType.RTB) {
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
                    for (PremiumMADRTBProtocol.BidResponse.SeatBid.Bid.Monitor.Track track : monitor.getImpurlList()) {
                        PremiumMADDataModel.MediaBid.MediaResponse.Track.Builder var1 = PremiumMADDataModel.MediaBid.MediaResponse.Track.newBuilder();
                        var1.setStartdelay(track.getStartdelay());
                        var1.setUrl(track.getUrl());
                        mediaResponse.addImpurl(var1);
                    }

                    mediaResponse.addAllClkurl(monitor.getClkurlList());
                    mediaResponse.addAllSecurl(monitor.getSecurlList());
                    mediaBidBuilder.setResponse(mediaResponse);
                    mediaBidBuilder.setStatus(Constant.StatusCode.OK);

                    return true;

                } catch (Exception ex) {
                    System.err.println(ex.toString());
                    return false;
                }
            }
        }

        return false;
    }

    private List<Long> policyTargeting(PremiumMADDataModel.MediaBid.MediaRequest mediaRequest) {
        List<Long> policyList = new LinkedList<Long>();
        return policyList;
    }

    private void internalError(HttpServletResponse resp, PremiumMADDataModel.MediaBid.Builder mediaBidBuilder, int statusCode) {
        try {
            resp.setStatus(statusCode);
            mediaBidBuilder.setStatus(statusCode);
            LoggerUtil.getInstance().writeMediaLog(ResourceManager.getInstance().getKafkaProducer(), mediaBidBuilder.build());
        } catch (Exception ex) {

        }
    }

    private PolicyMetaData selectPolicy(List<Long> policyList) {

        List<PolicyMetaData> policyMetaDatas = new LinkedList<PolicyMetaData>();
        for (long policyId : policyList) {
            PolicyMetaData policyMetaData = CacheManager.getInstance().getPolicyMetaData(policyId);
            if (policyMetaData != null) {
                policyMetaDatas.add(policyMetaData);
            }
        }

        policyMetaDatas.sort(new Comparator<PolicyMetaData>() {
            public int compare(PolicyMetaData o1, PolicyMetaData o2) {
                return (o1.getDeliverytype() < o2.getDeliverytype()) ? 1 : -1;
            }
        });

        int selectType = -1;
        List<Pair<PolicyMetaData, Integer>> policyMetaList = new LinkedList<Pair<PolicyMetaData, Integer>>();
        for (PolicyMetaData policyMetaData : policyMetaDatas) {
            if (selectType > 0 && selectType != policyMetaData.getDeliverytype()) {
                break;
            } else {
                policyMetaList.add(Pair.of(policyMetaData, policyMetaData.getWeight()));
            }
        }

        return (Utility.randomWithWeights(policyMetaList));
    }

    private Pair<DSPMetaData, Pair<DSPBidMetaData, Integer>> selectWinner(PlcmtMetaData plcmtMetaData,
                                                                   PolicyMetaData policyMetaData,
                                                                   List<Pair<DSPMetaData, DSPBidMetaData>> dspMetaDataList) {
        Pair<DSPMetaData, Pair<DSPBidMetaData, Integer>> winner = null;

        if (policyMetaData.getDeliverytype() == Constant.DeliveryType.RTB) {
            dspMetaDataList.sort(new Comparator<Pair<DSPMetaData, DSPBidMetaData>>() {
                public int compare(Pair<DSPMetaData, DSPBidMetaData> o1, Pair<DSPMetaData, DSPBidMetaData> o2) {
                    return o1.getRight().getPrice() > o2.getRight().getPrice() ? 1 : -1;
                }
            });

            int price = plcmtMetaData.getBidfloor();
            if (dspMetaDataList.size() >= 2) {
                DSPBidMetaData dspBidMetaData = dspMetaDataList.get(1).getRight();
                price = dspBidMetaData.getPrice();
            }

            winner = Pair.of(dspMetaDataList.get(0).getLeft(), Pair.of(dspMetaDataList.get(0).getRight(), price + 1));
        } else {
            List<Pair<Pair<DSPMetaData, DSPBidMetaData>, Integer>> dspList = new LinkedList<Pair<Pair<DSPMetaData, DSPBidMetaData>, Integer>>();
            Map<Long, Integer> dsplist = policyMetaData.getDsplist();
            for (Pair<DSPMetaData, DSPBidMetaData> entry : dspMetaDataList) {
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
                Pair<DSPMetaData, DSPBidMetaData> selected = Utility.randomWithWeights(dspList);
                winner = Pair.of(selected.getLeft(), Pair.of(selected.getRight(), 0));
            }
        }

        return winner;
    }
}


