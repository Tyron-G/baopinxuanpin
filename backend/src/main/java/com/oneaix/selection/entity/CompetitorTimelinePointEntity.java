package com.oneaix.selection.entity;

import com.baomidou.mybatisplus.annotation.TableName;

/** 竞品时间轴节点（H2）2026-06-04 */
@TableName("competitor_timeline_point")
public class CompetitorTimelinePointEntity {
    private Long id;
    private String shopName;
    private String platform;
    private String focusCategory;
    private Integer weekOrder;
    private String weekLabel;
    private Integer heatIndex;
    private Integer salesIndex;
    private String note;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getShopName() { return shopName; }
    public void setShopName(String shopName) { this.shopName = shopName; }
    public String getPlatform() { return platform; }
    public void setPlatform(String platform) { this.platform = platform; }
    public String getFocusCategory() { return focusCategory; }
    public void setFocusCategory(String focusCategory) { this.focusCategory = focusCategory; }
    public Integer getWeekOrder() { return weekOrder; }
    public void setWeekOrder(Integer weekOrder) { this.weekOrder = weekOrder; }
    public String getWeekLabel() { return weekLabel; }
    public void setWeekLabel(String weekLabel) { this.weekLabel = weekLabel; }
    public Integer getHeatIndex() { return heatIndex; }
    public void setHeatIndex(Integer heatIndex) { this.heatIndex = heatIndex; }
    public Integer getSalesIndex() { return salesIndex; }
    public void setSalesIndex(Integer salesIndex) { this.salesIndex = salesIndex; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
