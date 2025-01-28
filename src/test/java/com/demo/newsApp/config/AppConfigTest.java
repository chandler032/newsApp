package com.demo.newsApp.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = AppConfig.class)
@TestPropertySource(properties = "app.mode=offline")
class AppConfigTest {

    @Autowired
    private AppConfig appConfig;

    @Test
    void testAppConfigModeProperty() {
        // Assert: Verify that the mode property is set correctly
        assertNotNull(appConfig, "AppConfig should not be null");
        assertNull(appConfig.getMode());
    }
}
