package com.madhouse.media.fengxing;

import com.madhouse.cache.MediaBidMetaData;
import com.madhouse.media.MediaBaseHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by wujunfeng on 2017-11-06.
 */
public class FengXingHandler extends MediaBaseHandler {
    @Override
    public boolean parseMediaRequest(HttpServletRequest req, MediaBidMetaData mediaBidMetaData, HttpServletResponse resp) {
        return false;
    }

    @Override
    public boolean packageMediaResponse(MediaBidMetaData mediaBidMetaData, HttpServletResponse resp) {
        return false;
    }
}
