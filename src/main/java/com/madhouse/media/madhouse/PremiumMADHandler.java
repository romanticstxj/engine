package com.madhouse.media.madhouse;

import com.madhouse.cache.MediaBidMetaData;
import com.madhouse.media.MediaBaseHandler;
import com.madhouse.rtb.PremiumMADRTBProtocol;
import com.madhouse.ssp.PremiumMADDataModel;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by WUJUNFENG on 2017/5/23.
 */
public class PremiumMADHandler extends MediaBaseHandler {
    @Override
    public boolean packageMediaResponse(PremiumMADDataModel.DSPBid.Builder dspBidBuilder, MediaBidMetaData mediaBidMetaData, HttpServletResponse resp) {
        return super.packageMediaResponse(dspBidBuilder, mediaBidMetaData, resp);
    }

    @Override
    public boolean parseMediaRequest(HttpServletRequest req, MediaBidMetaData mediaBidMetaData, HttpServletResponse resp) {
        return super.parseMediaRequest(req, mediaBidMetaData, resp);
    }
}
