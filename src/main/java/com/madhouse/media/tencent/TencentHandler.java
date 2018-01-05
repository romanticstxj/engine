package com.madhouse.media.tencent;

import com.googlecode.protobuf.format.JsonFormat;
import com.madhouse.cache.*;
import com.madhouse.media.MediaBaseHandler;
import com.madhouse.media.tencent.GPBForDSP.Request;
import com.madhouse.media.tencent.GPBForDSP.Request.App;
import com.madhouse.media.tencent.GPBForDSP.Request.Device;
import com.madhouse.media.tencent.GPBForDSP.Request.Impression;
import com.madhouse.media.tencent.GPBForDSP.Response;
import com.madhouse.ssp.Constant;
import com.madhouse.ssp.avro.Geo;
import com.madhouse.ssp.avro.MediaBid;
import com.madhouse.ssp.avro.MediaRequest;
import com.madhouse.ssp.avro.MediaResponse.Builder;
import com.madhouse.ssp.avro.Track;
import com.madhouse.util.ObjectUtils;
import com.madhouse.util.StringUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedList;
import java.util.List;

public class TencentHandler extends MediaBaseHandler {

    @Override
    public boolean parseMediaRequest(HttpServletRequest req, MediaBidMetaData mediaBidMetaData, HttpServletResponse resp) {
        try {
            GPBForDSP.Request bidRequest = GPBForDSP.Request.parseFrom(IOUtils.toByteArray(req.getInputStream()));
            logger.info("Tencent Request params is : {}", JsonFormat.printToString(bidRequest));
            int status = validateRequiredParam(bidRequest);
            mediaBidMetaData.setRequestObject(bidRequest);
            if (Constant.StatusCode.OK == status) {
                List<MediaBid.Builder> mediaBids = conversionToPremiumMADDataModel(bidRequest);
                if (!ObjectUtils.isEmpty(mediaBids)) {
                    mediaBidMetaData.setMediaBids(mediaBids);
                    return true;
                }
            }
        } catch (Exception e) {
            logger.error(e.toString() + "_Status_" + Constant.StatusCode.NO_CONTENT);
        }
        GPBForDSP.Response.Builder bidResponse = convertToTencentResponse(mediaBidMetaData, Constant.StatusCode.NO_CONTENT);
        outputStreamWrite(resp, bidResponse);
        resp.setStatus(Constant.StatusCode.OK);
        return false;

    }


