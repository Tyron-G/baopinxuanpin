package com.oneaix.selection.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** 2026-06-04 品类关键词 */
class CategoryKeywordTest {

    @Test
    void shouldDetectContentDrivenCategory() {
        assertTrue(CategoryKeyword.isContentDriven("宠物智能用品"));
        assertTrue(CategoryKeyword.isContentDriven("便携式咖啡器具"));
        assertFalse(CategoryKeyword.isContentDriven("家用清洁机器人"));
    }

    @Test
    void shouldDetectSearchDrivenCategory() {
        assertTrue(CategoryKeyword.isSearchDriven("家用清洁机器人"));
        assertFalse(CategoryKeyword.isSearchDriven("宠物智能用品"));
    }
}
