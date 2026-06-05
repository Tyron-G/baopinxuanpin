package com.oneaix.selection.dto;

import java.util.List;

public record ConstraintMatch(
        List<String> matched,
        List<String> warnings,
        BrandFitDetail brandFitDetail,
        List<ConstraintMismatch> mismatches
) {
}
