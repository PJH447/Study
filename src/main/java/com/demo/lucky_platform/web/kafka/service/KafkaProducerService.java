package com.demo.lucky_platform.web.kafka.service;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String message) {
        System.out.println("send message : " + message);
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send("my-topic", message);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                System.err.println("메시지 전송 실패: " + ex.getMessage());
            } else {
                System.out.println("메시지 전송 성공: " + result.getRecordMetadata());
            }
        });
    }

}
