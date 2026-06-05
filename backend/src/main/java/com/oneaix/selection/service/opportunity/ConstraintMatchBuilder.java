package com.oneaix.selection.service.opportunity;

import com.oneaix.selection.dto.ConstraintMatch;
import com.oneaix.selection.dto.InsightCardView;
import com.oneaix.selection.entity.BrandInfo;
import com.oneaix.selection.entity.InsightCard;
import com.oneaix.selection.enums.ConstraintHintTone;
import com.oneaix.selection.service.constraint.BrandConstraintEvaluator;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/** 约束匹配结果组装 2026-06-04 */
@Component
public class ConstraintMatchBuilder {

    private final BrandConstraintEvaluator constraintEvaluator;

    public ConstraintMatchBuilder(BrandConstraintEvaluator constraintEvaluator) {
        this.constraintEvaluator = constraintEvaluator;
    }

    public ConstraintMatch build(BrandInfo brand, InsightCard card, InsightCardView cardView) {
        List<String> matched = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        for (String hint : constraintEvaluator.buildConstraintHints(brand, card)) {
            if (ConstraintHintTone.fromHint(hint) == ConstraintHintTone.WARNING) {
                warnings.add(hint);
            } else {
                matched.add(hint);
            }
        }
        return new ConstraintMatch(
                matched,
                warnings,
                cardView.brandFitDetail(),
                cardView.mismatches()
        );
    }
}
