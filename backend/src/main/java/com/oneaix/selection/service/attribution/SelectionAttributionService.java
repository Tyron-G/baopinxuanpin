package com.oneaix.selection.service.attribution;

import com.oneaix.selection.dto.AttributionInsight;
import com.oneaix.selection.dto.BrandSelectionContext;
import com.oneaix.selection.dto.InsightCardView;
import com.oneaix.selection.dto.SelectionAttributionReport;
import com.oneaix.selection.enums.DecisionType;
import com.oneaix.selection.service.BrandSelectionContextLoader;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/** 选品归因分析（迭代2）2026-06-04 */
@Service
public class SelectionAttributionService {

    private final BrandSelectionContextLoader contextLoader;

    public SelectionAttributionService(BrandSelectionContextLoader contextLoader) {
        this.contextLoader = contextLoader;
    }

    public SelectionAttributionReport report(Long brandId) {
        BrandSelectionContext context = contextLoader.load(brandId);
        List<AttributionInsight> success = new ArrayList<>();
        List<AttributionInsight> failure = new ArrayList<>();
        for (InsightCardView view : context.cards()) {
            if (DecisionType.RECOMMEND.getLabel().equals(view.decision())) {
                success.add(new AttributionInsight(
                        view.card().getCategoryName() + " 立项成功因子",
                        "搜索与内容双升 + 竞争集中度可控",
                        view.reasons().isEmpty() ? "规则评分达标" : view.reasons().get(0).description()
                ));
            } else if (DecisionType.ABANDON.getLabel().equals(view.decision())) {
                failure.add(new AttributionInsight(
                        view.card().getCategoryName() + " 放弃原因",
                        "头部锁定或利润弹性不足",
                        view.risks().isEmpty() ? "竞争壁垒过高" : view.risks().get(0).description()
                ));
            }
        }
        List<String> nextQuarter = context.cards().stream()
                .filter(view -> !DecisionType.ABANDON.getLabel().equals(view.decision()))
                .map(view -> view.card().getCategoryName())
                .limit(5)
                .toList();
        return new SelectionAttributionReport(
                context.brand().getBrandName(),
                "基于历史选品成败与当前约束，沉淀「为什么爆/为什么死」的可复用判断。",
                success,
                failure,
                nextQuarter
        );
    }
}
