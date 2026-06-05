package com.oneaix.selection.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** 2026-06-04 品类名称匹配 */
class CategoryNameMatcherTest {

    @Test
    void shouldMatchExactAndPartialNames() {
        assertTrue(CategoryNameMatcher.matches("宠物智能用品", "宠物"));
        assertTrue(CategoryNameMatcher.matches("便携式咖啡器具", "咖啡器具"));
        assertFalse(CategoryNameMatcher.matches("宠物智能用品", "清洁"));
        assertFalse(CategoryNameMatcher.matches(null, "宠物"));
    }
}
