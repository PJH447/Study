package com.demo.lucky_platform.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a duplicate user is detected.
 * This can happen when trying to create a user with an existing nickname or email,
 * or when a phone number has already been registered.
 */
@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "중복된 사용자 정보가 존재합니다.")
public class DuplicateUserException extends RuntimeException {

    /**
     * Constructs a new DuplicateUserException with the specified detail message.
     *
     * @param message the detail message
     */
    public DuplicateUserException(String message) {
        super(message);
    }
}