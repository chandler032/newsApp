package com.demo.newsApp.config;

import com.demo.newsApp.util.AESUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AesConfig {
    @Value("${news.api.key}")
    private String encryptedApiKey;

    @Value("${news.api.secret-key}")
    private String secretKeyBase64;

    public String getDecryptedApiKey() throws Exception {
        return AESUtil.decrypt(encryptedApiKey,secretKeyBase64);
    }
}
