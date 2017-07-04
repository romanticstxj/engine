package com.madhouse.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

import com.madhouse.ssp.LoggerUtil;

public class ObjectUtils {
    public static boolean isNotEmpty(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof Collection) {
            if (((Collection)obj).size() < 1) {
                return false;
            }
        }
        if (obj instanceof Map) {
            if (((Map)obj).size() < 1) {
                return false;
            }
        }
        if (obj instanceof String) {
            if (StringUtils.isEmpty((String)obj)) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isEmpty(Object obj) {
        return !isNotEmpty(obj);
    }
    
    public static String ReadFile(String path) {
        File file = new File(path);
        BufferedReader reader = null;
        String laststr = "";
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                laststr += tempString;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return laststr;
    }
    public static String toEntityString(final HttpEntity entity) throws IOException {

        if (entity == null) {
            return null;
        }

        boolean needDecompress = false;

        Header ceheader = entity.getContentEncoding();
        if (ceheader != null) {
            HeaderElement[] codecs = ceheader.getElements();
            for (int i = 0; i < codecs.length; i++) {
                if (codecs[i].getName().equalsIgnoreCase("gzip")) {
                    needDecompress = true;
                }
            }
        }

        String outStr = "";
        if (needDecompress) {
            InputStream is = entity.getContent();
            GZIPInputStream gis = new GZIPInputStream(is);
            BufferedReader bufferedReader = null;
            try {
                bufferedReader = new BufferedReader(new InputStreamReader(gis));

                String line = null;
                while ((line = bufferedReader.readLine()) != null) {
                    outStr += line;
                }
            } finally {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            }
        } else {
            outStr = EntityUtils.toString(entity);
        }
        return outStr;
    }
    /**
     * 计算字符串的MD5
     */
    public static String getMD5(String str) {
        if (str != null) {
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                byte[] temp = md.digest(str.getBytes());
                return byteToHexString(temp);
            } catch (NoSuchAlgorithmException e) {
                LoggerUtil.getInstance().getPremiummadlogger().error(e.toString());
            }
        }
        return null;
    }
    /**
     * 将指定byte数组转换成32位字符串
     */
    public static String byteToHexString(byte[] b) {
        StringBuilder hexString = new StringBuilder();
        for (byte aB : b) {
            String hex = Integer.toString(aB & 0xFF, 16);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
