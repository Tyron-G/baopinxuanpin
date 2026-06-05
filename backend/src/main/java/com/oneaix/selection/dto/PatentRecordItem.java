package com.oneaix.selection.dto;

/** 专利检索样例条目 2026-06-04 */
public record PatentRecordItem(
        String applicationNo,
        String title,
        String status,
        String applicant,
        String filingDate
) {
}
