package com.jcr.api.statistics;

/**
 * Spring test MVC Layer.
 *
 * @author jean-charles.robert
 * @since 10.06.18
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcr.api.statistics.controller.StatisticsController;
import com.jcr.api.statistics.model.Statistics;
import com.jcr.api.statistics.model.Transaction;
import com.jcr.api.statistics.service.StatisticsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebMvcTest(StatisticsController.class)
public class StatisticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StatisticsService statisticsService;

    @Test
    public void okTransactionRequest201() throws Exception {
        Transaction validTransaction = new Transaction(12.5, Instant.now().minusSeconds(5).toEpochMilli());
        mockMvc.perform(post("/transaction")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(toJson(validTransaction)))
                //.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string(""));
    }

    @Test
    public void notOkTransactionRequest204() throws Exception {
        Transaction expiredTransaction = new Transaction(12.5, 1478192204000L);
        mockMvc.perform(post("/transaction")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(toJson(expiredTransaction)))
                //.andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }


    @Test
    public void testEmptyStastisticsRequest() throws Exception {
        when(statisticsService.getStatistics()).thenReturn(Statistics.empty());
        mockMvc.perform(get("/statistics")
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().string("{\"sum\":0.0,\"avg\":0.0,\"max\":0.0,\"min\":0.0,\"count\":0}"))
                .andExpect(content().string(toJson(Statistics.empty())));
    }

    @Test
    public void testStastisticsRequest() throws Exception {
        Statistics stats = new Statistics(1245.8, 75.0, 15.90, 250.0, 12L, null);
        when(statisticsService.getStatistics()).thenReturn(stats);
        mockMvc.perform(get("/statistics")
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().string("{\"sum\":1245.8,\"avg\":75.0,\"max\":15.9,\"min\":250.0,\"count\":12}"))
                .andExpect(content().string(toJson(stats)));
    }

    private static String toJson(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
