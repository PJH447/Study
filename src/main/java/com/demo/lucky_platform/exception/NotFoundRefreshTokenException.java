package com.demo.lucky_platform.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "refresh token 을 찾을 수 없습니다.")
public class NotFoundRefreshTokenException extends RuntimeException {

    public NotFoundRefreshTokenException(String message) {
        super(message);
    }

}
