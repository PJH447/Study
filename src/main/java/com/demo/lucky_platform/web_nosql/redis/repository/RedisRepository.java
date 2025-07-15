package com.demo.lucky_platform.web_nosql.redis.repository;

/**
 * Redis key-value repository interface
 * Provides methods for basic CRUD operations on Redis
 * Supports both String values and serialized objects
 */
public interface RedisRepository {
    
    /**
     * Store a value with the given key
     * 
     * @param key the key
     * @param value the value to store
     */
    void set(String key, String value);
    
    /**
     * Store a value with the given key and expiration time
     * 
     * @param key the key
     * @param value the value to store
     * @param expirationInSeconds expiration time in seconds
     */
    void set(String key, String value, long expirationInSeconds);
    
    /**
     * Get the value for the given key
     * 
     * @param key the key
     * @return the value, or null if the key does not exist
     */
    String get(String key);
    
    /**
     * Delete the value for the given key
     * 
     * @param key the key
     * @return true if the key was deleted, false otherwise
     */
    boolean delete(String key);
    
    /**
     * Check if the key exists
     * 
     * @param key the key
     * @return true if the key exists, false otherwise
     */
    boolean hasKey(String key);
    
    /**
     * Set expiration time for the given key
     * 
     * @param key the key
     * @param expirationInSeconds expiration time in seconds
     * @return true if the expiration was set, false otherwise
     */
    boolean expire(String key, long expirationInSeconds);

    /**
     * Store an object with the given key
     * The object will be serialized using JSON
     * 
     * @param key the key
     * @param value the object to store
     */
    void setObject(String key, Object value);

    /**
     * Store an object with the given key and expiration time
     * The object will be serialized using JSON
     * 
     * @param key the key
     * @param value the object to store
     * @param expirationInSeconds expiration time in seconds
     */
    void setObject(String key, Object value, long expirationInSeconds);

    /**
     * Get the object for the given key
     * 
     * @param key the key
     * @param classType the class type of the object
     * @return the deserialized object, or null if the key does not exist
     */
    <T> T getObject(String key, Class<T> classType);
}