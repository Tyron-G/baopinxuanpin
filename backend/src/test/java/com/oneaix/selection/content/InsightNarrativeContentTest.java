package com.oneaix.selection.content;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/** 2026-06-04 洞察叙事内容 */
class InsightNarrativeContentTest {

    @Test
    void shouldExposeSummaryPainPoints() {
        assertEquals(5, InsightNarrativeContent.summaryPainPoints().size());
        assertEquals(5, InsightNarrativeContent.summaryPainPointItems().size());
        assertFalse(InsightNarrativeContent.summaryPainPoints().get(0).isBlank());
    }
}

