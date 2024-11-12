package com.demo.lucky_platform.web.chat.service;

import com.demo.lucky_platform.web.chat.domain.Chat;
import com.demo.lucky_platform.web.chat.domain.ChatRoom;
import com.demo.lucky_platform.web.chat.dto.ChatMessageDto;
import com.demo.lucky_platform.web.chat.repository.ChatRepository;
import com.demo.lucky_platform.web.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatServiceImpl implements ChatService{

    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;

    @Transactional
    @Override
    public Long createChat(Long roomId, ChatMessageDto chatMessageDto) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                                               .orElseGet(() ->
                                                       chatRoomRepository.save(ChatRoom.builder()
                                                                                       .name("new ChatRoom")
                                                                                       .build())
                                               );

        Chat chat = Chat.builder()
                         .chatRoom(chatRoom)
                         .sender(chatMessageDto.getSender())
                         .senderEmail(chatMessageDto.getSenderEmail())
                         .message(chatMessageDto.getMessage())
                         .build();
        return chatRepository.save(chat).getId();
    }
}
