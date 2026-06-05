package com.oneaix.selection.exception;

import org.springframework.http.HttpStatus;

/** 资源不存在 2026-06-04 */
public class ResourceNotFoundException extends ApiException {
    public ResourceNotFoundException(String errorCode, String message) {
        super(HttpStatus.NOT_FOUND.value(), errorCode, message);
    }

    public static ResourceNotFoundException brand(Long brandId) {
        return new ResourceNotFoundException("brand_not_found", "品牌不存在：" + brandId);
    }

    public static ResourceNotFoundException insightCard(Long cardId) {
        return new ResourceNotFoundException("insight_card_not_found", "洞察卡片不存在：" + cardId);
    }

    public static ResourceNotFoundException insightCardExcluded(Long cardId, String categoryName) {
        return new ResourceNotFoundException(
                "insight_card_excluded",
                "洞察卡片不可访问（品类「" + categoryName + "」已被品牌约束排除）：" + cardId
        );
    }
}
