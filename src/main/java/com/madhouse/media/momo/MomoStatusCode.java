package com.madhouse.media.momo;

public final class MomoStatusCode {
    
    //"1"为iOS,"2"为安卓
    public static final class Os {
        public static final String OS_IOS = "1";
        public static final String OS_ANDROID = "2";
        
        //信息流是protobuf格式
        public static final String OS_IOS_P = "ios";
    }
    public static final class Type {
        public static final String PROTOBUF = "1";
        public static final String JSON = "2";
        
    }
    //网络类型//"WIFI" "CELL_UNKNOWN
    public static final class ConnectionType{
        public static final String WIFI = "WIFI";
        public static final String CELL_UNKNOWN = "CELL_UNKNOWN";
    }
}

