package com.oneaix.selection.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** OpenAPI 文档配置 2026-06-04 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI selectionOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("爆品选品雷达 API")
                        .description("本地 MVP · selection-service · 内置样例数据 + 规则引擎")
                        .version("0.1.0"));
    }
}
