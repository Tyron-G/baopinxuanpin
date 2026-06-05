package com.oneaix.selection.config;

import com.oneaix.selection.exception.BadRequestException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/** 开放 API Key 校验（迭代2）2026-06-04 */
@Component
public class OpenApiKeyInterceptor implements HandlerInterceptor {

    @Value("${selection.open-api.key:selection-open-demo}")
    private String apiKey;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String provided = request.getHeader("X-Api-Key");
        if (provided == null || provided.isBlank()) {
            provided = request.getParameter("apiKey");
        }
        if (apiKey.equals(provided)) {
            return true;
        }
        throw new BadRequestException("无效的开放 API Key");
    }
}
