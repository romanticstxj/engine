package com.madhouse.media.momo;

import java.util.HashMap;
import java.util.Map;


public enum ImageTypeEnums {
    /**
     * JPG
     */
    JPG("jpg"),

    /**
     * PNG
     */
    PNG("png"),

    /**
     * GIF
     */
    GIF("gif");

    private String code;

    private static Map<String, ImageTypeEnums> valuesMap = new HashMap<>();

    static {
        valuesMap.put(JPG.code, JPG);
        valuesMap.put(PNG.code, PNG);
        valuesMap.put(GIF.code, GIF);
    }

    ImageTypeEnums(String code) {
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    public static ImageTypeEnums getMessageTypeByCode(String code) {
        return valuesMap.get(code);
    }

    public static boolean contains(String code) {
        return valuesMap.containsKey(code);
    }

    public static ImageTypeEnums of(String code) {
        return valuesMap.get(code);
    }
}
