package com.oneaix.selection.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/** 2026-06-04 动作状态持久化实体 */
@TableName("action_status")
public class ActionStatus {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long insightCardId;
    private String actionTitle;
    private String status;
    private String note;
    private String updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getInsightCardId() {
        return insightCardId;
    }

    public void setInsightCardId(Long insightCardId) {
        this.insightCardId = insightCardId;
    }

    public String getActionTitle() {
        return actionTitle;
    }

    public void setActionTitle(String actionTitle) {
        this.actionTitle = actionTitle;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
