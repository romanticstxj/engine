package com.madhouse.media.vamaker;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClients;

import junit.framework.Test;
import junit.framework.TestSuite;

public class VamakerTest {
    
    public static void main(String[] arg) {
        VamakerRTB.VamRequest.Builder reBuild = VamakerRTB.VamRequest.newBuilder();
        
        reBuild.setId("4k25zDk16C900bba");
        reBuild.setTMax(120);
        reBuild.setUserAgent("Mozilla/5.0(Windows;U;WindowsNT5.1;en-US");
        reBuild.setIp("114.80.24.23");
        reBuild.setLanguage("zh-CN");
        
        reBuild.setDeviceType(VamakerRTB.VamRequest.DEVICETYPE.MOBILE);
        reBuild.setMediaId(479);
        reBuild.setDomain("qq.com");
        reBuild.setPage("http://news.qq.com ");
        reBuild.setReferer("http://www.baidu.com/s?wd=%E8%85%BE%E8%AE%AF&tn=baidu&ie=utf-8&oq=tengxun&f=3&rsv_bp=1&rsv_sug3=9&rsv_sug4=671&rsv_sug1=9&rsv_sug2=0&rsp=1&inputT=4023&bs=%E6%96%B0%E6%B5%AA%E5%BE%AE%E5%8D%9A&rsv_spt=3");
        reBuild.addVertical(303);
        
        VamakerRTB.VamRequest.Mobile.Builder mobileBu = VamakerRTB.VamRequest.Mobile.newBuilder();
//      mobileBu.setAdspaceId(1740758639);//okos:1
        mobileBu.setAdspaceId(1919942637);//okos：
//      mobileBu.setAdspaceId(1604372794);//库里没配置
        mobileBu.setBidfloor(60);
        mobileBu.setWidth(210);
        mobileBu.setHeight(140);
//      mobileBu.setWidth(640);
//      mobileBu.setHeight(200);
        mobileBu.setBrand("samsung");
        mobileBu.setModel("N70");
        mobileBu.setOs(2);
        mobileBu.setOsVersion("dsaf");
        mobileBu.setImei("dafdsaf");
        mobileBu.setMac("dafdsaf");
        mobileBu.setIDFA("123456789123456789123456789123456789");//ios必须是36位
        mobileBu.setPgn("dafdsaf");
        mobileBu.setAppName("dafdsaf");
        mobileBu.setNetwork(1);
        mobileBu.setOperateId(2);
        
        reBuild.setVamMobile(mobileBu);
    
        System.out.println(reBuild.build().toString());

//        String str = JsonFormat.printToString(requestBuilder.build());
        HttpPost request = new HttpPost("http://localhost:8181/adcall/vam/bidrequest");
//        HttpPost request = new HttpPost("http://localhost:8080/ROOT/adcall/vam/bidrequest");
        request.setEntity(new ByteArrayEntity(reBuild.build().toByteArray()));

        HttpClient client = HttpClients.createDefault();
        try {
            HttpResponse response = client.execute(request);

            System.out.println("返回code:"+response.getStatusLine().getStatusCode());

//          if (response != null && (response.getStatusLine().getStatusCode() == 204 || response.getStatusLine().getStatusCode() == 200 || response.getStatusLine().getStatusCode() == 400)) {
                HttpEntity httpEntity = response.getEntity();
//              byte[] bytes = EntityUtils.toByteArray(response.getEntity());
//              TOUTIAOAds.BidResponse bidResponse = TOUTIAOAds.BidResponse.parseFrom(bytes);
//              VamRealtimeBidding.VamResponse bidResponse = VamRealtimeBidding.VamResponse.parseFrom(bytes);
                
//              System.out.println("打印："+bidResponse.toString());
//              String title = bidResponse.getSeatbids(0).getAds(0).getCreative().getTitle();
//              String source = bidResponse.getSeatbids(0).getAds(0).getCreative().getSource();
//              System.out.println(title);
//              System.out.println(source);
//              System.out.println(bidResponse.toString());
//          }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
