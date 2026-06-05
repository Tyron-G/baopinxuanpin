package com.oneaix.selection.controller;

import com.oneaix.selection.constant.ApiConstants;
import com.oneaix.selection.dto.OpportunityRankingPage;
import com.oneaix.selection.service.BrandSelectionContextLoader;
import com.oneaix.selection.service.catalog.InsightCardQueryService;
import com.oneaix.selection.service.ranking.OpportunityRankingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** 机会榜单 TOP50（MVP P0）2026-06-04 */
@Validated
@RestController
@RequestMapping("/api/ranking")
@Tag(name = "机会榜单")
public class RankingController {

    private final BrandSelectionContextLoader contextLoader;
    private final InsightCardQueryService cardQueryService;
    private final OpportunityRankingService rankingService;

    public RankingController(
            BrandSelectionContextLoader contextLoader,
            InsightCardQueryService cardQueryService,
            OpportunityRankingService rankingService
    ) {
        this.contextLoader = contextLoader;
        this.cardQueryService = cardQueryService;
        this.rankingService = rankingService;
    }

    @GetMapping("/top50")
    public OpportunityRankingPage top50(
            @RequestParam(defaultValue = ApiConstants.DEFAULT_BRAND_ID_PARAM) @Min(1) Long brandId,
            @RequestParam(defaultValue = "1") @Min(1) int page,
            @RequestParam(defaultValue = "50") @Min(1) @Max(50) int pageSize,
            @RequestParam(required = false) String platform
    ) {
        var context = contextLoader.load(brandId);
        var cards = cardQueryService.rankedViews(context.brand(), context.catalog(), platform);
        return rankingService.top50(cards, page, pageSize);
    }
}
