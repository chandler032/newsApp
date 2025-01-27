package com.demo.newsApp.controller;

import com.demo.newsApp.services.impl.CacheServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CacheControllerTest {

    @Mock
    private CacheServiceImpl cacheService;

    @InjectMocks
    private CacheController cacheController;

    @BeforeEach
    void setUp() {
        reset(cacheService);
    }

    @Test
    void testClearCache_Success() throws Exception {
        // Arrange
        String keyword = "technology";
        doNothing().when(cacheService).evictCache(keyword);

        // Act
        String result = cacheController.clearCache(keyword).getBody();

        // Assert
        assertEquals("Cache cleared for keyword: " + keyword, result);
        verify(cacheService, times(1)).evictCache(keyword);
    }

    @Test
    void testClearCache_Exception() throws Exception {
        // Arrange
        String keyword = "invalidKeyword";
        String errorMessage = "Cache not found";
        doThrow(new RuntimeException(errorMessage)).when(cacheService).evictCache(keyword);

        // Act
        String result = cacheController.clearCache(keyword).getBody();

        // Assert
        assertEquals("Error clearing cache for keyword: " + keyword + " - " + errorMessage, result);
        verify(cacheService, times(1)).evictCache(keyword);
    }
}