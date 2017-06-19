package com.madhouse.dsp;

/**
 * Created by WUJUNFENG on 2017/5/22.
 */

import com.madhouse.cache.*;
import com.madhouse.cache.AdBlockMetaData;
import com.madhouse.ssp.Constant;
import com.madhouse.ssp.PremiumMADDataModel;
import com.madhouse.util.AESUtil;
import com.madhouse.util.StringUtil;
import com.madhouse.util.Utility;
import com.madhouse.rtb.PremiumMADRTBProtocol.*;
import com.sun.corba.se.pept.transport.ReaderThread;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.util.EntityUtils;
import scala.Predef;

import javax.print.DocFlavor;


public abstract class DSPBaseHandler {
    public HttpRequestBase packageBidRequest(PremiumMADDataModel.MediaBid.Builder mediaBidBuilder,
                                             MediaMetaData mediaMetaData,
                                             PlcmtMetaData plcmtMetaData,
                                             AdBlockMetaData adBlockMetaData,
                                             PolicyMetaData policyMetaData,
                                             DSPMetaData dspMetaData,
                                             PremiumMADDataModel.DSPBid.Builder dspBidBuilder,
                                             String tagid) {

        HttpPost httpPost = new HttpPost(dspMetaData.getBidurl());
        httpPost.setHeader("Content-Type", "application/x-protobuf");

        PremiumMADDataModel.MediaBid.MediaRequest mediaRequest = mediaBidBuilder.getRequest();

        int bidfloor = plcmtMetaData.getBidfloor();
        if (policyMetaData.getTradingtype() != Constant.TradingType.RTB) {
            float floating = policyMetaData.getFloating(dspMetaData.getDspid());
            if (floating <= 0.0f) {
                floating = dspMetaData.getFloating();
            }

            bidfloor = (int)(bidfloor * floating);
        }

        //bid request
        BidRequest.Builder bidRequest = BidRequest.newBuilder();
        bidRequest.setId(StringUtil.getUUID());
        bidRequest.setTmax(mediaMetaData.getTmax());
        bidRequest.setTest(0);
        bidRequest.setAt(2);

        if (adBlockMetaData != null) {
            bidRequest.addAllBadv(adBlockMetaData.getBadv());
            bidRequest.addAllBcat(adBlockMetaData.getBcat());
        }

        if (mediaMetaData.getMtype() == Constant.MediaType.APP) {
            BidRequest.App.Builder app = BidRequest.App.newBuilder();
            app.setId(Long.toString(mediaMetaData.getMid()));
            app.setBundle(mediaRequest.getBundle());
            app.addCat(Integer.toString(mediaMetaData.getMcat()));
            app.setName(mediaMetaData.getName());
            bidRequest.setApp(app);
        }

        if (mediaMetaData.getMtype() == Constant.MediaType.SITE) {
            BidRequest.Site.Builder site = BidRequest.Site.newBuilder();
            site.setId(Long.toString(mediaMetaData.getMid()));
            site.addCat(Integer.toString(mediaMetaData.getMcat()));
            site.setName(mediaMetaData.getName());
            bidRequest.setSite(site);
        }

        {
            BidRequest.Device.Builder device = BidRequest.Device.newBuilder();
            device.setIp(mediaRequest.getIp());
            device.setUa(mediaRequest.getUa());
            device.setDid(mediaRequest.getDid());
            device.setDidmd5(mediaRequest.getDidmd5());
            device.setDpid(mediaRequest.getDpid());
            device.setDpidmd5(mediaRequest.getDpidmd5());
            device.setIfa(mediaRequest.getIfa());
            device.setMac1(mediaRequest.getMac());
            device.setMac1Md5(mediaRequest.getMacmd5());
            device.setDevicetype(mediaRequest.getDevicetype());
            device.setConnectiontype(mediaRequest.getConnectiontype());
            device.setCarrier(mediaRequest.getCarrier());
            device.setMake(mediaRequest.getMake());
            device.setModel(mediaRequest.getModel());
            device.setOs(mediaRequest.getOs());
            device.setOsv(mediaRequest.getOsv());
            BidRequest.Device.Geo.Builder geo = BidRequest.Device.Geo.newBuilder();
            geo.setLon(mediaRequest.getLon());
            geo.setLat(mediaRequest.getLat());
            device.setGeo(geo);
            bidRequest.setDevice(device);
        }

        {
            BidRequest.Impression.Builder impression = BidRequest.Impression.newBuilder();
            impression.setId(mediaBidBuilder.getImpid());
            impression.setBidtype(Constant.BidType.CPM);
            impression.setBidfloor(bidfloor);
            impression.setTagid(tagid);

            if (policyMetaData.getTradingtype() != Constant.TradingType.RTB) {
                BidRequest.Impression.PMP.Builder pmp = BidRequest.Impression.PMP.newBuilder();
                pmp.setPrivateAuction(1);
                BidRequest.Impression.PMP.Deal.Builder deal = BidRequest.Impression.PMP.Deal.newBuilder();
                deal.setAt(3);
                deal.setBidfloor(bidfloor);
                pmp.addDeals(deal);
                impression.setPmp(pmp);
            }

            switch (plcmtMetaData.getType()) {
                case Constant.PlcmtType.VIDEO: {
                    BidRequest.Impression.Video.Builder video = BidRequest.Impression.Video.newBuilder();
                    video.setW(plcmtMetaData.getW());
                    video.setH(plcmtMetaData.getH());
                    video.setLinearity(plcmtMetaData.getLinearity());
                    video.setStartdelay(plcmtMetaData.getStartdelay());
                    video.setMinduration(plcmtMetaData.getMinduration());
                    video.setMaxduration(plcmtMetaData.getMaxduration());
                    video.addAllMimes(plcmtMetaData.getMimes());
                    impression.setVideo(video);
                    break;
                }

                case Constant.PlcmtType.NATIVE: {
                    BidRequest.Impression.Native.Builder vnative =BidRequest.Impression.Native.newBuilder();
                    vnative.setVer("1.1");
                    BidRequest.Impression.Native.NativeRequest.Builder nativeRequest = BidRequest.Impression.Native.NativeRequest.newBuilder();
                    nativeRequest.setPlcmtcnt(1);
                    nativeRequest.setLayout(plcmtMetaData.getLayout());

                    int id = 1;

                    if (plcmtMetaData.getIcon() > 0) {
                        BidRequest.Impression.Native.NativeRequest.Asset.Builder asset = BidRequest.Impression.Native.NativeRequest.Asset.newBuilder();
                        BidRequest.Impression.Native.NativeRequest.Asset.Image.Builder image = BidRequest.Impression.Native.NativeRequest.Asset.Image.newBuilder();
                        image.setType(Constant.NativeImageType.ICON);
                        image.setW(128);
                        image.setH(128);
                        image.addMimes("image/png");
                        image.addMimes("image/jpeg");
                        asset.setId(Integer.toString(id++));
                        asset.setRequired(true);
                        asset.setImage(image);
                        nativeRequest.addAssets(asset);
                    }

                    if (plcmtMetaData.getTitle() >= 0) {
                        BidRequest.Impression.Native.NativeRequest.Asset.Builder asset = BidRequest.Impression.Native.NativeRequest.Asset.newBuilder();
                        BidRequest.Impression.Native.NativeRequest.Asset.Title.Builder title = BidRequest.Impression.Native.NativeRequest.Asset.Title.newBuilder();
                        title.setLen(plcmtMetaData.getTitle());
                        asset.setId(Integer.toString(id++));
                        asset.setRequired(true);
                        asset.setTitle(title);
                        nativeRequest.addAssets(asset);
                    }

                    if (plcmtMetaData.getDesc() >= 0) {
                        BidRequest.Impression.Native.NativeRequest.Asset.Builder asset = BidRequest.Impression.Native.NativeRequest.Asset.newBuilder();
                        BidRequest.Impression.Native.NativeRequest.Asset.Data.Builder data = BidRequest.Impression.Native.NativeRequest.Asset.Data.newBuilder();
                        data.setType(Constant.NativeDescType.DESC);
                        data.setLen(plcmtMetaData.getDesc());
                        asset.setId(Integer.toString(id++));
                        asset.setRequired(true);
                        asset.setData(data);
                        nativeRequest.addAssets(asset);
                    }

                    if (plcmtMetaData.getCover() > 0) {
                        BidRequest.Impression.Native.NativeRequest.Asset.Builder asset = BidRequest.Impression.Native.NativeRequest.Asset.newBuilder();
                        BidRequest.Impression.Native.NativeRequest.Asset.Image.Builder image = BidRequest.Impression.Native.NativeRequest.Asset.Image.newBuilder();
                        image.setType(Constant.NativeImageType.COVER);
                        image.setW(plcmtMetaData.getW());
                        image.setH(plcmtMetaData.getH());
                        image.addMimes("image/png");
                        image.addMimes("image/jpeg");
                        asset.setId(Integer.toString(id++));
                        asset.setRequired(true);
                        asset.setImage(image);
                        nativeRequest.addAssets(asset);
                    }

                    if (plcmtMetaData.getLayout() == Constant.NativeLayout.VIDEO) {
                        BidRequest.Impression.Native.NativeRequest.Asset.Builder asset = BidRequest.Impression.Native.NativeRequest.Asset.newBuilder();
                        BidRequest.Impression.Native.NativeRequest.Asset.Video.Builder video = BidRequest.Impression.Native.NativeRequest.Asset.Video.newBuilder();
                        video.setW(plcmtMetaData.getW());
                        video.setH(plcmtMetaData.getH());
                        video.addMimes("video/mp4");
                        video.addMimes("video/x-flv");
                        video.setMinduration(plcmtMetaData.getMinduration());
                        video.setMaxduration(plcmtMetaData.getMaxduration());
                        asset.setId(Integer.toString(id++));
                        asset.setRequired(true);
                        asset.setVideo(video);
                        nativeRequest.addAssets(asset);
                    } else {
                        BidRequest.Impression.Native.NativeRequest.Asset.Builder asset = BidRequest.Impression.Native.NativeRequest.Asset.newBuilder();
                        BidRequest.Impression.Native.NativeRequest.Asset.Image.Builder image = BidRequest.Impression.Native.NativeRequest.Asset.Image.newBuilder();
                        image.setType(Constant.NativeImageType.MAIN);
                        image.setW(plcmtMetaData.getW());
                        image.setH(plcmtMetaData.getH());
                        image.addAllMimes(plcmtMetaData.getMimes());
                        asset.setId(Integer.toString(id++));
                        asset.setRequired(true);
                        asset.setImage(image);
                        nativeRequest.addAssets(asset);
                    }

                    vnative.setRequest(nativeRequest);
                    impression.setNative(vnative);
                    break;
                }

                default: {
                    BidRequest.Impression.Banner.Builder banner = BidRequest.Impression.Banner.newBuilder();
                    banner.setType(plcmtMetaData.getType());
                    banner.setW(plcmtMetaData.getW());
                    banner.setH(plcmtMetaData.getH());
                    banner.addAllMimes(plcmtMetaData.getMimes());
                    impression.setBanner(banner);
                    break;
                }
            }

            bidRequest.addImp(impression);
        }

        try {
            dspBidBuilder.setDspid(dspMetaData.getDspid());
            dspBidBuilder.setPolicyid(policyMetaData.getId());
            dspBidBuilder.setTradingtype(policyMetaData.getTradingtype());
            dspBidBuilder.setTime(System.currentTimeMillis());
            dspBidBuilder.setRequest(bidRequest);

            ByteArrayEntity entity = new ByteArrayEntity(bidRequest.build().toByteArray());
            httpPost.setEntity(entity);
        } catch (Exception ex) {
            System.err.println(ex.toString());
            return null;
        }

        return httpPost;
    }

