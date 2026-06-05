package com.oneaix.selection.service.report;

import com.oneaix.selection.dto.OpportunityDetail;
import com.oneaix.selection.dto.ReportActionSummary;
import com.oneaix.selection.dto.ReportRiskSummary;
import com.oneaix.selection.dto.report.ReportMarkdownInput;
import com.oneaix.selection.entity.BrandInfo;
import com.oneaix.selection.entity.InsightCard;
import com.oneaix.selection.enums.PlatformView;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/** 2026-06-04 报告 Markdown 渲染测试 */
class ReportMarkdownRendererTest {
    private final ReportMarkdownRenderer renderer = new ReportMarkdownRenderer(new ReportMarkdownTemplateLoader());

    @Test
    void shouldRenderCoreSections() {
        BrandInfo brand = new BrandInfo();
        brand.setBrandName("测试品牌");
        brand.setIndustry("宠物用品");
        brand.setTargetPlatforms("天猫,抖音");

        InsightCard card = new InsightCard();
        card.setCategoryName("宠物智能用品");
        card.setMarketSize("10亿");
        card.setMarketGrowth("+30%");
        card.setRecommendation("推荐立项");

        OpportunityDetail detail = mock(OpportunityDetail.class);
        when(detail.insightCard()).thenReturn(card);
        when(detail.points()).thenReturn(List.of());
        when(detail.profitAnalysis()).thenReturn(new com.oneaix.selection.dto.ProfitAnalysis(
                "129-199 元", "52 元", "8%", "18%", "20%", "利润可行"));
        when(detail.supplyChainFeasibility()).thenReturn(new com.oneaix.selection.dto.SupplyChainFeasibility(
                "300 件", "30 天", "月产 1 万", "低风险", "可试产"));
        when(detail.competitorSummary()).thenReturn(new com.oneaix.selection.dto.CompetitorSummary(
                0, "暂无", 0, List.of(), "暂无", "暂无"));
        when(detail.platformPlaybook()).thenReturn(new com.oneaix.selection.dto.PlatformPlaybook(
                "天猫", "抖音", "天猫", List.of("先测款"), "平台建议"));
        when(detail.differentiationAdvice()).thenReturn(List.of());
        when(detail.relatedCompetitors()).thenReturn(List.of());

        String markdown = renderer.render(new ReportMarkdownInput(
                "测试品牌 · 宠物智能用品 选品报告",
                "2026-06-04 12:00",
                PlatformView.ALL,
                brand,
                detail,
                List.of(),
                List.of(),
                List.of(),
                null,
                new ReportRiskSummary(0, "low", "无", "无", "关注", "摘要"),
                new ReportActionSummary(0, 0, 0, 0, "无", "-", "-", "摘要"),
                List.of(),
                (shop, platform) -> "基础跟踪"
        ));

        assertTrue(markdown.contains("## 一、品牌约束"));
        assertTrue(markdown.contains("## 十三、下一步动作"));
        assertTrue(markdown.contains("宠物智能用品"));
    }
}
