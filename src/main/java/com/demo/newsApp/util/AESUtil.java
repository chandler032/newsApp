package com.demo.newsApp.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class AESUtil {

    // Algorithm and padding
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String IV = "1234567890123456"; // 16 bytes IV

    /**
     * Encrypts a plain text using AES encryption.
     * @param plainText The text to encrypt.
     * @param key       The encryption key (16, 24, or 32 bytes).
     * @return Encrypted text in Base64 format.
     * @throws Exception If any encryption error occurs.
     */
    public static String encrypt(String plainText, String key) throws Exception {
        // Validate key length
        byte[] keyBytes = validateAndPadKey(key);
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        IvParameterSpec iv = new IvParameterSpec(IV.getBytes(StandardCharsets.UTF_8));
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);

        byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    /**
     * Decrypts an encrypted text using AES decryption.
     * @param encryptedText The Base64 encoded encrypted text.
     * @param key           The decryption key (16, 24, or 32 bytes).
     * @return Decrypted plain text.
     * @throws Exception If any decryption error occurs.
     */
    public static String decrypt(String encryptedText, String key) throws Exception {
        // Validate key length
        byte[] keyBytes = validateAndPadKey(key);
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "AES");

        Cipher cipher = Cipher.getInstance(ALGORITHM);
        IvParameterSpec iv = new IvParameterSpec(IV.getBytes(StandardCharsets.UTF_8));
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);

        byte[] decoded = Base64.getDecoder().decode(encryptedText);
        return new String(cipher.doFinal(decoded), StandardCharsets.UTF_8);
    }

    /**
     * Validates and pads the encryption key to the required length.
     * @param key The input key.
     * @return A valid key (16 bytes).
     */
    private static byte[] validateAndPadKey(String key) {
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);

        if (keyBytes.length < 16) {
            // Pad with zero bytes if key is too short
            byte[] paddedKey = new byte[16];
            System.arraycopy(keyBytes, 0, paddedKey, 0, keyBytes.length);
            return paddedKey;
        } else if (keyBytes.length > 16) {
            // Truncate if key is too long
            return new byte[]{keyBytes[0], keyBytes[1], keyBytes[2], keyBytes[3], keyBytes[4], keyBytes[5], keyBytes[6], keyBytes[7], keyBytes[8], keyBytes[9], keyBytes[10], keyBytes[11], keyBytes[12], keyBytes[13], keyBytes[14], keyBytes[15]};
        }
        return keyBytes;
    }
}
