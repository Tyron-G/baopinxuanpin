package com.oneaix.selection.aspect;

import com.oneaix.selection.annotation.TrackedExecution;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/** 关键服务执行耗时日志 2026-06-04 */
@Aspect
@Component
public class TrackedExecutionAspect {
    private static final Logger log = LoggerFactory.getLogger(TrackedExecutionAspect.class);

    private final MeterRegistry meterRegistry;

    public TrackedExecutionAspect(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Around("@annotation(trackedExecution)")
    public Object around(ProceedingJoinPoint joinPoint, TrackedExecution trackedExecution) throws Throwable {
        long startedAt = System.nanoTime();
        String label = trackedExecution.value().isBlank()
                ? joinPoint.getSignature().toShortString()
                : trackedExecution.value();
        String domain = trackedExecution.domain().isBlank()
                ? "other"
                : trackedExecution.domain();
        try {
            return joinPoint.proceed();
        } finally {
            long elapsedNs = System.nanoTime() - startedAt;
            long elapsedMs = elapsedNs / 1_000_000;
            Timer.builder("selection.tracked.execution")
                    .description("Tracked execution duration for core service methods")
                    .tag("label", label)
                    .tag("domain", domain)
                    .register(meterRegistry)
                    .record(elapsedNs, TimeUnit.NANOSECONDS);
            log.info("tracked-execution label={} domain={} elapsedMs={}", label, domain, elapsedMs);
        }
    }
}
