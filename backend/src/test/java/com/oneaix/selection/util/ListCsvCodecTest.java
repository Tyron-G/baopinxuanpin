package com.oneaix.selection.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** 2026-06-04 列表编解码 */
class ListCsvCodecTest {

    @Test
    void shouldRoundTripListValues() {
        List<String> source = List.of("卡粮", "噪音", "APP 连接不稳");
        String encoded = ListCsvCodec.encode(source);
        assertEquals(source, ListCsvCodec.decode(encoded));
    }
}
