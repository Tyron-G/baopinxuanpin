package com.oneaix.selection.content;

import com.oneaix.selection.dto.ChanmamaFeedSample;
import com.oneaix.selection.dto.DataConnectorStatus;
import com.oneaix.selection.dto.DataConnectorsOverview;
import com.oneaix.selection.dto.FeiguaFeedSample;
import org.springframework.stereotype.Component;

import java.util.List;

/** 蝉妈妈/飞瓜等数据源样例状态 2026-06-04 */
@Component
public class SampleDataConnectorCatalog {

    public DataConnectorsOverview overview() {
        return new DataConnectorsOverview(
                List.of(
                        new DataConnectorStatus("蝉妈妈", "电商直播", "已同步", "2026-06-04 07:00", "抖音/天猫 12 类目", "近 7 日 GMV 增速 TOP 品类已入库", true),
                        new DataConnectorStatus("飞瓜数据", "短视频", "已同步", "2026-06-04 06:45", "抖音 8 垂类", "爆款视频播放增速与话题聚类已更新", true),
                        new DataConnectorStatus("1688", "供应链", "已同步", "2026-06-04 07:15", "家居/宠物/个护", "报价/MOQ/产能样例已关联机会页", true),
                        new DataConnectorStatus("合享专利检索", "合规", "已同步", "2026-06-04 06:30", "结构/外观专利库", "FTO 快检结果已写入机会页", true)
                ),
                List.of(
                        new ChanmamaFeedSample("静音宠物自动喂食器 Pro", "宠物智能用品", "+68%", "直播榜 #3", "7日 GMV 120-180万"),
                        new ChanmamaFeedSample("便携手压浓缩咖啡机", "便携式咖啡器具", "+41%", "直播榜 #11", "7日 GMV 45-72万"),
                        new ChanmamaFeedSample("跨境家居收纳六件套", "跨境家居收纳", "+55%", "跨境榜 #6", "7日 GMV 80-110万")
                ),
                List.of(
                        new FeiguaFeedSample("「猫咪分离焦虑」远程陪伴实测", "宠物智能用品", "+92%", "腰部达人", "#宠物焦虑 #智能喂食"),
                        new FeiguaFeedSample("办公室 3 分钟手压浓缩咖啡", "便携式咖啡器具", "+57%", "尾部达人", "#咖啡自由 #便携咖啡"),
                        new FeiguaFeedSample("亚马逊轻小件收纳爆款拆解", "跨境家居收纳", "+63%", "头部达人", "#跨境家居 #收纳神器")
                )
        );
    }
}
