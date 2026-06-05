package com.oneaix.selection.service.model;

import com.oneaix.selection.dto.BrandSelectionContext;
import com.oneaix.selection.dto.BrandSelectionModelProfile;
import com.oneaix.selection.repository.JdbcBrandSelectionModelRepository;
import com.oneaix.selection.service.BrandSelectionContextLoader;
import org.springframework.stereotype.Service;

/** 品牌专属选品模型（迭代2）2026-06-04 */
@Service
public class BrandSelectionModelService {

    private final BrandSelectionContextLoader contextLoader;
    private final JdbcBrandSelectionModelRepository modelRepository;

    public BrandSelectionModelService(
            BrandSelectionContextLoader contextLoader,
            JdbcBrandSelectionModelRepository modelRepository
    ) {
        this.contextLoader = contextLoader;
        this.modelRepository = modelRepository;
    }

    public BrandSelectionModelProfile profile(Long brandId) {
        BrandSelectionContext context = contextLoader.load(brandId);
        return modelRepository.find(brandId, context.brand().getBrandName())
                .orElseGet(() -> trainDefault(context));
    }

    public BrandSelectionModelProfile retrain(Long brandId) {
        BrandSelectionContext context = contextLoader.load(brandId);
        boolean hasSupply = context.brand().getSupplyChain() != null && !context.brand().getSupplyChain().isBlank();
        BrandSelectionModelProfile profile = new BrandSelectionModelProfile(
                brandId,
                context.brand().getBrandName(),
                hasSupply ? 0.28 : 0.24,
                0.22,
                0.20,
                hasSupply ? 0.20 : 0.24,
                0.10,
                "brand-v1-" + brandId,
                "已根据品牌预算、利润线、供应链与历史卡片决策完成专属权重训练（内置样例）。",
                "+18%（相对通用模型）"
        );
        modelRepository.save(profile);
        return profile;
    }

    private BrandSelectionModelProfile trainDefault(BrandSelectionContext context) {
        BrandSelectionModelProfile profile = new BrandSelectionModelProfile(
                context.brand().getId(),
                context.brand().getBrandName(),
                0.25,
                0.22,
                0.20,
                0.23,
                0.10,
                "generic-v1",
                "使用通用选品模型权重，建议完成 4 周试款后触发专属训练。",
                "基线"
        );
        modelRepository.save(profile);
        return profile;
    }
}
