package com.demo.newsApp.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;


import static org.junit.jupiter.api.Assertions.*;

public class CacheConfigTest {

    private CacheConfig cacheConfig;
    private CacheManager cacheManager;

    @BeforeEach
    public void setUp() {
        cacheConfig = new CacheConfig();
        cacheManager = cacheConfig.cacheManager();
    }

    @Test
    public void testCacheManagerConfiguration() {
        // Verify the CacheManager is not null
        assertNotNull(cacheManager, "CacheManager should not be null");
        assertTrue(cacheManager instanceof CaffeineCacheManager, "CacheManager should be an instance of CaffeineCacheManager");

        // Verify the 'articles' cache exists
        Cache articlesCache = cacheManager.getCache("articles");
        assertNotNull(articlesCache, "Cache 'articles' should be available");
    }
}