    private List<MediaBid.Builder> conversionToPremiumMADDataModel(Request bidRequest) {
        List<MediaBid.Builder> mediaBids = new LinkedList<>();

        MediaRequest.Builder mediaRequest = MediaRequest.newBuilder();


        Device device = bidRequest.getDevice();
        App app = bidRequest.getApp();

        mediaRequest.setBid(bidRequest.getId());

        if (app != null) {
            mediaRequest.setName(StringUtil.toString(app.getName()));
        } else {
            mediaRequest.setName(StringUtil.toString("TENCENT"));
        }

        mediaRequest.setBundle("com.tencent.adx");

        mediaRequest.setDevicetype(Constant.DeviceType.UNKNOWN);
        // banner&video同时存在 优先响应video广告





        if (device.hasCarrier()) {
            int carrier = device.getCarrier();
            switch (carrier) {
                case TencentStatusCode.Carrier.CHINA_MOBILE:
                    mediaRequest.setCarrier(Constant.Carrier.CHINA_MOBILE);
                    break;
                case TencentStatusCode.Carrier.CHINA_TELECOM:
                    mediaRequest.setCarrier(Constant.Carrier.CHINA_TELECOM);
                    break;
                case TencentStatusCode.Carrier.CHINA_UNICOM:
                    mediaRequest.setCarrier(Constant.Carrier.CHINA_UNICOM);
                    break;
                default:
                    mediaRequest.setCarrier(Constant.Carrier.UNKNOWN);
                    break;
            }
        } else {
            mediaRequest.setCarrier(Constant.Carrier.UNKNOWN);
        }
        if (device.hasConnectiontype()) {
            int connectiontype = device.getConnectiontype();
            switch (connectiontype) {
                case TencentStatusCode.ConnectionType.Ethernet:
                    mediaRequest.setConnectiontype(Constant.ConnectionType.ETHERNET);
                    break;
                case TencentStatusCode.ConnectionType._2G:
                    mediaRequest.setConnectiontype(Constant.ConnectionType._2G);
                    break;
                case TencentStatusCode.ConnectionType._3G:
                    mediaRequest.setConnectiontype(Constant.ConnectionType._3G);
                    break;
                case TencentStatusCode.ConnectionType._4G:
                    mediaRequest.setConnectiontype(Constant.ConnectionType._4G);
                    break;
                case TencentStatusCode.ConnectionType.WIFI:
                    mediaRequest.setConnectiontype(Constant.ConnectionType.WIFI);
                    break;
                default:
                    mediaRequest.setConnectiontype(Constant.ConnectionType.CELL);
                    break;
            }
        } else {
            mediaRequest.setConnectiontype(Constant.ConnectionType.CELL);
        }


        mediaRequest.setOsv(StringUtil.toString(device.getOsv()));
        mediaRequest.setMacmd5(StringUtil.toString(device.getMac()));
        mediaRequest.setName(StringUtil.toString(app.getName()));
        mediaRequest.setMake(StringUtil.toString(device.getMake()));
        mediaRequest.setModel(StringUtil.toString(device.getModel()));
        mediaRequest.setUa(StringUtil.toString(device.getUa()));
        mediaRequest.setIp(StringUtil.toString(device.getIp()));

        if (device.getGeo() != null) {
            if (device.getGeo().hasLongitude() && device.getGeo().hasLongitude()) {
                Geo.Builder geo = Geo.newBuilder();
                geo.setLat(device.getGeo().getLatitude());
                geo.setLon(device.getGeo().getLongitude());
                mediaRequest.setGeoBuilder(geo);
            }
        }

        mediaRequest.setAdtype(2);
        mediaRequest.setType(bidRequest.hasSite() ? Constant.MediaType.APP : Constant.MediaType.SITE);
        for (Impression imp : bidRequest.getImpressionList()) {
            MediaBid.Builder mediaBid = MediaBid.newBuilder();
            MediaRequest.Builder request = MediaRequest.newBuilder(mediaRequest);
            request.setBid(imp.getId());
            request.setDealid(StringUtil.toString(imp.getDealid()));
            StringBuilder sb = new StringBuilder();
            sb.append("TENC:");
            String tencAdspaceId = bidRequest.getImpression(0).getTagid();//腾讯广告位(广告位ID，同资源报表中的广告位ID，如 Ent_F_Width1)
            sb.append(tencAdspaceId).append(":");

            if (imp.hasVideo()) {
                request.setW(imp.getVideo().getWidth());
                request.setH(imp.getVideo().getHeight());
                sb.append(":" + imp.getVideo().getWidth());
                sb.append(":" + imp.getVideo().getHeight());
            } else if (imp.hasBanner()) {
                // 优先使用最大尺寸
                Impression.MaterialFormat maxMaterialFormat = getMaxSizeMaterialFormat(imp);
                if (maxMaterialFormat != null) {
                    request.setW(maxMaterialFormat.getWidth());
                    request.setH(maxMaterialFormat.getHeight());
                    sb.append(":" + maxMaterialFormat.getWidth());
                    sb.append(":" + maxMaterialFormat.getHeight());
                } else {
                    request.setW(imp.getBanner().getWidth());
                    request.setH(imp.getBanner().getHeight());
                    sb.append(":" + imp.getBanner().getWidth());
                    sb.append(":" + imp.getBanner().getHeight());
                }
            }

            if (isBanner(imp)) {
                sb.append(":BANNER");
            } else {
                sb.append(":VIDEO");
            }
            String os = device.getOs();//iPhone.OS.9.3.2
            if (!StringUtils.isEmpty(os)) {
                if (os.toLowerCase().contains(TencentStatusCode.Os.OS_IPHONE) || os.toLowerCase().contains(TencentStatusCode.Os.OS_IOS)) {
                    request.setOs(Constant.OSType.IOS);
                    sb.append("IOS");
                    if (TencentStatusCode.Encryption.EXPRESS == device.getIdfaEnc()) {
                        request.setIfa(device.getIdfa());
                    }
                    if (device.hasOpenudid()) {
                        request.setDpid(device.getOpenudid());
                    }
                } else {
                    sb.append("ANDROID");
                    request.setOs(Constant.OSType.ANDROID);
                    if (device.hasImei()) {
                        request.setDidmd5(device.getImei());
                    }
                    if (device.hasAndroidid()) {
                        request.setDpidmd5(device.getAndroidid());
                    }
                }
            }

            MediaMappingMetaData mappingMetaData = CacheManager.getInstance().getMediaMapping(sb.toString());
            if (mappingMetaData != null) {
                request.setAdspacekey(mappingMetaData.getAdspaceKey());
            } else {
                return null;
            }

            mediaBid.setRequestBuilder(request);
            mediaBids.add(mediaBid);
        }

        return mediaBids;
    }

