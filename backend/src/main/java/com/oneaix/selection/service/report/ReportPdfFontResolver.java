package com.oneaix.selection.service.report;

import com.lowagie.text.Font;
import com.lowagie.text.pdf.BaseFont;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * PDF 中文字体解析（优先 classpath / 系统字体，避免 Helvetica 中文乱码）2026-06-05
 */
@Component
public class ReportPdfFontResolver {

    private static final String[] CLASSPATH_FONTS = {
            "fonts/NotoSansSC-Regular.otf",
            "fonts/NotoSansSC-Regular.ttf",
            "fonts/SourceHanSansSC-Regular.otf"
    };

    private volatile BaseFont cachedBaseFont;

    public BaseFont baseFont() {
        if (cachedBaseFont != null) {
            return cachedBaseFont;
        }
        synchronized (this) {
            if (cachedBaseFont != null) {
                return cachedBaseFont;
            }
            cachedBaseFont = resolveBaseFont();
            return cachedBaseFont;
        }
    }

    public Font font(float size, int style) {
        return new Font(baseFont(), size, style);
    }

    private BaseFont resolveBaseFont() {
        List<String> candidates = new ArrayList<>();
        String configured = System.getProperty("selection.report.pdf.font");
        if (configured == null || configured.isBlank()) {
            configured = System.getenv("REPORT_PDF_FONT");
        }
        if (configured != null && !configured.isBlank()) {
            candidates.add(configured.trim());
        }
        for (String classpathFont : CLASSPATH_FONTS) {
            try {
                ClassPathResource resource = new ClassPathResource(classpathFont);
                if (resource.exists()) {
                    return BaseFont.createFont(
                            resource.getURL().toString(),
                            BaseFont.IDENTITY_H,
                            BaseFont.EMBEDDED
                    );
                }
            } catch (IOException ignored) {
                // try next
            }
        }
        candidates.addAll(List.of(
                "C:/Windows/Fonts/msyh.ttc,0",
                "C:/Windows/Fonts/msyhbd.ttc,0",
                "C:/Windows/Fonts/simsun.ttc,0",
                "C:/Windows/Fonts/simhei.ttf",
                "/usr/share/fonts/truetype/noto/NotoSansCJK-Regular.ttc,0",
                "/usr/share/fonts/opentype/noto/NotoSansCJK-Regular.ttc,0",
                "/usr/share/fonts/truetype/wqy/wqy-microhei.ttc,0",
                "/System/Library/Fonts/PingFang.ttc,0",
                "/System/Library/Fonts/STHeiti Light.ttc,0"
        ));
        for (String candidate : candidates) {
            try {
                if (!fontPathExists(candidate)) {
                    continue;
                }
                return BaseFont.createFont(candidate, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            } catch (Exception ignored) {
                // try next
            }
        }
        try {
            return BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        } catch (Exception ex) {
            throw new IllegalStateException(
                    "未找到可用中文字体，请配置 selection.report.pdf.font 或 REPORT_PDF_FONT 环境变量",
                    ex
            );
        }
    }

    private boolean fontPathExists(String fontPath) {
        String rawPath = fontPath.contains(",") ? fontPath.substring(0, fontPath.indexOf(',')) : fontPath;
        return Files.exists(Path.of(rawPath));
    }
}
