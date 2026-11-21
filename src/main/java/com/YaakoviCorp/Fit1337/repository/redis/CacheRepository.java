package com.YaakoviCorp.Fit1337.repository.redis;

public interface CacheRepository {
    void createCacheEntity(String key, String value);
    void updateCacheEntity(String key, String value);
    String getCacheEntity(String key);
    void removeCacheEntity(String key);
}
