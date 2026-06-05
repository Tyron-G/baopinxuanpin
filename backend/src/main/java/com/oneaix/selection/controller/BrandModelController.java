package com.oneaix.selection.controller;

import com.oneaix.selection.constant.ApiConstants;
import com.oneaix.selection.dto.BrandSelectionModelProfile;
import com.oneaix.selection.service.model.BrandSelectionModelService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** 品牌专属模型 2026-06-04 */
@Validated
@RestController
@RequestMapping("/api/brand-model")
@Tag(name = "品牌专属模型")
public class BrandModelController {

    private final BrandSelectionModelService modelService;

    public BrandModelController(BrandSelectionModelService modelService) {
        this.modelService = modelService;
    }

    @GetMapping
    public BrandSelectionModelProfile profile(
            @RequestParam(defaultValue = ApiConstants.DEFAULT_BRAND_ID_PARAM) @Min(1) Long brandId
    ) {
        return modelService.profile(brandId);
    }

    @PostMapping("/retrain")
    public BrandSelectionModelProfile retrain(
            @RequestParam(defaultValue = ApiConstants.DEFAULT_BRAND_ID_PARAM) @Min(1) Long brandId
    ) {
        return modelService.retrain(brandId);
    }
}
