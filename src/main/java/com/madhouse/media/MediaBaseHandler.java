package com.madhouse.media;

import com.madhouse.cache.*;

import com.madhouse.configuration.Configuration;
import com.madhouse.configuration.WebApp;
import com.madhouse.resource.ResourceManager;
import com.madhouse.ssp.Constant;
import com.madhouse.ssp.LoggerUtil;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.madhouse.ssp.avro.*;
import com.madhouse.util.ObjectUtils;
import com.madhouse.util.StringUtil;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by WUJUNFENG on 2017/5/23.
 */
public abstract class MediaBaseHandler {
    @SuppressWarnings("static-access")
    public static Logger logger = LoggerUtil.getInstance().getPremiummadlogger();

    public final boolean parseRequest(HttpServletRequest req, MediaBidMetaData mediaBidMetaData, HttpServletResponse resp) {
        try {
            return this.parseMediaRequest(req, mediaBidMetaData, resp);
        } catch (Exception ex) {
            logger.error(ex.toString());
        }

        return false;
    }

    public final boolean packageResponse(MediaBidMetaData mediaBidMetaData, HttpServletResponse resp, DSPBid.Builder dspBid, MaterialMetaData materialMetaData) {
        try {
            MediaBid.Builder mediaBid = mediaBidMetaData.getMediaBidBuilder();
            MediaMetaData mediaMetaData = mediaBidMetaData.getMediaMetaData();
            PlcmtMetaData plcmtMetaData = mediaBidMetaData.getPlcmtMetaData();

            mediaBid.setStatus(Constant.StatusCode.NO_CONTENT);
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
                    monitor.getImpurl().add(new Track(0, mediaBidMetaData.getImpressionTrackingUrl()));
                    monitor.getClkurl().add(mediaBidMetaData.getClickTrackingUrl());

                    mediaBid.setResponseBuilder(mediaResponse);
                    mediaBid.setStatus(Constant.StatusCode.OK);
                }
            }

            resp.setHeader("Connection", "keep-alive");
            if (this.packageMediaResponse(mediaBidMetaData, resp)) {
                LoggerUtil.getInstance().writeMediaLog(ResourceManager.getInstance().getKafkaProducer(), mediaBid);
                return true;
            }
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
}
