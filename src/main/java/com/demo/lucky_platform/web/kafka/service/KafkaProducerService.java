package com.demo.lucky_platform.web.kafka.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Kafka 토픽에 메시지를 생성하는 서비스.
 * 이 서비스는 구성된 Kafka 토픽에 메시지를 보내는 메서드를 제공하고
 * 성공 및 오류 로깅을 처리합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${spring.kafka.topic.name:my-topic}")
    private String topicName;

    /**
     * 구성된 Kafka 토픽에 메시지를 전송합니다.
     *
     * @param message 전송할 메시지
     * @return 전송 작업의 결과를 포함하는 CompletableFuture
     */
    public CompletableFuture<SendResult<String, String>> sendMessage(String message) {
        log.info("Sending message to topic {}: {}", topicName, message);
        return sendMessageInternal(topicName, message, "Failed to send message: {}");
    }

    /**
     * 특정 Kafka 토픽에 메시지를 전송합니다.
     *
     * @param topic   메시지를 전송할 토픽
     * @param message 전송할 메시지
     * @return 전송 작업의 결과를 포함하는 CompletableFuture
     */
    public CompletableFuture<SendResult<String, String>> sendMessageToTopic(String topic, String message) {
        log.info("Sending message to specific topic {}: {}", topic, message);
        return sendMessageInternal(topic, message, "Failed to send message to topic " + topic + ": {}");
    }

    /**
     * Kafka 토픽에 메시지를 전송하고 결과를 처리하는 내부 헬퍼 메서드입니다.
     *
     * @param topic        메시지를 전송할 토픽
     * @param message      전송할 메시지
     * @param errorMessage 전송 실패 시 사용할 오류 메시지 템플릿
     * @return 전송 작업의 결과를 포함하는 CompletableFuture
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
