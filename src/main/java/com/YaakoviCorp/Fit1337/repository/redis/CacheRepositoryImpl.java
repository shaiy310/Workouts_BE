package com.YaakoviCorp.Fit1337.repository.redis;

import com.YaakoviCorp.Fit1337.config.RedisDetailsConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPooled;

@Repository
public class CacheRepositoryImpl implements CacheRepository {
    private static final Logger log = LoggerFactory.getLogger(CacheRepositoryImpl.class);

    @Autowired
    JedisPooled  jedisPooled;
    @Autowired
    RedisDetailsConfig redisDetailsConfig;

    @Override
    public void createCacheEntity(String key, String value) {
        if (!jedisPooled.exists(key)) {
            jedisPooled.setex(key, redisDetailsConfig.getTtl(), value);
            log.debug("repository=cache_create outcome=success key={}", key);
        } else {
            log.debug("repository=cache_create outcome=skipped reason=already_exists key={}", key);
        }
    }

    @Override
    public void updateCacheEntity(String key, String value) {
        if (jedisPooled.exists(key)) {
            jedisPooled.setex(key, redisDetailsConfig.getTtl(), value);
            log.debug("repository=cache_update outcome=success key={}", key);
        } else {
            log.debug("repository=cache_update outcome=skipped reason=not_found key={}", key);
        }
    }

    @Override
    public String getCacheEntity(String key) {
        if (!jedisPooled.exists(key)) {
            log.debug("repository=cache_read outcome=not_found key={}", key);
            return null;
        }
        String value = jedisPooled.get(key);
        log.debug("repository=cache_read outcome=success key={}", key);
        return value;
    }

    @Override
    public void removeCacheEntity(String key) {
        long removed = jedisPooled.del(key);
        log.debug("repository=cache_delete outcome={} key={}", removed > 0 ? "success" : "not_found", key);
    }
}
