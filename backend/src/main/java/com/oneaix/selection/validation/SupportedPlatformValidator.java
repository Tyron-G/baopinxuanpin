package com.oneaix.selection.validation;

import com.oneaix.selection.enums.PlatformView;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/** 2026-06-04 */
public class SupportedPlatformValidator implements ConstraintValidator<SupportedPlatform, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return PlatformView.isSupportedSelectionLabel(value);
    }
}
