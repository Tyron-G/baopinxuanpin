package com.oneaix.selection.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.oneaix.selection.dto.CompetitorTimelinePoint;
import com.oneaix.selection.entity.CompetitorTimelinePointEntity;
import com.oneaix.selection.mapper.CompetitorTimelinePointMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/** H2 竞品时间轴节点仓储 2026-06-04 */
@Repository
public class JdbcCompetitorTimelinePointRepository implements CompetitorTimelinePointRepository {

    private final CompetitorTimelinePointMapper mapper;

    public JdbcCompetitorTimelinePointRepository(CompetitorTimelinePointMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public List<CompetitorTimelinePoint> findByShop(String shopName, String platform, String focusCategory) {
        return mapper.selectList(new LambdaQueryWrapper<CompetitorTimelinePointEntity>()
                        .eq(CompetitorTimelinePointEntity::getShopName, shopName)
                        .eq(CompetitorTimelinePointEntity::getPlatform, platform)
                        .eq(CompetitorTimelinePointEntity::getFocusCategory, focusCategory)
                        .orderByAsc(CompetitorTimelinePointEntity::getWeekOrder))
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public boolean existsByShop(String shopName, String platform, String focusCategory) {
        return mapper.selectCount(new LambdaQueryWrapper<CompetitorTimelinePointEntity>()
                .eq(CompetitorTimelinePointEntity::getShopName, shopName)
                .eq(CompetitorTimelinePointEntity::getPlatform, platform)
                .eq(CompetitorTimelinePointEntity::getFocusCategory, focusCategory)) > 0;
    }

    @Override
    public void saveBasePoints(String shopName, String platform, String focusCategory, List<CompetitorTimelinePoint> points) {
        if (points == null || points.isEmpty() || existsByShop(shopName, platform, focusCategory)) {
            return;
        }
        for (int index = 0; index < points.size(); index++) {
            CompetitorTimelinePoint point = points.get(index);
            CompetitorTimelinePointEntity entity = new CompetitorTimelinePointEntity();
            entity.setShopName(shopName);
            entity.setPlatform(platform);
            entity.setFocusCategory(focusCategory);
            entity.setWeekOrder(index + 1);
            entity.setWeekLabel(point.period());
            entity.setHeatIndex(point.heatIndex());
            entity.setSalesIndex(point.salesIndex());
            entity.setNote(point.note());
            mapper.insert(entity);
        }
    }

    private CompetitorTimelinePoint toDto(CompetitorTimelinePointEntity entity) {
        return new CompetitorTimelinePoint(
                entity.getWeekLabel(),
                entity.getHeatIndex(),
                entity.getSalesIndex(),
                entity.getNote()
        );
    }
}
