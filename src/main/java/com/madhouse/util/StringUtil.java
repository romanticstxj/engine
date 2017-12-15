package com.madhouse.util;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.IOUtils;

import org.apache.commons.lang3.StringUtils;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * Created by WUJUNFENG on 2017/6/9.
 */
public class StringUtil {

    public static final Random random = new Random();

    public static final String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
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
                sb.append("0");
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
            String text = base64Encode(data);

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

            return base64Decode(text);
        } catch (Exception ex) {
            System.err.println(ex.toString());
        }

        return null;
    }

    public static final String readFile(InputStream is) {
        try {
            return IOUtils.toString(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static final String getMD5(String str) {
        byte[] data = EncryptUtil.getMessageDigest(EncryptUtil.Type.MD5, str.getBytes());
        if (data != null) {
            return bytesToHex(data);
        }

        return null;
    }

    public static final String base64Encode(byte[] input) {
        if (input != null && input.length > 0) {
            return StringUtil.base64Encode(input, 0, input.length);
        }

        return null;
    }

    public static final String base64Encode(byte[] input, int off, int len) {
        if (input != null && input.length >= off + len) {
            if (off == 0 && len == input.length) {
                return new BASE64Encoder().encode(input).replace("\r\n", "");
            } else {
                byte[] buffer = new byte[len];
                System.arraycopy(input, off, buffer, 0, len);
                return new BASE64Encoder().encode(buffer).replace("\r\n", "");
            }
        }

        return null;
    }

    public static final String base64Encode(InputStream is) {
        if (is != null) {
            try {
                StringBuilder sb = new StringBuilder();

                int len = 0;
                byte[] buffer = new byte[3072];
                while ((len = is.read(buffer)) > 0) {
                    sb.append(StringUtil.base64Encode(buffer, 0, len));
                }

                return sb.toString();
            } catch (Exception ex) {
                System.err.println(ex.toString());
            }
        }

        return null;
    }

    public static final byte[] base64Decode(String str) {
        try {
            if (!StringUtils.isEmpty(str)) {
                return new BASE64Decoder().decodeBuffer(str);
            }
        } catch (Exception ex) {
            System.err.println(ex.toString());
        }

        return null;
    }

    public static final Date toDate(String date, String format) {
        try {
            SimpleDateFormat df = new SimpleDateFormat(format);
            return df.parse(date);
        } catch (Exception ex) {
            System.err.println(ex.toString());
        }

        return null;
    }

    public static final Date toDate(String date) {
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

    public static boolean isNumeric(String str) {
        if (str != null && !str.isEmpty()) {
            boolean result = true;

            for (int i = 0; i < str.length(); ++i) {
                if (str.charAt(i) < '0' || str.charAt(i) > '9') {
                    result = false;
                    break;
                }
            }

            return result;
        }

        return false;
    }

    public static boolean formatCheck(String regex, String text) {
        Pattern pattern = Pattern.compile(regex, Pattern.UNICODE_CASE);
        
        Matcher matcher = pattern.matcher(text);
        return matcher.matches();
    }

    public static String[] split(String text, String delim) {
        int pos = 0;
        List<String> result = new LinkedList<>();

        while ((pos = text.indexOf(delim)) >= 0) {
            result.add(text.substring(0, pos));
            text = text.substring(pos + delim.length());
        }

        result.add(text);
        return result.toArray(new String[result.size()]);
    }
}
