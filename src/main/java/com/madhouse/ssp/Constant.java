package com.madhouse.ssp;


import org.omg.CORBA.PUBLIC_MEMBER;

/**
 * Created by WUJUNFENG on 2017/5/22.
 */

public final class Constant {

    public static final class CommonKey {
        public static final String BID_RECORD = "str:%s:%s:%s:%s:bidrecord";

        public static final String TARGET_KEY = "set:%d:%s:policys";

        public static final String MEDIA_META_DATA = "str:media_meta_data";
        public static final String PLACEMENT_META_DATA = "str:plcmt_meta_data";
        public static final String ADBLOCK_META_DATA = "str:adblock_meta_data";
        public static final String POLICY_META_DATA = "str:policy_meta_data";
        public static final String DSP_META_DATA = "str:dsp_meta_data";
        public static final String MEDIA_MAPPING_DATA = "str:media_mapping_data";
        public static final String DSP_MAPPING_DATA = "str:dsp_mapping_data";

        public static final String POLICY_CONTORL_TOTAL = "str:%d:policy_total";
        public static final String POLICY_CONTORL_DAILY = "str:%d:%s:policy_daily";

        public static final String DSP_QPS_CONTROL = "str:%d:%d:max_qps";
    }

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

    public static final class BidAt {
        public static final int FIRST_PRICE = 1;
        public static final int SECOND_PRICE = 2;
        public static final int FIXED_PRICE = 3;
    }

    public static final class DSPApiType {
        public static final int MADHOUSE_PROTO = 1;
        public static final int MADHOUSE_JSON = 2;
    }

    public static final class DeliveryType {
        public static final int PDB = 1;
        public static final int PD = 2;
        public static final int PMP = 4;
        public static final int RTB = 8;
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
        public static final int VIDEO = 2;
        public static final int NATIVE = 3;
    }

    public static final class NativeLayout {
        public static final int PIC_1 = 301;
        public static final int PIC_2 = 302;
        public static final int PIC_3 = 303;
        public static final int VIDEO = 311;
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

    public static final class OSType {
        public static final int UNKNOWN = 0;
        public static final int ANDROID = 1;
        public static final int IOS = 2;
        public static final int WINDOWS_PHONE = 3;
    }

    public static final class DeviceType {
        public static final int UNKNOWN = 0;
        public static final int PHONE = 1;
        public static final int PAD = 2;
        public static final int BOX = 3;
        public static final int TV = 4;
        public static final int COMPUTER = 11;
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

    public static final class Test {
        public static final int REAL = 0;
        public static final int SIMULATION = 1;
        public static final int PING = 2;
    }

    public static final class TopicType {
        public static final String MEDIA_BID = "mediabid";
        public static final String DSP_BID = "dspbid";
        public static final String WINNOTICE = "winnotice";
        public static final String IMPRESSION = "impression";
        public static final String CLICK = "click";
    }

    public static final class TargetType {
        public static final int PLACEMENT = 1;
        public static final int WEEK_HOUR = 2;
        public static final int LOCATION = 4;
        public static final int OS = 8;
        public static final int CONNECTION_TYPE = 16;
    }

    public static final class PolicyControlType {
        public static final int NULL = 0;
        public static final int TOTAL = 1;
        public static final int DAY = 2;
    }
}
