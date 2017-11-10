package com.madhouse.media.oppo;

import com.madhouse.util.FormatUtil;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;

public class OPPOBidRequestTest {

	public static void main(String[] args) throws IOException {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(URI.create("http://localhost:8181/adcall/oppo/bidrequest"));
		httpPost.setHeader("Content-Type","application/json");
		httpPost.setHeader("Accept-Encoding","application/gzip");
		httpPost.setHeader("x-openrtb-version","2.5");
		httpPost.setEntity(new StringEntity("{\n" +
				"    \"id\": \"80ce30c53c16e6ede735f123ef6e32361bfc7b22\",\n" +
				"    \"at\": 1,\n" +
				"    \"imp\": [\n" +
				"        {\n" +
				"            \"id\": \"3ef6e32361bfc7b22\",\n" +
				"            \"tagId\":\"tagid001\",\n" +
				"            \"bidfloor\": 13,\n" +
				"            \"native\": {\n" +
				"                \"request\": \"{\\\"native\\\":{\\\"ver\\\":\\\"1.1\\\",\\\"assets\\\":[{\\\"id\\\":122,\\\"required\\\":1,\\\"title\\\":{\\\"len\\\":\n" +
				"17},\\\"img\\\":{\\\"w\\\":200,\\\"h\\\":130}},{\\\"id\\\":123,\\\"required\\\":1,\\\"data\\\":{\\\"len\\\":8,\\\"type\\\":1}},{\\\"id\\\":124,\\\"required\\\":1,\\\"specificFee\n" +
				"ds\\\":{\\\"formatTypes\\\":[1,2,3]}}]}}\",\n" +
				"                \"ver\": \"1.1\"\n" +
				"            }\n" +
				"        }\n" +
				"    ],\n" +
				"    \"app\": {\n" +
				"        \"id\": \"agltb3B1Yi1pbmNyDAsSA0FwcBiJkfIUDA\",\n" +
				"        \"name\":\"appName\",\n" +
				"        \"bundle\": \"12345\",\n" +
				"        \"publisher\": {\n" +
				"            \"id\": \"agltb3B1Yi1pbmNyDAsSA0FwcBiJkfTUCV\",\n" +
				"            \"name\": \"oppo\"\n" +
				"        }\n" +
				"    },\n" +
				"    \"device\": {\n" +
				"    \t\"ua\":\"device.user-agent\",\n" +
				"        \"ip\": \"123.145.167.189\",\n" +
				"        \"make\": \"OPPO\",\n" +
				"        \"model\": \"OPPO R9\",\n" +
				"        \"os\": \"Android\",\n" +
				"        \"osv\": \"7.0\",\n" +
				"        \"connectiontype\": 3,\n" +
				"        \"devicetype\": 1,\n" +
				"        \"didmd5\":\"device.didmd5\"\n" +
				"    },\n" +
				"    \"user\": {\n" +
				"        \"id\": \"55816b39711f9b5acf3b90e313ed29e51665623f\"\n" +
				"    }\n" +
				"}"));
		CloseableHttpResponse execute = client.execute(httpPost);
		String response = EntityUtils.toString(execute.getEntity());

		System.out.println(FormatUtil.formatJson(response));
	}

}
