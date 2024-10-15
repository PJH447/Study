package com.demo.lucky_platform.web.user.controller;

import com.demo.lucky_platform.web.user.domain.AuthenticatedUser;
import com.demo.lucky_platform.web.user.dto.EditInfoForm;
import com.demo.lucky_platform.web.user.dto.EditPasswordForm;
import com.demo.lucky_platform.web.user.dto.SignUpForm;
import com.demo.lucky_platform.web.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserRestController {

    private final UserService userService;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/v1/sign-up")
    public String signUp(@RequestBody SignUpForm signUpForm) {
        userService.signUp(signUpForm);
        return "success";
    }

    @PostMapping("/v1/edit/info")
    public String editUserInfo(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestBody EditInfoForm editInfoForm
    ) {

        userService.editNickname(user.getId(), editInfoForm.nickname());

        return "success";
    }

    @PostMapping("/v1/edit/password")
    public String editUserInfo(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestBody EditPasswordForm editPasswordForm
    ) {
        userService.editPassword(user.getId(), editPasswordForm);
        return "success";
    }
}
