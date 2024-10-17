package com.demo.lucky_platform.web.counselor.repository;

import com.demo.lucky_platform.web.counselor.domain.Counselor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CounselorRepository extends JpaRepository<Counselor, Long> {

    Optional<Counselor> findByIdAndEnabledIsTrue(Long id);
}
