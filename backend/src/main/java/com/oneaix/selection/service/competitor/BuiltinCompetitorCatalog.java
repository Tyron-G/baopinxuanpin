package com.oneaix.selection.service.competitor;

import com.oneaix.selection.content.CategoryUniverseCatalog;
import com.oneaix.selection.dto.CompetitorShop;
import com.oneaix.selection.enums.PlatformView;

import java.util.ArrayList;
import java.util.List;

/** 10 品类 × 2～3 家内置竞品（含差评主题与爆款时间信号）2026-06-05 */
public final class BuiltinCompetitorCatalog {

    private static final String[][] PET_COMPLAINTS = {
            {"卡粮", "噪音", "APP 连接不稳"},
            {"分餐不准", "清洗麻烦", "售后慢"},
            {"远程延迟", "耗材贵", "夜视模糊"}
    };
    private static final String[][] COFFEE_COMPLAINTS = {
            {"清洗麻烦", "防漏一般", "保温时长短"},
            {"萃取不稳", "材质异味", "体积偏大"},
            {"露营场景漏液", "磨豆噪音", "配件易丢"}
    };
    private static final String[][] ROBOT_COMPLAINTS = {
            {"避障差", "耗材贵", "基站占地"},
            {"宠物毛发缠绕", "噪音大", "售后慢"},
            {"越障失败", "地图丢失", "污水箱异味"}
    };
    private static final String[][] BEAUTY_COMPLAINTS = {
            {"烫伤风险", "效果不稳定", "续航短"},
            {"凝胶过敏", "操作复杂", "价格偏高"},
            {"售后慢", "配件难买", "充电发热"}
    };
    private static final String[][] SUN_COMPLAINTS = {
            {"闷热不透气", "尺码偏小", "易勾丝"},
            {"防晒指数虚标", "清洗掉色", "帽檐不够大"},
            {"儿童款勒耳朵", "反光条脱落", "收纳不便"}
    };
    private static final String[][] SECURITY_COMPLAINTS = {
            {"误报多", "安装复杂", "云存储贵"},
            {"夜视不清", "电池续航短", "APP 卡顿"},
            {"门磁脱落", "本地存储难", "客服响应慢"}
    };
    private static final String[][] BABY_COMPLAINTS = {
            {"防胀气效果一般", "材质异味", "刻度不清"},
            {"温奶不均", "消毒不彻底", "配件难配"},
            {"辅食棒噪音", "售后慢", "密封圈老化"}
    };
    private static final String[][] FOOD_COMPLAINTS = {
            {"口感偏甜", "保质期短", "包装易破"},
            {"蛋白粉结块", "携带漏粉", "溶解慢"},
            {"代餐棒偏硬", "热量标注争议", "复购贵"}
    };
    private static final String[][] CROSS_COMPLAINTS = {
            {"真空失效", "拉链易坏", "尺寸不准"},
            {"抽屉分隔不稳", "壁挂胶不牢", "异味"},
            {"旅行套装缺件", "压缩回弹慢", "跨境物流慢"}
    };
    private static final String[][] CHARGER_COMPLAINTS = {
            {"发热明显", "协议兼容差", "线材易断"},
            {"磁吸不稳", "多口功率虚标", "体积偏大"},
            {"车载接触不良", "快充断充", "售后慢"}
    };

    private BuiltinCompetitorCatalog() {
    }

