package com.oneaix.selection.enums;

/** 产品工作流阶段 2026-06-03 */
public enum WorkflowStageKey {
    DATA_PREP("data-prep", "数据准备", "品牌约束、平台、预算和供应链边界已录入。", "生成品牌上下文"),
    RADAR("radar", "信号雷达", "系统聚焦今日最值得跟进的搜索、社媒和差评信号。", "确认高优先级信号"),
    INSIGHT("insight", "洞察发现", "系统从趋势、竞争和供需三个维度筛出可进入赛道。", "选择优先深挖的赛道"),
    RANKING("ranking", "机会榜单", "查看 TOP50 机会榜单", "锁定优先验证的候选"),
    OPPORTUNITY("opportunity", "爆品机会", "系统输出机会点、风险、利润和供应链可行性结论。", "确认立项、观望或放弃"),
    REPORT("report", "选品报告", "系统已具备沉淀报告所需信息，可对外输出本轮结论。", "导出报告并沉淀判断");

    private final String key;
    private final String title;
    private final String summary;
    private final String nextAction;

    WorkflowStageKey(String key, String title, String summary, String nextAction) {
        this.key = key;
        this.title = title;
        this.summary = summary;
        this.nextAction = nextAction;
    }

    public String getKey() {
        return key;
    }

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }

    public String getNextAction() {
        return nextAction;
    }

    public static WorkflowStageKey fromKey(String key) {
        for (WorkflowStageKey stage : values()) {
            if (stage.key.equals(key)) {
                return stage;
            }
        }
        return DATA_PREP;
    }

    public WorkflowStageKey nextStage() {
        WorkflowStageKey[] stages = values();
        int index = ordinal();
        return index >= stages.length - 1 ? REPORT : stages[index + 1];
    }

    public String statusComparedTo(WorkflowStageKey current) {
        if (ordinal() < current.ordinal()) {
            return WorkflowStageStatus.DONE.getCode();
        }
        if (ordinal() == current.ordinal()) {
            return WorkflowStageStatus.CURRENT.getCode();
        }
        return WorkflowStageStatus.PENDING.getCode();
    }
}
