package com.madhouse.util;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

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

        ip = req.getRemoteAddr();
        return ip.equals("0:0:0:0:0:0:0:1") ? "127.0.0.1" : ip;
    }

    public static String getUserAgent(HttpServletRequest req) {
        String ua = req.getHeader("X-Real-UA");
        if (ua != null) {
            return ua;
        }

        return req.getHeader("User-Agent");
    }

    public static String getParameter(HttpServletRequest req, String param) {
        try {
            Map<String, String[]> params = req.getParameterMap();
            if (params.containsKey(param)) {
                return params.get(param)[0];
            }
        } catch (Exception ex) {
            System.err.println(ex.toString());
        }

        return "";
    }
}
