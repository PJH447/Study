package com.demo.lucky_platform.web.counselor.repository;

import com.demo.lucky_platform.web.counselor.domain.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    @Query(value = "SELECT f FROM Favorite f WHERE f.user.id = :userId AND f.counselor.id = :counselorId AND f.enabled = TRUE ")
    Optional<Favorite> findByUserAndCounselor(Long userId, Long counselorId);


}
