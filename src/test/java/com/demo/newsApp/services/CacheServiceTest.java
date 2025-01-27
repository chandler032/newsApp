package com.demo.newsApp.services;

import com.demo.newsApp.services.impl.CacheServiceImpl;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;

import java.util.Set;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class CacheServiceTest {

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache cache;

    @InjectMocks
    private CacheServiceImpl cacheService;

    @BeforeEach
    public void setUp() {
        // Create a mock CacheManager and Cache instance
        cacheManager = mock(CaffeineCacheManager.class);
        cache = mock(Cache.class);
        when(cacheManager.getCache("articles")).thenReturn(cache);

        // Initialize the CacheService with the mock CacheManager
        cacheService = new CacheServiceImpl(cacheManager);
    }

    @Test
    @SneakyThrows
    public void evictCache_shouldEvictCacheForSpecificKeyword() throws Exception {
        // Arrange
        String keyword = "apple";
        when(cacheManager.getCache("articles")).thenReturn(null);  // Simulate that cache is not available

        Exception exception = assertThrows(Exception.class, () -> {
            cacheService.evictCache(keyword);
        });
        // Assert
        assertEquals("No cache found for the keyword: apple", exception.getMessage());
    }

    @Test
    public void evictCache_shouldThrowExceptionIfCacheDoesNotContainKeyword() {
        // Arrange
        String keyword = "apple";
        when(cache.get(keyword)).thenReturn(null);  // Simulate that the cache does not contain the keyword

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> cacheService.evictCache(keyword));
        assertEquals("No cache found for the keyword: apple", exception.getMessage());

        // Ensure that the cache's evict method was not called
        verify(cache, times(0)).evict(keyword);
    }

    @Test
    public void clearAllCache_shouldClearAllCaches() {
        // Arrange
        when(cacheManager.getCacheNames()).thenReturn(Set.of("articles", "otherCache"));
        when(cacheManager.getCache("articles")).thenReturn(cache);
        Cache otherCache = mock(Cache.class);
        when(cacheManager.getCache("otherCache")).thenReturn(otherCache);

        // Act
        cacheService.clearAllCache();

        // Assert
        verify(cache, times(1)).clear();  // Ensure that the "articles" cache was cleared
        verify(otherCache, times(1)).clear();  // Ensure that the "otherCache" was cleared
    }

    @Test
    public void clearAllCache_shouldNotThrowErrorIfCacheIsNull() {
        // Arrange
        when(cacheManager.getCacheNames()).thenReturn(Set.of("articles"));
        when(cacheManager.getCache("articles")).thenReturn(null);  // Simulate a null cache

        // Act
        cacheService.clearAllCache();

        // Assert
        // The method should not throw any exception even if the cache is null
        verify(cacheManager, times(1)).getCacheNames();  // Ensure the cache names were fetched
    }
}
