package com.oneaix.selection.monitoring;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

/** 2026-06-04 异常响应计数器 */
@Component
public class ExceptionMetricsRecorder {

    private final MeterRegistry meterRegistry;

    public ExceptionMetricsRecorder(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void record(String path, int status, String errorCode, Throwable throwable) {
        Counter.builder("selection.error.responses")
                .description("Error response counts grouped by domain and exception type")
                .tag("domain", MetricsTags.resolveDomain(path))
                .tag("error_code", errorCode == null || errorCode.isBlank() ? "unknown_error" : errorCode)
                .tag("exception", MetricsTags.resolveExceptionType(throwable))
                .tag("status", String.valueOf(status))
                .register(meterRegistry)
                .increment();
    }
}
