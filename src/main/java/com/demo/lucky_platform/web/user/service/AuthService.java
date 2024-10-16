package com.demo.lucky_platform.web.user.service;

import com.demo.lucky_platform.web.user.domain.AuthenticatedUser;
import com.demo.lucky_platform.web.user.dto.LoginForm;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {

    void login(LoginForm loginForm, HttpServletResponse response);
    void reissueAccessToken(HttpServletRequest request, HttpServletResponse response);
    void logout(AuthenticatedUser user, HttpServletResponse response);

}
