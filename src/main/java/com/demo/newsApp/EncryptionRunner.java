package com.demo.newsApp;

import com.demo.newsApp.util.AESUtil;

public class EncryptionRunner {
    public static void main(String[] args) throws Exception {
        String plainApiKey = "ccaf5d41cc5140c984818c344edcc14d";
        String secretKey="1234567890123456";
        String encryptedApiKey = AESUtil.encrypt(plainApiKey,secretKey);
        System.out.println("Encrypted API Key: " + encryptedApiKey);
    }
}
