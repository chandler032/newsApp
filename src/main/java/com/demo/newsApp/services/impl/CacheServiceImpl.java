package com.demo.newsApp.services.impl;

import com.demo.newsApp.services.CacheService;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
public class CacheServiceImpl implements CacheService {

    private final CacheManager cacheManager;

    public CacheServiceImpl(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public void evictCache(String keyword) throws Exception {
        Cache cache = cacheManager.getCache("articles");
        if (cache != null && cache.get(keyword) != null) {
            cache.evict(keyword); // Evict cache for the specific keyword
        } else {
            throw new Exception("No cache found for the keyword: " + keyword);  // Keyword not found in cache
        }
    }

    public void clearAllCache() {
        cacheManager.getCacheNames().forEach(cacheName -> {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
            }
        });
    }
}
