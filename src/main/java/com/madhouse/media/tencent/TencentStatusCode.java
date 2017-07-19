package com.madhouse.media.tencent;

public final class TencentStatusCode {
    //  操作系统类型
    public static final class Os {
        public static final String OS_ANDROID = "android";
        public static final String OS_IOS = "iphone";
    }
    //0: 明文，1 ：MD5, 2:SHA1 默认填0
    public static final class Encryption{
        public static final int EXPRESS = 0;
        public static final int MD5 = 1;
    }
    public static final class Carrier   {
        public static final int CHINA_MOBILE  = 46001;
        public static final int  CHINA_UNICOM  = 46002;
        public static final int  CHINA_TELECOM = 46003;
    }
    ////连接类型，0：未知; 1：以太网; 2：Wifi; 3：移动数据-未知; 4：2G; 5：3G; 6：4G
    public static final class ConnectionType{
        public static final int UNKNOWN = 0;
        public static final int Ethernet = 1;
        public static final int WIFI = 2;
        public static final int MOBILE_UNKNOWN = 3;
        public static final int _2G = 4;
        public static final int _3G = 5;
        public static final int _4G = 6;
    }
}

