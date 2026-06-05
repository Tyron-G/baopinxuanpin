package com.oneaix.selection.service;

import com.oneaix.selection.dto.BrandRequest;
import com.oneaix.selection.entity.BrandInfo;
import com.oneaix.selection.exception.ResourceNotFoundException;
import com.oneaix.selection.mapper.BrandInfoMapper;
import com.oneaix.selection.service.brand.BrandProfileNormalizer;
import com.oneaix.selection.validation.BrandRequestValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** 2026-06-04 BrandService 单元测试 */
@ExtendWith(MockitoExtension.class)
class BrandServiceTest {
    @Mock
    private BrandInfoMapper brandInfoMapper;

    @Mock
    private BrandProfileNormalizer profileNormalizer;

    @Mock
    private BrandRequestValidator brandRequestValidator;

    @InjectMocks
    private BrandService brandService;

    @Test
    void shouldReturnBrandWhenExists() {
        BrandInfo brand = new BrandInfo();
        brand.setId(2L);
        brand.setBrandName("测试品牌");
        when(brandInfoMapper.selectById(2L)).thenReturn(brand);

        BrandInfo result = brandService.requireById(2L);
        assertEquals(2L, result.getId());
    }

    @Test
    void shouldThrowWhenBrandMissing() {
        when(brandInfoMapper.selectById(99L)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> brandService.requireById(99L));
    }

    @Test
    void shouldNormalizePlatformsAndExcludeCategoriesOnCreate() {
        when(profileNormalizer.normalize(any())).thenReturn(new BrandProfileNormalizer.NormalizedBrandProfile(
                "品牌A",
                "消费品",
                "宠物智能用品",
                true,
                "",
                "天猫,抖音",
                "20-50万",
                "15%",
                "",
                "30-60天",
                "清洁,食品",
                "智能喂食器"
        ));

        BrandRequest request = new BrandRequest(
                "品牌A",
                "消费品",
                "宠物智能用品",
                true,
                "",
                List.of("天猫", "天猫", "抖音"),
                "20-50万",
                "15%",
                "",
                "30-60天",
                List.of("清洁", "清洁", "食品"),
                "智能喂食器"
        );

        brandService.create(request);

        ArgumentCaptor<BrandInfo> captor = ArgumentCaptor.forClass(BrandInfo.class);
        verify(brandInfoMapper).insert(captor.capture());
        assertEquals("天猫,抖音", captor.getValue().getTargetPlatforms());
        assertEquals("清洁,食品", captor.getValue().getExcludeCategories());
    }
}
