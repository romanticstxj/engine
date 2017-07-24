package com.madhouse.dsp.iflytek;

public final class IflytekStatusCode {
    //  操作系统类型
    public static final class Os {
        public static final String OS_ANDROID = "Android";
        public static final String OS_IOS = "iOS";
        public static final String OS_WINDOWS_PHONE = "WP";
        public static final String OS_OTHERS = "Others";
    }
    public static final class Carrier   {
        public static final String CHINA_MOBILE  = "46000";
        public static final String CHINA_UNICOM  = "46001";
        public static final String CHINA_TELECOM = "46003";
    }
    //联网类型(0—未知，
    //1—Ethernet，2—wifi，
    //3—蜂窝网络，未知代， 4—，2G，5—蜂窝网络，3G，6—蜂窝网络，4G) 
    public static final class ConnectionType{
        public static final String UNKNOWN = "0";
        public static final String ETHERNET = "1";
        public static final String WIFI = "2";
        public static final String MOBILE_UNKNOWN = "3";
        public static final String _2G = "4";
        public static final String _3G = "5";
        public static final String _4G = "6";
    }
    //设备类型  Y       -1-未知 
    //0   - phone 
    //1   - pad 
    //2   - pc 
    //3   - tv 
    //4   - wap 
    public static final class DeviceType {
        public static final String UNKNOWN = "-1";
        public static final String PHONE = "0";
        public static final String PAD = "1";
        public static final String PC = "2";
        public static final String TV = "3";
        public static final String WAP = "4";
    }
    
    public static final String X_PROTOCOL_VER = "2.0";
    
    public static final String IFLYTEK_SUCESS_CODE = "70200";
}
