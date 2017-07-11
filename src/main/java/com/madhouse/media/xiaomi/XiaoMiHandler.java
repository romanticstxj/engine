package com.madhouse.media.xiaomi;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.madhouse.cache.CacheManager;
import com.madhouse.cache.MediaBidMetaData;
import com.madhouse.cache.PlcmtMetaData;
import com.madhouse.media.MediaBaseHandler;
import com.madhouse.media.xiaomi.request.AdTemplate;
import com.madhouse.media.xiaomi.request.App;
import com.madhouse.media.xiaomi.request.Device;
import com.madhouse.media.xiaomi.request.Geo;
import com.madhouse.media.xiaomi.request.Imp;
import com.madhouse.media.xiaomi.request.XiaoMiBidRequest;
import com.madhouse.ssp.Constant;
import com.madhouse.ssp.avro.MediaBid;
import com.madhouse.ssp.avro.MediaRequest;
import com.madhouse.ssp.avro.MediaResponse;
import com.madhouse.ssp.avro.Track;
import com.madhouse.util.ObjectUtils;
import com.madhouse.util.StringUtil;

public class XiaoMiHandler extends MediaBaseHandler {
    
    @Override
    public boolean parseMediaRequest(HttpServletRequest req, MediaBidMetaData mediaBidMetaData, HttpServletResponse resp) {
        boolean isSandbox = false;
        String env = req.getHeader("env");
        if (env != null && env.equals("sandbox")) {
            isSandbox = true;
        }
        try {
            req.setCharacterEncoding("UTF-8");
            String bytes = getRequestPostBytes(req);
            
            XiaoMiBidRequest bidRequest = JSON.parseObject(bytes, XiaoMiBidRequest.class);
            logger.debug("XiaoMi Request params is : {}", JSON.toJSONString(bidRequest));
            int status = validateRequiredParam(bidRequest);
            if (status == Constant.StatusCode.OK) {
                MediaRequest mediaRequest = conversionToPremiumMADDataModel(isSandbox,bidRequest);
                mediaBidMetaData.getMediaBidBuilder().setRequest(mediaRequest);
                mediaBidMetaData.setRequestObject(bidRequest);
                return true;
            } else {
                resp.setStatus(Constant.StatusCode.BAD_REQUEST);
                return false;
            }
        } catch (Exception e) {
            logger.error(e.toString() + "_Status_" + Constant.StatusCode.BAD_REQUEST);
            resp.setStatus(Constant.StatusCode.BAD_REQUEST);
            return false;
        }
    }
    
