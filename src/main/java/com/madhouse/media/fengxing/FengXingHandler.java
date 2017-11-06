package com.madhouse.media.fengxing;

import com.alibaba.fastjson.JSON;
import com.madhouse.cache.MediaBidMetaData;
import com.madhouse.media.MediaBaseHandler;
import com.madhouse.ssp.Constant;
import com.madhouse.ssp.avro.MediaRequest;
import com.madhouse.util.HttpUtil;
import com.madhouse.util.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by wujunfeng on 2017-11-06.
 */
public class FengXingHandler extends MediaBaseHandler {
    @Override
    public boolean parseMediaRequest(HttpServletRequest req, MediaBidMetaData mediaBidMetaData, HttpServletResponse resp) {
        try {
            String bytes = HttpUtil.getRequestPostBytes(req);
            if (!StringUtils.isEmpty(bytes)) {
                FXBidRequest bidRequest = JSON.parseObject(bytes, FXBidRequest.class);
                logger.info("FUNADX Request params is : {}", JSON.toJSONString(bidRequest));

                if (this.validateRequestParam(bidRequest) == Constant.StatusCode.OK) {
                    MediaRequest.Builder mediaRequest = this.conversionToPremiumMADData(bidRequest);
                    if (mediaRequest != null) {
                        mediaBidMetaData.getMediaBidBuilder().setRequestBuilder(mediaRequest);
                        return true;
                    }
                } else {
                    return outputStreamWrite(resp, null);
                }
            } else {
                resp.setStatus(Constant.StatusCode.BAD_REQUEST);
            }
        } catch (Exception e) {
            logger.error(e.toString());
            resp.setStatus(Constant.StatusCode.INTERNAL_ERROR);
        }

        return false;
    }

    private boolean outputStreamWrite(HttpServletResponse resp, FXBidResponse bidResponse) {
        try {
            if (bidResponse != null) {
                resp.setStatus(Constant.StatusCode.OK);
                resp.setHeader("Content-Type", "application/json; charset=utf-8");
                resp.getOutputStream().write(JSON.toJSONString(bidResponse).getBytes());
                return true;
            } else {
                resp.setStatus(Constant.StatusCode.NO_CONTENT);
            }
        } catch (Exception e) {
            logger.error(e.toString());
            resp.setStatus(Constant.StatusCode.INTERNAL_ERROR);
        }

        return false;
    }

    private int validateRequestParam(FXBidRequest bidRequest) {
        if (!ObjectUtils.isEmpty(bidRequest)) {

        }

        return Constant.StatusCode.BAD_REQUEST;
    }

    private MediaRequest.Builder conversionToPremiumMADData(FXBidRequest bidRequest) {
        return null;
    }

    @Override
    public boolean packageMediaResponse(MediaBidMetaData mediaBidMetaData, HttpServletResponse resp) {
        return false;
    }
}
