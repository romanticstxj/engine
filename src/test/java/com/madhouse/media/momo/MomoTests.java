package com.madhouse.media.momo;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class MomoTests {
    public static void main(String[] arg) {
        MomoExchange.BidRequest.Imp.Native.Builder nativeBuilder = MomoExchange.BidRequest.Imp.Native.newBuilder();
        nativeBuilder.addNativeFormat(MomoNativeTypeEnums.FEED_LANDING_PAGE_LARGE_IMG.getCode());
//        nativeBuilder.addNativeFormat(MomoExchange.NativeFormat.FEED_LANDING_PAGE_VIDEO);
        
        MomoExchange.BidRequest.Imp.Pmp.Deal.Builder dealBuilder = MomoExchange.BidRequest.Imp.Pmp.Deal.newBuilder();
        dealBuilder.setId("20170829");
        MomoExchange.BidRequest.Imp.Pmp.Builder pmpBuilder = MomoExchange.BidRequest.Imp.Pmp.newBuilder();
        pmpBuilder.addDeals(dealBuilder);
        
        MomoExchange.BidRequest.Imp.Builder impBuilder = MomoExchange.BidRequest.Imp.newBuilder();
        impBuilder.setId("3c7a762982484fb4");
        impBuilder.setSlotid("1-2");
        impBuilder.setBidfloor(2);
        impBuilder.setPmp(pmpBuilder);
        impBuilder.setNative(nativeBuilder);

        MomoExchange.BidRequest.App.Builder appBuilder = MomoExchange.BidRequest.App.newBuilder();
        appBuilder.setId("1");
        appBuilder.setBundle("ll.com");
        appBuilder.setVer("50701");
        appBuilder.setName("news_article");

        MomoExchange.BidRequest.Geo.Builder geoBuilder = MomoExchange.BidRequest.Geo.newBuilder();
        geoBuilder.setCity("上海");
        geoBuilder.setProvince("上海");
        geoBuilder.setLat(114.3);
        geoBuilder.setLon(89.2);

        MomoExchange.BidRequest.Device.Builder deviceBuilder = MomoExchange.BidRequest.Device.newBuilder();
        deviceBuilder.setUa("");
        deviceBuilder.setGeo(geoBuilder);
        deviceBuilder.setDid("EC8E44F5-20E3-4DE8-943E-270D0EB734C8");
        deviceBuilder.setOs("android");
        deviceBuilder.setIp("120.52.147.18");
        deviceBuilder.setOsv("9.3.2");
        deviceBuilder.setMake("apple");
        deviceBuilder.setModel("iPhone8,1");
        deviceBuilder.setConnectiontype(MomoExchange.BidRequest.Device.ConnectionType.WIFI);


        MomoExchange.BidRequest.Builder requestBuilder = MomoExchange.BidRequest.newBuilder();
        requestBuilder.setId("201607211121450100060292071739");
        requestBuilder.setVersion("1.3");
        requestBuilder.addImp(impBuilder);
        requestBuilder.setApp(appBuilder);
        requestBuilder.setDevice(deviceBuilder);

        System.out.println(requestBuilder.build().toString());

//        String str = JsonFormat.printToString(requestBuilder.build());
//      HttpPost request = new HttpPost("http://172.16.25.131:8080/adcall/momo/bidrequest");
        HttpPost request = new HttpPost("http://localhost:8181/adcall/momo/bidrequest");
        request.setEntity(new ByteArrayEntity(requestBuilder.build().toByteArray()));
        request.setHeader("Content-Type", "application/x-protobuf");

        HttpClient client = HttpClients.createDefault();
        try {
            HttpResponse response = client.execute(request);

            System.out.println(response.getStatusLine().getStatusCode());

            if (response != null && (response.getStatusLine().getStatusCode() == 204 || response.getStatusLine().getStatusCode() == 200 || response.getStatusLine().getStatusCode() == 400)) {
                HttpEntity httpEntity = response.getEntity();
                byte[] bytes = EntityUtils.toByteArray(response.getEntity());
                MomoExchange.BidResponse bidResponse = MomoExchange.BidResponse.parseFrom(bytes);
                
                System.out.println(bidResponse.toString());
//              String title = bidResponse.getSeatbids(0).getAds(0).getCreative().getTitle();
//              String source = bidResponse.getSeatbids(0).getAds(0).getCreative().getSource();
//              System.out.println(title);
//              System.out.println(source);
//              System.out.println(bidResponse.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
