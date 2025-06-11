package com.demo.lucky_platform.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a user is not found.
 * This can happen when trying to retrieve a user by ID, email, or other identifiers.
 */
@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "사용자를 찾을 수 없습니다.")
public class UserNotFoundException extends RuntimeException {

    /**
     * Constructs a new UserNotFoundException with the specified detail message.
     *
     * @param message the detail message
     */
    public UserNotFoundException(String message) {
        super(message);
    }
}