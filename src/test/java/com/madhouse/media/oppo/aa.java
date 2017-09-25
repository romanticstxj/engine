package com.madhouse.media.oppo;

import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.madhouse.media.oppo.OppoBidRequestTest;

public class aa {

	public static void main(String[] args) {
//		String aa ="{\"ver\":\"1.1\",\"assets\":[{\"id\":122,\"title\":{\"len\":17},\"required\":1},{\"id\":123,\"data\":{\"len\":8,\"type\":1},\"required\":1},{\"id\":124,\"specificFeeds\":{\"formatTypes\":[1,2,3]},\"required\":1}]}";
//     	OppoNativeRequest oppoNativeRequest = JSON.parseObject(aa, OppoNativeRequest.class);
//     	System.out.println(oppoNativeRequest.getAssets().get(0));
//     	System.out.println(oppoNativeRequest.getAssets().get(0));
//     	System.out.println(oppoNativeRequest.getAssets().get(0).getTitle().getLen());
		
		
		String nativeStr="{\\\"native\\\": {\\\"ver\\\": \\\"1.1\\\",\\\"assets\\\": [{\\\"id\\\": 122,\\\"required\\\": 1,\\\"title\\\": {\\\"len\\\": 17}},{\\\"id\\\": 123, \\\"required\\\": 1,\\\"data\\\": {\\\"len\\\": 8,\\\"type\\\": 1}},{\\\"id\\\": 124,\\\"required\\\": 1,\\\"specificFeeds\\\": {\\\"formatTypes\\\":[1,2,3]}}]}}";
	    String totalStr ="{\"id\":\"80cdwerfd451fds110\",\"at\":1,\"imp\":[{\"id\":\"3ef6e32361bf\",\"bidfloor\":13,\"tagid\":123,\"native\":{\"request\":\""+nativeStr+"\"}}]}";

	    OppoBidRequestTest bidRequest = JSON.parseObject(totalStr, OppoBidRequestTest.class);
	    String natives = bidRequest.getImp().get(0).getNatives().getRequest();
		Map nativeLast = JSON.parseObject(natives);  
		 for (Object obj : nativeLast.keySet()){  
			 if(obj.equals("native")){
	            	String abs =nativeLast.get(obj).toString();
	            	OppoNativeRequestTest oppoNativeRequest = JSON.parseObject(abs, OppoNativeRequestTest.class);
	            	System.out.println(oppoNativeRequest.getAssets().get(0));
	            	System.out.println(oppoNativeRequest.getAssets().get(0).getTitle());
	            	System.out.println(oppoNativeRequest.getAssets().get(0).getTitle().getLen());
			 }
		 } 
	}

}
