package com.oneaix.selection.repository.market;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.oneaix.selection.entity.CategoryTrend;
import com.oneaix.selection.entity.CompetitionData;
import com.oneaix.selection.entity.SupplyDemand;
import com.oneaix.selection.mapper.CategoryTrendMapper;
import com.oneaix.selection.mapper.CompetitionDataMapper;
import com.oneaix.selection.mapper.SupplyDemandMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

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
        if (CollectionUtils.isEmpty(categoryNames)) {
            return List.of();
        }
        return categoryTrendMapper.selectList(new LambdaQueryWrapper<CategoryTrend>()
                .in(CategoryTrend::getCategoryName, categoryNames)
                .orderByAsc(CategoryTrend::getCategoryName)
                .orderByAsc(CategoryTrend::getPlatform)
                .orderByAsc(CategoryTrend::getTrendMonth));
    }

    @Override
    public List<CompetitionData> findCompetitionByCategories(Collection<String> categoryNames) {
        if (CollectionUtils.isEmpty(categoryNames)) {
            return List.of();
        }
        return competitionDataMapper.selectList(new LambdaQueryWrapper<CompetitionData>()
                .in(CompetitionData::getCategoryName, categoryNames)
                .orderByAsc(CompetitionData::getPlatform)
                .orderByAsc(CompetitionData::getId));
    }

    @Override
    public List<SupplyDemand> findSupplyDemandByCategories(Collection<String> categoryNames) {
        if (CollectionUtils.isEmpty(categoryNames)) {
            return List.of();
        }
        return supplyDemandMapper.selectList(new LambdaQueryWrapper<SupplyDemand>()
                .in(SupplyDemand::getCategoryName, categoryNames)
                .orderByAsc(SupplyDemand::getPlatform)
                .orderByAsc(SupplyDemand::getCategoryName)
                .orderByAsc(SupplyDemand::getId));
    }
}
