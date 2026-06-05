package com.oneaix.selection.controller;

import com.oneaix.selection.constant.ApiConstants;
import com.oneaix.selection.dto.SupplyMatchItem;
import com.oneaix.selection.service.supply.SupplyMatchService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** 供应链匹配 P2 2026-06-05 */
@Validated
@RestController
@RequestMapping("/api/supply")
@Tag(name = "供应链匹配")
public class SupplyMatchController {

    private final SupplyMatchService supplyMatchService;

    public SupplyMatchController(SupplyMatchService supplyMatchService) {
        this.supplyMatchService = supplyMatchService;
    }

    @GetMapping("/matches")
    public List<SupplyMatchItem> matches(
            @RequestParam(defaultValue = ApiConstants.DEFAULT_BRAND_ID_PARAM) @Min(1) Long brandId,
            @RequestParam @Min(1) Long cardId,
            @RequestParam(defaultValue = ApiConstants.DEFAULT_PLATFORM_VIEW) String platform
    ) {
        return supplyMatchService.matches(brandId, cardId, platform);
    }
}
