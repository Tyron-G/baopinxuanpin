package com.oneaix.selection.content;

import com.oneaix.selection.dto.Alibaba1688Intel;
import com.oneaix.selection.dto.AlibabaOfferItem;
import com.oneaix.selection.dto.PatentIntel;
import com.oneaix.selection.dto.PatentRecordItem;
import com.oneaix.selection.entity.InsightCard;
import org.springframework.stereotype.Component;

import java.util.List;

/** 蝉妈妈/飞瓜/1688/专利样例情报 2026-06-04 */
@Component
public class SampleThirdPartyIntelCatalog {

    public PatentIntel patentFor(InsightCard card) {
        String category = card.getCategoryName();
        return new PatentIntel(
                category.contains("跨境") ? "中" : "低到中",
                category + " 已完成 FTO 快检：头部品牌外观专利集中，建议公模 + 软件交互差异化立项。",
                List.of(
                        "近 3 年同类结构专利 126 件，喂食器/摄像头云台占比 62%",
                        "无有效发明专利阻挡「低噪音+分餐识别」组合卖点",
                        "含电池模块需额外关注 GB/T 与 EMC 合规"
                ),
                "合享智慧芽检索（样例）",
                "2026-06-04 06:30",
                category + " AND (智能 OR 自动) AND (喂食 OR 收纳 OR 咖啡)",
                List.of(
                        new PatentRecordItem("CN202310882341.2", "一种宠物自动喂食器的出粮机构", "实质审查", "深圳某智能科技", "2023-08-12"),
                        new PatentRecordItem("CN202211556901.8", "带摄像头的宠物看护装置", "已授权", "杭州某物联股份", "2022-11-03"),
                        new PatentRecordItem("CN202410223156.0", "便携式手压咖啡机活塞结构", "公开", "宁波某精工制造", "2024-03-21")
                )
        );
    }

    public Alibaba1688Intel alibabaFor(InsightCard card) {
        String category = card.getCategoryName();
        return new Alibaba1688Intel(
                "出厂价 45-85 元",
                "MOQ 300-800 件",
                "月产能 2-5 万台（东莞/宁波产业带）",
                category + " 核心工艺可外协，建议先打样 50 件验证良率、噪音与售后返修率。",
                List.of("东莞市某智能宠物科技厂", "宁波市某小家电 OEM", "惠州市某结构件配套厂"),
                "1688 开放平台（样例）",
                "2026-06-04 07:15",
                category,
                List.of(
                        new AlibabaOfferItem("1688-demo-001", category + " 智能喂食器 OEM", "¥52/件", "500 件", "东莞某智能宠物科技厂", "广东东莞", "AAA"),
                        new AlibabaOfferItem("1688-demo-002", category + " 静音电机模组", "¥18/件", "1000 件", "宁波某电机厂", "浙江宁波", "AA"),
                        new AlibabaOfferItem("1688-demo-003", category + " 包装彩盒+说明书", "¥6.5/套", "2000 套", "惠州某包装厂", "广东惠州", "A+")
                )
        );
    }
}
