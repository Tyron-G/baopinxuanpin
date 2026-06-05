package com.oneaix.selection.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** 推送/Webhook 配置 2026-06-04 */
@ConfigurationProperties(prefix = "selection.push")
public class PushProperties {

    /** 为 true 时对演示 Webhook 模拟外发成功并落库 */
    private boolean simulateDelivery = true;

    public boolean isSimulateDelivery() {
        return simulateDelivery;
    }

    public void setSimulateDelivery(boolean simulateDelivery) {
        this.simulateDelivery = simulateDelivery;
    }
}
