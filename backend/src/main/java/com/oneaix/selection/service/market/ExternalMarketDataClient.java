package com.oneaix.selection.service.market;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oneaix.selection.config.MarketDataProperties;
import com.oneaix.selection.entity.CategoryTrend;
import com.oneaix.selection.entity.CompetitionData;
import com.oneaix.selection.entity.SupplyDemand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/** 外部搜索/类目市场数据 HTTP 客户端（蝉妈妈/飞瓜等聚合网关）2026-06-05 */
@Component
public class ExternalMarketDataClient {

    private static final Logger log = LoggerFactory.getLogger(ExternalMarketDataClient.class);

    private final MarketDataProperties properties;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    public ExternalMarketDataClient(
            MarketDataProperties properties,
            ObjectMapper objectMapper,
            RestTemplateBuilder restTemplateBuilder
    ) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        MarketDataProperties.External external = properties.getExternal();
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofMillis(external.getConnectTimeoutMs()))
                .setReadTimeout(Duration.ofMillis(external.getReadTimeoutMs()))
                .build();
    }

    public boolean isConfigured() {
        MarketDataProperties.External external = properties.getExternal();
        return properties.externalPrimary()
                && external.getBaseUrl() != null
                && !external.getBaseUrl().isBlank();
    }

    public Optional<List<CategoryTrend>> fetchTrends(Collection<String> categories, String platform) {
        return fetchList(properties.getExternal().getTrendsPath(), categories, platform, new TypeReference<>() {});
    }

    public Optional<List<CompetitionData>> fetchCompetition(Collection<String> categories, String platform) {
        return fetchList(properties.getExternal().getCompetitionPath(), categories, platform, new TypeReference<>() {});
    }

    public Optional<List<SupplyDemand>> fetchSupplyDemand(Collection<String> categories, String platform) {
        return fetchList(properties.getExternal().getSupplyPath(), categories, platform, new TypeReference<>() {});
    }

    private <T> Optional<List<T>> fetchList(
            String path,
            Collection<String> categories,
            String platform,
            TypeReference<List<T>> type
    ) {
        if (!isConfigured() || categories == null || categories.isEmpty()) {
            return Optional.empty();
        }
        try {
            String url = UriComponentsBuilder.fromHttpUrl(trimBase(properties.getExternal().getBaseUrl()) + path)
                    .queryParam("categories", String.join(",", categories))
                    .queryParam("platform", platform == null ? "" : platform)
                    .build()
                    .toUriString();
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));
            if (properties.getExternal().getApiKey() != null && !properties.getExternal().getApiKey().isBlank()) {
                headers.set("X-Api-Key", properties.getExternal().getApiKey());
            }
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    String.class
            );
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null || response.getBody().isBlank()) {
                return Optional.empty();
            }
            return Optional.of(objectMapper.readValue(response.getBody(), type));
        } catch (RestClientException | com.fasterxml.jackson.core.JsonProcessingException ex) {
            log.warn("external market data fetch failed path={}: {}", path, ex.getMessage());
            return Optional.empty();
        }
    }

    private static String trimBase(String baseUrl) {
        return baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }
}
