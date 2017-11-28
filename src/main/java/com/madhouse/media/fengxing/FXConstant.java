package com.madhouse.media.fengxing;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wujunfeng on 2017-11-07.
 */
public class FXConstant {
	public static Map<String, String> TagId = new HashMap<>();

    static {
    	//OTT资源
    	TagId.put("ftv_bt_2", "OTT");
    	TagId.put("ftv_pr", "OTT");
    }
    public static class DeviceType {
        public static final int PHONE = 0;
        public static final int PAD = 1;
        public static final int PC = 2;
        public static final int TV = 3;
    }

    public static class ConnectionType {
        public static final int UNKNOWN = 0;
        public static final int ETHERNET = 1;
        public static final int WIFI = 2;
        public static final int CELL = 3;
        public static final int _2G = 4;
        public static final int _3G = 5;
        public static final int _4G = 6;
    }

    public static class Carrier {
        public static final String CHINA_MOBILE  = "46000";
        public static final String CHINA_UNICOM  = "46001";
        public static final String CHINA_TELECOM = "46003";
    }
}
