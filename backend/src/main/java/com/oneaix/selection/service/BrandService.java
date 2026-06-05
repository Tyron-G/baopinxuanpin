package com.oneaix.selection.service;

import com.oneaix.selection.dto.BrandRequest;
import com.oneaix.selection.entity.BrandInfo;
import com.oneaix.selection.mapper.BrandInfoMapper;
import com.oneaix.selection.exception.ResourceNotFoundException;
import com.oneaix.selection.service.brand.BrandProfileNormalizer;
import com.oneaix.selection.validation.BrandRequestValidator;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BrandService {
    private final BrandInfoMapper brandInfoMapper;
    private final BrandProfileNormalizer profileNormalizer;
    private final BrandRequestValidator brandRequestValidator;

    public BrandService(
            BrandInfoMapper brandInfoMapper,
            BrandProfileNormalizer profileNormalizer,
            BrandRequestValidator brandRequestValidator
    ) {
        this.brandInfoMapper = brandInfoMapper;
        this.profileNormalizer = profileNormalizer;
        this.brandRequestValidator = brandRequestValidator;
    }

    public BrandInfo create(BrandRequest request) {
        brandRequestValidator.validate(request);
        BrandProfileNormalizer.NormalizedBrandProfile normalized = profileNormalizer.normalize(request);
        BrandInfo brand = new BrandInfo();
        brand.setBrandName(normalized.brandName());
        brand.setIndustry(normalized.industry());
        brand.setTargetCategory(normalized.targetCategory());
        brand.setHasCategory(normalized.hasCategory());
        brand.setInterestDirection(normalized.interestDirection());
        brand.setTargetPlatforms(normalized.targetPlatforms());
        brand.setBudgetRange(normalized.budgetRange());
        brand.setProfitMin(normalized.profitMin());
        brand.setSupplyChain(normalized.supplyChain());
        brand.setStockCycle(normalized.stockCycle());
        brand.setExcludeCategories(normalized.excludeCategories());
        brand.setExistingProducts(normalized.existingProducts());
        brandInfoMapper.insert(brand);
        return brand;
    }

    public Optional<BrandInfo> findById(Long id) {
        return Optional.ofNullable(brandInfoMapper.selectById(id));
    }

    /** 按 brandId 严格查询，不存在则 404 2026-06-04 */
    public BrandInfo requireById(Long brandId) {
        return findById(brandId).orElseThrow(() -> ResourceNotFoundException.brand(brandId));
    }

}
