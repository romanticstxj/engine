package com.madhouse.util;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;


import javax.servlet.http.HttpServletRequest;

import java.io.*;
import java.util.Map;

/**
 * Created by WUJUNFENG on 2017/6/9.
 */
public class HttpUtil {
    private static HttpClient httpClient = new HttpClient();

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
    
    public static String getRequestPostBytes(HttpServletRequest request)
        throws IOException {
        int contentLength = request.getContentLength();
        if (contentLength < 0) {
            return null;
        }
        byte buffer[] = new byte[contentLength];
        for (int i = 0; i < contentLength;) {
            
            int readlen = request.getInputStream().read(buffer, i, contentLength - i);
            if (readlen == -1) {
                break;
            }
            i += readlen;
        }
        return new String(buffer);
    }

    public static String downloadFile(String url, String localPath) {
        GetMethod getMethod = null;

        try {
            getMethod = new GetMethod(url);

            if (httpClient.executeMethod(getMethod) == HttpStatus.SC_OK) {
                String filePath = localPath;
                if (!filePath.endsWith("\\")) {
                    filePath += "\\";
                }

                String fileName = url;
                int pos = url.lastIndexOf("/");
                if (pos > 0) {
                    fileName = url.substring(pos + 1);
                }

                filePath += fileName;
                File file = new File(filePath);
                OutputStream outputStream = new FileOutputStream(file);
                outputStream.write(getMethod.getResponseBody());
                outputStream.flush();
                outputStream.close();

                return filePath;
            }
        } catch (Exception ex) {
            System.err.println(ex.toString());
        } finally {
            if (getMethod != null) {
                getMethod.releaseConnection();
            }
        }

        return null;
    }
}
