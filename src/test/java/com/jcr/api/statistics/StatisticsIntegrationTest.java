package com.jcr.api.statistics;

import com.jcr.api.statistics.model.Transaction;
import com.jcr.api.statistics.service.StatisticsService;
import com.jcr.api.statistics.service.TransactionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static java.time.Instant.now;
import static org.junit.Assert.assertEquals;

/**
 * Spring test @{@link StatisticsService}.
 * Integration test on a running server.
 *
 * @author jean-charles.robert
 * @since 10.06.18
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StatisticsIntegrationTest {

    @Autowired
    private StatisticsService statisticsService;
    @Autowired
    private TransactionService transactionService;

    @Test
    public void addTransactionsAndCalculateStatistics() throws InterruptedException {

        // long living transaction (50sec timelife)
        Transaction t1 = new Transaction(30.0, now().minusSeconds(10).toEpochMilli());
        // ready to expire (1 sec timelife)
        Transaction t2 = new Transaction(7.5, now().minusSeconds(60).toEpochMilli());
        // fresh new transaction
        Transaction t3 = new Transaction(200.0, now().toEpochMilli());
        assertEquals(0, statisticsService.getStatistics().getCount().longValue());

        transactionService.addTransaction(t1);
        assertEquals(1, statisticsService.getStatistics().getCount().longValue());
        assertEquals(Double.valueOf(30.0), statisticsService.getStatistics().getSum());

        transactionService.addTransaction(t2);
        assertEquals(2, statisticsService.getStatistics().getCount().longValue());
        assertEquals(Double.valueOf(37.5), statisticsService.getStatistics().getSum());

        transactionService.addTransaction(t1);
        assertEquals(3, statisticsService.getStatistics().getCount().longValue());
        assertEquals(Double.valueOf(67.5), statisticsService.getStatistics().getSum());
        assertEquals(Double.valueOf(7.5), statisticsService.getStatistics().getMin());
        assertEquals(Double.valueOf(30.0), statisticsService.getStatistics().getMax());

        transactionService.addTransaction(t1);
        transactionService.addTransaction(t1);
        transactionService.addTransaction(t1);
        transactionService.addTransaction(t1);
        assertEquals(Double.valueOf(187.5), statisticsService.getStatistics().getSum());
        // at second one, the transactions are still there
        Thread.sleep(1000);
        assertEquals(Double.valueOf(187.5), statisticsService.getStatistics().getSum());
        // from the second two, the t2 transactions has expired and is removed from the last 60 seconds transactions queue.
        Thread.sleep(1000);
        assertEquals(Double.valueOf(180.0), statisticsService.getStatistics().getSum());
        transactionService.addTransaction(t3);
        assertEquals(Double.valueOf(380.0), statisticsService.getStatistics().getSum());

    }


}
