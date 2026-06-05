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

/** 痛点优先级清单：按候选赛道 Playbook 负面舆情动态生成 2026-06-05 */
@Component
public class PainPointListBuilder {

    private static final int MAX_ITEMS = 5;

    private final CategoryPlaybookRegistry playbookRegistry;

    public PainPointListBuilder(CategoryPlaybookRegistry playbookRegistry) {
        this.playbookRegistry = playbookRegistry;
    }

    public List<PainPointItem> build(List<InsightCardView> rankedCards) {
        if (rankedCards == null || rankedCards.isEmpty()) {
            return List.of();
        }
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
                                + "，适合作为差异化切口（内置样例口径）。"
                ));
            }
        }
        return items;
    }

    public List<String> topics(List<PainPointItem> items) {
        return items.stream().map(PainPointItem::topic).toList();
    }
}
