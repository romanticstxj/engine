package com.madhouse.media.baofeng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.madhouse.rtb.PremiumMADRTBProtocol;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.madhouse.cache.CacheManager;
import com.madhouse.cache.MediaBidMetaData;
import com.madhouse.cache.PlcmtMetaData;
import com.madhouse.media.MediaBaseHandler;
import com.madhouse.media.baofeng.BaoFengResponse.PV;
import com.madhouse.ssp.Constant;
import com.madhouse.ssp.avro.*;
import com.madhouse.util.HttpUtil;
import com.madhouse.util.ObjectUtils;

public class BaoFengHandler extends MediaBaseHandler {
    // TODO
    @Override
    public boolean parseMediaRequest(HttpServletRequest req, MediaBidMetaData mediaBidMetaData, HttpServletResponse resp) {
        resp.setStatus(Constant.StatusCode.OK);
        boolean isSandbox = false;

        String env = req.getHeader("env");
        if (env != null && env.equals("sandbox")) {
            isSandbox = true;
        }

        try {
            req.setCharacterEncoding("UTF-8");
            String bytes = HttpUtil.getRequestPostBytes(req);
            BaoFengBidRequest baoFengBidRequest = JSON.parseObject(bytes, BaoFengBidRequest.class);
            logger.info("BaoFeng Request params is : {}",JSON.toJSONString(baoFengBidRequest));
            int status = validateRequiredParam(baoFengBidRequest, resp);
            if (status != Constant.StatusCode.OK) {
                resp.setStatus(status);
                return false;
            } else {
                MediaRequest mediaRequest = conversionToPremiumMADDataModel(isSandbox, baoFengBidRequest);
                mediaBidMetaData.getMediaBidBuilder().setRequest(mediaRequest);
                mediaBidMetaData.setRequestObject(baoFengBidRequest);
                if (StringUtils.isEmpty(mediaRequest.getAdspacekey())) {
                    resp.setStatus(Constant.StatusCode.BAD_REQUEST);
                    logger.debug(Constant.StatusCode.BAD_REQUEST);
                    return false;
                }
            }
        } catch (Exception e) {
            logger.error(e.toString() + "_Status_" + Constant.StatusCode.BAD_REQUEST);
            resp.setStatus(Constant.StatusCode.BAD_REQUEST);
            return false;
        }

        return true;
    }
    // TODO
    @Override
    public boolean packageMediaResponse(MediaBidMetaData mediaBidMetaData, HttpServletResponse resp) {

        if (mediaBidMetaData != null && mediaBidMetaData.getMediaBidBuilder() != null) {
            MediaBid.Builder mediaBid = mediaBidMetaData.getMediaBidBuilder();
            if (mediaBid.getResponseBuilder() != null && mediaBid.getStatus() == Constant.StatusCode.OK) {
                try {
                    BaoFengResponse baoFengResponse = convertToBaofengResponse(mediaBidMetaData);
                    if (baoFengResponse != null) {
                        resp.getOutputStream().write(JSON.toJSONString(baoFengResponse).getBytes("utf-8"));
                        resp.setStatus(Constant.StatusCode.OK);
                        logger.debug("_Status_" + Constant.StatusCode.OK);
                        return true;
                    }
                } catch (Exception e) {
                    logger.error(e.toString() + "_Status_" + Constant.StatusCode.NO_CONTENT);
                    resp.setStatus(Constant.StatusCode.NO_CONTENT);
                    return false;
                }
            }
        }
        resp.setStatus(Constant.StatusCode.NO_CONTENT);
        return true;
    }
    
