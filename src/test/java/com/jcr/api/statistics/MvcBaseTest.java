package com.jcr.api.statistics;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcr.api.statistics.service.StatisticsService;
import com.jcr.api.statistics.service.TransactionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.Assert.assertNotNull;

/**
 * Parent class of @WebMvcTest test classes.
 *
 * @author jean-charles.robert
 * @since 11.06.18
 */
@WebMvcTest
@RunWith(SpringRunner.class)
public class MvcBaseTest {

    @Autowired
    protected MockMvc mockMvc;

    @MockBean
    protected StatisticsService statisticsService;

    @MockBean
    protected TransactionService transactionService;

    @Test
    public void mockMvc() {
        assertNotNull(mockMvc);
    }

    protected static String toJson(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
