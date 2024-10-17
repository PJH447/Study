package com.demo.lucky_platform.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "결과를 찾을 수 없습니다.")
public class ResultNotFoundException extends RuntimeException {

    public ResultNotFoundException(String message) {
        super(message);
    }

}
