package com.madhouse.media.autohome;


public class AutoHomeConstant {
    
    public static final String DEVICEID_KEY = "Q43eFLWSlfx1HmoE";
	public static class Os {
        public static final int IOS = 1;
        public static final int ANDROID = 2;
    }

    public static class ConnectionType {
        public static final int UNKNOWN = 0;
        public static final int WIFI = 1;
        public static final int _2G = 2;
        public static final int _3G = 3;
        public static final int _4G = 4;
    }

    public static class Carrier {
        public static final int CHINA_MOBILE  = 7012;
        public static final int CHINA_UNICOM  = 70121;
        public static final int CHINA_TELECOM = 70123;
    }
    public static class ContentType {
    	//gif  img  text  bimg  simg stext
        public static final String GIF  = "gif";
        public static final String IMG  = "img";
        public static final String TEXT = "text";
        public static final String BIMG = "bimg";
        public static final String SIMG = "simg";
        public static final String STEXT = "stext";
    }
}
