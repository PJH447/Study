package com.demo.lucky_platform.web.config;

import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * Test configuration for Kafka.
 * This configuration disables Kafka auto-configuration in the test profile
 * and provides mock beans for Kafka-related components to prevent the application
 * from trying to connect to a Kafka broker during tests.
 */
@Profile("test")
@Configuration
@EnableAutoConfiguration(exclude = KafkaAutoConfiguration.class)
public class KafkaTestConfig {

    /**
     * Creates a mock KafkaTemplate bean for testing.
     * This prevents the application from trying to connect to a real Kafka broker.
     * 
     * @return A mock KafkaTemplate
     */
    @Bean
    @Primary
    public KafkaTemplate<String, String> kafkaTemplate() {
        return Mockito.mock(KafkaTemplate.class);
    }
}
