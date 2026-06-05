package com.oneaix.selection.service.attribution;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.oneaix.selection.dto.AttributionInsight;
import com.oneaix.selection.dto.BrandSelectionContext;
import com.oneaix.selection.dto.InsightCardView;
import com.oneaix.selection.dto.SelectionAttributionReport;
import com.oneaix.selection.entity.ActionStatus;
import com.oneaix.selection.enums.DecisionType;
import com.oneaix.selection.mapper.ActionStatusMapper;
import com.oneaix.selection.service.BrandSelectionContextLoader;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/** 选品归因：卡片决策 + 动作完成状态 2026-06-05 */
@Service
public class SelectionAttributionService {

    private final BrandSelectionContextLoader contextLoader;
    private final ActionStatusMapper actionStatusMapper;

    public SelectionAttributionService(
            BrandSelectionContextLoader contextLoader,
            ActionStatusMapper actionStatusMapper
    ) {
        this.contextLoader = contextLoader;
        this.actionStatusMapper = actionStatusMapper;
    }

    public SelectionAttributionReport report(Long brandId) {
        BrandSelectionContext context = contextLoader.load(brandId);
        List<AttributionInsight> success = new ArrayList<>();
        List<AttributionInsight> failure = new ArrayList<>();
        Set<String> actionTitles = new LinkedHashSet<>();

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
            appendActionAttribution(view, success, failure, actionTitles);
        }

        List<String> nextQuarter = context.cards().stream()
                .filter(view -> !DecisionType.ABANDON.getLabel().equals(view.decision()))
                .map(view -> view.card().getCategoryName())
                .limit(5)
                .toList();
        return new SelectionAttributionReport(
                context.brand().getBrandName(),
                "基于当前品牌工作区卡片决策与机会页动作状态（action_status）沉淀归因，"
                        + "共分析 " + context.cards().size() + " 张卡片、"
                        + actionTitles.size() + " 条动作记录。",
                success,
                failure,
                nextQuarter
        );
    }

    private void appendActionAttribution(
            InsightCardView view,
            List<AttributionInsight> success,
            List<AttributionInsight> failure,
            Set<String> actionTitles
    ) {
        List<ActionStatus> rows = actionStatusMapper.selectList(new LambdaQueryWrapper<ActionStatus>()
                .eq(ActionStatus::getInsightCardId, view.card().getId()));
        for (ActionStatus row : rows) {
            if (row.getActionTitle() == null || row.getActionTitle().isBlank()) {
                continue;
            }
            if (!actionTitles.add(view.card().getCategoryName() + "::" + row.getActionTitle())) {
                continue;
            }
            String status = row.getStatus() == null ? "" : row.getStatus();
            if (status.contains("完成")) {
                success.add(new AttributionInsight(
                        view.card().getCategoryName() + " · " + row.getActionTitle(),
                        "验证动作已完成",
                        row.getNote() == null || row.getNote().isBlank() ? "动作闭环成功" : row.getNote()
                ));
            } else if (status.contains("放弃") || status.contains("驳回")) {
                failure.add(new AttributionInsight(
                        view.card().getCategoryName() + " · " + row.getActionTitle(),
                        "验证未通过或主动放弃",
                        row.getNote() == null || row.getNote().isBlank() ? "需复盘投放或供应链约束" : row.getNote()
                ));
            }
        }
    }
}
