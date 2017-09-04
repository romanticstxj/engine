package com.madhouse.media.momo;

import java.util.HashMap;
import java.util.Map;


public enum VideoTypeEnums {
    /**
     * FLV
     */
    FLV("flv"),

    /**
     * MP4
     */
    MP4("mp4");

    private String code;

    private static Map<String, VideoTypeEnums> valuesMap = new HashMap<>();

    static {
        valuesMap.put(FLV.code, FLV);
        valuesMap.put(MP4.code, MP4);
    }

    VideoTypeEnums(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    public static VideoTypeEnums getMessageTypeByCode(String code) {
        return valuesMap.get(code);
    }

    public static boolean contains(String code) {
        return valuesMap.containsKey(code);
    }

    public static VideoTypeEnums of(String code) {
        return valuesMap.get(code);
    }
}
