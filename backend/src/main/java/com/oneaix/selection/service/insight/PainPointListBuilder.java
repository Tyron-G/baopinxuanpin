package com.oneaix.selection.service.insight;

import com.oneaix.selection.dto.InsightCardView;
import com.oneaix.selection.dto.PainPointItem;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/** 痛点优先级清单：仅竞品差评聚合 2026-06-05 */
@Component
public class PainPointListBuilder {

    private static final int MAX_ITEMS = 5;

    private final CompetitorComplaintAggregator complaintAggregator;

    public PainPointListBuilder(CompetitorComplaintAggregator complaintAggregator) {
        this.complaintAggregator = complaintAggregator;
    }

    public List<PainPointItem> build(Long brandId, List<InsightCardView> rankedCards) {
        if (rankedCards == null || rankedCards.isEmpty()) {
            return List.of();
        }
        List<CompetitorComplaintAggregator.ComplaintTopicStat> stats =
                complaintAggregator.aggregate(brandId, rankedCards);
        List<PainPointItem> items = new ArrayList<>();
        int rank = 1;
        for (CompetitorComplaintAggregator.ComplaintTopicStat stat : stats) {
            if (items.size() >= MAX_ITEMS) {
                break;
            }
            items.add(new PainPointItem(
                    rank++,
                    stat.topic(),
                    stat.frequency(),
                    stat.frequency() >= 2 ? "高" : "中",
                    stat.categoryName() + " 差评主题「" + stat.topic() + "」在 "
                            + stat.frequency() + " 个竞品样本中出现（"
                            + String.join("、", stat.shopNames()) + "）。"
            ));
        }
        return items;
    }

    public List<String> topics(List<PainPointItem> items) {
        if (items == null || items.isEmpty()) {
            return List.of();
        }
        return items.stream().map(PainPointItem::topic).toList();
    }
}
