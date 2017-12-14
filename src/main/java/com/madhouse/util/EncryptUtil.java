package com.madhouse.util;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by WUJUNFENG on 2017-11-21.
 */
public class EncryptUtil {
    public enum Type {
        MD5("MD5", "^[0-9a-f]{32}$"),
        SHA1("SHA1", "^[0-9a-f]{40}$"),
        HMAC_MD5("HmacMD5", "^[0-9a-f]{32}$"),
        HMAC_SHA1("HmacSHA1", "^[0-9a-f]{40}$");

        private String name;
        private String format;

        private Type(String name, String format) {
            this.name = name;
            this.format = format;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getFormat() {
            return format;
        }

        public void setFormat(String format) {
            this.format = format;
        }
    }

    public static byte[] getMessageDigest(Type type, InputStream is) {
        try {
            MessageDigest md = MessageDigest.getInstance(type.getName());

            int len = 0;
            byte[] buffer = new byte[4096];
            if ((len = is.read(buffer)) > 0) {
                md.update(buffer, 0, len);
            }

            return md.digest();
        } catch (Exception e) {
            System.err.println(e.toString());
        }

        return null;
    }

    public static byte[] getMessageDigest(Type type, byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance(type.getName());
            md.update(data);
            return md.digest();
        } catch (Exception e) {
            System.err.println(e.toString());
        }

        return null;
    }

    public static byte[] getHMAC(Type type, String key, InputStream is) {
        try {
            SecretKey secretKey = new SecretKeySpec(key.getBytes(), type.getName());
            Mac mac = Mac.getInstance(type.getName());
            mac.init(secretKey);

            int len = 0;
            byte[] data = new byte[4096];
            while ((len = is.read(data)) > 0) {
                mac.update(data, 0, len);
            }

            return mac.doFinal();
        } catch (Exception e) {
            System.err.println(e.toString());
        }

        return null;
    }

    public static byte[] getHMAC(Type type, String key, byte[] data) {
        try {
            SecretKey secretKey = new SecretKeySpec(key.getBytes(), type.getName());
            Mac mac = Mac.getInstance(type.getName());
            mac.init(secretKey);
            mac.update(data);
            return mac.doFinal();
        } catch (Exception e) {
            System.err.println(e.toString());
        }

        return null;
    }

    public static boolean formatCheck(Type type, String text) {
        Pattern pattern = Pattern.compile(type.getFormat(), Pattern.UNICODE_CASE);

        Matcher matcher = pattern.matcher(text);
        return matcher.matches();
    }
}
