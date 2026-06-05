package com.oneaix.selection.content;

import com.oneaix.selection.dto.CompetitionReport;
import com.oneaix.selection.dto.CrowdScene;
import com.oneaix.selection.dto.ProfitAnalysis;
import com.oneaix.selection.dto.SentimentTerm;
import com.oneaix.selection.dto.SupplyChainFeasibility;
import com.oneaix.selection.entity.BrandInfo;
import com.oneaix.selection.entity.InsightCard;
import com.oneaix.selection.enums.PlatformView;
import com.oneaix.selection.enums.SentimentPolarity;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * 类目深度内容：机会页叙事、利润与供应链分析素材，与 cardId 绑定。
 * 2026-06-04
 */
public final class CategoryPlaybook {
    private final long cardId;
    private final List<String> differentiationAdvice;
    private final String marketType;
    private final String cr5;
    private final String entryWindow;
    private final String defaultCompetitionNote;
    private final Map<PlatformView, String> competitionPlatformNotes;
    private final ProfitTemplate profitTemplate;
    private final SupplyChainTemplate supplyChainTemplate;
    private final List<SentimentTerm> sentimentTerms;
    private final List<CrowdScene> crowdScenes;

    private CategoryPlaybook(
            long cardId,
            List<String> differentiationAdvice,
            String marketType,
            String cr5,
            String entryWindow,
            String defaultCompetitionNote,
            Map<PlatformView, String> competitionPlatformNotes,
            ProfitTemplate profitTemplate,
            SupplyChainTemplate supplyChainTemplate,
            List<SentimentTerm> sentimentTerms,
            List<CrowdScene> crowdScenes
    ) {
        this.cardId = cardId;
        this.differentiationAdvice = List.copyOf(differentiationAdvice);
        this.marketType = marketType;
        this.cr5 = cr5;
        this.entryWindow = entryWindow;
        this.defaultCompetitionNote = defaultCompetitionNote;
        this.competitionPlatformNotes = Map.copyOf(competitionPlatformNotes);
        this.profitTemplate = profitTemplate;
        this.supplyChainTemplate = supplyChainTemplate;
        this.sentimentTerms = List.copyOf(sentimentTerms);
        this.crowdScenes = List.copyOf(crowdScenes);
    }

    public long cardId() {
        return cardId;
    }

    public List<String> differentiationAdvice() {
        return differentiationAdvice;
    }

    public List<SentimentTerm> sentimentTerms() {
        return sentimentTerms;
    }

    public List<CrowdScene> crowdScenes() {
        return crowdScenes;
    }

    public CompetitionReport buildCompetitionReport(InsightCard card, PlatformView platform) {
        String summary = card.getCompetitionPattern() + "，" + resolveCompetitionNote(card, platform);
        return new CompetitionReport(marketType, cr5, entryWindow, summary);
    }

    public ProfitAnalysis buildProfitAnalysis(PlatformView platform) {
        String summary = profitTemplate.platformSummaries().getOrDefault(platform, profitTemplate.defaultSummary());
        return new ProfitAnalysis(
                profitTemplate.targetPrice(),
                profitTemplate.unitCost(),
                profitTemplate.platformFee(),
                profitTemplate.adCost(),
                profitTemplate.netMargin(),
                summary
        );
    }

    public SupplyChainFeasibility buildSupplyChainFeasibility(BrandInfo brand, String supplyChainAbbreviation) {
        String leadTime = brand.getStockCycle() == null || brand.getStockCycle().isBlank()
                ? supplyChainTemplate.defaultLeadTime()
                : brand.getStockCycle();
        String conclusion = supplyChainTemplate.conclusionTemplate().replace("{supplyChain}", supplyChainAbbreviation);
        return new SupplyChainFeasibility(
                supplyChainTemplate.moq(),
                leadTime,
                supplyChainTemplate.factoryCapacity(),
                supplyChainTemplate.riskHint(),
                conclusion
        );
    }

    private String resolveCompetitionNote(InsightCard card, PlatformView platform) {
        if (platform.isAll() || !competitionPlatformNotes.containsKey(platform)) {
            if ("{recommendation}".equals(defaultCompetitionNote)) {
                return card.getRecommendation();
            }
            return defaultCompetitionNote;
        }
        return competitionPlatformNotes.get(platform);
    }

