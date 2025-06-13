package com.demo.lucky_platform.web.counselor.service;

import com.demo.lucky_platform.exception.ResultNotFoundException;
import com.demo.lucky_platform.web.counselor.domain.Counselor;
import com.demo.lucky_platform.web.counselor.domain.Favorite;
import com.demo.lucky_platform.web.counselor.dto.CounselorDto;
import com.demo.lucky_platform.web.counselor.repository.CounselorRepository;
import com.demo.lucky_platform.web.counselor.repository.FavoriteRepository;
import com.demo.lucky_platform.web.user.domain.User;
import com.demo.lucky_platform.web.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class CounselorServiceImpl implements CounselorService {

    private final CounselorRepository counselorRepository;
    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;

    @Override
    public CounselorDto findCounselor(final Long counselorId, final Long userId) {
        Counselor counselor = findCounselorById(counselorId);
        boolean isFavorite = userId != null && findFavorite(userId, counselorId).isPresent();
        return CounselorDto.of(counselor, isFavorite);
    }

    @Override
    @Transactional
    public void createFavorite(final Long userId, final Long counselorId) {
        Optional<Favorite> existingFavorite = findFavorite(userId, counselorId);
        if (existingFavorite.isPresent()) {
            log.warn("이미 존재하는 favorite");
            return;
        }

        User user = userRepository.findById(userId)
                                  .orElseThrow(() -> new ResultNotFoundException("유저가 존재하지 않습니다."));
        Counselor counselor = findCounselorById(counselorId);

        Favorite favorite = Favorite.builder()
                                    .user(user)
                                    .counselor(counselor)
                                    .build();
        favoriteRepository.save(favorite);
    }

    @Override
    @Transactional
    public void deleteFavorite(final Long userId, final Long counselorId) {
        Favorite favorite = findFavorite(userId, counselorId)
                .orElseThrow(() -> new ResultNotFoundException("favorite가 존재하지 않습니다."));
        favorite.disabled();
        favoriteRepository.save(favorite);
    }

    private Counselor findCounselorById(final Long counselorId) {
        return counselorRepository.findByIdAndEnabledIsTrue(counselorId)
                                  .orElseThrow(() -> new ResultNotFoundException("상담사가 존재하지 않습니다"));
    }

    private Optional<Favorite> findFavorite(final Long userId, final Long counselorId) {
        return favoriteRepository.findByUserAndCounselor(userId, counselorId);
    }
}