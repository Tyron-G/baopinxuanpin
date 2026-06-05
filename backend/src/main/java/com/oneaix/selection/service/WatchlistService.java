package com.oneaix.selection.service;

import com.oneaix.selection.dto.WatchlistItem;
import com.oneaix.selection.repository.JdbcWatchlistRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/** 基础看板关注列表 2026-06-04 */
@Service
public class WatchlistService {

    private final JdbcWatchlistRepository watchlistRepository;

    public WatchlistService(JdbcWatchlistRepository watchlistRepository) {
        this.watchlistRepository = watchlistRepository;
    }

    public List<WatchlistItem> list(Long brandId) {
        return watchlistRepository.listByBrand(brandId);
    }

    public WatchlistItem add(Long brandId, Long cardId, String categoryName, String note) {
        return watchlistRepository.add(brandId, cardId, categoryName, note);
    }

    public void remove(Long brandId, Long id) {
        watchlistRepository.remove(brandId, id);
    }

    public int count(Long brandId) {
        return watchlistRepository.countByBrand(brandId);
    }
}
