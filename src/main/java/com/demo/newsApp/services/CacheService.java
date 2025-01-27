package com.demo.newsApp.services;

public interface CacheService {
    void evictCache(String keyword) throws Exception;
    void clearAllCache();
}
