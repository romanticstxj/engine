package com.madhouse.media.sohu;

public final class SohuStatusCode {
    //终端类型(不区分大小写)： Mobile，PC 或者 Wap
    public static final class Devicetype{
        public static final String MOBILE = "Mobile";
        public static final String PC = "PC";
        public static final String WAP = "Wap";
    }
    
    //  移动设备的类型(不区分大小 写)，如 iPhone、iPad、AndroidPhone、AndroidPad。当Device type 为 Mobile 时有效 
    public static final class Os {
        public static final String OS_IPHONE = "iPhone";
        public static final String OS_IPAD = "iPad";
        public static final String OS_ANDROIDPHONE = "AndroidPhone";
        public static final String OS_ANDROIDPAD = "AndroidPad";
        
        public static final String IOS = "IOS";
        public static final String ANDROID = "ANDROID";
    }
    //网络类型(不区分大小写)：2G，3G，4G，WIFI 
    public static final class ConnectionType{
        public static final String WIFI = "WIFI";
        public static final String _2G = "2G";
        public static final String _3G = "3G";
        public static final String _4G = "4G";
    }
}