    public boolean parseBidResponse(HttpResponse httpResponse, BidMetaData bidMetaData, PremiumMADDataModel.DSPBid.Builder dspBidBuilder) {
        if (httpResponse != null) {
            int status = httpResponse.getStatusLine().getStatusCode();
            dspBidBuilder.setStatus(status);

            if (status == 200) {
                try {
                    HttpEntity entity = httpResponse.getEntity();
                    BidResponse bidResponse = BidResponse.parseFrom(EntityUtils.toByteArray(entity));
                    dspBidBuilder.setResponse(bidResponse);

                    if (!bidResponse.hasNbr() || bidResponse.getNbr() < 0) {
                        if (bidResponse.getSeatbidCount() > 0 && bidResponse.getSeatbid(0).getBidCount() > 0) {
                            BidResponse.SeatBid.Bid bid = bidResponse.getSeatbid(0).getBid(0);

                            bidMetaData.setId(bidResponse.hasId() ? bidResponse.getId() : "");
                            bidMetaData.setImpid(bid.hasImpid() ? bid.getImpid() : "");;
                            bidMetaData.setBidid(bidResponse.hasBidid() ? bidResponse.getBidid() : "");
                            bidMetaData.setAdid(bid.hasAdid() ? bid.getAdid() : "");
                            bidMetaData.setAdmid(bid.hasAdmid() ? bid.getAdmid() : "");
                            bidMetaData.setPrice(bid.hasPrice() ? bid.getPrice() : 0);
                            return true;
                        }
                    }

                } catch (Exception ex) {
                    dspBidBuilder.setStatus(Constant.StatusCode.BAD_REQUEST);
                    System.err.println(ex.toString());
                }
            }
        }

        return false;
    }

    public String getWinNoticeUrl(int price, DSPMetaData dspMetaData, BidMetaData bidMetaData) {

        try {
            String url = bidMetaData.getWinurl();
            url = url.replace("${AUCTION_ID}", bidMetaData.getId())
                    .replace("${AUCTION_IMP_ID}", bidMetaData.getImpid())
                    .replace("${AUCTION_BID_ID}", bidMetaData.getBidid())
                    .replace("${AUCTION_AD_ID}", bidMetaData.getAdid());

            if (url.contains("${AUCTION_PRICE")) {
                String text = String.format("%d_%d", price, System.currentTimeMillis() / 1000);
                byte[] key = StringUtil.hex2Bytes(dspMetaData.getToken());
                byte[] data = AESUtil.encryptECB(text.getBytes("utf-8"), key, AESUtil.Algorithm.AES);
                return url.replace("${AUCTION_PRICE}", StringUtil.urlSafeBase64Encode(data));
            }

            return url;
        } catch (Exception ex) {
            System.err.println(ex.toString());
            return bidMetaData.getWinurl();
        }
    }
}
