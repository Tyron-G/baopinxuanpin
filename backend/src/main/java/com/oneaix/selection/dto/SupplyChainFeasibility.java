package com.oneaix.selection.dto;

public record SupplyChainFeasibility(
        String moq,
        String leadTime,
        String factoryCapacity,
        String riskHint,
        String conclusion
) {
}
