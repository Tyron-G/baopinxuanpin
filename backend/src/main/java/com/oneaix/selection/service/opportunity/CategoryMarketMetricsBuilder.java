package com.oneaix.selection.service.opportunity;

import com.oneaix.selection.dto.CategoryMarketMetrics;
import com.oneaix.selection.entity.CompetitionData;
import com.oneaix.selection.util.PlatformMarketFilter;
import org.springframework.stereotype.Component;

import java.util.List;

/** 从竞争数据提取机会页 SKU/同质化指标 2026-06-05 */
@Component
public class CategoryMarketMetricsBuilder {

    public CategoryMarketMetrics build(String categoryName, String platform, List<CompetitionData> rows) {
        CompetitionData row = PlatformMarketFilter.byPlatform(rows, platform, CompetitionData::getPlatform).stream()
                .filter(item -> categoryName.equals(item.getCategoryName()))
                .findFirst()
                .orElse(null);

        if (row == null) {
            int seed = Math.abs(categoryName.hashCode());
            return new CategoryMarketMetrics(
                    platform,
                    900 + seed % 600,
                    35.0,
                    48.0,
                    32000 + seed % 8000,
                    categoryName + " 暂无分平台竞争样例，展示内置默认口径。"
            );
        }

        String summary = categoryName + " 在 " + platform + " 在售 SKU 约 "
                + row.getTotalSkuCount() + "，Top10 销量占比 "
                + row.getTop10SalesRatio() + "%，同质化评分 "
                + row.getHomogeneityScore() + "（越低越易差异化）。";

        return new CategoryMarketMetrics(
                platform,
                row.getTotalSkuCount(),
                row.getTop10SalesRatio().doubleValue(),
                row.getHomogeneityScore().doubleValue(),
                row.getTotalSearchVolume(),
                summary
        );
    }
}
