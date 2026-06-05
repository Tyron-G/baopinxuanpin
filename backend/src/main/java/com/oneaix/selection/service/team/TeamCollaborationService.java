package com.oneaix.selection.service.team;

import com.oneaix.selection.dto.TeamAssignmentItem;
import com.oneaix.selection.dto.TeamMemberItem;
import com.oneaix.selection.repository.JdbcTeamRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/** 团队协作：成员、权限、分派与审批 2026-06-05 */
@Service
public class TeamCollaborationService {

    private final JdbcTeamRepository teamRepository;

    public TeamCollaborationService(JdbcTeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    public List<TeamMemberItem> members(Long brandId) {
        teamRepository.ensureSeedMembers(brandId);
        return teamRepository.listMembers(brandId);
    }

    public TeamMemberItem addMember(
            Long brandId,
            String memberName,
            String roleLabel,
            String permissionLevel,
            String email,
            String accountId
    ) {
        return teamRepository.addMember(brandId, memberName, roleLabel, permissionLevel, email, accountId);
    }

    public List<TeamAssignmentItem> assignments(Long brandId) {
        return teamRepository.listAssignments(brandId);
    }

    public TeamAssignmentItem assign(
            Long brandId,
            Long cardId,
            String actionTitle,
            String assigneeName,
            String status,
            String note
    ) {
        return teamRepository.addAssignment(brandId, cardId, actionTitle, assigneeName, status, note);
    }

    public TeamAssignmentItem approve(Long brandId, Long assignmentId, String approverName) {
        return teamRepository.approveAssignment(brandId, assignmentId, approverName)
                .orElseThrow(() -> new IllegalArgumentException("分派任务不存在: " + assignmentId));
    }

    public TeamAssignmentItem reject(Long brandId, Long assignmentId, String approverName, String note) {
        return teamRepository.rejectAssignment(brandId, assignmentId, approverName, note)
                .orElseThrow(() -> new IllegalArgumentException("分派任务不存在: " + assignmentId));
    }
}
