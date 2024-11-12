package com.demo.lucky_platform.web.chat.controller;


import com.demo.lucky_platform.web.chat.domain.Chat;
import com.demo.lucky_platform.web.chat.dto.ChatMessageDto;
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

    @MessageMapping("/{roomId}")
    @SendTo("/topic/{roomId}")
    public ChatMessageDto chat(@DestinationVariable Long roomId, ChatMessageDto chatMessageDto) {

        System.out.println("roomId = " + roomId);
        //채팅 저장
        Long chatId = chatService.createChat(roomId, chatMessageDto);
        return ChatMessageDto.builder()
                             .chatId(chatId)
                             .roomId(roomId)
                             .sender(chatMessageDto.getSender())
                             .senderEmail(chatMessageDto.getSenderEmail())
                             .message(chatMessageDto.getMessage())
                             .build();
    }
}
