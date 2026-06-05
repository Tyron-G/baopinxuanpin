package com.oneaix.selection.validation;

import com.oneaix.selection.enums.PlatformView;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

/** 2026-06-04 */
public class SupportedPlatformsValidator implements ConstraintValidator<SupportedPlatforms, List<String>> {

    @Override
    public boolean isValid(List<String> platforms, ConstraintValidatorContext context) {
        if (platforms == null || platforms.isEmpty()) {
            return false;
        }
        return platforms.stream().allMatch(PlatformView::isSupportedSelectionLabel);
    }
}
