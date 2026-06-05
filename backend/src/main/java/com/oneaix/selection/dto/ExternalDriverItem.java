package com.oneaix.selection.dto;

/** 机会页外部驱动因素（政策/人口/技术/季节）2026-06-05 */
public record ExternalDriverItem(
        String driverType,
        String signal,
        String impact
) {
}
