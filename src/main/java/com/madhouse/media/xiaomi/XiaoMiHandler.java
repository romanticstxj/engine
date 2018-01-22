package com.madhouse.media.xiaomi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.madhouse.resource.ResourceManager;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.madhouse.cache.CacheManager;
import com.madhouse.cache.MediaBidMetaData;
import com.madhouse.cache.MediaMappingMetaData;
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
import com.madhouse.ssp.avro.MediaRequest.Builder;
import com.madhouse.ssp.avro.MediaResponse;
import com.madhouse.ssp.avro.Track;
import com.madhouse.util.HttpUtil;
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
            String bytes = HttpUtil.getRequestPostBytes(req);
            
            XiaoMiBidRequest bidRequest = JSON.parseObject(bytes, XiaoMiBidRequest.class);
            logger.info("XiaoMi Request params is : {}", JSON.toJSONString(bidRequest));
            int status = validateRequiredParam(bidRequest);
            if (status == Constant.StatusCode.OK) {
                List<MediaBid.Builder> mediaBids = conversionToPremiumMADDataModel(isSandbox, bidRequest);
                if(mediaBids != null){
                    mediaBidMetaData.setMediaBids(mediaBids);
                    return true;
                }
            }
            resp.setStatus(Constant.StatusCode.BAD_REQUEST);
            return false;
        } catch (Exception e) {
            logger.error(e.toString() + "_Status_" + Constant.StatusCode.BAD_REQUEST);
            resp.setStatus(Constant.StatusCode.BAD_REQUEST);
            return false;
        }
    }
    
    private int validateRequiredParam(XiaoMiBidRequest bidRequest) {
        if (ObjectUtils.isNotEmpty(bidRequest)) {
            String id = bidRequest.getId();
            if (StringUtils.isEmpty(id)) {
                logger.warn("bidRequest.id is missing");
                return Constant.StatusCode.BAD_REQUEST;
            }
            if (bidRequest.getImp() == null || bidRequest.getImp().length == 0) {
                logger.warn("{},bidRequest.Imp is missing",id);
                return Constant.StatusCode.BAD_REQUEST;
            }
            Imp imp = bidRequest.getImp()[0];
    
            Integer admtype = imp.getAdmtype();
            if (admtype == null || admtype != 2) {//只支持json
                logger.warn("{},bidRequest.Imp.admtype is missing",id);
                return Constant.StatusCode.BAD_REQUEST;
            }
           
            if (StringUtils.isEmpty(imp.getId())) {
                logger.warn("{},bidRequest.Imp.id is missing",id);
                return Constant.StatusCode.BAD_REQUEST;
            }
            if (StringUtils.isEmpty(imp.getTagid())) {
                logger.warn("{},bidRequest.Imp.Tagid is missing",id);
                return Constant.StatusCode.BAD_REQUEST;
            }
            if (ObjectUtils.isEmpty(imp.getBanner()) && ObjectUtils.isEmpty(imp.getNativead()) && ObjectUtils.isEmpty(imp.getSplash())) {
                logger.warn("{},bidRequest.Imp.Banner, Imp.Nativead,Imp.Splash is missing",id);
                return Constant.StatusCode.BAD_REQUEST;
            }
    
            if (imp.getNativead() != null) {
                if (imp.getNativead().getRequest() == null) {
                    logger.warn("{},bidRequest.Imp.Nativead.Request is missing",id);
                    return Constant.StatusCode.BAD_REQUEST;
                }
            }
            //以AdTemplate的尺寸为准传多个支持的宽度高度，取第一个
            AdTemplate[] adTemplates = imp.getTemplates();
            if (adTemplates != null && adTemplates.length != 0) {
                AdTemplate adTemplate = adTemplates[0];
                if (adTemplate != null) {
                    if (StringUtils.isEmpty(adTemplate.getId())) {
                        logger.warn("{},bidRequest.Imp.adTemplates.id is missing",id);
                        return Constant.StatusCode.BAD_REQUEST;
                    }
                    if (adTemplate.getWidth() == null){
                        logger.warn("{},bidRequest.Imp.adTemplates.Width is missing",id);
                        return Constant.StatusCode.BAD_REQUEST;
                    }
                    if (adTemplate.getHeight() == null){
                        logger.warn("{},bidRequest.Imp.adTemplates.Height is missing",id);
                        return Constant.StatusCode.BAD_REQUEST;
                    }
                }
            } else {
                logger.warn("{},bidRequest.Imp.adTemplates is missing",id);
                return Constant.StatusCode.BAD_REQUEST;
            }
            return Constant.StatusCode.OK;
        }
        return Constant.StatusCode.BAD_REQUEST;
    }
    private List<MediaBid.Builder> conversionToPremiumMADDataModel(boolean isSandbox, XiaoMiBidRequest bidRequest) {
        List<MediaBid.Builder> mediaBids = new ArrayList<>();
        MediaRequest.Builder mediaRequest = MediaRequest.newBuilder();
        App app = bidRequest.getApp();
        if (app != null) {
            mediaRequest.setName(StringUtil.toString(app.getName()));
            mediaRequest.setBundle(StringUtil.toString(app.getBundle()));
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
            if(!StringUtils.isEmpty(device.getMake())){
                mediaRequest.setMake(device.getMake()); 
            }
            if(!StringUtils.isEmpty(device.getModel())){
                mediaRequest.setModel(device.getModel()); 
            }
            String os = device.getOs();
            if (StringUtils.isNotEmpty(os)) {
                if (XiaoMiStatusCode.XiaoMiOs.ANDROID.equalsIgnoreCase(os)) {
                    mediaRequest.setOs(Constant.OSType.ANDROID);
                    mediaRequest.setDid(device.getDidsha1());
                    mediaRequest.setDidmd5(!StringUtils.isEmpty(device.getDidmd5()) ? device.getDidmd5() : !StringUtils.isEmpty(device.getDidsha1()) ? device.getDidsha1() : "");
                    //Android ID - md5 - sha1
                    mediaRequest.setDpid(device.getDpid());
                    mediaRequest.setDpidmd5(device.getDpidmd5() != null ? device.getDpidmd5() : device.getDpidsha1());
                } else if (XiaoMiStatusCode.XiaoMiOs.IOS.equalsIgnoreCase(os)) {
                    mediaRequest.setOs(Constant.OSType.IOS);
                    mediaRequest.setIfa(!StringUtils.isEmpty(device.getIdfasha1()) ? device.getIdfasha1() : !StringUtils.isEmpty(device.getIdfamd5()) ? device.getIdfamd5() : "");
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
            if(!StringUtils.isEmpty(device.getMacmd5())){
                mediaRequest.setMacmd5(!StringUtils.isEmpty(device.getMacmd5()) ? device.getMacmd5() : !StringUtils.isEmpty(device.getMacsha1()) ? device.getMacsha1() : "");
            }
            Geo geo = device.getGeo();
            if (geo != null) {
                com.madhouse.ssp.avro.Geo.Builder vargeo = com.madhouse.ssp.avro.Geo.newBuilder();
                if(ObjectUtils.isNotEmpty(geo.getLat()+"")){
                    vargeo.setLat((float)geo.getLat());
                }
                if(ObjectUtils.isNotEmpty(geo.getLon()+"")){
                    vargeo.setLon((float)geo.getLon());
                }
                mediaRequest.setGeoBuilder(vargeo);
            }
            mediaRequest.setType(Constant.MediaType.APP);
            boolean isExsitsMediaMappingMetadata = true;
            for (Imp imp : bidRequest.getImp()) {
                MediaBid.Builder mediaBid = MediaBid.newBuilder();
                MediaRequest.Builder request = MediaRequest.newBuilder(mediaRequest);
                AdTemplate[] adTemplates = imp.getTemplates();
                AdTemplate adTemplate = adTemplates[0];
                request.setW(adTemplate.getWidth());
                request.setH(adTemplate.getHeight());
                request.setBidfloor((int)imp.getBidfloor());
                request.setBid(imp.getId());
                String adspaceKey = "";
                if (isSandbox) {//sandbox环境
                    adspaceKey = new StringBuffer().append("sandbox:").append("XM:").append(adTemplate.getWidth()).append(":").append(adTemplate.getHeight()).toString();
                    //模拟竞价，
                    request.setTest(Constant.Test.SIMULATION);
                } else {
                    adspaceKey = new StringBuffer().append("XM:").append(adTemplate.getWidth()).append(":").append(adTemplate.getHeight()).toString();
                    request.setTest(Constant.Test.REAL);
                }
                MediaMappingMetaData mappingMetaData = CacheManager.getInstance().getMediaMapping(adspaceKey);
                if (mappingMetaData != null) {
                    request.setAdspacekey(mappingMetaData.getAdspaceKey());
                } else {
                    if (isSandbox) {//sandbox环境
                        mappingMetaData = CacheManager.getInstance().getMediaMapping("sandbox:XM:0:0");
                    } else {
                        mappingMetaData = CacheManager.getInstance().getMediaMapping("XM:0:0");
                    }
                    if(mappingMetaData != null){
                        request.setAdspacekey(mappingMetaData.getAdspaceKey());
                    }else{
                        isExsitsMediaMappingMetadata = false;
                    }
                }
                mediaBid.setRequestBuilder(request);
                mediaBids.add(mediaBid);
            }
            if (!isExsitsMediaMappingMetadata) {
                return null;
            }

            logger.info("xiaomi convert mediaRequest is : {}", mediaRequest.toString());
        }else{
            return null;
        }
        
        return mediaBids;
        
    }

    @Override
    public boolean packageMediaResponse(MediaBidMetaData mediaBidMetaData, HttpServletResponse resp) {
        try {
            if (mediaBidMetaData != null && mediaBidMetaData.getMediaBids() != null && mediaBidMetaData.getMediaBids().size()>0) {
                MediaBid.Builder mediaBid = mediaBidMetaData.getMediaBids().get(0);
                if (mediaBid.getResponseBuilder() != null && mediaBid.getStatus() == Constant.StatusCode.OK) {
                    XiaoMiResponse xiaoMiResponse = convertToXiaoMiResponse(mediaBidMetaData);
                    if(null != xiaoMiResponse){
                        resp.setHeader("Content-Type", "application/json; charset=utf-8");
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
        MediaBid.Builder mediaBid = mediaBidMetaData.getMediaBids().get(0);
        MediaResponse.Builder mediaResponse= mediaBid.getResponseBuilder();
        Builder mediaRequest= mediaBid.getRequestBuilder();
        
        XiaoMiBidRequest bidRequest =(XiaoMiBidRequest)mediaBidMetaData.getRequestObject();
        
        bidResponse.setId(bidRequest.getId());
        //mediaResponse.get
        XiaoMiResponse.SeatBid seatBid = bidResponse.new SeatBid();
        
        
        XiaoMiResponse.Bid bid = bidResponse.new Bid();
        bid.setId(mediaBid.getImpid());
        bid.setImpid(mediaBid.getRequestBuilder().getBid());
        bid.setPrice(mediaResponse.getPrice());//Bid price as CPM; 必须高于底价,否则竞价失败 ;必须字段,单位为分
        bid.setAdid(mediaRequest.getAdspacekey());
        //判断接受的类型
        String[] mines = null;
        Imp imp = bidRequest.getImp()[0];
        if (imp.getBanner() != null) {
            mines = imp.getBanner().getMines();
        } else if (imp.getSplash() != null) {
            mines = imp.getSplash().getMines();
        }
        if (mines != null && mines.length > 0) {//需要判断类型
            String imgUrl = mediaResponse.getAdm().get(0);
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
        adm.put("langdingurl", mediaResponse.getLpgurl());
        adm.put("imgurl", mediaResponse.getAdm().get(0));
        adm.put("title", mediaResponse.getTitle());
        adm.put("source", mediaResponse.getDesc());
        bid.setAdm(JSON.toJSONString(adm));
        bid.setTagid(bidRequest.getImp()[0].getTagid());//广告位标识ID，对应imp中的tagid
        bid.setTemplateid(bidRequest.getImp()[0].getTemplateid());

        bid.setBillingtype(1);//计费方式。1:千次展示 2:点击 3:下载 4:排期 5:轮 播。目前只支持CPM计费
        bid.setH(mediaRequest.getH());
        bid.setW(mediaRequest.getW());
        bid.setLandingurl(mediaResponse.getLpgurl());
        bid.setTemplateid(bidRequest.getImp()[0].getTemplates()[0].getId());

        List<Track> imgtracking = mediaResponse.getMonitorBuilder().getImpurl();
        if (imgtracking != null && imgtracking.size() != 0) {
            String[] impurl = new String[imgtracking.size()];
            for (int i = 0; i < imgtracking.size(); i++) {
                String s = imgtracking.get(i).getUrl();
                if (s != null && s.length() != 0) {
                    impurl[i] = imgtracking.get(i).getUrl();
                }
            }
            bid.setImpurl(impurl);
        }
        List<String> thclkurl = mediaResponse.getMonitorBuilder().getClkurl();
        if (thclkurl != null && thclkurl.size() != 0) {
            String[] curl = new String[thclkurl.size()];
            for (int i = 0; i < thclkurl.size(); i++) {
                String s = thclkurl.get(i);
                if (s != null && s.length() != 0) {
                    curl[i] = thclkurl.get(i);
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
        bidResponse.setBidid(ResourceManager.getInstance().nextId());
        logger.info("XiaoMi Response params is : {}", JSON.toJSONString(bidResponse));
        return bidResponse;
    }

}
