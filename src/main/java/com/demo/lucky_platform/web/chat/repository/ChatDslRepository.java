package com.demo.lucky_platform.web.chat.repository;

import com.demo.lucky_platform.web.chat.domain.Chat;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface ChatDslRepository {

    Slice<Chat> findRecentChat(Long targetUserId, Pageable pageable);

}
