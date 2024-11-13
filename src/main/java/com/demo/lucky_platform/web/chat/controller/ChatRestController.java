package com.demo.lucky_platform.web.chat.controller;


import com.demo.lucky_platform.web.chat.dto.ChatMessageDto;
import com.demo.lucky_platform.web.chat.dto.CreateChatRequest;
import com.demo.lucky_platform.web.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatRestController {

    public final ChatService chatService;

    @MessageMapping("/{targetUserId}")
    @SendTo("/topic/{targetUserId}")
    public ChatMessageDto chat(@DestinationVariable Long targetUserId, CreateChatRequest createChatRequest) {
        return chatService.createChat(targetUserId, createChatRequest);
    }
}
