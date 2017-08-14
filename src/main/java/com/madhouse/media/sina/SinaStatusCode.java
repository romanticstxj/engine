package com.madhouse.media.sina;

public final class SinaStatusCode {
    //  操作系统类型
    public static final class Os {
        public static final String OS_ANDROID = "android";
        public static final String OS_IOS = "ios";
    }
    //运行商 移动46000；联通46001；电信46003
    public static final class Carrier   {
        public static final String CHINA_MOBILE  = "46000";
        public static final String CHINA_UNICOM  = "46001";
        public static final String CHINA_TELECOM = "46003";
        public static final String CHINA_CTT = "46020";
    }
    public static final class ConnectionType{
        public static final int UNKNOWN = 0;
        public static final int Ethernet = 1;
        public static final int WIFI = 2;
        public static final int MOBILE_UNKNOWN= 3;
        public static final int _2G = 4;
        public static final int _3G = 5;
        public static final int _4G = 6;
    }
}

