package com.madhouse.media.baidu;

import java.io.IOException;

import junit.framework.TestCase;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.googlecode.protobuf.format.JsonFormat;
import com.googlecode.protobuf.format.JsonFormat.ParseException;

public class BaiduHandlerTest extends TestCase {

	public static void main(String[] args) {
		Baidu.BidRequest.Builder bidRequest = Baidu.BidRequest.newBuilder();
		
		/*
		bidRequest.setId("0000000000001");
		
		
		Baidu.BidRequest.Imp.Builder imp = Baidu.BidRequest.Imp.newBuilder();
		
		imp.setId("1222");
		
		
		Baidu.BidRequest.Imp.Banner.Builder banner = Baidu.BidRequest.Imp.Banner.newBuilder();
		
		
		banner.setW(480);
		banner.setH(360);
		imp.setBanner(banner);
		
		
		bidRequest.addImp(imp);
		
		Baidu.BidRequest.Device.Builder device = Baidu.BidRequest.Device.newBuilder();
		
		device.setConnectiontype(Baidu.BidRequest.Device.ConnectionType.CELL_2G);
		device.setCarrier(1);
		device.setDevicetype(Baidu.BidRequest.Device.DeviceType.CONNECTED_TV);
		device.setOs("ios");
		device.setIdfa("4CFD11F0-09D0-4BF3-91CE-D50600BD0E64");
		device.setDpid("123132");
		
		bidRequest.setDevice(device);
		
		Baidu.BidRequest.App.Builder app = Baidu.BidRequest.App.newBuilder();
		app.setName("Baidu");
		app.setBundle("com.Baidu");
		
		
		bidRequest.setApp(app);*/
		
		
		
		
		
		
		
	     // CharSequence charsequence = "{\"badv\": [\"www.baidu.com\", \"www.google.com\"], \"app\": {\"content\": {\"id\": \"autotest_content_id\", \"title\": \"autotest_content_title\"}, \"publisher\": {\"cat\": [\"IAB1-5\", \"IAB1\"], \"id\": \"autotest_publisher_id\", \"name\": \"autotest_publisher_name\"}, \"domain\": \"http://app.video.baidu.com\", \"ver\": \"7.37.1\", \"name\": \"baiduvideo\", \"storeurl\": \"http://list.video.baidu.com/iph_promote.html\", \"id\": \"autotest_app_id\", \"bundle\": \"com.baidu.video\"}, \"imp\": [{\"pmp\": {\"deals\": [{\"bidfloorcur\": \"CNY\", \"id\": \"2\", \"bidfloor\": 0}]}, \"bidinfo\": [{\"bidfloor\": 800, \"bidtype\": 0}], \"banner\": {\"h\": 200, \"slottype\": 2, \"w\": 240}, \"id\": \"1010\"}], \"at\": \"SECOND_PRICE\", \"device\": {\"model\": \"FRD-AL00\", \"os\": \"Android\", \"didsha1\": \"afa46ab58f2cb938e135d70f2f1f9295d6ea0c72\", \"did\": \"862915036883441\", \"ip\": \"10.91.141.14\", \"make\": \"HUAWEI\", \"connectiontype\": \"WIFI\", \"osv\": \"24\", \"mac\": \"44:6e:e5:55:2b:e6\", \"carrier\": 2, \"devicetype\": \"PHONE\", \"w\": 1080, \"macmd5\": \"699d2faa1c1cb2806c9fc589c016cf66\", \"didmd5\": \"5ada5263bfd4c50f\", \"ua\": \"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36\", \"geo\": {\"country\": \"CN\", \"city\": \"\u5317\u4eac\", \"prov\": \"\u5317\u4eac\"}, \"macsha1\": \"9b3be5a255aa163e9e677d4bbc5843e80ca218d8\"}, \"id\": \"smh_test\", \"user\": {\"id\": \"DF168AB6743A94293A95F40ECD0F5DB4|144388630519268\"}}";
		CharSequence charsequence = "{\"badv\": [\"www.baidu.com\", \"www.google.com\"], \"app\": {\"content\": {\"id\": \"autotest_content_id\", \"title\": \"autotest_content_title\"}, \"publisher\": {\"cat\": [\"IAB1-5\", \"IAB1\"], \"id\": \"autotest_publisher_id\", \"name\": \"autotest_publisher_name\"}, \"domain\": \"http://app.video.baidu.com\", \"ver\": \"7.37.1\", \"name\": \"baiduvideo\", \"storeurl\": \"http://list.video.baidu.com/iph_promote.html\", \"id\": \"autotest_app_id\", \"bundle\": \"com.baidu.video\"}, \"imp\": [{\"bidinfo\": [{\"bidfloor\": 800, \"bidtype\": 0}], \"pmp\": {\"deals\": [{\"bidfloorcur\": \"CNY\", \"id\": \"2\", \"bidfloor\": 0}]}, \"id\": \"1035\", \"native\": {\"assets\": [{\"required\": true, \"id\": 0, \"title\": {\"len\": 30}}, {\"required\": true, \"id\": 1, \"img\": {\"h\": 360, \"type\": 1, \"w\": 480}}, {\"required\": false, \"data\": {\"type\": 2, \"len\": 30}, \"id\": 2}]}}], \"at\": \"SECOND_PRICE\", \"device\": {\"model\": \"FRD-AL00\", \"os\": \"Android\", \"didsha1\": \"afa46ab58f2cb938e135d70f2f1f9295d6ea0c72\", \"did\": \"862915036883441\", \"ip\": \"10.91.141.14\", \"make\": \"HUAWEI\", \"connectiontype\": \"WIFI\", \"osv\": \"24\", \"mac\": \"44:6e:e5:55:2b:e6\", \"carrier\": 2, \"devicetype\": \"PHONE\", \"w\": 1080, \"macmd5\": \"699d2faa1c1cb2806c9fc589c016cf66\", \"didmd5\": \"5ada5263bfd4c50f\", \"ua\": \"Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36\", \"geo\": {\"country\": \"CN\", \"city\": \"\u5317\u4eac\", \"prov\": \"\u5317\u4eac\"}, \"macsha1\": \"9b3be5a255aa163e9e677d4bbc5843e80ca218d8\"}, \"id\": \"smh_test\", \"user\": {\"id\": \"DF168AB6743A94293A95F40ECD0F5DB4|144388630519268\"}}";
		
		try {
			JsonFormat.merge(charsequence, bidRequest);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		
		
		
		
		
		
		System.out.println(bidRequest.build().toString());

        HttpPost request = new HttpPost("http://localhost:8181/adcall/baidu/bidrequest");
        request.setEntity(new ByteArrayEntity(bidRequest.build().toByteArray()));

        HttpClient client = HttpClients.createDefault();
        try {
            HttpResponse response = client.execute(request);

            System.out.println(response.getStatusLine().getStatusCode());

            if (response != null &&  response.getStatusLine().getStatusCode() == 200 ) {
                HttpEntity httpEntity = response.getEntity();
                byte[] bytes = EntityUtils.toByteArray(response.getEntity());
                Baidu.BidResponse bidResponse = Baidu.BidResponse.parseFrom(bytes);

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
