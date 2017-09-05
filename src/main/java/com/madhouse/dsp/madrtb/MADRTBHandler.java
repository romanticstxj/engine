package com.madhouse.dsp.madrtb;

import com.madhouse.cache.*;
import com.madhouse.dsp.DSPBaseHandler;
import com.madhouse.rtb.PremiumMADRTBProtocol;
import com.madhouse.ssp.Constant;
import com.madhouse.ssp.avro.*;
import com.madhouse.util.AESUtil;
import com.madhouse.util.StringUtil;
import org.apache.commons.httpclient.Header;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.util.EntityUtils;

/**
 * Created by WUJUNFENG on 2017/7/19.
 */
public class MADRTBHandler extends DSPBaseHandler {
    @Override
    public HttpRequestBase packageBidRequest(MediaBid.Builder mediaBidBuilder, MediaMetaData mediaMetaData, PlcmtMetaData plcmtMetaData, AdBlockMetaData adBlockMetaData, PolicyMetaData policyMetaData, DSPBidMetaData dspBidMetaData) {
        DSPRequest.Builder dspRequest = dspBidMetaData.getDspBidBuilder().getRequestBuilder();
        MediaRequest.Builder mediaRequest = mediaBidBuilder.getRequestBuilder();

        HttpPost httpPost = new HttpPost(dspBidMetaData.getDspMetaData().getBidUrl());
        httpPost.setHeader("x-madrtb-version", "1.2");
        httpPost.setHeader("Content-Type", "application/x-protobuf; charset=utf-8");

        DSPMappingMetaData dspMappingMetaData = CacheManager.getInstance().getDSPMapping(dspBidMetaData.getDspMetaData().getId(), plcmtMetaData.getId());

        //bid request
        PremiumMADRTBProtocol.BidRequest.Builder bidRequest = PremiumMADRTBProtocol.BidRequest.newBuilder();
        bidRequest.setId(dspRequest.getId());
        bidRequest.setTmax(mediaMetaData.getTimeout());
        bidRequest.setTest(mediaBidBuilder.getRequestBuilder().getTest());
        bidRequest.setAt(Constant.BidAt.SECOND_PRICE);

        if (adBlockMetaData != null) {
            bidRequest.addAllBadv(adBlockMetaData.getBadv());
            bidRequest.addAllBcat(adBlockMetaData.getBcat());
        }

        if (mediaMetaData.getType() == Constant.MediaType.APP) {
            PremiumMADRTBProtocol.BidRequest.App.Builder app = PremiumMADRTBProtocol.BidRequest.App.newBuilder();
            if (dspMappingMetaData != null && !StringUtils.isEmpty(dspMappingMetaData.getDspMediaId())) {
                app.setId(dspMappingMetaData.getDspMediaId());
            } else {
                app.setId(Long.toString(mediaMetaData.getId()));
            }

            app.setBundle(mediaRequest.getBundle());
            app.addCat(Integer.toString(mediaMetaData.getCategory()));
            app.setName(mediaRequest.getName());
            bidRequest.setApp(app);
        }

        if (mediaMetaData.getType() == Constant.MediaType.SITE) {
            PremiumMADRTBProtocol.BidRequest.Site.Builder site = PremiumMADRTBProtocol.BidRequest.Site.newBuilder();
            if (dspMappingMetaData != null && !StringUtils.isEmpty(dspMappingMetaData.getDspMediaId())) {
                site.setId(dspMappingMetaData.getDspMediaId());
            } else {
                site.setId(Long.toString(mediaMetaData.getId()));
            }

            site.addCat(Integer.toString(mediaMetaData.getCategory()));
            site.setName(mediaRequest.getName());
            bidRequest.setSite(site);
        }

        {
            PremiumMADRTBProtocol.BidRequest.Device.Builder device = PremiumMADRTBProtocol.BidRequest.Device.newBuilder();
            device.setIp(StringUtil.toString(mediaRequest.getIp()));
            device.setUa(StringUtil.toString(mediaRequest.getUa()));
            device.setDid(StringUtil.toString(mediaRequest.getDid()));
            device.setDidmd5(StringUtil.toString(mediaRequest.getDidmd5()));
            device.setDpid(StringUtil.toString(mediaRequest.getDpid()));
            device.setDpidmd5(StringUtil.toString(mediaRequest.getDpidmd5()));
            device.setIfa(StringUtil.toString(mediaRequest.getIfa()));
            device.setMac1(StringUtil.toString(mediaRequest.getMac()));
            device.setMac1Md5(StringUtil.toString(mediaRequest.getMacmd5()));
            device.setDevicetype(mediaRequest.getDevicetype());
            device.setConnectiontype(mediaRequest.getConnectiontype());
            device.setCarrier(mediaRequest.getCarrier());
            device.setMake(StringUtil.toString(mediaRequest.getMake()));
            device.setModel(StringUtil.toString(mediaRequest.getModel()));
            device.setOs(mediaRequest.getOs());
            device.setOsv(StringUtil.toString(mediaRequest.getOsv()));

            if (mediaRequest.getGeoBuilder() != null) {
                PremiumMADRTBProtocol.BidRequest.Device.Geo.Builder geo = PremiumMADRTBProtocol.BidRequest.Device.Geo.newBuilder();
                geo.setLon(mediaRequest.getGeoBuilder().getLon());
                geo.setLat(mediaRequest.getGeoBuilder().getLat());
                device.setGeo(geo);
            }

            bidRequest.setDevice(device);
        }

        {
            PremiumMADRTBProtocol.BidRequest.Impression.Builder impression = PremiumMADRTBProtocol.BidRequest.Impression.newBuilder();
            impression.setId(mediaBidBuilder.getImpid());

            if (policyMetaData.getDeliveryType() == Constant.DeliveryType.RTB) {
                impression.setBidfloor(plcmtMetaData.getBidFloor());
                impression.setBidtype(plcmtMetaData.getBidType());
            } else {
                //policyMetaData.getAdspaceInfoMap().get(plcmtMetaData.getId()).getBidFloor()
                impression.setBidfloor(0);
                impression.setBidtype(policyMetaData.getAdspaceInfoMap().get(plcmtMetaData.getId()).getBidType());
            }

            if (dspMappingMetaData != null && !StringUtils.isEmpty(dspMappingMetaData.getMappingKey())) {
                impression.setTagid(dspMappingMetaData.getMappingKey());
            } else {
                impression.setTagid(plcmtMetaData.getAdspaceKey());
            }

            if (policyMetaData.getDeliveryType() != Constant.DeliveryType.RTB) {
                PremiumMADRTBProtocol.BidRequest.Impression.PMP.Builder pmp = PremiumMADRTBProtocol.BidRequest.Impression.PMP.newBuilder();
                pmp.setPrivateAuction(Constant.AuctionType.PRIVATE_MARKETING);
                PremiumMADRTBProtocol.BidRequest.Impression.PMP.Deal.Builder deal = PremiumMADRTBProtocol.BidRequest.Impression.PMP.Deal.newBuilder();
                deal.setId(StringUtil.toString(policyMetaData.getDealId()));
                deal.setAt(Constant.BidAt.FIXED_PRICE);
                deal.setBidfloor(0);
                pmp.addDeals(deal);
                impression.setPmp(pmp);
            }

            switch (plcmtMetaData.getAdType()) {
                case Constant.PlcmtType.BANNER: {
                    PlcmtMetaData.Image var = plcmtMetaData.getBanner();
                    PremiumMADRTBProtocol.BidRequest.Impression.Banner.Builder banner = PremiumMADRTBProtocol.BidRequest.Impression.Banner.newBuilder();
                    banner.setLayout(plcmtMetaData.getLayout());
                    banner.setW(mediaRequest.getW());
                    banner.setH(mediaRequest.getH());
                    banner.addAllMimes(var.getMimes());
                    impression.setBanner(banner);
                    break;
                }

                case Constant.PlcmtType.VIDEO: {
                    PlcmtMetaData.Video var = plcmtMetaData.getVideo();
                    PremiumMADRTBProtocol.BidRequest.Impression.Video.Builder video = PremiumMADRTBProtocol.BidRequest.Impression.Video.newBuilder();
                    video.setW(mediaRequest.getW());
                    video.setH(mediaRequest.getH());
                    video.setLinearity(var.getLinearity());
                    video.setStartdelay(var.getStartDelay());
                    video.setMinduration(var.getMinDuraion());
                    video.setMaxduration(var.getMaxDuration());
                    video.addAllMimes(var.getMimes());
                    impression.setVideo(video);
                    break;
                }

                case Constant.PlcmtType.NATIVE: {
                    PremiumMADRTBProtocol.BidRequest.Impression.Native.Builder natives = PremiumMADRTBProtocol.BidRequest.Impression.Native.newBuilder();
                    natives.setVer("1.1");
                    PremiumMADRTBProtocol.BidRequest.Impression.Native.NativeRequest.Builder nativeRequest = PremiumMADRTBProtocol.BidRequest.Impression.Native.NativeRequest.newBuilder();
                    nativeRequest.setPlcmtcnt(1);
                    nativeRequest.setLayout(plcmtMetaData.getLayout());

                    int id = 1;
                    PlcmtMetaData.Native var1 = plcmtMetaData.getNatives();
                    if (var1.getIcon() != null) {
                        PlcmtMetaData.Image icon = var1.getIcon();
                        PremiumMADRTBProtocol.BidRequest.Impression.Native.NativeRequest.Asset.Builder asset = PremiumMADRTBProtocol.BidRequest.Impression.Native.NativeRequest.Asset.newBuilder();
                        PremiumMADRTBProtocol.BidRequest.Impression.Native.NativeRequest.Asset.Image.Builder image = PremiumMADRTBProtocol.BidRequest.Impression.Native.NativeRequest.Asset.Image.newBuilder();
                        image.setType(Constant.NativeImageType.ICON);
                        image.setW(icon.getW());
                        image.setH(icon.getH());
                        image.addAllMimes(icon.getMimes());
                        asset.setId(Integer.toString(id++));
                        asset.setImage(image);
                        nativeRequest.addAssets(asset);
                    }

                    if (var1.getCover() != null) {
                        PlcmtMetaData.Image cover = var1.getCover();
                        PremiumMADRTBProtocol.BidRequest.Impression.Native.NativeRequest.Asset.Builder asset = PremiumMADRTBProtocol.BidRequest.Impression.Native.NativeRequest.Asset.newBuilder();
                        PremiumMADRTBProtocol.BidRequest.Impression.Native.NativeRequest.Asset.Image.Builder image = PremiumMADRTBProtocol.BidRequest.Impression.Native.NativeRequest.Asset.Image.newBuilder();
                        image.setType(Constant.NativeImageType.COVER);
                        image.setW(cover.getW());
                        image.setH(cover.getH());
                        image.addAllMimes(cover.getMimes());
                        asset.setId(Integer.toString(id++));
                        asset.setImage(image);
                        nativeRequest.addAssets(asset);
                    }

                    if (var1.getTitle() > 0) {
                        PremiumMADRTBProtocol.BidRequest.Impression.Native.NativeRequest.Asset.Builder asset = PremiumMADRTBProtocol.BidRequest.Impression.Native.NativeRequest.Asset.newBuilder();
                        PremiumMADRTBProtocol.BidRequest.Impression.Native.NativeRequest.Asset.Title.Builder title = PremiumMADRTBProtocol.BidRequest.Impression.Native.NativeRequest.Asset.Title.newBuilder();
                        title.setLen(var1.getTitle());
                        asset.setId(Integer.toString(id++));
                        asset.setTitle(title);
                        nativeRequest.addAssets(asset);
                    }

                    if (var1.getDesc() > 0) {
                        PremiumMADRTBProtocol.BidRequest.Impression.Native.NativeRequest.Asset.Builder asset = PremiumMADRTBProtocol.BidRequest.Impression.Native.NativeRequest.Asset.newBuilder();
                        PremiumMADRTBProtocol.BidRequest.Impression.Native.NativeRequest.Asset.Data.Builder desc = PremiumMADRTBProtocol.BidRequest.Impression.Native.NativeRequest.Asset.Data.newBuilder();
                        desc.setType(Constant.NativeDataType.NORMAL);
                        desc.setLen(var1.getDesc());
                        asset.setId(Integer.toString(id++));
                        asset.setDesc(desc);
                        nativeRequest.addAssets(asset);
                    }

                    if (var1.getContent() > 0) {
                        PremiumMADRTBProtocol.BidRequest.Impression.Native.NativeRequest.Asset.Builder asset = PremiumMADRTBProtocol.BidRequest.Impression.Native.NativeRequest.Asset.newBuilder();
                        PremiumMADRTBProtocol.BidRequest.Impression.Native.NativeRequest.Asset.Data.Builder content = PremiumMADRTBProtocol.BidRequest.Impression.Native.NativeRequest.Asset.Data.newBuilder();
                        content.setType(Constant.NativeDataType.NORMAL);
                        content.setLen(var1.getContent());
                        asset.setId(Integer.toString(id++));
                        asset.setContent(content);
                        nativeRequest.addAssets(asset);
                    }

                    if (plcmtMetaData.getLayout() == Constant.NativeLayout.VIDEO) {
                        PlcmtMetaData.Video var2 = var1.getVideo();
                        PremiumMADRTBProtocol.BidRequest.Impression.Native.NativeRequest.Asset.Builder asset = PremiumMADRTBProtocol.BidRequest.Impression.Native.NativeRequest.Asset.newBuilder();
                        PremiumMADRTBProtocol.BidRequest.Impression.Native.NativeRequest.Asset.Video.Builder video = PremiumMADRTBProtocol.BidRequest.Impression.Native.NativeRequest.Asset.Video.newBuilder();
                        video.setW(mediaRequest.getW());
                        video.setH(mediaRequest.getH());
                        video.addAllMimes(var2.getMimes());
                        video.setMinduration(var2.getMinDuraion());
                        video.setMaxduration(var2.getMaxDuration());
                        asset.setId(Integer.toString(id++));
                        asset.setVideo(video);
                        nativeRequest.addAssets(asset);
                    } else {
                        PlcmtMetaData.Image var2 = var1.getImage();
                        PremiumMADRTBProtocol.BidRequest.Impression.Native.NativeRequest.Asset.Builder asset = PremiumMADRTBProtocol.BidRequest.Impression.Native.NativeRequest.Asset.newBuilder();
                        PremiumMADRTBProtocol.BidRequest.Impression.Native.NativeRequest.Asset.Image.Builder image = PremiumMADRTBProtocol.BidRequest.Impression.Native.NativeRequest.Asset.Image.newBuilder();
                        image.setType(Constant.NativeImageType.MAIN);
                        image.setW(mediaRequest.getW());
                        image.setH(mediaRequest.getH());
                        image.addAllMimes(var2.getMimes());
                        asset.setId(Integer.toString(id++));
                        asset.setImage(image);
                        nativeRequest.addAssets(asset);
                    }

                    natives.setRequest(nativeRequest);
                    impression.setNative(natives);
                    break;
                }

                default: {
                    break;
                }
            }

            bidRequest.addImp(impression);
        }

        try {
            ByteArrayEntity entity = new ByteArrayEntity(bidRequest.build().toByteArray());
            httpPost.setEntity(entity);
        } catch (Exception ex) {
            System.err.println(ex.toString());
            return null;
        }

        return httpPost;
    }


