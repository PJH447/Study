package com.demo.lucky_platform.exception.handler;

import com.demo.lucky_platform.exception.*;
import com.demo.lucky_platform.web.common.dto.CommonResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.demo.lucky_platform.exception.LoggingUtil.warningLogging;

/**
 * Global exception handler for REST controllers.
 * Handles various exceptions and returns appropriate responses.
 */
@Slf4j
@Order(GlobalRestExceptionHandler.ORDER)
@RestControllerAdvice(annotations = RestController.class)
public class GlobalRestExceptionHandler {

    public static final int ORDER = 0;

    /**
     * Handles duplicate request exceptions.
     *
     * @param req The HTTP request
     * @param e   The exception
     * @return A CommonResponse with error details
     */
    @ResponseStatus(code = HttpStatus.OK)
    @ExceptionHandler(value = {DuplicateRequestException.class})
    public CommonResponse handleDuplicateRequestException(HttpServletRequest req, DuplicateRequestException e) {
        return CommonResponse.createErrorResponse(e);
    }

    /**
     * Handles resource not found exceptions.
     *
     * @param req The HTTP request
     * @param e   The exception
     * @return A CommonResponse with error details
     */
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = ResultNotFoundException.class)
    public CommonResponse handleResultNotFoundException(HttpServletRequest req, ResultNotFoundException e) {
        warningLogging(req, e, false);
        return CommonResponse.createErrorResponse(e);
    }

    /**
     * Handles refresh token not found exceptions.
     *
     * @param req The HTTP request
     * @param e   The exception
     * @return A CommonResponse with error details
     */
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = NotFoundRefreshTokenException.class)
    public CommonResponse handleNotFoundRefreshTokenException(HttpServletRequest req, NotFoundRefreshTokenException e) {
        warningLogging(req, e, false);
        return CommonResponse.createErrorResponse(e);
    }

    /**
     * Handles authentication exceptions.
     *
     * @param req The HTTP request
     * @param e   The exception
     * @return A CommonResponse with error details
     */
    @ResponseStatus(code = HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(value = AuthenticationException.class)
    public CommonResponse handleAuthenticationException(HttpServletRequest req, AuthenticationException e) {
        warningLogging(req, e, false);
        return CommonResponse.createErrorResponse(e);
    }

    /**
     * Handles duplicate user exceptions.
     *
     * @param req The HTTP request
     * @param e   The exception
     * @return A CommonResponse with error details
     */
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = DuplicateUserException.class)
    public CommonResponse handleDuplicateUserException(HttpServletRequest req, DuplicateUserException e) {
        warningLogging(req, e, false);
        return CommonResponse.createErrorResponse(e);
    }

    /**
     * Handles invalid password exceptions.
     *
     * @param req The HTTP request
     * @param e   The exception
     * @return A CommonResponse with error details
     */
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = InvalidPasswordException.class)
    public CommonResponse handleInvalidPasswordException(HttpServletRequest req, InvalidPasswordException e) {
        warningLogging(req, e, false);
        return CommonResponse.createErrorResponse(e);
    }

    /**
     * Handles phone certification exceptions.
     *
     * @param req The HTTP request
     * @param e   The exception
     * @return A CommonResponse with error details
     */
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = PhoneCertificationException.class)
    public CommonResponse handlePhoneCertificationException(HttpServletRequest req, PhoneCertificationException e) {
        warningLogging(req, e, false);
        return CommonResponse.createErrorResponse(e);
    }

    /**
     * Handles user not found exceptions.
     *
     * @param req The HTTP request
     * @param e   The exception
     * @return A CommonResponse with error details
     */
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = UserNotFoundException.class)
    public CommonResponse handleUserNotFoundException(HttpServletRequest req, UserNotFoundException e) {
        warningLogging(req, e, false);
        return CommonResponse.createErrorResponse(e);
    }
}
