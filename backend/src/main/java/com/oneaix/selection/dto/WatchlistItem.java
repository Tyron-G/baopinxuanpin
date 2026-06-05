package com.oneaix.selection.dto;

/** 基础看板关注项 2026-06-04 */
public record WatchlistItem(
        Long id,
        Long brandId,
        Long cardId,
        String categoryName,
        String note,
        String createdAt
) {
}
