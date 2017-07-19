package com.madhouse.media.baofeng;

public final class BaoFengStatusCode {
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
    //运行商 移动46000；联通46001；电信46003
    public static final class Carrier   {
        public static final String CHINA_MOBILE  = "46000";
        public static final String CHINA_UNICOM  = "46001";
        public static final String CHINA_TELECOM = "46003";
    }
    // 0：unknow 1：wifi 2：2G/3G/4G
    public static final class ConnectionType{
        public static final int UNKNOWN = 0;
        public static final int WIFI = 2;
        public static final int _2G_3G_4G = 4;
    }
    //// 1 iphone 2 ipad 3 android
    public static final class Devicetype{
        public static final int IPHONE  = 1;
        public static final int IPAD  = 2;
        public static final int ANDROID  = 3;
    }
}

