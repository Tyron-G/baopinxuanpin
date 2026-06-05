package com.oneaix.selection.service.report;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

/** 2026-06-04 报告模板加载 */
class ReportMarkdownTemplateLoaderTest {

    @Test
    void shouldReplacePlaceholders() {
        ReportMarkdownTemplateLoader loader = new ReportMarkdownTemplateLoader();
        String rendered = loader.render("header.md", Map.of(
                "title", "测试报告",
                "generatedAt", "2026-06-04 10:00",
                "brandName", "样例品牌",
                "industry", "消费品",
                "platform", "全平台"
        ));
        assertTrue(rendered.contains("# 测试报告"));
        assertTrue(rendered.contains("样例品牌"));
        assertTrue(!rendered.contains("{{"));
    }
}
