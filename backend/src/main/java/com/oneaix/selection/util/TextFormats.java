package com.oneaix.selection.util;

/** 展示用文本格式化 2026-06-04 */
public final class TextFormats {

    private TextFormats() {
    }

    public static String nullToDash(String value) {
        return value == null || value.isBlank() ? "未设置" : value;
    }

    public static String abbreviate(String value, int max) {
        if (value == null || value.isBlank()) {
            return "未填写";
        }
        if (value.length() <= max) {
            return value;
        }
        return value.substring(0, max) + "…";
    }
}
