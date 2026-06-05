package com.oneaix.selection.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;

@TableName("supply_demand")
public class SupplyDemand {
    private Long id;
    private String categoryName;
    private String platform;
    private String priceRange;
    private Integer searchVolume;
    private Integer supplyCount;
    private BigDecimal demandSupplyRatio;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }
    public String getPriceRange() { return priceRange; }
    public void setPriceRange(String priceRange) { this.priceRange = priceRange; }
    public Integer getSearchVolume() { return searchVolume; }
    public void setSearchVolume(Integer searchVolume) { this.searchVolume = searchVolume; }
    public Integer getSupplyCount() { return supplyCount; }
    public void setSupplyCount(Integer supplyCount) { this.supplyCount = supplyCount; }
    public BigDecimal getDemandSupplyRatio() { return demandSupplyRatio; }
    public void setDemandSupplyRatio(BigDecimal demandSupplyRatio) { this.demandSupplyRatio = demandSupplyRatio; }
}
