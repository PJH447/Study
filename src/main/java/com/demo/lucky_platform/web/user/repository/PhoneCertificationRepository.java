package com.demo.lucky_platform.web.user.repository;

import com.demo.lucky_platform.web.user.domain.PhoneCertification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PhoneCertificationRepository extends JpaRepository<PhoneCertification, Long> {

    Optional<PhoneCertification> findByUniqueKeyAndEnabledIsTrue(String uniqueKey);
}
