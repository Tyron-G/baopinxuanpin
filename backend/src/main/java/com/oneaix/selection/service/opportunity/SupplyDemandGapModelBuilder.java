package com.oneaix.selection.service.opportunity;

import com.oneaix.selection.content.CategoryPlaybook;
import com.oneaix.selection.content.CategoryPlaybookRegistry;
import com.oneaix.selection.dto.SentimentTerm;
import com.oneaix.selection.dto.SupplyDemandGapModel;
import com.oneaix.selection.entity.CategoryTrend;
import com.oneaix.selection.entity.CompetitionData;
import com.oneaix.selection.entity.InsightCard;
import com.oneaix.selection.entity.SupplyDemand;
import com.oneaix.selection.enums.SentimentPolarity;
import com.oneaix.selection.util.PlatformMarketFilter;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;

/**
 * PRD 供需缺口：需求热度 ÷ 供给充分度 × 用户满意度缺口（内置样例口径）2026-06-05
 */
@Component
public class SupplyDemandGapModelBuilder {

    private final CategoryPlaybookRegistry playbookRegistry;

    public SupplyDemandGapModelBuilder(CategoryPlaybookRegistry playbookRegistry) {
        this.playbookRegistry = playbookRegistry;
    }

    public SupplyDemandGapModel build(
            InsightCard card,
            String platform,
            List<CategoryTrend> trends,
            List<SupplyDemand> supplyRows,
            List<CompetitionData> competitionRows
    ) {
        String categoryName = card.getCategoryName();
        CategoryTrend latestTrend = latestTrend(categoryName, platform, trends);
        SupplyDemand bestGap = bestGapBand(categoryName, platform, supplyRows);
        CompetitionData competition = pickCompetition(categoryName, platform, competitionRows);
        CategoryPlaybook playbook = playbookRegistry.resolve(card);

        double growth = latestTrend != null ? latestTrend.getGrowthRate().doubleValue() : 15.0;
        int searchVolume = latestTrend != null ? latestTrend.getSearchVolume() : 20000;
        double demandHeat = searchVolume * (1.0 + growth / 100.0) / 1000.0;

        int supplyCount = bestGap != null ? bestGap.getSupplyCount() : 400;
        double supplyAdequacy = Math.max(8.0, supplyCount * 100.0 / Math.max(1, searchVolume));
        double satisfactionGap = satisfactionGapFromPlaybook(playbook);

        double gapIndex = demandHeat / supplyAdequacy * satisfactionGap;
        String vacuum = bestGap != null ? bestGap.getPriceRange() : card.getPriceGap();
        String homogeneityHint = competition != null
                ? "同质化 " + competition.getHomogeneityScore() + "%"
                : "同质化样例口径";

        String summary = categoryName + " 缺口指数 " + scale(gapIndex)
                + "：需求热度 " + scale(demandHeat) + " ÷ 供给充分度 " + scale(supplyAdequacy)
                + " × 满意度缺口 " + scale(satisfactionGap)
                + "；优先验证 " + vacuum + "（" + homogeneityHint + "）。";

        return new SupplyDemandGapModel(
                scale(demandHeat),
                scale(supplyAdequacy),
                scale(satisfactionGap),
                scale(gapIndex),
                vacuum,
                summary
        );
    }

    private double satisfactionGapFromPlaybook(CategoryPlaybook playbook) {
        List<SentimentTerm> terms = playbook.sentimentTerms();
        if (terms.isEmpty()) {
            return 1.0;
        }
        double negative = 0;
        double total = 0;
        for (SentimentTerm term : terms) {
            int weight = term.value() == null ? 50 : term.value();
            total += weight;
            if (SentimentPolarity.NEGATIVE.getCode().equals(term.sentiment())) {
                negative += weight;
            }
        }
        double ratio = total > 0 ? negative / total : 0.5;
        return Math.max(0.35, Math.min(1.8, 0.6 + ratio));
    }

    private CategoryTrend latestTrend(String categoryName, String platform, List<CategoryTrend> trends) {
        return PlatformMarketFilter.byPlatform(trends, platform, CategoryTrend::getPlatform).stream()
                .filter(row -> categoryName.equals(row.getCategoryName()))
                .max(Comparator.comparing(CategoryTrend::getTrendMonth))
                .orElse(null);
    }

    private SupplyDemand bestGapBand(String categoryName, String platform, List<SupplyDemand> rows) {
        return PlatformMarketFilter.byPlatform(rows, platform, SupplyDemand::getPlatform).stream()
                .filter(row -> categoryName.equals(row.getCategoryName()))
                .max(Comparator.comparing(SupplyDemand::getDemandSupplyRatio))
                .orElse(null);
    }

    private CompetitionData pickCompetition(String categoryName, String platform, List<CompetitionData> rows) {
        return PlatformMarketFilter.byPlatform(rows, platform, CompetitionData::getPlatform).stream()
                .filter(row -> categoryName.equals(row.getCategoryName()))
                .findFirst()
                .orElse(null);
    }

    private double scale(double value) {
        return BigDecimal.valueOf(value).setScale(1, RoundingMode.HALF_UP).doubleValue();
    }
}
