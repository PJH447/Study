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

    /**
     * ID로 상담사를 찾고 사용자의 즐겨찾기 여부를 확인합니다.
     *
     * @param counselorId 찾을 상담사의 ID
     * @param userId 사용자의 ID, 인증되지 않은 경우 null일 수 있음
     * @return 상담사 정보와 즐겨찾기 상태가 포함된 DTO
     * @throws ResultNotFoundException 상담사를 찾을 수 없는 경우
     */
    @Override
    public CounselorDto findCounselor(final Long counselorId, final Long userId) {
        Counselor counselor = findCounselorById(counselorId);
        boolean isFavorite = userId != null && findFavorite(userId, counselorId).isPresent();
        return CounselorDto.of(counselor, isFavorite);
    }

    /**
     * 사용자와 상담사 간의 즐겨찾기 관계를 생성합니다.
     *
     * @param userId 사용자의 ID
     * @param counselorId 상담사의 ID
     * @throws ResultNotFoundException 사용자 또는 상담사를 찾을 수 없는 경우
     */
    @Override
    @Transactional
    public void createFavorite(final Long userId, final Long counselorId) {
        Optional<Favorite> existingFavorite = findFavorite(userId, counselorId);
        if (existingFavorite.isPresent()) {
            log.warn("이미 존재하는 favorite[user {} and counselor {}]", userId, counselorId);
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

    /**
     * 사용자와 상담사 간의 즐겨찾기 관계를 제거합니다.
     *
     * @param userId 사용자의 ID
     * @param counselorId 상담사의 ID
     * @throws ResultNotFoundException 즐겨찾기 관계를 찾을 수 없는 경우
     */
    @Override
    @Transactional
    public void deleteFavorite(final Long userId, final Long counselorId) {
        Favorite favorite = findFavorite(userId, counselorId)
                .orElseThrow(() -> new ResultNotFoundException("favorite가 존재하지 않습니다."));
        favorite.disabled();
        favoriteRepository.save(favorite);
    }

    /**
     * ID로 상담사를 찾습니다.
     *
     * @param counselorId 찾을 상담사의 ID
     * @return 상담사
     * @throws ResultNotFoundException 상담사를 찾을 수 없는 경우
     */
    private Counselor findCounselorById(final Long counselorId) {
        return counselorRepository.findByIdAndEnabledIsTrue(counselorId)
                                  .orElseThrow(() -> new ResultNotFoundException("상담사가 존재하지 않습니다"));
    }

    /**
     * 사용자와 상담사 간의 즐겨찾기 관계를 찾습니다.
     *
     * @param userId 사용자의 ID
     * @param counselorId 상담사의 ID
     * @return 즐겨찾기가 발견되면 해당 즐겨찾기를 포함하는 Optional, 발견되지 않으면 빈 Optional
     */
    private Optional<Favorite> findFavorite(final Long userId, final Long counselorId) {
        return favoriteRepository.findByUserAndCounselor(userId, counselorId);
    }
}