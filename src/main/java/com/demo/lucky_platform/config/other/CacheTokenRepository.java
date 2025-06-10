package com.demo.lucky_platform.config.other;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.Objects;

/**
 * Repository for caching tokens and other data in Redis.
 */
@Slf4j
@Component
public class CacheTokenRepository {
    private final ObjectMapper objectMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final ValueOperations<String, String> stringValueOperations;
    private final HashOperations<String, String, String> hashValueOperations;

    /**
     * Constructor for CacheTokenRepository.
     *
     * @param objectMapper        The ObjectMapper for JSON serialization/deserialization
     * @param stringRedisTemplate The StringRedisTemplate for Redis operations
     */
    public CacheTokenRepository(ObjectMapper objectMapper, StringRedisTemplate stringRedisTemplate) {
        this.objectMapper = objectMapper;
        this.stringRedisTemplate = stringRedisTemplate;
        this.stringValueOperations = stringRedisTemplate.opsForValue();
        this.hashValueOperations = stringRedisTemplate.opsForHash();
    }

    /**
     * Retrieves a string value from cache by key.
     *
     * @param key The cache key
     * @return The string value or null if not found
     */
    public String getString(String key) {
        if (!StringUtils.hasText(key)) {
            throw new IllegalArgumentException("Key cannot be null or empty");
        }
        return stringValueOperations.get(key);
    }

