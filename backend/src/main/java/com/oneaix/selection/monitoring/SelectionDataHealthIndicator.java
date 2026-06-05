package com.oneaix.selection.monitoring;

import com.oneaix.selection.mapper.BrandInfoMapper;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/** 2026-06-04 业务样例数据健康检查 */
@Component
public class SelectionDataHealthIndicator implements HealthIndicator {

    private final BrandInfoMapper brandInfoMapper;

    public SelectionDataHealthIndicator(BrandInfoMapper brandInfoMapper) {
        this.brandInfoMapper = brandInfoMapper;
    }

    @Override
    public Health health() {
        long brandCount = brandInfoMapper.selectCount(null);
        if (brandCount <= 0) {
            return Health.down()
                    .withDetail("reason", "brand 数据为空")
                    .withDetail("brandCount", brandCount)
                    .build();
        }
        return Health.up()
                .withDetail("brandCount", brandCount)
                .withDetail("seedDataReady", true)
                .build();
    }
}
