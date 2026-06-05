package com.oneaix.selection.enums;

/** 工作流阶段状态 2026-06-03 */
public enum WorkflowStageStatus {
    DONE("done"),
    CURRENT("current"),
    PENDING("pending");

    private final String code;

    WorkflowStageStatus(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
