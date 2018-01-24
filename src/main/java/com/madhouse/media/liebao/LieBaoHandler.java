package com.madhouse.media.liebao;

import com.alibaba.fastjson.JSON;
import com.madhouse.cache.CacheManager;
import com.madhouse.cache.MediaBidMetaData;
import com.madhouse.cache.MediaMappingMetaData;
import com.madhouse.media.MediaBaseHandler;
import com.madhouse.resource.ResourceManager;
import com.madhouse.ssp.Constant;
import com.madhouse.ssp.avro.*;
import com.madhouse.util.*;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class LieBaoHandler extends MediaBaseHandler {
    @Override
    public boolean parseMediaRequest(HttpServletRequest req, MediaBidMetaData mediaBidMetaData, HttpServletResponse resp) {
        try {
            String bytes = HttpUtil.getRequestPostBytes(req);
            if (!StringUtils.isEmpty(bytes)) {
                LieBaoBidRequest bidRequest = JSON.parseObject(bytes, LieBaoBidRequest.class);
                if (bidRequest == null) {
                    resp.setStatus(Constant.StatusCode.BAD_REQUEST);
                    return false;
                }

                logger.info("LieBao Request params is : {}", JSON.toJSONString(bidRequest));

                MediaRequest.Builder mediaRequest = this.conversionToPremiumMADData(bidRequest);
                if (mediaRequest == null) {
                    outputStreamWrite(resp, null);
                    return false;
                }

                if (!this.checkRequestParam(mediaRequest)) {
                    outputStreamWrite(resp, null);
                    return false;
                }

                MediaBid.Builder mediaBid = MediaBid.newBuilder();
                mediaBid.setRequestBuilder(mediaRequest);
                mediaBidMetaData.getMediaBids().add(mediaBid);
                mediaBidMetaData.setRequestObject(bidRequest);
                return true;
            } else {
                resp.setStatus(Constant.StatusCode.BAD_REQUEST);
            }
        } catch (Exception e) {
            logger.error(e.toString());
            resp.setStatus(Constant.StatusCode.INTERNAL_ERROR);
        }

        return false;
    }

    private boolean outputStreamWrite(HttpServletResponse resp, LieBaoBidResponse bidResponse) {
        try {
            if (bidResponse != null) {
                resp.setStatus(Constant.StatusCode.OK);
                resp.setHeader("Content-Type", "application/json; charset=utf-8");

                String response = JSON.toJSONString(bidResponse);
                logger.info("LieBao Response is: {}", response);

                resp.getOutputStream().write(response.getBytes());
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

    private MediaRequest.Builder conversionToPremiumMADData(LieBaoBidRequest bidRequest) {
        MediaRequest.Builder mediaRequest = MediaRequest.newBuilder();

        try {
            if (ObjectUtils.isEmpty(bidRequest.getImp())) {
                return null;
            }

            LieBaoBidRequest.Imp impression = bidRequest.getImp().get(0);
            LieBaoBidRequest.Device device = bidRequest.getDevice();
            String os = StringUtil.toString(device.getOs()).toUpperCase();

            if (impression.getPmp() != null) {
                if (!ObjectUtils.isEmpty(impression.getPmp().getDeals())) {
                    int size = impression.getPmp().getDeals().size();
                    mediaRequest.setDealid(StringUtil.toString(impression.getPmp().getDeals().get(Utility.nextInt(size)).getId()));
                }
            }

            StringBuilder adspaceKey = new StringBuilder();
            adspaceKey.append("LB:").append(impression.getTagid()).append(":");

            mediaRequest.setOs(Constant.OSType.UNKNOWN);
            if (os.equals("ANDROID")) {
                adspaceKey.append("ANDROID");
                mediaRequest.setOs(Constant.OSType.ANDROID);
                mediaRequest.setDid(device.getImei());
                mediaRequest.setDpid(device.getIfa());
            } else if (os.equals("IOS")) {
                adspaceKey.append("IOS");
                mediaRequest.setOs(Constant.OSType.IOS);
                mediaRequest.setIfa(device.getIfa());
            }

            if (impression.getBanner() != null) {
                mediaRequest.setW(impression.getBanner().getW());
                mediaRequest.setH(impression.getBanner().getH());
            } else if (impression.getVideo() != null) {
                mediaRequest.setW(impression.getVideo().getW());
                mediaRequest.setH(impression.getVideo().getH());
            } else if (impression.getNativeObject() != null && impression.getNativeObject().getRequestNativeObject() != null) {
                LieBaoBidRequest.Imp.Native.LieBaoNative nativeObject = impression.getNativeObject().getRequestNativeObject();
                List<LieBaoBidRequest.Imp.Native.LieBaoNative.NativeTopLevel.Assets> assetsList = nativeObject.getNativeTopLevel().getAssets();
                LieBaoBidRequest.Imp.Native.LieBaoNative.NativeTopLevel.Assets imgAssets = getMainImageAssets(assetsList);
                LieBaoBidRequest.Imp.Native.LieBaoNative.NativeTopLevel.Assets titleAssets = getTitleAssets(assetsList);
                LieBaoBidRequest.Imp.Native.LieBaoNative.NativeTopLevel.Assets dataAssets = getDataAssets(assetsList);
                bidRequest.setSelectedImgId(imgAssets.getId());
                bidRequest.setSelectedTitleId(titleAssets.getId());
                bidRequest.setSelectedDataId(dataAssets.getId());
                mediaRequest.setW(imgAssets.getImg().getW());
                mediaRequest.setH(imgAssets.getImg().getH());
            }


            MediaMappingMetaData mediaMappingMetaData = CacheManager.getInstance().getMediaMapping(adspaceKey.toString());
            if (mediaMappingMetaData == null) {
                return null;
            }

            mediaRequest.setAdtype(2);
            mediaRequest.setAdspacekey(mediaMappingMetaData.getAdspaceKey());
            mediaRequest.setBid(StringUtil.toString(bidRequest.getId()));
            mediaRequest.setIp(StringUtil.toString(device.getIp()));
            mediaRequest.setUa(StringUtil.toString(device.getUa()));

            mediaRequest.setDpidmd5(StringUtil.toString(device.getDpidmd5()));
            mediaRequest.setMake(StringUtil.toString(device.getMake()));
            mediaRequest.setModel(StringUtil.toString(device.getModel()));
            mediaRequest.setOsv(StringUtil.toString(device.getOsv()));

            if (bidRequest.getTest() != null && bidRequest.getTest() == 1) {
                mediaRequest.setTest(Constant.Test.SIMULATION);
            } else {
                mediaRequest.setTest(Constant.Test.REAL);
            }

            mediaRequest.setCarrier(Constant.Carrier.UNKNOWN);
            if (!StringUtils.isEmpty(device.getCarrier())) {
                // 使用mnc+mcc识别码，默认UNKNOWN
                switch (device.getCarrier()) {
                    case LieBaoConstants.Carrier.CHINA_MOBILE: {
                        mediaRequest.setCarrier(Constant.Carrier.CHINA_MOBILE);
                        break;
                    }

                    case LieBaoConstants.Carrier.CHINA_UNICOM: {
                        mediaRequest.setCarrier(Constant.Carrier.CHINA_UNICOM);
                        break;
                    }

                    case LieBaoConstants.Carrier.CHINA_TELECOM: {
                        mediaRequest.setCarrier(Constant.Carrier.CHINA_TELECOM);
                        break;
                    }
                    default:
                        mediaRequest.setCarrier(Constant.Carrier.UNKNOWN);
                }
            }

            if (impression.getBidfloor() != null) {
                // ssp engine价格单位是分，猎豹是元
                mediaRequest.setBidfloor(impression.getBidfloor().intValue() * 100);
            }

            mediaRequest.setConnectiontype(Constant.ConnectionType.UNKNOWN);
            if (device.getConnectiontype() != null) {
                switch (device.getConnectiontype()) {
                    case LieBaoConstants.ConnectionType.UNKNOWN: {
                        mediaRequest.setConnectiontype(Constant.ConnectionType.UNKNOWN);
                        break;
                    }

                    case LieBaoConstants.ConnectionType.WIFI: {
                        mediaRequest.setConnectiontype(Constant.ConnectionType.WIFI);
                        break;
                    }
                    default:
                        mediaRequest.setConnectiontype(Constant.ConnectionType.UNKNOWN);
                        break;
                }
            }

            mediaRequest.setDevicetype(Constant.DeviceType.UNKNOWN);
            if (device.getDevicetype() != null) {
                switch (device.getDevicetype()) {
                    case LieBaoConstants.DeviceType.PHONE:
                        mediaRequest.setDevicetype(Constant.DeviceType.PHONE);
                        break;
                    case LieBaoConstants.DeviceType.TABLET:
                        mediaRequest.setDevicetype(Constant.DeviceType.PAD);
                        break;
                    default:
                        mediaRequest.setDevicetype(Constant.DeviceType.UNKNOWN);
                        break;
                }
            }

            if (device.getGeo() != null) {
                LieBaoBidRequest.Device.Geo geo = device.getGeo();
                Geo.Builder builder = Geo.newBuilder();
                builder.setLat(geo.getLat() == null ? 0f : geo.getLat().floatValue());
                builder.setLon(geo.getLon() == null ? 0f : geo.getLon().floatValue());
                mediaRequest.setGeoBuilder(builder);
            }

            if (bidRequest.getApp() != null) {
                String name = StringUtil.toString(bidRequest.getApp().getName());
                String bundle = StringUtil.toString(bidRequest.getApp().getBundle());
                mediaRequest.setName(StringUtils.isBlank(name) ? LieBaoConstants.App.getAppname(bundle) : name);
                mediaRequest.setBundle(StringUtils.isBlank(bundle) ? LieBaoConstants.App.BUNDLE : bundle);
                mediaRequest.setType(Constant.MediaType.APP);
            } else {
                mediaRequest.setName(LieBaoConstants.App.APPNAME);
                mediaRequest.setBundle(LieBaoConstants.App.BUNDLE);
                mediaRequest.setType(Constant.MediaType.APP);

                LieBaoBidRequest.App app = new LieBaoBidRequest.App();
                app.setBundle(LieBaoConstants.App.BUNDLE);
                app.setName(LieBaoConstants.App.APPNAME);
                bidRequest.setApp(app);
            }


            if (impression.getVideo() != null && impression.getBanner() != null) {
                logger.warn("LieBao Video and Banner Cannot exist at the same time");
                return null;
            }
            if (impression.getNativeObject() != null &&
                    null != impression.getNativeObject().getRequestNativeObject() &&
                    impression.getNativeObject().getRequestNativeObject().getNativeTopLevel() != null &&
                    impression.getNativeObject().getRequestNativeObject().getNativeTopLevel().getAssets() != null &&
                    impression.getNativeObject().getRequestNativeObject().getNativeTopLevel().getAssets().get(0) != null &&
                    impression.getNativeObject().getRequestNativeObject().getNativeTopLevel().getAssets().get(0).getId() == null) {
                logger.warn("LieBao bidRequest Native.Assets.id is null");
                return null;
            }
            return mediaRequest;
        } catch (Exception e) {
            logger.error(e.toString());
        }

        return null;
    }

    private LieBaoBidRequest.Imp.Native.LieBaoNative.NativeTopLevel.Assets getMainImageAssets(List<LieBaoBidRequest.Imp.Native.LieBaoNative.NativeTopLevel.Assets> assetsList) {
        for (LieBaoBidRequest.Imp.Native.LieBaoNative.NativeTopLevel.Assets assets : assetsList) {
            if (assets.getImg().getType() == LieBaoConstants.ImgType.MAIN_IGMAGE) {
                return assets;
            }
        }

        return null;
    }

    private LieBaoBidRequest.Imp.Native.LieBaoNative.NativeTopLevel.Assets getTitleAssets(List<LieBaoBidRequest.Imp.Native.LieBaoNative.NativeTopLevel.Assets> assetsList) {
        for (LieBaoBidRequest.Imp.Native.LieBaoNative.NativeTopLevel.Assets assets : assetsList) {
            if (assets.getTitle() != null) {
                return assets;
            }
        }

        return null;
    }

    private LieBaoBidRequest.Imp.Native.LieBaoNative.NativeTopLevel.Assets getDataAssets(List<LieBaoBidRequest.Imp.Native.LieBaoNative.NativeTopLevel.Assets> assetsList) {
        for (LieBaoBidRequest.Imp.Native.LieBaoNative.NativeTopLevel.Assets assets : assetsList) {
            if (assets.getData() != null) {
                return assets;
            }
        }

        return null;
    }


    @Override
    public boolean packageMediaResponse(MediaBidMetaData mediaBidMetaData, HttpServletResponse resp) {
        try {
            if (mediaBidMetaData != null && mediaBidMetaData.getMediaBids() != null && mediaBidMetaData.getMediaBids().size() > 0) {
                MediaBid.Builder mediaBid = mediaBidMetaData.getMediaBids().get(0);
                if (mediaBid.getStatus() == Constant.StatusCode.OK) {
                    LieBaoBidRequest bidRequest = (LieBaoBidRequest) mediaBidMetaData.getRequestObject();
                    MediaResponse.Builder mediaResponse = mediaBid.getResponseBuilder();

                    LieBaoBidResponse bidResponse = new LieBaoBidResponse();
                    bidResponse.setId(bidRequest.getId());
                    bidResponse.setBidid(ResourceManager.getInstance().nextId());

                    LieBaoBidResponse.Seatbid seatBid = new LieBaoBidResponse.Seatbid();
                    bidResponse.setSeatbid(new LinkedList<>());
                    bidResponse.getSeatbid().add(seatBid);

                    LieBaoBidResponse.Seatbid.Bid bid = new LieBaoBidResponse.Seatbid.Bid();
                    seatBid.setBid(new LinkedList<>());
                    seatBid.getBid().add(bid);

                    LieBaoBidRequest.Imp imp = bidRequest.getImp().get(0);

                    bid.setId(mediaBid.getImpid());
                    bid.setImpid(StringUtil.toString(imp.getId()));
                    // ssp engine价格单位是分，猎豹是元
                    bid.setPrice(mediaResponse.getPrice() != null ? mediaResponse.getPrice().floatValue() / 100 : 0);
                    // 当前只对接banner 开屏，可以没有bundle字段
                    Monitor.Builder monitor = mediaResponse.getMonitorBuilder();
                    // 这个字段是自定义的，用来在序列化response时选中适当的数据类型
                    bid.setBidRequest(bidRequest);
                    if (null != imp.getNativeObject()) {
                        buildAdmNative(bidRequest, imp, mediaResponse, bid, monitor);
                    } else if (null != imp.getBanner()) {
                        buildAdmBannerForOpen(imp, mediaResponse, bid, monitor);
                    } else if (null != imp.getVideo()) {
                        // video是 vast协议，暂不对接
                        return outputStreamWrite(resp, null);
                    }
                    bidResponse.setCur(LieBaoConstants.MoneyMark.CNY);
                    return outputStreamWrite(resp, bidResponse);
                }
            }
        } catch (Exception e) {
            logger.error(e.toString());
        }

        return outputStreamWrite(resp, null);
    }

    private void buildAdmBannerForOpen(LieBaoBidRequest.Imp imp, MediaResponse.Builder mediaResponse, LieBaoBidResponse.Seatbid.Bid bid, Monitor.Builder monitor) {
        LieBaoBidResponse.Seatbid.Bid.AdmBanner admBanner = new LieBaoBidResponse.Seatbid.Bid.AdmBanner();
        bid.setAdmBanner(admBanner);
        LieBaoBidResponse.Seatbid.Bid.AdmBanner.Banner banner = new LieBaoBidResponse.Seatbid.Bid.AdmBanner.Banner();
        admBanner.setBanner(banner);
        List<String> impUrls = new ArrayList<>(monitor.getImpurl().size());
        for (Track track : monitor.getImpurl()) {
            impUrls.add(track.getUrl());
        }
        banner.setImptrackers(impUrls);
        LieBaoBidResponse.Seatbid.Bid.AdmBanner.Banner.Link link = new LieBaoBidResponse.Seatbid.Bid.AdmBanner.Banner.Link();
        link.setClicktrackers(monitor.getClkurl());
        link.setUrl(mediaResponse.getLpgurl());
        banner.setLink(link);
        LieBaoBidResponse.Seatbid.Bid.AdmBanner.Banner.Img img = new LieBaoBidResponse.Seatbid.Bid.AdmBanner.Banner.Img();
        img.setH(imp.getBanner().getH());
        img.setW(imp.getBanner().getW());
        img.setUrl(mediaResponse.getAdm().get(0));
        banner.setImg(img);
    }

    private void buildAdmNative(LieBaoBidRequest bidRequest, LieBaoBidRequest.Imp imp, MediaResponse.Builder mediaResponse, LieBaoBidResponse.Seatbid.Bid bid, Monitor.Builder monitor) {
        LieBaoBidResponse.Seatbid.Bid.AdmNative admNative = new LieBaoBidResponse.Seatbid.Bid.AdmNative();
        bid.setAdmNative(admNative);
        LieBaoBidResponse.Seatbid.Bid.AdmNative.ResponseNative adsResNative = new LieBaoBidResponse.Seatbid.Bid.AdmNative.ResponseNative();
        admNative.setResponseNative(adsResNative);
        // 封装展示，点击跟踪链接
        if (monitor != null) {
            // 设置展示跟踪链接
            List<String> imptrackers = new ArrayList<>();
            adsResNative.setImptrackers(imptrackers);
            for (Track track : monitor.getImpurl()) {
                imptrackers.add(track.getUrl());
            }
            LieBaoBidResponse.Seatbid.Bid.AdmNative.ResponseNative.Link link = new LieBaoBidResponse.Seatbid.Bid.AdmNative.ResponseNative.Link();
            // 设置点击跟踪链接
            link.setClicktrackers(monitor.getClkurl());
            // 设置落地页
            link.setUrl(mediaResponse.getLpgurl());
            adsResNative.setLink(link);
        }
        // 设置素材信息
        ArrayList<LieBaoBidResponse.Seatbid.Bid.AdmNative.ResponseNative.Assets> assetsArrayList = new ArrayList<>();
        assetsArrayList.add(buildImage(bidRequest,imp, mediaResponse));
        assetsArrayList.add(buildTitle(bidRequest,mediaResponse));
        assetsArrayList.add(buildData(bidRequest,mediaResponse));
        adsResNative.setAssets(assetsArrayList);
    }

    private LieBaoBidResponse.Seatbid.Bid.AdmNative.ResponseNative.Assets buildData(LieBaoBidRequest bidRequest, MediaResponse.Builder mediaResponse) {
        LieBaoBidResponse.Seatbid.Bid.AdmNative.ResponseNative.Assets assets = new LieBaoBidResponse.Seatbid.Bid.AdmNative.ResponseNative.Assets();
        assets.setId(bidRequest.getSelectedDataId());
        LieBaoBidResponse.Seatbid.Bid.AdmNative.ResponseNative.Assets.Data data = new LieBaoBidResponse.Seatbid.Bid.AdmNative.ResponseNative.Assets.Data();
        data.setLabel("desc");
        data.setValue(mediaResponse.getDesc());
        assets.setData(data);
        return assets;
    }

    private LieBaoBidResponse.Seatbid.Bid.AdmNative.ResponseNative.Assets buildTitle(LieBaoBidRequest bidRequest, MediaResponse.Builder mediaResponse) {
        LieBaoBidResponse.Seatbid.Bid.AdmNative.ResponseNative.Assets assets = new LieBaoBidResponse.Seatbid.Bid.AdmNative.ResponseNative.Assets();
        assets.setId(bidRequest.getSelectedTitleId());
        LieBaoBidResponse.Seatbid.Bid.AdmNative.ResponseNative.Assets.Title title = new LieBaoBidResponse.Seatbid.Bid.AdmNative.ResponseNative.Assets.Title();
        title.setText(mediaResponse.getTitle());
        assets.setTitle(title);
        return assets;
    }

    private LieBaoBidResponse.Seatbid.Bid.AdmNative.ResponseNative.Assets buildImage(LieBaoBidRequest bidRequest, LieBaoBidRequest.Imp imp, MediaResponse.Builder mediaResponse) {

        LieBaoBidResponse.Seatbid.Bid.AdmNative.ResponseNative.Assets assets = new LieBaoBidResponse.Seatbid.Bid.AdmNative.ResponseNative.Assets();
        assets.setId(bidRequest.getSelectedImgId());
        LieBaoBidResponse.Seatbid.Bid.AdmNative.ResponseNative.Assets.Img img = new LieBaoBidResponse.Seatbid.Bid.AdmNative.ResponseNative.Assets.Img();
        LieBaoBidRequest.Imp.Native.LieBaoNative.NativeTopLevel.Assets mainImageAssets = getMainImageAssets(imp.getNativeObject().getRequestNativeObject().getNativeTopLevel().getAssets());
        img.setW(mainImageAssets.getImg().getW());
        img.setH(mainImageAssets.getImg().getH());
        img.setUrl(mediaResponse.getAdm().get(0));
        assets.setImg(img);
        return assets;
    }
}
