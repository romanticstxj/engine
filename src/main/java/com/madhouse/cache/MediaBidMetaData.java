package com.madhouse.cache;

import com.madhouse.ssp.PremiumMADDataModel;

/**
 * Created by WUJUNFENG on 2017/6/20.
 */
public class MediaBidMetaData {
    private Object requestObject;
    private PremiumMADDataModel.MediaBid.Builder mediaBidBuilder;

    public Object getRequestObject() {
        return requestObject;
    }

    public void setRequestObject(Object requestObject) {
        this.requestObject = requestObject;
    }

    public PremiumMADDataModel.MediaBid.Builder getMediaBidBuilder() {
        return mediaBidBuilder;
    }

    public void setMediaBidBuilder(PremiumMADDataModel.MediaBid.Builder mediaBidBuilder) {
        this.mediaBidBuilder = mediaBidBuilder;
    }
}
