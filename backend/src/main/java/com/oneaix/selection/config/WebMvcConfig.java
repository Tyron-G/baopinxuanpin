package com.oneaix.selection.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Web MVC 配置 2026-06-04 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final OpenApiKeyInterceptor openApiKeyInterceptor;

    public WebMvcConfig(OpenApiKeyInterceptor openApiKeyInterceptor) {
        this.openApiKeyInterceptor = openApiKeyInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(openApiKeyInterceptor).addPathPatterns("/api/open/v1/**");
    }
}
