package com.oneaix.selection.service.insight;

import com.oneaix.selection.content.CategoryPlaybookRegistry;
import com.oneaix.selection.dto.InsightCardView;
import com.oneaix.selection.dto.PainPointItem;
import com.oneaix.selection.dto.ScoreBreakdown;
import com.oneaix.selection.entity.InsightCard;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** 2026-06-05 PainPointListBuilder */
class PainPointListBuilderTest {

    private final PainPointListBuilder builder = new PainPointListBuilder(new CategoryPlaybookRegistry());

    @Test
    void shouldBuildNegativePainPointsFromRankedPlaybooks() {
        InsightCard pet = card("宠物智能用品");
        InsightCard coffee = card("便携式咖啡器具");
        List<PainPointItem> items = builder.build(List.of(view(pet), view(coffee)));

        assertFalse(items.isEmpty());
        assertTrue(items.size() <= 5);
        assertEquals(1, items.get(0).rank());
        assertTrue(items.stream().anyMatch(item -> item.topic().contains("卡粮")));
        assertTrue(items.stream().noneMatch(item -> item.topic().contains("远程看护")));
    }

    @Test
    void shouldReturnEmptyWhenNoCards() {
        assertTrue(builder.build(List.of()).isEmpty());
    }

    private InsightCard card(String categoryName) {
        InsightCard card = new InsightCard();
        card.setCategoryName(categoryName);
        return card;
    }

    private InsightCardView view(InsightCard card) {
        return new InsightCardView(
                card,
                false,
                true,
                List.of(),
                "推荐立项",
                new ScoreBreakdown(30, 20, 15, 10, 0, 75, 80),
                List.of(),
                List.of(),
                null,
                List.of()
        );
    }
}
