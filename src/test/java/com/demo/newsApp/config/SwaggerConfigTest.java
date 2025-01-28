package com.demo.newsApp.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SwaggerConfigTest {

    @Test
    void testCustomOpenAPI() {
        // Arrange
        SwaggerConfig swaggerConfig = new SwaggerConfig();

        // Act
        OpenAPI openAPI = swaggerConfig.customOpenAPI();

        // Assert
        assertNotNull(openAPI, "OpenAPI bean should not be null");
        assertNotNull(openAPI.getInfo(), "Info object in OpenAPI should not be null");

        Info info = openAPI.getInfo();
        assertEquals("News Search API", info.getTitle(), "Title should match the configured value");
        assertEquals("API documentation for News Search Service", info.getDescription(), "Description should match the configured value");
        assertEquals("1.0", info.getVersion(), "Version should match the configured value");
    }
}