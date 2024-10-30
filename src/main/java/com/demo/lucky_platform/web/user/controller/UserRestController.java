package com.demo.lucky_platform.web.user.controller;

import com.demo.lucky_platform.web.common.dto.CommonResponse;
import com.demo.lucky_platform.web.user.domain.AuthenticatedUser;
import com.demo.lucky_platform.web.user.dto.EditInfoForm;
import com.demo.lucky_platform.web.user.dto.EditPasswordForm;
import com.demo.lucky_platform.web.user.dto.SignUpForm;
import com.demo.lucky_platform.web.user.service.AuthService;
import com.demo.lucky_platform.web.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserRestController {

    private final UserService userService;
    private final AuthService authService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/v1/signUp")
    public CommonResponse signUp(@RequestBody SignUpForm signUpForm) {
        userService.signUp(signUpForm);
        return CommonResponse.createVoidResponse();
    }

    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/v1/edit/info")
    public CommonResponse editUserInfo(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestBody EditInfoForm editInfoForm
    ) {

        userService.editNickname(user.getId(), editInfoForm.nickname());

        return CommonResponse.createVoidResponse();
    }

    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/v1/edit/password")
    public CommonResponse editPassword(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestBody EditPasswordForm editPasswordForm,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        userService.editPassword(user.getId(), editPasswordForm);
        authService.logout(request, response);
        return CommonResponse.createVoidResponse();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/v1/nickname")
    public CommonResponse existSameNickname(@RequestParam(value = "nickname") String nickname) {
        boolean existSameNickname = userService.existSameNickname(nickname);
        return CommonResponse.createResponse(existSameNickname);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/v1/email")
    public CommonResponse existSameEmail(@RequestParam(value = "email") String email) {
        boolean existSameEmail = userService.existSameEmail(email);
        return CommonResponse.createResponse(existSameEmail);
    }

    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/v1/headerInfo")
    public CommonResponse getHeaderInfo(
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        return CommonResponse.createResponse(userService.findHeaderInfo(authenticatedUser.getId()));
    }

}
