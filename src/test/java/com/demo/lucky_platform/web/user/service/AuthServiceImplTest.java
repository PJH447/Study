package com.demo.lucky_platform.web.user.service;

import com.demo.lucky_platform.config.other.CacheTokenRepository;
import com.demo.lucky_platform.config.security.JwtTokenProvider;
import com.demo.lucky_platform.exception.AuthenticationException;
import com.demo.lucky_platform.exception.NotFoundRefreshTokenException;
import com.demo.lucky_platform.exception.ResultNotFoundException;
import com.demo.lucky_platform.web.user.domain.User;
import com.demo.lucky_platform.web.user.dto.LoginForm;
import com.demo.lucky_platform.web.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private CacheTokenRepository cacheTokenRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private User user;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Cookie cookie;

    private static final int JWT_ACCESS_EXPIRATION_DATE_MS = 3600000;
    private static final long JWT_REFRESH_EXPIRATION_DATE_MS = 86400000;
    private static final String REFRESH_TOKEN_CACHE_PREFIX = "refresh::";
    private static final String REFRESH_TOKEN_COOKIE_KEY = "refresh";
    private static final String ACCESS_TOKEN_COOKIE_KEY = "access";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "jwtAccessExpirationDateMs", JWT_ACCESS_EXPIRATION_DATE_MS);
        ReflectionTestUtils.setField(authService, "jwtRefreshExpirationDateMs", JWT_REFRESH_EXPIRATION_DATE_MS);
    }

    @Nested
    @DisplayName("로그인 테스트")
    class LoginTest {

        private LoginForm loginForm;

        @BeforeEach
        void setup() {
            loginForm = new LoginForm("test@example.com", "password");
        }

        @Test
        @DisplayName("성공")
        void 성공() {
            // given
            when(userRepository.findByEmailAndEnabledIsTrue(anyString())).thenReturn(Optional.of(user));
            when(bCryptPasswordEncoder.matches(anyString(), anyString())).thenReturn(true);
            when(user.getPassword()).thenReturn("password");
            when(jwtTokenProvider.generateAccessToken(any(User.class))).thenReturn("accessToken");
            when(jwtTokenProvider.generateRefreshToken(any(User.class))).thenReturn("refreshToken");
            when(user.getId()).thenReturn(1L);

            // when
            authService.login(loginForm, response);

            // then
            verify(userRepository, times(1)).findByEmailAndEnabledIsTrue(loginForm.email());
            verify(bCryptPasswordEncoder, times(1)).matches(loginForm.password(), user.getPassword());
            verify(jwtTokenProvider, times(1)).generateAccessToken(user);
            verify(jwtTokenProvider, times(1)).generateRefreshToken(user);
            verify(cacheTokenRepository, times(1)).setStringWithExpiration(
                    eq(REFRESH_TOKEN_CACHE_PREFIX + user.getId()),
                    anyString(),
                    anyLong()
            );
            verify(response, times(2)).addHeader(eq(HttpHeaders.SET_COOKIE), anyString());
        }

        @Test
        @DisplayName("실패 - 사용자 없음")
        void 실패_사용자없음() {
            // given
            when(userRepository.findByEmailAndEnabledIsTrue(anyString())).thenReturn(Optional.empty());

            // when & then
            assertThrows(ResultNotFoundException.class, () -> authService.login(loginForm, response));
            verify(userRepository, times(1)).findByEmailAndEnabledIsTrue(loginForm.email());
            verify(bCryptPasswordEncoder, never()).matches(anyString(), anyString());
        }

        @Test
        @DisplayName("실패 - 비밀번호 불일치")
        void 실패_비밀번호불일치() {
            // given
            when(userRepository.findByEmailAndEnabledIsTrue(anyString())).thenReturn(Optional.of(user));
            when(bCryptPasswordEncoder.matches(anyString(), anyString())).thenReturn(false);
            when(user.getPassword()).thenReturn("password");

            // when & then
            assertThrows(AuthenticationException.class, () -> authService.login(loginForm, response));
            verify(userRepository, times(1)).findByEmailAndEnabledIsTrue(loginForm.email());
            verify(bCryptPasswordEncoder, times(1)).matches(loginForm.password(), user.getPassword());
        }
    }

    @Nested
    @DisplayName("토큰 재발급 테스트")
    class ReissueAccessTokenTest {

        @BeforeEach
        void setup() {
            Cookie[] cookies = new Cookie[]{cookie};
            lenient().when(request.getCookies()).thenReturn(cookies);
            lenient().when(cookie.getName()).thenReturn(REFRESH_TOKEN_COOKIE_KEY);
            lenient().when(cookie.getValue()).thenReturn("refreshToken");
        }

        @Test
        @DisplayName("성공")
        void 성공() {
            // given
            Cookie[] cookies = new Cookie[]{cookie};
            when(request.getCookies()).thenReturn(cookies);
            when(jwtTokenProvider.getSubjectByToken(anyString())).thenReturn("test@example.com");
            when(userRepository.findByEmailAndEnabledIsTrue(anyString())).thenReturn(Optional.of(user));
            when(user.getId()).thenReturn(1L);
            when(cacheTokenRepository.getString(anyString())).thenReturn("refreshToken");
            when(jwtTokenProvider.generateAccessToken(any(User.class))).thenReturn("accessToken");
            when(jwtTokenProvider.generateRefreshToken(any(User.class))).thenReturn("refreshToken");

            // when
            authService.reissueAccessToken(request, response);

            // then
            verify(jwtTokenProvider, times(1)).getSubjectByToken("refreshToken");
            verify(userRepository, times(1)).findByEmailAndEnabledIsTrue("test@example.com");
            verify(cacheTokenRepository, times(1)).getString(REFRESH_TOKEN_CACHE_PREFIX + user.getId());
            verify(jwtTokenProvider, times(1)).generateAccessToken(user);
            verify(jwtTokenProvider, times(1)).generateRefreshToken(user);
            verify(cacheTokenRepository, times(1)).setStringWithExpiration(
                    eq(REFRESH_TOKEN_CACHE_PREFIX + user.getId()),
                    anyString(),
                    anyLong()
            );
            verify(response, times(2)).addHeader(eq(HttpHeaders.SET_COOKIE), anyString());
        }

        @Test
        @DisplayName("실패 - 리프레시 토큰 없음")
        void 실패_리프레시토큰없음() {
            // given
            when(request.getCookies()).thenReturn(null);

            // when & then
            assertThrows(NotFoundRefreshTokenException.class, () -> authService.reissueAccessToken(request, response));
        }

        @Test
        @DisplayName("실패 - 사용자 없음")
        void 실패_사용자없음() {
            // given
            Cookie[] cookies = new Cookie[]{cookie};
            when(request.getCookies()).thenReturn(cookies);
            when(jwtTokenProvider.getSubjectByToken(anyString())).thenReturn("test@example.com");
            when(userRepository.findByEmailAndEnabledIsTrue(anyString())).thenReturn(Optional.empty());

            // when & then
            assertThrows(ResultNotFoundException.class, () -> authService.reissueAccessToken(request, response));
        }

        @Test
        @DisplayName("실패 - 캐시된 리프레시 토큰 불일치")
        void 실패_캐시된리프레시토큰불일치() {
            // given
            Cookie[] cookies = new Cookie[]{cookie};
            when(request.getCookies()).thenReturn(cookies);
            when(jwtTokenProvider.getSubjectByToken(anyString())).thenReturn("test@example.com");
            when(userRepository.findByEmailAndEnabledIsTrue(anyString())).thenReturn(Optional.of(user));
            when(user.getId()).thenReturn(1L);
            when(cacheTokenRepository.getString(anyString())).thenReturn("differentRefreshToken");

            // when & then
            assertThrows(NotFoundRefreshTokenException.class, () -> authService.reissueAccessToken(request, response));
        }
    }

    @Nested
    @DisplayName("로그아웃 테스트")
    class LogoutTest {

        @Test
        @DisplayName("성공")
        void 성공() {
            // given
            Cookie[] cookies = new Cookie[]{cookie};
            when(request.getCookies()).thenReturn(cookies);
            when(cookie.getName()).thenReturn(REFRESH_TOKEN_COOKIE_KEY);
            when(cookie.getValue()).thenReturn("refreshToken");
            when(jwtTokenProvider.getSubjectByToken(anyString())).thenReturn("test@example.com");
            when(userRepository.findByEmailAndEnabledIsTrue(anyString())).thenReturn(Optional.of(user));
            when(user.getId()).thenReturn(1L);

            // when
            authService.logout(request, response);

            // then
            verify(response, times(2)).addHeader(eq(HttpHeaders.SET_COOKIE), anyString());
            verify(cacheTokenRepository, times(1)).delete(REFRESH_TOKEN_CACHE_PREFIX + user.getId());
        }

        @Test
        @DisplayName("성공 - 쿠키 없음")
        void 성공_쿠키없음() {
            // given
            when(request.getCookies()).thenReturn(null);

            // when
            authService.logout(request, response);

            // then
            verify(response, times(2)).addHeader(eq(HttpHeaders.SET_COOKIE), anyString());
            verify(cacheTokenRepository, never()).delete(anyString());
        }
    }

    @Nested
    @DisplayName("인증 헤더 설정 테스트")
    class SetAuthorizationHeaderTest {

        @Test
        @DisplayName("성공")
        void 성공() {
            // given
            when(jwtTokenProvider.resolveToken(any(HttpServletRequest.class))).thenReturn("Bearer accessToken");

            // when
            authService.setAuthorizationHeader(request, response);

            // then
            verify(jwtTokenProvider, times(1)).resolveToken(request);
            verify(response, times(1)).setHeader(HttpHeaders.AUTHORIZATION, "Bearer accessToken");
        }
    }
}
