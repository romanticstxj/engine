package com.madhouse.media.liebao;

import java.util.ArrayList;
import java.util.List;

public class LieBaoConstants {
    public static class AdType {
        public static final float NATIVE_BIG = (float) Math.round(1200f / 628f * 10) / 10;
        public static final float NATIVE_SMALL = (float) Math.round(84f / 84f * 10) / 10;
        public static final float BANNER_IAB = (float) Math.round(300f / 250f * 10) / 10;
        public static final float BANNER_OPEN = (float) Math.round(480f / 684f * 10) / 10;
        /**
         * 横凭
         */
        public static final float VIDEO_HOR = (float) Math.round(1280f / 720f * 10) / 10;
        /**
         * 竖屏
         */
        public static final float VIDEO_VER = (float) Math.round(720f / 1280f * 10) / 10;
    }

    public static class ConnectionType {
        public static final int UNKNOWN = 0;
        public static final int WIFI = 2;
    }

    public static class DeviceType {
        public static final int UNKNOWN = 0;
        public static final int PHONE = 4;
        public static final int TABLET = 5;
    }

    public static class ImgType {
        public static final int ICON = 1;
        public static final int LOGO = 2;
        public static final int MAIN_IGMAGE = 3;
        public static final int VAST_VIDEO = 4;
        public static final int BANNER = 5;
    }

    public static class App {
        public static final String APPNAME = "liebao";
        public static final String BUNDLE = "com.liebao";
    }

    /**
     * 货币标示，ISO­4217标准，支持一种
     * 国内：CNY
     * 海外：USD
     */
    public static class MoneyMark {
        public static final String CNY = "CNY";
        public static final String USD = "USD";
    }

    public static class Vast {
        private static final int INLINE_ONE = 1;
        private static final int INLINE_TWO = 2;
        private static final int INLINE_THREE = 3;
        private static final int WRAPPER_ONE = 4;
        private static final int WRAPPER_TWO = 5;
        private static final int WRAPPER_THREE = 6;
        public static final List<Integer> INLINE_LIST;
        public static final List<Integer> WRAPPER_LIST;

        static {
            ArrayList<Integer> inLineList = new ArrayList<>();
            inLineList.add(INLINE_ONE);
            inLineList.add(INLINE_TWO);
            inLineList.add(INLINE_THREE);
            INLINE_LIST = inLineList;
            ArrayList<Integer> wrapperList = new ArrayList<>();
            wrapperList.add(WRAPPER_ONE);
            wrapperList.add(WRAPPER_TWO);
            wrapperList.add(WRAPPER_THREE);
            WRAPPER_LIST = wrapperList;
        }
    }

    public static class Carrier {
        public static final String CHINA_MOBILE = "46000";
        public static final String CHINA_UNICOM = "46001";
        public static final String CHINA_TELECOM = "46003";
    }

    public static class MimeType {
        public static final List<String> IMAGE_JPEG;

        static {
            List<String> tmpList = new ArrayList<>();
            tmpList.add("jpe");
            tmpList.add("jpeg");
            tmpList.add("jpg");
            tmpList.add("jpz");
            IMAGE_JPEG = tmpList;
        }
    }

}
