package com.madhouse.media.sohu;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClients;



public class SohuTests {

    String[] searchList ={"{bid}","{requestid}","{adspaceid}","{cid}","{crea}","{adtype}","{pkgname}","{appname}","{conn}","{carrier}","{apitype}","{os}","{osv}","{imei}","{wma}","{aid}","{aaid}","{idfa}","{oid}","{uid}","{device}","{ua}","{ip}","{width}","{height}","{pid}","{pcat}","{media}","{debug}","{density}","{lon}","{lat}","{cell}","{mcell}","{province}","{city}","{ts}","{target}"};
    public static void main(String[] arg) {
        SohuRTB.Request.Builder requestBuild = SohuRTB.Request.newBuilder();
        requestBuild.setVersion(1);
        requestBuild.setBidid("c2e390b0ee301f011ce9b1ee0c5c44b002241704");
        requestBuild.setIsTest(1);
        requestBuild.addExcludeAdCategory("1");
        requestBuild.addExcludeAdCategory("2");
        requestBuild.addExcludeAdCategory("3");
        
        SohuRTB.Request.User.Builder user = SohuRTB.Request.User.newBuilder();
        user.setSuid("c2e390b0ee301f011ce9b1ee0c5c44b0");
        user.setVersion(1);
        user.addCategory(1);
        user.addCategory(2);
        user.addSearchKeyWords("1");
        requestBuild.setUser(user);
        
        SohuRTB.Request.Device.Builder device = SohuRTB.Request.Device.newBuilder();
        device.setType("0406");
        device.setIp("14.102.156.0");
        device.setUa("c2e390b0ee301f011ce9b1ee0c5c44b0|c02237869cfe0e931971ed953ba40432|||5.0.225");
        device.setCarrier("0");
        device.setNetType("2G");
        device.setMobileType("iphone");
        requestBuild.setDevice(device);
        
        SohuRTB.Request.Site.Builder site = SohuRTB.Request.Site.newBuilder();
        site.setName("SOHU_NEWS");
        site.setPage("25");
        site.setCategory(1);
        site.setRef("http://adv.madserving.com/material/1452753047414_0d765b3aeb3e71ba6be92fb499f6a688.mp4");
        requestBuild.setSite(site);
        
        SohuRTB.Request.Impression.Banner.Builder banner = SohuRTB.Request.Impression.Banner.newBuilder();
        banner.addMimes(1);
        banner.setWidth(320);
        banner.setHeight(480);
        banner.setTemplate("25");
        
        
        SohuRTB.Request.Impression.Builder imp = SohuRTB.Request.Impression.newBuilder();
        imp.setIdx(1);
        imp.setPid("12224");
        imp.setBidFloor(1400);
        imp.setIsPreferredDeals(true);
        imp.setCampaignId("20170607");
        imp.setLineId("700DF305C5B69149");
        imp.addAcceptAdvertisingType("1010000");
        imp.addAcceptAdvertisingType("102100");
        imp.setBanner(banner);
        requestBuild.addImpression(imp);
        
        System.out.println(requestBuild.build().toString());

//        String str = JsonFormat.printToString(requestBuilder.build());
//      HttpPost request = new HttpPost("http://ae.qa.madserving.com/adcall/sohu/bidrequest");
        HttpPost request = new HttpPost("http://localhost:8181/adcall/sohu/bidrequest");
        request.setEntity(new ByteArrayEntity(requestBuild.build().toByteArray()));

        HttpClient client = HttpClients.createDefault();
        try {
            HttpResponse response = client.execute(request);

            System.out.println(response.getStatusLine().getStatusCode());

//          if (response != null && (response.getStatusLine().getStatusCode() == 204 || response.getStatusLine().getStatusCode() == 200 || response.getStatusLine().getStatusCode() == 400)) {
//              HttpEntity httpEntity = response.getEntity();
//              byte[] bytes = EntityUtils.toByteArray(response.getEntity());
////                TOUTIAOAds.BidResponse bidResponse = TOUTIAOAds.BidResponse.parseFrom(bytes);
//              VamRealtimeBidding.VamResponse bidResponse = VamRealtimeBidding.VamResponse.parseFrom(bytes);
//              
////                System.out.println("打印："+bidResponse.toString());
////                String title = bidResponse.getSeatbids(0).getAds(0).getCreative().getTitle();
////                String source = bidResponse.getSeatbids(0).getAds(0).getCreative().getSource();
////                System.out.println(title);
////                System.out.println(source);
////                System.out.println(bidResponse.toString());
//          }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}