package com.madhouse.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by JEFF on 2017/6/13.
 */
public class AESUtil {
    public static final class Algorithm {
        public static final String AES = "AES";
        public static final String AES_ECB_NoPadding = "AES/ECB/NoPadding";
        public static final String AES_ECB_PKCS5Padding = "AES/ECB/PKCS5Padding";
    }

    public static final byte[] decryptECB(byte[] data, byte[] key, String algorithm) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            return cipher.doFinal(data);
        } catch (Exception ex) {
            System.err.println(ex.toString());
            return null;
        }
    }

    public static final byte[] encryptECB(byte[] data, byte[] key, String algorithm) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            Cipher cipher = Cipher.getInstance(algorithm);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            return cipher.doFinal(data);
        } catch (Exception ex) {
            System.err.println(ex.toString());
            return null;
        }
    }
}
