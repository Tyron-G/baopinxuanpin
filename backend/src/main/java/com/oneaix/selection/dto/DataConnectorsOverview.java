package com.oneaix.selection.dto;

import java.util.List;

/** 第三方数据源总览（样例）2026-06-04 */
public record DataConnectorsOverview(
        List<DataConnectorStatus> connectors,
        List<ChanmamaFeedSample> chanmamaFeeds,
        List<FeiguaFeedSample> feiguaFeeds
) {
}
