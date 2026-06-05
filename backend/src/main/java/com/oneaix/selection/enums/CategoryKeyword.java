package com.oneaix.selection.enums;

/** 品类关键词 2026-06-03 */
public enum CategoryKeyword {
    PET("宠物"),
    COFFEE("咖啡"),
    CLEANING("清洁");

    private final String keyword;

    CategoryKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getKeyword() {
        return keyword;
    }

    public boolean matches(String categoryName) {
        return categoryName != null && categoryName.contains(keyword);
    }

    /** 内容驱动起量类目（短视频/种草）2026-06-04 */
    public static boolean isContentDriven(String categoryName) {
        return PET.matches(categoryName) || COFFEE.matches(categoryName);
    }

    /** 搜索/货架驱动类目 2026-06-04 */
    public static boolean isSearchDriven(String categoryName) {
        return CLEANING.matches(categoryName);
    }
}
