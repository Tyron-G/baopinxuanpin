package com.oneaix.selection.entity;

import com.baomidou.mybatisplus.annotation.TableName;

@TableName("insight_card")
public class InsightCard {
    private Long id;
    private Long brandId;
    private String categoryName;
    private String marketSize;
    private String marketGrowth;
    private String competitionPattern;
    private String competitionLevel;
    private String priceGap;
    private String estimatedStartupCost;
    private String recommendation;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getBrandId() { return brandId; }
    public void setBrandId(Long brandId) { this.brandId = brandId; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public String getMarketSize() { return marketSize; }
    public void setMarketSize(String marketSize) { this.marketSize = marketSize; }
    public String getMarketGrowth() { return marketGrowth; }
    public void setMarketGrowth(String marketGrowth) { this.marketGrowth = marketGrowth; }
    public String getCompetitionPattern() { return competitionPattern; }
    public void setCompetitionPattern(String competitionPattern) { this.competitionPattern = competitionPattern; }
    public String getCompetitionLevel() { return competitionLevel; }
    public void setCompetitionLevel(String competitionLevel) { this.competitionLevel = competitionLevel; }
    public String getPriceGap() { return priceGap; }
    public void setPriceGap(String priceGap) { this.priceGap = priceGap; }
    public String getEstimatedStartupCost() { return estimatedStartupCost; }
    public void setEstimatedStartupCost(String estimatedStartupCost) { this.estimatedStartupCost = estimatedStartupCost; }
    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
}
