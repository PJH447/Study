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
     * Authenticates a user and issues JWT tokens.
     *
     * @param loginForm the login form containing email and password
     * @param response  the HTTP response to set cookies
     * @throws ResultNotFoundException if the user is not found
     * @throws AuthenticationException if the password is incorrect
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
     * Reissues access token using refresh token.
     *
     * @param request  the HTTP request containing the refresh token cookie
     * @param response the HTTP response to set new cookies
     * @throws NotFoundRefreshTokenException if the refresh token is not found or invalid
     * @throws ResultNotFoundException       if the user is not found
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
     * Logs out a user by deleting tokens and cookies.
     *
     * @param request  the HTTP request containing the refresh token cookie
     * @param response the HTTP response to delete cookies
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
     * Creates a cookie with the given name, value, and expiration time.
     *
     * @param name     the cookie name
     * @param value    the cookie value (null for deletion)
     * @param maxAge   the cookie max age in milliseconds (0 for deletion)
     * @param response the HTTP response to add the cookie to
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
     * Sets the access token cookie.
     *
     * @param accessToken the access token value
     * @param response    the HTTP response to add the cookie to
     */
    private void setAccessToken(final String accessToken, final HttpServletResponse response) {
        setCookie(ACCESS_TOKEN_COOKIE_KEY, accessToken, jwtAccessExpirationDateMs, response);
    }

    /**
     * Deletes the access token cookie.
     *
     * @param response the HTTP response to add the cookie to
     */
    private void deleteAccessToken(final HttpServletResponse response) {
        setCookie(ACCESS_TOKEN_COOKIE_KEY, null, 0, response);
    }

    /**
     * Sets the refresh token cookie.
     *
     * @param refreshToken the refresh token value
     * @param response     the HTTP response to add the cookie to
     */
    private void setRefreshToken(final String refreshToken, final HttpServletResponse response) {
        setCookie(REFRESH_TOKEN_COOKIE_KEY, refreshToken, jwtRefreshExpirationDateMs, response);
    }

    /**
     * Deletes the refresh token cookie.
     *
     * @param response the HTTP response to add the cookie to
     */
    private void deleteRefreshToken(final HttpServletResponse response) {
        setCookie(REFRESH_TOKEN_COOKIE_KEY, null, 0, response);
    }

}
