package com.madhouse.util;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by WUJUNFENG on 2017/6/9.
 */
public class HttpUtil {
    public static String getRealIp(HttpServletRequest req) {
        String ip = req.getHeader("X-Real-IP");
        if (ip != null) {
            return ip;
        }

        ip = req.getHeader("X-Forwarded-For");
        if (ip != null) {
            int pos = ip.lastIndexOf(",");
            if (pos >= 0) {
                ip = ip.substring(pos);
            }

            return ip;
        }

        return req.getRemoteHost();
    }

    public static String getUserAgent(HttpServletRequest req) {
        String ua = req.getHeader("X-Real-UA");
        if (ua != null) {
            return ua;
        }

        return req.getHeader("User-Agent");
    }
}