    @Override
    public boolean parseBidResponse(HttpResponse httpResponse, DSPBidMetaData dspBidMetaData) {
        try {
            DSPBid.Builder dspBid = dspBidMetaData.getDspBidBuilder();

            if (httpResponse != null) {
                int status = httpResponse.getStatusLine().getStatusCode();
                if (status != Constant.StatusCode.OK) {
                    dspBid.setStatus(status);
                    return false;
                }

                HttpEntity entity = httpResponse.getEntity();
                PremiumMADRTBProtocol.BidResponse bidResponse = PremiumMADRTBProtocol.BidResponse.parseFrom(EntityUtils.toByteArray(entity));
                if (bidResponse != null) {
                    if (bidResponse.hasNbr() && bidResponse.getNbr() >= 0) {
                        dspBid.setStatus(Constant.StatusCode.NO_CONTENT);
                        return false;
                    }

                    if (bidResponse.getSeatbidCount() > 0 && bidResponse.getSeatbid(0).getBidCount() > 0) {
                        PremiumMADRTBProtocol.BidResponse.SeatBid.Bid bid = bidResponse.getSeatbid(0).getBid(0);
                        DSPResponse.Builder dspResponse = DSPResponse.newBuilder();
                        dspResponse.setId(bidResponse.getId());
                        dspResponse.setBidid(StringUtil.toString(bid.getId()));
                        dspResponse.setImpid(dspBid.getRequestBuilder().getImpid());
                        dspResponse.setAdid(StringUtil.toString(bid.getAdid()));
                        dspResponse.setCid(StringUtil.toString(bid.getCid()));
                        dspResponse.setCrid(StringUtil.toString(bid.getCrid()));
                        dspResponse.setIcon(StringUtil.toString(bid.getIcon()));
                        dspResponse.setCover(StringUtil.toString(bid.getCover()));
                        dspResponse.setPrice(bid.getPrice());
                        dspResponse.setNurl(StringUtil.toString(bid.getNurl()));
                        dspResponse.setDuration(bid.hasDuration() ? bid.getDuration() : 0);
                        dspResponse.setDealid(StringUtil.toString(bid.getDealid()));
                        dspResponse.setLpgurl(StringUtil.toString(bid.getLpgurl()));
                        dspResponse.setActtype(bid.hasActtype() ? bid.getActtype() : 0);

                        if (bid.getMonitor() != null) {
                            Monitor.Builder monitor = Monitor.newBuilder();
                            dspResponse.setMonitorBuilder(monitor);

                            for (PremiumMADRTBProtocol.BidResponse.SeatBid.Bid.Monitor.Track track : bid.getMonitor().getImpurlList()) {
                                Track.Builder var = Track.newBuilder();
                                var.setStartdelay(track.getStartdelay());
                                var.setUrl(track.getUrl());
                                monitor.getImpurl().add(var.build());
                            }

                            for (String url : bid.getMonitor().getClkurlList()) {
                                monitor.getClkurl().add(url);
                            }

                            for (String url : bid.getMonitor().getSecurlList()) {
                                monitor.getSecurl().add(url);
                            }

                            for (String url : bid.getMonitor().getExtsList()) {
                                monitor.getExts().add(url);
                            }

                            monitor.setExptime(bid.getMonitor().hasExptime() ? bid.getMonitor().getExptime() : 86400);
                        }

                        if (bid.getAdmCount() > 0) {
                            for (String url : bid.getAdmList()) {
                                dspResponse.getAdm().add(url);
                            }

                            dspResponse.setCover(bid.getCover());
                        } else {
                            for (PremiumMADRTBProtocol.BidResponse.SeatBid.Bid.NativeResponse.Asset asset : bid.getAdmNative().getAssetsList()) {
                                if (asset.hasTitle()) {
                                    dspResponse.setTitle(StringUtil.toString(asset.getTitle().getText()));
                                    continue;
                                }

                                if (asset.hasDesc()) {
                                    dspResponse.setDesc(StringUtil.toString(asset.getDesc().getValue()));
                                    continue;
                                }

                                if (asset.hasVideo()) {
                                    dspResponse.getAdm().add(StringUtil.toString(asset.getVideo().getUrl()));
                                    dspResponse.setDuration(asset.getVideo().getDuration());
                                    continue;
                                }

                                if (asset.hasImage()) {
                                    switch (asset.getImage().getType()) {
                                        case Constant.NativeImageType.ICON: {
                                            dspResponse.setIcon(StringUtil.toString(asset.getImage().getUrl(0)));
                                            break;
                                        }

                                        case Constant.NativeImageType.COVER: {
                                            dspResponse.setCover(StringUtil.toString(asset.getImage().getUrl(0)));
                                            break;
                                        }

                                        default: {
                                            for (String url : asset.getImage().getUrlList()) {
                                                dspResponse.getAdm().add(url);
                                            }

                                            break;
                                        }
                                    }
                                }
                            }
                        }

                        dspBid.setResponseBuilder(dspResponse);
                        dspBid.setStatus(Constant.StatusCode.OK);
                        return true;
                    } else {
                        dspBid.setStatus(Constant.StatusCode.BAD_REQUEST);
                    }
                } else {
                    dspBid.setStatus(Constant.StatusCode.BAD_REQUEST);
                }
            } else {
                dspBid.setStatus(Constant.StatusCode.REQUEST_TIMEOUT);
            }
        } catch (Exception ex) {
            dspBidMetaData.getDspBidBuilder().setStatus(Constant.StatusCode.BAD_REQUEST);
            System.err.println(ex.toString());
        }

        return false;
    }