    static CategoryPlaybook petSmart() {
        Map<PlatformView, String> platformNotes = new EnumMap<>(PlatformView.class);
        platformNotes.put(PlatformView.DOUYIN, "抖音短视频内容红利仍在，但需要更快验证前 3 秒转化卖点。");
        platformNotes.put(PlatformView.TMALL, "货架竞争可控，重点比拼参数稳定性和评价沉淀。");
        platformNotes.put(PlatformView.XIAOHONGSHU, "种草内容分散，适合先从养宠陪伴场景切入。");

        Map<PlatformView, String> profitNotes = new EnumMap<>(PlatformView.class);
        profitNotes.put(PlatformView.DOUYIN, "抖音视角下内容转化效率更关键，若前 3 秒卖点成立，129-199 元仍有较好起量空间。");
        profitNotes.put(PlatformView.TMALL, "天猫视角下 129-199 元价格带更依赖评价沉淀和参数说服，利润空间相对更稳。");
        profitNotes.put(PlatformView.XIAOHONGSHU, "小红书视角下可先验证看护与陪伴场景种草，再决定是否承接到高转化平台。");

        return new CategoryPlaybook(
                1L,
                List.of(
                        "优先围绕“卡粮、分餐不准、远程提醒延迟”做稳定性差异化，把核心卖点收敛到“少故障、能看护、好清洗”。",
                        "如果主平台包含抖音，先用“独自在家宠物焦虑”场景做内容切入，再把夜视和异常提醒作为高转化卖点。"
                ),
                "浅蓝海 / 分散竞争",
                "CR5 28%",
                "6-12 个月",
                "{recommendation}",
                platformNotes,
                new ProfitTemplate(
                        "129-199 元", "52-68 元", "8%-12%", "18%-22%", "18%-24%",
                        "目标价格带与 100-150 元供需缺口一致，广告 CPC 仍低于类目均值，具备试产验证空间。",
                        profitNotes
                ),
                new SupplyChainTemplate(
                        "300-800 件", "30-45 天",
                        "东莞/宁波小家电代工，月产能 1-3 万件",
                        "需关注电机噪音与 APP 联调周期",
                        "核心模组可外采，适合结合现有「{supplyChain}」做小批量试产。"
                ),
                List.of(
                        term("卡粮", 92, SentimentPolarity.NEGATIVE),
                        term("噪音", 76, SentimentPolarity.NEGATIVE),
                        term("远程看护", 88, SentimentPolarity.POSITIVE),
                        term("APP稳定", 64, SentimentPolarity.NEGATIVE),
                        term("多宠分餐", 58, SentimentPolarity.POSITIVE),
                        term("夜视", 52, SentimentPolarity.POSITIVE),
                        term("分离焦虑", 48, SentimentPolarity.NEGATIVE)
                ),
                List.of(
                        new CrowdScene("养猫上班族", "白天独自在家", "担心喂食异常和宠物焦虑", "稳定喂食、远程陪伴、异常提醒"),
                        new CrowdScene("多宠家庭", "多只宠物共同进食", "抢食、分量不准、清洁麻烦", "自动识别、精准分餐、易拆洗")
                )
        );
    }

    static CategoryPlaybook portableCoffee() {
        Map<PlatformView, String> platformNotes = new EnumMap<>(PlatformView.class);
        platformNotes.put(PlatformView.DOUYIN, "抖音更适合用露营和办公室冲泡场景拉开内容差异。");
        platformNotes.put(PlatformView.TMALL, "建议先把防漏、清洗和收纳结构讲透再做投放。");
        platformNotes.put(PlatformView.XIAOHONGSHU, "礼物感和生活方式表达更容易获得自然种草反馈。");

        Map<PlatformView, String> profitNotes = new EnumMap<>(PlatformView.class);
        profitNotes.put(PlatformView.DOUYIN, "抖音视角下场景种草可抬高点击率，但便携器具仍需严格控制退货和材质质感风险。");
        profitNotes.put(PlatformView.TMALL, "天猫视角下该价格带适合做礼盒化和参数对比，但转化更依赖评价与客服承接。");
        profitNotes.put(PlatformView.XIAOHONGSHU, "小红书视角下礼物感、露营氛围和颜值表达更容易支撑毛利，但成交闭环通常更长。");

        return new CategoryPlaybook(
                2L,
                List.of(
                        "不要正面卷基础手压功能，优先做“露营 / 办公”双场景的轻量收纳和防漏体验。",
                        "把竞品高频差评里的“清洗麻烦、防漏一般”转成显性结构优化，价格带仍控制在 99-169 元区间内。"
                ),
                "腰部竞争 / 场景可细分",
                "CR5 39%",
                "3-6 个月验证窗",
                "建议先做场景测款。",
                platformNotes,
                new ProfitTemplate(
                        "99-169 元", "38-55 元", "8%-10%", "15%-18%", "14%-20%",
                        "礼品化与露营场景可支撑更高毛利，但需控制退货与售后成本。",
                        profitNotes
                ),
                new SupplyChainTemplate(
                        "500-1200 件", "25-40 天",
                        "义乌/永康五金供应链，支持轻定制",
                        "结构件公模较多，差异化主要在体验设计",
                        "MOQ 门槛中等，可先走众筹/众测验证后再扩产。"
                ),
                List.of(
                        term("便携", 82, SentimentPolarity.POSITIVE),
                        term("清洗麻烦", 69, SentimentPolarity.NEGATIVE),
                        term("露营", 74, SentimentPolarity.POSITIVE),
                        term("防漏", 62, SentimentPolarity.NEGATIVE),
                        term("控温", 44, SentimentPolarity.POSITIVE),
                        term("礼物感", 40, SentimentPolarity.POSITIVE)
                ),
                List.of(
                        new CrowdScene("露营爱好者", "户外无电环境", "器具体积大、清洗复杂", "轻量收纳、快速出品"),
                        new CrowdScene("差旅白领", "酒店和办公室", "外卖咖啡贵且不稳定", "低门槛、可复用、卫生")
                )
        );
    }

