package com.oneaix.selection.service;

import com.oneaix.selection.annotation.TrackedExecution;
import com.oneaix.selection.dto.BrandSelectionContext;
import com.oneaix.selection.dto.OpportunityDetail;
import com.oneaix.selection.entity.Opportunity;
import com.oneaix.selection.enums.PlatformView;
import com.oneaix.selection.service.catalog.InsightCardCatalogService;
import com.oneaix.selection.service.opportunity.OpportunityDetailAssembler;
import com.oneaix.selection.service.opportunity.OpportunityPointService;
import org.springframework.stereotype.Service;

import java.util.List;

/** 机会详情 API 编排 2026-06-04 */
@Service
public class OpportunityService {

    private final BrandSelectionContextLoader contextLoader;
    private final InsightCardCatalogService catalogService;
    private final OpportunityDetailAssembler detailAssembler;
    private final OpportunityPointService opportunityPointService;

    public OpportunityService(
            BrandSelectionContextLoader contextLoader,
            InsightCardCatalogService catalogService,
            OpportunityDetailAssembler detailAssembler,
            OpportunityPointService opportunityPointService
    ) {
        this.contextLoader = contextLoader;
        this.catalogService = catalogService;
        this.detailAssembler = detailAssembler;
        this.opportunityPointService = opportunityPointService;
    }

    public OpportunityDetail detail(Long cardId, Long brandId) {
        return detail(cardId, brandId, PlatformView.ALL.getLabel());
    }

    @TrackedExecution(value = "opportunity-detail", domain = "opportunity")
    public OpportunityDetail detail(Long cardId, Long brandId, String platformView) {
        return detail(cardId, contextLoader.load(brandId), platformView);
    }

    public OpportunityDetail detail(Long cardId, BrandSelectionContext context, String platformView) {
        return detailAssembler.assemble(cardId, context, platformView);
    }

    public List<Opportunity> points(Long cardId, Long brandId, String platformView) {
        BrandSelectionContext context = contextLoader.load(brandId);
        catalogService.requireVisible(cardId, context);
        var card = catalogService.requireVisible(cardId, context);
        return opportunityPointService.list(cardId, card.getCategoryName(), platformView);
    }
}
