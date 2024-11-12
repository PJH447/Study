package com.demo.lucky_platform.web.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto {

    private Long chatId;
    private Long roomId;
    private String sender;
    private String senderEmail;
    private String message;

}
