package com.demo.lucky_platform.web.chat.service;

import com.demo.lucky_platform.web.chat.dto.ChatMessageDto;
import com.demo.lucky_platform.web.chat.dto.CreateChatRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ChatService {

    ChatMessageDto createChat(Long targetUserId, CreateChatRequest createChatRequest);
    Slice<ChatMessageDto> getChatessageSlice(final Long targetUserId, final Pageable pageable);
}
