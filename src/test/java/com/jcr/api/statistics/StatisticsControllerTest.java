package com.jcr.api.statistics;

import com.jcr.api.statistics.controller.StatisticsController;
import com.jcr.api.statistics.model.Statistics;
import org.junit.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Spring test MVC Layer {@link StatisticsController}.
 *
 * @author jean-charles.robert
 * @since 10.06.18
 */
@WebMvcTest(StatisticsController.class)
public class StatisticsControllerTest extends MvcBaseTest {

    @Test
    public void testEmptyStastisticsRequest() throws Exception {
        when(statisticsService.getStatistics()).thenReturn(Statistics.empty());
        mockMvc.perform(get("/statistics")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(content().string("{\"sum\":0.0,\"avg\":0.0,\"max\":0.0,\"min\":0.0,\"count\":0}"))
                .andExpect(content().string(toJson(Statistics.empty())));
    }

    @Test
    public void testStastisticsRequest() throws Exception {
        Statistics stats = new Statistics(1245.8, 75.0, 15.90, 250.0, 12L, null);
        when(statisticsService.getStatistics()).thenReturn(stats);
        mockMvc.perform(get("/statistics")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(content().string("{\"sum\":1245.8,\"avg\":75.0,\"max\":15.9,\"min\":250.0,\"count\":12}"))
                .andExpect(content().string(toJson(stats)));
    }

}
