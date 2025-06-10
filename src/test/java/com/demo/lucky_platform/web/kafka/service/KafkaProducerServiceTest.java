package com.demo.lucky_platform.web.kafka.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the KafkaProducerService class.
 */
@ExtendWith(MockitoExtension.class)
public class KafkaProducerServiceTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private KafkaProducerService kafkaProducerService;

    private CompletableFuture<SendResult<String, String>> completableFuture;
    private final String DEFAULT_TOPIC = "my-topic";
    private final String CUSTOM_TOPIC = "custom-topic";

    @BeforeEach
    void setUp() {
        completableFuture = new CompletableFuture<>();
        // Set the topic name using reflection since we're not loading application properties in tests
        ReflectionTestUtils.setField(kafkaProducerService, "topicName", DEFAULT_TOPIC);
    }

    /**
     * Test successful message sending to the default topic.
     */
    @Test
    void sendMessage_Success() throws ExecutionException, InterruptedException {
        // Arrange
        String message = "Test message";
        SendResult<String, String> sendResult = mock(SendResult.class);
        when(kafkaTemplate.send(eq(DEFAULT_TOPIC), eq(message))).thenReturn(completableFuture);

        // Act
        CompletableFuture<SendResult<String, String>> resultFuture = kafkaProducerService.sendMessage(message);

        // Complete the future with success
        completableFuture.complete(sendResult);

        // Assert
        assertSame(sendResult, resultFuture.get());
        verify(kafkaTemplate, times(1)).send(DEFAULT_TOPIC, message);
    }

    /**
     * Test failure handling when sending to the default topic.
     */
    @Test
    void sendMessage_Failure() {
        // Arrange
        String message = "Test message";
        RuntimeException exception = new RuntimeException("Error sending message");
        when(kafkaTemplate.send(eq(DEFAULT_TOPIC), eq(message))).thenReturn(completableFuture);

        // Act
        CompletableFuture<SendResult<String, String>> resultFuture = kafkaProducerService.sendMessage(message);

        // Complete the future with failure
        completableFuture.completeExceptionally(exception);

        // Assert
        ExecutionException executionException = assertThrows(
                ExecutionException.class,
                resultFuture::get
        );
        assertEquals(exception, executionException.getCause());
        verify(kafkaTemplate, times(1)).send(DEFAULT_TOPIC, message);
    }

    /**
     * Test successful message sending to a custom topic.
     */
    @Test
    void sendMessageToTopic_Success() throws ExecutionException, InterruptedException {
        // Arrange
        String message = "Test message for custom topic";
        SendResult<String, String> sendResult = mock(SendResult.class);
        when(kafkaTemplate.send(eq(CUSTOM_TOPIC), eq(message))).thenReturn(completableFuture);

        // Act
        CompletableFuture<SendResult<String, String>> resultFuture =
                kafkaProducerService.sendMessageToTopic(CUSTOM_TOPIC, message);

        // Complete the future with success
        completableFuture.complete(sendResult);

        // Assert
        assertSame(sendResult, resultFuture.get());
        verify(kafkaTemplate, times(1)).send(CUSTOM_TOPIC, message);
    }

    /**
     * Test failure handling when sending to a custom topic.
     */
    @Test
    void sendMessageToTopic_Failure() {
        // Arrange
        String message = "Test message for custom topic";
        RuntimeException exception = new RuntimeException("Error sending message to custom topic");
        when(kafkaTemplate.send(eq(CUSTOM_TOPIC), eq(message))).thenReturn(completableFuture);

        // Act
        CompletableFuture<SendResult<String, String>> resultFuture =
                kafkaProducerService.sendMessageToTopic(CUSTOM_TOPIC, message);

        // Complete the future with failure
        completableFuture.completeExceptionally(exception);

        // Assert
        ExecutionException executionException = assertThrows(
                ExecutionException.class,
                resultFuture::get
        );
        assertEquals(exception, executionException.getCause());
        verify(kafkaTemplate, times(1)).send(CUSTOM_TOPIC, message);
    }
}
