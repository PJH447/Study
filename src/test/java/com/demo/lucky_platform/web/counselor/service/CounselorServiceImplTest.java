package com.demo.lucky_platform.web.counselor.service;

import com.demo.lucky_platform.exception.ResultNotFoundException;
import com.demo.lucky_platform.web.counselor.domain.CounselingCategory;
import com.demo.lucky_platform.web.counselor.domain.Counselor;
import com.demo.lucky_platform.web.counselor.domain.Favorite;
import com.demo.lucky_platform.web.counselor.domain.Site;
import com.demo.lucky_platform.web.counselor.dto.CounselorDto;
import com.demo.lucky_platform.web.counselor.repository.CounselorRepository;
import com.demo.lucky_platform.web.counselor.repository.FavoriteRepository;
import com.demo.lucky_platform.web.user.domain.User;
import com.demo.lucky_platform.web.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ActiveProfiles({"test"})
@ExtendWith(MockitoExtension.class)
class CounselorServiceImplTest {

    @InjectMocks
    private CounselorServiceImpl counselorService;

    @Mock
    private CounselorRepository counselorRepository;

    @Mock
    private FavoriteRepository favoriteRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Counselor counselor;

    @Mock
    private User user;

    @Mock
    private Favorite favorite;

    @Nested
    @DisplayName("상담사 조회 테스트")
    class FindCounselorTest {

        private final Long counselorId = 1L;
        private final Long userId = 2L;

        @BeforeEach
        void setup() {
            lenient().when(counselor.getId()).thenReturn(counselorId);
            lenient().when(counselor.getNickname()).thenReturn("상담사닉네임");
            lenient().when(counselor.getServiceNumber()).thenReturn("12345");
            lenient().when(counselor.getSite()).thenReturn(Site.SITE_EXAMPLE);
            lenient().when(counselor.getCounselingCategory()).thenReturn(CounselingCategory.CATEGORY_EXAMPLE);
            lenient().when(counselor.getPerCoin()).thenReturn(100);
            lenient().when(counselor.getPicture()).thenReturn("picture.jpg");
            lenient().when(counselor.getSourceUrl()).thenReturn("http://example.com");
        }

        @Test
        void 성공_즐겨찾기있음() {
            // given
            when(counselorRepository.findByIdAndEnabledIsTrue(counselorId)).thenReturn(Optional.of(counselor));
            when(favoriteRepository.findByUserAndCounselor(userId, counselorId)).thenReturn(Optional.of(favorite));

            // when
            CounselorDto result = counselorService.findCounselor(counselorId, userId);

            // then
            assertNotNull(result);
            assertEquals(counselorId, result.counselorId());
            assertEquals("상담사닉네임", result.nickname());
            assertEquals("12345", result.serviceNumber());
            assertEquals("siteName", result.site());
            assertEquals("categoryString", result.counselingCategory());
            assertEquals(100, result.perCoin());
            assertEquals("picture.jpg", result.picture());
            assertEquals("http://example.com", result.sourceUrl());
            assertTrue(result.isFavorite());

            verify(counselorRepository, times(1)).findByIdAndEnabledIsTrue(counselorId);
            verify(favoriteRepository, times(1)).findByUserAndCounselor(userId, counselorId);
        }

        @Test
        void 성공_즐겨찾기없음() {
            // given
            when(counselorRepository.findByIdAndEnabledIsTrue(counselorId)).thenReturn(Optional.of(counselor));
            when(favoriteRepository.findByUserAndCounselor(userId, counselorId)).thenReturn(Optional.empty());

            // when
            CounselorDto result = counselorService.findCounselor(counselorId, userId);

            // then
            assertNotNull(result);
            assertEquals(counselorId, result.counselorId());
            assertEquals("상담사닉네임", result.nickname());
            assertEquals("12345", result.serviceNumber());
            assertEquals("siteName", result.site());
            assertEquals("categoryString", result.counselingCategory());
            assertEquals(100, result.perCoin());
            assertEquals("picture.jpg", result.picture());
            assertEquals("http://example.com", result.sourceUrl());
            assertFalse(result.isFavorite());

            verify(counselorRepository, times(1)).findByIdAndEnabledIsTrue(counselorId);
            verify(favoriteRepository, times(1)).findByUserAndCounselor(userId, counselorId);
        }

        @Test
        void 성공_사용자ID가_null() {
            // given
            when(counselorRepository.findByIdAndEnabledIsTrue(counselorId)).thenReturn(Optional.of(counselor));

            // when
            CounselorDto result = counselorService.findCounselor(counselorId, null);

            // then
            assertNotNull(result);
            assertEquals(counselorId, result.counselorId());
            assertEquals("상담사닉네임", result.nickname());
            assertEquals("12345", result.serviceNumber());
            assertEquals("siteName", result.site());
            assertEquals("categoryString", result.counselingCategory());
            assertEquals(100, result.perCoin());
            assertEquals("picture.jpg", result.picture());
            assertEquals("http://example.com", result.sourceUrl());
            assertFalse(result.isFavorite());

            verify(counselorRepository, times(1)).findByIdAndEnabledIsTrue(counselorId);
            verify(favoriteRepository, never()).findByUserAndCounselor(anyLong(), anyLong());
        }

