package com.madhouse.media.momo;

import java.util.HashMap;
import java.util.Map;


public enum MomoNativeTypeEnums {
    /**
     * 大图样式落地页广告
     */
	FEED_LANDING_PAGE_LARGE_IMG("FEED_LANDING_PAGE_LARGE_IMG"),

    /**
     * 三图样式落地页广告
     */
	FEED_LANDING_PAGE_SMALL_IMG("FEED _LANDING_PAGE_SMALL_IMG"),

    /**
     * 图标样式落地页广告
     */
	NEARBY_LANDING_PAGE_NO_IMG("NEARBY_LANDING_PAGE_NO_IMG"),
	
	/**
	 * 单图样式落地页广告
	 */
	FEED_LANDING_PAGE_SQUARE_IMG("FEED_LANDING_PAGE_SQUARE_IMG"),
	
	/**
	 * 横版视频落地页广告
	 */
	FEED_LANDING_PAGE_VIDEO("FEED_LANDING_PAGE_VIDEO");
	

    private String code;

    private static Map<String, MomoNativeTypeEnums> valuesMap = new HashMap<>();

    static {
        valuesMap.put(FEED_LANDING_PAGE_LARGE_IMG.code, FEED_LANDING_PAGE_LARGE_IMG);
        valuesMap.put(FEED_LANDING_PAGE_SMALL_IMG.code, FEED_LANDING_PAGE_SMALL_IMG);
        valuesMap.put(NEARBY_LANDING_PAGE_NO_IMG.code, NEARBY_LANDING_PAGE_NO_IMG);
        valuesMap.put(FEED_LANDING_PAGE_SQUARE_IMG.code, FEED_LANDING_PAGE_SQUARE_IMG);
        valuesMap.put(FEED_LANDING_PAGE_VIDEO.code, FEED_LANDING_PAGE_VIDEO);
    }

    MomoNativeTypeEnums(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    public static MomoNativeTypeEnums getMessageTypeByCode(String code) {
        return valuesMap.get(code);
    }

    public static boolean contains(String code) {
        return valuesMap.containsKey(code);
    }

    public static MomoNativeTypeEnums of(String code) {
        return valuesMap.get(code);
    }
}
