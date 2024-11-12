package com.demo.lucky_platform.web.chat.service;

import com.demo.lucky_platform.web.chat.domain.Chat;
import com.demo.lucky_platform.web.chat.dto.ChatMessageDto;

public interface ChatService {

    Long createChat(Long roomId, ChatMessageDto chatMessageDto);
}