    public static List<CompetitorShop> shops() {
        List<CompetitorShop> shops = new ArrayList<>();
        var profiles = CategoryUniverseCatalog.profiles();
        shops.addAll(categoryShops(profiles.get(0).categoryName(), 1L,
                new String[]{"小佩宠物旗舰店", "霍曼宠物科技", "CATLINK 官方店"},
                new PlatformView[]{PlatformView.TMALL, PlatformView.DOUYIN, PlatformView.XIAOHONGSHU},
                new int[]{14, 18, 11}, PET_COMPLAINTS));
        shops.addAll(categoryShops(profiles.get(1).categoryName(), 2L,
                new String[]{"九阳便携厨电", "摩飞厨房电器", "泰摩器具专营"},
                new PlatformView[]{PlatformView.DOUYIN, PlatformView.TMALL, PlatformView.XIAOHONGSHU},
                new int[]{16, 20, 12}, COFFEE_COMPLAINTS));
        shops.addAll(categoryShops(profiles.get(2).categoryName(), 3L,
                new String[]{"石头科技旗舰", "科沃斯官方", "云鲸智能店"},
                new PlatformView[]{PlatformView.TMALL, PlatformView.TMALL, PlatformView.DOUYIN},
                new int[]{22, 26, 19}, ROBOT_COMPLAINTS));
        shops.addAll(categoryShops(profiles.get(3).categoryName(), 4L,
                new String[]{"AMIRO 美容仪", "Ulike 官方", "JOVS 旗舰店"},
                new PlatformView[]{PlatformView.TMALL, PlatformView.DOUYIN, PlatformView.XIAOHONGSHU},
                new int[]{15, 17, 13}, BEAUTY_COMPLAINTS));
        shops.addAll(categoryShops(profiles.get(4).categoryName(), 5L,
                new String[]{"蕉下防晒专营", "迪卡侬户外", "ohsunny 官方"},
                new PlatformView[]{PlatformView.TMALL, PlatformView.TMALL, PlatformView.DOUYIN},
                new int[]{13, 21, 10}, SUN_COMPLAINTS));
        shops.addAll(categoryShops(profiles.get(5).categoryName(), 6L,
                new String[]{"萤石智能安防", "绿米 Aqara", "小米智能家居"},
                new PlatformView[]{PlatformView.TMALL, PlatformView.TMALL, PlatformView.DOUYIN},
                new int[]{18, 16, 14}, SECURITY_COMPLAINTS));
        shops.addAll(categoryShops(profiles.get(6).categoryName(), 7L,
                new String[]{"贝亲母婴旗舰", "hegen 官方", "小白熊电器"},
                new PlatformView[]{PlatformView.TMALL, PlatformView.TMALL, PlatformView.DOUYIN},
                new int[]{24, 20, 15}, BABY_COMPLAINTS));
        shops.addAll(categoryShops(profiles.get(7).categoryName(), 8L,
                new String[]{"ffit8 轻食", "王饱饱官方", "WonderLab 旗舰店"},
                new PlatformView[]{PlatformView.DOUYIN, PlatformView.TMALL, PlatformView.XIAOHONGSHU},
                new int[]{12, 16, 11}, FOOD_COMPLAINTS));
        shops.addAll(categoryShops(profiles.get(8).categoryName(), 9L,
                new String[]{"太力收纳官方", "佳帮手家居", "旅行收纳工坊"},
                new PlatformView[]{PlatformView.TMALL, PlatformView.TMALL, PlatformView.DOUYIN},
                new int[]{19, 14, 10}, CROSS_COMPLAINTS));
        shops.add(shop(9L, profiles.get(8).categoryName(), "Amazon Basics 收纳", PlatformView.AMAZON, 8,
                List.of(CROSS_COMPLAINTS[2]), 4));
        shops.add(shop(9L, profiles.get(8).categoryName(), "Shopee 家居精选", PlatformView.SHOPEE, 6,
                List.of(CROSS_COMPLAINTS[0]), 5));
        shops.addAll(categoryShops(profiles.get(9).categoryName(), 10L,
                new String[]{"Anker 安克创新", "倍思数码", "绿联官方店"},
                new PlatformView[]{PlatformView.TMALL, PlatformView.DOUYIN, PlatformView.TMALL},
                new int[]{17, 13, 15}, CHARGER_COMPLAINTS));
        return List.copyOf(shops);
    }

    private static List<CompetitorShop> categoryShops(
            String category,
            long cardId,
            String[] shopNames,
            PlatformView[] platforms,
            int[] firstHitMonths,
            String[][] complaintSets
    ) {
        List<CompetitorShop> rows = new ArrayList<>();
        for (int i = 0; i < shopNames.length; i++) {
            rows.add(shop(
                    cardId,
                    category,
                    shopNames[i],
                    platforms[i],
                    firstHitMonths[i],
                    List.of(complaintSets[i]),
                    i + 1
            ));
        }
        return rows;
    }

    private static CompetitorShop shop(
            long cardId,
            String category,
            String shopName,
            PlatformView platform,
            int firstHitMonths,
            List<String> complaints,
            int variant
    ) {
        int hitCount = 2 + variant;
        return new CompetitorShop(
                shopName,
                platform.getLabel(),
                category,
                category + " 爆款 SKU 月销 " + (6000 + variant * 900) + "+",
                "首个类目爆款出现约 " + firstHitMonths + " 个月前",
                "2026-06-01 10:00",
                cardId,
                "builtin-" + category.hashCode() + "-" + shopName.hashCode(),
                "搜索飙升",
                "近 14 天持续上架 " + hitCount + " 款相关 SKU",
                hitCount,
                complaints,
                List.of("品类相关", "差评跟踪", "样例口径")
        );
    }
}
