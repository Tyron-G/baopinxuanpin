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

/** 2026-06-05 PainPointListBuilder */
@ExtendWith(MockitoExtension.class)
class PainPointListBuilderTest {

    @Mock
    private CompetitorService competitorService;

    @Test
    void shouldBuildFromComplaintTopics() {
        when(competitorService.list(1L)).thenReturn(List.of(
                new CompetitorShop(
                        "小佩宠物旗舰店",
                        PlatformView.TMALL.getLabel(),
                        "宠物智能用品",
                        "hit", "signal", "2026-06-05", 1L, "id", "type", "launch", 3,
                        List.of("卡粮", "噪音"), List.of()
                )
        ));
        PainPointListBuilder builder = new PainPointListBuilder(
                new CompetitorComplaintAggregator(competitorService),
                new ComplaintSentimentClassifier()
        );
        var items = builder.build(1L, List.of(view("宠物智能用品")));
        assertEquals("卡粮", items.get(0).topic());
        assertTrue(items.get(0).summary().contains("竞品样本"));
    }

    @Test
    void shouldReturnEmptyWhenNoCards() {
        PainPointListBuilder builder = new PainPointListBuilder(
                new CompetitorComplaintAggregator(competitorService),
                new ComplaintSentimentClassifier()
        );
        assertTrue(builder.build(1L, List.of()).isEmpty());
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
