package com.madhouse.media.tencent;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;


public class TencentHandlerTest {

    public static void main(String[] args) {
        GPBForDSP.Request.Builder re = GPBForDSP.Request.newBuilder();
        re.setId("requestid001");
        re.setAt(2);

        GPBForDSP.Request.Impression.Builder imp = GPBForDSP.Request.Impression.newBuilder();

        imp.setId("impressionid001");
        imp.setTagid("App_Stream_news_news");
        imp.setBidfloor(300f);
        imp.setTradecode("003007;005001");
        imp.setDealid("20170816");

//        测试优先响应video时打开
//        GPBForDSP.Request.Impression.Video.Builder video = GPBForDSP.Request.Impression.Video.newBuilder();
//        video.setWidth(200);
//        video.setHeight(130);
//        video.addMimes("txt");


        GPBForDSP.Request.Impression.Banner.Builder banner = GPBForDSP.Request.Impression.Banner.newBuilder();
        banner.setWidth(200);
        banner.setHeight(130);
        banner.addMimes("txt");
        banner.setVisibility(0);
        // 测试banner最大尺寸功能
//        GPBForDSP.Request.Impression.MaterialFormat.Builder adm_require = GPBForDSP.Request.Impression.MaterialFormat.newBuilder();
//        adm_require.setWidth(320);
//        adm_require.setHeight(246);
//        adm_require.setMimes("jpg");
//
//        GPBForDSP.Request.Impression.MaterialFormat.Builder adm_require2 = GPBForDSP.Request.Impression.MaterialFormat.newBuilder();
//        adm_require2.setWidth(640);
//        adm_require2.setHeight(246);
//        adm_require2.setMimes("png");
//
//        GPBForDSP.Request.Impression.MaterialFormat.Builder adm_require3 = GPBForDSP.Request.Impression.MaterialFormat.newBuilder();
//        adm_require3.setWidth(1080);
//        adm_require3.setHeight(900);
//        adm_require3.setMimes("txt");

        imp.setBanner(banner);
//        imp.setVideo(video);
//        imp.addAdmRequire(adm_require);
//        imp.addAdmRequire(adm_require2);
//        imp.addAdmRequire(adm_require3);

        GPBForDSP.Request.Impression.DisplayType.Builder displya_type = GPBForDSP.Request.Impression.DisplayType.newBuilder();


        displya_type.setDisplayType(1);
        displya_type.addClickType(0);

        GPBForDSP.Request.Impression.MaterialFormat.Builder adm_require1 = GPBForDSP.Request.Impression.MaterialFormat.newBuilder();
        adm_require1.setWidth(200);
        adm_require1.setHeight(130);
        adm_require1.setMimes("txt");
        displya_type.addAdmRequire(adm_require1);

        imp.addDisplayType(displya_type);


        GPBForDSP.Request.Device.Builder device = GPBForDSP.Request.Device.newBuilder();

        device.setIp("1.14.128.0");
        device.setOs("android");
        device.setOsv("3.1.2");
        device.setUa("chrome");
        device.setCarrier(460000);
        device.setConnectiontype(0);
        device.setImei("imeitest0001");
        re.setDevice(device);


        GPBForDSP.Request.User.Builder user = GPBForDSP.Request.User.newBuilder();

        user.setId("userid0001");
        user.setBuyerid("buyerid0001");
        re.setUser(user);

        GPBForDSP.Request.App.Builder app = GPBForDSP.Request.App.newBuilder();

        app.setId("appid0001");
        app.setName("App_Stream_news_news");
        re.setApp(app);
        re.addImpression(imp);
        System.out.println(re.build().toString());

        HttpPost request = new HttpPost("http://localhost:8181/adcall/tencent/bidrequest");
        request.setEntity(new ByteArrayEntity(re.build().toByteArray()));

        HttpClient client = HttpClients.createDefault();
        try {
            HttpResponse response = client.execute(request);

            System.out.println(response.getStatusLine().getStatusCode());

            if (response != null && (response.getStatusLine().getStatusCode() == 204 || response.getStatusLine().getStatusCode() == 200 || response.getStatusLine().getStatusCode() == 400)) {
                HttpEntity httpEntity = response.getEntity();
                byte[] bytes = EntityUtils.toByteArray(response.getEntity());
                GPBForDSP.Response bidResponse = GPBForDSP.Response.parseFrom(bytes);

                System.out.println(bidResponse.toString());
//            String title = bidResponse.getSeatbids(0).getAds(0).getCreative().getTitle();
//            String source = bidResponse.getSeatbids(0).getAds(0).getCreative().getSource();
//            System.out.println(title);
//            System.out.println(source);
//            System.out.println(bidResponse.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
