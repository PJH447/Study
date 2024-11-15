package com.demo.lucky_platform.web.chat.service;

import com.demo.lucky_platform.web.chat.domain.Chat;
import com.demo.lucky_platform.web.chat.dto.ChatMessageDto;
import com.demo.lucky_platform.web.chat.dto.CreateChatRequest;
import com.demo.lucky_platform.web.chat.repository.ChatRepository;
import com.demo.lucky_platform.web.user.domain.User;
import com.demo.lucky_platform.web.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
                        .user(sender)
                        .message(createChatRequest.message())
                        .targetUserId(targetUserId)
                        .build();

        chat.validateEmptyMessage();

        Chat _chat = chatRepository.save(chat);

        return ChatMessageDto.builder()
                             .chatId(_chat.getId())
                             .senderNickname(sender.getNickname())
                             .message(_chat.getMessage())
                             .createdAt(_chat.getCreatedAt())
                             .build();
    }

    @Override
    public Slice<ChatMessageDto> getChatessageSlice(final Long targetUserId, final Pageable pageable) {

        Slice<Chat> recentChat = chatRepository.findRecentChat(targetUserId, pageable);

        List<ChatMessageDto> list = recentChat.stream()
                                              .map(chat -> ChatMessageDto.builder()
                                                                         .chatId(chat.getId())
                                                                         .senderNickname(chat.getUser().getNickname())
                                                                         .message(chat.getMessage())
                                                                         .createdAt(chat.getCreatedAt())
                                                                         .build()
                                              )
                                              .collect(Collectors.toList());
        Collections.reverse(list);

        return new SliceImpl<>(list, recentChat.getPageable(), recentChat.hasNext());

    }
}
