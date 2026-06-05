package com.oneaix.selection.service.opportunity;

import com.oneaix.selection.dto.CompetitorShop;
import com.oneaix.selection.dto.PlatformPlaybook;
import com.oneaix.selection.entity.BrandInfo;
import com.oneaix.selection.entity.InsightCard;
import com.oneaix.selection.enums.CategoryKeyword;
import com.oneaix.selection.enums.PlatformView;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/** 平台打法建议组装 2026-06-04 */
@Component
public class PlatformPlaybookBuilder {

    public PlatformPlaybook build(BrandInfo brand, InsightCard card, List<CompetitorShop> competitors) {
        List<String> preferredPlatforms = PlatformView.parseCsv(brand.getTargetPlatforms());
        boolean contentDriven = CategoryKeyword.isContentDriven(card.getCategoryName());
        boolean searchDriven = CategoryKeyword.isSearchDriven(card.getCategoryName());

        String firstLaunch = pickPlatform(preferredPlatforms,
                contentDriven
                        ? List.of(PlatformView.DOUYIN.getLabel(), PlatformView.XIAOHONGSHU.getLabel(), PlatformView.TMALL.getLabel())
                        : List.of(PlatformView.TMALL.getLabel(), PlatformView.TAOBAO.getLabel(), PlatformView.DOUYIN.getLabel()));
        String validation = pickPlatform(preferredPlatforms,
                searchDriven
                        ? List.of(PlatformView.TMALL.getLabel(), PlatformView.TAOBAO.getLabel(), PlatformView.DOUYIN.getLabel())
                        : List.of(PlatformView.DOUYIN.getLabel(), PlatformView.XIAOHONGSHU.getLabel(), PlatformView.TMALL.getLabel()));
        String conversion = pickPlatform(preferredPlatforms,
                searchDriven
                        ? List.of(PlatformView.TMALL.getLabel(), PlatformView.TAOBAO.getLabel(), PlatformView.DOUYIN.getLabel())
                        : List.of(PlatformView.TMALL.getLabel(), PlatformView.DOUYIN.getLabel(), PlatformView.TAOBAO.getLabel()));

        List<String> executionHints = new ArrayList<>();
        executionHints.add("首发平台优先上 1 个核心卖点版本，避免同时铺太多 SKU 拉高试错成本。");
        if (contentDriven) {
            executionHints.add("内容验证阶段优先测试痛点场景素材，再决定是否扩展到达人分发。");
        } else {
            executionHints.add("验证阶段重点看搜索承接与详情页转化，不要只看内容曝光。");
        }
        if (!competitors.isEmpty()) {
            executionHints.add("转化平台上线前，先复用已跟踪竞品的高频差评主题，补齐「"
                    + competitors.get(0).complaintTopics().stream().findFirst().orElse("核心痛点") + "」对应卖点。");
        }

        String summary = "建议以「" + firstLaunch + "」作为首发平台，用「" + validation + "」验证内容/搜索反馈，"
                + "再把转化重心放到「" + conversion + "」。"
                + (contentDriven
                ? " 该类目更依赖场景种草和情绪表达。"
                : " 该类目更依赖稳定搜索需求和货架转化。");

        return new PlatformPlaybook(
                firstLaunch,
                validation,
                conversion,
                executionHints.stream().distinct().limit(3).toList(),
                summary
        );
    }

    private String pickPlatform(List<String> preferredPlatforms, List<String> fallbackOrder) {
        for (String option : fallbackOrder) {
            if (preferredPlatforms.contains(option)) {
                return option;
            }
        }
        return fallbackOrder.get(0);
    }
}
