package com.demo.lucky_platform.web.chat.repository;

import com.demo.lucky_platform.web.chat.domain.Chat;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface ChatDslRepository {

    Slice<Chat> findRecentChat(Long targetUserId, Pageable pageable);

    List<Chat> findLastChatListByUser(Pageable pageable);
}
