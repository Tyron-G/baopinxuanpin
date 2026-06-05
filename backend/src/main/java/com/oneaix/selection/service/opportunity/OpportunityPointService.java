package com.oneaix.selection.service.opportunity;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.oneaix.selection.entity.Opportunity;
import com.oneaix.selection.enums.PlatformView;
import com.oneaix.selection.mapper.OpportunityMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/** 机会点查询与平台视角排序 2026-06-04 */
@Service
public class OpportunityPointService {

    private final OpportunityMapper opportunityMapper;

    public OpportunityPointService(OpportunityMapper opportunityMapper) {
        this.opportunityMapper = opportunityMapper;
    }

    public List<Opportunity> list(Long cardId, String platformView) {
        PlatformView platform = PlatformView.normalize(platformView);
        return opportunityMapper.selectList(new LambdaQueryWrapper<Opportunity>()
                        .eq(Opportunity::getInsightCardId, cardId)
                        .orderByDesc(Opportunity::getOpportunityScore))
                .stream()
                .sorted((left, right) -> Integer.compare(
                        platformAdjustedScore(right, platform),
                        platformAdjustedScore(left, platform)))
                .toList();
    }

    private int platformAdjustedScore(Opportunity point, PlatformView platform) {
        return point.getOpportunityScore() + platform.opportunityScoreBoost(
                point.getScenarioText(),
                point.getDifferentiation(),
                point.getLifecycleStage()
        );
    }
}
