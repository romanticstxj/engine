package com.madhouse.media.sohu;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by wujunfeng on 2017-11-08.
 */
public class SohuConstant {
    public static Map<String, String> AdStyle = new HashMap<>();

    static {
        //开屏
        AdStyle.put("12224", "SPLASH");
        //文章页底部信息流
        AdStyle.put("12232", "BOTTOM");

        //小图信息流
        AdStyle.put("12355-140-112", "SMALL");
        AdStyle.put("12355-360-234", "SMALL");

        //大图信息流
        AdStyle.put("12355-640-396", "LARGE");
        AdStyle.put("12355-644-322", "LARGE");

        //视频信息流
        AdStyle.put("12355-656-370", "VIDEO");
    }
}