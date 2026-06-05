package com.oneaix.selection.service.opportunity;

import com.oneaix.selection.dto.CompetitionQuadrantReport;
import com.oneaix.selection.dto.CompetitorShop;
import com.oneaix.selection.dto.PriceFunctionPoint;
import com.oneaix.selection.entity.InsightCard;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** 竞争格局四象限：按价格×功能分布识别空白区间 2026-06-05 */
@Component
public class CompetitionQuadrantBuilder {

    private static final double MID = 50.0;

    public CompetitionQuadrantReport build(InsightCard card, List<CompetitorShop> competitors) {
        List<PriceFunctionPoint> points = new ArrayList<>();
        Set<String> seen = new LinkedHashSet<>();
        for (CompetitorShop shop : competitors) {
            if (!seen.add(shop.shopName())) {
                continue;
            }
            double priceIndex = normalizePrice(shop);
            double functionIndex = normalizeFunction(shop);
            points.add(new PriceFunctionPoint(
                    shop.shopName(),
                    priceIndex,
                    functionIndex,
                    quadrant(priceIndex, functionIndex),
                    shop.hitProductCount() >= 3 ? "头部样本" : "跟随样本"
            ));
        }
        if (points.isEmpty()) {
            points.add(syntheticGapPoint(card.getCategoryName()));
        } else {
            points.add(syntheticOpportunityPoint(card));
        }

        Map<String, Integer> counts = new HashMap<>();
        for (Quadrant quadrant : Quadrant.values()) {
            counts.put(quadrant.label(), 0);
        }
        for (PriceFunctionPoint point : points) {
            counts.merge(point.quadrant(), 1, Integer::sum);
        }
        String blankZone = counts.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("高价高功能");
        String summary = card.getCategoryName() + " 在「" + blankZone + "」象限竞品密度最低，"
                + "建议优先验证该价格×功能组合的差异化表达（内置样例分布）。";
        return new CompetitionQuadrantReport(points, blankZone, summary);
    }

    private double normalizePrice(CompetitorShop shop) {
        int seed = Math.abs(shop.shopName().hashCode());
        double base = 25 + seed % 55;
        if (shop.latestHit() != null && shop.latestHit().contains("旗舰")) {
            base += 12;
        }
        return Math.min(95, base);
    }

    private double normalizeFunction(CompetitorShop shop) {
        double base = 30 + shop.hitProductCount() * 9.0 + shop.complaintTopics().size() * 4.5;
        if (shop.opportunityTags() != null) {
            base += shop.opportunityTags().size() * 3;
        }
        return Math.min(95, base);
    }

    private PriceFunctionPoint syntheticGapPoint(String categoryName) {
        return new PriceFunctionPoint(
                categoryName + " 机会带",
                62,
                68,
                quadrant(62, 68),
                "建议验证区"
        );
    }

    private PriceFunctionPoint syntheticOpportunityPoint(InsightCard card) {
        int seed = Math.abs(card.getCategoryName().hashCode());
        double price = 55 + seed % 20;
        double function = 58 + seed % 18;
        return new PriceFunctionPoint(
                "可切入空白带",
                price,
                function,
                quadrant(price, function),
                "系统建议"
        );
    }

    private String quadrant(double priceIndex, double functionIndex) {
        boolean highPrice = priceIndex >= MID;
        boolean highFunction = functionIndex >= MID;
        if (!highPrice && !highFunction) {
            return Quadrant.LOW_PRICE_LOW_FUNCTION.label();
        }
        if (!highPrice && highFunction) {
            return Quadrant.LOW_PRICE_HIGH_FUNCTION.label();
        }
        if (highPrice && !highFunction) {
            return Quadrant.HIGH_PRICE_LOW_FUNCTION.label();
        }
        return Quadrant.HIGH_PRICE_HIGH_FUNCTION.label();
    }

    private enum Quadrant {
        LOW_PRICE_LOW_FUNCTION("低价低功能"),
        LOW_PRICE_HIGH_FUNCTION("低价高功能"),
        HIGH_PRICE_LOW_FUNCTION("高价低功能"),
        HIGH_PRICE_HIGH_FUNCTION("高价高功能");

        private final String label;

        Quadrant(String label) {
            this.label = label;
        }

        String label() {
            return label;
        }
    }
}
