package com.oneaix.selection.service;

import com.oneaix.selection.dto.ReportAction;
import com.oneaix.selection.entity.BrandInfo;
import com.oneaix.selection.entity.InsightCard;
import com.oneaix.selection.enums.PlatformView;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/** 后续动作规划器 2026-06-04 */
@Component
public class NextActionPlanner {
    private static final DateTimeFormatter DUE_AT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final ActionStatusTracker actionStatusTracker;

    public NextActionPlanner(ActionStatusTracker actionStatusTracker) {
        this.actionStatusTracker = actionStatusTracker;
    }

    public List<ReportAction> build(Long cardId, InsightCard card, BrandInfo brand) {
        return build(cardId, card, brand, PlatformView.ALL.getLabel());
    }

    public List<ReportAction> build(Long cardId, InsightCard card, BrandInfo brand, String platformView) {
        PlatformView platform = PlatformView.normalize(platformView);
        String validationGoal = validationGoal(platform, card);
        String sampleNote = sampleNote(platform);
        String cpcGoal = cpcGoal(platform, card);
        String cpcNote = cpcNote(platform);
        String dueAt = LocalDateTime.now().plusDays(1).format(DUE_AT_FORMAT);

        return List.of(
                actionStatusTracker.merge(cardId, new ReportAction(
                        "确认 7 天小样验证方案",
                        "选品负责人",
                        validationGoal,
                        "P0",
                        "1-2 天",
                        "待执行",
                        dueAt,
                        sampleNote
                )),
                actionStatusTracker.merge(cardId, new ReportAction(
                        "与供应链确认 MOQ 和打样周期",
                        "供应链经理",
                        "核实启动资金与备货周期是否覆盖首批试单，并确认关键工艺风险",
                        "P0",
                        brand.getStockCycle() == null || brand.getStockCycle().isBlank() ? "3-5 天" : brand.getStockCycle(),
                        "待确认",
                        dueAt,
                        "重点确认 MOQ、模组交期和关键返修风险。"
                )),
                actionStatusTracker.merge(cardId, new ReportAction(
                        "补充头部竞品拆解与 CPC 试投",
                        "运营负责人",
                        cpcGoal,
                        "P1",
                        "3-5 天",
                        "待执行",
                        dueAt,
                        cpcNote
                ))
        );
    }

    private String validationGoal(PlatformView platform, InsightCard card) {
        return switch (platform) {
            case DOUYIN -> "验证「" + card.getCategoryName() + "」在抖音短视频场景中的点击、完播和加购反馈";
            case TMALL -> "验证「" + card.getCategoryName() + "」在天猫搜索承接和详情页转化中的表现";
            case XIAOHONGSHU -> "验证「" + card.getCategoryName() + "」在小红书种草内容中的收藏、互动和站外转化线索";
            default -> "验证「" + card.getCategoryName() + "」核心场景的点击、收藏和加购反馈";
        };
    }

    private String sampleNote(PlatformView platform) {
        return switch (platform) {
            case DOUYIN -> "优先准备前 3 秒钩子素材、场景化脚本和低门槛体验卖点。";
            case TMALL -> "优先完成搜索词、主图卖点和详情页结构化承接设计。";
            case XIAOHONGSHU -> "优先准备种草笔记素材、生活方式表达和高频痛点对照内容。";
            default -> "优先完成样品定义、验证指标和投放素材准备。";
        };
    }

    private String cpcGoal(PlatformView platform, InsightCard card) {
        return switch (platform) {
            case DOUYIN -> "验证当前利润空间是否覆盖抖音内容投流成本，并筛出可放大的前 2 个短视频卖点";
            case TMALL -> "验证当前利润空间是否覆盖天猫搜索投放成本，并筛出高转化搜索词与卖点组合";
            case XIAOHONGSHU -> "验证当前利润空间是否覆盖小红书种草投放和达人分发成本，并筛出高互动表达";
            default -> "验证当前利润空间是否覆盖广告成本，并筛出首批差异化卖点";
        };
    }

    private String cpcNote(PlatformView platform) {
        return switch (platform) {
            case DOUYIN -> "先跑小预算内容试投，再回看完播率、点击率和评论反馈。";
            case TMALL -> "先跑小预算搜索试投，再回看点击率、收藏率和详情页转化。";
            case XIAOHONGSHU -> "先跑小预算种草试投，再回看收藏率、互动率和评论关键词。";
            default -> "先跑小预算试投，再回看点击率、收藏率和评论反馈。";
        };
    }
}
