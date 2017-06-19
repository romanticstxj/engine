package com.madhouse.media;

import com.madhouse.rtb.PremiumMADRTBProtocol.*;
import com.madhouse.ssp.PremiumMADDataModel;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * Created by WUJUNFENG on 2017/5/23.
 */
public abstract class MediaBaseHandler {
    public boolean parseMediaRequest(HttpServletRequest req, PremiumMADDataModel.MediaBid.Builder mediaBidBuilder) {
        return true;
    }

    public boolean packageMediaResponse(PremiumMADDataModel.DSPBid.Builder dspBidBuilder, HttpServletResponse resp, PremiumMADDataModel.MediaBid.Builder mediaBidBuilder) {
        return true;
    }
}
