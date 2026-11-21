package com.YaakoviCorp.Fit1337.repository.redis;

import com.YaakoviCorp.Fit1337.config.RedisDetailsConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.JedisPooled;

@Repository
public class CacheRepositoryImpl implements CacheRepository {

    @Autowired
    JedisPooled  jedisPooled;
    @Autowired
    RedisDetailsConfig redisDetailsConfig;

    @Override
    public void createCacheEntity(String key, String value) {
        if (!jedisPooled.exists(key)) {
            jedisPooled.setex(key, redisDetailsConfig.getTtl(), value);
        }
    }

    @Override
    public void updateCacheEntity(String key, String value) {
        if (jedisPooled.exists(key)) {
            jedisPooled.setex(key, redisDetailsConfig.getTtl(), value);
        }
    }

    @Override
    public String getCacheEntity(String key) {
        if (!jedisPooled.exists(key)) {
            return null;
        }
        return jedisPooled.get(key);
    }

    @Override
    public void removeCacheEntity(String key) {
        jedisPooled.del(key);
    }
}
