package com.oneaix.selection.util;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/** 列表与管道分隔字符串互转（H2 字段存储）2026-06-04 */
public final class ListCsvCodec {

    private static final String DELIMITER = "|";

    private ListCsvCodec() {
    }

    public static String encode(List<String> values) {
        if (values == null || values.isEmpty()) {
            return "";
        }
        return values.stream()
                .filter(value -> value != null && !value.isBlank())
                .collect(Collectors.joining(DELIMITER));
    }

    public static List<String> decode(String raw) {
        if (raw == null || raw.isBlank()) {
            return List.of();
        }
        return Arrays.stream(raw.split("\\|"))
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .toList();
    }
}
