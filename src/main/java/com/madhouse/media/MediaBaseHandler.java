package com.madhouse.media;

import com.madhouse.cache.AuctionPriceInfo;
import com.madhouse.cache.MediaBidMetaData;

import com.madhouse.configuration.Configuration;
import com.madhouse.configuration.WebApp;
import com.madhouse.resource.ResourceManager;
import com.madhouse.ssp.Constant;
import com.madhouse.ssp.LoggerUtil;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.madhouse.ssp.avro.*;
import org.apache.logging.log4j.Logger;

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

    public final boolean packageResponse(MediaBidMetaData mediaBidMetaData, HttpServletResponse resp, DSPBid.Builder dspBid) {
        try {
            MediaBid.Builder mediaBid = mediaBidMetaData.getMediaBidBuilder();

            mediaBid.setStatus(Constant.StatusCode.NO_CONTENT);
            if (dspBid != null && dspBid.getStatus() == Constant.StatusCode.OK && dspBid.getRequestBuilder() != null) {
                DSPResponse.Builder dspResponse = dspBid.getResponseBuilder();
                MediaResponse.Builder mediaResponse = MediaResponse.newBuilder();
                mediaResponse.setDspid(dspBid.getDspid());
                mediaResponse.setAdmid(dspResponse.getAdmid());
                mediaResponse.setLayout(dspBid.getRequestBuilder().getLayout());
                mediaResponse.setTitle(dspResponse.getTitle());
                mediaResponse.setDesc(dspResponse.getDesc());
                mediaResponse.setIcon(dspResponse.getIcon());
                mediaResponse.setCover(dspResponse.getCover());
                mediaResponse.setAdm(dspResponse.getAdm());
                mediaResponse.setDealid(dspResponse.getDealid());
                mediaResponse.setDuration(dspResponse.getDuration());
                mediaResponse.setLpgurl(dspResponse.getLpgurl());
                mediaResponse.setActtype(dspResponse.getActtype());
                mediaResponse.setMonitorBuilder(Monitor.newBuilder(dspResponse.getMonitorBuilder()));

                Monitor.Builder monitor = mediaResponse.getMonitorBuilder();
                monitor.getImpurl().add(new Track(0, mediaBidMetaData.getImpressionTrackingUrl()));
                monitor.getClkurl().add(mediaBidMetaData.getClickTrackingUrl());

                mediaBid.setResponseBuilder(mediaResponse);
                mediaBid.setStatus(Constant.StatusCode.OK);
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

    public abstract boolean parseMediaRequest(HttpServletRequest req, MediaBidMetaData mediaBidMetaData, HttpServletResponse resp);
    public abstract boolean packageMediaResponse(MediaBidMetaData mediaBidMetaData, HttpServletResponse resp);
}
