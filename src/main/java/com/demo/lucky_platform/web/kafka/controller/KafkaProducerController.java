package com.demo.lucky_platform.web.kafka.controller;

import com.demo.lucky_platform.web.kafka.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class KafkaProducerController {

    private final KafkaProducerService kafkaProducerService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/api/v1/kafka/sendMessage")
    public void sedMessage(@RequestParam("message") String message) {
        kafkaProducerService.sendMessage(message);
    }
}
