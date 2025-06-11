package com.demo.lucky_platform.exception;

/**
 * Exception thrown when authentication fails.
 */
public class AuthenticationException extends RuntimeException {

    /**
     * Constructs a new AuthenticationException with the specified detail message.
     *
     * @param message the detail message
     */
    public AuthenticationException(String message) {
        super(message);
    }

    /**
     * Constructs a new AuthenticationException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause
     */
    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}