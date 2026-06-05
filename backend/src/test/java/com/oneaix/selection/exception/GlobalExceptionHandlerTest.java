package com.oneaix.selection.exception;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** 2026-06-04 全局异常处理测试 */
@SpringBootTest
@AutoConfigureMockMvc
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnNotFoundForMissingCard() throws Exception {
        mockMvc.perform(get("/api/opportunity/99999").param("brandId", "1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("洞察卡片不存在：99999"));
    }

    @Test
    void shouldReturnBadRequestForInvalidBrandId() throws Exception {
        mockMvc.perform(get("/api/insight/cards").param("brandId", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    void shouldExposeErrorMetricsAfterHandledException() throws Exception {
        mockMvc.perform(get("/api/opportunity/99999").param("brandId", "1"))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/actuator/metrics/selection.error.responses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("selection.error.responses"))
                .andExpect(jsonPath("$.availableTags[?(@.tag=='domain')]").exists())
                .andExpect(jsonPath("$.availableTags[?(@.tag=='error_code')]").exists())
                .andExpect(jsonPath("$.availableTags[?(@.tag=='exception')]").exists())
                .andExpect(jsonPath("$.availableTags[?(@.tag=='status')]").exists());
    }
}
