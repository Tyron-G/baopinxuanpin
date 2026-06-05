package com.oneaix.selection.service.opportunity;

import com.oneaix.selection.dto.CompetitionReport;
import com.oneaix.selection.dto.EntryBarrierAssessment;
import com.oneaix.selection.dto.OpportunityMarketContext;
import com.oneaix.selection.dto.PatentIntel;
import com.oneaix.selection.dto.SupplyChainFeasibility;
import com.oneaix.selection.entity.InsightCard;
import org.springframework.stereotype.Component;

/** 进入壁垒四项综合判断（内置样例）2026-06-05 */
@Component
public class EntryBarrierAssessmentBuilder {

    public EntryBarrierAssessment build(
            InsightCard card,
            CompetitionReport competitionReport,
            OpportunityMarketContext marketContext,
            PatentIntel patentIntel,
            SupplyChainFeasibility supplyChain
    ) {
        String category = card.getCategoryName();
        int seed = Math.abs(category.hashCode());
        String listingCycle = (38 + seed % 28) + " 天";
        String commentThreshold = (800 + seed % 2200) + " 条（Top20 均值）";
        String cpcBarrier = marketContext.cpcVsCategory();
        String patentBarrier = "专利风险 " + patentIntel.riskLevel() + "：" + patentIntel.summary();
        String supplyBarrier = "MOQ " + supplyChain.moq() + " · " + supplyChain.riskHint();

        int barrierScore = seed % 100;
        if (patentIntel.riskLevel().contains("高")) {
            barrierScore += 25;
        }
        if (competitionReport.marketType().contains("深红")) {
            barrierScore += 20;
        }
        String overall = barrierScore >= 70 ? "高" : barrierScore >= 45 ? "中" : "低";
        String summary = category + " 进入壁垒综合「" + overall + "」：上榜周期约 "
                + listingCycle + "，头部评论门槛 " + commentThreshold
                + "；CPC/专利/供应链需同步验证（内置样例口径）。";

        return new EntryBarrierAssessment(
                listingCycle,
                commentThreshold,
                cpcBarrier,
                patentBarrier,
                supplyBarrier,
                overall,
                summary
        );
    }
}
