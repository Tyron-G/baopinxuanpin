package com.oneaix.selection.enums;

/** 约束提示语气：匹配项 vs 预警项 2026-06-04 */
public enum ConstraintHintTone {
    MATCHED,
    WARNING;

    public static ConstraintHintTone fromHint(String hint) {
        if (hint == null || hint.isBlank()) {
            return MATCHED;
        }
        if (hint.contains("超出") || hint.contains("谨慎")) {
            return WARNING;
        }
        return MATCHED;
    }
}