    @Override
    public String getWinNoticeUrl(DSPBidMetaData dspBidMetaData) {
        try {
            DSPResponse.Builder dspResponse = dspBidMetaData.getDspBidBuilder().getResponseBuilder();
            String url = dspResponse.getNurl();
            if (StringUtils.isEmpty(url)) {
                return null;
            }

            url = url.replace("${AUCTION_ID}", StringUtil.toString(dspResponse.getId()))
                    .replace("${AUCTION_IMP_ID}", StringUtil.toString(dspResponse.getImpid()))
                    .replace("${AUCTION_BID_ID}", StringUtil.toString(dspResponse.getBidid()))
                    .replace("${AUCTION_AD_ID}", StringUtil.toString(dspResponse.getAdid()));

            if (url.contains("${AUCTION_PRICE")) {
                AuctionPriceInfo auctionPriceInfo = dspBidMetaData.getAuctionPriceInfo();
                String text = String.format("%d_%d", auctionPriceInfo.getBidPrice(), System.currentTimeMillis() / 1000);
                byte[] key = StringUtil.hexToBytes(dspBidMetaData.getDspMetaData().getToken());
                byte[] data = AESUtil.encryptECB(text.getBytes("utf-8"), key, AESUtil.Algorithm.AES);
                return url.replace("${AUCTION_PRICE}", StringUtil.urlSafeBase64Encode(data));
            }

            return url;
        } catch (Exception ex) {
            System.err.println(ex.toString());
            return dspBidMetaData.getDspBidBuilder().getResponseBuilder().getNurl();
        }
    }

}