    private int validateRequiredParam(XiaoMiBidRequest bidRequest) {
        if (!ObjectUtils.isEmpty(bidRequest)) {
            String id = bidRequest.getId();
            if (StringUtils.isEmpty(id)) {
                logger.debug("bidRequest.id is missing");
                return Constant.StatusCode.BAD_REQUEST;
            }
            if (bidRequest.getImp() == null || bidRequest.getImp().length == 0) {
                logger.debug("{},bidRequest.Imp is missing",id);
                return Constant.StatusCode.BAD_REQUEST;
            }
            Imp imp = bidRequest.getImp()[0];
    
            Integer admtype = imp.getAdmtype();
            if (admtype == null || admtype != 2) {//只支持json
                logger.debug("{},bidRequest.Imp.admtype is missing",id);
                return Constant.StatusCode.BAD_REQUEST;
            }
           
            if (StringUtils.isEmpty(imp.getId())) {
                logger.debug("{},bidRequest.Imp.id is missing",id);
                return Constant.StatusCode.BAD_REQUEST;
            }
            if (StringUtils.isEmpty(imp.getTagid())) {
                logger.debug("{},bidRequest.Imp.Tagid is missing",id);
                return Constant.StatusCode.BAD_REQUEST;
            }
            if (ObjectUtils.isEmpty(imp.getBanner()) && ObjectUtils.isEmpty(imp.getNativead()) && ObjectUtils.isEmpty(imp.getSplash())) {
                logger.debug("{},bidRequest.Imp.Banner, Imp.Nativead,Imp.Splash is missing",id);
                return Constant.StatusCode.BAD_REQUEST;
            }
    
            if (imp.getNativead() != null) {
                if (imp.getNativead().getRequest() == null) {
                    logger.debug("{},bidRequest.Imp.Nativead.Request is missing",id);
                    return Constant.StatusCode.BAD_REQUEST;
                }
            }
            //以AdTemplate的尺寸为准传多个支持的宽度高度，取第一个
            AdTemplate[] adTemplates = imp.getTemplates();
            if (adTemplates != null && adTemplates.length != 0) {
                AdTemplate adTemplate = adTemplates[0];
                if (adTemplate != null) {
                    if (StringUtils.isEmpty(adTemplate.getId())) {
                        logger.debug("{},bidRequest.Imp.adTemplates.id is missing",id);
                        return Constant.StatusCode.BAD_REQUEST;
                    }
                    if (adTemplate.getWidth() == null){
                        logger.debug("{},bidRequest.Imp.adTemplates.Width is missing",id);
                        return Constant.StatusCode.BAD_REQUEST;
                    }
                    if (adTemplate.getHeight() == null){
                        logger.debug("{},bidRequest.Imp.adTemplates.Height is missing",id);
                        return Constant.StatusCode.BAD_REQUEST;
                    }
                }
            } else {
                logger.debug("{},bidRequest.Imp.adTemplates is missing",id);
                return Constant.StatusCode.BAD_REQUEST;
            }
            return Constant.StatusCode.OK;
        }
        return Constant.StatusCode.BAD_REQUEST;
    }
    private MediaRequest conversionToPremiumMADDataModel(boolean isSandbox, XiaoMiBidRequest bidRequest) {
        MediaRequest.Builder mediaRequest = MediaRequest.newBuilder();
        
        Imp imp = bidRequest.getImp()[0];
        AdTemplate[] adTemplates = imp.getTemplates();
        AdTemplate adTemplate = adTemplates[0];
        
        mediaRequest.setW(adTemplate.getWidth());
        mediaRequest.setH(adTemplate.getHeight());
        
        mediaRequest.setBid(imp.getId());
        App app = bidRequest.getApp();
        if (app != null) {
            mediaRequest.setName(StringUtil.validateString(app.getName()));
            mediaRequest.setBundle(StringUtil.validateString(app.getBundle()));
        }
        Device device = bidRequest.getDevice();
        if (device != null) {
            if(!StringUtils.isEmpty(device.getIp())){
                mediaRequest.setIp(device.getIp());
            }
            if(!StringUtils.isEmpty(device.getUa())){
                mediaRequest.setUa(device.getUa());
            }
            switch (device.getDevicetype()) {
                case XiaoMiStatusCode.Devicetype.UNKNOWN:
                    mediaRequest.setDevicetype(Constant.DeviceType.UNKNOWN);
                    break;
                case XiaoMiStatusCode.Devicetype.IPHONE:
                    mediaRequest.setDevicetype(Constant.DeviceType.PHONE);
                    break;
                case XiaoMiStatusCode.Devicetype.IPAD:
                    mediaRequest.setDevicetype(Constant.DeviceType.PAD);
                    break;
                default:
                    mediaRequest.setDevicetype(Constant.DeviceType.UNKNOWN);
                    break;
            }
            //网络服务提供商，未填 
            mediaRequest.setCarrier(Constant.Carrier.UNKNOWN);
            mediaRequest.setMake(device.getMake());
            mediaRequest.setModel(device.getModel());
            String os = device.getOs();
            if (StringUtils.isNotEmpty(os)) {
                if (XiaoMiStatusCode.XiaoMiOs.ANDROID.equalsIgnoreCase(os)) {
                    mediaRequest.setOs(Constant.OSType.ANDROID);
                    mediaRequest.setDid(device.getDidsha1());
                    mediaRequest.setDidmd5(device.getDidmd5());
                    //Android ID - md5 - sha1
                    mediaRequest.setDpid(device.getDpid());
                    mediaRequest.setDpidmd5(device.getDpidmd5() != null ? device.getDpidmd5() : device.getDpidsha1());
                } else if (XiaoMiStatusCode.XiaoMiOs.IOS.equalsIgnoreCase(os)) {
                    mediaRequest.setOs(Constant.OSType.IOS);
                    mediaRequest.setIfa(device.getIdfasha1());
                } else {
                    mediaRequest.setOs(Constant.OSType.UNKNOWN);
                }
            } else {
                mediaRequest.setOs(Constant.OSType.UNKNOWN);
            }
            Integer connectionType = device.getConnectiontype();
            if (connectionType != null) {
                switch (connectionType) {
                    case XiaoMiStatusCode.ConnectionType.WIFI://WIFI 网络
                        mediaRequest.setConnectiontype(Constant.ConnectionType.WIFI);
                        break;
                    case XiaoMiStatusCode.ConnectionType._2G://蜂窝数据网络 ­ 2G
                        mediaRequest.setConnectiontype(Constant.ConnectionType._2G);
                        break;
                    case XiaoMiStatusCode.ConnectionType._3G://蜂窝数据网络 ­ 3G
                        mediaRequest.setConnectiontype(Constant.ConnectionType._3G);
                        break;
                    case XiaoMiStatusCode.ConnectionType._4G://蜂窝数据网络 ­ 4G
                        mediaRequest.setConnectiontype(Constant.ConnectionType._4G);
                        break;
                    default:
                        mediaRequest.setConnectiontype(Constant.ConnectionType.CELL);
                        break;
                }
            }
            mediaRequest.setOsv(device.getOsv() != null ? device.getOsv() : null);
            mediaRequest.setMac(device.getMacsha1());
            mediaRequest.setMac(device.getMacmd5());

            Geo geo = device.getGeo();
            if (geo != null) {
                if(ObjectUtils.isNotEmpty(geo.getLat())){
                    mediaRequest.setLat((float)geo.getLat());
                }
                if(ObjectUtils.isNotEmpty(geo.getLon())){
                    mediaRequest.setLon((float)geo.getLon());
                }
            }
            String adspaceKey = "";
            if (isSandbox) {//sandbox环境
                adspaceKey = new StringBuffer().append("sandbox:").append("XM:").append(adTemplate.getWidth()).append(":").append(adTemplate.getHeight()).toString();
                //模拟竞价，不计费
                mediaRequest.setTest(Constant.Test.SIMULATION);
            } else {
                adspaceKey = new StringBuffer().append("XM:").append(adTemplate.getWidth()).append(":").append(adTemplate.getHeight()).toString();
                mediaRequest.setTest(Constant.Test.REAL);
            }
            PlcmtMetaData plcmtMetaData = CacheManager.getInstance().getPlcmtMetaData(adspaceKey);
            if (plcmtMetaData != null) {
                mediaRequest.setAdspacekey(plcmtMetaData.getAdspaceKey());
            } else {
                if (isSandbox) {//sandbox环境
                    plcmtMetaData = CacheManager.getInstance().getPlcmtMetaData("sandbox:XM:0:0");
                } else {
                    plcmtMetaData = CacheManager.getInstance().getPlcmtMetaData("XM:0:0");
                }
                if(plcmtMetaData != null){
                    mediaRequest.setAdspacekey(plcmtMetaData.getAdspaceKey());
                }
            }
            logger.info("xiaomi request params is : {}", mediaRequest.toString());
        }else{
            return null;
        }
        
        return mediaRequest.build();
        
    }

