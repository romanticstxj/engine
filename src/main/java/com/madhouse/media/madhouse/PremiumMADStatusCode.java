package com.madhouse.media.madhouse;

public final class PremiumMADStatusCode {
    public static final class StatusCode {
        //正常
        public static final int CODE_200 = 200;
        //网络超时，请重新发起请求
        public static final int CODE_400 = 400;
        //非法的广告位ID 
        public static final int CODE_401 = 401;
        //UID验证失败
        public static final int CODE_402 = 402;
        //UA验证失败
        public static final int CODE_403 = 403;
        //无效的用户IP
        public static final int CODE_404 = 404;
        //定向原因，暂时不被接收广告 
        public static final int CODE_405 = 405;
        //缺少必填参数
        public static final int CODE_406 = 406;
        //服务器IP非法
        public static final int CODE_407 = 407;
        //服务器繁忙
        public static final int CODE_500 = 500;
        //服务器错误
        public static final int CODE_501 = 501;
        //请求超时
        public static final int CODE_502 = 502;
    }
    public static final class PremiumMadOs {
        public static final String OS_ANDROID = "0";
        public static final String OS_IOS = "1";
        public static final String OS_WINDOWS_PHONE = "2";
        public static final String OS_OTHERS = "3";
    }
}
