package com.oneaix.selection.service.opportunity;

import com.oneaix.selection.content.CategoryPlaybook;
import com.oneaix.selection.content.SampleThirdPartyIntelCatalog;
import com.oneaix.selection.dto.Alibaba1688Intel;
import com.oneaix.selection.dto.OpportunityMarketContext;
import com.oneaix.selection.dto.PatentIntel;
import com.oneaix.selection.dto.SellingPointSuggestion;
import com.oneaix.selection.entity.InsightCard;
import org.springframework.stereotype.Component;

import java.util.List;

/** 机会页专利/1688/卖点建议（样例情报）2026-06-04 */
@Component
public class OpportunityIntelBuilder {

    private final SampleThirdPartyIntelCatalog thirdPartyIntelCatalog;

    public OpportunityIntelBuilder(SampleThirdPartyIntelCatalog thirdPartyIntelCatalog) {
        this.thirdPartyIntelCatalog = thirdPartyIntelCatalog;
    }

    public PatentIntel buildPatent(InsightCard card, CategoryPlaybook playbook) {
        return thirdPartyIntelCatalog.patentFor(card);
    }

    public Alibaba1688Intel build1688(InsightCard card, CategoryPlaybook playbook) {
        return thirdPartyIntelCatalog.alibabaFor(card);
    }

    public OpportunityMarketContext buildMarketContext(InsightCard card) {
        String category = card.getCategoryName();
        return new OpportunityMarketContext(
                "低于类目均值约 12%",
                "当前 CPC 约 1.8-2.6 元（蝉妈妈样例），低于类目均值 12%",
                "头程+包材约占售价 18%-22%",
                "体积重量比约 1:4.2，适合轻小件跨境小包",
                category.contains("跨境")
                        ? "亚马逊轻小件计划 + TikTok Shop 类目扶持叠加"
                        : "天猫该类目仍有新品扶持流量包，抖音内容冷启动成本可控",
                "TikTok Shop / Temu 仍存在类目竞争洼地",
                "综合判断：广告与物流成本尚未进入红海，适合小批量验证。"
        );
    }

    public List<SellingPointSuggestion> buildSellingPoints(InsightCard card) {
        return List.of(
                new SellingPointSuggestion("99-149 元", "低噪音 + 卡粮提醒", "用「安静陪伴」替代参数堆叠"),
                new SellingPointSuggestion("149-199 元", "分餐识别 + APP 远控", "面向多宠家庭做套餐组合"),
                new SellingPointSuggestion("199-259 元", "夜视看护 + 耗材订阅", "提高 LTV，降低一次性比价")
        );
    }
}
