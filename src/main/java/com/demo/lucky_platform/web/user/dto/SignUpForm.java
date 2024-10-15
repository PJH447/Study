package com.demo.lucky_platform.web.user.dto;

import lombok.Builder;

@Builder
public record SignUpForm(
        String email,
        String password,
        String name,
        String nickname,
        String phone,
        String impUid
) {
}
