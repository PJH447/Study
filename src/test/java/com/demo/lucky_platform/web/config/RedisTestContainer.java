package com.demo.lucky_platform.web.config;

import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.DisplayName;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

@DisplayName("Redis Test Containers")
@Profile("test")
@Configuration
public class RedisTestContainer {

    private static final String REDIS_DOCKER_IMAGE = "redis:7.0.8-alpine";
    private static final String REDISSON_HOST_PREFIX = "redis://";
    private static final int REDIS_PORT = 6379;

    private static GenericContainer<?> redisContainer;
    private String redisHost;
    private Integer redisPort;

    static {
        redisContainer = new GenericContainer<>(DockerImageName.parse(REDIS_DOCKER_IMAGE))
                .withExposedPorts(REDIS_PORT)
                .withReuse(true);
        redisContainer.start();
    }

    @PostConstruct
    public void startRedisContainer() {

        redisHost = redisContainer.getHost();
        redisPort = redisContainer.getMappedPort(REDIS_PORT);

        System.setProperty("spring.data.redis.host", redisHost);
        System.setProperty("spring.data.redis.port", redisPort.toString());
    }

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        config.useSingleServer()
              .setAddress(REDISSON_HOST_PREFIX + redisHost + ":" + redisPort)
              .setSslEnableEndpointIdentification(true);

        return Redisson.create(config);
    }

}
