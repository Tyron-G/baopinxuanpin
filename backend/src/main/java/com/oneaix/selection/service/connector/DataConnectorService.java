package com.oneaix.selection.service.connector;

import com.oneaix.selection.content.SampleDataConnectorCatalog;
import com.oneaix.selection.dto.DataConnectorsOverview;
import com.oneaix.selection.service.market.MarketDataSyncService;
import org.springframework.stereotype.Service;

/** 蝉妈妈/飞瓜等数据源总览 + 市场同步状态 2026-06-05 */
@Service
public class DataConnectorService {

    private final SampleDataConnectorCatalog connectorCatalog;
    private final MarketDataSyncService marketDataSyncService;

    public DataConnectorService(
            SampleDataConnectorCatalog connectorCatalog,
            MarketDataSyncService marketDataSyncService
    ) {
        this.connectorCatalog = connectorCatalog;
        this.marketDataSyncService = marketDataSyncService;
    }

    public DataConnectorsOverview overview() {
        var base = connectorCatalog.overview();
        return new DataConnectorsOverview(
                base.connectors(),
                base.chanmamaFeeds(),
                base.feiguaFeeds(),
                marketDataSyncService.dataSourceLabel(),
                marketDataSyncService.lastSyncAt(),
                marketDataSyncService.lastSyncStatus()
        );
    }
}