    private boolean isBanner(Impression impression) {
        for (Impression.MaterialFormat currentMaterialFormat : impression.getAdmRequireList()) {
            if (currentMaterialFormat.getMimes().contains("mp4") || currentMaterialFormat.getMimes().contains("flv")) {
                return false;
            }
        }
        return true;
    }

    /**
     * find max size material
     *
     * @param impression
     * @return
     */
    private Impression.MaterialFormat getMaxSizeMaterialFormat(Impression impression) {
        Impression.MaterialFormat maxSizeMaterialFormat = null;
        int maxMaterialSize = 0;
        for (Impression.MaterialFormat currentMaterialFormat : impression.getAdmRequireList()) {
            if (currentMaterialFormat.getMimes().contains("jpg") || currentMaterialFormat.getMimes().contains("png")) {
                int currentMaterialSize = currentMaterialFormat.getWidth() * currentMaterialFormat.getHeight();
                if (currentMaterialSize > maxMaterialSize) {
                    maxMaterialSize = currentMaterialSize;
                    maxSizeMaterialFormat = currentMaterialFormat;
                }
            }
        }
        return maxSizeMaterialFormat;
    }

    private int validateRequiredParam(Request bidRequest) {
        if (ObjectUtils.isNotEmpty(bidRequest)) {
            String id = bidRequest.getId();
            if (StringUtils.isEmpty(id)) {
                logger.warn("Tencent.bidRequest.id is missing");
                return Constant.StatusCode.BAD_REQUEST;
            }
            Impression impression = bidRequest.getImpression(0);
            if (ObjectUtils.isNotEmpty(impression)) {
                if (impression.hasBanner()) {
                    if (!impression.getBanner().hasWidth()) {
                        logger.warn("{}:Tencent.bidRequest.impression.Banner.W is missing", id);
                        return Constant.StatusCode.BAD_REQUEST;
                    }
                    if (!impression.getBanner().hasWidth()) {
                        logger.warn("{}:Tencent.bidRequest.impression.Video.H is missing", id);
                        return Constant.StatusCode.BAD_REQUEST;
                    }
                } else if (impression.hasVideo()) {
                    if (!impression.getVideo().hasWidth()) {
                        logger.warn("{}:Tencent.bidRequest.impression.Banner.W is missing", id);
                        return Constant.StatusCode.BAD_REQUEST;
                    }
                    if (!impression.getVideo().hasHeight()) {
                        logger.warn("{}:Tencent.bidRequest.impression.Video.H is missing", id);
                        return Constant.StatusCode.BAD_REQUEST;
                    }
                }
            } else {
                logger.warn("{}:Tencent.bidRequest.impression is missing", id);
                return Constant.StatusCode.BAD_REQUEST;
            }
            if (!bidRequest.hasDevice()) {
                logger.warn("{}:Tencent.bidRequest.Device is missing", id);
                return Constant.StatusCode.BAD_REQUEST;
            }
            if (!bidRequest.hasApp()) {
                logger.warn("{}:Tencent.bidRequest.App is missing", id);
                return Constant.StatusCode.BAD_REQUEST;
            }
            return Constant.StatusCode.OK;
        }
        return Constant.StatusCode.BAD_REQUEST;
    }

