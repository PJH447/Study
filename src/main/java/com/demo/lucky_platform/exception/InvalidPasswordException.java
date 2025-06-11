package com.demo.lucky_platform.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a password validation fails.
 * This can happen when the provided password doesn't match the stored password,
 * or when the password doesn't meet the required criteria.
 */
@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "비밀번호가 유효하지 않습니다.")
public class InvalidPasswordException extends RuntimeException {

    /**
     * Constructs a new InvalidPasswordException with the specified detail message.
     *
     * @param message the detail message
     */
    public InvalidPasswordException(String message) {
        super(message);
    }
}