package com.demo.lucky_platform.web.webSocket;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class MessageDto {
    private MessageType type;
    private String sessionId;
    private String message;
    private Map<String, Object> data;

    public MessageDto(MessageType type, String sessionId, String message, Map<String, Object> data) {
        this.type = type;
        this.sessionId = sessionId;
        this.message = message;
        this.data = data;
    }

    public enum MessageType {
        CONNECT,
        DISCONNECT,
        MESSAGE,
        ERROR
    }
}

