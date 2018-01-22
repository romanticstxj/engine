package com.madhouse.media.oppo;


import com.alibaba.fastjson.JSON;
import com.madhouse.cache.CacheManager;
import com.madhouse.cache.MaterialMetaData;
import com.madhouse.cache.MediaBidMetaData;
import com.madhouse.cache.MediaMappingMetaData;
import com.madhouse.media.MediaBaseHandler;
import com.madhouse.media.oppo.OppoNativeRequest.Asset;
import com.madhouse.resource.ResourceManager;
import com.madhouse.ssp.Constant;
import com.madhouse.ssp.avro.MediaBid;
import com.madhouse.ssp.avro.MediaRequest;
import com.madhouse.ssp.avro.MediaResponse.Builder;
import com.madhouse.ssp.avro.Track;
import com.madhouse.util.HttpUtil;
import com.madhouse.util.ObjectUtils;
import com.madhouse.util.Utility;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class OppoHandler extends MediaBaseHandler {

    @Override
    public boolean parseMediaRequest(HttpServletRequest req, MediaBidMetaData mediaBidMetaData, HttpServletResponse resp) {
        try {
            req.setCharacterEncoding("UTF-8");
            String bytes = HttpUtil.getRequestPostBytes(req);
            OppoBidRequest oppoBidRequest = JSON.parseObject(bytes, OppoBidRequest.class);
            int status = validateRequiredParam(oppoBidRequest);
            if (status == Constant.StatusCode.OK) {
                mediaBidMetaData.setRequestObject(oppoBidRequest);
                MediaRequest.Builder mediaRequest = conversionToPremiumMADDataModel(oppoBidRequest);
                if (mediaRequest != null) {
                    MediaBid.Builder mediaBid = MediaBid.newBuilder();
                    mediaBid.setRequestBuilder(mediaRequest);
                    mediaBidMetaData.getMediaBids().add(mediaBid);
                    mediaBidMetaData.setRequestObject(oppoBidRequest);
                    return true;
                } else {
                    status = Constant.StatusCode.BAD_REQUEST;
                }
            }
            OppoResponse oppoBidResponse = convertToOppoResponse(mediaBidMetaData, status);
            outputStreamWrite(resp, oppoBidResponse);
            resp.setStatus(Constant.StatusCode.OK);
            return false;
        } catch (Exception e) {
            logger.error(e.toString() + "_Status_" + Constant.StatusCode.BAD_REQUEST);
            resp.setStatus(Constant.StatusCode.OK);
            return false;
        }
    }

    /**
     * 转换OppoNativeRequest的json结构数据，到OppoNativeRequest对象
     *
     * @return OppoNativeRequest
     */
    private OppoNativeRequest getRequestNative(String nativeStr) {
        OppoNativeRequest oppoNativeRequest = null;
        try {
            Map nativeLast = JSON.parseObject(nativeStr);
            for (Object obj : nativeLast.keySet()) {
                if (obj.equals("native")) {
                    String natives = nativeLast.get(obj).toString();
                    oppoNativeRequest = JSON.parseObject(natives, OppoNativeRequest.class);
                }
            }
        } catch (Exception e) {
            logger.error("json to OppoNativeRequest failed:", e);
        }
        return oppoNativeRequest;

    }


    private MediaRequest.Builder conversionToPremiumMADDataModel(OppoBidRequest oppoBidRequest) {
        MediaRequest.Builder mediaRequest = MediaRequest.newBuilder();

        OppoBidRequest.App app = oppoBidRequest.getApp();
        OppoBidRequest.Device device = oppoBidRequest.getDevice();
        OppoBidRequest.Imp imp = oppoBidRequest.getImp().get(0);
//        OppoBidRequest.Imp.Pmp pmp =oppoBidRequest.getImp().get(0).getPmp();

        // 广告请求唯一id
        mediaRequest.setAdtype(2);
        mediaRequest.setBid(oppoBidRequest.getId());
        mediaRequest.setName(app.getName());
        mediaRequest.setBundle(app.getBundle());
        mediaRequest.setBidfloor(Integer.parseInt(String.valueOf(imp.getBidfloor())));
        mediaRequest.setDevicetype(Constant.DeviceType.PHONE);
        mediaRequest.setType(Constant.MediaType.APP);

        StringBuilder sb = new StringBuilder();
        sb.append("OPPO:");
        //广告位id
        if (!StringUtils.isEmpty(imp.getTagid())) {
            sb.append(imp.getTagid());
        }
        // oppo没有ios，操作系统的类型
        mediaRequest.setDid(device.getDidmd5());
        mediaRequest.setOs(Constant.OSType.ANDROID);
        // OPPO bidRequest参数中没有运营商字段
        mediaRequest.setCarrier(Constant.Carrier.UNKNOWN);
        //0—未知，1—Ethernet，2—wifi，3—蜂窝网络，未知代，4—蜂窝网络，2G，5—蜂窝网络，3G，6—蜂窝网络，4G。
        switch (device.getConnectiontype()) {
            case OppoStatusCode.ConnectionType.WIFI:
                mediaRequest.setConnectiontype(Constant.ConnectionType.WIFI);
                break;
            case OppoStatusCode.ConnectionType._2G:
                mediaRequest.setConnectiontype(Constant.ConnectionType._2G);
                break;
            case OppoStatusCode.ConnectionType._3G:
                mediaRequest.setConnectiontype(Constant.ConnectionType._3G);
                break;
            case OppoStatusCode.ConnectionType._4G:
                mediaRequest.setConnectiontype(Constant.ConnectionType._4G);
                break;
            case OppoStatusCode.ConnectionType.Ethernet:
                mediaRequest.setConnectiontype(Constant.ConnectionType.ETHERNET);
                break;
            default:
                mediaRequest.setConnectiontype(Constant.ConnectionType.UNKNOWN);
                break;
        }

        mediaRequest.setIp(device.getIp());
        mediaRequest.setUa(device.getUa());
        mediaRequest.setOsv(device.getOsv());
        mediaRequest.setMake(device.getMake());
        mediaRequest.setModel(device.getModel());
        // native和pmp是却是，wordThread会设置宽高，pmp可以没有，所以验证方法中没有对native和pmp做验证，所以这里取值时要多一些判断。
        if (ObjectUtils.isNotEmpty(imp.getNatives()) && StringUtils.isNotEmpty(imp.getNatives().getRequest())) {
            OppoNativeRequest nativeRequest = getRequestNative(imp.getNatives().getRequest());
            if (ObjectUtils.isNotEmpty(nativeRequest.getAssets()) &&
                    ObjectUtils.isNotEmpty(nativeRequest.getAssets().get(0).getImg()) &&
                    nativeRequest.getAssets().get(0).getImg().getH() > 0 &&
                    nativeRequest.getAssets().get(0).getImg().getW() > 0
                    ) {
                mediaRequest.setH(nativeRequest.getAssets().get(0).getImg().getH());
                mediaRequest.setW(nativeRequest.getAssets().get(0).getImg().getW());
                imp.getNatives().setNativeObj(nativeRequest);
            }
        }
        if (ObjectUtils.isNotEmpty(imp.getPmp()) &&
                ObjectUtils.isNotEmpty(imp.getPmp().getDeals()) &&
                StringUtils.isNotEmpty(imp.getPmp().getDeals().get(0).getId())
                ) {
            List<OppoBidRequest.Imp.Pmp.Deal> delas = imp.getPmp().getDeals();
            // 如果有多个pmp对象，随机取一个返回
            mediaRequest.setDealid(delas.get(Utility.nextInt(delas.size())).getId());
            imp.setImpressionType(OppoStatusCode.ImpressionType.PMP);
        } else {
            // 没有pmp对象时，不管有没有native，设置为native
            imp.setImpressionType(OppoStatusCode.ImpressionType.NATIVE);
        }
        MediaMappingMetaData mappingMetaData = CacheManager.getInstance().getMediaMapping(sb.toString());
        if (mappingMetaData != null) {
            mediaRequest.setAdspacekey(mappingMetaData.getAdspaceKey());
        } else {
            return null;
        }
        logger.debug("OPPO convert mediaRequest is : {}", JSON.toJSONString(mediaRequest));
        return mediaRequest;
    }

    private int validateRequiredParam(OppoBidRequest oppoBidRequest) {
        if (ObjectUtils.isEmpty(oppoBidRequest)) {
            logger.warn("oppoBidRequest is null");
            return Constant.StatusCode.BAD_REQUEST;
        }
        if (StringUtils.isEmpty(oppoBidRequest.getId())) {
            logger.warn("oppoBidRequest.id is null");
            return Constant.StatusCode.BAD_REQUEST;
        }
        if (ObjectUtils.isEmpty(oppoBidRequest.getImp())) {
            logger.warn("oppoBidRequest.Imp is null");
            return Constant.StatusCode.BAD_REQUEST;
        }
        if (ObjectUtils.isEmpty(oppoBidRequest.getDevice())) {
            logger.warn("oppoBidRequest.Device is null");
            return Constant.StatusCode.BAD_REQUEST;
        }
        if (ObjectUtils.isEmpty(oppoBidRequest.getApp())) {
            logger.warn("oppoBidRequest.App is null");
            return Constant.StatusCode.BAD_REQUEST;
        }
        if (StringUtils.isEmpty(oppoBidRequest.getApp().getBundle())) {
            logger.warn("oppoBidRequest.App.Bundle is null");
            return Constant.StatusCode.BAD_REQUEST;
        }
        if (StringUtils.isEmpty(oppoBidRequest.getApp().getName())) {
            logger.warn("oppoBidRequest.App.Name is null");
            return Constant.StatusCode.BAD_REQUEST;
        }
        if (ObjectUtils.isEmpty(oppoBidRequest.getDevice().getConnectiontype())) {
            logger.warn("oppoBidRequest.Device.Connectiontype is null");
            return Constant.StatusCode.BAD_REQUEST;
        }
        if (ObjectUtils.isEmpty(oppoBidRequest.getDevice().getDevicetype())) {
            logger.warn("oppoBidRequest.Device.devicetype is null");
            return Constant.StatusCode.BAD_REQUEST;
        }
        if (StringUtils.isEmpty(oppoBidRequest.getDevice().getUa())) {
            logger.warn("oppoBidRequest.Device.ua is null");
            return Constant.StatusCode.BAD_REQUEST;
        }
        if (StringUtils.isEmpty(oppoBidRequest.getDevice().getIp())) {
            logger.warn("oppoBidRequest.Device.ip is null");
            return Constant.StatusCode.BAD_REQUEST;
        }
        if (StringUtils.isEmpty(oppoBidRequest.getDevice().getOsv())) {
            logger.warn("oppoBidRequest.Device.osv is null");
            return Constant.StatusCode.BAD_REQUEST;
        }
        if (StringUtils.isEmpty(oppoBidRequest.getDevice().getOs())) {
            logger.warn("oppoBidRequest.Device.os is null");
            return Constant.StatusCode.BAD_REQUEST;
        }
        if (StringUtils.isEmpty(oppoBidRequest.getDevice().getDidmd5())) {
            logger.warn("oppoBidRequest.Device.didmd5 is null");
            return Constant.StatusCode.BAD_REQUEST;
        }
        if (!OppoStatusCode.Os.OS_ANDROID.equalsIgnoreCase(oppoBidRequest.getDevice().getOs())) {
            logger.warn("oppoBidRequest.Device.os is {},not is android", oppoBidRequest.getDevice().getOs() == null ? "null" : oppoBidRequest.getDevice().getOs());
            return Constant.StatusCode.BAD_REQUEST;
        }
        if (oppoBidRequest.getImp().get(0).getBidfloor() <= 0) {
            logger.warn("oppoBidRequest.Imp[0].Bidfloor <= 0");
            return Constant.StatusCode.BAD_REQUEST;
        }
        if (StringUtils.isEmpty(oppoBidRequest.getImp().get(0).getTagid())) {
            logger.warn("oppoBidRequest.Imp[0].tagId is null");
            return Constant.StatusCode.BAD_REQUEST;
        }
        return Constant.StatusCode.OK;
    }

    @Override
    public boolean packageMediaResponse(MediaBidMetaData mediaBidMetaData, HttpServletResponse resp) {
        try {
            if (mediaBidMetaData != null && mediaBidMetaData.getMediaBids() != null && mediaBidMetaData.getMediaBids().size() > 0) {
                MediaBid.Builder mediaBid = mediaBidMetaData.getMediaBids().get(0);
                OppoResponse result;
                if (mediaBid.hasResponseBuilder() && mediaBid.getStatus() == Constant.StatusCode.OK) {
                    result = convertToOppoResponse(mediaBidMetaData, mediaBid.getStatus());
                } else {
                    result = convertToOppoResponse(mediaBidMetaData, Constant.StatusCode.NO_CONTENT);
                }
                outputStreamWrite(resp, result);
            }
        } catch (Exception e) {
            logger.error(e.toString() + "_Status_" + Constant.StatusCode.BAD_REQUEST);
            resp.setStatus(Constant.StatusCode.BAD_REQUEST);
            return false;
        }
        resp.setStatus(Constant.StatusCode.OK);
        return false;
    }


    private void outputStreamWrite(HttpServletResponse resp, OppoResponse oppoResponse) {
        try {
            if (oppoResponse != null) {
                resp.setHeader("Content-Type", "application/json; charset=utf-8");
                resp.getOutputStream().write(JSON.toJSONString(oppoResponse).getBytes("utf-8"));
                resp.setStatus(Constant.StatusCode.OK);
                logger.warn("_Status_" + Constant.StatusCode.OK);
            }
        } catch (Exception e) {
            logger.error(e.toString() + "_Status_" + Constant.StatusCode.NO_CONTENT);
            return;
        }
        logger.debug("OPPO outputStreamWrite is:{}", JSON.toJSONString(oppoResponse));
    }

    private OppoResponse convertToOppoResponse(MediaBidMetaData mediaBidMetaData, int status) {
        OppoBidRequest oppoBidRequest = (OppoBidRequest) mediaBidMetaData.getRequestObject();
        //response DSP对象
        MediaBid.Builder mediaBid = mediaBidMetaData.getMediaBids().get(0);
        Builder mediaResponse = mediaBid.getResponseBuilder();
        OppoResponse response = new OppoResponse();
        List<OppoResponse.SeatBid.Bid> bids = new ArrayList<>(1);
        List<OppoResponse.SeatBid> seatbids = new ArrayList<>(1);
        OppoResponse.SeatBid seatBid = response.new SeatBid();
        OppoResponse.SeatBid.Bid bid = seatBid.new Bid();
        //Oppo Bidresponse

        if (Constant.StatusCode.OK != status) {
            if (status == Constant.StatusCode.NO_CONTENT) {
                response.setNbr(1002);
            } else {
                response.setNbr(1001);
            }
            response.setId(oppoBidRequest.getId());//竞价请求id
            response.setBidid(oppoBidRequest.getImp().get(0).getId());//竞价者生成的id唯一标识
        } else {
            //request请求对象
            //判断native模式，还是pmp模式曝光
            if (oppoBidRequest.getImp().get(0).getImpressionType() == OppoStatusCode.ImpressionType.NATIVE) {
                OppoNativeRequest oppoNativeRequest = getRequestNative(oppoBidRequest.getImp().get(0).getNatives().getRequest());
                OppoNativeResponse oppoNativeResponse = new OppoNativeResponse();
                OppoNativeResponse.AdmNative admNative = oppoNativeResponse.new AdmNative();
                oppoNativeResponse.setAdmNative(admNative);
                if (null != oppoNativeRequest && null != oppoNativeRequest.getAssets() && oppoNativeRequest.getAssets().size() > 0) {
                    List<com.madhouse.media.oppo.OppoNativeResponse.AdmNative.Asset> assetNativeResponseList = new ArrayList<>();
                    // 用来临时存储封装img对象的asset，用来判断specificFeeds.FormateType
                    OppoNativeResponse.AdmNative.Asset.Img imgAssetForFormateType = null;
                    for (Asset assetNativeRequest : oppoNativeRequest.getAssets()) {
                        MaterialMetaData materialMetaData = mediaBidMetaData.getBidMetaDataMap().get(mediaBid.getImpid()).getMaterialMetaData();
                        OppoNativeResponse.AdmNative.Asset assetResponse = admNative.new Asset();
                        assetResponse.setId(assetNativeRequest.getId());
                        assetResponse.setRequired(assetNativeRequest.getRequired());
                        if (null != assetNativeRequest.getTitle()) {
                            OppoNativeResponse.AdmNative.Asset.Title titleResponse = assetResponse.new Title();
                            titleResponse.setText(materialMetaData.getTitle());
                            assetResponse.setTitle(titleResponse);
                        }
                        if (null != assetNativeRequest.getImg()) {
                            OppoNativeResponse.AdmNative.Asset.Img imgResponse = assetResponse.new Img();
                            imgResponse.setH(materialMetaData.getH());
                            imgResponse.setW(materialMetaData.getW());
                            imgResponse.setUrl(mediaResponse.getAdm().get(0));//物料url
                            assetResponse.setImg(imgResponse);
                            imgAssetForFormateType = imgResponse;
                        }
                        if (null != assetNativeRequest.getData()) {
                            OppoNativeResponse.AdmNative.Asset.Data dataResponse = assetResponse.new Data();
                            dataResponse.setValue(mediaResponse.getBrand());//指定类型的数据内容
                            assetResponse.setData(dataResponse);
                        }
                        if (null != assetNativeRequest.getSpecificFeeds()) {
                            OppoNativeResponse.AdmNative.Asset.SpecificFeeds specificFeeds = assetResponse.new SpecificFeeds();
                            if (imgAssetForFormateType!=null && imgAssetForFormateType.getW() * imgAssetForFormateType.getH() == 640 * 320 && null != mediaResponse.getAdm() && mediaResponse.getAdm().size() == 1) {
                                specificFeeds.setFormateType(1);//信息流大图
                            } else if (imgAssetForFormateType!=null && imgAssetForFormateType.getW() * imgAssetForFormateType.getH() == 320 * 210 && null != mediaResponse.getAdm() && mediaResponse.getAdm().size() == 1) {
                                specificFeeds.setFormateType(2);//信息流小图
                            } else if (imgAssetForFormateType!=null && imgAssetForFormateType.getW() * imgAssetForFormateType.getH() == 320 * 210 && null != mediaResponse.getAdm() && mediaResponse.getAdm().size() == 3) {
                                specificFeeds.setFormateType(3);//信息流多图
                            }
                            if (null != mediaResponse.getAdm()) {
                                specificFeeds.setImageUrls(mediaResponse.getAdm());
                            }
                            assetResponse.setSpecificFeeds(specificFeeds);
                        }
                        assetNativeResponseList.add(assetResponse);
                    }
                    admNative.setAssets(assetNativeResponseList);
                    //Link 对象:落地页和点击监测
                    OppoNativeResponse.AdmNative.Link linkResponse = admNative.new Link();
                    linkResponse.setUrl(mediaResponse.getLpgurl());
                    linkResponse.setClicktrackers(mediaResponse.getMonitorBuilder().getClkurl());
                    admNative.setLink(linkResponse);
                    //展示监测
                    List<String> imptrackers = new ArrayList<String>();
                    for (Track track : mediaResponse.getMonitorBuilder().getImpurl()) {
                        imptrackers.add(track.getUrl());
                    }
                    admNative.setImptrackers(imptrackers);
                    admNative.setVer("1.1");
                }
                bid.setAdm(JSON.toJSONString(oppoNativeResponse).toString());

            } else if (oppoBidRequest.getImp().get(0).getImpressionType() == OppoStatusCode.ImpressionType.PMP) {
                if (null != oppoBidRequest.getImp().get(0).getPmp().getDeals() && oppoBidRequest.getImp().get(0).getPmp().getDeals().size() > 0) {
                    bid.setDealid(oppoBidRequest.getImp().get(0).getPmp().getDeals().get(0).getId());
                    //设置点击和展示监测:如果asset对象中有，以asset为主，如果没有，则以bid对象中为主
                    bid.setClicktrackers(mediaResponse.getMonitorBuilder().getClkurl());
                    List<String> imptrackers = new ArrayList<String>();
                    for (Track track : mediaResponse.getMonitorBuilder().getImpurl()) {
                        imptrackers.add(track.getUrl());
                    }
                    bid.setImptrackers(imptrackers);
                    MaterialMetaData materialMetaData = mediaBidMetaData.getBidMetaDataMap().get(mediaBid.getImpid()).getMaterialMetaData();
                    bid.setCrid(Long.toString(materialMetaData.getId()));
                }
            }


            //seatBid中的bid对象
            bid.setId(mediaBid.getImpid());
            bid.setImpid(oppoBidRequest.getImp().get(0).getId());
            bid.setPrice(mediaResponse.getPrice());
            bid.setAdid(mediaResponse.getCid());//预加载的广告id(dsp广告活动id)

            //设置List值，组装到response中
            bids.add(bid);
            seatBid.setBid(bids);
            seatbids.add(seatBid);
            response.setSeatbid(seatbids);

            response.setId(oppoBidRequest.getId());//竞价请求id
            response.setBidid(ResourceManager.getInstance().nextId());//竞价者生成的id唯一标识
        }
        logger.debug("OPPO Response params is : {}", JSON.toJSONString(response));
        return response;
    }

}
