package com.madhouse.media.madhouse;

import com.madhouse.media.MediaBaseHandler;
import com.madhouse.rtb.PremiumMadRTBProtocol;
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
    public boolean packageMediaResponse(PremiumMADDataModel.DSPBid.Builder dspBidBuilder, HttpServletResponse resp, PremiumMADDataModel.MediaBid.Builder mediaBidBuilder) {
        return super.packageMediaResponse(dspBidBuilder, resp, mediaBidBuilder);
    }

    @Override
    public boolean parseMediaRequest(HttpServletRequest req, PremiumMADDataModel.MediaBid.Builder mediaBidBuilder) {
        return super.parseMediaRequest(req, mediaBidBuilder);
    }
}
