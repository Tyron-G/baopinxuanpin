package com.oneaix.selection.service.team;

import com.oneaix.selection.dto.TeamAssignmentItem;
import com.oneaix.selection.dto.TeamMemberItem;
import com.oneaix.selection.repository.JdbcTeamRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/** 团队协作：成员与选品任务分派 2026-06-05 */
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

    public TeamMemberItem addMember(Long brandId, String memberName, String roleLabel, String email) {
        return teamRepository.addMember(brandId, memberName, roleLabel, email);
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
}
