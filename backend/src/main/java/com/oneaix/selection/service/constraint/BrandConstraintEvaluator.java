package com.oneaix.selection.service.constraint;

import com.oneaix.selection.dto.ConstraintMismatch;
import com.oneaix.selection.entity.BrandInfo;
import com.oneaix.selection.entity.InsightCard;
import com.oneaix.selection.enums.BudgetRange;
import com.oneaix.selection.enums.CategoryKeyword;
import com.oneaix.selection.enums.CompetitionLevel;
import com.oneaix.selection.enums.ConstraintType;
import com.oneaix.selection.enums.PlatformView;
import com.oneaix.selection.enums.RiskLevel;
import com.oneaix.selection.enums.StockCycle;
import com.oneaix.selection.service.BrandFitEvaluator;
import com.oneaix.selection.util.CategoryNameMatcher;
import com.oneaix.selection.util.TextFormats;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/** 品牌约束校验与卡片筛选 2026-06-04 */
@Component
public class BrandConstraintEvaluator {

    private final BrandFitEvaluator brandFitEvaluator;

    public BrandConstraintEvaluator(BrandFitEvaluator brandFitEvaluator) {
        this.brandFitEvaluator = brandFitEvaluator;
    }

    public Set<String> resolveVisibleCategories(BrandInfo brand, List<String> allCategories) {
        Set<String> excludes = parseCsv(brand.getExcludeCategories());
        return allCategories.stream()
                .filter(category -> excludes.stream().noneMatch(ex -> matchesExclude(category, ex)))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public List<InsightCard> filterAndRankCards(BrandInfo brand, List<InsightCard> catalog) {
        List<String> allNames = catalog.stream().map(InsightCard::getCategoryName).toList();
        Set<String> visible = resolveVisibleCategories(brand, allNames);

        return catalog.stream()
                .filter(card -> visible.contains(card.getCategoryName()))
                .sorted(buildCardComparator(brand))
                .toList();
    }

    public boolean isBudgetCompatible(BrandInfo brand, InsightCard card) {
        int budgetMax = BudgetRange.maxWanFromLabel(brand.getBudgetRange());
        int startupMin = startupMinWan(card.getEstimatedStartupCost());
        if (budgetMax <= 0 || startupMin <= 0) {
            return true;
        }
        return startupMin <= budgetMax;
    }

    public boolean isPlatformCompatible(BrandInfo brand, InsightCard card) {
        String platforms = brand.getTargetPlatforms();
        if (platforms == null || platforms.isBlank()) {
            return true;
        }
        if (PlatformView.onlyDouyin(platforms) && isSearchDrivenCategory(card)) {
            return false;
        }
        if (PlatformView.onlyShelfSearch(platforms) && isContentDrivenCategory(card)) {
            return false;
        }
        return true;
    }

    public boolean isProfitCompatible(BrandInfo brand, InsightCard card) {
        return brandFitEvaluator.isProfitCompatible(brand, card);
    }

    public boolean isPinnedTarget(BrandInfo brand, InsightCard card) {
        return isTargetCategory(brand, card);
    }

    public List<String> buildConstraintHints(BrandInfo brand, InsightCard card) {
        List<String> hints = new ArrayList<>();
        if (Boolean.TRUE.equals(brand.getHasCategory())
                && brand.getTargetCategory() != null
                && CategoryNameMatcher.matches(card.getCategoryName(), brand.getTargetCategory())) {
            hints.add("与您的目标品类一致，已优先展示");
        }
        if (isBudgetCompatible(brand, card)) {
            hints.add("启动资金落在预算带「" + TextFormats.nullToDash(brand.getBudgetRange()) + "」内");
        } else {
            hints.add("启动资金可能超出预算带「" + TextFormats.nullToDash(brand.getBudgetRange()) + "」，建议谨慎评估");
        }
        if (brand.getProfitMin() != null && !brand.getProfitMin().isBlank()) {
            hints.add("利润目标参考：" + brand.getProfitMin());
        }
        if (brand.getSupplyChain() != null && !brand.getSupplyChain().isBlank()) {
            hints.add("可结合现有供应链：" + TextFormats.abbreviate(brand.getSupplyChain(), 36));
        }
        return hints;
    }

    public List<ConstraintMismatch> buildConstraintMismatches(BrandInfo brand, InsightCard card) {
        List<ConstraintMismatch> mismatches = new ArrayList<>();
        if (Boolean.TRUE.equals(brand.getHasCategory()) && !isTargetCategory(brand, card)) {
            mismatches.add(new ConstraintMismatch(
                    ConstraintType.TARGET_CATEGORY.getCode(),
                    "未命中当前目标品类，优先级低于定向赛道",
                    RiskLevel.LOW.getCode()
            ));
        }
        if (!isPlatformCompatible(brand, card)) {
            mismatches.add(new ConstraintMismatch(
                    ConstraintType.PLATFORM.getCode(),
                    "主平台与该类目的起量方式不完全匹配",
                    RiskLevel.MEDIUM.getCode()
            ));
        }
        if (!isBudgetCompatible(brand, card)) {
            mismatches.add(new ConstraintMismatch(
                    ConstraintType.BUDGET.getCode(),
                    "启动资金高于当前预算带",
                    RiskLevel.HIGH.getCode()
            ));
        }
        if (CompetitionLevel.fromLabel(card.getCompetitionLevel()).isHigh()) {
            mismatches.add(new ConstraintMismatch(
                    ConstraintType.COMPETITION.getCode(),
                    "竞争格局偏红海，需要更强差异化",
                    RiskLevel.MEDIUM.getCode()
            ));
        }
        StockCycle stockCycle = StockCycle.fromLabel(brand.getStockCycle());
        if (stockCycle != null && stockCycle.isLongCycle()) {
            mismatches.add(new ConstraintMismatch(
                    ConstraintType.STOCK_CYCLE.getCode(),
                    "备货周期偏长，不适合抢短周期红利",
                    RiskLevel.MEDIUM.getCode()
            ));
        }
        if (!isProfitCompatible(brand, card)) {
            mismatches.add(new ConstraintMismatch(
                    ConstraintType.PROFIT.getCode(),
                    "高利润目标下，当前类目利润弹性偏弱",
                    RiskLevel.MEDIUM.getCode()
            ));
        }
        if (brand.getSupplyChain() == null || brand.getSupplyChain().isBlank()) {
            mismatches.add(new ConstraintMismatch(
                    ConstraintType.SUPPLY_CHAIN.getCode(),
                    "尚未填写供应链资源，需要额外验证履约能力",
                    RiskLevel.LOW.getCode()
            ));
        }
        return mismatches;
    }

    private Comparator<InsightCard> buildCardComparator(BrandInfo brand) {
        return Comparator
                .comparing((InsightCard card) -> isPinnedTarget(brand, card) ? 0 : 1)
                .thenComparing(card -> isBudgetCompatible(brand, card) ? 0 : 1)
                .thenComparing(card -> parseGrowth(card.getMarketGrowth()), Comparator.reverseOrder())
                .thenComparing(InsightCard::getId);
    }

    private boolean isContentDrivenCategory(InsightCard card) {
        return CategoryKeyword.isContentDriven(card.getCategoryName());
    }

    private boolean isSearchDrivenCategory(InsightCard card) {
        return CategoryKeyword.isSearchDriven(card.getCategoryName());
    }

    private boolean isTargetCategory(BrandInfo brand, InsightCard card) {
        if (!Boolean.TRUE.equals(brand.getHasCategory()) || brand.getTargetCategory() == null) {
            return false;
        }
        return CategoryNameMatcher.matches(card.getCategoryName(), brand.getTargetCategory());
    }

    private boolean matchesExclude(String category, String exclude) {
        return CategoryNameMatcher.matches(category, exclude);
    }

    private Set<String> parseCsv(String raw) {
        if (raw == null || raw.isBlank()) {
            return Set.of();
        }
        return Arrays.stream(raw.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
    }

    private int startupMinWan(String startupCost) {
        if (startupCost == null) {
            return -1;
        }
        String digits = startupCost.replaceAll("[^0-9]", " ").trim();
        if (digits.isEmpty()) {
            return -1;
        }
        String[] parts = digits.split("\\s+");
        try {
            return Integer.parseInt(parts[0]);
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

    private double parseGrowth(String marketGrowth) {
        if (marketGrowth == null) {
            return 0;
        }
        String digits = marketGrowth.replaceAll("[^0-9.]", "");
        if (digits.isBlank()) {
            return 0;
        }
        try {
            return Double.parseDouble(digits);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

}