    static CategoryPlaybook cleaningRobot() {
        Map<PlatformView, String> platformNotes = new EnumMap<>(PlatformView.class);
        platformNotes.put(PlatformView.DOUYIN, "抖音获客成本和内容竞争双高，新品牌冷启动压力更大。");
        platformNotes.put(PlatformView.TMALL, "天猫货架头部锁定明显，评价与服务壁垒很难短期补齐。");
        platformNotes.put(PlatformView.XIAOHONGSHU, "小红书可做种草，但很难反推高客单重决策转化。");

        Map<PlatformView, String> profitNotes = new EnumMap<>(PlatformView.class);
        profitNotes.put(PlatformView.DOUYIN, "抖音视角下高客单清洁机器人需要极高素材与投放效率，新品牌很难跑出健康 ROI。");
        profitNotes.put(PlatformView.TMALL, "天猫视角下高客单类目仍有搜索需求，但头部评价壁垒和售后成本会持续压缩利润。");
        profitNotes.put(PlatformView.XIAOHONGSHU, "小红书视角下可以做心智种草，但高客单转化链路长，首年回本压力依旧偏大。");

        return new CategoryPlaybook(
                3L,
                List.of("当前赛道头部锁定较强，更适合寻找售后透明、耗材成本可控的小切口，而不是直接卷整机性能。"),
                "深红海 / 头部锁定",
                "CR5 63%",
                "不建议新品牌冷启动",
                "{recommendation}",
                platformNotes,
                new ProfitTemplate(
                        "1299-2499 元", "620-980 元", "5%-8%", "22%-28%", "6%-10%",
                        "高客单但广告与售后成本高，新品牌难以在首年形成有效 ROI。",
                        profitNotes
                ),
                new SupplyChainTemplate(
                        "2000 件以上", "60 天以上",
                        "头部代工厂产能紧张，新品牌排期困难",
                        "售后与耗材体系复杂",
                        "供应链与售后体系门槛高，不符合轻资产切入策略。"
                ),
                List.of(
                        term("避障", 80, SentimentPolarity.NEGATIVE),
                        term("自清洁", 77, SentimentPolarity.POSITIVE),
                        term("耗材贵", 70, SentimentPolarity.NEGATIVE),
                        term("宠物毛发", 64, SentimentPolarity.NEGATIVE),
                        term("基站占地", 55, SentimentPolarity.NEGATIVE)
                ),
                List.of(
                        new CrowdScene("大户型家庭", "日常深度清洁", "清洁死角和维护成本高", "低维护、强避障"),
                        new CrowdScene("养宠家庭", "毛发频繁堆积", "滚刷缠绕、耗材贵", "防缠绕、耗材透明")
                )
        );
    }

    private static SentimentTerm term(String word, int weight, SentimentPolarity polarity) {
        return new SentimentTerm(word, weight, polarity.getCode());
    }

    private record ProfitTemplate(
            String targetPrice,
            String unitCost,
            String platformFee,
            String adCost,
            String netMargin,
            String defaultSummary,
            Map<PlatformView, String> platformSummaries
    ) {
    }

    private record SupplyChainTemplate(
            String moq,
            String defaultLeadTime,
            String factoryCapacity,
            String riskHint,
            String conclusionTemplate
    ) {
    }
}

