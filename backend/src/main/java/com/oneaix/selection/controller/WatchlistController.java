package com.oneaix.selection.controller;

import com.oneaix.selection.constant.ApiConstants;
import com.oneaix.selection.dto.WatchlistItem;
import com.oneaix.selection.service.WatchlistService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** 关注列表（MVP P1 基础看板）2026-06-04 */
@Validated
@RestController
@RequestMapping("/api/watchlist")
@Tag(name = "关注列表")
public class WatchlistController {

    private final WatchlistService watchlistService;

    public WatchlistController(WatchlistService watchlistService) {
        this.watchlistService = watchlistService;
    }

    @GetMapping
    public List<WatchlistItem> list(@RequestParam(defaultValue = ApiConstants.DEFAULT_BRAND_ID_PARAM) @Min(1) Long brandId) {
        return watchlistService.list(brandId);
    }

    @PostMapping
    public WatchlistItem add(
            @RequestParam(defaultValue = ApiConstants.DEFAULT_BRAND_ID_PARAM) @Min(1) Long brandId,
            @RequestParam @NotBlank String categoryName,
            @RequestParam(required = false) Long cardId,
            @RequestParam(required = false) String note
    ) {
        return watchlistService.add(brandId, cardId, categoryName, note);
    }

    @DeleteMapping("/{id}")
    public void remove(
            @RequestParam(defaultValue = ApiConstants.DEFAULT_BRAND_ID_PARAM) @Min(1) Long brandId,
            @PathVariable @Min(1) Long id
    ) {
        watchlistService.remove(brandId, id);
    }
}
