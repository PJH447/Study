package com.demo.lucky_platform.web.chat.service;

import com.demo.lucky_platform.web.chat.domain.Chat;
import com.demo.lucky_platform.web.chat.dto.ChatMessageDto;
import com.demo.lucky_platform.web.chat.dto.CreateChatRequest;
import com.demo.lucky_platform.web.chat.repository.ChatRepository;
import com.demo.lucky_platform.web.user.domain.User;
import com.demo.lucky_platform.web.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatServiceImpl implements ChatService {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public ChatMessageDto createChat(final Long targetUserId, final CreateChatRequest createChatRequest) {
        User sender = userRepository.findByEmailAndEnabledIsTrue(createChatRequest.senderEmail())
                                    .orElseThrow(RuntimeException::new);

        Chat chat = Chat.builder()
                        .senderId(sender.getId())
                        .message(createChatRequest.message())
                        .targetUserId(targetUserId)
                        .build();
        Chat _chat = chatRepository.save(chat);

        return ChatMessageDto.builder()
                             .chatId(_chat.getId())
                             .senderNickname(sender.getNickname())
                             .message(_chat.getMessage())
                             .createdAt(_chat.getCreatedAt())
                             .build();
    }
}
