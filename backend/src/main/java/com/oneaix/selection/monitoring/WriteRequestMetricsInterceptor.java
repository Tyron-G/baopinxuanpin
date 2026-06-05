package com.oneaix.selection.monitoring;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import java.util.Set;

/** 2026-06-04 写接口请求计数器 */
@Component
public class WriteRequestMetricsInterceptor implements HandlerInterceptor {
    private static final Set<String> WRITE_METHODS = Set.of("POST", "PUT", "PATCH", "DELETE");

    private final MeterRegistry meterRegistry;

    public WriteRequestMetricsInterceptor(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        if (!WRITE_METHODS.contains(request.getMethod())) {
            return;
        }
        String endpoint = resolveEndpoint(request);
        String domain = MetricsTags.resolveDomain(request.getRequestURI());
        String status = String.valueOf(response.getStatus());
        String outcome = ex == null && response.getStatus() < 400 ? "success" : "error";
        Counter.builder("selection.write.requests")
                .description("Write request counts for API endpoints")
                .tag("method", request.getMethod())
                .tag("endpoint", endpoint)
                .tag("domain", domain)
                .tag("outcome", outcome)
                .tag("status", status)
                .register(meterRegistry)
                .increment();
    }

    private String resolveEndpoint(HttpServletRequest request) {
        Object pattern = request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        if (pattern instanceof String patternValue && !patternValue.isBlank()) {
            return patternValue;
        }
        return request.getRequestURI();
    }
}
