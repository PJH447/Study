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
        Counselor counselor = counselorRepository.findByIdAndEnabledIsTrue(counselorId)
                                                 .orElseThrow(() -> new ResultNotFoundException("상담사가 존재하지 않습니다"));

        boolean isFavorite = userId != null && favoriteRepository.findByUserAndCounselor(userId, counselorId).isPresent();

        return CounselorDto.of(counselor, isFavorite);
    }

    @Override
    @Transactional
    public void createFavorite(final Long userId, final Long counselorId) {

        if (favoriteRepository.findByUserAndCounselor(userId, counselorId).isPresent()) {
            log.warn("이미 존재하는 favorite");
            return;
        }

        User user = userRepository.findById(userId)
                                  .orElseThrow(() -> new RuntimeException("유저가 존재하지 않습니다."));
        Counselor counselor = counselorRepository.findByIdAndEnabledIsTrue(counselorId)
                                                 .orElseThrow(() -> new ResultNotFoundException("상담사가 존재하지 않습니다"));

        Favorite favorite = Favorite.builder()
                                    .user(user)
                                    .counselor(counselor)
                                    .build();
        favoriteRepository.save(favorite);
    }

    @Override
    @Transactional
    public void deleteFavorite(final Long userId, final Long counselorId) {

        Favorite favorite = favoriteRepository.findByUserAndCounselor(userId, counselorId)
                                              .orElseThrow(() -> new ResultNotFoundException("[잘못된 요청]favorite 가 존재하지 않습니다."));
        favorite.disabled();

        favoriteRepository.save(favorite);
    }

}
