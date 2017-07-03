package com.madhouse.dsp;

/**
 * Created by WUJUNFENG on 2017/5/22.
 */

import com.madhouse.cache.*;
import com.madhouse.cache.AdBlockMetaData;
import com.madhouse.ssp.Constant;
import com.madhouse.ssp.avro.*;
import com.madhouse.util.AESUtil;
import com.madhouse.util.StringUtil;
import com.madhouse.rtb.PremiumMADRTBProtocol.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.util.EntityUtils;


public abstract class DSPBaseHandler {
    public HttpRequestBase packageBidRequest(MediaBid.Builder mediaBidBuilder, MediaMetaData mediaMetaData, PlcmtMetaData plcmtMetaData, AdBlockMetaData adBlockMetaData, PolicyMetaData policyMetaData, DSPBidMetaData dspBidMetaData) {
        dspBidMetaData.getDspBidBuilder().setStatus(Constant.StatusCode.REQUEST_TIMEOUT);

        if (!this.createDSPRequest(mediaBidBuilder, mediaMetaData, plcmtMetaData, adBlockMetaData, policyMetaData, dspBidMetaData.getDspMetaData(), dspBidMetaData.getDspBidBuilder())) {
            dspBidMetaData.getDspBidBuilder().setStatus(Constant.StatusCode.INTERNAL_ERROR);
            return null;
        }

        DSPRequest dspRequest = dspBidMetaData.getDspBidBuilder().getRequest();
        MediaRequest mediaRequest = mediaBidBuilder.getRequest();

        HttpPost httpPost = new HttpPost(dspBidMetaData.getDspMetaData().getBidUrl());
        httpPost.setHeader("Content-Type", "application/x-protobuf");

        DSPMappingMetaData dspMappingMetaData = CacheManager.getInstance().getDSPMapping(dspBidMetaData.getDspMetaData().getId(), plcmtMetaData.getId());

        //bid request
        BidRequest.Builder bidRequest = BidRequest.newBuilder();
        bidRequest.setId(dspRequest.getId().toString());
        bidRequest.setTmax(mediaMetaData.getTimeout());
        bidRequest.setTest(mediaBidBuilder.getRequestBuilder().getTest());
        bidRequest.setAt(Constant.BidAt.SECOND_PRICE);

        if (adBlockMetaData != null) {
            bidRequest.addAllBadv(adBlockMetaData.getBadv());
            bidRequest.addAllBcat(adBlockMetaData.getBcat());
        }

        if (mediaMetaData.getType() == Constant.MediaType.APP) {
            BidRequest.App.Builder app = BidRequest.App.newBuilder();
            app.setId(Long.toString(mediaMetaData.getId()));
            app.setBundle(mediaRequest.getBundle().toString());
            app.addCat(Integer.toString(mediaMetaData.getCategory()));
            app.setName(mediaMetaData.getName());
            bidRequest.setApp(app);
        }

        if (mediaMetaData.getType() == Constant.MediaType.SITE) {
            BidRequest.Site.Builder site = BidRequest.Site.newBuilder();
            site.setId(Long.toString(mediaMetaData.getId()));
            site.addCat(Integer.toString(mediaMetaData.getCategory()));
            site.setName(mediaMetaData.getName());
            bidRequest.setSite(site);
        }

        {
            BidRequest.Device.Builder device = BidRequest.Device.newBuilder();
            device.setIp(mediaRequest.getIp().toString());
            device.setUa(mediaRequest.getUa().toString());
            device.setDid(mediaRequest.getDid().toString());
            device.setDidmd5(mediaRequest.getDidmd5().toString());
            device.setDpid(mediaRequest.getDpid().toString());
            device.setDpidmd5(mediaRequest.getDpidmd5().toString());
            device.setIfa(mediaRequest.getIfa().toString());
            device.setMac1(mediaRequest.getMac().toString());
            device.setMac1Md5(mediaRequest.getMacmd5().toString());
            device.setDevicetype(mediaRequest.getDevicetype());
            device.setConnectiontype(mediaRequest.getConnectiontype());
            device.setCarrier(mediaRequest.getCarrier());
            device.setMake(mediaRequest.getMake().toString());
            device.setModel(mediaRequest.getModel().toString());
            device.setOs(mediaRequest.getOs());
            device.setOsv(mediaRequest.getOsv().toString());
            BidRequest.Device.Geo.Builder geo = BidRequest.Device.Geo.newBuilder();
            geo.setLon(mediaRequest.getLon());
            geo.setLat(mediaRequest.getLat());
            device.setGeo(geo);
            bidRequest.setDevice(device);
        }

        {
            BidRequest.Impression.Builder impression = BidRequest.Impression.newBuilder();
            impression.setId(mediaBidBuilder.getImpid().toString());

            if (policyMetaData.getDeliveryType() == Constant.DeliveryType.RTB) {
                impression.setBidfloor(plcmtMetaData.getBidFloor());
                impression.setBidtype(plcmtMetaData.getBidType());
            } else {
                impression.setBidfloor(0);
                impression.setBidtype(Constant.BidType.CPM);
            }

            if (dspMappingMetaData != null && !StringUtils.isEmpty(dspMappingMetaData.getMappingKey())) {
                impression.setTagid(dspMappingMetaData.getMappingKey());
            } else {
                impression.setTagid(plcmtMetaData.getAdspaceKey());
            }

            if (policyMetaData.getDeliveryType() != Constant.DeliveryType.RTB) {
                BidRequest.Impression.PMP.Builder pmp = BidRequest.Impression.PMP.newBuilder();
                pmp.setPrivateAuction(1);
                BidRequest.Impression.PMP.Deal.Builder deal = BidRequest.Impression.PMP.Deal.newBuilder();
                deal.setAt(Constant.BidAt.FIXED_PRICE);
                deal.setBidfloor(0);
                pmp.addDeals(deal);
                impression.setPmp(pmp);
            }

            switch (plcmtMetaData.getAdType()) {
                case Constant.PlcmtType.BANNER: {
                    PlcmtMetaData.Image var = plcmtMetaData.getBanner();
                    BidRequest.Impression.Banner.Builder banner = BidRequest.Impression.Banner.newBuilder();
                    banner.setLayout(plcmtMetaData.getLayout());
                    banner.setW(var.getW());
                    banner.setH(var.getH());
                    banner.addAllMimes(var.getMimes());
                    impression.setBanner(banner);
                    break;
                }

                case Constant.PlcmtType.VIDEO: {
                    PlcmtMetaData.Video var = plcmtMetaData.getVideo();
                    BidRequest.Impression.Video.Builder video = BidRequest.Impression.Video.newBuilder();
                    video.setW(var.getW());
                    video.setH(var.getH());
                    video.setLinearity(var.getLinearity());
                    video.setStartdelay(var.getStartDelay());
                    video.setMinduration(var.getMinDuraion());
                    video.setMaxduration(var.getMaxDuration());
                    video.addAllMimes(var.getMimes());
                    impression.setVideo(video);
                    break;
                }

                case Constant.PlcmtType.NATIVE: {
                    BidRequest.Impression.Native.Builder natives =BidRequest.Impression.Native.newBuilder();
                    natives.setVer("1.1");
                    BidRequest.Impression.Native.NativeRequest.Builder nativeRequest = BidRequest.Impression.Native.NativeRequest.newBuilder();
                    nativeRequest.setPlcmtcnt(1);
                    nativeRequest.setLayout(plcmtMetaData.getLayout());

                    int id = 1;
                    PlcmtMetaData.Native var1 = plcmtMetaData.getNatives();
                    if (var1.getIcon() != null) {
                        PlcmtMetaData.Image icon = var1.getIcon();
                        BidRequest.Impression.Native.NativeRequest.Asset.Builder asset = BidRequest.Impression.Native.NativeRequest.Asset.newBuilder();
                        BidRequest.Impression.Native.NativeRequest.Asset.Image.Builder image = BidRequest.Impression.Native.NativeRequest.Asset.Image.newBuilder();
                        image.setType(Constant.NativeImageType.ICON);
                        image.setW(icon.getW());
                        image.setH(icon.getH());
                        image.addAllMimes(icon.getMimes());
                        asset.setId(Integer.toString(id++));
                        asset.setRequired(true);
                        asset.setImage(image);
                        nativeRequest.addAssets(asset);
                    }

                    if (var1.getCover() != null) {
                        PlcmtMetaData.Image cover = var1.getCover();
                        BidRequest.Impression.Native.NativeRequest.Asset.Builder asset = BidRequest.Impression.Native.NativeRequest.Asset.newBuilder();
                        BidRequest.Impression.Native.NativeRequest.Asset.Image.Builder image = BidRequest.Impression.Native.NativeRequest.Asset.Image.newBuilder();
                        image.setType(Constant.NativeImageType.COVER);
                        image.setW(cover.getW());
                        image.setH(cover.getH());
                        image.addAllMimes(cover.getMimes());
                        asset.setId(Integer.toString(id++));
                        asset.setRequired(true);
                        asset.setImage(image);
                        nativeRequest.addAssets(asset);
                    }

                    if (var1.getTitle() >= 0) {
                        BidRequest.Impression.Native.NativeRequest.Asset.Builder asset = BidRequest.Impression.Native.NativeRequest.Asset.newBuilder();
                        BidRequest.Impression.Native.NativeRequest.Asset.Title.Builder title = BidRequest.Impression.Native.NativeRequest.Asset.Title.newBuilder();
                        title.setLen(var1.getTitle());
                        asset.setId(Integer.toString(id++));
                        asset.setRequired(true);
                        asset.setTitle(title);
                        nativeRequest.addAssets(asset);
                    }

                    if (var1.getDesc() >= 0) {
                        BidRequest.Impression.Native.NativeRequest.Asset.Builder asset = BidRequest.Impression.Native.NativeRequest.Asset.newBuilder();
                        BidRequest.Impression.Native.NativeRequest.Asset.Data.Builder data = BidRequest.Impression.Native.NativeRequest.Asset.Data.newBuilder();
                        data.setType(Constant.NativeDescType.DESC);
                        data.setLen(var1.getDesc());
                        asset.setId(Integer.toString(id++));
                        asset.setRequired(true);
                        asset.setData(data);
                        nativeRequest.addAssets(asset);
                    }

                    if (plcmtMetaData.getLayout() == Constant.NativeLayout.VIDEO) {
                        PlcmtMetaData.Video var2 = var1.getVideo();
                        BidRequest.Impression.Native.NativeRequest.Asset.Builder asset = BidRequest.Impression.Native.NativeRequest.Asset.newBuilder();
                        BidRequest.Impression.Native.NativeRequest.Asset.Video.Builder video = BidRequest.Impression.Native.NativeRequest.Asset.Video.newBuilder();
                        video.setW(var2.getW());
                        video.setH(var2.getH());
                        video.addAllMimes(var2.getMimes());
                        video.setMinduration(var2.getMinDuraion());
                        video.setMaxduration(var2.getMaxDuration());
                        asset.setId(Integer.toString(id++));
                        asset.setRequired(true);
                        asset.setVideo(video);
                        nativeRequest.addAssets(asset);
                    } else {
                        PlcmtMetaData.Image var2 = var1.getImage();
                        BidRequest.Impression.Native.NativeRequest.Asset.Builder asset = BidRequest.Impression.Native.NativeRequest.Asset.newBuilder();
                        BidRequest.Impression.Native.NativeRequest.Asset.Image.Builder image = BidRequest.Impression.Native.NativeRequest.Asset.Image.newBuilder();
                        image.setType(Constant.NativeImageType.MAIN);
                        image.setW(var2.getW());
                        image.setH(var2.getH());
                        image.addAllMimes(var2.getMimes());
                        asset.setId(Integer.toString(id++));
                        asset.setRequired(true);
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

    public boolean parseBidResponse(HttpResponse httpResponse, DSPBidMetaData dspBidMetaData) {
        try {
            DSPBid.Builder dspBidBuilder = dspBidMetaData.getDspBidBuilder();

            if (httpResponse != null) {
                int status = httpResponse.getStatusLine().getStatusCode();
                if (status != Constant.StatusCode.OK) {
                    dspBidBuilder.setStatus(status);
                    return false;
                }

                HttpEntity entity = httpResponse.getEntity();
                BidResponse bidResponse = BidResponse.parseFrom(EntityUtils.toByteArray(entity));
                if (bidResponse != null) {
                    if (bidResponse.hasNbr() && bidResponse.getNbr() >= 0) {
                        dspBidBuilder.setStatus(Constant.StatusCode.NO_CONTENT);
                        return false;
                    }

                    if (bidResponse.getSeatbidCount() > 0 && bidResponse.getSeatbid(0).getBidCount() > 0) {
                        BidResponse.SeatBid.Bid bid = bidResponse.getSeatbid(0).getBid(0);
                        DSPResponse.Builder dspResponse = DSPResponse.newBuilder();
                        dspResponse.setId(dspBidBuilder.getRequest().getId());
                        dspResponse.setBidid(bid.getId());
                        dspResponse.setImpid(dspBidBuilder.getRequest().getImpid());
                        dspResponse.setAdid(bid.getAdid());
                        dspResponse.setCid(bid.getCid());
                        dspResponse.setCrid(bid.getCrid());
                        dspResponse.setPrice(bid.getPrice());
                        dspResponse.setNurl(bid.getNurl());
                        dspResponse.setAdmid(bid.getAdmid());
                        dspResponse.setDuration(bid.getDuration());
                        dspResponse.setDealid(bid.getDealid());
                        dspResponse.setLpgurl(bid.getLpgurl());
                        dspResponse.setActtype(bid.getActtype());

                        if (bid.getMonitor() != null) {
                            Monitor.Builder monitor = Monitor.newBuilder();
                            dspResponse.setMonitorBuilder(monitor);

                            for (BidResponse.SeatBid.Bid.Monitor.Track track : bid.getMonitor().getImpurlList()) {
                                Track.Builder track1 = Track.newBuilder();
                                track1.setStartdelay(track.getStartdelay());
                                track1.setUrl(track.getUrl());
                                monitor.getImpurl().add(track1.build());
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
                        }

                        if (bid.getAdmCount() > 0) {
                            for (String url : bid.getAdmList()) {
                                dspResponse.getAdm().add(url);
                            }
                        } else {
                            for (BidResponse.SeatBid.Bid.NativeResponse.Asset asset : bid.getAdmNative().getAssetsList()) {
                                if (asset.hasTitle()) {
                                    dspResponse.setTitle(asset.getTitle().getText());
                                    continue;
                                }

                                if (asset.hasData()) {
                                    dspResponse.setDesc(asset.getData().getValue());
                                    continue;
                                }

                                if (asset.hasVideo()) {
                                    dspResponse.getAdm().add(asset.getVideo().getUrl());
                                    dspResponse.setDuration(asset.getVideo().getDuration());
                                    continue;
                                }

                                if (asset.hasImage()) {
                                    switch (asset.getImage().getType()) {
                                        case Constant.NativeImageType.ICON: {
                                            dspResponse.setIcon(asset.getImage().getUrl(0));
                                            break;
                                        }

                                        case Constant.NativeImageType.COVER: {
                                            dspResponse.setCover(asset.getImage().getUrl(0));
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

                        dspBidBuilder.setResponseBuilder(dspResponse);
                        dspBidBuilder.setStatus(Constant.StatusCode.OK);
                        return true;
                    } else {
                        dspBidBuilder.setStatus(Constant.StatusCode.BAD_REQUEST);
                    }
                } else {
                    dspBidBuilder.setStatus(Constant.StatusCode.BAD_REQUEST);
                }
            } else {
                dspBidBuilder.setStatus(Constant.StatusCode.REQUEST_TIMEOUT);
            }
        } catch (Exception ex) {
            dspBidMetaData.getDspBidBuilder().setStatus(Constant.StatusCode.BAD_REQUEST);
            System.err.println(ex.toString());
        }

        return false;
    }

    public String getWinNoticeUrl(DSPBidMetaData dspBidMetaData) {
        try {
            DSPResponse dspResponse = dspBidMetaData.getDspBidBuilder().getResponse();
            String url = dspResponse.getNurl().toString();
            url = url.replace("${AUCTION_ID}", dspResponse.getId())
                    .replace("${AUCTION_IMP_ID}", dspResponse.getImpid())
                    .replace("${AUCTION_BID_ID}", dspResponse.getBidid())
                    .replace("${AUCTION_AD_ID}", dspResponse.getAdid());

            if (url.contains("${AUCTION_PRICE")) {
                String text = String.format("%d_%d", dspBidMetaData.getDspBidBuilder().getPrice(), System.currentTimeMillis() / 1000);
                byte[] key = StringUtil.hex2Bytes(dspBidMetaData.getDspMetaData().getToken());
                byte[] data = AESUtil.encryptECB(text.getBytes("utf-8"), key, AESUtil.Algorithm.AES);
                return url.replace("${AUCTION_PRICE}", StringUtil.urlSafeBase64Encode(data));
            }

            return url;
        } catch (Exception ex) {
            System.err.println(ex.toString());
            return dspBidMetaData.getDspBidBuilder().getResponse().getNurl().toString();
        }
    }

    protected final boolean createDSPRequest(MediaBid.Builder mediaBidBuilder, MediaMetaData mediaMetaData, PlcmtMetaData plcmtMetaData, AdBlockMetaData adBlockMetaData, PolicyMetaData policyMetaData, DSPMetaData dspMetaData, DSPBid.Builder dspBidBuilder) {

        try {
            MediaRequest mediaRequest = mediaBidBuilder.getRequest();

            DSPRequest.Builder dspRequest = DSPRequest.newBuilder()
                    .setId(StringUtil.getUUID())
                    .setImpid(mediaBidBuilder.getImpid())
                    .setAdtype(plcmtMetaData.getAdType())
                    .setLayout(plcmtMetaData.getLayout())
                    .setTagid(plcmtMetaData.getAdspaceKey())
                    .setDealid(policyMetaData.getDealId())
                    .setTest(mediaRequest.getTest())
                    .setBidfloor(policyMetaData.getAdspaceInfoMap().get(plcmtMetaData.getId()).getBidFloor())
                    .setBidtype(policyMetaData.getAdspaceInfoMap().get(plcmtMetaData.getId()).getBidType())
                    .setTmax(mediaMetaData.getTimeout());

            dspBidBuilder.setDspid(dspMetaData.getId())
                    .setPolicyid(policyMetaData.getId())
                    .setDeliverytype(policyMetaData.getDeliveryType())
                    .setTime(System.currentTimeMillis())
                    .setRequestBuilder(dspRequest);

            return true;
        } catch (Exception ex) {
            System.err.println(ex.toString());
            return false;
        }
    }
}
