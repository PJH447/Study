package com.demo.lucky_platform.web.user.dto;

public record EditPasswordForm(
        String password,
        String newPassword
) {
}
