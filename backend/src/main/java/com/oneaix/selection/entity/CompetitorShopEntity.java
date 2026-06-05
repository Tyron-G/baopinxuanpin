package com.oneaix.selection.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/** 竞品店铺持久化实体 2026-06-04 */
@TableName("competitor_shop")
public class CompetitorShopEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long brandId;
    private String shopName;
    private String platform;
    private String focusCategory;
    private String latestHit;
    private String growthSignal;
    private String addedAt;
    private Long cardId;
    private String sourceSignalId;
    private String sourceSignalType;
    private String recentLaunch;
    private Integer hitProductCount;
    private String complaintTopics;
    private String opportunityTags;
    private Boolean builtinSeed;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getBrandId() { return brandId; }
    public void setBrandId(Long brandId) { this.brandId = brandId; }
    public String getShopName() { return shopName; }
    public void setShopName(String shopName) { this.shopName = shopName; }
    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }
    public String getFocusCategory() { return focusCategory; }
    public void setFocusCategory(String focusCategory) { this.focusCategory = focusCategory; }
    public String getLatestHit() { return latestHit; }
    public void setLatestHit(String latestHit) { this.latestHit = latestHit; }
    public String getGrowthSignal() { return growthSignal; }
    public void setGrowthSignal(String growthSignal) { this.growthSignal = growthSignal; }
    public String getAddedAt() { return addedAt; }
    public void setAddedAt(String addedAt) { this.addedAt = addedAt; }
    public Long getCardId() { return cardId; }
    public void setCardId(Long cardId) { this.cardId = cardId; }
    public String getSourceSignalId() { return sourceSignalId; }
    public void setSourceSignalId(String sourceSignalId) { this.sourceSignalId = sourceSignalId; }
    public String getSourceSignalType() { return sourceSignalType; }
    public void setSourceSignalType(String sourceSignalType) { this.sourceSignalType = sourceSignalType; }
    public String getRecentLaunch() { return recentLaunch; }
    public void setRecentLaunch(String recentLaunch) { this.recentLaunch = recentLaunch; }
    public Integer getHitProductCount() { return hitProductCount; }
    public void setHitProductCount(Integer hitProductCount) { this.hitProductCount = hitProductCount; }
    public String getComplaintTopics() { return complaintTopics; }
    public void setComplaintTopics(String complaintTopics) { this.complaintTopics = complaintTopics; }
    public String getOpportunityTags() { return opportunityTags; }
    public void setOpportunityTags(String opportunityTags) { this.opportunityTags = opportunityTags; }
    public Boolean getBuiltinSeed() { return builtinSeed; }
    public void setBuiltinSeed(Boolean builtinSeed) { this.builtinSeed = builtinSeed; }
}
