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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Optional;

@Slf4j
@Transactional
@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private static final String REFRESH_TOKEN_CACHE_PREFIX = "refresh::";
    private static final String REFRESH_TOKEN_COOKIE_KEY = "refresh";
    private static final String ACCESS_TOKEN_COOKIE_KEY = "access";

    @Value("${app.jwt.access-expiration-milliseconds}")
    private int jwtAccessExpirationDateMs;

    @Value("${app.jwt.refresh-expiration-milliseconds}")
    private long jwtRefreshExpirationDateMs;

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final CacheTokenRepository cacheTokenRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    /**
     * 사용자를 인증하고 JWT 토큰을 발급합니다.
     *
     * @param loginForm 이메일과 비밀번호가 포함된 로그인 양식
     * @param response  쿠키를 설정하기 위한 HTTP 응답
     * @throws ResultNotFoundException 사용자를 찾을 수 없는 경우
     * @throws AuthenticationException 비밀번호가 올바르지 않은 경우
     */
    @Override
    public void login(final LoginForm loginForm, final HttpServletResponse response) {
        User user = userRepository.findByEmailAndEnabledIsTrue(loginForm.email())
                                  .orElseThrow(() -> new ResultNotFoundException("User not found with email: " + loginForm.email()));

        boolean matches = bCryptPasswordEncoder.matches(loginForm.password(), user.getPassword());
        if (!matches) {
            throw new AuthenticationException("Invalid password");
        }

        this.issueToken(response, user);
    }

    /**
     * 리프레시 토큰을 사용하여 액세스 토큰을 재발급합니다.
     *
     * @param request  리프레시 토큰 쿠키가 포함된 HTTP 요청
     * @param response 새 쿠키를 설정하기 위한 HTTP 응답
     * @throws NotFoundRefreshTokenException 리프레시 토큰을 찾을 수 없거나 유효하지 않은 경우
     * @throws ResultNotFoundException       사용자를 찾을 수 없는 경우
     */
    @Override
    public void reissueAccessToken(final HttpServletRequest request, final HttpServletResponse response) {
        String refreshToken = this.getCookie(request, REFRESH_TOKEN_COOKIE_KEY)
                                  .map(Cookie::getValue)
                                  .orElseThrow(() -> new NotFoundRefreshTokenException("Refresh token not found"));

        String email = jwtTokenProvider.getSubjectByToken(refreshToken);
        User user = userRepository.findByEmailAndEnabledIsTrue(email)
                                  .orElseThrow(() -> new ResultNotFoundException("User not found with email: " + email));

        String _refreshToken = cacheTokenRepository.getString(REFRESH_TOKEN_CACHE_PREFIX + user.getId());
        if (!refreshToken.equals(_refreshToken)) {
            throw new NotFoundRefreshTokenException("Invalid refresh token");
        }

        this.issueToken(response, user);
    }

    /**
     * 토큰과 쿠키를 삭제하여 사용자를 로그아웃합니다.
     *
     * @param request  리프레시 토큰 쿠키가 포함된 HTTP 요청
     * @param response 쿠키를 삭제하기 위한 HTTP 응답
     */
    @Override
    public void logout(final HttpServletRequest request, final HttpServletResponse response) {
        this.deleteAccessToken(response);
        this.deleteRefreshToken(response);

        this.getCookie(request, REFRESH_TOKEN_COOKIE_KEY)
            .map(Cookie::getValue)
            .ifPresent(refreshToken -> {
                try {
                    String email = jwtTokenProvider.getSubjectByToken(refreshToken);
                    User user = userRepository.findByEmailAndEnabledIsTrue(email)
                                              .orElseThrow(() -> new ResultNotFoundException("User not found with email: " + email));
                    cacheTokenRepository.delete(REFRESH_TOKEN_CACHE_PREFIX + user.getId());
                } catch (Exception e) {
                    // Log but don't throw during logout to ensure cookies are still cleared
                    log.warn("Error during logout: {}", e.getMessage());
                }
            });
    }

    @Override
    public void setAuthorizationHeader(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = jwtTokenProvider.resolveToken(request);
        response.setHeader(HttpHeaders.AUTHORIZATION, accessToken);
    }

    private void issueToken(final HttpServletResponse response, final User user) {
        String accessToken = jwtTokenProvider.generateAccessToken(user);
        this.setAccessToken(accessToken, response);

        String refreshToken = jwtTokenProvider.generateRefreshToken(user);
        this.setRefreshToken(refreshToken, response);
        // Convert milliseconds to seconds for Redis expiration
        long refreshExpirationSeconds = jwtRefreshExpirationDateMs / 1000;
        cacheTokenRepository.setStringWithExpiration(REFRESH_TOKEN_CACHE_PREFIX + user.getId(), refreshToken, refreshExpirationSeconds);
    }

    private Optional<Cookie> getCookie(final HttpServletRequest request, final String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0)
            return Optional.empty();

        return Arrays.stream(cookies)
                     .filter(cookie -> cookie.getName().equals(name))
                     .findAny();
    }

    /**
     * 주어진 이름, 값, 만료 시간으로 쿠키를 생성합니다.
     *
     * @param name     쿠키 이름
     * @param value    쿠키 값 (삭제 시 null)
     * @param maxAge   쿠키 최대 수명(밀리초) (삭제 시 0)
     * @param response 쿠키를 추가할 HTTP 응답
     */
    private void setCookie(final String name, final String value, final long maxAge, final HttpServletResponse response) {
        ResponseCookie responseCookie = ResponseCookie.from(name, value)
                                                      .path("/")
                                                      .maxAge(maxAge)
                                                      .httpOnly(true)
                                                      .secure(true)
                                                      .sameSite("None")
                                                      .build();
        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
    }

    /**
     * 액세스 토큰 쿠키를 설정합니다.
     *
     * @param accessToken 액세스 토큰 값
     * @param response    쿠키를 추가할 HTTP 응답
     */
    private void setAccessToken(final String accessToken, final HttpServletResponse response) {
        setCookie(ACCESS_TOKEN_COOKIE_KEY, accessToken, jwtAccessExpirationDateMs, response);
    }

    /**
     * 액세스 토큰 쿠키를 삭제합니다.
     *
     * @param response 쿠키를 추가할 HTTP 응답
     */
    private void deleteAccessToken(final HttpServletResponse response) {
        setCookie(ACCESS_TOKEN_COOKIE_KEY, null, 0, response);
    }

    /**
     * 리프레시 토큰 쿠키를 설정합니다.
     *
     * @param refreshToken 리프레시 토큰 값
     * @param response     쿠키를 추가할 HTTP 응답
     */
    private void setRefreshToken(final String refreshToken, final HttpServletResponse response) {
        setCookie(REFRESH_TOKEN_COOKIE_KEY, refreshToken, jwtRefreshExpirationDateMs, response);
    }

    /**
     * 리프레시 토큰 쿠키를 삭제합니다.
     *
     * @param response 쿠키를 추가할 HTTP 응답
     */
    private void deleteRefreshToken(final HttpServletResponse response) {
        setCookie(REFRESH_TOKEN_COOKIE_KEY, null, 0, response);
    }

}
