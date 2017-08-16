package com.madhouse.cache;

import com.madhouse.configuration.WebApp;
import com.madhouse.resource.ResourceManager;
import com.madhouse.ssp.Constant;
import com.madhouse.ssp.avro.*;

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

    public class TrackingParam {
        private String impId;
        private long mediaId;
        private long adspaceId;
        private long policyId;
        private long dspId;
        private String location;
        private AuctionPriceInfo mediaIncome;
        private AuctionPriceInfo dspCost;
        private long time;

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

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
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

        public String getImpressionTracking() {
            WebApp webApp = ResourceManager.getInstance().getConfiguration().getWebapp();
            StringBuilder sb = new StringBuilder(webApp.getDomain() + webApp.getImpression());
            sb.append("?mediaid=")
                    .append(trackingParam.getMediaId())
                    .append("&plcmtid=")
                    .append(trackingParam.getAdspaceId())
                    .append("&policyid=")
                    .append(trackingParam.getPolicyId())
                    .append("&dspid=")
                    .append(trackingParam.getDspId())
                    .append("&location=")
                    .append(trackingParam.getLocation())
                    .append("&t=")
                    .append(trackingParam.getTime());


            AuctionPriceInfo mediaIncome = trackingParam.getMediaIncome();
            if (mediaIncome.getBidType() == Constant.BidType.CPM) {
                sb.append("&income=")
                        .append(mediaIncome.getBidPrice());
            } else {
                sb.append("&income=0");
            }

            AuctionPriceInfo dspCost = trackingParam.getDspCost();
            if (dspCost.getBidType() == Constant.BidType.CPM) {
                sb.append("&cost=")
                        .append(dspCost.getBidPrice());
            } else {
                sb.append("&cost=0");
            }

            return sb.toString();
        }

        public String getClickTracking() {
            WebApp webApp = ResourceManager.getInstance().getConfiguration().getWebapp();
            StringBuilder sb = new StringBuilder(webApp.getDomain() + webApp.getImpression());
            sb.append("?mediaid=")
                    .append(trackingParam.getMediaId())
                    .append("&plcmtid=")
                    .append(trackingParam.getAdspaceId())
                    .append("&policyid=")
                    .append(trackingParam.getPolicyId())
                    .append("&dspid=")
                    .append(trackingParam.getDspId())
                    .append("&location=")
                    .append(trackingParam.getLocation())
                    .append("&t=")
                    .append(trackingParam.getTime());

            AuctionPriceInfo mediaIncome = trackingParam.getMediaIncome();
            if (mediaIncome.getBidType() == Constant.BidType.CPC) {
                sb.append("&income=")
                        .append(mediaIncome.getBidPrice());
            } else {
                sb.append("&income=0");
            }

            AuctionPriceInfo dspCost = trackingParam.getDspCost();
            if (dspCost.getBidType() == Constant.BidType.CPC) {
                sb.append("&cost=")
                        .append(dspCost.getBidPrice());
            } else {
                sb.append("&cost=0");
            }

            return sb.toString();
        }
    }
}
