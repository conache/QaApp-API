package com.project.qa.utils;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

public class EncryptUtils {

    private static StandardPBEStringEncryptor getEncryptor(String key) {
        StandardPBEStringEncryptor stringEncryptor = new StandardPBEStringEncryptor();
        stringEncryptor.setPassword(key);
        stringEncryptor.setAlgorithm("PBEWITHMD5ANDDES");
        return stringEncryptor;
    }

    public static String encrypt(String key, String value) {
        return getEncryptor(key).encrypt(value);
    }

    public static String decrypt(String key, String value) {
        return getEncryptor(key).decrypt(value);
    }


}
