package com.madhouse.media.toutiao;

import java.util.HashSet;
import java.util.Set;

public final class ToutiaoConstant{
	
	//广告样式
	public static Set<Integer> ADTYPE = new HashSet<>();

    static {
    	//信息流大图
    	ADTYPE.add(1);
    	//信息流小图
    	ADTYPE.add(2);
    	//信息流落地页视频
    	ADTYPE.add(20);
    }
	
	public static final class OSType {
        public static final String IOS = "IOS";
        public static final String ANDROID = "ANDROID";
    }
	
    public static final class Carrier   {
        public static final String CHINA_MOBILE  = "China Mobile";
        public static final String  CHINA_UNICOM  = "China Unicom";
        public static final String  CHINA_TELECOM = "China Telecom";
    }
    public static final String URL  = "http://win.madserving.com/toutiao/winnotice?adspaceid={adspaceid}&uid={user_id}&request_id={request_id}&adid={adid}&price={bid_price}&ip={ip}&timestamp={timestamp}&did={did}" ;
}