    @Override
    public boolean packageMediaResponse(MediaBidMetaData mediaBidMetaData, HttpServletResponse resp) {
        try {
            if (mediaBidMetaData != null && mediaBidMetaData.getMediaBidBuilder() != null) {
                MediaBid.Builder mediaBid = mediaBidMetaData.getMediaBidBuilder();
                if (mediaBid.getResponseBuilder() != null && mediaBid.getStatus() == Constant.StatusCode.OK) {
                    XiaoMiResponse xiaoMiResponse = convertToXiaoMiResponse(mediaBidMetaData);
                    if(null != xiaoMiResponse){
                        resp.getOutputStream().write(JSON.toJSONString(xiaoMiResponse).getBytes());
                        return true;
                    }
                } else {
                    resp.setStatus(mediaBid.getStatus());
                    return false;
                }
            }
        } catch (Exception e) {
            logger.error(e.toString() + "_Status_" + Constant.StatusCode.BAD_REQUEST);
            resp.setStatus(Constant.StatusCode.BAD_REQUEST);
            return false;
        }
        resp.setStatus(Constant.StatusCode.BAD_REQUEST);
        return false;
    }
    private XiaoMiResponse convertToXiaoMiResponse(MediaBidMetaData mediaBidMetaData) {
        XiaoMiResponse bidResponse = new XiaoMiResponse();
        MediaResponse mediaResponse= mediaBidMetaData.getMediaBidBuilder().getResponse();
        MediaRequest mediaRequest= mediaBidMetaData.getMediaBidBuilder().getRequest();
        
        XiaoMiBidRequest bidRequest =(XiaoMiBidRequest)mediaBidMetaData.getRequestObject();
        
        bidResponse.setId(bidRequest.getId());
        //mediaResponse.get
        XiaoMiResponse.SeatBid seatBid = bidResponse.new SeatBid();
        
        
        XiaoMiResponse.Bid bid = bidResponse.new Bid();
        bid.setImpid(bidRequest.getImp()[0].getId());
        bid.setPrice(mediaRequest.getBidfloor());//Bid price as CPM; 必须高于底价,否则竞价失败 ;必须字段,单位为分
        bid.setAdid(mediaRequest.getAdspacekey().toString());
        //判断接受的类型
        String[] mines = null;
        Imp imp = bidRequest.getImp()[0];
        if (imp.getBanner() != null) {
            mines = imp.getBanner().getMines();
        } else if (imp.getSplash() != null) {
            mines = imp.getSplash().getMines();
        }
        if (mines != null && mines.length > 0) {//需要判断类型
            String imgUrl = mediaResponse.getAdm().get(0).toString();
            if (StringUtils.isEmpty(imgUrl)) {
                logger.warn("imgurl is null");
                return null;
            }
            String lowSuffix = imgUrl.substring(imgUrl.lastIndexOf(".") + 1).toLowerCase();
            boolean flag = false;
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0; i < mines.length; i++) {
                String lowMine = mines[i].toLowerCase();
                if (lowMine.contains(lowSuffix)) {
                    flag = true;
                }
            }
            if (!flag) { //没有支持的类型
                logger.warn("mine type no support:" + lowSuffix);
                return null;
            }
        }
        Map<String, String> adm = new HashMap<>();
        adm.put("langdingurl", mediaResponse.getLpgurl().toString());
        adm.put("imgurl", mediaResponse.getAdm().get(0).toString());
        adm.put("title", mediaResponse.getTitle().toString());
        adm.put("source", mediaResponse.getDesc().toString());
        bid.setAdm(JSON.toJSONString(adm));
        bid.setTagid(bidRequest.getImp()[0].getTagid());//广告位标识ID，对应imp中的tagid
        bid.setTemplateid(bidRequest.getImp()[0].getTemplateid());

