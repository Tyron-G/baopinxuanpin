package com.oneaix.selection.enums;

/** 品牌约束不匹配类型 2026-06-03 */
public enum ConstraintType {
    TARGET_CATEGORY("target_category"),
    PLATFORM("platform"),
    BUDGET("budget"),
    COMPETITION("competition"),
    STOCK_CYCLE("stock_cycle"),
    PROFIT("profit"),
    SUPPLY_CHAIN("supply_chain");

    private final String code;

    ConstraintType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
