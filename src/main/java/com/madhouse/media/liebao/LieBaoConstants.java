package com.madhouse.media.liebao;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LieBaoConstants {

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
        private static final Map<String, String> appName;

        static {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("com.cleanmaster.mguard_cn", "猎豹清理大师");
            hashMap.put("com.cleanmaster.security_cn", "猎豹安全大师");
            hashMap.put("com.ijinshan.browser_fast", "猎豹浏览器");
            hashMap.put("com.ijinshan.kbatterydoctor", "金山电池医生");
            appName = hashMap;
        }

        public static String getAppname(String bundle) {
            if (null == bundle) {
                return APPNAME;
            }
            String name = appName.get(bundle);
            return StringUtils.isEmpty(name) ? APPNAME : name;
        }
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

    public static class Assets {
        public static final int NO_NEED = 0;
        public static final int NEED = 1;
    }
}
