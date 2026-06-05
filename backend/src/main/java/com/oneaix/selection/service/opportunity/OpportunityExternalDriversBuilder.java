package com.oneaix.selection.service.opportunity;

import com.oneaix.selection.dto.ExternalDriverItem;
import com.oneaix.selection.entity.InsightCard;
import org.springframework.stereotype.Component;

import java.util.List;

/** PRD 外部驱动因素专节（政策/人口/技术/季节）2026-06-05 */
@Component
public class OpportunityExternalDriversBuilder {

    public List<ExternalDriverItem> build(InsightCard card) {
        String category = card.getCategoryName();
        String growth = card.getMarketGrowth() == null ? "+20%" : card.getMarketGrowth();
        return List.of(
                new ExternalDriverItem(
                        "政策/法规",
                        category.contains("母婴") ? "婴童用品安全标准趋严" : "平台新品扶持与合规要求并行",
                        "利好有认证能力的品牌，抬高无资质白牌进入成本"
                ),
                new ExternalDriverItem(
                        "人口/生活方式",
                        category.contains("宠物") ? "养宠家庭渗透率持续提升" : "轻户外与办公室场景消费增加",
                        "拉长结构性需求周期，非纯季节炒作"
                ),
                new ExternalDriverItem(
                        "技术/产品升级",
                        category.contains("智能") || category.contains("数码")
                                ? "IoT 与快充规格迭代窗口仍在"
                                : "材料与结构件轻量化为差异化切入点",
                        "适合用「稳定体验 + 轻创新」切入，而非纯参数堆叠"
                ),
                new ExternalDriverItem(
                        "季节 vs 结构",
                        "当前搜索增速 " + growth,
                        growth.contains("8") || growth.contains("9")
                                ? "偏结构性需求，可规划季度节奏"
                                : "存在季节波峰，建议预留备货弹性"
                )
        );
    }
}
