package com.demo.lucky_platform.web.kafka.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Service
public class KafkaConsumerService {

    @KafkaListener(topics = "my-topic", groupId = "group-id-temp")
    public void consume(String message) {
        System.out.println("receive message : " + message);
    }

}