    private BaoFengResponse convertToBaofengResponse(MediaBidMetaData mediaBidMetaData) {
        BaoFengResponse baoFengResponse = new BaoFengResponse();
        
        // 广告高度
        Integer adheight = mediaBidMetaData.getMediaBidBuilder().getRequest().getH();
        
        // 广告位宽度
        Integer adwidth = mediaBidMetaData.getMediaBidBuilder().getRequest().getW();
        
        // 广告流水唯一标识
        String bid = mediaBidMetaData.getMediaBidBuilder().getRequest().getBid();

        MediaResponse.Builder mediaResponse = mediaBidMetaData.getMediaBidBuilder().getResponseBuilder();
        
        // 点击url
        baoFengResponse.setTarget(mediaResponse.getLpgurl());
        
        String imgurl = null;
        if (mediaResponse.hasTitle()) {
            baoFengResponse.setTitle(mediaResponse.getTitle());
        }

        if (mediaResponse.hasDesc()) {
            baoFengResponse.setDesc(mediaResponse.getDesc());
        }

        if (mediaResponse.getAdm() != null && !mediaResponse.getAdm().isEmpty()) {
            imgurl = mediaResponse.getAdm().get(0);
        }

        // 展示监播
        List<String> imgtracking = new LinkedList<>();
        for (Track track : mediaResponse.getMonitor().getImpurl()) {
            imgtracking.add(track.getUrl());
        }

        // 点击监播
        List<String> thclkurl = mediaResponse.getMonitor().getClkurl();
        // 暴风没有区分第三方点击和
        ArrayList<BaoFengResponse.Img> imgList = new ArrayList<>();
        BaoFengResponse.Img img = baoFengResponse.new Img();
        img.setSrc(imgurl);
        img.setW(adwidth == null ? 0 : adwidth);
        img.setH(adheight == null ? 0 : adheight);
        imgList.add(img);
        
        baoFengResponse.setId(bid);
        // 目前我们没有点击下载的广告。我们也没有这个逻辑来区分普通广告和点击下载广告
        baoFengResponse.setAd_type(0);
        List<BaoFengResponse.PV> clkPvList = new ArrayList<>();
        // 处理MMA
        handleMMA(baoFengResponse, thclkurl, clkPvList);
        baoFengResponse.setClick(clkPvList);
        
        baoFengResponse.setImg(imgList.size() > 0 ? imgList.get(0) : null);
        
        List<BaoFengResponse.PV> trackPVList = new ArrayList<>();
        // 处理MMA
        handleMMA(baoFengResponse, imgtracking, trackPVList);
        baoFengResponse.setPv(trackPVList);
        ;
        if (StringUtils.isEmpty(baoFengResponse.getTarget()) || StringUtils.isEmpty(baoFengResponse.getImg().getSrc()) || ObjectUtils.isEmpty(baoFengResponse.getClick())
            || ObjectUtils.isEmpty(baoFengResponse.getPv()))
            return null;
        logger.info("BaoFeng Response params is : {}", JSON.toJSONString(baoFengResponse));
        return baoFengResponse;
        
    }
    
    private void handleMMA(BaoFengResponse baoFengResponse, List<String> thclkurl, List<PV> clkPvList) {
        for (String url : thclkurl) {
            if (url.equals("__IDFA__") || url.equals("__IMEI__")) {
                clkPvList.add(baoFengResponse.new PV(1, url));
            }
            else {
                clkPvList.add(baoFengResponse.new PV(0, url));
            }
        }
        
    }
    
    private int validateRequiredParam(BaoFengBidRequest baoFengBidRequest, HttpServletResponse resp) {
        if (ObjectUtils.isNotEmpty(baoFengBidRequest)) {
            String id = baoFengBidRequest.getId();
            if (StringUtils.isNotEmpty(id)) {
                // 验证app
                BaoFengBidRequest.App app = baoFengBidRequest.getApp();
                if (ObjectUtils.isEmpty(app)) {
                    logger.debug("{}:app is null",id);
                    return Constant.StatusCode.BAD_REQUEST;
                }

                if (StringUtils.isEmpty(app.getId())) {
                    logger.debug("{}:app or appid is null",id);
                    return Constant.StatusCode.BAD_REQUEST;
                }
                
                // 验证Impression对象
                BaoFengBidRequest.Impression imp = baoFengBidRequest.getImp();
                if (ObjectUtils.isEmpty(imp)) {
                    logger.debug("{}:imp or impid,H,W is null",id);
                    return Constant.StatusCode.BAD_REQUEST;
                }
                if (StringUtils.isEmpty(imp.getId())) {
                    logger.debug("{}:imp or impid is null",id);
                    return Constant.StatusCode.BAD_REQUEST;
                }
                if (0 == imp.getW()) {
                    logger.debug("{}:imp or W is null",id);
                    return Constant.StatusCode.BAD_REQUEST;
                }
                if (0 == imp.getH()) {
                    logger.debug("{}:imp or H is null",id);
                    return Constant.StatusCode.BAD_REQUEST;
                }
                
                // 验证Device对象
                BaoFengBidRequest.Device device = baoFengBidRequest.getDevice();
                if (ObjectUtils.isEmpty(device)) {
                    logger.debug("{},device or deviceid,dpid is null",id);
                    return Constant.StatusCode.BAD_REQUEST;
                }
                if (StringUtils.isEmpty(device.getId())) {
                    logger.debug("{},device or deviceid is null",id);
                    return Constant.StatusCode.BAD_REQUEST;
                }
                if (StringUtils.isEmpty(device.getDpid())) {
                    logger.debug("{},device or dpid is null",id);
                    return Constant.StatusCode.BAD_REQUEST;
                }
                return Constant.StatusCode.OK;
            }
            logger.debug("baoFengBidRequest.id is null");
        }
        return Constant.StatusCode.BAD_REQUEST;
    }
    
