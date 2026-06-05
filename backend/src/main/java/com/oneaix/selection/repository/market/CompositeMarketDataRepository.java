package com.oneaix.selection.repository.market;

import com.oneaix.selection.entity.CategoryTrend;
import com.oneaix.selection.entity.CompetitionData;
import com.oneaix.selection.entity.SupplyDemand;
import com.oneaix.selection.enums.PlatformView;
import com.oneaix.selection.service.market.ExternalMarketDataClient;
import com.oneaix.selection.util.PlatformMarketFilter;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/** 外部 API 优先、JDBC 兜底的市场数据仓储 2026-06-05 */
@Primary
@Repository
public class CompositeMarketDataRepository implements MarketDataRepository {

    private final JdbcMarketDataRepository jdbcRepository;
    private final ExternalMarketDataClient externalClient;

    public CompositeMarketDataRepository(
            JdbcMarketDataRepository jdbcRepository,
            ExternalMarketDataClient externalClient
    ) {
        this.jdbcRepository = jdbcRepository;
        this.externalClient = externalClient;
    }

    @Override
    public List<CategoryTrend> findTrendsByCategories(Collection<String> categoryNames) {
        return findTrendsByCategories(categoryNames, null);
    }

    @Override
    public List<CategoryTrend> findTrendsByCategories(Collection<String> categoryNames, String platform) {
        if (externalClient.isConfigured()) {
            var fetched = externalClient.fetchTrends(categoryNames, platform);
            if (fetched.isPresent() && !fetched.get().isEmpty()) {
                return filterPlatform(fetched.get(), platform, CategoryTrend::getPlatform);
            }
        }
        return jdbcRepository.findTrendsByCategories(categoryNames, platform);
    }

    @Override
    public List<CompetitionData> findCompetitionByCategories(Collection<String> categoryNames) {
        return findCompetitionByCategories(categoryNames, null);
    }

    @Override
    public List<CompetitionData> findCompetitionByCategories(Collection<String> categoryNames, String platform) {
        if (externalClient.isConfigured()) {
            var fetched = externalClient.fetchCompetition(categoryNames, platform);
            if (fetched.isPresent() && !fetched.get().isEmpty()) {
                return filterPlatform(fetched.get(), platform, CompetitionData::getPlatform);
            }
        }
        return jdbcRepository.findCompetitionByCategories(categoryNames, platform);
    }

    @Override
    public List<SupplyDemand> findSupplyDemandByCategories(Collection<String> categoryNames) {
        return findSupplyDemandByCategories(categoryNames, null);
    }

    @Override
    public List<SupplyDemand> findSupplyDemandByCategories(Collection<String> categoryNames, String platform) {
        if (externalClient.isConfigured()) {
            var fetched = externalClient.fetchSupplyDemand(categoryNames, platform);
            if (fetched.isPresent() && !fetched.get().isEmpty()) {
                return filterPlatform(fetched.get(), platform, SupplyDemand::getPlatform);
            }
        }
        return jdbcRepository.findSupplyDemandByCategories(categoryNames, platform);
    }

    private <T> List<T> filterPlatform(List<T> rows, String platform, java.util.function.Function<T, String> platformGetter) {
        if (platform == null || platform.isBlank() || PlatformView.ALL.getLabel().equals(platform)) {
            return rows;
        }
        return PlatformMarketFilter.byPlatform(rows, platform, platformGetter);
    }
}
