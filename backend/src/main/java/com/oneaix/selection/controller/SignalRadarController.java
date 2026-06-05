package com.oneaix.selection.controller;

import com.oneaix.selection.constant.ApiConstants;
import jakarta.validation.constraints.Min;
import com.oneaix.selection.dto.SignalItem;
import com.oneaix.selection.service.SignalRadarService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/radar")
@Tag(name = "信号雷达")
public class SignalRadarController {
    private final SignalRadarService signalRadarService;

    public SignalRadarController(SignalRadarService signalRadarService) {
        this.signalRadarService = signalRadarService;
    }

    @GetMapping("/signals")
    public List<SignalItem> signals(@RequestParam(defaultValue = ApiConstants.DEFAULT_BRAND_ID_PARAM) @Min(1) Long brandId) {
        return signalRadarService.signals(brandId);
    }
}
