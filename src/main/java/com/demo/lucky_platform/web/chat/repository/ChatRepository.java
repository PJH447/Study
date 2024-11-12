package com.demo.lucky_platform.web.chat.repository;

import com.demo.lucky_platform.web.chat.domain.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, Long> {

}
