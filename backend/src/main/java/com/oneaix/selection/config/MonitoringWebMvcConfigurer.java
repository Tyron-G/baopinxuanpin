package com.oneaix.selection.config;

import com.oneaix.selection.monitoring.WriteRequestMetricsInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** 2026-06-04 注册写接口指标拦截器 */
@Configuration
public class MonitoringWebMvcConfigurer implements WebMvcConfigurer {

    private final WriteRequestMetricsInterceptor writeRequestMetricsInterceptor;

    public MonitoringWebMvcConfigurer(WriteRequestMetricsInterceptor writeRequestMetricsInterceptor) {
        this.writeRequestMetricsInterceptor = writeRequestMetricsInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(writeRequestMetricsInterceptor)
                .addPathPatterns("/api/**");
    }
}
