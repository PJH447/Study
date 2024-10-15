package com.demo.lucky_platform.web.user.controller;

import com.demo.lucky_platform.web.user.dto.SignUpForm;
import com.demo.lucky_platform.web.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
}
