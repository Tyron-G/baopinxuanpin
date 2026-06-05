package com.oneaix.selection.service.testrun;

import com.oneaix.selection.dto.BrandSelectionContext;
import com.oneaix.selection.dto.TestRunDiagnosis;
import com.oneaix.selection.dto.TestRunMetric;
import com.oneaix.selection.entity.InsightCard;
import com.oneaix.selection.enums.DecisionType;
import com.oneaix.selection.enums.PlatformView;
import com.oneaix.selection.exception.ResourceNotFoundException;
import com.oneaix.selection.service.BrandSelectionContextLoader;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/** 测款优化诊断（内置样例规则）2026-06-05 */
@Service
public class TestRunDiagnosisService {

    private final BrandSelectionContextLoader contextLoader;

    public TestRunDiagnosisService(BrandSelectionContextLoader contextLoader) {
        this.contextLoader = contextLoader;
    }

    public TestRunDiagnosis diagnose(Long brandId, Long cardId, String platformView) {
        BrandSelectionContext context = contextLoader.load(brandId);
        InsightCard card = context.findCard(cardId)
                .orElseThrow(() -> ResourceNotFoundException.insightCard(cardId))
                .card();
        PlatformView platform = PlatformView.normalize(platformView);
        DecisionType decision = DecisionType.fromRecommendation(card.getRecommendation());
        String productTitle = card.getCategoryName() + " · 测款 SKU-A";

        List<TestRunMetric> metrics = buildMetrics(decision);
        String verdict = verdictFor(decision, metrics);
        int confidence = confidenceFor(decision, metrics);

        return new TestRunDiagnosis(
                brandId,
                cardId,
                productTitle,
                card.getCategoryName(),
                platform.getLabel(),
                "上架第 7 天",
                verdict,
                confidence,
                buildSummary(verdict, card.getCategoryName()),
                metrics,
                scaleUpActions(verdict, card.getCategoryName()),
                stopSignals(verdict),
                true
        );
    }

    private List<TestRunMetric> buildMetrics(DecisionType decision) {
        List<TestRunMetric> metrics = new ArrayList<>();
        metrics.add(metric("ctr", "点击率 CTR", decision == DecisionType.RECOMMEND ? "4.8%" : "2.6%", "≥3.5%", decision == DecisionType.RECOMMEND ? "good" : "warn", "主图与前三秒卖点是否命中场景"));
        metrics.add(metric("cvr", "转化率 CVR", decision == DecisionType.RECOMMEND ? "3.2%" : "1.4%", "≥2.0%", decision == DecisionType.RECOMMEND ? "good" : "warn", "详情页是否解释清楚差异化"));
        metrics.add(metric("cpc", "广告 CPC", "2.1 元", "≤2.6 元", "good", "相对类目均值低 12%（蝉妈妈样例）"));
        metrics.add(metric("roi", "投放 ROI", decision == DecisionType.RECOMMEND ? "1.42" : "0.86", "≥1.20", decision == DecisionType.RECOMMEND ? "good" : "bad", "是否达到可放量门槛"));
        metrics.add(metric("refund", "7 日退款率", decision == DecisionType.ABANDON ? "8.6%" : "3.1%", "≤5.0%", decision == DecisionType.ABANDON ? "bad" : "good", "差评主题是否集中在单一功能点"));
        metrics.add(metric("cart", "加购率", decision == DecisionType.RECOMMEND ? "11.5%" : "6.2%", "≥8.0%", decision == DecisionType.RECOMMEND ? "good" : "warn", "价格带与场景表达是否一致"));
        return metrics;
    }

    private TestRunMetric metric(String key, String label, String actual, String benchmark, String status, String hint) {
        return new TestRunMetric(key, label, actual, benchmark, status, hint);
    }

    private String verdictFor(DecisionType decision, List<TestRunMetric> metrics) {
        long badCount = metrics.stream().filter(item -> "bad".equals(item.status())).count();
        if (badCount >= 2 || decision == DecisionType.ABANDON) {
            return "建议停投";
        }
        if (decision == DecisionType.RECOMMEND && badCount == 0) {
            return "建议加投";
        }
        return "继续观望";
    }

    private int confidenceFor(DecisionType decision, List<TestRunMetric> metrics) {
        int base = switch (decision) {
            case RECOMMEND -> 78;
            case WATCH -> 66;
            case ABANDON -> 58;
        };
        long goodCount = metrics.stream().filter(item -> "good".equals(item.status())).count();
        return Math.min(92, base + (int) goodCount * 2);
    }

    private String buildSummary(String verdict, String categoryName) {
        return switch (verdict) {
            case "建议加投" -> categoryName + " 测款第 7 天核心指标达标，建议小步加投并同步优化素材前三秒。";
            case "建议停投" -> categoryName + " 测款 ROI 与退款率未达门槛，建议暂停放量并回到卖点/履约验证。";
            default -> categoryName + " 测款信号尚未收敛，建议保持小预算验证 1 周后再决策。";
        };
    }

    private List<String> scaleUpActions(String verdict, String categoryName) {
        if ("建议停投".equals(verdict)) {
            return List.of(
                    "暂停大预算计划，保留最小样本继续观察退款原因",
                    "针对差评主题做功能/文案修正后再开第二轮测款",
                    "对比 " + categoryName + " 竞品主图与价格带，确认是否误打红海词"
            );
        }
        if ("建议加投".equals(verdict)) {
            return List.of(
                    "将预算提升 20%-30%，优先投放已验证的场景素材",
                    "补充「低噪音/卡粮提醒」等功能证明，提高详情页转化",
                    "同步监控 7 日退款率，超过 5% 立即回调预算"
            );
        }
        return List.of(
                "保持当前日预算，不扩大投放面",
                "A/B 测试 2 套主图与 1 套价格带",
                "第 14 天再评估是否进入加投"
        );
    }

    private List<String> stopSignals(String verdict) {
        if ("建议停投".equals(verdict)) {
            return List.of("ROI 连续 3 日低于 1.0", "退款率突破 6%", "CPC 突然上涨 30% 且转化未改善");
        }
        return List.of("ROI 跌破 1.0 且持续 2 日", "CTR 低于 2% 且无改善", "差评集中在未覆盖的功能承诺");
    }
}
