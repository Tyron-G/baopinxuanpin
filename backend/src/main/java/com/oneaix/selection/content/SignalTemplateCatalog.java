package com.oneaix.selection.content;

import com.oneaix.selection.entity.BrandInfo;
import com.oneaix.selection.enums.PlatformView;
import com.oneaix.selection.enums.SignalStrength;
import com.oneaix.selection.enums.SignalType;
import org.springframework.stereotype.Component;

import java.util.List;

/** 内置信号模板库 2026-06-04 */
@Component
public class SignalTemplateCatalog {

    private static final String DEFAULT_PLATFORMS = PlatformView.TMALL.getLabel() + "," + PlatformView.DOUYIN.getLabel();

    public List<SignalTemplate> templatesFor(BrandInfo brand) {
        String platforms = brand.getTargetPlatforms() == null || brand.getTargetPlatforms().isBlank()
                ? DEFAULT_PLATFORMS
                : brand.getTargetPlatforms();
        return allTemplates().stream()
                .filter(template -> template.matchesBrandPlatforms(platforms))
                .toList();
    }

    private static List<SignalTemplate> allTemplates() {
        return List.of(
                new SignalTemplate(
                        "sig-search-001",
                        "宠物智能用品",
                        SignalType.SEARCH_SURGE,
                        SignalStrength.STRONG,
                        94,
                        88,
                        PlatformView.TMALL,
                        "月搜索 +41.3%",
                        "自动喂食、远程看护相关词搜索连续 6 个月加速。",
                        "今日 08:30",
                        "优先进入洞察分析",
                        List.of("趋势加速", "搜索异常", "目标平台相关"),
                        SignalTemplate.PlatformGate.ALWAYS
                ),
                new SignalTemplate(
                        "sig-social-002",
                        "宠物智能用品",
                        SignalType.SOCIAL_ANOMALY,
                        SignalStrength.STRONG,
                        89,
                        84,
                        PlatformView.DOUYIN,
                        "话题播放 +68%",
                        "「独自在家宠物焦虑」话题互动显著高于类目均值。",
                        "今日 09:10",
                        "进入机会验证",
                        List.of("内容声量", "场景痛点", "人群关注"),
                        SignalTemplate.PlatformGate.REQUIRES_DOUYIN
                ),
                new SignalTemplate(
                        "sig-social-003",
                        "便携式咖啡器具",
                        SignalType.CONTENT_SEEDING,
                        SignalStrength.MEDIUM,
                        72,
                        68,
                        PlatformView.XIAOHONGSHU,
                        "笔记互动 +22%",
                        "露营咖啡、办公室咖啡场景内容稳定增长。",
                        "今日 07:55",
                        "继续观察场景热度",
                        List.of("内容种草", "场景细分"),
                        SignalTemplate.PlatformGate.REQUIRES_XIAOHONGSHU
                ),
                new SignalTemplate(
                        "sig-pain-004",
                        "宠物智能用品",
                        SignalType.PAIN_POINT,
                        SignalStrength.STRONG,
                        86,
                        82,
                        PlatformView.TMALL,
                        "卡粮提及 92 次",
                        "Top20 竞品中卡粮/噪音差评未充分解决，存在差异化窗口。",
                        "今日 08:05",
                        "优先验证差异化方案",
                        List.of("差评聚集", "功能缺口", "可优化"),
                        SignalTemplate.PlatformGate.ALWAYS
                ),
                new SignalTemplate(
                        "sig-rank-005",
                        "家用清洁机器人",
                        SignalType.RANK_CHANGE,
                        SignalStrength.WEAK,
                        58,
                        61,
                        PlatformView.TMALL,
                        "新品上榜周期延长",
                        "头部品牌占榜稳定，新玩家进入难度高。",
                        "昨日 22:40",
                        "降低优先级",
                        List.of("头部锁定", "进入壁垒"),
                        SignalTemplate.PlatformGate.ALWAYS
                )
        );
    }
}

