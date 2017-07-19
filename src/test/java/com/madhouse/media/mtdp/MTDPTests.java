package com.madhouse.media.mtdp;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class MTDPTests {

    public void requestTest() {


        DPAds.BidRequest.Imp.Native.Requestobj.Assets.Image.Builder imageBuilder = DPAds.BidRequest.Imp.Native.Requestobj.Assets.Image.newBuilder();
        DPAds.BidRequest.Imp.Native.Requestobj.Assets.Image image = imageBuilder.setExt("")
                .setW(0)
                .setH(0)
                .build();

        DPAds.BidRequest.Imp.Native.Requestobj.Assets.Title.Builder titleBuilder = DPAds.BidRequest.Imp.Native.Requestobj.Assets.Title.newBuilder();
        DPAds.BidRequest.Imp.Native.Requestobj.Assets.Title title = titleBuilder.setExt("titleExt")
                .setText("titleText")
                .build();

        DPAds.BidRequest.Imp.Native.Requestobj.Assets.Builder assetsBuilder = DPAds.BidRequest.Imp.Native.Requestobj.Assets.newBuilder();
        DPAds.BidRequest.Imp.Native.Requestobj.Assets assets = assetsBuilder.setExt("assetsExt")
                .setId(123)
                .setImg(image)
                .setRequired(456)
                .setTitle(title)
                .build();

        DPAds.BidRequest.Imp.Native.Requestobj.Builder requestObjBuilder = DPAds.BidRequest.Imp.Native.Requestobj.newBuilder();
        DPAds.BidRequest.Imp.Native.Requestobj requestobj = requestObjBuilder.addAssets(assets)
                .setVer("requestObjVersion")
                .build();

        DPAds.BidRequest.Imp.Native.Builder nNativeBuilder = DPAds.BidRequest.Imp.Native.newBuilder();
        DPAds.BidRequest.Imp.Native aNative = nNativeBuilder.setExt("nativeExt")
                .setRequestobj(requestobj)
                .build();

        DPAds.BidRequest.Imp.Banner.Builder bannerBuilder = DPAds.BidRequest.Imp.Banner.newBuilder();
        DPAds.BidRequest.Imp.Banner banner = bannerBuilder.setW(640)
                .setH(140)
                .setId("bannerId")
                .build();

        DPAds.BidRequest.Imp.Builder impBuilder = DPAds.BidRequest.Imp.newBuilder();
        DPAds.BidRequest.Imp imp = impBuilder.setBanner(banner)
                .setId("impid")
                .setNative(aNative)
                .setSlotId("10024")
                .setBidfloor(0.0001f)
                .build();


        DPAds.BidRequest.Site.Builder siteBuilder = DPAds.BidRequest.Site.newBuilder();
        DPAds.BidRequest.Site site = siteBuilder.setDomain("www.test.com")
                .setId("siteId")
                .setName("siteName")
                .build();

        DPAds.BidRequest.App.Builder appBuilder = DPAds.BidRequest.App.newBuilder();
        DPAds.BidRequest.App app = appBuilder.setId("appid")
                .setName("appName")
                .build();

        // DPAds.BidRequest.Device.Geo.Builder geoBuilder = DPAds.BidRequest.Device.Geo.newBuilder();
        DPAds.BidRequest.Device.Builder deviceBuilder = DPAds.BidRequest.Device.newBuilder();
        DPAds.BidRequest.Device device = deviceBuilder.setIdfa("idfa")
                .setIp("14.102.156.0")
                .setModel("model")
                .setOs("android")
                .setOsv("4.1.0")
                .setUa("user-agent abcde")
                .build();

        DPAds.BidRequest bidRequest = DPAds.BidRequest.newBuilder()
                .setDevice(device)
                .setApp(app)
                .setSite(site)
                .setId("000001")
                .addImp(imp)
                .build();


        System.out.println(bidRequest.toString());
//        System.out.println("json:"+ JsonFormat.printToString(bidRequest));

        CloseableHttpClient client = HttpClients.createDefault();
//        HttpPost httpPost = new HttpPost("http://ad.beta.madserving.com/adcall/dianping/bidrequest");
        HttpPost httpPost = new HttpPost("http://localhost:8181/adcall/dianping/bidrequest");
        httpPost.setEntity(new ByteArrayEntity(bidRequest.toByteArray()));
        try {
            CloseableHttpResponse execute = client.execute(httpPost);
            HttpEntity entity = execute.getEntity();
            DPAds.BidResponse bidResponse = DPAds.BidResponse.parseFrom(EntityUtils.toByteArray(entity));

            System.out.println(bidResponse.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    
}
