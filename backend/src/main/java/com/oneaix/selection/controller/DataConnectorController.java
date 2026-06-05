package com.oneaix.selection.controller;

import com.oneaix.selection.dto.DataConnectorsOverview;
import com.oneaix.selection.service.connector.DataConnectorService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 蝉妈妈/飞瓜/1688/专利数据源样例 2026-06-04 */
@RestController
@RequestMapping("/api/connectors")
@Tag(name = "数据源连接")
public class DataConnectorController {

    private final DataConnectorService dataConnectorService;

    public DataConnectorController(DataConnectorService dataConnectorService) {
        this.dataConnectorService = dataConnectorService;
    }

    @GetMapping("/overview")
    public DataConnectorsOverview overview() {
        return dataConnectorService.overview();
    }
}
