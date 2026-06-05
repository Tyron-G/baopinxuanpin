package com.oneaix.selection.dto;

/** 2026-06-03 约束冲突项 */
public record ConstraintMismatch(
        String type,
        String message,
        String severity
) {
}
