package com.demo.lucky_platform.web.chat.dto;

public record CreateChatRequest(
        String senderEmail,
        String message
) {

}