    private MediaRequest conversionToPremiumMADDataModel(boolean isSandbox, BaoFengBidRequest baoFengBidRequest) {
        MediaRequest.Builder mediaRequest = MediaRequest.newBuilder();
        
        BaoFengBidRequest.App app = baoFengBidRequest.getApp();
        BaoFengBidRequest.Device device = baoFengBidRequest.getDevice();
        BaoFengBidRequest.Impression imp = baoFengBidRequest.getImp();
        
        // 操作系统的类型
        String os = device.getOs();
        if ("ios".equalsIgnoreCase(os)) {
            mediaRequest.setOs(Constant.OSType.IOS);
        } else if ("android".equalsIgnoreCase(os)) {
            mediaRequest.setOs(Constant.OSType.ANDROID);
        } else {
            // 1 iphone 2 ipad 3 android
            switch (device.getDevicetype()) {
                case 1:
                    mediaRequest.setOs(Constant.OSType.IOS);
                    break;
                case 2:
                    mediaRequest.setOs(Constant.OSType.IOS);
                    break;
                case 3:
                    mediaRequest.setOs(Constant.OSType.ANDROID);
                    break;
                default:
                    mediaRequest.setOs(Constant.OSType.UNKNOWN);
                    break;
            }
        }

        switch (mediaRequest.getOs()) {
            case Constant.OSType.IOS: {
                mediaRequest.setIfa(device.getDpid());
                break;
            }

            case Constant.OSType.ANDROID: {
                mediaRequest.setDid(device.getDpid());
                break;
            }

            default:
                break;
        }

        // 广告请求唯一id
        mediaRequest.setBid(baoFengBidRequest.getId());
        // 广告类型id 暴风所有的adtype都对应我们的Banner广告类型
        // 广告类型 4=频道页banner; 5=详情页banner; 6=焦点图 7=开屏
        // 通过pos和os/deviceType设置标准的宽和高 由于os和diviceType都是可选的，所以做了这样的判断
        setWidthAndHeight(mediaRequest, device.getDevicetype() != 0 ? device.getDevicetype() : osToDeviceType(mediaRequest), imp.getPos());
        // 这里2对应exchange的adtype 1=文字链 2=Banner 3=图形文字链 4=全屏 5=插页 6=开屏  
        mediaRequest.setAdtype(2);
        // app名称，暴风没有提供app包名
        mediaRequest.setBundle(app.getName());
        mediaRequest.setName(app.getName());
        // 设备类型，1=iPhone；2=iPad；3=android
        switch (device.getDevicetype()){
            case 1:
                mediaRequest.setDevicetype(Constant.DeviceType.PHONE);
                break;
            case 2:
                mediaRequest.setDevicetype(Constant.DeviceType.PAD);
                break;
            case 3:
                mediaRequest.setDevicetype(Constant.DeviceType.UNKNOWN);
                break;
            default:
                mediaRequest.setDevicetype(Constant.DeviceType.UNKNOWN);
                break;
        }
        
        // 广告位ID
        String adspaceKey = null;
        if (isSandbox) {
            // sandbox环境
            adspaceKey = "sandbox:BF:" + mediaRequest.getW() + ":" + mediaRequest.getH();
            //模拟竞价，不计费
            mediaRequest.setTest(Constant.Test.SIMULATION);
        } else {
            adspaceKey = "BF:" + mediaRequest.getW() + ":" + mediaRequest.getH();
            mediaRequest.setTest(Constant.Test.REAL);
        }
        
        if (adspaceKey != null) {
            PlcmtMetaData plcmtMetaData = CacheManager.getInstance().getPlcmtMetaData(adspaceKey);
            if (plcmtMetaData != null) {
                mediaRequest.setAdspacekey(plcmtMetaData.getAdspaceKey());
            }
        }

        // 连接方式 0：unknow 1：wifi 2：2G/3G/4G
        switch (device.getConnectiontype()) {
            case 0: {
                mediaRequest.setConnectiontype(Constant.ConnectionType.UNKNOWN);
                break;
            }

            case 1: {
                mediaRequest.setConnectiontype(Constant.ConnectionType.WIFI);
                break;
            }

            case 2: {
                mediaRequest.setConnectiontype(Constant.ConnectionType.CELL);
                break;
            }

            default:
                mediaRequest.setConnectiontype(Constant.ConnectionType.UNKNOWN);
                break;
        }

        // 运行商 移动46000；联通46001；电信46003
        String carrier = device.getCarrier() != null ? device.getCarrier() : "";
        switch (carrier) {
            case "46000":
                mediaRequest.setCarrier(Constant.Carrier.CHINA_MOBILE);
                break;
            case "46001":
                mediaRequest.setCarrier(Constant.Carrier.CHINA_UNICOM);
                break;
            case "46003":
                mediaRequest.setCarrier(Constant.Carrier.CHINA_TELECOM);
                break;
            default:
                mediaRequest.setCarrier(Constant.Carrier.UNKNOWN);
                break;
        }
        
        

        // 设备浏览器的User-Agent字符串
        if (!StringUtils.isEmpty(device.getUa())) {
            mediaRequest.setUa(device.getUa());
        }

        // ip地址
        if (!StringUtils.isEmpty(device.getIp())) {
            mediaRequest.setIp(device.getIp());
        }
        
        // 操作系统的版本
        mediaRequest.setOsv(device.getOsv() != null ? device.getOsv() : "");
        // 设备型号
        mediaRequest.setModel(device.getModel() != null ? device.getModel() : "");
        // mac地址
        mediaRequest.setMac(device.getMac() != null ? device.getMac() : "");
        mediaRequest.setType(Constant.MediaType.APP);
        logger.info("BaoFengrequest convert mediaRequest is : {}", JSON.toJSONString(mediaRequest));
        return mediaRequest.build();
        
    }
    
