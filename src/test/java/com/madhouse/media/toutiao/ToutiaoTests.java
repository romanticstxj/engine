package com.madhouse.media.toutiao;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import junit.framework.Test;
import junit.framework.TestSuite;

public class ToutiaoTests {
    
    public static void main(String[] arg) {
        TOUTIAOAds.AdSlot.Banner.Builder bannerBuilder = TOUTIAOAds.AdSlot.Banner.newBuilder();
        bannerBuilder.setHeight(100);
        bannerBuilder.setWidth(640);
        bannerBuilder.setPos(TOUTIAOAds.AdSlot.Position.DETAIL);
        bannerBuilder.setSequence("21");

        TOUTIAOAds.Pmp.Deal.Builder deal=TOUTIAOAds.Pmp.Deal.newBuilder();
        deal.setId(1);
        deal.setBidFloor(3);
        
        
        TOUTIAOAds.AdSlot.Builder adSlotBuilder = TOUTIAOAds.AdSlot.newBuilder();
        adSlotBuilder.setId("3c7a762982484fb4");
  /*      adSlotBuilder.addAdType(TOUTIAOAds.AdType.TOUTIAO_FEED_APP_LARGE);
        adSlotBuilder.addAdType(TOUTIAOAds.AdType.TOUTIAO_FEED_APP_SMALL);
        adSlotBuilder.addAdType(TOUTIAOAds.AdType.TOUTIAO_FEED_LP_LARGE);*/
        adSlotBuilder.addAdType(TOUTIAOAds.AdType.TOUTIAO_FEED_LP_LARGE);
        //adSlotBuilder.addAdType(TOUTIAOAds.AdType.TOUTIAO_FEED_LP_GROUP);
        adSlotBuilder.addBanner(bannerBuilder);
        adSlotBuilder.setBidFloor(400);
        adSlotBuilder.setChannelId(Long.valueOf("12312313213"));

        adSlotBuilder.setPmp(TOUTIAOAds.Pmp.newBuilder().addDeals(deal));
        
        TOUTIAOAds.App.Builder appBuilder = TOUTIAOAds.App.newBuilder();
        appBuilder.setId("appid01");
//      appBuilder.setDomain("toutiao.com");
        appBuilder.setBundle("ll.com");
        appBuilder.setVer("50701");
        appBuilder.setName("news_article");
//      appBuilder.setPrivacypolicy(1);
//      appBuilder.setPaid(0);

        TOUTIAOAds.Geo.Builder geoBuilder = TOUTIAOAds.Geo.newBuilder();
        geoBuilder.setCity("上海");
        geoBuilder.setLat(114.3);
        geoBuilder.setLon(89.2);

        TOUTIAOAds.Device.Builder deviceBuilder = TOUTIAOAds.Device.newBuilder();
        deviceBuilder.setDnt(false);
        deviceBuilder.setUa("13213213134646513");
        deviceBuilder.setGeo(geoBuilder);
        deviceBuilder.setDeviceId("EC8E44F5-20E3-4DE8-943E-270D0EB734C8");
        deviceBuilder.setOs("ios");
        deviceBuilder.setIp("120.52.147.18");
        deviceBuilder.setOsv("9.3.2");
        deviceBuilder.setMake("apple");
        deviceBuilder.setModel("iPhone8,1");
        deviceBuilder.setConnectionType(TOUTIAOAds.Device.ConnectionType.WIFI);
        deviceBuilder.setDeviceType(TOUTIAOAds.Device.DeviceType.PHONE);

        TOUTIAOAds.User.Builder userBuilder = TOUTIAOAds.User.newBuilder();
        userBuilder.setId("12930060874");

       
        
        TOUTIAOAds.BidRequest.Builder requestBuilder = TOUTIAOAds.BidRequest.newBuilder();
        requestBuilder.setRequestId("asdfghjkl123456789");
        requestBuilder.setApiVersion("2.1");
        requestBuilder.addAdslots(adSlotBuilder);
        requestBuilder.addAdslots(adSlotBuilder);
        requestBuilder.setApp(appBuilder);
        requestBuilder.setDevice(deviceBuilder);
        requestBuilder.setUser(userBuilder);
        requestBuilder.setDspId(123);
        System.out.println(requestBuilder.build().toString());

//        String str = JsonFormat.printToString(requestBuilder.build());
        HttpPost request = new HttpPost("http://localhost:8181/adcall/toutiao/bidrequest");
//        HttpPost request = new HttpPost("http://localhost:8080/toutiao/bidrequest");
        request.setEntity(new ByteArrayEntity(requestBuilder.build().toByteArray()));

        HttpClient client = HttpClients.createDefault();
        try {
            HttpResponse response = client.execute(request);

            System.out.println(response.getStatusLine().getStatusCode());

            if (response != null && (response.getStatusLine().getStatusCode() == 204 || response.getStatusLine().getStatusCode() == 200 || response.getStatusLine().getStatusCode() == 400)) {
                HttpEntity httpEntity = response.getEntity();
                byte[] bytes = EntityUtils.toByteArray(response.getEntity());
                TOUTIAOAds.BidResponse bidResponse = TOUTIAOAds.BidResponse.parseFrom(bytes);
                
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
