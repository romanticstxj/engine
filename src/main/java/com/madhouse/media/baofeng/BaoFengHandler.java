package com.madhouse.media.baofeng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.madhouse.cache.CacheManager;
import com.madhouse.cache.MediaBidMetaData;
import com.madhouse.cache.PlcmtMetaData;
import com.madhouse.media.MediaBaseHandler;
import com.madhouse.media.baofeng.BaoFengResponse.PV;
import com.madhouse.ssp.Constant;
import com.madhouse.ssp.PremiumMADDataModel;
import com.madhouse.util.ObjectUtils;
import com.madhouse.util.StringUtil;

public class BaoFengHandler extends MediaBaseHandler {
    
    @Override
    public boolean parseMediaRequest(HttpServletRequest req, MediaBidMetaData mediaBidMetaData, HttpServletResponse resp) {
        resp.setStatus(Constant.StatusCode.OK);
        PremiumMADDataModel.MediaBid.MediaRequest premiumMADDataModel;
        boolean isSandbox = false;
        String env = req.getHeader("env");
        if (env != null && env.equals("sandbox")) {
            isSandbox = true;
        }
        try {
            req.setCharacterEncoding("UTF-8");
            String bytes = getRequestPostBytes(req);
            BaoFengBidRequest parseObject = JSON.parseObject(bytes, BaoFengBidRequest.class);
            BaoFengBidRequest BaoFengBidRequest = validateReuiredParam(parseObject, resp);
            if (resp.getStatus() != Constant.StatusCode.OK) {
                return false;
            }
            else {
                premiumMADDataModel = conversionToPremiumMADDataModel(isSandbox, BaoFengBidRequest);
                mediaBidMetaData.getMediaBidBuilder().setRequest(premiumMADDataModel);
                mediaBidMetaData.setRequestObject(parseObject);
                if (StringUtil.isEmpty(premiumMADDataModel.getAdspacekey())) {
                    resp.setStatus(Constant.StatusCode.BAD_REQUEST);
                    return false;
                }
            }
            
        }
        catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(Constant.StatusCode.BAD_REQUEST);
            return false;
        }

