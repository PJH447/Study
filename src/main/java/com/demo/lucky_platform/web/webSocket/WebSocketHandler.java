package com.demo.lucky_platform.web.webSocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class WebSocketHandler extends TextWebSocketHandler {

    private final static ConcurrentHashMap<String, WebSocketSession> CLIENTS = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {

        String sessionId = session.getId();
        log.info("Connection established, session id={}", sessionId);

        try {
            CLIENTS.putIfAbsent(sessionId, session);
            log.info("Current session count: {}", CLIENTS.size());
            this.broadcastMessage(MessageDto.builder()
                                       .type(MessageDto.MessageType.CONNECT)
                                       .sessionId(sessionId)
                                       .message("New user connected")
                                       .build());
        } catch (Exception e) {
            log.error("Error during connection establishment for session {}: {}", sessionId, e.getMessage());
            throw e;
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        String sessionId = session.getId();
        log.error("Session transport error, session id={}, error={}", sessionId, exception.getMessage());

        try {
            this.removeSession(sessionId);
        } catch (Exception e) {
            log.error("Error handling transport error for session {}: {}", sessionId, e.getMessage());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String sessionId = session.getId();
        log.info("Connection closed, session id={}, close status={}", sessionId, status);

        try {
            this.removeSession(sessionId);
            this.broadcastMessage(MessageDto.builder()
                                       .type(MessageDto.MessageType.DISCONNECT)
                                       .sessionId(sessionId)
                                       .message("User disconnected")
                                       .build());
        } catch (Exception e) {
            log.error("Error during connection closure for session {}: {}", sessionId, e.getMessage());
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String sessionId = session.getId();
        String payload = message.getPayload();
        log.info("Received message, session id={}, message={}", sessionId, payload);

        try {
            MessageDto messageDto = MessageDto.builder()
                                              .type(MessageDto.MessageType.MESSAGE)
                                              .sessionId(sessionId)
                                              .message(payload)
                                              .build();
            messageDto.setSessionId(sessionId);
            this.broadcastMessage(messageDto);
        } catch (Exception e) {
            log.error("Error processing message from session {}: {}", sessionId, e.getMessage());
            this.sendErrorMessage(session, "Failed to process message: " + e.getMessage());
        }
    }

    private void broadcastMessage(MessageDto messageDto) {
        String payload;
        try {
            payload = objectMapper.writeValueAsString(messageDto);
            TextMessage textMessage = new TextMessage(payload);

            CLIENTS.forEach((id, ws) -> {
                try {
                    if (ws.isOpen()) {
                        ws.sendMessage(textMessage);
                    } else {
                        log.warn("Session {} is closed, removing from session map", id);
                        this.removeSession(id);
                    }
                } catch (IOException e) {
                    log.error("Failed to send message to session {}: {}", id, e.getMessage());
                }
            });
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize message: {}", e.getMessage());
        }
    }

    private void sendErrorMessage(WebSocketSession session, String errorMessage) {
        try {
            MessageDto errorDto = MessageDto.builder()
                                            .type(MessageDto.MessageType.ERROR)
                                            .message(errorMessage)
                                            .build();
            String payload = objectMapper.writeValueAsString(errorDto);
            session.sendMessage(new TextMessage(payload));
        } catch (Exception e) {
            log.error("Failed to send error message: {}", e.getMessage());
        }
    }

    private void removeSession(String sessionId) {
        CLIENTS.remove(sessionId);
        log.info("Session removed, current session count: {}", CLIENTS.size());
    }

}


