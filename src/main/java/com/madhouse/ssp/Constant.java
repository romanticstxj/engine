package com.madhouse.ssp;

/**
 * Created by WUJUNFENG on 2017/5/22.
 */

public final class Constant {
    public static final String LOCATION_UNKNOWN = "1000000000";

    public static final class CommonKey {
        public static final String WORKER_ID = "str:premiummad:worker_id";

        //deliverytype, targettype, targetinfo
        public static final String TARGET_KEY = "set:%d:%d:%s:policys";
        //valid check record
        public static final String BID_RECORD = "str:%s:%s:%s:%s:bid_record";
        public static final String IMP_RECORD = "str:%s:imp_record";
        public static final String CLK_RECORD = "str:%s:clk_record";

        //metadata cache
        public static final String ALL_MEDIA = "set:all_media";
        public static final String MEDIA_META_DATA = "str:%s:media_info";
        public static final String ALL_PLACEMENT = "set:all_plcmt";
        public static final String PLACEMENT_META_DATA = "str:%s:plcmt_info";
        public static final String ALL_ADBLOCK = "set:all_adblock";
        public static final String ADBLOCK_META_DATA = "str:%s:adblock_info";
        public static final String ALL_POLICY = "set:all_policy";
        public static final String POLICY_META_DATA = "str:%s:policy_info";
        public static final String ALL_DSP = "set:all_dsp";
        public static final String DSP_META_DATA = "str:%s:dsp_info";
        public static final String ALL_MATERIAL = "set:all_material";
        public static final String MATERIAL_META_DATA = "str:%s:material_info";
        public static final String MEDIA_MAPPING_DATA = "str:%s:media_mapping_info";
        public static final String DSP_MAPPING_DATA = "str:%s:%s:dsp_mapping_info";

        //engine control
        public static final String POLICY_CONTORL_TOTAL = "str:%d:policy_total";
        public static final String POLICY_CONTORL_DAILY = "str:%d:%s:policy_daily";
        public static final String DSP_QPS_CONTROL = "str:%d:%d:max_qps";
        //str:dspid,materialId,mediaId,adspaceId:adm
        public static final String MATERIAL_MAPPING_DATA = "str:%d:%s:%d:%d:adm";

        //blocked device
        public static final String ALL_MEDIA_WHITELIST = "set:all_media_whitelist";
        public static final String ALL_BLOCKED_DEVICE_IP = "set:all_blocked_ip";
        public static final String ALL_BLOCKED_DEVICE_IFA = "set:all_blocked_ifa";
        public static final String ALL_BLOCKED_DEVICE_DIDMD5 = "set:all_blocked_didmd5";
        public static final String ALL_BLOCKED_DEVICE_DPIDMD5 = "set:all_blocked_dpidmd5";
    }

    public static final class StatusCode {
        public static final int OK  = 200;
        public static final int NO_CONTENT = 204;
        public static final int REDIRECT = 302;
        public static final int BAD_REQUEST = 400;
        public static final int NOT_ALLOWED = 405;
        public static final int REQUEST_TIMEOUT = 408;
        public static final int INTERNAL_ERROR = 500;
    }

    public static final class DSPApiType {
        public static final int MAD_RTB = 1;
        public static final int MAD_API = 2;
        public static final int MAD_PROTO = 3;

        public static final int VAMAKER = 11;
        public static final int IFLYTEK = 12;
        public static final int PG = 13;
        public static final int REACHMAX = 14;
    }

    public static final class BidAt {
        public static final int FIRST_PRICE = 1;
        public static final int SECOND_PRICE = 2;
        public static final int FIXED_PRICE = 3;
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

    public static final class NativeDataType {
        public static final int NORMAL = 1;
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

    public static final class KafkaTopicType {
        public static final String KAFKA_MEDIA_BID = "KAFKA_MEDIA_BID";
        public static final String KAFKA_DSP_BID = "KAFKA_DSP_BID";
        public static final String KAFKA_WIN_NOTICE = "KAFKA_WIN_NOTICE";
        public static final String KAFKA_IMPRESSION = "KAFKA_IMPRESSION";
        public static final String KAFKA_CLICK = "KAFKA_CLICK";
    }

    public static final class TargetType {
        public static final int PLACEMENT = 1;
        public static final int DATE = 2;
        public static final int WEEKDAY_HOUR = 4;
        public static final int LOCATION = 8;
        public static final int OS = 16;
        public static final int CONNECTION_TYPE = 32;
    }

    public static final class PolicyControlType {
        public static final int NONE = 0;
        public static final int DAILY = 1;
        public static final int TOTAL = 2;
    }

    public static final class PolicyControlMethod {
        public static final int FAST = 1;
        public static final int AVERAGE = 2;
    }

    public static final class InvalidType {
        public static final int NO_REQUEST = -1;
        public static final int NORMAL = 0;
        public static final int EXPIRED = 1;
        public static final int DUPLICATE = 2;
    }

    public static final class AuctionType {
        public static final int PUBLIC_MARKETING = 0;
        public static final int PRIVATE_MARKETING = 1;
    }

    public static final class AuditMode {
        public static final int NONE = 0;
        public static final int PLATFORM = 1;
        public static final int MEDIA = 2;
    }

    public static final class GeoType {
        public static final int WGS84 = 0;
        public static final int GCJ02 = 1;
        public static final int BD09 = 2;
    }
}