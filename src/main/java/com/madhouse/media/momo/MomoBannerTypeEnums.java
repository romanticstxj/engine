package com.madhouse.media.momo;

import java.util.HashMap;
import java.util.Map;


public enum MomoBannerTypeEnums {
    /**
     * img
     */
	BANNER_SPLASH_IMG("BANNER_SPLASH_IMG"),

    /**
     * git
     */
	BANNER_SPLASH_GIF("BANNER_SPLASH_GIF"),

    /**
     * video
     */
	SPLASH_VIDEO("SPLASH_VIDEO");
	

	

    private String code;

    private static Map<String, MomoBannerTypeEnums> valuesMap = new HashMap<>();

    static {
        valuesMap.put(BANNER_SPLASH_IMG.code, BANNER_SPLASH_IMG);
        valuesMap.put(BANNER_SPLASH_GIF.code, BANNER_SPLASH_GIF);
        valuesMap.put(SPLASH_VIDEO.code, SPLASH_VIDEO);
       
    }

    MomoBannerTypeEnums(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    public static MomoBannerTypeEnums getMessageTypeByCode(String code) {
        return valuesMap.get(code);
    }

    public static boolean contains(String code) {
        return valuesMap.containsKey(code);
    }

    public static MomoBannerTypeEnums of(String code) {
        return valuesMap.get(code);
    }
}
