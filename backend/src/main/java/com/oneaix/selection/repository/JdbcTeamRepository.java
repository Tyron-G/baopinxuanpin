package com.oneaix.selection.repository;

import com.oneaix.selection.dto.TeamAssignmentItem;
import com.oneaix.selection.dto.TeamMemberItem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

/** 团队协作 JDBC 仓储 2026-06-05 */
@Repository
public class JdbcTeamRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcTeamRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void ensureSeedMembers(Long brandId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM team_member WHERE brand_id = ?",
                Integer.class,
                brandId
        );
        if (count != null && count > 0) {
            return;
        }
        addMember(brandId, "林运营", "电商运营", "editor", "ops@demo.local", "account-ops");
        addMember(brandId, "周买手", "选品买手", "editor", "buyer@demo.local", "account-buyer");
        addMember(brandId, "陈负责人", "品牌负责人", "admin", "owner@demo.local", "account-owner");
    }

    public List<TeamMemberItem> listMembers(Long brandId) {
        return jdbcTemplate.query(
                """
                        SELECT id, member_name, role_label, permission_level, account_id, email
                        FROM team_member WHERE brand_id = ? ORDER BY id
                        """,
                (rs, rowNum) -> new TeamMemberItem(
                        rs.getLong("id"),
                        rs.getString("member_name"),
                        rs.getString("role_label"),
                        rs.getString("permission_level"),
                        rs.getString("account_id"),
                        rs.getString("email")
                ),
                brandId
        );
    }

    public TeamMemberItem addMember(
            Long brandId,
            String memberName,
            String roleLabel,
            String permissionLevel,
            String email,
            String accountId
    ) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    """
                            INSERT INTO team_member
                            (brand_id, member_name, role_label, permission_level, account_id, email)
                            VALUES (?, ?, ?, ?, ?, ?)
                            """,
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setLong(1, brandId);
            ps.setString(2, memberName);
            ps.setString(3, roleLabel);
            ps.setString(4, permissionLevel == null || permissionLevel.isBlank() ? "editor" : permissionLevel);
            ps.setString(5, accountId);
            ps.setString(6, email);
            return ps;
        }, keyHolder);
        Number key = keyHolder.getKey();
        return new TeamMemberItem(
                key == null ? null : key.longValue(),
                memberName,
                roleLabel,
                permissionLevel,
                accountId,
                email
        );
    }

    public List<TeamAssignmentItem> listAssignments(Long brandId) {
        return jdbcTemplate.query(
                """
                        SELECT id, card_id, action_title, assignee_name, status, approval_status, approver_name, note
                        FROM team_assignment WHERE brand_id = ? ORDER BY id DESC
                        """,
                (rs, rowNum) -> new TeamAssignmentItem(
                        rs.getLong("id"),
                        rs.getObject("card_id") == null ? null : rs.getLong("card_id"),
                        rs.getString("action_title"),
                        rs.getString("assignee_name"),
                        rs.getString("status"),
                        rs.getString("approval_status"),
                        rs.getString("approver_name"),
                        rs.getString("note")
                ),
                brandId
        );
    }

    public TeamAssignmentItem addAssignment(
            Long brandId,
            Long cardId,
            String actionTitle,
            String assigneeName,
            String status,
            String note
    ) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    """
                            INSERT INTO team_assignment
                            (brand_id, card_id, action_title, assignee_name, status, approval_status, note)
                            VALUES (?, ?, ?, ?, ?, 'pending', ?)
                            """,
                    Statement.RETURN_GENERATED_KEYS
            );
            ps.setLong(1, brandId);
            if (cardId == null) {
                ps.setObject(2, null);
            } else {
                ps.setLong(2, cardId);
            }
            ps.setString(3, actionTitle);
            ps.setString(4, assigneeName);
            ps.setString(5, status);
            ps.setString(6, note);
            return ps;
        }, keyHolder);
        Number key = keyHolder.getKey();
        return new TeamAssignmentItem(
                key == null ? null : key.longValue(),
                cardId,
                actionTitle,
                assigneeName,
                status,
                "pending",
                null,
                note
        );
    }

    public Optional<TeamAssignmentItem> approveAssignment(Long brandId, Long assignmentId, String approverName) {
        int updated = jdbcTemplate.update(
                """
                        UPDATE team_assignment
                        SET approval_status = 'approved', approver_name = ?, status = '已审批'
                        WHERE brand_id = ? AND id = ?
                        """,
                approverName,
                brandId,
                assignmentId
        );
        if (updated <= 0) {
            return Optional.empty();
        }
        return listAssignments(brandId).stream()
                .filter(item -> assignmentId.equals(item.id()))
                .findFirst();
    }

    public Optional<TeamAssignmentItem> rejectAssignment(Long brandId, Long assignmentId, String approverName, String note) {
        int updated = jdbcTemplate.update(
                """
                        UPDATE team_assignment
                        SET approval_status = 'rejected', approver_name = ?, status = '已驳回', note = ?
                        WHERE brand_id = ? AND id = ?
                        """,
                approverName,
                note,
                brandId,
                assignmentId
        );
        if (updated <= 0) {
            return Optional.empty();
        }
        return listAssignments(brandId).stream()
                .filter(item -> assignmentId.equals(item.id()))
                .findFirst();
    }
}