        bid.setBillingtype(1);//计费方式。1:千次展示 2:点击 3:下载 4:排期 5:轮 播。目前只支持CPM计费
        bid.setH(mediaRequest.getH());
        bid.setW(mediaRequest.getW());
        bid.setLandingurl(mediaResponse.getLpgurl().toString());
        bid.setTemplateid(bidRequest.getImp()[0].getTemplates()[0].getId());

        List<Track> imgtracking = mediaResponse.getMonitor().getImpurl();
        if (imgtracking != null && imgtracking.size() != 0) {
            String[] impurl = new String[imgtracking.size()];
            for (int i = 0; i < imgtracking.size(); i++) {
                String s = imgtracking.get(i).getUrl().toString();
                if (s != null && s.length() != 0) {
                    impurl[i] = imgtracking.get(i).getUrl().toString();
                }
            }
            bid.setImpurl(impurl);
        }
        List<CharSequence> thclkurl = mediaResponse.getMonitor().getClkurl();
        if (thclkurl != null && thclkurl.size() != 0) {
            String[] curl = new String[thclkurl.size()];
            for (int i = 0; i < thclkurl.size(); i++) {
                String s = thclkurl.get(i).toString();
                if (s != null && s.length() != 0) {
                    curl[i] = thclkurl.get(i).toString();
                }
            }
            bid.setCurl(curl);
        }
        
        seatBid.setBid(new XiaoMiResponse.Bid[]{bid});
        seatBid.setSeat("madhouse");
        seatBid.setCm(0); //默认值
        seatBid.setGroup(0);//默认值

        XiaoMiResponse.SeatBid[] setBids = {seatBid};//目前仅支持长度为1
        bidResponse.setSeatbid(setBids);
        logger.debug("XiaoMi Response params is : {}", JSON.toJSONString(bidResponse));
        return bidResponse;
    }

    public static String getRequestPostBytes(HttpServletRequest request) throws IOException {
        int contentLength = request.getContentLength();
        if (contentLength <= 0) {
            return null;
        }
        byte buffer[] = new byte[contentLength];
        for (int i = 0; i < contentLength; ) {

            int readlen = request.getInputStream().read(buffer, i, contentLength - i);
            if (readlen == -1) {
                break;
            }
            i += readlen;
        }
        return new String(buffer);
    }
}
