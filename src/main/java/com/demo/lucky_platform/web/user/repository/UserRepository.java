package com.demo.lucky_platform.web.user.repository;

import com.demo.lucky_platform.web.user.domain.User;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserDslRepository {

    Optional<User> findByEmailAndEnabledIsTrue(String email);

    Optional<User> findByNicknameAndEnabledIsTrue(String nickname);

}
