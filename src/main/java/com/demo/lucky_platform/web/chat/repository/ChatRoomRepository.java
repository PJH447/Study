package com.demo.lucky_platform.web.chat.repository;

import com.demo.lucky_platform.web.chat.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
}
