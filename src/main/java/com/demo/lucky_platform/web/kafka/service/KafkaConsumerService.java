package com.demo.lucky_platform.web.kafka.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Service for consuming messages from Kafka topics.
 * This service has two consumers with different consumer groups
 * to demonstrate different consumption patterns.
 */
@Slf4j
@Service
public class KafkaConsumerService {

    @Value("${spring.kafka.topic.name:my-topic}")
    private String topicName;

    @Value("${spring.kafka.consumer-groups.first:group-id-temp}")
    private String firstGroupId;

    @Value("${spring.kafka.consumer-groups.second:group-id-temp-2}")
    private String secondGroupId;

    /**
     * Consumes messages from the configured Kafka topic with the first consumer group.
     *
     * @param message The message received from Kafka
     */
    @KafkaListener(topics = "${spring.kafka.topic.name:my-topic}",
            groupId = "${spring.kafka.consumer-groups.first:group-id-temp}")
    public void consume(String message) {
        processMessage(message, 1, firstGroupId);
    }

    /**
     * Consumes messages from the configured Kafka topic with the second consumer group.
     *
     * @param message The message received from Kafka
     */
    @KafkaListener(topics = "${spring.kafka.topic.name:my-topic}",
            groupId = "${spring.kafka.consumer-groups.second:group-id-temp-2}")
    public void consume2(String message) {
        processMessage(message, 2, secondGroupId);
    }

    /**
     * Processes a message received from Kafka.
     *
     * @param message        The message received from Kafka
     * @param consumerNumber The consumer number (for logging)
     * @param groupId        The consumer group ID
     */
    private void processMessage(String message, int consumerNumber, String groupId) {
        try {
            log.info("Consumer {} (group: {}) received message from topic {}: {}", consumerNumber, groupId, topicName, message);
            // Process the message here
        } catch (Exception e) {
            log.error("Error processing message in consumer {} (group: {}): {}", consumerNumber, groupId, e.getMessage(), e);
        }
    }
}
