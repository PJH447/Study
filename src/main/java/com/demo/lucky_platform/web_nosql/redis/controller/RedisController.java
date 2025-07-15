package com.demo.lucky_platform.web_nosql.redis.controller;

import com.demo.lucky_platform.web_nosql.redis.repository.RedisRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for testing Redis repository
 */
@RestController
@RequestMapping("/api/redis")
public class RedisController {

    private final RedisRepository redisRepository;

    public RedisController(RedisRepository redisRepository) {
        this.redisRepository = redisRepository;
    }

    /**
     * Set a key-value pair in Redis
     */
    @PostMapping("/set")
    public ResponseEntity<Map<String, Object>> setValue(
            @RequestParam String key,
            @RequestParam String value,
            @RequestParam(required = false) Long expiration) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            if (expiration != null) {
                redisRepository.set(key, value, expiration);
                response.put("message", "Value set with expiration: " + expiration + " seconds");
            } else {
                redisRepository.set(key, value);
                response.put("message", "Value set successfully");
            }
            
            response.put("key", key);
            response.put("value", value);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Get a value from Redis by key
     */
    @GetMapping("/get")
    public ResponseEntity<Map<String, Object>> getValue(@RequestParam String key) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String value = redisRepository.get(key);
            if (value != null) {
                response.put("key", key);
                response.put("value", value);
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "Key not found");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Delete a key-value pair from Redis
     */
    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, Object>> deleteValue(@RequestParam String key) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean deleted = redisRepository.delete(key);
            if (deleted) {
                response.put("message", "Key deleted successfully");
                response.put("key", key);
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "Key not found");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Check if a key exists in Redis
     */
    @GetMapping("/exists")
    public ResponseEntity<Map<String, Object>> keyExists(@RequestParam String key) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            boolean exists = redisRepository.hasKey(key);
            response.put("key", key);
            response.put("exists", exists);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}