        // TODO 自动生成的方法存根
        return true;
    }

    @Override
    public boolean packageMediaResponse(MediaBidMetaData mediaBidMetaData, HttpServletResponse resp) {

        if (mediaBidMetaData != null && mediaBidMetaData.getMediaBidBuilder() != null) {
            PremiumMADDataModel.MediaBid.Builder mediaBid = mediaBidMetaData.getMediaBidBuilder();
            if (mediaBid.getResponseBuilder() != null && mediaBid.getStatus() == Constant.StatusCode.OK) {
                try {
                    BaoFengResponse baoFengResponse = conversionToBaoFengResponse(mediaBidMetaData);
                    if (baoFengResponse != null) {
                        resp.getOutputStream().write(JSON.toJSONString(baoFengResponse).getBytes("utf-8"));
                        resp.setStatus(Constant.StatusCode.OK);
                        return true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    resp.setStatus(Constant.StatusCode.NO_CONTENT);
                    return false;
                }
            }
        }

        resp.setStatus(Constant.StatusCode.NO_CONTENT);
        return true;
    }
    
    private BaoFengResponse conversionToBaoFengResponse(MediaBidMetaData mediaBidMetaData) {
        BaoFengResponse baoFengResponse = new BaoFengResponse();
        
        // 广告高度
        Integer adheight = mediaBidMetaData.getMediaBidBuilder().getRequest().getH();
        
        // 广告位宽度
        Integer adwidth = mediaBidMetaData.getMediaBidBuilder().getRequest().getH();
        
        // 广告流水唯一标识
        String bid = mediaBidMetaData.getMediaBidBuilder().getRequest().getBid();

        PremiumMADDataModel.MediaBid.MediaResponse.Builder mediaResponse = mediaBidMetaData.getMediaBidBuilder().getResponseBuilder();
        
        // 点击url
        baoFengResponse.setTarget(mediaResponse.getLpgurl());
        
        String imgurl = null;
        if (mediaResponse.hasTitle()) {
            baoFengResponse.setTitle(mediaResponse.getTitle());
        }

        if (mediaResponse.hasDesc()) {
            baoFengResponse.setDesc(mediaResponse.getDesc());
        }

        if (mediaResponse.getAdmCount() > 0) {
            imgurl = mediaResponse.getAdm(0);
        }

        // 展示监播
        List<String> imgtracking = new LinkedList<>();
        for (PremiumMADDataModel.MediaBid.MediaResponse.Monitor.Track track : mediaResponse.getMonitor().getImpurlList()) {
            imgtracking.add(track.getUrl());
        }

        // 点击监播
        List<String> thclkurl = mediaResponse.getMonitor().getClkurlList();
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
        // logger.debug("BaoFeng request params is : {}",
        // JSON.toJSONString(dto));
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
    
    public static String getRequestPostBytes(HttpServletRequest request)
        throws IOException {
        int contentLength = request.getContentLength();
        if (contentLength < 0) {
            return null;
        }
        byte buffer[] = new byte[contentLength];
        for (int i = 0; i < contentLength;) {
            
            int readlen = request.getInputStream().read(buffer, i, contentLength - i);
            if (readlen == -1) {
                break;
            }
            i += readlen;
        }
        return new String(buffer);
    }
    
    private BaoFengBidRequest validateReuiredParam(BaoFengBidRequest baoFengBidRequest, HttpServletResponse resp) {
        if (ObjectUtils.isNotEmpty(baoFengBidRequest)) {
            if (StringUtils.isNotEmpty(baoFengBidRequest.getId())) {
                
                // 验证app
                BaoFengBidRequest.App app = baoFengBidRequest.getApp();
                
                if (ObjectUtils.isEmpty(app)) {
                    resp.setStatus(Constant.StatusCode.BAD_REQUEST);
                    return baoFengBidRequest;
                }
                if (StringUtils.isEmpty(app.getId())) {
                    // *logger.debug("app or appid is null");
                    resp.setStatus(Constant.StatusCode.BAD_REQUEST);
                    return baoFengBidRequest;
                }
                
                // 验证Impression对象
                
                BaoFengBidRequest.Impression imp = baoFengBidRequest.getImp();
                if (ObjectUtils.isEmpty(imp)) {
                    // *logger.debug("imp or impid,H,W is null");
                    resp.setStatus(Constant.StatusCode.BAD_REQUEST);
                    return baoFengBidRequest;
                }
                if (StringUtils.isEmpty(imp.getId())) {
                    resp.setStatus(Constant.StatusCode.BAD_REQUEST);
                    return baoFengBidRequest;
                }
                if (0 == imp.getW()) {
                    resp.setStatus(Constant.StatusCode.BAD_REQUEST);
                    return baoFengBidRequest;
                }
                if (0 == imp.getH()) {
                    resp.setStatus(Constant.StatusCode.BAD_REQUEST);
                    return baoFengBidRequest;
                }
                
                // 验证Device对象
                BaoFengBidRequest.Device device = baoFengBidRequest.getDevice();
                if (ObjectUtils.isEmpty(device)) {
                    // *logger.debug("device or deviceid,dpid is null");
                    resp.setStatus(Constant.StatusCode.BAD_REQUEST);
                    return baoFengBidRequest;
                }
                if (StringUtils.isEmpty(device.getId())) {
                    resp.setStatus(Constant.StatusCode.BAD_REQUEST);
                    return baoFengBidRequest;
                }
                if (StringUtils.isEmpty(device.getDpid())) {
                    resp.setStatus(Constant.StatusCode.BAD_REQUEST);
                    return baoFengBidRequest;
                }
                
                return baoFengBidRequest;
            }
        }
        else {
            baoFengBidRequest = new BaoFengBidRequest();
            resp.setStatus(Constant.StatusCode.BAD_REQUEST);
        }
        return baoFengBidRequest;
    }
    
    private PremiumMADDataModel.MediaBid.MediaRequest conversionToPremiumMADDataModel(boolean isSandbox, BaoFengBidRequest baoFengBidRequest) {
        PremiumMADDataModel.MediaBid.MediaRequest.Builder mediaRequest = PremiumMADDataModel.MediaBid.MediaRequest.newBuilder();
        
        BaoFengBidRequest.App app = baoFengBidRequest.getApp();
        BaoFengBidRequest.Device device = baoFengBidRequest.getDevice();
        BaoFengBidRequest.Impression imp = baoFengBidRequest.getImp();
        
        // 操作系统的类型
        String os = device.getOs();
        if ("ios".equalsIgnoreCase(os)) {
            mediaRequest.setOs(2);
            mediaRequest.setIfa(device.getDpid());
        }
        else if ("android".equalsIgnoreCase(os)) {
            mediaRequest.setOs(1);
            mediaRequest.setDpid(device.getDpid());
        }
        else {
            // 1 iphone 2 ipad 3 android
            switch (device.getDevicetype()) {
                case 1:
                    mediaRequest.setOs(2);
                    break;
                case 2:
                    mediaRequest.setOs(2);
                    break;
                case 3:
                    mediaRequest.setOs(1);
                    break;
                default:
                    mediaRequest.setOs(0);
                    break;
            }
        }
        // 广告请求唯一id
        mediaRequest.setBid(baoFengBidRequest.getId());
        // 广告类型id 暴风所有的adtype都对应我们的Banner广告类型
        // 广告类型 4=频道页banner; 5=详情页banner; 6=焦点图 7=开屏
        int pos = imp.getPos();
        // 通过pos和os/deviceType设置标准的宽和高 由于os和diviceType都是可选的，所以做了这样的判断
        setWidthAndHeight(mediaRequest, device.getDevicetype() != 0 ? device.getDevicetype() : osToDeviceType(mediaRequest), pos);
        // 这里2对应exchange的adtype 1=文字链 2=Banner 3=图形文字链 4=全屏 5=插页 6=开屏  
        pos = 2;
        mediaRequest.setAdtype(pos);
        // app名称，暴风没有提供app包名
        mediaRequest.setBundle(app.getName());
        // 广告位ID
        String adspaceKey = null;
        if (isSandbox) {// sandbox环境
            adspaceKey = "sandbox:BF:" + mediaRequest.getW() + ":" + mediaRequest.getH();
            //
            mediaRequest.setTest(0);
        }
        else {
            adspaceKey = "BF:" + mediaRequest.getW() + ":" + mediaRequest.getH();
            mediaRequest.setTest(1);
        }
        
        if (adspaceKey != null) {
            PlcmtMetaData plcmtMetaData = CacheManager.getInstance().getPlcmtMetaData(adspaceKey);
            if (plcmtMetaData != null) {
                mediaRequest.setAdspacekey(plcmtMetaData.getAdspaceKey());
                mediaRequest.setMediaid(plcmtMetaData.getMediaId());
                mediaRequest.setAdspaceid(plcmtMetaData.getId());
            }
        }
        
        // 连接方式 0：unknow 1：wifi 2：2G/3G/4G
        mediaRequest.setConnectiontype(device.getConnectiontype());
        // 运行商 移动46000；联通46001；电信46003
        String carrier = device.getCarrier() != null ? device.getCarrier() : "";
        switch (carrier) {
            case "46000":
                mediaRequest.setCarrier(1);
                break;
            case "46001":
                mediaRequest.setCarrier(2);
                break;
            case "46003":
                mediaRequest.setCarrier(3);
                break;
            default:
                mediaRequest.setCarrier(0);
                break;
        }
        // 设备浏览器的User-Agent字符串
        mediaRequest.setUa(device.getUa() != null ? device.getUa() : "");
        // ip地址
        mediaRequest.setIp(device.getIp() != null ? device.getIp() : "");
        
        // 操作系统的版本
        mediaRequest.setOsv(device.getOsv() != null ? device.getOsv() : "");
        // 设备型号
        mediaRequest.setModel(device.getModel() != null ? device.getModel() : "");
        // mac地址
        mediaRequest.setMac(device.getMac() != null ? device.getMac() : "");
        
        return mediaRequest.build();
        
    }
    
    private void setWidthAndHeight(com.madhouse.ssp.PremiumMADDataModel.MediaBid.MediaRequest.Builder mediaRequest, int deviceType, int pos) {
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
    
    private void setDefaultWH(com.madhouse.ssp.PremiumMADDataModel.MediaBid.MediaRequest.Builder mediaRequest) {
        mediaRequest.setW(0);
        mediaRequest.setH(0);
    }
    
    private int osToDeviceType(com.madhouse.ssp.PremiumMADDataModel.MediaBid.MediaRequest.Builder mediaRequest) {
        if (StringUtil.isEmpty(Integer.toString(mediaRequest.getOs())))
            return 0;
        if (0 == mediaRequest.getOs()) {
            // android系统对应的deviceType是3
            return 3;
        }
        else {
            // ios系统对应的deviceType是1（这里默认使用1表示iphone，ios系统细分了iphone和ipad）
            return -1;
        }
    }
    
}
