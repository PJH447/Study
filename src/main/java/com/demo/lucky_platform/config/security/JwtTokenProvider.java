package com.demo.lucky_platform.config.security;

import com.demo.lucky_platform.web.user.domain.User;
import com.demo.lucky_platform.web.user.service.UserDetailService;
import io.jsonwebtoken.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private static final String ACCESS_TOKEN_COOKIE_KEY = "access";

    @Value("${app.jwt.secret-key}")
    private String secretKey;

    @Value("${app.jwt.access-expiration-milliseconds}")
    private long jwtAccessExpirationDateMs;

    @Value("${app.jwt.refresh-expiration-milliseconds}")
    private long jwtRefreshExpirationDateMs;

    private final UserDetailService userDetailService;

    public String generateAccessToken(User user) {
        return this.generateToken(user, jwtAccessExpirationDateMs);
    }

    public String generateRefreshToken(User user) {
        return this.generateToken(user, jwtRefreshExpirationDateMs);
    }

    private String generateToken(User user, Long expirationTime) {
        Map<String, Object> claims = new HashMap<>();
        // claims에 사용자 정보 추가 (예: userId, roles)

        return Jwts.builder()
                   .setClaims(claims)
                   .setSubject(user.getEmail())
                   .setIssuedAt(new Date())
                   .setExpiration(new Date(new Date().getTime() + expirationTime))
                   .signWith(SignatureAlgorithm.HS512, secretKey)
                   .compact();
    }

    public String getSubjectByToken(String token) {
        Claims claims = getClaims(token);
        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (SignatureException ex) {
            log.info("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.info("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.info("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.info("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.info("JWT claims string is empty.");
        }
        return false;
    }

    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);

        UserDetails userDetails = userDetailService.loadUserByUsername(claims.getSubject());
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                userDetails.getPassword(),
                userDetails.getAuthorities());
    }

    public String resolveToken(HttpServletRequest request) {
        String string = this.getCookie(request, ACCESS_TOKEN_COOKIE_KEY)
                            .map(Cookie::getValue)
                            .orElse(null);
        return string;
    }

    private Claims getClaims(String token) {
        try {
            return Jwts.parser()
                       .setSigningKey(secretKey)
                       .parseClaimsJws(token)
                       .getBody();
        } catch (ExpiredJwtException e) {
            log.error("ExpiredJwtException = {}", e.getClaims());
            return e.getClaims();
        }
    }

    private Optional<Cookie> getCookie(final HttpServletRequest request, final String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0)
            return Optional.empty();

        return Arrays.stream(cookies)
                     .filter(cookie -> cookie.getName().equals(name))
                     .findAny();
    }

}
