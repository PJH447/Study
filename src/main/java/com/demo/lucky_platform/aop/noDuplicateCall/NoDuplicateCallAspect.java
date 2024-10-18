package com.demo.lucky_platform.aop.noDuplicateCall;

import com.demo.lucky_platform.exception.DuplicateRequestException;
import com.demo.lucky_platform.web.user.domain.AuthenticatedUser;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@Aspect
public class NoDuplicateCallAspect {

    private final RedissonClient redissonClient;

    public NoDuplicateCallAspect(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Around("@annotation(com.demo.lucky_platform.aop.noDuplicateCall.NoDuplicateCall)")
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof String) {
            return joinPoint.proceed();
        }
        AuthenticatedUser user = (AuthenticatedUser) authentication.getPrincipal();

        String signature = joinPoint.getSignature().toShortString();
        RLock lock = redissonClient.getLock(signature + user.getId());

        Boolean isLocked = false;
        try {

            isLocked = lock.tryLock(0, 2, TimeUnit.SECONDS);
            if (!isLocked) {
                throw new DuplicateRequestException("짧은 시간 내에 동일한 요청이 있습니다.");
            }

            Object result = joinPoint.proceed();
            return result;
        } catch (DuplicateRequestException e) {
            log.info("userId(" + user.getId() + ")로부터 짧은 시간 내에 동일한 " + signature + " 요청이 있습니다.");
            throw e;
        } catch (Throwable e) {
            throw e;
        } finally {
            if (isLocked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
