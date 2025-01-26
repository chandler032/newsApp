package com.demo.newsApp.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AESUtilTests {

    @Test
    public void encrypt_shouldEncryptPlainTextWithValidKey() throws Exception {
        // Arrange
        String plainText = "Hello, World!";
        String key = "1234567890123456"; // 16-byte key

        // Act
        String encryptedText = AESUtil.encrypt(plainText, key);

        // Assert
        assertNotNull(encryptedText);
        assertNotEquals(plainText, encryptedText); // Encrypted text should not match plain text
    }

    @Test
    public void encrypt_shouldPadShortKey() throws Exception {
        // Arrange
        String plainText = "Hello, World!";
        String shortKey = "1234567890"; // 10-byte key (too short)

        // Act
        String encryptedText = AESUtil.encrypt(plainText, shortKey);

        // Assert
        assertNotNull(encryptedText);
        assertNotEquals(plainText, encryptedText); // Encrypted text should not match plain text
    }

    @Test
    public void encrypt_shouldTruncateLongKey() throws Exception {
        // Arrange
        String plainText = "Hello, World!";
        String longKey = "1234567890123456789012345678901234567890"; // 40-byte key (too long)

        // Act
        String encryptedText = AESUtil.encrypt(plainText, longKey);

        // Assert
        assertNotNull(encryptedText);
        assertNotEquals(plainText, encryptedText); // Encrypted text should not match plain text
    }

    @Test
    public void decrypt_shouldDecryptEncryptedTextWithValidKey() throws Exception {
        // Arrange
        String plainText = "Hello, World!";
        String key = "1234567890123456"; // 16-byte key
        String encryptedText = AESUtil.encrypt(plainText, key);

        // Act
        String decryptedText = AESUtil.decrypt(encryptedText, key);

        // Assert
        assertEquals(plainText, decryptedText); // Decrypted text should match plain text
    }

    @Test
    public void decrypt_shouldPadShortKey() throws Exception {
        // Arrange
        String plainText = "Hello, World!";
        String shortKey = "1234567890"; // 10-byte key (too short)
        String encryptedText = AESUtil.encrypt(plainText, shortKey);

        // Act
        String decryptedText = AESUtil.decrypt(encryptedText, shortKey);

        // Assert
        assertEquals(plainText, decryptedText); // Decrypted text should match plain text
    }

    @Test
    public void decrypt_shouldTruncateLongKey() throws Exception {
        // Arrange
        String plainText = "Hello, World!";
        String longKey = "1234567890123456789012345678901234567890"; // 40-byte key (too long)
        String encryptedText = AESUtil.encrypt(plainText, longKey);

        // Act
        String decryptedText = AESUtil.decrypt(encryptedText, longKey);

        // Assert
        assertEquals(plainText, decryptedText); // Decrypted text should match plain text
    }

    @Test
    public void decrypt_shouldThrowExceptionForInvalidEncryptedText() {
        // Arrange
        String invalidEncryptedText = "InvalidBase64Text";
        String key = "1234567890123456"; // 16-byte key

        // Act & Assert
        assertThrows(Exception.class, () -> {
            AESUtil.decrypt(invalidEncryptedText, key);
        });
    }
}