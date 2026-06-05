package com.oneaix.selection.repository;

import com.oneaix.selection.dto.PushDeliveryRecord;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/** Webhook 投递记录 2026-06-04 */
@Repository
public class JdbcPushDeliveryRepository {

    private static final RowMapper<PushDeliveryRecord> ROW_MAPPER = (rs, rowNum) -> new PushDeliveryRecord(
            rs.getLong("id"),
            rs.getLong("brand_id"),
            rs.getString("channel_type"),
            rs.getString("status"),
            rs.getString("webhook_masked"),
            rs.getString("payload_preview"),
            rs.getString("response_body"),
            rs.getTimestamp("delivered_at").toLocalDateTime().toString().replace('T', ' ')
    );

    private final JdbcTemplate jdbcTemplate;

    public JdbcPushDeliveryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insert(
            Long brandId,
            String channelType,
            String status,
            String webhookMasked,
            String payloadPreview,
            String responseBody
    ) {
        jdbcTemplate.update(
                """
                        INSERT INTO push_delivery_log
                        (brand_id, channel_type, status, webhook_masked, payload_preview, response_body)
                        VALUES (?, ?, ?, ?, ?, ?)
                        """,
                brandId,
                channelType,
                status,
                webhookMasked,
                payloadPreview,
                responseBody
        );
    }

    public List<PushDeliveryRecord> listRecent(Long brandId, int limit) {
        return jdbcTemplate.query(
                """
                        SELECT id, brand_id, channel_type, status, webhook_masked, payload_preview, response_body, delivered_at
                        FROM push_delivery_log
                        WHERE brand_id = ?
                        ORDER BY delivered_at DESC
                        LIMIT ?
                        """,
                ROW_MAPPER,
                brandId,
                limit
        );
    }
}
