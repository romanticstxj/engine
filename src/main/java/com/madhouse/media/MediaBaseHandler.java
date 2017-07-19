package com.madhouse.media;

import com.madhouse.cache.MediaBidMetaData;

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
        return this.parseMediaRequest(req, mediaBidMetaData, resp);
    }

    public final boolean packageResponse(DSPBid.Builder dspBid, MediaBidMetaData mediaBidMetaData, HttpServletResponse resp) {
        MediaBid.Builder mediaBid = mediaBidMetaData.getMediaBidBuilder();

        mediaBid.setStatus(Constant.StatusCode.NO_CONTENT);
        if (dspBid != null && dspBid.getStatus() == Constant.StatusCode.OK && dspBid.getResponse() != null) {
            DSPResponse dspResponse = dspBid.getResponse();
            MediaResponse.Builder mediaResponse = MediaResponse.newBuilder();
            mediaResponse.setDspid(dspBid.getDspid());
            mediaResponse.setAdmid(dspResponse.getAdmid());
            mediaResponse.setLayout(dspBid.getRequest().getLayout());
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

            mediaBid.setResponseBuilder(mediaResponse);
            mediaBid.setStatus(Constant.StatusCode.OK);
        }

        return this.packageMediaResponse(mediaBidMetaData, resp);
    }

    public abstract boolean parseMediaRequest(HttpServletRequest req, MediaBidMetaData mediaBidMetaData, HttpServletResponse resp);
    public abstract boolean packageMediaResponse(MediaBidMetaData mediaBidMetaData, HttpServletResponse resp);
}
