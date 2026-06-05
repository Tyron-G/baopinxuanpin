package com.oneaix.selection.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/** 2026-06-04 */
@SpringBootTest
@Transactional
class JdbcCompetitorTimelinePointRepositoryTest {

    @Autowired
    private CompetitorTimelinePointRepository repository;

    @Test
    void shouldLoadSeededTimelinePoints() {
        var points = repository.findByShop("小佩宠物旗舰店", "天猫", "宠物智能用品");
        assertFalse(points.isEmpty());
        assertEquals("第1周", points.get(0).period());
        assertEquals(4, points.size());
    }
}