        @Test
        void 실패_상담사가존재하지않음() {
            // given
            when(counselorRepository.findByIdAndEnabledIsTrue(counselorId)).thenReturn(Optional.empty());

            // when & then
            assertThrows(ResultNotFoundException.class, () -> counselorService.findCounselor(counselorId, userId));
            verify(counselorRepository, times(1)).findByIdAndEnabledIsTrue(counselorId);
            verify(favoriteRepository, never()).findByUserAndCounselor(anyLong(), anyLong());
        }
    }

    @Nested
    @DisplayName("즐겨찾기 생성 테스트")
    class CreateFavoriteTest {

        private final Long counselorId = 1L;
        private final Long userId = 2L;

        @Test
        void 성공() {
            // given
            when(favoriteRepository.findByUserAndCounselor(userId, counselorId)).thenReturn(Optional.empty());
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(counselorRepository.findByIdAndEnabledIsTrue(counselorId)).thenReturn(Optional.of(counselor));

            // when
            counselorService.createFavorite(userId, counselorId);

            // then
            verify(favoriteRepository, times(1)).findByUserAndCounselor(userId, counselorId);
            verify(userRepository, times(1)).findById(userId);
            verify(counselorRepository, times(1)).findByIdAndEnabledIsTrue(counselorId);
            verify(favoriteRepository, times(1)).save(any(Favorite.class));
        }

        @Test
        void 성공_이미즐겨찾기가존재함() {
            // given
            when(favoriteRepository.findByUserAndCounselor(userId, counselorId)).thenReturn(Optional.of(favorite));

            // when
            counselorService.createFavorite(userId, counselorId);

            // then
            verify(favoriteRepository, times(1)).findByUserAndCounselor(userId, counselorId);
            verify(userRepository, never()).findById(anyLong());
            verify(counselorRepository, never()).findByIdAndEnabledIsTrue(anyLong());
            verify(favoriteRepository, never()).save(any(Favorite.class));
        }

        @Test
        void 실패_사용자가존재하지않음() {
            // given
            when(favoriteRepository.findByUserAndCounselor(userId, counselorId)).thenReturn(Optional.empty());
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            // when & then
            assertThrows(ResultNotFoundException.class, () -> counselorService.createFavorite(userId, counselorId));
            verify(favoriteRepository, times(1)).findByUserAndCounselor(userId, counselorId);
            verify(userRepository, times(1)).findById(userId);
            verify(counselorRepository, never()).findByIdAndEnabledIsTrue(anyLong());
            verify(favoriteRepository, never()).save(any(Favorite.class));
        }

        @Test
        void 실패_상담사가존재하지않음() {
            // given
            when(favoriteRepository.findByUserAndCounselor(userId, counselorId)).thenReturn(Optional.empty());
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(counselorRepository.findByIdAndEnabledIsTrue(counselorId)).thenReturn(Optional.empty());

            // when & then
            assertThrows(ResultNotFoundException.class, () -> counselorService.createFavorite(userId, counselorId));
            verify(favoriteRepository, times(1)).findByUserAndCounselor(userId, counselorId);
            verify(userRepository, times(1)).findById(userId);
            verify(counselorRepository, times(1)).findByIdAndEnabledIsTrue(counselorId);
            verify(favoriteRepository, never()).save(any(Favorite.class));
        }
    }

    @Nested
    @DisplayName("즐겨찾기 삭제 테스트")
    class DeleteFavoriteTest {

        private final Long counselorId = 1L;
        private final Long userId = 2L;

        @Test
        void 성공() {
            // given
            when(favoriteRepository.findByUserAndCounselor(userId, counselorId)).thenReturn(Optional.of(favorite));

            // when
            counselorService.deleteFavorite(userId, counselorId);

            // then
            verify(favoriteRepository, times(1)).findByUserAndCounselor(userId, counselorId);
            verify(favorite, times(1)).disabled();
            verify(favoriteRepository, times(1)).save(favorite);
        }

        @Test
        void 실패_즐겨찾기가존재하지않음() {
            // given
            when(favoriteRepository.findByUserAndCounselor(userId, counselorId)).thenReturn(Optional.empty());

            // when & then
            assertThrows(ResultNotFoundException.class, () -> counselorService.deleteFavorite(userId, counselorId));
            verify(favoriteRepository, times(1)).findByUserAndCounselor(userId, counselorId);
            verify(favorite, never()).disabled();
            verify(favoriteRepository, never()).save(any(Favorite.class));
        }
    }
}
