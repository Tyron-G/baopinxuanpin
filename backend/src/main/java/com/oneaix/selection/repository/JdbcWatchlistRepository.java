package com.oneaix.selection.repository;

import com.oneaix.selection.dto.WatchlistItem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/** 关注列表持久化 2026-06-04 */
@Repository
public class JdbcWatchlistRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<WatchlistItem> ROW_MAPPER = (rs, rowNum) -> new WatchlistItem(
            rs.getLong("id"),
            rs.getLong("brand_id"),
            rs.getObject("card_id") != null ? rs.getLong("card_id") : null,
            rs.getString("category_name"),
            rs.getString("note"),
            rs.getString("created_at")
    );

    public JdbcWatchlistRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<WatchlistItem> listByBrand(Long brandId) {
        return jdbcTemplate.query(
                "SELECT id, brand_id, card_id, category_name, note, created_at FROM watchlist_item WHERE brand_id = ? ORDER BY id DESC",
                ROW_MAPPER,
                brandId
        );
    }

    public WatchlistItem add(Long brandId, Long cardId, String categoryName, String note) {
        jdbcTemplate.update(
                "INSERT INTO watchlist_item (brand_id, card_id, category_name, note) VALUES (?, ?, ?, ?)",
                brandId,
                cardId,
                categoryName,
                note
        );
        return jdbcTemplate.query(
                "SELECT id, brand_id, card_id, category_name, note, created_at FROM watchlist_item WHERE brand_id = ? AND category_name = ? ORDER BY id DESC LIMIT 1",
                ROW_MAPPER,
                brandId,
                categoryName
        ).get(0);
    }

    public void remove(Long brandId, Long id) {
        jdbcTemplate.update("DELETE FROM watchlist_item WHERE brand_id = ? AND id = ?", brandId, id);
    }

    public int countByBrand(Long brandId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM watchlist_item WHERE brand_id = ?",
                Integer.class,
                brandId
        );
        return count == null ? 0 : count;
    }
}
