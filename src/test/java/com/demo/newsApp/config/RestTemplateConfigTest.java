package com.demo.newsApp.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

class RestTemplateConfigTest {

    @Test
    void testRestTemplateBeanCreation() {
        // Arrange
        RestTemplateConfig restTemplateConfig = new RestTemplateConfig();

        // Act
        RestTemplate restTemplate = restTemplateConfig.restTemplate();

        // Assert
        assertNotNull(restTemplate, "RestTemplate bean should not be null");
        assertTrue(restTemplate instanceof RestTemplate, "Bean should be an instance of RestTemplate");
    }
}