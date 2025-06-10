package com.demo.lucky_platform.web.kafka.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KafkaProducerServiceTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private KafkaProducerService kafkaProducerService;

    private CompletableFuture<SendResult<String, String>> completableFuture;

    @BeforeEach
    void setUp() {
        completableFuture = new CompletableFuture<>();
    }

    @Test
    void sendMessage_Success() {
        // Arrange
        String message = "Test message";
        SendResult<String, String> sendResult = mock(SendResult.class);
        when(kafkaTemplate.send(eq("my-topic"), eq(message))).thenReturn(completableFuture);
        
        // Act
        kafkaProducerService.sendMessage(message);
        
        // Complete the future with success
        completableFuture.complete(sendResult);
        
        // Assert
        verify(kafkaTemplate, times(1)).send("my-topic", message);
    }

    @Test
    void sendMessage_Failure() {
        // Arrange
        String message = "Test message";
        Exception exception = new RuntimeException("Error sending message");
        when(kafkaTemplate.send(eq("my-topic"), eq(message))).thenReturn(completableFuture);
        
        // Act
        kafkaProducerService.sendMessage(message);
        
        // Complete the future with failure
        completableFuture.completeExceptionally(exception);
        
        // Assert
        verify(kafkaTemplate, times(1)).send("my-topic", message);
    }
}