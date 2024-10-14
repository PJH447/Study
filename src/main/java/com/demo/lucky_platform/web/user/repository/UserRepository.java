package com.demo.lucky_platform.web.user.repository;

import com.demo.lucky_platform.web.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
}
