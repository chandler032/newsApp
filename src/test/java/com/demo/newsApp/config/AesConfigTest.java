package com.demo.newsApp.config;

import com.demo.newsApp.util.AESUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class AesConfigTest {

    @InjectMocks
    private AesConfig aesConfig;

    @Mock
    private AESUtil aesUtil;

    @BeforeEach
    public void setUp() {
        // Inject mock values into the AesConfig class
        ReflectionTestUtils.setField(aesConfig, "encryptedApiKey", "encryptedApiKeyValue");
        ReflectionTestUtils.setField(aesConfig, "secretKeyBase64", "secretKeyBase64Value");
    }

    @Test
    public void testGetDecryptedApiKey() throws Exception {
        // Arrange
        String expectedDecryptedApiKey = "decryptedApiKeyValue";
        try (MockedStatic<AESUtil> mockedAesUtil = Mockito.mockStatic(AESUtil.class)) {
            mockedAesUtil.when(() -> AESUtil.decrypt("encryptedApiKeyValue", "secretKeyBase64Value"))
                    .thenReturn(expectedDecryptedApiKey);

            // Act
            String decryptedApiKey = aesConfig.getDecryptedApiKey();

            // Assert
            assertEquals(expectedDecryptedApiKey, decryptedApiKey);
        }
    }
}