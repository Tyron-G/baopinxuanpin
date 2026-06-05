package com.oneaix.selection.service.insight;

import com.oneaix.selection.content.CategoryPlaybook;
import com.oneaix.selection.content.CategoryPlaybookRegistry;
import com.oneaix.selection.dto.InsightCardView;
import com.oneaix.selection.dto.PainPointItem;
import com.oneaix.selection.dto.SentimentTerm;
import com.oneaix.selection.enums.SentimentPolarity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/** 痛点优先级清单：优先竞品差评聚合，Playbook 兜底 2026-06-05 */
@Component
public class PainPointListBuilder {

    private static final int MAX_ITEMS = 5;

    private final CategoryPlaybookRegistry playbookRegistry;
    private final CompetitorComplaintAggregator complaintAggregator;

    public PainPointListBuilder(
            CategoryPlaybookRegistry playbookRegistry,
            CompetitorComplaintAggregator complaintAggregator
    ) {
        this.playbookRegistry = playbookRegistry;
        this.complaintAggregator = complaintAggregator;
    }

    public List<PainPointItem> build(Long brandId, List<InsightCardView> rankedCards) {
        if (rankedCards == null || rankedCards.isEmpty()) {
            return List.of();
        }
        List<PainPointItem> fromComplaints = buildFromComplaints(brandId, rankedCards);
        if (!fromComplaints.isEmpty()) {
            return fromComplaints;
        }
        return buildFromPlaybook(rankedCards);
    }

    private List<PainPointItem> buildFromComplaints(Long brandId, List<InsightCardView> rankedCards) {
        List<CompetitorComplaintAggregator.ComplaintTopicStat> stats =
                complaintAggregator.aggregate(brandId, rankedCards);
        if (stats.isEmpty()) {
            return List.of();
        }
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
                            + stat.frequency() + " 个跟踪竞品中出现（"
                            + String.join("、", stat.shopNames()) + "）。"
            ));
        }
        return items;
    }

    private List<PainPointItem> buildFromPlaybook(List<InsightCardView> rankedCards) {
        List<PainPointItem> items = new ArrayList<>();
        LinkedHashSet<String> seenTopics = new LinkedHashSet<>();
        int rank = 1;
        for (InsightCardView view : rankedCards) {
            if (items.size() >= MAX_ITEMS) {
                break;
            }
            CategoryPlaybook playbook = playbookRegistry.resolve(view.card());
            for (SentimentTerm term : playbook.sentimentTerms()) {
                if (items.size() >= MAX_ITEMS) {
                    break;
                }
                if (!SentimentPolarity.NEGATIVE.getCode().equals(term.sentiment())) {
                    continue;
                }
                if (!seenTopics.add(term.name())) {
                    continue;
                }
                int weight = term.value() == null ? 50 : term.value();
                items.add(new PainPointItem(
                        rank++,
                        term.name(),
                        Math.max(5, weight / 4),
                        weight >= 65 ? "高" : "中",
                        view.card().getCategoryName() + " 用户差评簇：" + term.name()
                                + "（Playbook 兜底口径）。"
                ));
            }
        }
        return items;
    }

    public List<String> topics(List<PainPointItem> items) {
        return items.stream().map(PainPointItem::topic).toList();
    }
}
