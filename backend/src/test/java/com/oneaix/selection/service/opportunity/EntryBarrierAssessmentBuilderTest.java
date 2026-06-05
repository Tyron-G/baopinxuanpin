package com.oneaix.selection.service.opportunity;

import com.oneaix.selection.dto.CompetitionReport;
import com.oneaix.selection.dto.OpportunityMarketContext;
import com.oneaix.selection.dto.PatentIntel;
import com.oneaix.selection.dto.SupplyChainFeasibility;
import com.oneaix.selection.entity.InsightCard;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** 2026-06-05 EntryBarrierAssessmentBuilder */
class EntryBarrierAssessmentBuilderTest {

    private final EntryBarrierAssessmentBuilder builder = new EntryBarrierAssessmentBuilder();

    @Test
    void shouldExposeListingCycleAndCommentThreshold() {
        InsightCard card = new InsightCard();
        card.setCategoryName("宠物智能用品");
        var result = builder.build(
                card,
                new CompetitionReport("浅蓝海", "18%", "28%", "6-12 个月", "竞争分散"),
                new OpportunityMarketContext("低", "低于均值", "18%", "1:4", "扶持", "TikTok", "summary"),
                new PatentIntel("低", "无高风险", List.of(), "合享样例", "2026-06-05", "宠物智能", List.of()),
                new SupplyChainFeasibility("300", "30天", "1万件", "联调风险", "可试产")
        );
        assertTrue(result.newProductListingCycle().contains("天"));
        assertTrue(result.topCommentThreshold().contains("条"));
        assertFalse(result.summary().isBlank());
    }
}
