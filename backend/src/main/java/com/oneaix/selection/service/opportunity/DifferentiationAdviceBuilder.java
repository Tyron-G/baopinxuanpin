package com.oneaix.selection.service.opportunity;

import com.oneaix.selection.content.CategoryPlaybook;
import com.oneaix.selection.dto.CompetitorShop;
import com.oneaix.selection.entity.BrandInfo;
import com.oneaix.selection.enums.PlatformView;
import com.oneaix.selection.enums.ProfitMin;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/** 差异化建议组装 2026-06-04 */
@Component
public class DifferentiationAdviceBuilder {

    public List<String> build(
            CategoryPlaybook playbook,
            BrandInfo brand,
            List<CompetitorShop> competitors,
            String platformView
    ) {
        List<String> advice = new ArrayList<>(playbook.differentiationAdvice());
        PlatformView platform = PlatformView.normalize(platformView);

        if (brand.getSupplyChain() == null || brand.getSupplyChain().isBlank()) {
            advice.add("在供应链未明确前，先选择不依赖复杂模组和大 MOQ 的体验改良型切口。");
        }
        if (ProfitMin.ABOVE_25 == ProfitMin.fromLabel(brand.getProfitMin())) {
            advice.add("利润目标较高，建议优先验证高感知卖点是否足够支撑溢价，再决定是否立项。");
        }
        advice.add(switch (platform) {
            case DOUYIN -> "抖音视角下优先把差异化表达压缩成 1-2 个能在前 3 秒讲清的卖点。";
            case TMALL -> "天猫视角下优先强化参数稳定性、质保承诺和详情页结构化说明，减少决策阻力。";
            case XIAOHONGSHU -> "小红书视角下优先包装生活方式和情绪价值，让卖点更容易被自然种草。";
            default -> null;
        });
        advice.removeIf(item -> item == null);
        if (!competitors.isEmpty()) {
            String joined = competitors.stream()
                    .flatMap(shop -> shop.complaintTopics().stream())
                    .distinct()
                    .limit(3)
                    .reduce((left, right) -> left + "、" + right)
                    .orElse("差评主题");
            advice.add("关联竞品里仍反复出现「" + joined + "」问题，可以作为首轮差异化切入的优先验证点。");
        }

        return advice.stream().distinct().limit(4).toList();
    }
}
