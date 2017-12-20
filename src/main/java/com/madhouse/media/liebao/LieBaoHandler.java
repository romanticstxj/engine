package com.madhouse.media.liebao;

import com.alibaba.fastjson.JSON;
import com.madhouse.cache.CacheManager;
import com.madhouse.cache.MediaBidMetaData;
import com.madhouse.cache.MediaMappingMetaData;
import com.madhouse.media.MediaBaseHandler;
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

                mediaBidMetaData.getMediaBidBuilder().setRequestBuilder(mediaRequest);
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
            adspaceKey.append("LB:");
            if (impression.getBanner() != null) {
                adspaceKey.append(impression.getBanner().getW()).append(":");
                adspaceKey.append(impression.getBanner().getH()).append(":");
                mediaRequest.setW(impression.getBanner().getW());
                mediaRequest.setH(impression.getBanner().getH());
            } else if (impression.getVideo() != null) {
                adspaceKey.append(impression.getVideo().getW()).append(":");
                adspaceKey.append(impression.getVideo().getH()).append(":");
                mediaRequest.setW(impression.getVideo().getW());
                mediaRequest.setH(impression.getVideo().getH());
            } else if (impression.getNativeObject() != null && impression.getNativeObject().getRequestNativeObject() != null) {
                LieBaoBidRequest.Imp.Native.LieBaoNative nativeObject = impression.getNativeObject().getRequestNativeObject();
                List<LieBaoBidRequest.Imp.Native.LieBaoNative.NativeTopLevel.Assets> assetsList = nativeObject.getNativeTopLevel().getAssets();
                LieBaoBidRequest.Imp.Native.LieBaoNative.NativeTopLevel.Assets assets = getMainImageAssets(assetsList);
                bidRequest.setSelectedAssetsId(assets.getId());
                adspaceKey.append(assets.getImg().getW()).append(":");
                adspaceKey.append(assets.getImg().getH()).append(":");
                mediaRequest.setW(assets.getImg().getW());
                mediaRequest.setH(assets.getImg().getH());
            }
            mediaRequest.setOs(Constant.OSType.UNKNOWN);
            if (os.equals("ANDROID")) {
                adspaceKey.append("ANDROID").append(":");
                mediaRequest.setOs(Constant.OSType.ANDROID);
            } else if (os.equals("IOS")) {
                adspaceKey.append("IOS").append(":");
                mediaRequest.setOs(Constant.OSType.IOS);
            }

            float ratio = (float) Math.round(mediaRequest.getW().floatValue() / mediaRequest.getH().floatValue() * 10) / 10;
            if (ratio == LieBaoConstants.AdType.NATIVE_BIG) {
                adspaceKey.append("NATIVE_BIG");
                bidRequest.setAdmType(LieBaoConstants.AdType.NATIVE_BIG);
            } else if (ratio == LieBaoConstants.AdType.NATIVE_SMALL) {
                adspaceKey.append("NATIVE_SMALL");
                bidRequest.setAdmType(LieBaoConstants.AdType.NATIVE_SMALL);
            } else if (ratio == LieBaoConstants.AdType.BANNER_IAB) {
                adspaceKey.append("BANNER_IAB");
                bidRequest.setAdmType(LieBaoConstants.AdType.BANNER_IAB);
            } else if (ratio == LieBaoConstants.AdType.BANNER_OPEN) {
                adspaceKey.append("BANNER_OPEN");
                bidRequest.setAdmType(LieBaoConstants.AdType.BANNER_OPEN);
            } else if (ratio == LieBaoConstants.AdType.VIDEO_HOR) {
                adspaceKey.append("VIDEO_HOR");
                bidRequest.setAdmType(LieBaoConstants.AdType.VIDEO_HOR);
            } else if (ratio == LieBaoConstants.AdType.VIDEO_VER) {
                adspaceKey.append("VIDEO_VER");
                bidRequest.setAdmType(LieBaoConstants.AdType.VIDEO_VER);
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

            mediaRequest.setDid(StringUtil.toString(device.getImei()));
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
                }
            }

            mediaRequest.setDevicetype(Constant.DeviceType.UNKNOWN);
            if (device.getDevicetype() != null) {
                switch (device.getDevicetype()) {
                    case LieBaoConstants.DeviceType.PHONE: {
                        mediaRequest.setDevicetype(Constant.DeviceType.PHONE);
                        break;
                    }

                    case LieBaoConstants.DeviceType.TABLET: {
                        mediaRequest.setDevicetype(Constant.DeviceType.PAD);
                        break;
                    }

                    case LieBaoConstants.DeviceType.UNKNOWN: {
                        mediaRequest.setDevicetype(Constant.DeviceType.UNKNOWN);
                        break;
                    }
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
                mediaRequest.setName(StringUtil.toString(bidRequest.getApp().getName()));
                mediaRequest.setBundle(StringUtil.toString(bidRequest.getApp().getBundle()));
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
            if ((bidRequest.getAdmType() == LieBaoConstants.AdType.NATIVE_BIG || bidRequest.getAdmType() == LieBaoConstants.AdType.NATIVE_SMALL) && bidRequest.getSelectedAssetsId() <= 0) {
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


    @Override
    public boolean packageMediaResponse(MediaBidMetaData mediaBidMetaData, HttpServletResponse resp) {
        try {
            if (mediaBidMetaData != null && mediaBidMetaData.getMediaBidBuilder() != null && mediaBidMetaData.getMaterialMetaData() != null) {
                if (mediaBidMetaData.getMediaBidBuilder().getStatus() == Constant.StatusCode.OK) {
                    LieBaoBidRequest bidRequest = (LieBaoBidRequest) mediaBidMetaData.getRequestObject();
                    MediaBid.Builder mediaBid = mediaBidMetaData.getMediaBidBuilder();
                    MediaResponse.Builder mediaResponse = mediaBid.getResponseBuilder();

                    LieBaoBidResponse bidResponse = new LieBaoBidResponse();
                    bidResponse.setId(bidRequest.getId());
                    bidResponse.setBidid(mediaBid.getImpid());

                    LieBaoBidResponse.Seatbid seatBid = new LieBaoBidResponse.Seatbid();
                    bidResponse.setSeatbid(new LinkedList<>());
                    bidResponse.getSeatbid().add(seatBid);

                    LieBaoBidResponse.Seatbid.Bid bid = new LieBaoBidResponse.Seatbid.Bid();
                    seatBid.setBid(new LinkedList<>());
                    seatBid.getBid().add(bid);

                    bid.setId(mediaBid.getImpid());
                    bid.setImpid(StringUtil.toString(bidRequest.getImp().get(0).getId()));
                    // ssp engine价格单位是分，猎豹是元
                    bid.setPrice(mediaResponse.getPrice() != null ? mediaResponse.getPrice().floatValue() / 100 : 0);
                    // 当前只对接banner 开屏，可以没有bundle字段
                    // bid.setBundle(bidRequest.getApp().getBundle());
                    // 分别构建native，banner，video，的adm字段,目前只构建banner 开屏
                    Monitor.Builder monitor = mediaResponse.getMonitorBuilder();
                    // 这个字段是自定义的，用来在序列化response时选中适当的数据类型
                    float admType = bidRequest.getAdmType();
                    bid.setAdmType(admType);
                    if (admType == LieBaoConstants.AdType.NATIVE_BIG || admType == LieBaoConstants.AdType.NATIVE_SMALL) {
                        // native时：
                        //  buildAdmNative(bidRequest, mediaResponse, bid, monitor);
                        return outputStreamWrite(resp, null);
                    } else if (admType == LieBaoConstants.AdType.BANNER_OPEN) {
                        // 当前猎豹只支持image/jpeg类型。
                        String[] split = mediaBidMetaData.getMaterialMetaData().getAdm().get(0).split("\\.");
                        if (LieBaoConstants.MimeType.IMAGE_JPEG.contains(split[split.length - 1])) {
                            // banner 开屏时：
                            buildAdmBannerForOpen(mediaBidMetaData, mediaResponse, bid, monitor);
                        }else{
                            return outputStreamWrite(resp, null);
                        }
                    } else if (admType == LieBaoConstants.AdType.BANNER_IAB) {
                        // banner IAB时：
                        // buildAdmBannerForIAB(mediaBidMetaData, mediaResponse, bid, monitor);
                        return outputStreamWrite(resp, null);
                    } else if (admType == LieBaoConstants.AdType.VIDEO_HOR || admType == LieBaoConstants.AdType.VIDEO_VER) {
                        // video时：
                        // 不管是那个版本，都转成文档中提供的google网盘中的vast版本
                        // List<Integer> protocols = bidRequest.getImp().get(0).getVideo().getProtocols();
                        // if (protocols.containsAll(LieBaoConstants.Vast.INLINE_LIST)) {

                        // } else if (protocols.containsAll(LieBaoConstants.Vast.WRAPPER_LIST)) {

                        // }
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

    private void buildAdmBannerForIAB(MediaBidMetaData mediaBidMetaData, MediaResponse.Builder mediaResponse, LieBaoBidResponse.Seatbid.Bid bid, Monitor.Builder monitor) {

    }

    private void buildAdmBannerForOpen(MediaBidMetaData mediaBidMetaData, MediaResponse.Builder mediaResponse, LieBaoBidResponse.Seatbid.Bid bid, Monitor.Builder monitor) {
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
        img.setH(mediaBidMetaData.getMaterialMetaData().getH());
        img.setW(mediaBidMetaData.getMaterialMetaData().getW());
        img.setUrl(mediaBidMetaData.getMaterialMetaData().getAdm().get(0));
        banner.setImg(img);
    }

    private void buildAdmNative(LieBaoBidRequest bidRequest, MediaResponse.Builder mediaResponse, LieBaoBidResponse.Seatbid.Bid bid, Monitor.Builder monitor) {
        bid.setAdmType(LieBaoConstants.AdType.NATIVE_BIG);
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
        LieBaoBidResponse.Seatbid.Bid.AdmNative.ResponseNative.Assets assets = new LieBaoBidResponse.Seatbid.Bid.AdmNative.ResponseNative.Assets();
        ArrayList<LieBaoBidResponse.Seatbid.Bid.AdmNative.ResponseNative.Assets> assetsArrayList = new ArrayList<>();
        assetsArrayList.add(assets);
        adsResNative.setAssets(assetsArrayList);
        assets.setId(bidRequest.getSelectedAssetsId());
        /**
         // 由于assets只有id是必须的，其他的可以先不设置，需要的时候再打开。节省带宽
         // 设置素材title
         LieBaoBidResponse.Seatbid.Bid.AdmNative.ResponseNative.Assets.Title title = assets.new Title();
         title.setText(mediaBidMetaData.getMaterialMetaData().getTitle());
         assets.setTitle(title);
         // 设置素材img
         LieBaoBidResponse.Seatbid.Bid.AdmNative.ResponseNative.Assets.Img img = assets.new Img();
         img.setW(mediaBidMetaData.getMaterialMetaData().getW());
         img.setH(mediaBidMetaData.getMaterialMetaData().getH());
         img.setUrl(mediaBidMetaData.getMaterialMetaData().getMediaMaterialUrl());
         assets.setImg(img);
         LieBaoBidResponse.Seatbid.Bid.AdmNative.ResponseNative.Assets.Data data = assets.new Data();
         data.setLabel("desc");
         data.setValue(mediaBidMetaData.getMaterialMetaData().getDesc());
         assets.setData(data);
         // link对象有外层的link就足够了，如果这里也设置link外层的将被替换
         */}
}
