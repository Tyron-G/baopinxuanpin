package com.oneaix.selection.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;

@TableName("opportunity")
public class Opportunity {
    private Long id;
    private Long insightCardId;
    private String categoryName;
    private BigDecimal opportunityGravity;
    private BigDecimal competitionResistance;
    private BigDecimal profitElasticity;
    private Integer opportunityScore;
    private String opportunityLevel;
    private String targetCrowd;
    private String scenarioText;
    private String differentiation;
    private String marketEstimate;
    private String entryTiming;
    private String lifecycleStage;
    private String decision;
    private String reason;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getInsightCardId() { return insightCardId; }
    public void setInsightCardId(Long insightCardId) { this.insightCardId = insightCardId; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public BigDecimal getOpportunityGravity() { return opportunityGravity; }
    public void setOpportunityGravity(BigDecimal opportunityGravity) { this.opportunityGravity = opportunityGravity; }
    public BigDecimal getCompetitionResistance() { return competitionResistance; }
    public void setCompetitionResistance(BigDecimal competitionResistance) { this.competitionResistance = competitionResistance; }
    public BigDecimal getProfitElasticity() { return profitElasticity; }
    public void setProfitElasticity(BigDecimal profitElasticity) { this.profitElasticity = profitElasticity; }
    public Integer getOpportunityScore() { return opportunityScore; }
    public void setOpportunityScore(Integer opportunityScore) { this.opportunityScore = opportunityScore; }
    public String getOpportunityLevel() { return opportunityLevel; }
    public void setOpportunityLevel(String opportunityLevel) { this.opportunityLevel = opportunityLevel; }
    public String getTargetCrowd() { return targetCrowd; }
    public void setTargetCrowd(String targetCrowd) { this.targetCrowd = targetCrowd; }
    public String getScenarioText() { return scenarioText; }
    public void setScenarioText(String scenarioText) { this.scenarioText = scenarioText; }
    public String getDifferentiation() { return differentiation; }
    public void setDifferentiation(String differentiation) { this.differentiation = differentiation; }
    public String getMarketEstimate() { return marketEstimate; }
    public void setMarketEstimate(String marketEstimate) { this.marketEstimate = marketEstimate; }
    public String getEntryTiming() { return entryTiming; }
    public void setEntryTiming(String entryTiming) { this.entryTiming = entryTiming; }
    public String getLifecycleStage() { return lifecycleStage; }
    public void setLifecycleStage(String lifecycleStage) { this.lifecycleStage = lifecycleStage; }
    public String getDecision() { return decision; }
    public void setDecision(String decision) { this.decision = decision; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
