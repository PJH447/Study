package com.demo.lucky_platform.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when there's an issue with phone certification.
 * This can happen when the certification process fails, when the certification
 * doesn't exist, or when there's already a registration with the same phone.
 */
@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "휴대폰 인증에 문제가 발생했습니다.")
public class PhoneCertificationException extends RuntimeException {

    /**
     * Constructs a new PhoneCertificationException with the specified detail message.
     *
     * @param message the detail message
     */
    public PhoneCertificationException(String message) {
        super(message);
    }

    /**
     * Constructs a new PhoneCertificationException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause
     */
    public PhoneCertificationException(String message, Throwable cause) {
        super(message, cause);
    }
}