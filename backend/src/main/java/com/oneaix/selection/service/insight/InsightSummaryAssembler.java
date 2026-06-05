package com.oneaix.selection.service.insight;

import com.oneaix.selection.dto.CategoryBrief;
import com.oneaix.selection.dto.InsightCardView;
import com.oneaix.selection.dto.InsightSummary;
import com.oneaix.selection.dto.InsightSummaryBuildRequest;
import com.oneaix.selection.dto.MarketScaleBrief;
import com.oneaix.selection.dto.PainPointItem;
import com.oneaix.selection.dto.PotentialCategoryItem;
import com.oneaix.selection.entity.BrandInfo;
import com.oneaix.selection.entity.CompetitionData;
import com.oneaix.selection.enums.ConstraintType;
import com.oneaix.selection.enums.DecisionType;
import com.oneaix.selection.service.catalog.InsightCardQueryService;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/** 洞察摘要页聚合 2026-06-04 */
@Component
public class InsightSummaryAssembler {

    private final InsightCardQueryService cardQueryService;
    private final CategoryBriefBuilder categoryBriefBuilder;
    private final PotentialCategoryListBuilder potentialCategoryListBuilder;
    private final PainPointListBuilder painPointListBuilder;

    public InsightSummaryAssembler(
            InsightCardQueryService cardQueryService,
            CategoryBriefBuilder categoryBriefBuilder,
            PotentialCategoryListBuilder potentialCategoryListBuilder,
            PainPointListBuilder painPointListBuilder
    ) {
        this.cardQueryService = cardQueryService;
        this.categoryBriefBuilder = categoryBriefBuilder;
        this.potentialCategoryListBuilder = potentialCategoryListBuilder;
        this.painPointListBuilder = painPointListBuilder;
    }

    public InsightSummary build(InsightSummaryBuildRequest request) {
        BrandInfo brand = request.brand();
        List<InsightCardView> rankedCards = request.rankedCards();
        List<String> filteredCategories = cardQueryService.filteredCategoryNames(
                request.catalog(),
                request.visibleCategoryNames()
        );

        String platform = request.platform() == null || request.platform().isBlank()
                ? "全平台"
                : request.platform();
        List<CategoryBrief> trendTop3 = categoryBriefBuilder.trendTop3(request.trends(), platform);
        List<CategoryBrief> competitionTop3 = categoryBriefBuilder.competitionTop3(request.competition(), platform);
        List<CategoryBrief> supplyTop3 = categoryBriefBuilder.supplyTop3(request.supplyDemand(), platform);

        String trendConclusion = trendTop3.isEmpty()
                ? "当前约束下暂无可用趋势信号，请调整排除品类或预算后重试。"
                : "12 月同比与社媒热度同步上升，" + trendTop3.get(0).categoryName() + " 处于需求加速阶段。";

        String competitionConclusion = competitionTop3.isEmpty()
                ? "暂无竞争格局数据。"
                : request.competition().stream()
                .filter(item -> item.getCategoryName().equals(competitionTop3.get(0).categoryName()))
                .map(CompetitionData::getConclusion)
                .findFirst()
                .orElse("竞争格局分化明显，需结合头部集中度判断进入难度。");

        String supplyConclusion = supplyTop3.isEmpty()
                ? "暂无价格带缺口数据。"
                : supplyTop3.get(0).categoryName() + " 在 " + supplyTop3.get(0).metric()
                + " 存在明显供需错配，属于可切入的价格真空带。";

        String trendJudgment = rankedCards.isEmpty()
                ? "暂无趋势判断"
                : rankedCards.get(0).scoreBreakdown().trendScore() >= 28
                ? "结构性长期需求：搜索与内容声量双升，非短期炒作。"
                : "需区分季节波动与长期需求，建议先做小批量验证。";

        String crowdProfile = Boolean.TRUE.equals(brand.getHasCategory())
                ? "优先围绕「" + brand.getTargetCategory() + "」对应人群做场景验证。"
                : "围绕「" + brand.getInterestDirection() + "」方向，从年轻养宠与轻户外人群切入。";

        List<PotentialCategoryItem> potentialCategories = potentialCategoryListBuilder.build(
                rankedCards,
                request.trends()
        );
        String primaryCategory = trendTop3.isEmpty()
                ? (rankedCards.isEmpty() ? "宠物智能用品" : rankedCards.get(0).card().getCategoryName())
                : trendTop3.get(0).categoryName();
        MarketScaleBrief marketScaleBrief = potentialCategoryListBuilder.marketScaleFor(primaryCategory);
        List<PainPointItem> painPointItems = painPointListBuilder.build(brand.getId(), rankedCards);

        return new InsightSummary(
                brand,
                trendConclusion,
                trendTop3,
                competitionConclusion,
                competitionTop3,
                supplyConclusion,
                supplyTop3,
                trendJudgment,
                painPointItems,
                painPointListBuilder.topics(painPointItems),
                crowdProfile,
                rankedCards.stream().skip(1).limit(2).toList(),
                buildBlockingReasons(brand, rankedCards, filteredCategories),
                buildRecommendedAdjustments(brand, rankedCards, filteredCategories),
                filteredCategories,
                potentialCategories,
                marketScaleBrief
        );
    }

