package com.demo.lucky_platform.web.chat.service;

import com.demo.lucky_platform.web.chat.dto.ChatMessageDto;
import com.demo.lucky_platform.web.chat.dto.CreateChatRequest;

public interface ChatService {

    ChatMessageDto createChat(Long targetUserId, CreateChatRequest createChatRequest);
}
