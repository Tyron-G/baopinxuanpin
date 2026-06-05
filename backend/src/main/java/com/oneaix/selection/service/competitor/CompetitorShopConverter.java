package com.oneaix.selection.service.competitor;

import com.oneaix.selection.dto.CompetitorShop;
import com.oneaix.selection.entity.CompetitorShopEntity;
import com.oneaix.selection.util.ListCsvCodec;
import org.springframework.stereotype.Component;

/** 竞品 DTO 与实体转换 2026-06-04 */
@Component
public class CompetitorShopConverter {

    public CompetitorShop toDto(CompetitorShopEntity entity) {
        return new CompetitorShop(
                entity.getShopName(),
                entity.getPlatform(),
                entity.getFocusCategory(),
                entity.getLatestHit(),
                entity.getGrowthSignal(),
                entity.getAddedAt(),
                entity.getCardId(),
                entity.getSourceSignalId(),
                entity.getSourceSignalType(),
                entity.getRecentLaunch(),
                entity.getHitProductCount() == null ? 0 : entity.getHitProductCount(),
                ListCsvCodec.decode(entity.getComplaintTopics()),
                ListCsvCodec.decode(entity.getOpportunityTags())
        );
    }

    public CompetitorShopEntity toEntity(Long brandId, CompetitorShop shop, boolean builtinSeed) {
        CompetitorShopEntity entity = new CompetitorShopEntity();
        entity.setBrandId(brandId);
        entity.setShopName(shop.shopName());
        entity.setPlatform(shop.platform());
        entity.setFocusCategory(shop.focusCategory());
        entity.setLatestHit(shop.latestHit());
        entity.setGrowthSignal(shop.growthSignal());
        entity.setAddedAt(shop.addedAt());
        entity.setCardId(shop.cardId());
        entity.setSourceSignalId(shop.sourceSignalId());
        entity.setSourceSignalType(shop.sourceSignalType());
        entity.setRecentLaunch(shop.recentLaunch());
        entity.setHitProductCount(shop.hitProductCount());
        entity.setComplaintTopics(ListCsvCodec.encode(shop.complaintTopics()));
        entity.setOpportunityTags(ListCsvCodec.encode(shop.opportunityTags()));
        entity.setBuiltinSeed(builtinSeed);
        return entity;
    }
}
