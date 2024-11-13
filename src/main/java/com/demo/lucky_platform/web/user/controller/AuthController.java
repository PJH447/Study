package com.demo.lucky_platform.web.user.controller;

import com.demo.lucky_platform.web.common.dto.CommonResponse;
import com.demo.lucky_platform.web.user.domain.AuthenticatedUser;
import com.demo.lucky_platform.web.user.dto.LoginForm;
import com.demo.lucky_platform.web.user.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/v1/login")
    public CommonResponse login(
            @RequestBody LoginForm loginForm,
            HttpServletResponse response
    ) {
        authService.login(loginForm, response);
        return CommonResponse.createVoidResponse();
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/v1/reissue")
    public CommonResponse reissueAccessToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        authService.reissueAccessToken(request, response);
        return CommonResponse.createVoidResponse();
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/v1/logout")
    public CommonResponse logout(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        authService.logout(request, response);
        return CommonResponse.createVoidResponse();
    }

    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/v1/socket")
    public CommonResponse createAuthorizationHeader(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        authService.setAuthorizationHeader(request, response);
        return CommonResponse.createVoidResponse();
    }
}
