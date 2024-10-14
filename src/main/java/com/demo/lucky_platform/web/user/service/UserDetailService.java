package com.demo.lucky_platform.web.user.service;

import com.demo.lucky_platform.web.user.domain.AuthenticatedUser;
import com.demo.lucky_platform.web.user.domain.User;
import com.demo.lucky_platform.web.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashMap;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();

        User user = userRepository.findByEmailAAndEnabledIsTrue(email)
                                  .orElseThrow(() -> new UsernameNotFoundException("No user found with email: " + email));

        AuthenticatedUser authenticatedUser = new AuthenticatedUser(user);

        HashMap<String, String> userinfo = new HashMap<>();
        userinfo.put("id", Optional.ofNullable(user.getId()).orElse(0L).toString());
        userinfo.put("email", user.getEmail());
        userinfo.put("nickname", user.getNickname());
        userinfo.put("phone", user.getPhone());

        request.getSession().setAttribute("userinfo", userinfo);

        return authenticatedUser;
    }

}