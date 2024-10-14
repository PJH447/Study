package com.demo.lucky_platform.config.other;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@EnableCaching
@Configuration
public class CacheConfig implements CachingConfigurer {

    public static class CacheKey {
        public static final long DEFAULT_TTL_MIN = 60;
        public static final long USER_TTL_MIN = 15;
        public static final long COUNSELOR_TTL_MIN = 30;
        public static final long SHORT_TTL_MIN = 5;
        public static final long VERY_SHORT_TTL_MIN = 2;
        public static final String USERS = "users";
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration configuration =
                RedisCacheConfiguration.defaultCacheConfig()
                                       .disableCachingNullValues()
                                       .entryTtl(Duration.ofMinutes(CacheKey.DEFAULT_TTL_MIN))
                                       .computePrefixWith(CacheKeyPrefix.simple())
                                       .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()));

        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        cacheConfigurations.put(CacheKey.USERS, RedisCacheConfiguration.defaultCacheConfig()
                                                                       .entryTtl(Duration.ofMinutes(CacheKey.USER_TTL_MIN)));

        return RedisCacheManager.RedisCacheManagerBuilder.fromConnectionFactory(redisConnectionFactory)
                                                         .cacheDefaults(configuration)
                                                         .withInitialCacheConfigurations(cacheConfigurations)
                                                         .build();

    }
}
