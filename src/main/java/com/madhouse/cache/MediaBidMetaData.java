package com.madhouse.cache;

import com.madhouse.configuration.WebApp;
import com.madhouse.resource.ResourceManager;
import com.madhouse.ssp.Constant;
import com.madhouse.ssp.avro.*;
import com.madhouse.util.StringUtil;

import java.util.zip.CRC32;

/**
 * Created by WUJUNFENG on 2017/6/20.
 */
public class MediaBidMetaData {
    private Object requestObject;
    private MediaBid.Builder mediaBidBuilder;
    private TrackingParam trackingParam;

    public Object getRequestObject() {
        return requestObject;
    }

    public void setRequestObject(Object requestObject) {
        this.requestObject = requestObject;
    }

    public MediaBid.Builder getMediaBidBuilder() {
        return mediaBidBuilder;
    }

    public void setMediaBidBuilder(MediaBid.Builder mediaBidBuilder) {
        this.mediaBidBuilder = mediaBidBuilder;
    }

    public TrackingParam getTrackingParam() {
        return trackingParam;
    }

    public void setTrackingParam(TrackingParam trackingParam) {
        this.trackingParam = trackingParam;
    }

    public String getImpressionTrackingUrl() {
        WebApp webApp = ResourceManager.getInstance().getConfiguration().getWebapp();
        String requestUrl = webApp.getDomain() + webApp.getImpression();
        StringBuilder sb = new StringBuilder(webApp.getDomain() + webApp.getImpression());
        if (requestUrl.contains("?")) {
            sb.append("&");
        } else {
            sb.append("?");
        }

        sb.append(this.trackingParam.toString());
        return sb.toString();
    }

    public String getClickTrackingUrl() {
        WebApp webApp = ResourceManager.getInstance().getConfiguration().getWebapp();
        String requestUrl = webApp.getDomain() + webApp.getImpression();
        StringBuilder sb = new StringBuilder(webApp.getDomain() + webApp.getClick());
        if (requestUrl.contains("?")) {
            sb.append("&");
        } else {
            sb.append("?");
        }

        sb.append(this.trackingParam.toString());
        return sb.toString();
    }

    public static class TrackingParam {
        private String impId;
        private long mediaId;
        private long adspaceId;
        private long policyId;
        private long dspId;
        private String location;
        private AuctionPriceInfo mediaIncome;
        private AuctionPriceInfo dspCost;
        private long bidTime;

        public String getImpId() {
            return impId;
        }

        public void setImpId(String impId) {
            this.impId = impId;
        }

        public long getMediaId() {
            return mediaId;
        }

        public void setMediaId(long mediaId) {
            this.mediaId = mediaId;
        }

        public long getAdspaceId() {
            return adspaceId;
        }

        public void setAdspaceId(long adspaceId) {
            this.adspaceId = adspaceId;
        }

        public long getPolicyId() {
            return policyId;
        }

        public void setPolicyId(long policyId) {
            this.policyId = policyId;
        }

        public long getDspId() {
            return dspId;
        }

        public void setDspId(long dspId) {
            this.dspId = dspId;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public long getBidTime() {
            return bidTime;
        }

        public void setBidTime(long bidTime) {
            this.bidTime = bidTime;
        }

        public AuctionPriceInfo getMediaIncome() {
            return mediaIncome;
        }

        public void setMediaIncome(AuctionPriceInfo mediaIncome) {
            this.mediaIncome = mediaIncome;
        }

        public AuctionPriceInfo getDspCost() {
            return dspCost;
        }

        public void setDspCost(AuctionPriceInfo dspCost) {
            this.dspCost = dspCost;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            String ext = String.format("%d,%d,%d,%d,%d,%d,%d",
                    getPolicyId(),
                    getDspId(),
                    getMediaIncome().getBidType(),
                    getMediaIncome().getBidPrice(),
                    getDspCost().getBidType(),
                    getDspCost().getBidPrice(),
                    getBidTime());

            try {
                CRC32 crc32 = new CRC32();
                crc32.update(ext.getBytes("utf-8"));
                long sign = crc32.getValue();

                sb.append("_impid=")
                        .append(getImpId())
                        .append("&_mid=")
                        .append(getMediaId())
                        .append("&_pid=")
                        .append(getAdspaceId())
                        .append("&_loc=")
                        .append(getLocation())
                        .append("&_ext=")
                        .append(StringUtil.urlSafeBase64Encode(ext.getBytes("utf-8")))
                        .append("&_sn=")
                        .append(sign);
            } catch (Exception ex) {
                System.err.println(ex.toString());
            }

            return sb.toString();
        }
    }
}
