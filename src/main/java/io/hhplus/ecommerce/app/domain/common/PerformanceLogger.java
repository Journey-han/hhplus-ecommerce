package io.hhplus.ecommerce.app.domain.common;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PerformanceLogger {
    public static void logPerformance(long startTime, String methodName) {
        long endTime = System.currentTimeMillis();
        log.info("Method {} executed in {} ms", methodName, (endTime - startTime));
    }
}
