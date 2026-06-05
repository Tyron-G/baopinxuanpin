package com.oneaix.selection.monitoring;

/** 2026-06-04 监控标签解析 */
public final class MetricsTags {

    private MetricsTags() {
    }

    public static String resolveDomain(String path) {
        if (path == null || path.isBlank()) {
            return "unknown";
        }
        if (path.startsWith("/api/brand")) {
            return "brand";
        }
        if (path.startsWith("/api/competitor")) {
            return "competitor";
        }
        if (path.startsWith("/api/opportunity")) {
            return "opportunity";
        }
        if (path.startsWith("/api/report")) {
            return "report";
        }
        if (path.startsWith("/api/insight")) {
            return "insight";
        }
        if (path.startsWith("/api/radar")) {
            return "radar";
        }
        if (path.startsWith("/actuator")) {
            return "actuator";
        }
        return "other";
    }

    public static String resolveExceptionType(Throwable throwable) {
        if (throwable == null) {
            return "none";
        }
        return throwable.getClass().getSimpleName();
    }
}