    /**
     * Retrieves an integer value from cache by key.
     *
     * @param key The cache key
     * @return The integer value
     * @throws IllegalArgumentException if the key doesn't exist or value is not a valid integer
     */
    public int getInteger(String key) {
        if (!StringUtils.hasText(key)) {
            throw new IllegalArgumentException("Key cannot be null or empty");
        }

        String value = stringValueOperations.get(key);
        if (value == null) {
            throw new IllegalArgumentException("No value found for key: " + key);
        }

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Value for key '" + key + "' is not a valid integer: " + value, e);
        }
    }

    /**
     * Stores a value in cache with the given key.
     *
     * @param key   The cache key
     * @param value The value to store
     */
    public void setString(String key, Object value) {
        if (!StringUtils.hasText(key)) {
            throw new IllegalArgumentException("Key cannot be null or empty");
        }
        stringValueOperations.set(key, String.valueOf(value));
    }

    /**
     * Stores a value in cache with the given key and expiration time.
     *
     * @param key      The cache key
     * @param value    The value to store
     * @param duration The expiration time in seconds
     */
    public void setStringWithExpiration(String key, Object value, Long duration) {
        if (!StringUtils.hasText(key) ) {
            throw new IllegalArgumentException("Key cannot be null or empty");
        }

        if (duration == null || duration <= 0) {
            throw new IllegalArgumentException("duration cannot be null or negative");
        }

        Duration expireDuration = Duration.ofSeconds(duration);
        stringValueOperations.set(key, String.valueOf(value), expireDuration);
    }

    /**
     * Deletes a value from cache by key.
     *
     * @param key The cache key to delete
     */
    public void delete(String key) {
        if (!StringUtils.hasText(key)) {
            throw new IllegalArgumentException("Key cannot be null or empty");
        }
        stringRedisTemplate.delete(key);
    }

    /**
     * Retrieves a hash value from cache by key and hash key.
     *
     * @param key     The cache key
     * @param hashKey The hash key
     * @return The string value or null if not found
     */
    public String getHashValue(String key, String hashKey) {
        if (!StringUtils.hasText(key) || !StringUtils.hasText(hashKey)) {
            throw new IllegalArgumentException("Key/HashKey cannot be null or empty");
        }
        return hashValueOperations.get(key, hashKey);
    }

    /**
     * Stores a hash value in cache with the given key and hash key.
     *
     * @param key     The cache key
     * @param hashKey The hash key
     * @param value   The value to store
     */
    public void setHashValue(String key, String hashKey, Object value) {
        if (!StringUtils.hasText(key) || !StringUtils.hasText(hashKey)) {
            throw new IllegalArgumentException("Key/HashKey cannot be null or empty");
        }
        hashValueOperations.put(key, hashKey, String.valueOf(value));
    }

    /**
     * Increments a counter value in cache by 1.
     *
     * @param key The cache key of the counter
     */
    public void increment(String key) {
        if (!StringUtils.hasText(key)) {
            throw new IllegalArgumentException("Key cannot be null or empty");
        }
        stringValueOperations.increment(key, 1L);
    }

    /**
     * Retrieves and deserializes a JSON object from cache by key.
     *
     * @param key   The cache key
     * @param clazz The class type to deserialize to
     * @param <T>   The type parameter
     * @return The deserialized object or null if not found or deserialization fails
     */
    public <T> T getObject(String key, Class<T> clazz) {
        if (!StringUtils.hasText(key) || clazz == null) {
            throw new IllegalArgumentException("Key/Class cannot be null or empty");
        }

        String json = stringValueOperations.get(key);
        if (json == null) {
            return null;
        }

        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize object from cache: key={}, type={}, error={}", 
                    key, clazz.getSimpleName(), e.getMessage());
            return null;
        }
    }

    /**
     * Serializes an object to JSON and stores it in cache with the given key.
     *
     * @param key    The cache key
     * @param object The object to serialize and store
     * @param <T>    The type parameter
     * @return true if the operation was successful, false otherwise
     */
    public <T> boolean setObject(String key, T object) {
        if (!StringUtils.hasText(key) || object == null) {
            throw new IllegalArgumentException("Key/Object cannot be null or empty");
        }

        try {
            String json = objectMapper.writeValueAsString(object);
            stringValueOperations.set(key, json);
            return true;
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize object to cache: key={}, type={}, error={}", 
                    key, object.getClass().getSimpleName(), e.getMessage());
            return false;
        }
    }

    /**
     * Serializes an object to JSON and stores it in cache with the given key and expiration time.
     *
     * @param key      The cache key
     * @param object   The object to serialize and store
     * @param duration The expiration time in seconds
     * @param <T>      The type parameter
     * @return true if the operation was successful, false otherwise
     */
    public <T> boolean setObjectWithExpiration(String key, T object, Long duration) {
        if (!StringUtils.hasText(key) || object == null || duration == null || duration <= 0) {
            log.warn("Invalid parameters for setObjectWithExpiration: key={}, object={}, duration={}", 
                    key, (object != null ? object.getClass().getSimpleName() : "null"), duration);
            return false;
        }

        try {
            String json = objectMapper.writeValueAsString(object);
            Duration expireDuration = Duration.ofSeconds(duration);
            stringValueOperations.set(key, json, expireDuration);
            return true;
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize object to cache with expiration: key={}, type={}, duration={}, error={}", 
                    key, object.getClass().getSimpleName(), duration, e.getMessage());
            return false;
        }
    }

    /**
     * Stores a hash value in cache with the given key and hash key, with an expiration time.
     *
     * @param key      The cache key
     * @param hashKey  The hash key
     * @param value    The value to store
     * @param duration The expiration time in seconds
     * @return true if the operation was successful, false otherwise
     */
    public boolean setHashValueWithExpiration(String key, String hashKey, Object value, Long duration) {
        if (!StringUtils.hasText(key) || !StringUtils.hasText(hashKey) || duration == null || duration <= 0) {
            log.warn("Invalid parameters for setHashValueWithExpiration: key={}, hashKey={}, duration={}", 
                    key, hashKey, duration);
            return false;
        }

        try {
            hashValueOperations.put(key, hashKey, String.valueOf(value));
            stringRedisTemplate.expire(key, Duration.ofSeconds(duration));
            return true;
        } catch (Exception e) {
            log.error("Failed to set hash value with expiration: key={}, hashKey={}, duration={}, error={}", 
                    key, hashKey, duration, e.getMessage());
            return false;
        }
    }

    /**
     * Checks if a key exists in the cache.
     *
     * @param key The cache key to check
     * @return true if the key exists, false otherwise
     */
    public boolean hasKey(String key) {
        if (!StringUtils.hasText(key)) {
            log.warn("Attempted to check existence with null or empty key");
            return false;
        }

        Boolean exists = stringRedisTemplate.hasKey(key);
        return Boolean.TRUE.equals(exists);
    }

    /**
     * Gets the remaining time to live for a key in seconds.
     *
     * @param key The cache key
     * @return The remaining time to live in seconds, -2 if the key does not exist, -1 if the key exists but has no expiration
     */
    public Long getExpiration(String key) {
        if (!StringUtils.hasText(key)) {
            log.warn("Attempted to get expiration with null or empty key");
            return -2L;
        }

        return stringRedisTemplate.getExpire(key);
    }

    /**
     * Extends the expiration time of a key.
     *
     * @param key      The cache key
     * @param duration The new expiration time in seconds
     * @return true if the expiration was set, false if the key does not exist or the operation failed
     */
    public boolean extendExpiration(String key, Long duration) {
        if (!StringUtils.hasText(key)) {
            throw new IllegalArgumentException("Key cannot be null or empty");
        }

        if (duration == null || duration <= 0) {
            throw new IllegalArgumentException("Duration cannot be null or empty");
        }

        if (!hasKey(key)) {
            log.warn("Attempted to extend expiration for non-existent key: {}", key);
            return false;
        }

        Boolean result = stringRedisTemplate.expire(key, Duration.ofSeconds(duration));
        return Boolean.TRUE.equals(result);
    }
}
