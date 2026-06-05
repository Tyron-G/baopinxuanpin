package com.oneaix.selection.repository;

import com.oneaix.selection.dto.CompetitorTimelinePoint;

import java.util.List;

/** 竞品时间轴节点仓储 2026-06-04 */
public interface CompetitorTimelinePointRepository {

    List<CompetitorTimelinePoint> findByShop(String shopName, String platform, String focusCategory);

    boolean existsByShop(String shopName, String platform, String focusCategory);

    void saveBasePoints(String shopName, String platform, String focusCategory, List<CompetitorTimelinePoint> points);
}
