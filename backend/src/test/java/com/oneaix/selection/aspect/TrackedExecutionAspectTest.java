package com.oneaix.selection.aspect;

import com.oneaix.selection.service.InsightService;
import org.junit.jupiter.api.Test;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

/** 2026-06-04 AOP 执行跟踪切面装配测试 */
@SpringBootTest
class TrackedExecutionAspectTest {

    @Autowired
    private InsightService insightService;

    @Test
    void insightServiceShouldBeProxiedForTrackedExecution() {
        assertTrue(AopUtils.isAopProxy(insightService));
    }
}