    private List<String> buildBlockingReasons(
            BrandInfo brand,
            List<InsightCardView> rankedCards,
            List<String> filteredCategories
    ) {
        Set<String> reasons = new LinkedHashSet<>();
        if (!filteredCategories.isEmpty()) {
            reasons.add("排除品类已过滤赛道：" + String.join("、", filteredCategories) + "。");
        }
        if (Boolean.TRUE.equals(brand.getHasCategory()) && rankedCards.stream().noneMatch(InsightCardView::pinned)) {
            reasons.add("当前候选池没有命中目标品类，系统只能返回相邻赛道。");
        }
        if (!rankedCards.isEmpty() && rankedCards.stream().noneMatch(InsightCardView::budgetCompatible)) {
            reasons.add("当前预算带无法覆盖现有候选赛道的启动资金。");
        }
        if (!rankedCards.isEmpty() && rankedCards.stream().noneMatch(view ->
                DecisionType.RECOMMEND.getLabel().equals(view.decision()))) {
            reasons.add("当前可见赛道都未达到「" + DecisionType.RECOMMEND.getLabel() + "」门槛，优先级更偏验证而不是直接投入。");
        }
        if (!rankedCards.isEmpty() && rankedCards.stream().allMatch(view ->
                view.mismatches().stream().anyMatch(item -> ConstraintType.PROFIT.getCode().equals(item.type())))) {
            reasons.add("利润下限偏高，当前候选赛道的利润弹性不足。");
        }
        if (!rankedCards.isEmpty() && rankedCards.stream().allMatch(view ->
                view.mismatches().stream().anyMatch(item -> ConstraintType.SUPPLY_CHAIN.getCode().equals(item.type())))) {
            reasons.add("尚未提供供应链资源，系统默认提高了履约相关风险权重。");
        }
        if (rankedCards.isEmpty() && reasons.isEmpty()) {
            reasons.add("当前约束下暂无可展示的候选赛道。");
        }
        return List.copyOf(reasons);
    }

    private List<String> buildRecommendedAdjustments(
            BrandInfo brand,
            List<InsightCardView> rankedCards,
            List<String> filteredCategories
    ) {
        Set<String> actions = new LinkedHashSet<>();
        if (!filteredCategories.isEmpty()) {
            actions.add("减少排除品类范围，至少保留 1 个待观察赛道。");
        }
        if (!rankedCards.isEmpty() && rankedCards.stream().noneMatch(InsightCardView::budgetCompatible)) {
            actions.add("将预算调整到 20-50 万以上，或优先选择启动资金 35 万内的类目。");
        }
        if (Boolean.TRUE.equals(brand.getHasCategory()) && rankedCards.stream().noneMatch(InsightCardView::pinned)) {
            actions.add("先取消固定目标品类，改用兴趣方向查看更宽的候选池。");
        }
        if (brand.getSupplyChain() == null || brand.getSupplyChain().isBlank()) {
            actions.add("补充现有供应链资源，系统会提高可履约类目的优先级。");
        }
        if (brand.getStockCycle() != null && brand.getStockCycle().contains("60")) {
            actions.add("如果希望抢短周期红利，将备货周期调整到 30-60 天以内。");
        }
        if (actions.isEmpty()) {
            actions.add("保持当前约束，优先进入最佳机会页继续做小样验证。");
        }
        return List.copyOf(actions);
    }
}
