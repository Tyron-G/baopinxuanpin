package com.oneaix.selection.controller;



import com.oneaix.selection.constant.ApiConstants;

import com.oneaix.selection.dto.DataConnectorsOverview;

import com.oneaix.selection.dto.MarketDataSyncResult;

import com.oneaix.selection.service.BrandSelectionContextLoader;

import com.oneaix.selection.service.connector.DataConnectorService;

import com.oneaix.selection.service.market.MarketDataSyncService;

import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.constraints.Min;

import org.springframework.validation.annotation.Validated;

import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.bind.annotation.RestController;



/** 蝉妈妈/飞瓜/1688/专利数据源 + 市场同步 2026-06-05 */

@Validated

@RestController

@RequestMapping("/api/connectors")

@Tag(name = "数据源连接")

public class DataConnectorController {



    private final DataConnectorService dataConnectorService;

    private final MarketDataSyncService marketDataSyncService;

    private final BrandSelectionContextLoader contextLoader;



    public DataConnectorController(

            DataConnectorService dataConnectorService,

            MarketDataSyncService marketDataSyncService,

            BrandSelectionContextLoader contextLoader

    ) {

        this.dataConnectorService = dataConnectorService;

        this.marketDataSyncService = marketDataSyncService;

        this.contextLoader = contextLoader;

    }



    @GetMapping("/overview")

    public DataConnectorsOverview overview() {

        return dataConnectorService.overview();

    }



    @PostMapping("/sync-market")

    public MarketDataSyncResult syncMarket(

            @RequestParam(defaultValue = ApiConstants.DEFAULT_BRAND_ID_PARAM) @Min(1) Long brandId,

            @RequestParam(required = false) String platform

    ) {

        var context = contextLoader.load(brandId);

        return marketDataSyncService.syncVisibleCategories(context.visibleCategoryNames(), platform);

    }

}


