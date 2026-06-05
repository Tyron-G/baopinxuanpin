package com.oneaix.selection.controller;

import com.oneaix.selection.constant.ApiConstants;
import com.oneaix.selection.dto.PushChannelConfig;
import com.oneaix.selection.dto.PushChannelRequest;
import com.oneaix.selection.dto.PushDeliveryRecord;
import com.oneaix.selection.dto.PushDigestResult;
import com.oneaix.selection.service.push.SignalPushService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** 信号推送（钉钉/企业微信）2026-06-04 */
@Validated
@RestController
@RequestMapping("/api/push")
@Tag(name = "信号推送")
public class PushController {

    private final SignalPushService signalPushService;

    public PushController(SignalPushService signalPushService) {
        this.signalPushService = signalPushService;
    }

    @GetMapping("/config")
    public List<PushChannelConfig> listConfig(
            @RequestParam(defaultValue = ApiConstants.DEFAULT_BRAND_ID_PARAM) @Min(1) Long brandId
    ) {
        return signalPushService.listConfig(brandId);
    }

    @PostMapping("/config")
    public PushChannelConfig saveConfig(
            @RequestParam(defaultValue = ApiConstants.DEFAULT_BRAND_ID_PARAM) @Min(1) Long brandId,
            @Valid @RequestBody PushChannelRequest request
    ) {
        return signalPushService.saveConfig(brandId, request.channelType(), request.webhookUrl(), request.enabled());
    }

    @PostMapping("/digest")
    public PushDigestResult pushDigest(
            @RequestParam(defaultValue = ApiConstants.DEFAULT_BRAND_ID_PARAM) @Min(1) Long brandId
    ) {
        return signalPushService.pushTodayDigest(brandId);
    }

    @GetMapping("/deliveries")
    public List<PushDeliveryRecord> deliveries(
            @RequestParam(defaultValue = ApiConstants.DEFAULT_BRAND_ID_PARAM) @Min(1) Long brandId,
            @RequestParam(defaultValue = "10") @Min(1) int limit
    ) {
        return signalPushService.listDeliveries(brandId, Math.min(limit, 50));
    }
}
