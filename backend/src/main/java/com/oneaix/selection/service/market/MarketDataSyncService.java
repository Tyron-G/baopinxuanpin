package com.oneaix.selection.service.market;

import com.oneaix.selection.config.MarketDataProperties;
import com.oneaix.selection.dto.MarketDataSyncResult;
import com.oneaix.selection.entity.CategoryTrend;
import com.oneaix.selection.entity.CompetitionData;
import com.oneaix.selection.entity.SupplyDemand;
import com.oneaix.selection.mapper.CategoryTrendMapper;
import com.oneaix.selection.mapper.CompetitionDataMapper;
import com.oneaix.selection.mapper.SupplyDemandMapper;
import com.oneaix.selection.repository.market.JdbcMarketDataRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

/** 将外部市场 API 拉取结果写入本地库（替换样例链路入口）2026-06-05 */
@Service
public class MarketDataSyncService {

    private static final DateTimeFormatter SYNC_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final MarketDataProperties properties;
    private final ExternalMarketDataClient externalClient;
    private final JdbcMarketDataRepository jdbcRepository;
    private final CategoryTrendMapper categoryTrendMapper;
    private final CompetitionDataMapper competitionDataMapper;
    private final SupplyDemandMapper supplyDemandMapper;

    private volatile String lastSyncAt = "-";
    private volatile String lastSyncStatus = "未同步";

    public MarketDataSyncService(
            MarketDataProperties properties,
            ExternalMarketDataClient externalClient,
            JdbcMarketDataRepository jdbcRepository,
            CategoryTrendMapper categoryTrendMapper,
            CompetitionDataMapper competitionDataMapper,
            SupplyDemandMapper supplyDemandMapper
    ) {
        this.properties = properties;
        this.externalClient = externalClient;
        this.jdbcRepository = jdbcRepository;
        this.categoryTrendMapper = categoryTrendMapper;
        this.competitionDataMapper = competitionDataMapper;
        this.supplyDemandMapper = supplyDemandMapper;
    }

    public MarketDataSyncResult syncVisibleCategories(Set<String> categories, String platform) {
        if (!externalClient.isConfigured()) {
            return new MarketDataSyncResult(false, "外部市场 API 未启用，请在 application.yml 配置 selection.market-data", lastSyncAt);
        }
        int trendCount = 0;
        int competitionCount = 0;
        int supplyCount = 0;
        var trends = externalClient.fetchTrends(categories, platform).orElse(List.of());
        for (CategoryTrend row : trends) {
            categoryTrendMapper.insert(row);
            trendCount++;
        }
        var competition = externalClient.fetchCompetition(categories, platform).orElse(List.of());
        for (CompetitionData row : competition) {
            competitionDataMapper.insert(row);
            competitionCount++;
        }
        var supply = externalClient.fetchSupplyDemand(categories, platform).orElse(List.of());
        for (SupplyDemand row : supply) {
            supplyDemandMapper.insert(row);
            supplyCount++;
        }
        boolean success = trendCount + competitionCount + supplyCount > 0;
        lastSyncAt = LocalDateTime.now().format(SYNC_TIME);
        lastSyncStatus = success ? "已同步" : "外部返回空数据，仍使用 JDBC 样例";
        String message = success
                ? "已写入趋势 " + trendCount + " 条、竞争 " + competitionCount + " 条、供需 " + supplyCount + " 条"
                : "外部 API 无数据返回，请检查网关地址与 categories 参数";
        return new MarketDataSyncResult(success, message, lastSyncAt);
    }

    public String lastSyncAt() {
        return lastSyncAt;
    }

    public String lastSyncStatus() {
        return lastSyncStatus;
    }

    public String dataSourceLabel() {
        return properties.externalPrimary() ? "external-primary" : "jdbc-sample";
    }
}
