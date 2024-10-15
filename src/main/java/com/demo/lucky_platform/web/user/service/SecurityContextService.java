package com.demo.lucky_platform.web.user.service;

import com.demo.lucky_platform.web.user.domain.User;

public interface SecurityContextService {

    void refreshSecurityContext(User user);
    void clearContext();
}
