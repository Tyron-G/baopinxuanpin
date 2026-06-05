package com.oneaix.selection.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** 品牌建档目标平台白名单 2026-06-04 */
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SupportedPlatformsValidator.class)
public @interface SupportedPlatforms {
    String message() default "目标平台仅支持天猫、淘宝、抖音、小红书及跨境平台";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
