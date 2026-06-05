package com.oneaix.selection.service.report;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** 报告 Markdown 模板加载与占位符替换 2026-06-04 */
@Component
public class ReportMarkdownTemplateLoader {

    private static final String TEMPLATE_ROOT = "templates/report/";

    private final Map<String, String> cache = new ConcurrentHashMap<>();

    public String render(String templatePath, Map<String, String> variables) {
        String template = load(templatePath);
        String rendered = template;
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            rendered = rendered.replace("{{" + entry.getKey() + "}}", entry.getValue() == null ? "" : entry.getValue());
        }
        return rendered;
    }

    public String load(String templatePath) {
        return cache.computeIfAbsent(templatePath, this::readTemplate);
    }

    private String readTemplate(String templatePath) {
        try {
            ClassPathResource resource = new ClassPathResource(TEMPLATE_ROOT + templatePath);
            try (InputStreamReader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
                return FileCopyUtils.copyToString(reader);
            }
        } catch (IOException ex) {
            throw new IllegalStateException("无法加载报告模板：" + templatePath, ex);
        }
    }
}
