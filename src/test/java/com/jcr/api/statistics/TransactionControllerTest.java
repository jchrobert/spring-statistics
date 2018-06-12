package com.jcr.api.statistics;

import com.jcr.api.statistics.controller.TransactionController;
import com.jcr.api.statistics.model.Transaction;
import org.junit.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;

import java.time.Instant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Spring test MVC Layer {@link TransactionController}.
 *
 * @author jean-charles.robert
 * @since 11.06.18
 */
@WebMvcTest(TransactionController.class)
public class TransactionControllerTest extends MvcBaseTest {

    @Test
    public void okTransactionRequest201() throws Exception {
        Transaction validTransaction = new Transaction(12.5, Instant.now().minusSeconds(5).toEpochMilli());
        mockMvc.perform(post("/transaction")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(toJson(validTransaction)))
                //.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().string(""));
    }

    @Test
    public void notOkTransactionRequest204() throws Exception {
        Transaction expiredTransaction = new Transaction(12.5, 1478192204000L);
        mockMvc.perform(post("/transaction")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .content(toJson(expiredTransaction)))
                //.andDo(print())
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }

}