    private void setWidthAndHeight(MediaRequest.Builder mediaRequest, int deviceType, int pos) {
        // 开屏（7） 焦点图（6） 频道页banner（4） 详情页banner（5）
        // android 720*1280 720*264 230*130 480*110
        // iphone 640*1136 699*263 592*110
        switch (deviceType) {
        // android
            case 3:
                switch (pos) {
                    case 7:
                        mediaRequest.setW(720);
                        mediaRequest.setH(1280);
                        break;
                    case 6:
                        mediaRequest.setW(720);
                        mediaRequest.setH(265);
                        break;
                    case 4:
                        mediaRequest.setW(230);
                        mediaRequest.setH(130);
                        break;
                    case 5:
                        mediaRequest.setW(480);
                        mediaRequest.setH(110);
                        break;
                    default:
                        setDefaultWH(mediaRequest);
                        break;
                }
                break;
            // iphone
            case 1:
                switch (pos) {
                    case 7:
                        mediaRequest.setW(640);
                        mediaRequest.setH(1136);
                        break;
                    case 6:
                        mediaRequest.setW(699);
                        mediaRequest.setH(263);
                        break;
                    case 5:
                        mediaRequest.setW(592);
                        mediaRequest.setH(110);
                        break;
                    default:
                        setDefaultWH(mediaRequest);
                        break;
                }
                break;
            // ipad
            case 2:
                switch (pos) {
                    case 7:
                        mediaRequest.setW(2048);
                        mediaRequest.setH(1536);
                        break;
                    case 6:
                        mediaRequest.setW(2048);
                        mediaRequest.setH(504);
                        break;
                    default:
                        setDefaultWH(mediaRequest);
                        break;
                }
                break;
            default:
                setDefaultWH(mediaRequest);
                break;
        }

        
    }
    private void setDefaultWH(MediaRequest.Builder mediaRequest) {
        mediaRequest.setW(0);
        mediaRequest.setH(0);
    }
    
    private int osToDeviceType(MediaRequest.Builder mediaRequest) {
        if (Constant.OSType.ANDROID == mediaRequest.getOs()) {
            // android系统对应的deviceType是3
            return 3;
        } else {
            // ios系统对应的deviceType是1（这里默认使用1表示iphone，ios系统细分了iphone和ipad）
            return 1;
        }
    }
    
}
