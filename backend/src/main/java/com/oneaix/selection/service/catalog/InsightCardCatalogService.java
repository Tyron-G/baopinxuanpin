package com.oneaix.selection.service.catalog;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.oneaix.selection.constant.ApiConstants;
import com.oneaix.selection.dto.BrandSelectionContext;
import com.oneaix.selection.entity.InsightCard;
import com.oneaix.selection.exception.ResourceNotFoundException;
import com.oneaix.selection.mapper.InsightCardMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/** 洞察卡片目录读取（单点查询）2026-06-04 */
@Service
public class InsightCardCatalogService {

    private final InsightCardMapper insightCardMapper;

    public InsightCardCatalogService(InsightCardMapper insightCardMapper) {
        this.insightCardMapper = insightCardMapper;
    }

    public List<InsightCard> loadCatalog() {
        return insightCardMapper.selectList(new LambdaQueryWrapper<InsightCard>()
                .eq(InsightCard::getBrandId, ApiConstants.CATALOG_BRAND_ID)
                .orderByAsc(InsightCard::getId));
    }

    public Optional<InsightCard> findById(Long cardId) {
        if (cardId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(insightCardMapper.selectById(cardId));
    }

    public InsightCard requireById(Long cardId) {
        return findById(cardId).orElseThrow(() -> ResourceNotFoundException.insightCard(cardId));
    }

    /** 卡片存在且未被当前品牌约束排除（与约束引擎可见集一致）2026-06-04 */
    public InsightCard requireVisible(Long cardId, BrandSelectionContext context) {
        InsightCard card = requireById(cardId);
        if (!context.visibleCategoryNames().contains(card.getCategoryName())) {
            throw ResourceNotFoundException.insightCardExcluded(cardId, card.getCategoryName());
        }
        return card;
    }
}
