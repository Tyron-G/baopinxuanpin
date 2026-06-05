package com.oneaix.selection.controller;

import com.oneaix.selection.constant.ApiConstants;
import com.oneaix.selection.dto.TestRunDiagnosis;
import com.oneaix.selection.service.testrun.TestRunDiagnosisService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** 测款优化诊断（背景场景扩展）2026-06-05 */
@Validated
@RestController
@RequestMapping("/api/test-run")
@Tag(name = "测款优化")
public class TestRunController {

    private final TestRunDiagnosisService diagnosisService;

    public TestRunController(TestRunDiagnosisService diagnosisService) {
        this.diagnosisService = diagnosisService;
    }

    @GetMapping("/diagnosis")
    public TestRunDiagnosis diagnosis(
            @RequestParam(defaultValue = ApiConstants.DEFAULT_BRAND_ID_PARAM) @Min(1) Long brandId,
            @RequestParam(defaultValue = "1") @Min(1) Long cardId,
            @RequestParam(defaultValue = ApiConstants.DEFAULT_PLATFORM_VIEW) String platform
    ) {
        return diagnosisService.diagnose(brandId, cardId, platform);
    }
}
