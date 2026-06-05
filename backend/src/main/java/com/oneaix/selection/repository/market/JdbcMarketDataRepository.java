package com.oneaix.selection.repository.market;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.oneaix.selection.entity.CategoryTrend;
import com.oneaix.selection.entity.CompetitionData;
import com.oneaix.selection.entity.SupplyDemand;
import com.oneaix.selection.enums.PlatformView;
import com.oneaix.selection.mapper.CategoryTrendMapper;
import com.oneaix.selection.mapper.CompetitionDataMapper;
import com.oneaix.selection.mapper.SupplyDemandMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;

/** JDBC 市场数据仓储 2026-06-04 */
@Repository
public class JdbcMarketDataRepository implements MarketDataRepository {

    private final CategoryTrendMapper categoryTrendMapper;
    private final CompetitionDataMapper competitionDataMapper;
    private final SupplyDemandMapper supplyDemandMapper;

    public JdbcMarketDataRepository(
            CategoryTrendMapper categoryTrendMapper,
            CompetitionDataMapper competitionDataMapper,
            SupplyDemandMapper supplyDemandMapper
    ) {
        this.categoryTrendMapper = categoryTrendMapper;
        this.competitionDataMapper = competitionDataMapper;
        this.supplyDemandMapper = supplyDemandMapper;
    }

    @Override
    public List<CategoryTrend> findTrendsByCategories(Collection<String> categoryNames) {
        return findTrendsByCategories(categoryNames, null);
    }

    @Override
    public List<CategoryTrend> findTrendsByCategories(Collection<String> categoryNames, String platform) {
        if (CollectionUtils.isEmpty(categoryNames)) {
            return List.of();
        }
        LambdaQueryWrapper<CategoryTrend> wrapper = new LambdaQueryWrapper<CategoryTrend>()
                .in(CategoryTrend::getCategoryName, categoryNames)
                .orderByAsc(CategoryTrend::getCategoryName)
                .orderByAsc(CategoryTrend::getPlatform)
                .orderByAsc(CategoryTrend::getTrendMonth);
        applyPlatform(wrapper, platform, CategoryTrend::getPlatform);
        return categoryTrendMapper.selectList(wrapper);
    }

    @Override
    public List<CompetitionData> findCompetitionByCategories(Collection<String> categoryNames) {
        return findCompetitionByCategories(categoryNames, null);
    }

    @Override
    public List<CompetitionData> findCompetitionByCategories(Collection<String> categoryNames, String platform) {
        if (CollectionUtils.isEmpty(categoryNames)) {
            return List.of();
        }
        LambdaQueryWrapper<CompetitionData> wrapper = new LambdaQueryWrapper<CompetitionData>()
                .in(CompetitionData::getCategoryName, categoryNames)
                .orderByAsc(CompetitionData::getPlatform)
                .orderByAsc(CompetitionData::getId);
        applyPlatform(wrapper, platform, CompetitionData::getPlatform);
        return competitionDataMapper.selectList(wrapper);
    }

    @Override
    public List<SupplyDemand> findSupplyDemandByCategories(Collection<String> categoryNames) {
        return findSupplyDemandByCategories(categoryNames, null);
    }

    @Override
    public List<SupplyDemand> findSupplyDemandByCategories(Collection<String> categoryNames, String platform) {
        if (CollectionUtils.isEmpty(categoryNames)) {
            return List.of();
        }
        LambdaQueryWrapper<SupplyDemand> wrapper = new LambdaQueryWrapper<SupplyDemand>()
                .in(SupplyDemand::getCategoryName, categoryNames)
                .orderByAsc(SupplyDemand::getPlatform)
                .orderByAsc(SupplyDemand::getCategoryName)
                .orderByAsc(SupplyDemand::getId);
        applyPlatform(wrapper, platform, SupplyDemand::getPlatform);
        return supplyDemandMapper.selectList(wrapper);
    }

    private <T> void applyPlatform(
            LambdaQueryWrapper<T> wrapper,
            String platform,
            com.baomidou.mybatisplus.core.toolkit.support.SFunction<T, ?> platformColumn
    ) {
        if (!StringUtils.hasText(platform) || PlatformView.ALL.getLabel().equals(platform)) {
            return;
        }
        wrapper.eq(platformColumn, PlatformView.normalize(platform).getLabel());
    }
}
