package com.demo.lucky_platform.web.user.service;

import com.demo.lucky_platform.config.other.CacheTokenRepository;
import com.demo.lucky_platform.config.security.JwtTokenProvider;
import com.demo.lucky_platform.exception.NotFoundRefreshTokenException;
import com.demo.lucky_platform.web.user.domain.User;
import com.demo.lucky_platform.web.user.dto.LoginForm;
import com.demo.lucky_platform.web.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Optional;

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

    @Override
    public void login(final LoginForm loginForm, final HttpServletResponse response) {
        User user = userRepository.findByEmailAndEnabledIsTrue(loginForm.email())
                                  .orElseThrow(RuntimeException::new);

        boolean matches = bCryptPasswordEncoder.matches(loginForm.password(), user.getPassword());
        if (!matches) {
            throw new RuntimeException();
        }

        this.issueToken(response, user);
    }

    @Override
    public void reissueAccessToken(final HttpServletRequest request, final HttpServletResponse response) {
        String refreshToken = this.getCookie(request, REFRESH_TOKEN_COOKIE_KEY)
                                  .map(Cookie::getValue)
                                  .orElseThrow(() -> new NotFoundRefreshTokenException("refresh token 이 존재하지 않습니다."));

        String email = jwtTokenProvider.getSubjectByToken(refreshToken);
        User user = userRepository.findByEmailAndEnabledIsTrue(email)
                                  .orElseThrow(RuntimeException::new);

        String _refreshToken = cacheTokenRepository.getData(REFRESH_TOKEN_CACHE_PREFIX + user.getId());
        if (!refreshToken.equals(_refreshToken)) {
            throw new NotFoundRefreshTokenException("올바른 토큰이 아닙니다.");
        }

        this.issueToken(response, user);
    }

    @Override
    public void logout(final HttpServletRequest request, final HttpServletResponse response) {

        this.deleteAccessToken(response);
        this.deleteRefreshToken(response);

        this.getCookie(request, REFRESH_TOKEN_COOKIE_KEY)
            .map(Cookie::getValue)
            .ifPresent(refreshToken -> {
                String email = jwtTokenProvider.getSubjectByToken(refreshToken);
                User user = userRepository.findByEmailAndEnabledIsTrue(email)
                                          .orElseThrow(RuntimeException::new);
                cacheTokenRepository.deleteData(REFRESH_TOKEN_CACHE_PREFIX + user.getId());
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
        cacheTokenRepository.setDataAndExpiration(REFRESH_TOKEN_CACHE_PREFIX + user.getId(), refreshToken, jwtRefreshExpirationDateMs);
    }

    private Optional<Cookie> getCookie(final HttpServletRequest request, final String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0)
            return Optional.empty();

        return Arrays.stream(cookies)
                     .filter(cookie -> cookie.getName().equals(name))
                     .findAny();
    }

    private void setAccessToken(final String accessToken, final HttpServletResponse response) {
        ResponseCookie responseCookie = ResponseCookie.from(ACCESS_TOKEN_COOKIE_KEY, accessToken)
                                                      .path("/")
                                                      .maxAge(jwtAccessExpirationDateMs)
                                                      .httpOnly(true)
                                                      .secure(true)
                                                      .sameSite("None")
                                                      .build();
        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
    }

    private void deleteAccessToken(final HttpServletResponse response) {
        ResponseCookie responseCookie = ResponseCookie.from(ACCESS_TOKEN_COOKIE_KEY, null)
                                                      .path("/")
                                                      .maxAge(0)
                                                      .httpOnly(true)
                                                      .secure(true)
                                                      .sameSite("None")
                                                      .build();
        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
    }

    private void setRefreshToken(final String refreshToken, final HttpServletResponse response) {
        ResponseCookie responseCookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE_KEY, refreshToken)
                                                      .path("/")
                                                      .maxAge(jwtRefreshExpirationDateMs)
                                                      .httpOnly(true)
                                                      .secure(true)
                                                      .sameSite("None")
                                                      .build();
        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
    }

    private void deleteRefreshToken(final HttpServletResponse response) {
        ResponseCookie responseCookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE_KEY, null)
                                                      .path("/")
                                                      .maxAge(0)
                                                      .httpOnly(true)
                                                      .secure(true)
                                                      .sameSite("None")
                                                      .build();
        response.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
    }

}
