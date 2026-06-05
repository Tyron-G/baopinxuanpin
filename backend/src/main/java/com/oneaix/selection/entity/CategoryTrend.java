package com.oneaix.selection.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;

@TableName("category_trend")
public class CategoryTrend {
    private Long id;
    private String categoryName;
    private String platform;
    private String trendMonth;
    private Integer searchVolume;
    private Integer salesVolume;
    private BigDecimal growthRate;
    private Integer socialHeat;
    private String risingWords;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }
    public String getTrendMonth() { return trendMonth; }
    public void setTrendMonth(String trendMonth) { this.trendMonth = trendMonth; }
    public Integer getSearchVolume() { return searchVolume; }
    public void setSearchVolume(Integer searchVolume) { this.searchVolume = searchVolume; }
    public Integer getSalesVolume() { return salesVolume; }
    public void setSalesVolume(Integer salesVolume) { this.salesVolume = salesVolume; }
    public BigDecimal getGrowthRate() { return growthRate; }
    public void setGrowthRate(BigDecimal growthRate) { this.growthRate = growthRate; }
    public Integer getSocialHeat() { return socialHeat; }
    public void setSocialHeat(Integer socialHeat) { this.socialHeat = socialHeat; }
    public String getRisingWords() { return risingWords; }
    public void setRisingWords(String risingWords) { this.risingWords = risingWords; }
}
