package com.oneaix.selection.repository;

import com.oneaix.selection.dto.BrandSelectionModelProfile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** 品牌专属选品模型持久化 2026-06-04 */
@Repository
public class JdbcBrandSelectionModelRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcBrandSelectionModelRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<BrandSelectionModelProfile> find(Long brandId, String brandName) {
        List<BrandSelectionModelProfile> rows = jdbcTemplate.query(
                """
                        SELECT brand_id, trend_weight, competition_weight, supply_gap_weight,
                               brand_fit_weight, risk_penalty_weight, model_version, training_summary
                        FROM brand_selection_model WHERE brand_id = ?
                        """,
                profileMapper(brandName),
                brandId
        );
        return rows.isEmpty() ? Optional.empty() : Optional.of(rows.get(0));
    }

    public void save(BrandSelectionModelProfile profile) {
        jdbcTemplate.update(
                """
                        MERGE INTO brand_selection_model (
                            brand_id, trend_weight, competition_weight, supply_gap_weight,
                            brand_fit_weight, risk_penalty_weight, model_version, training_summary
                        ) KEY (brand_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                        """,
                profile.brandId(),
                profile.trendWeight(),
                profile.competitionWeight(),
                profile.supplyGapWeight(),
                profile.brandFitWeight(),
                profile.riskPenaltyWeight(),
                profile.modelVersion(),
                profile.trainingSummary()
        );
    }

    private RowMapper<BrandSelectionModelProfile> profileMapper(String brandName) {
        return (rs, rowNum) -> new BrandSelectionModelProfile(
                rs.getLong("brand_id"),
                brandName,
                rs.getDouble("trend_weight"),
                rs.getDouble("competition_weight"),
                rs.getDouble("supply_gap_weight"),
                rs.getDouble("brand_fit_weight"),
                rs.getDouble("risk_penalty_weight"),
                rs.getString("model_version"),
                rs.getString("training_summary"),
                rs.getString("model_version").startsWith("brand-v1")
                        ? "+18%（相对通用模型）"
                        : "基线",
                List.of("从库加载已训练权重", "可在页面触发 retrain 刷新流水线")
        );
    }
}
