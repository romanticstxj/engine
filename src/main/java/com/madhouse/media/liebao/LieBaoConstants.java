package com.madhouse.media.liebao;

public class LieBaoConstants {
    class AdType {
        public static final float NATIVE_BIG = 1200f/628f;
        public static final float NATIVE_SMALL = 84f/84f;
        public static final float BANNER_IAB = 300f/250f;
        public static final float BANNER_OPEN = 480f/684f;
        /**
         * 横凭
         */
        public static final float VIDEO_HOR = 1280f/720f;
        /**
         * 竖屏
         */
        public static final float VIDEO_VER = 720f/1280f;
    }
    public class ConnectionType {
        public static final int UNKNOWN = 0;
        public static final int WIFI = 2;
    }
    public class DeviceType {
        public static final int UNKNOWN = 0;
        public static final int PHONE = 4;
        public static final int TABLET = 5;
    }
    public class ImgType{
        public static final int ICON = 1;
        public static final int LOGO = 2;
        public static final int MAIN_IGMAGE = 3;
        public static final int VAST_VIDEO = 4;
        public static final int BANNER = 5;
    }
}
