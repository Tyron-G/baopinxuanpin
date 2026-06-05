package com.oneaix.selection.config;

import com.oneaix.selection.dto.BrandSelectionContext;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import java.util.function.Supplier;

/** 单次 HTTP 请求内品牌上下文缓存 2026-06-04 */
@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class RequestScopedSelectionContext {
    private Long brandId;
    private BrandSelectionContext context;

    public BrandSelectionContext getOrLoad(Long requestedBrandId, Supplier<BrandSelectionContext> loader) {
        if (context == null || brandId == null || !brandId.equals(requestedBrandId)) {
            brandId = requestedBrandId;
            context = loader.get();
        }
        return context;
    }
}
