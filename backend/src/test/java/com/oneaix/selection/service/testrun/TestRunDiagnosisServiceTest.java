package com.oneaix.selection.service.testrun;

import com.oneaix.selection.dto.TestRunDiagnosis;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/** 2026-06-05 测款诊断样例 */
@SpringBootTest
class TestRunDiagnosisServiceTest {

    @Autowired
    private TestRunDiagnosisService diagnosisService;

    @Test
    void shouldDiagnoseRecommendCard() {
        TestRunDiagnosis diagnosis = diagnosisService.diagnose(1L, 1L, "全平台");
        assertNotNull(diagnosis);
        assertEquals(1L, diagnosis.cardId());
        assertFalse(diagnosis.metrics().isEmpty());
        assertEquals("建议加投", diagnosis.verdict());
    }
}
