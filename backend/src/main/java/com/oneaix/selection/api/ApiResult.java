package com.oneaix.selection.api;

/** 统一 API 响应包装 2026-06-04 */
public record ApiResult<T>(
        int code,
        String message,
        T data,
        String path,
        long timestamp
) {
    public static final int SUCCESS_CODE = 0;

    public static <T> ApiResult<T> ok(T data, String path) {
        return new ApiResult<>(SUCCESS_CODE, "ok", data, path, System.currentTimeMillis());
    }

    public static <T> ApiResult<T> fail(int code, String message, String path) {
        return new ApiResult<>(code, message, null, path, System.currentTimeMillis());
    }
}
