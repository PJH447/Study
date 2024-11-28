package com.demo.lucky_platform.web.chat.dto;

import lombok.Builder;

@Builder
public record ChatListDto(
        Long chatId,
        Long userId,
        String userNickname,
        String message
) {
}
