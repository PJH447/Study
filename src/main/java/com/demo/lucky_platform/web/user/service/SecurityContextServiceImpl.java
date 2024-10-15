package com.demo.lucky_platform.web.user.service;

import com.demo.lucky_platform.aop.securityContextHandler.SecurityContextAop;
import com.demo.lucky_platform.web.user.domain.AuthenticatedUser;
import com.demo.lucky_platform.web.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
@SecurityContextAop
public class SecurityContextServiceImpl implements SecurityContextService {

    @Override
    public void refreshSecurityContext(User user) {
        try {
            SecurityContextHolder.getContext().setAuthentication(
                    new PreAuthenticatedAuthenticationToken(new AuthenticatedUser(user),
                            user.getPassword(),
                            user.getRoles().stream()
                                .map(role -> new SimpleGrantedAuthority(role.getAuthority()))
                                .collect(Collectors.toSet())));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            SecurityContextHolder.getContext().setAuthentication(null);
        }
    }

    @Override
    public void clearContext() {
        SecurityContextHolder.clearContext();
    }
}
