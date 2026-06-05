package com.oneaix.selection.service.brand;

import com.oneaix.selection.dto.BrandRequest;
import com.oneaix.selection.enums.PlatformView;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.StringJoiner;

/** 品牌建档字段规范化（去重、平台白名单）2026-06-04 */
@Component
public class BrandProfileNormalizer {

    public NormalizedBrandProfile normalize(BrandRequest request) {
        List<String> platforms = PlatformView.normalizeSelection(request.targetPlatforms());
        List<String> excludes = dedupe(request.excludeCategories());
        return new NormalizedBrandProfile(
                request.brandName().trim(),
                request.industry().trim(),
                request.targetCategory() == null ? null : request.targetCategory().trim(),
                request.hasCategory(),
                request.interestDirection() == null ? null : request.interestDirection().trim(),
                join(platforms),
                request.budgetRange(),
                request.profitMin(),
                request.supplyChain(),
                request.stockCycle(),
                join(excludes),
                request.existingProducts() == null ? null : request.existingProducts().trim()
        );
    }

    private List<String> dedupe(List<String> values) {
        if (values == null || values.isEmpty()) {
            return List.of();
        }
        return new ArrayList<>(new LinkedHashSet<>(
                values.stream()
                        .map(value -> value == null ? "" : value.trim())
                        .filter(value -> !value.isEmpty())
                        .toList()
        ));
    }

    private String join(List<String> values) {
        if (values == null || values.isEmpty()) {
            return "";
        }
        StringJoiner joiner = new StringJoiner(",");
        values.forEach(joiner::add);
        return joiner.toString();
    }

    public record NormalizedBrandProfile(
            String brandName,
            String industry,
            String targetCategory,
            Boolean hasCategory,
            String interestDirection,
            String targetPlatforms,
            String budgetRange,
            String profitMin,
            String supplyChain,
            String stockCycle,
            String excludeCategories,
            String existingProducts
    ) {
    }
}