    @Override
    public boolean packageMediaResponse(MediaBidMetaData mediaBidMetaData, HttpServletResponse resp) {
        try {
            GPBForDSP.Response.Builder bidResponse = GPBForDSP.Response.newBuilder();
            if (mediaBidMetaData != null && !ObjectUtils.isEmpty(mediaBidMetaData.getMediaBids())) {
                MediaBid.Builder mediaBid = mediaBidMetaData.getMediaBids().get(0);
                if (mediaBid.hasResponseBuilder() && mediaBid.getStatus() == Constant.StatusCode.OK) {
                    bidResponse = convertToTencentResponse(mediaBidMetaData, mediaBid.getStatus());
                } else {
                    bidResponse = convertToTencentResponse(mediaBidMetaData, Constant.StatusCode.NO_CONTENT);
                }
            }
            outputStreamWrite(resp, bidResponse);
            return true;
        } catch (Exception e) {
            logger.error(e.toString() + "_Status_" + Constant.StatusCode.NO_CONTENT);
            resp.setStatus(Constant.StatusCode.NO_CONTENT);
            return false;
        }
    }

    private boolean outputStreamWrite(HttpServletResponse resp, GPBForDSP.Response.Builder bidResponse) {
        GPBForDSP.Response response = bidResponse.build();
        try {
            resp.setContentType("application/octet-stream;charset=UTF-8");
            resp.getOutputStream().write(response.toByteArray());
        } catch (Exception e) {
            logger.error(e.toString() + "_Status_" + Constant.StatusCode.NO_CONTENT);
            return false;
        }
        logger.info("Tencent outputStreamWrite is:{}", JsonFormat.printToString(response));
        return true;
    }

    private Response.Builder convertToTencentResponse(MediaBidMetaData mediaBidMetaData, int status) {
        MediaBid.Builder mediaBid = mediaBidMetaData.getMediaBids().get(0);
        GPBForDSP.Response.Builder responseBuiler = GPBForDSP.Response.newBuilder();
        GPBForDSP.Request bidRequest = (GPBForDSP.Request) mediaBidMetaData.getRequestObject();
        responseBuiler.setId(StringUtil.toString(bidRequest.getId()));
        MaterialMetaData materialMetaData = mediaBidMetaData.getBidMetaDataMap().get(mediaBid.getImpid()).getMaterialMetaData();
        if (Constant.StatusCode.OK == status && materialMetaData != null) {
            GPBForDSP.Response.SeatBid.Builder seatBuilder = GPBForDSP.Response.SeatBid.newBuilder();
            Builder mediaResponse = mediaBid.getResponseBuilder();
            responseBuiler.setId(StringUtil.toString(bidRequest.getId()));
            GPBForDSP.Response.Bid.Builder bidResponseBuilder = GPBForDSP.Response.Bid.newBuilder();
            bidResponseBuilder.setId(mediaBid.getImpid());
            bidResponseBuilder.setImpid(mediaBid.getRequestBuilder().getBid());
            bidResponseBuilder.setAdid(StringUtil.toString(materialMetaData.getMediaQueryKey()));
            //宏替换
            List<String> extList = mediaResponse.getMonitorBuilder().getExts();
            if (!ObjectUtils.isEmpty(extList)) {
                if (extList.size() >= 1) {
                    bidResponseBuilder.setExt(StringUtil.toString(extList.get(0)));
                }
                if (extList.size() >= 2) {
                    bidResponseBuilder.setExt2(StringUtil.toString(extList.get(1)));
                }
                if (extList.size() >= 3) {
                    bidResponseBuilder.setExt3(StringUtil.toString(extList.get(2)));
                }
            }
            //ssp自己的展示和点击监测:去掉域名
            List<Track> tracks = mediaResponse.getMonitorBuilder().getImpurl();
            if (!tracks.isEmpty()) {
                String impUrl = tracks.get(tracks.size() - 1).getUrl();
                bidResponseBuilder.addDispExts(impUrl.substring(impUrl.lastIndexOf("?") + 1, impUrl.length()));
            }

            List<String> clkUrls = mediaResponse.getMonitorBuilder().getClkurl();
            if (!clkUrls.isEmpty()) {
                String clkUrl = clkUrls.get(clkUrls.size() - 1).toString();
                bidResponseBuilder.addClickExts(clkUrl.substring(clkUrl.lastIndexOf("?") + 1, clkUrl.length()));
            }

            seatBuilder.addBid(bidResponseBuilder);//与request中的impression对应，可以对多个impression回复参与竞价，也可以对其中一部分回复参与竞价
            responseBuiler.addSeatbid(seatBuilder);

        }
        return responseBuiler;
    }

}
