package com.demo.lucky_platform.web_nosql.redis.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
public class RedisRepositoryImpl implements RedisRepository {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisRepositoryImpl(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public void set(String key, String value, long expirationInSeconds) {
        redisTemplate.opsForValue().set(key, value, expirationInSeconds, TimeUnit.SECONDS);
    }

    @Override
    public String get(String key) {
        Object value = redisTemplate.opsForValue().get(key);
        return value != null ? value.toString() : null;
    }

    @Override
    public boolean delete(String key) {
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }

    @Override
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    @Override
    public boolean expire(String key, long expirationInSeconds) {
        return Boolean.TRUE.equals(redisTemplate.expire(key, expirationInSeconds, TimeUnit.SECONDS));
    }

    @Override
    public void setObject(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public void setObject(String key, Object value, long expirationInSeconds) {
        redisTemplate.opsForValue().set(key, value, expirationInSeconds, TimeUnit.SECONDS);
    }

    @Override
    public <T> T getObject(String key, Class<T> classType) {
        Object value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return null;
        }

        try {
            // If the value is already the correct type, return it
            if (classType.isInstance(value)) {
                return classType.cast(value);
            }

            // Otherwise, try to convert it using ObjectMapper
            return objectMapper.convertValue(value, classType);
        } catch (Exception e) {
            // Log the error and return null
            System.err.println("Error deserializing object: " + e.getMessage());
            return null;
        }
    }
}