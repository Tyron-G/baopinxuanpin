package com.oneaix.selection.enums;

/** 人群场景关键词 2026-06-04 */
public enum ScenarioKeyword {
    CAMPING("露营"),
    HOME_ALONE("独自在家"),
    OFFICE("办公室"),
    CARE("看护"),
    GIFT("礼物"),
    MULTI_PET("多宠");

    private final String keyword;

    ScenarioKeyword(String keyword) {
        this.keyword = keyword;
    }

    public static int matchScore(String scenario, String crowd) {
        String text = (scenario == null ? "" : scenario) + " " + (crowd == null ? "" : crowd);
        int score = 0;
        for (ScenarioKeyword item : values()) {
            if (text.contains(item.keyword)) {
                score++;
            }
        }
        return score;
    }
}
