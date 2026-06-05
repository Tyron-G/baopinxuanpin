package com.oneaix.selection.dto;

import com.oneaix.selection.validation.SupportedPlatforms;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record BrandRequest(
        @NotBlank String brandName,
        @NotBlank String industry,
        String targetCategory,
        @NotNull Boolean hasCategory,
        String interestDirection,
        @SupportedPlatforms List<String> targetPlatforms,
        String budgetRange,
        String profitMin,
        String supplyChain,
        String stockCycle,
        List<String> excludeCategories,
        String existingProducts
) {
}
