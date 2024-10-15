package com.demo.lucky_platform.aop.securityContextHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
@Aspect
public class SecurityContextAspect {

    private final SecurityContextRepository securityContextRepository;

    public SecurityContextAspect(SecurityContextRepository securityContextRepository) {
        this.securityContextRepository = securityContextRepository;
    }

    @Around("@within(com.demo.lucky_platform.aop.securityContextHandler.SecurityContextAop) || " +
            "@annotation(com.demo.lucky_platform.aop.securityContextHandler.SecurityContextAop)")
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {

        Object result = joinPoint.proceed();

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        HttpServletResponse response = attributes.getResponse();
        securityContextRepository.saveContext(SecurityContextHolder.getContext(), request, response);

        return result;
    }
}
