package io.hhplus.ecommerce.app.domain.common;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class PerformanceAspect {
    @Around("execution(* io.hhplus.ecommerce.app.application.service.*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        Object proceed = joinPoint.proceed();

        PerformanceLogger.logPerformance(startTime, joinPoint.getSignature().toShortString());

        return proceed;
    }
}
