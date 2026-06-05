package com.oneaix.selection.service.supply;

import com.oneaix.selection.content.CategoryPlaybookRegistry;
import com.oneaix.selection.dto.Alibaba1688Intel;
import com.oneaix.selection.dto.AlibabaOfferItem;
import com.oneaix.selection.dto.BrandSelectionContext;
import com.oneaix.selection.dto.SupplyMatchItem;
import com.oneaix.selection.entity.InsightCard;
import com.oneaix.selection.service.BrandSelectionContextLoader;
import com.oneaix.selection.service.opportunity.OpportunityIntelBuilder;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

/** 供应链匹配：基于 1688 情报与机会卡推荐工厂 2026-06-05 */
@Service
public class SupplyMatchService {

    private final BrandSelectionContextLoader contextLoader;
    private final OpportunityIntelBuilder opportunityIntelBuilder;
    private final CategoryPlaybookRegistry playbookRegistry;

    public SupplyMatchService(
            BrandSelectionContextLoader contextLoader,
            OpportunityIntelBuilder opportunityIntelBuilder,
            CategoryPlaybookRegistry playbookRegistry
    ) {
        this.contextLoader = contextLoader;
        this.opportunityIntelBuilder = opportunityIntelBuilder;
        this.playbookRegistry = playbookRegistry;
    }

    public List<SupplyMatchItem> matches(Long brandId, Long cardId, String platform) {
        BrandSelectionContext context = contextLoader.load(brandId);
        InsightCard card = context.findCard(cardId)
                .orElseThrow(() -> new IllegalArgumentException("卡片不存在: " + cardId))
                .card();
        var playbook = playbookRegistry.resolve(card);
        Alibaba1688Intel intel = opportunityIntelBuilder.build1688(card, playbook);
        return intel.offers().stream()
                .map(offer -> toMatch(card, offer))
                .sorted(Comparator.comparingInt(SupplyMatchItem::matchScore).reversed())
                .toList();
    }

    private SupplyMatchItem toMatch(InsightCard card, AlibabaOfferItem offer) {
        int score = 70;
        if ("AAA".equalsIgnoreCase(offer.creditLevel()) || "AA".equalsIgnoreCase(offer.creditLevel())) {
            score += 10;
        }
        if (offer.moq() != null && offer.moq().contains("500")) {
            score += 5;
        }
        String reason = card.getCategoryName() + " 机会卡匹配：" + offer.title()
                + "，MOQ " + offer.moq() + "，适合小样验证后扩产";
        return new SupplyMatchItem(
                offer.factoryName(),
                offer.location(),
                offer.title(),
                offer.unitPrice(),
                offer.moq(),
                offer.creditLevel(),
                Math.min(99, score),
                reason
        );
    }
}
