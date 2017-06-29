package com.madhouse.media;

import com.madhouse.cache.MediaBidMetaData;

import com.madhouse.ssp.LoggerUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Map;

/**
 * Created by WUJUNFENG on 2017/5/23.
 */
public abstract class MediaBaseHandler {
    @SuppressWarnings("static-access")
    public static Logger logger = LoggerUtil.getInstance().getPremiummadlogger();
    public abstract boolean parseMediaRequest(HttpServletRequest req, MediaBidMetaData mediaBidMetaData, HttpServletResponse resp);
    public abstract boolean packageMediaResponse(MediaBidMetaData mediaBidMetaData, HttpServletResponse resp);
}
