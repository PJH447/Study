package com.demo.lucky_platform.config.security;

import com.demo.lucky_platform.web.user.service.UserDetailService;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_TYPE = "Bearer ";

    @Value("${app.jwt.secret-key}")
    private String secretKey;

    @Value("${app.jwt.expiration-milliseconds}")
    private long jwtExpirationDateMs;

    private final UserDetailService userDetailService;

    public String generateToken(Authentication authentication) {
        Map<String, Object> claims = new HashMap<>();
        // claims에 사용자 정보 추가 (예: userId, roles)

        return Jwts.builder()
                   .setClaims(claims)
                   .setSubject(authentication.getName())
                   .setIssuedAt(new Date())
                   .setExpiration(new Date(new Date().getTime() + jwtExpirationDateMs))
                   .signWith(SignatureAlgorithm.HS512, secretKey)
                   .compact();
    }

    public String getUserIdFromJWT(String token) {
        Claims claims = getClaims(token);
        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (SignatureException ex) {
            log.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
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
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_TYPE)) {
            return bearerToken.substring(7);
        }
        return null;
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

}
