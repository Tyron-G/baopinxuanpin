package com.oneaix.selection.validation;

import com.oneaix.selection.dto.BrandRequest;
import org.springframework.stereotype.Component;

/** 品牌建档业务校验（PRD 条件必填）2026-06-05 */
@Component
public class BrandRequestValidator {

    public void validate(BrandRequest request) {
        if (Boolean.TRUE.equals(request.hasCategory())
                && (request.targetCategory() == null || request.targetCategory().isBlank())) {
            throw new IllegalArgumentException("已选择「有目标品类」时，目标品类为必填项");
        }
        if (request.targetPlatforms() == null || request.targetPlatforms().isEmpty()) {
            throw new IllegalArgumentException("请至少选择一个目标销售平台");
        }
    }
}
