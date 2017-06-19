package com.madhouse.ssp;


/**
 * Created by WUJUNFENG on 2017/5/22.
 */

public final class Constant {

    public static final class StatusCode {
        public static final int OK  = 200;
        public static final int NO_CONTENT = 204;
        public static final int REDIRECT = 302;
        public static final int BAD_REQUEST = 400;
        public static final int REQUEST_TIMEOUT = 408;
        public static final int INTERNAL_ERROR = 500;
    }

    public static final class MeiaApiType {
        public static final int MADHOUSE = 1;
    }

    public static final class DSPApiType {
        public static final int MADHOUSE_PROTO = 1;
        public static final int MADHOUSE_JSON = 2;
    }

    public static final class TradingType {
        public static final int PDB = 1;
        public static final int PD = 2;
        public static final int RTB = 4;
    }

    public static final class MediaType {
        public static final int APP = 1;
        public static final int SITE = 2;
    }

    public static final class BidType {
        public static final int CPM = 1;
        public static final int CPC = 2;
    }

    public static final class PlcmtType {
        public static final int BANNER = 1;
        public static final int INSTL = 2;
        public static final int SPLASH = 3;
        public static final int VIDEO = 11;
        public static final int NATIVE = 21;
    }

    public static final class NativeLayout {
        public static final int PIC_1 = 501;
        public static final int PIC_2 = 502;
        public static final int PIC_3 = 503;
        public static final int VIDEO = 511;
    }

    public static final class NativeImageType {
        public static final int MAIN = 1;
        public static final int ICON = 2;
        public static final int COVER = 3;
    }

    public static final class NativeDescType {
        public static final int DESC = 1;
    }

    public static final class NBR {
        public static final int UNKNOWN = 0;
        public static final int TECHNICAL_ERROR = 1;
        public static final int INVALID_REQUREST = 2;
        public static final int KNOWN_WEB_SPIDER = 3;
        public static final int SUSPECTED_NON_HUMAN_TRAFFIC = 4;
        public static final int PROXY_IP = 5;
        public static final int UNSUPPORTED_DEVICE = 6;
        public static final int BLOCKED_MEDIA = 7;
        public static final int UNMATCHED_USER = 8;
    }

    public static final class Carrier {
        public static final int UNKNOWN = 0;
        public static final int CHINA_MOBILE = 1;
        public static final int CHINA_UNICOM = 2;
        public static final int CHINA_TELECOM = 3;
    }

    public static final class ConnectionType {
        public static final int UNKNOWN = 0;
        public static final int WIFI = 1;
        public static final int _2G = 2;
        public static final int _3G = 3;
        public static final int _4G = 4;
        public static final int CELL = 10;
        public static final int ETHERNET = 11;
    }

    public static final class DeviceType {
        public static final int ANDROID_PHONE = 1;
        public static final int ANDROID_PAD = 2;
        public static final int IPHONE = 3;
        public static final int IPAD = 4;
        public static final int WINDOWS_PHONE = 5;
        public static final int WINDOWS_PAD = 6;
        public static final int COMPUTER = 51;
        public static final int TV = 52;
        public static final int BOX = 53;
    }

    public static final class ActionType {
        public static final int NONE = 0;
        public static final int OPEN_IN_APP = 1;
        public static final int DOWNLOAD_APP = 2;
        public static final int OPEN_WITH_EXPLORER = 3;
        public static final int PHONE_CALL = 4;
    }

    public static final class APIFramework {
        public static final int VPAID_1_0 = 1;
        public static final int VPAID_2_0 = 2;
        public static final int MRAID_1 = 3;
        public static final int ORMMA = 4;
        public static final int MRAID_2 = 5;
    }

    public static final class VASTProtocols {
        public static final int VAST_1_0 = 1;
        public static final int VAST_2_0 = 2;
        public static final int VAST_3_0 = 3;
        public static final int VAST_1_0_WRAPPER = 4;
        public static final int VAST_2_0_WRAPPER = 5;
        public static final int VAST_3_0_WRAPPER = 6;
    }
}
