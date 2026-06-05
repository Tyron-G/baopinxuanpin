package com.oneaix.selection.content;

import com.oneaix.selection.dto.KpiMetricItem;
import com.oneaix.selection.dto.ProductMetricsKpi;
import org.springframework.stereotype.Component;

import java.util.List;

/** 背景文档 MVP/迭代1/2 运营 KPI 样例 2026-06-04 */
@Component
public class SampleProductMetricsCatalog {

    public ProductMetricsKpi snapshot() {
        return new ProductMetricsKpi(
                "迭代1 · 样例看板",
                "2026-06-04",
                true,
                "以下为产品背景文档成功标准的演示数据，用于汇报与走查，不代表真实经营结果。",
                List.of(
                        new KpiMetricItem("seed_users_4w", "种子用户连续使用≥4周", "8 / 10 家", "5-10 家", "up", "met"),
                        new KpiMetricItem("hit_growth_2w", "推荐品 2 周销量增速>50%", "34%", "≥30%", "up", "met"),
                        new KpiMetricItem("nps_mvp", "种子用户 NPS（MVP）", "42", ">30", "up", "met"),
                        new KpiMetricItem("mau_brands", "月活跃品牌数", "56", ">50", "up", "met"),
                        new KpiMetricItem("retention", "客户留存率", "74%", ">70%", "up", "met"),
                        new KpiMetricItem("nps_iter1", "NPS（迭代1）", "45", ">40", "up", "met"),
                        new KpiMetricItem("accuracy_80", "机会分>80 且 2 周增速>30%", "52%", ">50%", "up", "met"),
                        new KpiMetricItem("enterprise", "企业版签约", "12 家", ">10 家", "up", "met"),
                        new KpiMetricItem("renewal", "客户续费率", "82%", ">80%", "up", "met"),
                        new KpiMetricItem("api_brands", "API 调用品牌", "24", ">20", "up", "met"),
                        new KpiMetricItem("model_lift", "专属模型准确率提升", "+22%", "+20%", "up", "met"),
                        new KpiMetricItem("arr", "ARR（年化经常性收入）", "¥128 万", "—", "up", "on-track"),
                        new KpiMetricItem("mrr", "MRR", "¥10.6 万", "—", "up", "on-track")
                )
        );
    }
}
