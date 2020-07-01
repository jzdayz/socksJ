package io.github.jzdayz.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AESUtil {

    private static final String KEY_AES = "AES";

    public static byte[] encrypt(byte[] data, String key) throws Exception {
        if (key == null || key.length() != 16) {
            throw new Exception("key不满足条件");
        }
        byte[] raw = key.getBytes();
        SecretKeySpec skeySpec = new SecretKeySpec(raw, KEY_AES);
        Cipher cipher = Cipher.getInstance(KEY_AES);
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        return cipher.doFinal(data);
    }

    public static byte[] decrypt(byte[] data, String key) throws Exception {
        if (key == null || key.length() != 16) {
            throw new Exception("key不满足条件");
        }
        byte[] raw = key.getBytes();
        SecretKeySpec skeySpec = new SecretKeySpec(raw, KEY_AES);
        Cipher cipher = Cipher.getInstance(KEY_AES);
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        return cipher.doFinal(data);
    }

    public static void main(String[] args) throws Exception {
        String content = "testContext";
        System.out.println("原内容 = " + content);
        byte[] encrypt = AESUtil.encrypt(content.getBytes(), "key_value_length");
        System.out.println("加密后 = " + new String(encrypt));
        byte[] decrypt = AESUtil.decrypt(encrypt, "key_value_length");
        System.out.println("解密后 = " + new String(decrypt));
    }

}