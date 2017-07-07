package com.madhouse.media.mojiweather;

import java.util.HashMap;
import java.util.Map;

public enum MojiWeatherADStyle {
    /**
     * 时景顶部
     */
    STYLE1((Integer) 1),
    /**
     * 左抽屉
     */
    STYLE2((Integer) 2),
    /**
     * 中部banner
     */
    STYLE4((Integer) 4),
    /**
     * 底部banner、推送文章、运营文章（纯图）
     */
    STYLE8((Integer) 8),
    /**
     * 优惠券
     */
    STYLE16((Integer) 16),
    /**
     * 页面动态menu
     */
    STYLE32((Integer) 32),
    /**
     * 底部banner、推送文章、运营文章（图文）
     */
    STYLE64((Integer) 64),
    /**
     * 时景评论、空指评论
     */
    STYLE128((Integer) 128),
    /**
     * 每日详情
     */
    STYLE256((Integer) 256),
    /**
     * feed 流卡片入口
     */
    STYLE512((Integer) 512),

    /**
     * 语音播报下方
     */
    STYLE1024((Integer) 1024),

    /**
     * feed 信息流
     */
    STYLE2048((Integer) 2048),

    /**
     * feed 首页推荐
     */
    STYLE4096((Integer) 4096);

    private Integer code;

    private static Map<Integer, MojiWeatherADStyle> valuesMap = new HashMap<>();

    static {
        valuesMap.put(STYLE1.code, STYLE1);
        valuesMap.put(STYLE2.code, STYLE2);
        valuesMap.put(STYLE4.code, STYLE4);
        valuesMap.put(STYLE8.code, STYLE8);
        valuesMap.put(STYLE16.code, STYLE16);
        valuesMap.put(STYLE32.code, STYLE32);
        valuesMap.put(STYLE64.code, STYLE64);
        valuesMap.put(STYLE128.code, STYLE128);
        valuesMap.put(STYLE256.code, STYLE256);
        valuesMap.put(STYLE512.code, STYLE512);
        valuesMap.put(STYLE1024.code, STYLE1024);
        valuesMap.put(STYLE2048.code, STYLE2048);
        valuesMap.put(STYLE4096.code, STYLE4096);
    }

    MojiWeatherADStyle(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }

    public static MojiWeatherADStyle getMessageTypeByCode(int code) {
        return valuesMap.get(code);
    }

    public static boolean contains(int code) {
        return valuesMap.containsKey(code);
    }

    public static MojiWeatherADStyle of(int code) {
        return valuesMap.get(code);
    }
}
