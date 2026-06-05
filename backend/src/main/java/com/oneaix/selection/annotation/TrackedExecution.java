package com.oneaix.selection.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** 关键服务执行耗时跟踪注解 2026-06-04 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TrackedExecution {
    String value() default "";

    String domain() default "other";
}
