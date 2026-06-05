package com.oneaix.selection;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** 2026-06-03 用户旅程 API 集成测试 */
@SpringBootTest
@AutoConfigureMockMvc
class SelectionApiFlowTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void brandToOpportunityFlow() throws Exception {
        MvcResult brandResult = mockMvc.perform(post("/api/brand")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "brandName": "流程测试品牌",
                                  "industry": "宠物用品",
                                  "targetCategory": "宠物智能用品",
                                  "hasCategory": true,
                                  "interestDirection": "",
                                  "targetPlatforms": ["天猫", "抖音"],
                                  "budgetRange": "20-50万",
                                  "profitMin": "15-25%",
                                  "supplyChain": "华东供应链",
                                  "stockCycle": "30-60天",
                                  "excludeCategories": []
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andReturn();

        long brandId = dataNode(brandResult).get("id").asLong();

        mockMvc.perform(get("/api/insight/cards").param("brandId", String.valueOf(brandId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].card.categoryName").value("宠物智能用品"))
                .andExpect(jsonPath("$.data[0].pinned").value(true));

        mockMvc.perform(get("/api/insight/trend").param("brandId", String.valueOf(brandId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].platform").exists());

        mockMvc.perform(get("/api/insight/competition").param("brandId", String.valueOf(brandId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].platform").exists());

        mockMvc.perform(get("/api/insight/supply-demand").param("brandId", String.valueOf(brandId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].platform").exists());

        mockMvc.perform(get("/api/radar/signals").param("brandId", String.valueOf(brandId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].score").exists());

        MvcResult radarAll = mockMvc.perform(get("/api/radar/signals")
                        .param("brandId", String.valueOf(brandId))
                        .param("platform", "全平台"))
                .andExpect(status().isOk())
                .andReturn();
        MvcResult radarTmall = mockMvc.perform(get("/api/radar/signals")
                        .param("brandId", String.valueOf(brandId))
                        .param("platform", "天猫"))
                .andExpect(status().isOk())
                .andReturn();
        assertEquals("+74.4%", signalMetricById(radarAll, "sig-card-1"));
        assertEquals("+60.1%", signalMetricById(radarTmall, "sig-card-1"));

        MvcResult rankingAll = mockMvc.perform(get("/api/ranking/top50")
                        .param("brandId", String.valueOf(brandId))
                        .param("platform", "全平台"))
                .andExpect(status().isOk())
                .andReturn();
        MvcResult rankingTmall = mockMvc.perform(get("/api/ranking/top50")
                        .param("brandId", String.valueOf(brandId))
                        .param("platform", "天猫"))
                .andExpect(status().isOk())
                .andReturn();
        assertEquals(65, rankingScoreByCardId(rankingAll, 2));
        assertEquals(54, rankingScoreByCardId(rankingTmall, 2));

        mockMvc.perform(get("/api/opportunity/1").param("brandId", String.valueOf(brandId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.insightCard.categoryName").value("宠物智能用品"))
                .andExpect(jsonPath("$.data.platformPlaybook.firstLaunchPlatform").exists())
                .andExpect(jsonPath("$.data.competitorSummary.trackedShopCount").exists())
                .andExpect(jsonPath("$.data.differentiationAdvice[0]").exists());

        mockMvc.perform(get("/api/opportunity/1")
                        .param("brandId", String.valueOf(brandId))
                        .param("platform", "抖音"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.competitionReport.summary").value(org.hamcrest.Matchers.containsString("抖音")))
                .andExpect(jsonPath("$.data.nextActions[0].expectedGoal").value(org.hamcrest.Matchers.containsString("抖音")));

        mockMvc.perform(get("/api/report/1").param("brandId", String.valueOf(brandId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.fileName").exists())
                .andExpect(jsonPath("$.data.content").isNotEmpty())
                .andExpect(jsonPath("$.data.platformView").exists())
                .andExpect(jsonPath("$.data.opportunityNarrative.competitorSummary.summary").exists())
                .andExpect(jsonPath("$.data.opportunityNarrative.platformPlaybook.firstLaunchPlatform").exists())
                .andExpect(jsonPath("$.data.opportunityNarrative.competitorComparison[0].shopName").exists())
                .andExpect(jsonPath("$.data.opportunityNarrative.differentiationAdvice[0]").exists())
                .andExpect(jsonPath("$.data.opportunityNarrative.opportunityLensFocuses[0].lensLabel").exists())
                .andExpect(jsonPath("$.data.opportunityNarrative.opportunityLensFocuses[0].scenarioText").exists())
                .andExpect(jsonPath("$.data.riskSummary.totalCount").exists())
                .andExpect(jsonPath("$.data.riskSummary.primaryRiskTitle").exists())
                .andExpect(jsonPath("$.data.actionSummary.totalCount").value(3))
                .andExpect(jsonPath("$.data.actionSummary.focusActionTitle").exists());

        mockMvc.perform(get("/api/report/1")
                        .param("brandId", String.valueOf(brandId))
                        .param("platform", "抖音"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.platformView").value("抖音"))
                .andExpect(jsonPath("$.data.competitionReport.summary").value(org.hamcrest.Matchers.containsString("抖音")))
                .andExpect(jsonPath("$.data.nextActions[0].expectedGoal").value(org.hamcrest.Matchers.containsString("抖音")));

        mockMvc.perform(get("/api/competitor/timeline")
                        .param("brandId", String.valueOf(brandId))
                        .param("category", "宠物智能用品")
                        .param("platform", "天猫"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].shopName").exists())
                .andExpect(jsonPath("$.data[0].platform").value("天猫"))
                .andExpect(jsonPath("$.data[0].points[0].period").value("第1周"))
                .andExpect(jsonPath("$.data[0].summary").exists());
    }

    @Test
    void excludeCategoryShouldFilterCards() throws Exception {
        MvcResult brandResult = mockMvc.perform(post("/api/brand")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "brandName": "排除测试",
                                  "industry": "宠物用品",
                                  "targetCategory": "宠物智能用品",
                                  "hasCategory": true,
                                  "interestDirection": "",
                                  "targetPlatforms": ["天猫"],
                                  "budgetRange": "20-50万",
                                  "profitMin": "15-25%",
                                  "supplyChain": "",
                                  "stockCycle": "30-60天",
                                  "excludeCategories": ["宠物"]
                                }
                                """))
                .andExpect(status().isOk())
                .andReturn();

        long brandId = dataNode(brandResult).get("id").asLong();

        MvcResult cardsResult = mockMvc.perform(get("/api/insight/cards").param("brandId", String.valueOf(brandId)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode cards = dataNode(cardsResult);
        for (JsonNode card : cards) {
            assertTrue(!card.get("card").get("categoryName").asText().contains("宠物"));
        }
    }

    @Test
    void competitorCanBeAdded() throws Exception {
        mockMvc.perform(post("/api/competitor")
                        .param("brandId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "shopName": "测试竞品店",
                                  "platform": "天猫",
                                  "focusCategory": "宠物智能用品",
                                  "cardId": 1,
                                  "sourceSignalId": "sig-search-001",
                                  "sourceSignalType": "搜索飙升"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.shopName").value("测试竞品店"))
                .andExpect(jsonPath("$.data.cardId").value(1))
                .andExpect(jsonPath("$.data.sourceSignalType").value("搜索飙升"))
                .andExpect(jsonPath("$.data.hitProductCount").value(3))
                .andExpect(jsonPath("$.data.complaintTopics[0]").exists())
                .andExpect(jsonPath("$.data.opportunityTags[0]").exists());

        mockMvc.perform(get("/api/competitor/timeline")
                        .param("brandId", "1")
                        .param("category", "宠物智能用品")
                        .param("platform", "天猫"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[?(@.shopName=='测试竞品店')].points[0].period").value("第1周"))
                .andExpect(jsonPath("$.data[?(@.shopName=='测试竞品店')].points[3].period").value("第4周"));
    }

    @Test
    void actionStatusCanBeUpdated() throws Exception {
        mockMvc.perform(post("/api/opportunity/{cardId}/actions/{actionTitle}", 1, "确认 7 天小样验证方案")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "进行中",
                                  "note": "已开始准备样品和投放素材"
                                }
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/opportunity/1").param("brandId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nextActions[0].status").value("进行中"))
                .andExpect(jsonPath("$.data.nextActions[0].note").value("已开始准备样品和投放素材"));
    }

    @Test
    void actionStatusCorsShouldAllowPlaywrightPort4173() throws Exception {
        mockMvc.perform(options("/api/opportunity/{cardId}/actions/{actionTitle}", 1, "确认 7 天小样验证方案")
                        .header("Origin", "http://127.0.0.1:4173")
                        .header("Access-Control-Request-Method", "POST"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://127.0.0.1:4173"));
    }

    @Test
    void actionStatusCorsShouldAllowLocalhostDevPort5173() throws Exception {
        mockMvc.perform(options("/api/opportunity/{cardId}/actions/{actionTitle}", 1, "确认 7 天小样验证方案")
                        .header("Origin", "http://localhost:5173")
                        .header("Access-Control-Request-Method", "POST"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"));
    }

    @Test
    void excludedCardPointsShouldReturnNotFound() throws Exception {
        MvcResult brandResult = mockMvc.perform(post("/api/brand")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "brandName": "排除机会点测试",
                                  "industry": "消费品",
                                  "targetCategory": "",
                                  "hasCategory": false,
                                  "interestDirection": "",
                                  "targetPlatforms": ["天猫"],
                                  "budgetRange": "20-50万",
                                  "profitMin": "15%",
                                  "supplyChain": "",
                                  "stockCycle": "",
                                  "excludeCategories": ["宠物"]
                                }
                                """))
                .andExpect(status().isOk())
                .andReturn();

        long brandId = dataNode(brandResult).get("id").asLong();

        mockMvc.perform(get("/api/opportunity/1/points").param("brandId", String.valueOf(brandId)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    void shouldRejectInvalidActionStatus() throws Exception {
        mockMvc.perform(post("/api/opportunity/{cardId}/actions/{actionTitle}", 1, "确认 7 天小样验证方案")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "无效状态",
                                  "note": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void shouldRejectUnsupportedCompetitorPlatform() throws Exception {
        mockMvc.perform(post("/api/competitor")
                        .param("brandId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "shopName": "非法平台店",
                                  "platform": "拼多多",
                                  "focusCategory": "宠物智能用品"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void shouldRejectUnsupportedPlatformOnBrandCreate() throws Exception {
        mockMvc.perform(post("/api/brand")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "brandName": "非法平台",
                                  "industry": "消费品",
                                  "targetCategory": "",
                                  "hasCategory": false,
                                  "interestDirection": "",
                                  "targetPlatforms": ["拼多多"],
                                  "budgetRange": "20-50万",
                                  "profitMin": "15%",
                                  "supplyChain": "",
                                  "stockCycle": "",
                                  "excludeCategories": []
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void actuatorHealthShouldExposeBusinessReadiness() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.components.selectionData.status").value("UP"))
                .andExpect(jsonPath("$.components.selectionData.details.seedDataReady").value(true));
    }

    @Test
    void actuatorMetricsShouldExposeTrackedExecutionTimer() throws Exception {
        mockMvc.perform(get("/api/opportunity/1").param("brandId", "1"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/actuator/metrics/selection.tracked.execution"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("selection.tracked.execution"))
                .andExpect(jsonPath("$.availableTags[?(@.tag=='label')]").exists())
                .andExpect(jsonPath("$.availableTags[?(@.tag=='domain')]").exists());
    }

    @Test
    void actuatorInfoShouldExposeApplicationMetadata() throws Exception {
        mockMvc.perform(get("/actuator/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.application.name").value("selection-service"))
                .andExpect(jsonPath("$.application.version").value("0.0.1-SNAPSHOT"))
                .andExpect(jsonPath("$.application.releaseDate").value("2026-06-04"))
                .andExpect(jsonPath("$.data.store").value("H2 in-memory"))
                .andExpect(jsonPath("$.data.sampleDataVersion").value("2026-06-04"));
    }

    @Test
    void actuatorMetricsShouldExposeWriteRequestCounter() throws Exception {
        mockMvc.perform(post("/api/brand")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "brandName": "指标测试品牌",
                                  "industry": "宠物用品",
                                  "targetCategory": "宠物智能用品",
                                  "hasCategory": true,
                                  "interestDirection": "",
                                  "targetPlatforms": ["天猫"],
                                  "budgetRange": "20-50万",
                                  "profitMin": "15-25%",
                                  "supplyChain": "华东供应链",
                                  "stockCycle": "30-60天",
                                  "excludeCategories": []
                                }
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(get("/actuator/metrics/selection.write.requests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("selection.write.requests"))
                .andExpect(jsonPath("$.availableTags[?(@.tag=='endpoint')]").exists())
                .andExpect(jsonPath("$.availableTags[?(@.tag=='domain')]").exists())
                .andExpect(jsonPath("$.availableTags[?(@.tag=='outcome')]").exists());
    }

    private JsonNode dataNode(MvcResult result) throws Exception {
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("data");
    }

    private String signalMetricById(MvcResult result, String signalId) throws Exception {
        JsonNode data = dataNode(result);
        for (JsonNode item : data) {
            if (signalId.equals(item.path("id").asText())) {
                return item.path("metric").asText();
            }
        }
        return null;
    }

    private int rankingScoreByCardId(MvcResult result, long cardId) throws Exception {
        for (JsonNode item : dataNode(result).path("items")) {
            if (item.path("cardId").asLong() == cardId) {
                return item.path("opportunityScore").asInt();
            }
        }
        return -1;
    }
}
