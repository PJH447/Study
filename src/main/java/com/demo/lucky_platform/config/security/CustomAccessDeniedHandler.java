package com.demo.lucky_platform.config.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.io.IOException;
import java.util.Objects;

@Configuration
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest httpServletRequest,
                       HttpServletResponse httpServletResponse,
                       AccessDeniedException exception) throws IOException {

        String requestedWithHeader = httpServletRequest.getHeader("X-Requested-With");

        if ("XMLHttpRequest".equals(requestedWithHeader)) {
            httpServletResponse.setCharacterEncoding("UTF-8");
            httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            httpServletResponse.getWriter().write(exception.getMessage());
        } else {
            var flashMap = new FlashMap();
            flashMap.put("errorMessage", exception.getMessage());

            var flashMapManager = RequestContextUtils.getFlashMapManager(httpServletRequest);
            Objects.requireNonNull(flashMapManager)
                   .saveOutputFlashMap(flashMap, httpServletRequest, httpServletResponse);

            httpServletResponse.sendRedirect(httpServletRequest.getContextPath() + "/");
        }
    }

}