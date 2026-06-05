package com.oneaix.selection.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("brand_info")
public class BrandInfo {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String brandName;
    private String industry;
    private String targetCategory;
    private Boolean hasCategory;
    private String interestDirection;
    private String targetPlatforms;
    private String budgetRange;
    private String profitMin;
    private String supplyChain;
    private String stockCycle;
    private String excludeCategories;
    private String existingProducts;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getBrandName() { return brandName; }
    public void setBrandName(String brandName) { this.brandName = brandName; }
    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }
    public String getTargetCategory() { return targetCategory; }
    public void setTargetCategory(String targetCategory) { this.targetCategory = targetCategory; }
    public Boolean getHasCategory() { return hasCategory; }
    public void setHasCategory(Boolean hasCategory) { this.hasCategory = hasCategory; }
    public String getInterestDirection() { return interestDirection; }
    public void setInterestDirection(String interestDirection) { this.interestDirection = interestDirection; }
    public String getTargetPlatforms() { return targetPlatforms; }
    public void setTargetPlatforms(String targetPlatforms) { this.targetPlatforms = targetPlatforms; }
    public String getBudgetRange() { return budgetRange; }
    public void setBudgetRange(String budgetRange) { this.budgetRange = budgetRange; }
    public String getProfitMin() { return profitMin; }
    public void setProfitMin(String profitMin) { this.profitMin = profitMin; }
    public String getSupplyChain() { return supplyChain; }
    public void setSupplyChain(String supplyChain) { this.supplyChain = supplyChain; }
    public String getStockCycle() { return stockCycle; }
    public void setStockCycle(String stockCycle) { this.stockCycle = stockCycle; }
    public String getExcludeCategories() { return excludeCategories; }
    public void setExcludeCategories(String excludeCategories) { this.excludeCategories = excludeCategories; }
    public String getExistingProducts() { return existingProducts; }
    public void setExistingProducts(String existingProducts) { this.existingProducts = existingProducts; }
}
