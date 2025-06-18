package com.demo.lucky_platform.web.kafka.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Kafka 토픽에서 메시지를 소비하는 서비스.
 * 이 서비스는 서로 다른 소비자 그룹을 가진 두 개의 소비자를 가지고 있어
 * 다양한 소비 패턴을 보여줍니다.
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
     * 첫 번째 소비자 그룹으로 구성된 Kafka 토픽에서 메시지를 소비합니다.
     *
     * @param message Kafka에서 수신한 메시지
     */
    @KafkaListener(topics = "${spring.kafka.topic.name:my-topic}",
            groupId = "${spring.kafka.consumer-groups.first:group-id-temp}")
    public void consumeWithFirstGroup(String message) {
        processMessage(message, 1, firstGroupId);
    }

    /**
     * 두 번째 소비자 그룹으로 구성된 Kafka 토픽에서 메시지를 소비합니다.
     *
     * @param message Kafka에서 수신한 메시지
     */
    @KafkaListener(topics = "${spring.kafka.topic.name:my-topic}",
            groupId = "${spring.kafka.consumer-groups.second:group-id-temp-2}")
    public void consumeWithSecondGroup(String message) {
        processMessage(message, 2, secondGroupId);
    }

    /**
     * Kafka에서 수신한 메시지를 처리합니다.
     *
     * @param message        Kafka에서 수신한 메시지
     * @param consumerNumber 소비자 번호 (로깅용)
     * @param groupId        소비자 그룹 ID
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
