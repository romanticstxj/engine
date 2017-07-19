package com.madhouse.media.xtrader;

public final class XtraderStatusCode {
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
    /**
     * xtrader
        00 中国移动
        01 中国联通
        03 中国电信
     */
    public static final class Carrier   {
        public static final String CHINA_MOBILE  = "01";
        public static final String CHINA_UNICOM  = "02";
        public static final String CHINA_TELECOM = "03";
    }
    //0—未知，1—Ethernet，2—wifi，3—蜂窝网络，未知代，4—蜂窝网络，2G，5—蜂窝网络，3G，6—蜂窝网络，4G。
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

