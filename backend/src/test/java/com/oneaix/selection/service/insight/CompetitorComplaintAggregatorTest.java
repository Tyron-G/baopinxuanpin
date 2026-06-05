package com.oneaix.selection.service.insight;

import com.oneaix.selection.dto.CompetitorShop;
import com.oneaix.selection.dto.InsightCardView;
import com.oneaix.selection.dto.ScoreBreakdown;
import com.oneaix.selection.entity.InsightCard;
import com.oneaix.selection.enums.PlatformView;
import com.oneaix.selection.service.CompetitorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

/** 2026-06-05 CompetitorComplaintAggregator */
@ExtendWith(MockitoExtension.class)
class CompetitorComplaintAggregatorTest {

    @Mock
    private CompetitorService competitorService;

    @Test
    void shouldAggregateComplaintFrequencyAcrossShops() {
        when(competitorService.list(1L)).thenReturn(List.of(
                shop("店A", "宠物智能用品", List.of("卡粮", "噪音")),
                shop("店B", "宠物智能用品", List.of("卡粮", "APP 连接不稳"))
        ));
        var aggregator = new CompetitorComplaintAggregator(competitorService);
        var stats = aggregator.aggregate(1L, List.of(view("宠物智能用品")));
        assertTrue(stats.stream().anyMatch(item -> "卡粮".equals(item.topic()) && item.frequency() == 2));
    }

    private CompetitorShop shop(String name, String category, List<String> topics) {
        return new CompetitorShop(
                name, PlatformView.TMALL.getLabel(), category, "hit", "signal",
                "2026-06-05", 1L, "id", "type", "launch", 2, topics, List.of()
        );
    }

    private InsightCardView view(String category) {
        InsightCard card = new InsightCard();
        card.setCategoryName(category);
        return new InsightCardView(
                card, false, true, List.of(), "推荐立项",
                new ScoreBreakdown(30, 20, 15, 10, 0, 75, 80),
                List.of(), List.of(), null, List.of(), null
        );
    }
}
