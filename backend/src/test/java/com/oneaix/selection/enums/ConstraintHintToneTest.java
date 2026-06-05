package com.oneaix.selection.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** 2026-06-04 约束提示分类 */
class ConstraintHintToneTest {

    @Test
    void shouldClassifyWarningHints() {
        assertEquals(ConstraintHintTone.WARNING, ConstraintHintTone.fromHint("启动资金可能超出预算带"));
        assertEquals(ConstraintHintTone.WARNING, ConstraintHintTone.fromHint("建议谨慎评估"));
    }

    @Test
    void shouldClassifyMatchedHints() {
        assertEquals(ConstraintHintTone.MATCHED, ConstraintHintTone.fromHint("启动资金落在预算带内"));
    }
}
