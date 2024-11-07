package io.hhplus.ecommerce.app.application.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class CacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    public CacheService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void cacheDataWithTTL(String key, Object value, long ttlInMinutes) {
        redisTemplate.opsForValue().set(key, value, ttlInMinutes, TimeUnit.MINUTES);
    }

    public Object getCachedData(String key) {
        return redisTemplate.opsForValue().get(key);
    }
}
