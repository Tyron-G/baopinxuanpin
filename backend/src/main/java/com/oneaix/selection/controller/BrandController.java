package com.oneaix.selection.controller;

import com.oneaix.selection.dto.BrandRequest;
import com.oneaix.selection.entity.BrandInfo;
import com.oneaix.selection.exception.ResourceNotFoundException;
import com.oneaix.selection.service.BrandService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/brand")
@Tag(name = "品牌上下文")
public class BrandController {
    private final BrandService brandService;

    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @PostMapping
    public BrandInfo create(@Valid @RequestBody BrandRequest request) {
        return brandService.create(request);
    }

    @GetMapping("/{id}")
    public BrandInfo findById(@PathVariable @Min(1) Long id) {
        return brandService.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.brand(id));
    }
}
