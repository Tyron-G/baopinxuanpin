package com.oneaix.selection.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** 2026-06-04 文本格式化 */
class TextFormatsTest {

    @Test
    void shouldFormatNullToDash() {
        assertEquals("未设置", TextFormats.nullToDash(null));
        assertEquals("天猫", TextFormats.nullToDash("天猫"));
    }

    @Test
    void shouldAbbreviateLongText() {
        String longText = "一二三四五六七八九十";
        assertTrue(TextFormats.abbreviate(longText, 5).endsWith("…"));
        assertEquals("短", TextFormats.abbreviate("短", 5));
    }
}
