package com.oneaix.selection.controller;

import com.oneaix.selection.constant.ApiConstants;
import com.oneaix.selection.dto.TeamAssignmentItem;
import com.oneaix.selection.dto.TeamMemberItem;
import com.oneaix.selection.service.team.TeamCollaborationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** 团队协作 P2 2026-06-05 */
@Validated
@RestController
@RequestMapping("/api/team")
@Tag(name = "团队协作")
public class TeamCollaborationController {

    private final TeamCollaborationService teamCollaborationService;

    public TeamCollaborationController(TeamCollaborationService teamCollaborationService) {
        this.teamCollaborationService = teamCollaborationService;
    }

    @GetMapping("/members")
    public List<TeamMemberItem> members(
            @RequestParam(defaultValue = ApiConstants.DEFAULT_BRAND_ID_PARAM) @Min(1) Long brandId
    ) {
        return teamCollaborationService.members(brandId);
    }

    @PostMapping("/members")
    public TeamMemberItem addMember(
            @RequestParam(defaultValue = ApiConstants.DEFAULT_BRAND_ID_PARAM) @Min(1) Long brandId,
            @RequestParam @NotBlank String memberName,
            @RequestParam @NotBlank String roleLabel,
            @RequestParam(defaultValue = "editor") String permissionLevel,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String accountId
    ) {
        return teamCollaborationService.addMember(brandId, memberName, roleLabel, permissionLevel, email, accountId);
    }

    @GetMapping("/assignments")
    public List<TeamAssignmentItem> assignments(
            @RequestParam(defaultValue = ApiConstants.DEFAULT_BRAND_ID_PARAM) @Min(1) Long brandId
    ) {
        return teamCollaborationService.assignments(brandId);
    }

    @PostMapping("/assignments")
    public TeamAssignmentItem assign(
            @RequestParam(defaultValue = ApiConstants.DEFAULT_BRAND_ID_PARAM) @Min(1) Long brandId,
            @RequestParam(required = false) Long cardId,
            @RequestParam @NotBlank String actionTitle,
            @RequestParam @NotBlank String assigneeName,
            @RequestParam(defaultValue = "待处理") String status,
            @RequestParam(required = false) String note
    ) {
        return teamCollaborationService.assign(brandId, cardId, actionTitle, assigneeName, status, note);
    }

    @PostMapping("/assignments/approve")
    public TeamAssignmentItem approve(
            @RequestParam(defaultValue = ApiConstants.DEFAULT_BRAND_ID_PARAM) @Min(1) Long brandId,
            @RequestParam @Min(1) Long assignmentId,
            @RequestParam @NotBlank String approverName
    ) {
        return teamCollaborationService.approve(brandId, assignmentId, approverName);
    }

    @PostMapping("/assignments/reject")
    public TeamAssignmentItem reject(
            @RequestParam(defaultValue = ApiConstants.DEFAULT_BRAND_ID_PARAM) @Min(1) Long brandId,
            @RequestParam @Min(1) Long assignmentId,
            @RequestParam @NotBlank String approverName,
            @RequestParam(required = false) String note
    ) {
        return teamCollaborationService.reject(brandId, assignmentId, approverName, note);
    }
}
