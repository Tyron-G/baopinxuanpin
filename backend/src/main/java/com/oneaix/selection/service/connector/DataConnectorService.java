package com.oneaix.selection.service.connector;

import com.oneaix.selection.content.SampleDataConnectorCatalog;
import com.oneaix.selection.dto.DataConnectorsOverview;
import org.springframework.stereotype.Service;

/** 蝉妈妈/飞瓜等数据源样例总览 2026-06-04 */
@Service
public class DataConnectorService {

    private final SampleDataConnectorCatalog connectorCatalog;

    public DataConnectorService(SampleDataConnectorCatalog connectorCatalog) {
        this.connectorCatalog = connectorCatalog;
    }

    public DataConnectorsOverview overview() {
        return connectorCatalog.overview();
    }
}
