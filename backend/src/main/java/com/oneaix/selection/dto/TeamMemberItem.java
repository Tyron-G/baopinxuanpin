package com.oneaix.selection.dto;

/** 团队成员 2026-06-05 */
public record TeamMemberItem(
        Long id,
        String memberName,
        String roleLabel,
        String permissionLevel,
        String accountId,
        String email
) {
}
