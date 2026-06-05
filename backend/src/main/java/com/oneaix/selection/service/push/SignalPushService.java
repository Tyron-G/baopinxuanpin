package com.oneaix.selection.service.push;

import com.oneaix.selection.config.PushProperties;
import com.oneaix.selection.dto.BrandSelectionContext;
import com.oneaix.selection.dto.PushChannelConfig;
import com.oneaix.selection.dto.PushDeliveryRecord;
import com.oneaix.selection.dto.PushDigestResult;
import com.oneaix.selection.dto.SignalItem;
import com.oneaix.selection.repository.JdbcPushChannelRepository;
import com.oneaix.selection.repository.JdbcPushDeliveryRepository;
import com.oneaix.selection.enums.PlatformView;
import com.oneaix.selection.service.BrandSelectionContextLoader;
import com.oneaix.selection.service.SignalRadarService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** 钉钉/企微/微信群 Webhook 推送（含样例外发模拟）2026-06-04 */
@Service
public class SignalPushService {

    private final JdbcPushChannelRepository pushChannelRepository;
    private final JdbcPushDeliveryRepository pushDeliveryRepository;
    private final BrandSelectionContextLoader contextLoader;
    private final SignalRadarService signalRadarService;
    private final PushProperties pushProperties;
    private final RestTemplate restTemplate;

    public SignalPushService(
            JdbcPushChannelRepository pushChannelRepository,
            JdbcPushDeliveryRepository pushDeliveryRepository,
            BrandSelectionContextLoader contextLoader,
            SignalRadarService signalRadarService,
            PushProperties pushProperties
    ) {
        this.pushChannelRepository = pushChannelRepository;
        this.pushDeliveryRepository = pushDeliveryRepository;
        this.contextLoader = contextLoader;
        this.signalRadarService = signalRadarService;
        this.pushProperties = pushProperties;
        this.restTemplate = new RestTemplate();
    }

    public List<PushChannelConfig> listConfig(Long brandId) {
        return pushChannelRepository.listByBrand(brandId);
    }

    public PushChannelConfig saveConfig(Long brandId, String channelType, String webhookUrl, boolean enabled) {
        return pushChannelRepository.upsert(brandId, channelType, webhookUrl, enabled);
    }

    public List<PushDeliveryRecord> listDeliveries(Long brandId, int limit) {
        return pushDeliveryRepository.listRecent(brandId, limit);
    }

    public PushDigestResult pushTodayDigest(Long brandId) {
        return pushTodayDigest(brandId, PlatformView.ALL.getLabel());
    }

    public PushDigestResult pushTodayDigest(Long brandId, String platform) {
        BrandSelectionContext context = contextLoader.load(brandId);
        String viewPlatform = platform == null || platform.isBlank()
                ? PlatformView.ALL.getLabel()
                : PlatformView.normalize(platform).getLabel();
        List<SignalItem> signals = signalRadarService.signals(brandId, viewPlatform);
        List<PushChannelConfig> channels = pushChannelRepository.listEnabled(brandId);
        if (channels.isEmpty()) {
            return new PushDigestResult(false, "未配置已启用的推送渠道", signals.size(), List.of(), List.of());
        }
        String body = buildDigestText(context.brand().getBrandName(), viewPlatform, signals);
        List<String> results = new ArrayList<>();
        List<PushDeliveryRecord> deliveries = new ArrayList<>();
        boolean allOk = true;
        for (PushChannelConfig channel : channels) {
            DispatchOutcome outcome = dispatch(brandId, channel, body);
            results.add(channel.channelType() + ": " + outcome.summary());
            if (outcome.record() != null) {
                deliveries.add(outcome.record());
            }
            allOk = allOk && outcome.success();
        }
        String message = allOk ? "今日新发现已推送（样例通道将模拟外发成功）" : "部分渠道推送失败";
        return new PushDigestResult(allOk, message, signals.size(), results, deliveries);
    }

    private String buildDigestText(String brandName, String viewPlatform, List<SignalItem> signals) {
        StringBuilder builder = new StringBuilder();
        builder.append("【爆品选品雷达】").append(brandName).append(" · ").append(viewPlatform).append(" · 今日新发现\n");
        int limit = Math.min(5, signals.size());
        for (int i = 0; i < limit; i++) {
            SignalItem item = signals.get(i);
            builder.append(i + 1).append(". ")
                    .append(item.categoryName())
                    .append(" | ")
                    .append(item.signalType())
                    .append(" | ")
                    .append(item.summary())
                    .append("\n");
        }
        if (signals.isEmpty()) {
            builder.append("今日暂无新增信号，请稍后刷新。");
        }
        return builder.toString();
    }

    private DispatchOutcome dispatch(Long brandId, PushChannelConfig channel, String text) {
        if (channel.webhookUrl() == null || channel.webhookUrl().isBlank()) {
            return DispatchOutcome.failed("Webhook 未配置");
        }
        String preview = text.length() > 120 ? text.substring(0, 120) + "…" : text;
        if (shouldSimulate(channel.webhookUrl())) {
            String response = "{\"errcode\":0,\"errmsg\":\"ok\",\"demo\":true}";
            pushDeliveryRepository.insert(
                    brandId,
                    channel.channelType(),
                    "success",
                    maskWebhook(channel.webhookUrl()),
                    preview,
                    response
            );
            PushDeliveryRecord record = pushDeliveryRepository.listRecent(brandId, 1).stream().findFirst().orElse(null);
            return DispatchOutcome.ok("已发送（样例模拟）", record);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> payload = Map.of(
                "msgtype", "text",
                "text", Map.of("content", text)
        );
        try {
            restTemplate.postForEntity(channel.webhookUrl(), new HttpEntity<>(payload, headers), String.class);
            pushDeliveryRepository.insert(
                    brandId,
                    channel.channelType(),
                    "success",
                    maskWebhook(channel.webhookUrl()),
                    preview,
                    "{\"errcode\":0,\"errmsg\":\"ok\"}"
            );
            PushDeliveryRecord record = pushDeliveryRepository.listRecent(brandId, 1).stream().findFirst().orElse(null);
            return DispatchOutcome.ok("已发送", record);
        } catch (RestClientException ex) {
            pushDeliveryRepository.insert(
                    brandId,
                    channel.channelType(),
                    "failed",
                    maskWebhook(channel.webhookUrl()),
                    preview,
                    ex.getMessage()
            );
            PushDeliveryRecord record = pushDeliveryRepository.listRecent(brandId, 1).stream().findFirst().orElse(null);
            return DispatchOutcome.failed("发送失败", record);
        }
    }

    private boolean shouldSimulate(String webhookUrl) {
        if (!pushProperties.isSimulateDelivery()) {
            return false;
        }
        String lower = webhookUrl.toLowerCase();
        return lower.contains("demo")
                || lower.contains("example.com")
                || lower.contains("localhost")
                || lower.contains("127.0.0.1")
                || lower.contains("mock");
    }

    private String maskWebhook(String url) {
        if (url == null || url.length() < 24) {
            return url;
        }
        return url.substring(0, 20) + "…" + url.substring(url.length() - 8);
    }

    private record DispatchOutcome(boolean success, String summary, PushDeliveryRecord record) {
        static DispatchOutcome ok(String summary, PushDeliveryRecord record) {
            return new DispatchOutcome(true, summary, record);
        }

        static DispatchOutcome failed(String summary) {
            return new DispatchOutcome(false, summary, null);
        }

        static DispatchOutcome failed(String summary, PushDeliveryRecord record) {
            return new DispatchOutcome(false, summary, record);
        }
    }
}
