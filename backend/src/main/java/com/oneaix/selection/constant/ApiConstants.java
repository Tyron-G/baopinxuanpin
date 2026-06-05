package com.oneaix.selection.constant;

/** API 默认参数常量 2026-06-04 */
public final class ApiConstants {
    public static final long DEFAULT_BRAND_ID = 1L;
    /** 平台类目目录所属品牌（内置类目库） */
    public static final long CATALOG_BRAND_ID = 1L;
    /** 供 @RequestParam(defaultValue) 使用 */
    public static final String DEFAULT_BRAND_ID_PARAM = "1";
    /** 与 {@link PlatformView#ALL} 标签一致，注解 defaultValue 须为字面量 */
    public static final String DEFAULT_PLATFORM_VIEW = "全平台";

    private ApiConstants() {
    }
}
