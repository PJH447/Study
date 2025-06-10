package com.demo.lucky_platform.web.kafka.controller;

import com.demo.lucky_platform.web.common.dto.CommonResponse;
import com.demo.lucky_platform.web.kafka.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.concurrent.CompletableFuture;

/**
 * REST controller for Kafka producer operations.
 * This controller provides endpoints for sending messages to Kafka topics
 * using non-blocking asynchronous processing.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/kafka")
@RequiredArgsConstructor
public class KafkaProducerController {

    private final KafkaProducerService kafkaProducerService;
    private static final long RESPONSE_TIMEOUT = 10000L; // 10 seconds

    /**
     * Sends a message to Kafka asynchronously.
     *
     * @param message The message to send
     * @return DeferredResult containing ResponseEntity with CommonResponse that includes operation status
     */
    @PostMapping("/sendMessage")
    public DeferredResult<ResponseEntity<CommonResponse>> sendMessage(@RequestParam("message") String message) {
        log.info("Received request to send message: {}", message);

        DeferredResult<ResponseEntity<CommonResponse>> deferredResult =
                createDeferredTimeoutResult("Request timed out for message: " + message);

        // Process the message asynchronously
        CompletableFuture<SendResult<String, String>> future = kafkaProducerService.sendMessage(message);

        handleKafkaResult(
                future,
                deferredResult,
                "Failed to send message: {}",
                "Message sent successfully"
        );

        return deferredResult;
    }

    /**
     * Sends a message to a specific Kafka topic asynchronously.
     *
     * @param topic   The topic to send the message to
     * @param message The message to send
     * @return DeferredResult containing ResponseEntity with CommonResponse that includes operation status
     */
    @PostMapping("/sendMessageToTopic")
    public DeferredResult<ResponseEntity<CommonResponse>> sendMessageToTopic(
            @RequestParam("topic") String topic,
            @RequestParam("message") String message) {

        log.info("Received request to send message to topic {}: {}", topic, message);

        DeferredResult<ResponseEntity<CommonResponse>> deferredResult =
                createDeferredTimeoutResult("Request timed out for message to topic " + topic + ": " + message);

        // Process the message asynchronously
        CompletableFuture<SendResult<String, String>> future = kafkaProducerService.sendMessageToTopic(topic, message);

        handleKafkaResult(
                future,
                deferredResult,
                "Failed to send message to topic " + topic + ": {}",
                "Message sent successfully to topic " + topic
        );

        return deferredResult;
    }

    /**
     * Creates a DeferredResult with timeout handling.
     *
     * @param timeoutMessage The message to log on timeout
     * @return A configured DeferredResult
     */
    private DeferredResult<ResponseEntity<CommonResponse>> createDeferredTimeoutResult(String timeoutMessage) {
        DeferredResult<ResponseEntity<CommonResponse>> deferredResult = new DeferredResult<>(RESPONSE_TIMEOUT);

        deferredResult.onTimeout(() -> {
            log.warn(timeoutMessage);
            CommonResponse response = CommonResponse.createErrorResponse("Request timed out after " + RESPONSE_TIMEOUT + "ms");
            deferredResult.setResult(ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body(response));
        });

        return deferredResult;
    }

    /**
     * Handles the completion of a Kafka send operation.
     *
     * @param future            The CompletableFuture returned by the Kafka send operation
     * @param deferredResult    The DeferredResult to set with the operation result
     * @param errorLogMessage   The message to log on error
     * @param successLogMessage The message to log on success
     */
    private void handleKafkaResult(
            CompletableFuture<SendResult<String, String>> future,
            DeferredResult<ResponseEntity<CommonResponse>> deferredResult,
            String errorLogMessage,
            String successLogMessage) {
        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error(errorLogMessage, ex.getMessage(), ex);

                String errorMsg;
                if (ex.getCause() instanceof InterruptedException) {
                    Thread.currentThread().interrupt();
                    errorMsg = "Message sending was interrupted";
                } else {
                    errorMsg = "Failed to send message: " + ex.getMessage();
                }

                CommonResponse errorResponse = CommonResponse.createErrorResponse(errorMsg);
                deferredResult.setResult(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse));
            } else {
                log.info(successLogMessage);

                String resultInfo = String.format(
                        "topic: %s, partition: %d, offset: %d",
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset()
                );

                CommonResponse successResponse = CommonResponse.createResponse(resultInfo);
                deferredResult.setResult(ResponseEntity.ok(successResponse));
            }
        });
    }
}
