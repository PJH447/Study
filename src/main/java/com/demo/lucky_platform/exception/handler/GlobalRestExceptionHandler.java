package com.demo.lucky_platform.exception.handler;

import com.demo.lucky_platform.exception.DuplicateRequestException;
import com.demo.lucky_platform.exception.ResultNotFoundException;
import com.demo.lucky_platform.web.common.dto.CommonResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.demo.lucky_platform.exception.LoggingUtil.warningLogging;

@Slf4j
@Order(GlobalRestExceptionHandler.ORDER)
@RestControllerAdvice(annotations = RestController.class)
public class GlobalRestExceptionHandler {

    public static final int ORDER = 0;

    @ResponseStatus(code = HttpStatus.OK)
    @ExceptionHandler(value = {DuplicateRequestException.class})
    public CommonResponse h1(HttpServletRequest req, RuntimeException e) {
        return CommonResponse.createErrorResponse(e);
    }

    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    @ExceptionHandler(value = ResultNotFoundException.class)
    public CommonResponse h2(HttpServletRequest req, ResultNotFoundException e) {
        warningLogging(req, e, false);
        return CommonResponse.createErrorResponse(e);
    }


}