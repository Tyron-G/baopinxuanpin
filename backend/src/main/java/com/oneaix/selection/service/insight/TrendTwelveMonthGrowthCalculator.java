package com.oneaix.selection.service.insight;

import com.oneaix.selection.entity.CategoryTrend;
import com.oneaix.selection.util.PlatformMarketFilter;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** 12 月增速：同自然年 1 月 vs 12 月搜索量同比（Jan→Dec）2026-06-05 */
@Component
public class TrendTwelveMonthGrowthCalculator {

    private static final Pattern FIRST_HIT_MONTHS = Pattern.compile("(\\d+)\\s*个月");

    public Optional<BigDecimal> janToDecGrowth(List<CategoryTrend> trends, String categoryName, String platform) {
        List<CategoryTrend> series = PlatformMarketFilter.byPlatform(trends, platform, CategoryTrend::getPlatform).stream()
                .filter(row -> categoryName.equals(row.getCategoryName()))
                .sorted(Comparator.comparing(CategoryTrend::getTrendMonth))
                .toList();
        if (series.isEmpty()) {
            return Optional.empty();
        }
        CategoryTrend jan = findMonth(series, "-01").orElse(series.get(0));
        CategoryTrend dec = findMonth(series, "-12").orElse(series.get(series.size() - 1));
        if (jan.getSearchVolume() <= 0) {
            return Optional.empty();
        }
        double pct = (dec.getSearchVolume() - jan.getSearchVolume()) * 100.0 / jan.getSearchVolume();
        return Optional.of(BigDecimal.valueOf(pct).setScale(1, RoundingMode.HALF_UP));
    }

    public int parseFirstHitMonths(String growthSignal, int fallback) {
        if (growthSignal == null || growthSignal.isBlank()) {
            return fallback;
        }
        Matcher matcher = FIRST_HIT_MONTHS.matcher(growthSignal);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }
        return fallback;
    }

    private Optional<CategoryTrend> findMonth(List<CategoryTrend> series, String monthSuffix) {
        return series.stream()
                .filter(row -> row.getTrendMonth() != null && row.getTrendMonth().endsWith(monthSuffix))
                .max(Comparator.comparing(CategoryTrend::getTrendMonth));
    }
}
