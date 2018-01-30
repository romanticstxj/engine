package com.madhouse.media;

import com.madhouse.cache.*;

import com.madhouse.resource.ResourceManager;
import com.madhouse.ssp.Constant;
import com.madhouse.ssp.LoggerUtil;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.madhouse.ssp.avro.*;
import com.madhouse.util.HttpUtil;
import com.madhouse.util.ObjectUtils;
import com.madhouse.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by WUJUNFENG on 2017/5/23.
 */
public abstract class MediaBaseHandler {
    protected Logger logger = LoggerUtil.getInstance().getPremiummadlogger();

    public final boolean parseRequest(HttpServletRequest req, MediaBidMetaData mediaBidMetaData, HttpServletResponse resp) {
        try {
            if (this.parseMediaRequest(req, mediaBidMetaData, resp)) {
                return true;
            }

            for (MediaBid.Builder mediaBid : mediaBidMetaData.getMediaBids()) {
                MediaRequest.Builder mediaRequest = mediaBid.getRequestBuilder();
                if (!StringUtils.isEmpty(mediaRequest.getAdspacekey())) {
                    //get placement metadata
                    PlcmtMetaData plcmtMetaData = CacheManager.getInstance().getPlcmtMetaData(mediaRequest.getAdspacekey());
                    if (plcmtMetaData != null) {
                        MediaMetaData mediaMetaData = CacheManager.getInstance().getMediaMetaData(plcmtMetaData.getMediaId());
                        if (mediaMetaData != null) {
                            mediaRequest.setMediaid(mediaMetaData.getId());
                            mediaRequest.setAdspaceid(plcmtMetaData.getId());

                            mediaBid.setIp(HttpUtil.getRealIp(req));
                            mediaBid.setUa(HttpUtil.getUserAgent(req));
                            mediaBid.setTime(System.currentTimeMillis());
                            mediaBid.setImpid(ResourceManager.getInstance().nextId());
                            LoggerUtil.getInstance().writeMediaLog(ResourceManager.getInstance().getKafkaProducer(), mediaBid);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            logger.error(ex.toString());
        }

        return false;
    }

    public final boolean packageResponse(MediaBidMetaData mediaBidMetaData, HttpServletResponse resp) {
        try {
            for (MediaBid.Builder mediaBid : mediaBidMetaData.getMediaBids()) {
                String impId = mediaBid.getImpid();
                MediaBidMetaData.BidMetaData bidMetaData = mediaBidMetaData.getBidMetaDataMap().get(mediaBid.getImpid());

                MediaMetaData mediaMetaData = bidMetaData.getMediaMetaData();
                PlcmtMetaData plcmtMetaData = bidMetaData.getPlcmtMetaData();

                DSPBid.Builder dspBid = bidMetaData.getDspBid();
                MaterialMetaData materialMetaData = bidMetaData.getMaterialMetaData();

                if (dspBid != null && dspBid.getStatus() == Constant.StatusCode.OK && dspBid.getRequestBuilder() != null) {
                    if (mediaMetaData.getMaterialAuditMode() == Constant.AuditMode.NONE || materialMetaData != null) {
                        DSPResponse.Builder dspResponse = dspBid.getResponseBuilder();
                        MediaResponse.Builder mediaResponse = MediaResponse.newBuilder();
                        mediaResponse.setDspid(dspBid.getDspid());
                        mediaResponse.setCid(dspResponse.getCid());
                        mediaResponse.setCrid(dspResponse.getCrid());
                        mediaResponse.setLayout(dspBid.getRequestBuilder().getLayout());
                        mediaResponse.setDealid(StringUtil.toString(mediaBid.getRequestBuilder().getDealid()));
                        mediaResponse.setPrice(plcmtMetaData.getBidFloor());

                        if (mediaMetaData.getMaterialAuditMode() != Constant.AuditMode.NONE) {
                            mediaResponse.setBrand(StringUtil.toString(materialMetaData.getBrand()));
                            mediaResponse.setTitle(StringUtil.toString(materialMetaData.getTitle()));
                            mediaResponse.setDesc(StringUtil.toString(materialMetaData.getDesc()));
                            mediaResponse.setContent(StringUtil.toString(materialMetaData.getContent()));
                            mediaResponse.setIcon(StringUtil.toString(materialMetaData.getIcon()));
                            mediaResponse.setCover(StringUtil.toString(materialMetaData.getCover()));
                            mediaResponse.setContent(StringUtil.toString(materialMetaData.getContent()));
                            mediaResponse.setAdm(materialMetaData.getAdm());
                            mediaResponse.setDuration(materialMetaData.getDuration());
                            mediaResponse.setLpgurl(StringUtil.toString(materialMetaData.getLpgUrl()));
                            mediaResponse.setActtype(materialMetaData.getActType());

                            if (materialMetaData.getMonitor() != null) {
                                Monitor.Builder monitor = Monitor.newBuilder();
                                if (!ObjectUtils.isEmpty(materialMetaData.getMonitor().getImpUrls())) {
                                    List<Track> impUrls = new LinkedList<>();
                                    for (MaterialMetaData.Monitor.Track t : materialMetaData.getMonitor().getImpUrls()) {
                                        impUrls.add(new Track(t.getStartDelay(), this.macroReplace(t.getUrl(), dspResponse.getMonitorBuilder().getExts())));
                                    }
                                    monitor.setImpurl(impUrls);
                                }

                                if (!ObjectUtils.isEmpty(materialMetaData.getMonitor().getClkUrls())) {
                                    List<String> clkUrls = new LinkedList<>();
                                    for (String clkUrl : materialMetaData.getMonitor().getClkUrls()) {
                                        clkUrls.add(this.macroReplace(clkUrl, dspResponse.getMonitorBuilder().getExts()));
                                    }
                                    monitor.setClkurl(clkUrls);
                                }

                                monitor.setSecurl(materialMetaData.getMonitor().getSecUrls());
                                monitor.setExts(dspResponse.getMonitorBuilder().getExts());
                                mediaResponse.setMonitorBuilder(monitor);
                            }
                        } else {
                            mediaResponse.setBrand(dspResponse.getBrand());
                            mediaResponse.setTitle(dspResponse.getTitle());
                            mediaResponse.setDesc(dspResponse.getDesc());
                            mediaResponse.setContent(dspResponse.getContent());
                            mediaResponse.setIcon(dspResponse.getIcon());
                            mediaResponse.setCover(dspResponse.getCover());
                            mediaResponse.setContent(dspResponse.getContent());
                            mediaResponse.setAdm(dspResponse.getAdm());
                            mediaResponse.setDuration(dspResponse.getDuration());
                            mediaResponse.setLpgurl(dspResponse.getLpgurl());
                            mediaResponse.setActtype(dspResponse.getActtype());
                            mediaResponse.setMonitorBuilder(Monitor.newBuilder(dspResponse.getMonitorBuilder()));
                        }

                        Monitor.Builder monitor = mediaResponse.getMonitorBuilder();

                        if (monitor.getImpurl() == null) {
                            monitor.setImpurl(new LinkedList<>());
                        }

                        if (monitor.getClkurl() == null) {
                            monitor.setClkurl(new LinkedList<>());
                        }

                        monitor.getImpurl().add(new Track(0, mediaBidMetaData.getImpressionTrackingUrl(impId, bidMetaData.getPlcmtMetaData().isEnableHttps())));
                        monitor.getClkurl().add(mediaBidMetaData.getClickTrackingUrl(impId, bidMetaData.getPlcmtMetaData().isEnableHttps()));

                        mediaBid.setResponseBuilder(mediaResponse);
                        mediaBid.setStatus(Constant.StatusCode.OK);
                    }
                }

                if (mediaBid.getStatus() != Constant.StatusCode.NOT_ALLOWED) {
                    LoggerUtil.getInstance().writeMediaLog(ResourceManager.getInstance().getKafkaProducer(), mediaBid);
                }
            }

            resp.setHeader("Connection", "keep-alive");
            return this.packageMediaResponse(mediaBidMetaData, resp);
        } catch (Exception ex) {
            logger.error(ex.toString());
        }

        return false;
    }

    private String macroReplace(String url, List<String> exts) {
        if (!ObjectUtils.isEmpty(exts)) {
            String ext1 = exts.size() >= 1 ? StringUtil.toString(exts.get(0)) : "";
            String ext2 = exts.size() >= 2 ? StringUtil.toString(exts.get(1)) : "";
            String ext3 = exts.size() >= 3 ? StringUtil.toString(exts.get(2)) : "";
            return url.replace("__EXT1__", ext1).replace("__EXT2__", ext2).replace("__EXT3__", ext3);
        }

        return url;
    }

    public abstract boolean parseMediaRequest(HttpServletRequest req, MediaBidMetaData mediaBidMetaData, HttpServletResponse resp);
    public abstract boolean packageMediaResponse(MediaBidMetaData mediaBidMetaData, HttpServletResponse resp);

    protected final boolean checkRequestParam(MediaRequest.Builder mediaRequest) {
        if (mediaRequest != null) {
            if (StringUtils.isEmpty(mediaRequest.getAdspacekey())) {
                logger.warn("adspaceKey is missing.");
                return false;
            }

            String adspaceKey = mediaRequest.getAdspacekey();
            if (StringUtils.isEmpty(mediaRequest.getBid())) {
                logger.warn("[{}]bid is missing.", adspaceKey);
                return false;
            }

            if (StringUtils.isEmpty(mediaRequest.getUa())) {
                logger.warn("[{}]ua is missing.", adspaceKey);
                return false;
            }

            if (StringUtils.isEmpty(mediaRequest.getName())) {
                logger.warn("[{}]appName is missing.", adspaceKey);
                return false;
            }

            if (StringUtils.isEmpty(mediaRequest.getBundle())) {
                logger.warn("[{}]pkgName is missing.", adspaceKey);
                return false;
            }

            if (StringUtils.isEmpty(mediaRequest.getOsv())) {
                logger.warn("[{}]osv is missing.", adspaceKey);
                return false;
            }

            if (!mediaRequest.hasOs()) {
                logger.warn("[{}]os is missing.", adspaceKey);
                return false;
            }

            switch (mediaRequest.getOs()) {
                case Constant.OSType.ANDROID: {
                    if (StringUtils.isEmpty(mediaRequest.getDid()) && StringUtils.isEmpty(mediaRequest.getDidmd5()) &&
                            StringUtils.isEmpty(mediaRequest.getDpid()) && StringUtils.isEmpty(mediaRequest.getDpidmd5())) {
                        logger.warn("[{}]android deviceId is missing.", adspaceKey);
                        return false;
                    }

                    break;
                }

                case Constant.OSType.IOS: {
                    if (StringUtils.isEmpty(mediaRequest.getDpid()) && StringUtils.isEmpty(mediaRequest.getDpidmd5()) &&
                            StringUtils.isEmpty(mediaRequest.getIfa())) {
                        logger.warn("[{}]iOS deviceId is missing.", adspaceKey);
                        return false;
                    }

                    break;
                }

                default: {
                    if (StringUtils.isEmpty(mediaRequest.getDpid()) && StringUtils.isEmpty(mediaRequest.getDpidmd5()) &&
                            StringUtils.isEmpty(mediaRequest.getMac()) && StringUtils.isEmpty(mediaRequest.getMacmd5())) {
                        logger.warn("[{}]deviceId is missing.", adspaceKey);
                    }

                    break;
                }
            }

            if (!mediaRequest.hasCarrier()) {
                logger.warn("[{}]carrier is missing.", adspaceKey);
                return false;
            }

            if (!mediaRequest.hasConnectiontype()) {
                logger.warn("[{}]connection is missing.", adspaceKey);
                return false;
            }

            if (!mediaRequest.hasDevicetype()) {
                logger.warn("[{}]devicetype is missing.", adspaceKey);
                return false;
            }

            if (!mediaRequest.hasCarrier()) {
                logger.warn("[{}]carrier is missing.", adspaceKey);
                return false;
            }

            return true;
        }

        return false;
    }
}
