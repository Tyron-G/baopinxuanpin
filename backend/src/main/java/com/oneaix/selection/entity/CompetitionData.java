package com.oneaix.selection.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;

@TableName("competition_data")
public class CompetitionData {
    private Long id;
    private String categoryName;
    private String platform;
    private Integer totalSearchVolume;
    private Integer totalSkuCount;
    private BigDecimal top10SalesRatio;
    private BigDecimal cr3;
    private BigDecimal cr5;
    private BigDecimal homogeneityScore;
    private String conclusion;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }
    public Integer getTotalSearchVolume() { return totalSearchVolume; }
    public void setTotalSearchVolume(Integer totalSearchVolume) { this.totalSearchVolume = totalSearchVolume; }
    public Integer getTotalSkuCount() { return totalSkuCount; }
    public void setTotalSkuCount(Integer totalSkuCount) { this.totalSkuCount = totalSkuCount; }
    public BigDecimal getTop10SalesRatio() { return top10SalesRatio; }
    public void setTop10SalesRatio(BigDecimal top10SalesRatio) { this.top10SalesRatio = top10SalesRatio; }
    public BigDecimal getCr3() { return cr3; }
    public void setCr3(BigDecimal cr3) { this.cr3 = cr3; }
    public BigDecimal getCr5() { return cr5; }
    public void setCr5(BigDecimal cr5) { this.cr5 = cr5; }
    public BigDecimal getHomogeneityScore() { return homogeneityScore; }
    public void setHomogeneityScore(BigDecimal homogeneityScore) { this.homogeneityScore = homogeneityScore; }
    public String getConclusion() { return conclusion; }
    public void setConclusion(String conclusion) { this.conclusion = conclusion; }
}
