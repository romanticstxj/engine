package com.madhouse.util;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.io.IOUtils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * Created by WUJUNFENG on 2017/6/9.
 */
public class StringUtil {

    public static final Random random = new Random();

    public static final String getUUID() {
        return UUID.randomUUID().toString();
    }

    public static final String toString(String str) {
        return str == null ? "" : str;
    }

    public static final String toString(CharSequence str) {
        return str != null ? toString(str.toString()) : "";
    }

    public static final String bytesToHex(byte[] data) {
        StringBuilder sb = new StringBuilder();

        for (byte b : data) {
            String str = Integer.toString(b & 0xff, 16);
            if (str.length() < 2) {
                str = "0" + str;
            }

            sb.append(str);
        }

        return sb.toString();
    }

    public static final byte[] hexToBytes(String hex) {
        try {
            if (hex.length() % 2 != 0) {
                return null;
            }

            byte[] data = new byte[hex.length() / 2];
            for (int i = 0; i < hex.length(); i += 2) {
                data[i / 2] = (byte)Integer.parseInt(hex.substring(i, i + 2), 16);
            }

            return data;
        } catch (Exception ex) {
            System.err.println(ex.toString());
            return null;
        }
    }

    public static final String urlSafeBase64Encode(byte[] data) {
        try {
            String text = new BASE64Encoder().encode(data);

            int pos = text.indexOf("=");
            if (pos > 0) {
                text = text.substring(0, pos);
            }

            return text.replace('+', '-').replace('/', '_');
        } catch (Exception ex) {
            System.err.println(ex.toString());
            return null;
        }
    }

    public static final byte[] urlSafeBase64Decode(String str) {
        try {
            String text = str;

            text.replace('-', '+');
            text.replace('_', '/');

            int mod = text.length() % 4;
            if (mod > 0) {
                text += new String("====").substring(mod);
            }

            return new BASE64Decoder().decodeBuffer(text);
        } catch (Exception ex) {
            System.err.println(ex.toString());
            return null;
        }
    }

    public static String readFile(InputStream is) {
        try {
            return IOUtils.toString(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getMD5(String str) {
        if (str != null) {
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                byte[] data = md.digest(str.getBytes());
                return bytesToHex(data);
            } catch (NoSuchAlgorithmException e) {
                System.err.println(e.toString());
            }
        }

        return null;
    }

    public static Date toDate(String date) {
        SimpleDateFormat df = null;

        if (date.contains("-")) {
            if (date.length() <= 10) {
                df = new SimpleDateFormat("yyyy-MM-dd");
            } else {
                df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            }
        } else if (date.contains("/")) {
            if (date.length() <= 10) {
                df = new SimpleDateFormat("yyyy/MM/dd");
            } else {
                df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            }
        } else {
            if (date.length() <= 8) {
                df = new SimpleDateFormat("yyyyMMdd");
            } else {
                df = new SimpleDateFormat("yyyyMMddHHmmss");
            }
        }

        try {
            return df.parse(date);
        } catch (Exception ex) {
            System.err.println(ex.toString());
        }

        return null;
    }
}
