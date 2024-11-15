package com.demo.lucky_platform.web.chat.controller;


import com.demo.lucky_platform.web.chat.dto.ChatMessageDto;
import com.demo.lucky_platform.web.chat.dto.CreateChatRequest;
import com.demo.lucky_platform.web.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatRestController {

    private final ChatService chatService;
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @MessageMapping("talk.{targetUserId}")
    public void chat(@DestinationVariable Long targetUserId, CreateChatRequest createChatRequest) {
        log.info("chat");
        ChatMessageDto chat = chatService.createChat(targetUserId, createChatRequest);

        rabbitTemplate.convertAndSend(exchangeName, "*.user." + targetUserId, chat);
    }

    @MessageMapping("enter.{targetUserId}")
    public void enter(@DestinationVariable Long targetUserId) {
        log.info("enter");
        ChatMessageDto enter = ChatMessageDto.builder()
                                             .message("enter")
                                             .build();

        rabbitTemplate.convertAndSend(exchangeName, "enter.user." + targetUserId, enter);
    }

    @MessageMapping("exit.{targetUserId}")
    public void exit(@DestinationVariable Long targetUserId) {
        log.info("exit");
        ChatMessageDto exit = ChatMessageDto.builder()
                                             .message("exit")
                                             .build();

        rabbitTemplate.convertAndSend(exchangeName, "exit.user." + targetUserId, exit);
    }
}
