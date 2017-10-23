package com.madhouse.media.mojiweather;

public final class MojiWeather {
    public static final class StatusCode {
        /**
         * 正常
         */
        public static final int CODE_200 = 200;
        /**
         * 暂无投放中的广告 
         */
        public static final int CODE_400 = 400; 
        /**
         * 非法的广告位 ID 
         */
        public static final int CODE_401 = 401;
        /**
         * 缺少必要的参数 
         */
        public static final int CODE_402 = 402;
        /**
         * 服务器繁忙 
         */
        public static final int CODE_500 = 500;
        /**
         * 服务器错误
         */
        public static final int CODE_501 = 501;
    }
    //  操作系统类型
    public static final class OSType {
        public static final int ANDROID = 0;
        public static final int IOS = 1;
        public static final int WINDOWS_PHONE = 2;
        public static final int OTHERS = 3;
    }

    public static final class FeedType {
        public static final int FEED_TYPE_3 = 3;
        public static final int FEED_TYPE_5 = 5;
        public static final int FEED_TYPE_6 = 6;
    }
}
