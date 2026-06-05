package com.oneaix.selection.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/** 应用配置绑定 2026-06-04 */
@Configuration
@EnableConfigurationProperties({PushProperties.class, MarketDataProperties.class})
public class SelectionConfig {
}
