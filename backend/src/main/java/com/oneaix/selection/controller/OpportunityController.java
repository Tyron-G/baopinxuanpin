package com.oneaix.selection.controller;

import com.oneaix.selection.constant.ApiConstants;
import com.oneaix.selection.dto.ActionStatusUpdateRequest;
import com.oneaix.selection.dto.OpportunityDetail;
import com.oneaix.selection.entity.Opportunity;
import com.oneaix.selection.service.ActionStatusTracker;
import com.oneaix.selection.service.OpportunityService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api/opportunity")
@Tag(name = "爆品机会")
public class OpportunityController {
    private final OpportunityService opportunityService;
    private final ActionStatusTracker actionStatusTracker;

    public OpportunityController(OpportunityService opportunityService, ActionStatusTracker actionStatusTracker) {
        this.opportunityService = opportunityService;
        this.actionStatusTracker = actionStatusTracker;
    }

    @GetMapping("/{cardId}")
    public OpportunityDetail detail(
            @PathVariable @Min(1) Long cardId,
            @RequestParam(defaultValue = ApiConstants.DEFAULT_BRAND_ID_PARAM) @Min(1) Long brandId,
            @RequestParam(defaultValue = ApiConstants.DEFAULT_PLATFORM_VIEW) String platform
    ) {
        return opportunityService.detail(cardId, brandId, platform);
    }

    @GetMapping("/{cardId}/points")
    public List<Opportunity> points(
            @PathVariable @Min(1) Long cardId,
            @RequestParam(defaultValue = ApiConstants.DEFAULT_BRAND_ID_PARAM) @Min(1) Long brandId,
            @RequestParam(defaultValue = ApiConstants.DEFAULT_PLATFORM_VIEW) String platform
    ) {
        return opportunityService.points(cardId, brandId, platform);
    }

    @PostMapping("/{cardId}/actions/{actionTitle}")
    public void updateActionStatus(
            @PathVariable @Min(1) Long cardId,
            @PathVariable String actionTitle,
            @Valid @RequestBody ActionStatusUpdateRequest request
    ) {
        actionStatusTracker.update(cardId, actionTitle, request.status(), request.note());
    }
}
