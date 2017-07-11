package com.madhouse.util;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by WUJUNFENG on 2017/6/9.
 */
public class StringUtil {

    public static final Random random = new Random();

    public static final String getUUID() {
        return UUID.randomUUID().toString();
    }

    public static final String getString(String str) {
        return str == null ? "" : str;
    }

    public static final byte[] hex2Bytes(String hex) {
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
    /**
     * 返回一个给定范围的随机数
     */
    public static int randomId(int n) {
        if (n <= 0) {
            return -1;
        }

        return random.nextInt(n) + 1;
    }
    
    public static String validateString(String str) {
        return StringUtils.isEmpty(str) ? "" : str;
    }

}
