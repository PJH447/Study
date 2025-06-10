package com.demo.lucky_platform.web.kafka.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Service for producing messages to Kafka topics.
 * This service provides methods to send messages to configured Kafka topics
 * and handles success and error logging.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${spring.kafka.topic.name:my-topic}")
    private String topicName;

    /**
     * Sends a message to the configured Kafka topic.
     *
     * @param message The message to send
     * @return CompletableFuture containing the result of the send operation
     */
    public CompletableFuture<SendResult<String, String>> sendMessage(String message) {
        log.info("Sending message to topic {}: {}", topicName, message);
        return sendMessageInternal(topicName, message, "Failed to send message: {}");
    }

    /**
     * Sends a message to a specific Kafka topic.
     *
     * @param topic   The topic to send the message to
     * @param message The message to send
     * @return CompletableFuture containing the result of the send operation
     */
    public CompletableFuture<SendResult<String, String>> sendMessageToTopic(String topic, String message) {
        log.info("Sending message to specific topic {}: {}", topic, message);
        return sendMessageInternal(topic, message, "Failed to send message to topic " + topic + ": {}");
    }

    /**
     * Internal helper method to send a message to a Kafka topic and handle the result.
     *
     * @param topic        The topic to send the message to
     * @param message      The message to send
     * @param errorMessage The error message template to use if sending fails
     * @return CompletableFuture containing the result of the send operation
     */
    private CompletableFuture<SendResult<String, String>> sendMessageInternal(String topic, String message, String errorMessage) {
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, message);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error(errorMessage, ex.getMessage(), ex);
            } else {
                log.info("Message sent successfully: topic={}, partition={}, offset={}",
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            }
        });

        return future;
    }
}
