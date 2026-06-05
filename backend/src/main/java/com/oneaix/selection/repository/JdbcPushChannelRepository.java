package com.oneaix.selection.repository;

import com.oneaix.selection.dto.PushChannelConfig;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/** 信号推送渠道配置持久化 2026-06-04 */
@Repository
public class JdbcPushChannelRepository {

    private final JdbcTemplate jdbcTemplate;

    private static final RowMapper<PushChannelConfig> ROW_MAPPER = (rs, rowNum) -> new PushChannelConfig(
            rs.getLong("id"),
            rs.getLong("brand_id"),
            rs.getString("channel_type"),
            rs.getString("webhook_url"),
            rs.getBoolean("enabled"),
            rs.getTimestamp("updated_at") != null
                    ? rs.getTimestamp("updated_at").toLocalDateTime().toString().replace('T', ' ')
                    : ""
    );

    public JdbcPushChannelRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<PushChannelConfig> listByBrand(Long brandId) {
        return jdbcTemplate.query(
                "SELECT id, brand_id, channel_type, webhook_url, enabled, updated_at FROM push_channel_config WHERE brand_id = ? ORDER BY id",
                ROW_MAPPER,
                brandId
        );
    }

    public List<PushChannelConfig> listEnabled(Long brandId) {
        return jdbcTemplate.query(
                "SELECT id, brand_id, channel_type, webhook_url, enabled, updated_at FROM push_channel_config WHERE brand_id = ? AND enabled = TRUE ORDER BY id",
                ROW_MAPPER,
                brandId
        );
    }

    public PushChannelConfig upsert(Long brandId, String channelType, String webhookUrl, boolean enabled) {
        jdbcTemplate.update(
                """
                        MERGE INTO push_channel_config (brand_id, channel_type, webhook_url, enabled)
                        KEY (brand_id, channel_type)
                        VALUES (?, ?, ?, ?)
                        """,
                brandId,
                channelType,
                webhookUrl,
                enabled
        );
        return jdbcTemplate.query(
                "SELECT id, brand_id, channel_type, webhook_url, enabled, updated_at FROM push_channel_config WHERE brand_id = ? AND channel_type = ?",
                ROW_MAPPER,
                brandId,
                channelType
        ).get(0);
    }
}
