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
 * Kafka 프로듀서 작업을 위한 REST 컨트롤러.
 * 이 컨트롤러는 논블로킹 비동기 처리를 사용하여 Kafka 토픽에
 * 메시지를 보내기 위한 엔드포인트를 제공합니다.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/kafka")
@RequiredArgsConstructor
public class KafkaProducerController {

    private final KafkaProducerService kafkaProducerService;
    private static final long RESPONSE_TIMEOUT = 10000L; // 10 seconds

    /**
     * 비동기적으로 Kafka에 메시지를 전송합니다.
     *
     * @param message 전송할 메시지
     * @return 작업 상태를 포함하는 CommonResponse와 ResponseEntity를 담고 있는 DeferredResult
     */
    @PostMapping("/sendMessage")
    public DeferredResult<ResponseEntity<CommonResponse>> sendMessage(@RequestParam("message") String message) {
        log.info("Received request to send message: {}", message);

        DeferredResult<ResponseEntity<CommonResponse>> deferredResult =
                createDeferredTimeoutResult("Request timed out for message: " + message);

        // Process the message asynchronously
        CompletableFuture<SendResult<String, String>> future = kafkaProducerService.sendMessage(message);

        handleKafkaResult(future, deferredResult);

        return deferredResult;
    }

    /**
     * 비동기적으로 특정 Kafka 토픽에 메시지를 전송합니다.
     *
     * @param topic   메시지를 전송할 토픽
     * @param message 전송할 메시지
     * @return 작업 상태를 포함하는 CommonResponse와 ResponseEntity를 담고 있는 DeferredResult
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

        handleKafkaResult(future, deferredResult);

        return deferredResult;
    }

    /**
     * 타임아웃 처리가 포함된 DeferredResult를 생성합니다.
     *
     * @param timeoutMessage 타임아웃 시 로깅할 메시지
     * @return 구성된 DeferredResult
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
     * Kafka 전송 작업의 완료를 처리합니다.
     *
     * @param future            Kafka 전송 작업에서 반환된 CompletableFuture
     * @param deferredResult    작업 결과로 설정할 DeferredResult
     */
    private void handleKafkaResult(
            CompletableFuture<SendResult<String, String>> future,
            DeferredResult<ResponseEntity<CommonResponse>> deferredResult
            ) {
        future.whenComplete((result, ex) -> {
            if (ex != null) {
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
