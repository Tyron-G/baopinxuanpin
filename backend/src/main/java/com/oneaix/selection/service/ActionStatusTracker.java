package com.oneaix.selection.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.oneaix.selection.annotation.TrackedExecution;
import com.oneaix.selection.entity.ActionStatus;
import com.oneaix.selection.mapper.ActionStatusMapper;
import com.oneaix.selection.dto.ReportAction;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** 2026-06-04 动作状态持久化（H2 action_status 表） */
@Component
public class ActionStatusTracker {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final ActionStatusMapper actionStatusMapper;

    public ActionStatusTracker(ActionStatusMapper actionStatusMapper) {
        this.actionStatusMapper = actionStatusMapper;
    }

    public ReportAction merge(Long cardId, ReportAction action) {
        ActionStatus state = actionStatusMapper.selectOne(new LambdaQueryWrapper<ActionStatus>()
                .eq(ActionStatus::getInsightCardId, cardId)
                .eq(ActionStatus::getActionTitle, action.title())
                .last("LIMIT 1"));
        if (state == null) {
            return action;
        }
        return new ReportAction(
                action.title(),
                action.ownerRole(),
                action.expectedGoal(),
                action.priority(),
                action.eta(),
                state.getStatus(),
                state.getUpdatedAt(),
                state.getNote() == null || state.getNote().isBlank() ? action.note() : state.getNote()
        );
    }

    @Transactional
    @TrackedExecution(value = "action-status-update", domain = "opportunity")
    public void update(Long cardId, String actionTitle, String status, String note) {
        ActionStatus candidate = buildStatus(cardId, actionTitle, status, note);
        int updated = actionStatusMapper.update(candidate, Wrappers.<ActionStatus>lambdaUpdate()
                .eq(ActionStatus::getInsightCardId, cardId)
                .eq(ActionStatus::getActionTitle, actionTitle));
        if (updated > 0) {
            return;
        }
        try {
            actionStatusMapper.insert(candidate);
        } catch (DuplicateKeyException ex) {
            actionStatusMapper.update(candidate, Wrappers.<ActionStatus>lambdaUpdate()
                    .eq(ActionStatus::getInsightCardId, cardId)
                    .eq(ActionStatus::getActionTitle, actionTitle));
        }
    }

    private ActionStatus buildStatus(Long cardId, String actionTitle, String status, String note) {
        ActionStatus state = new ActionStatus();
        state.setInsightCardId(cardId);
        state.setActionTitle(actionTitle);
        state.setStatus(status);
        state.setNote(note == null ? "" : note);
        state.setUpdatedAt(LocalDateTime.now().format(FORMATTER));
        return state;
    }
}
