package com.jcr.api.statistics;


import com.jcr.api.statistics.model.Statistics;
import com.jcr.api.statistics.model.Transaction;
import com.jcr.api.statistics.util.StatsUtil;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.jcr.api.statistics.util.StatsUtil.calculateStatistics;
import static com.jcr.api.statistics.util.StatsUtil.checkValidForStatistics;
import static com.jcr.api.statistics.util.StatsUtil.getValidTransactions;
import static java.time.Instant.now;
import static java.time.Instant.ofEpochMilli;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for @{@link StatsUtil}.
 *
 * @author jean-charles.robert
 * @since 09.06.18
 */
public class StatsUtilTest {

    private List<Transaction> validTransactions;
    private List<Transaction> expiredTransactions;
    private List<Transaction> allTransactions;

    @Before
    public void before() {
        validTransactions = Arrays.asList(
                // time past less than 60 seconds
                new Transaction(50.0, now().toEpochMilli()),
                new Transaction(35.6, now().minusSeconds(10).toEpochMilli()),
                new Transaction(7.0, now().minusSeconds(59).toEpochMilli()),
                new Transaction(83.0, now().minusSeconds(60).toEpochMilli()),
                new Transaction(740.5, now().minus(1, ChronoUnit.MINUTES).toEpochMilli()),
                new Transaction(24.8, ZonedDateTime.now(ZoneId.of("UTC")).toInstant().toEpochMilli()));

        expiredTransactions = Arrays.asList(
                // time past more than 60 seconds
                new Transaction(5.60, ofEpochMilli(1478192204L).toEpochMilli()),
                new Transaction(23.0, now().minusSeconds(61).toEpochMilli()),
                new Transaction(35.6, now().minusSeconds(155).toEpochMilli()),
                new Transaction(186.9, now().minus(2, ChronoUnit.MINUTES).toEpochMilli()),
                new Transaction(12.95, now().minus(5, ChronoUnit.DAYS).toEpochMilli()),
                // time in the future
                new Transaction(9.0, now().plus(1, ChronoUnit.SECONDS).toEpochMilli()),
                new Transaction(25.5, LocalDateTime.of(2018, Month.JUNE, 1, 12, 39).toInstant(ZoneOffset.UTC).toEpochMilli()));
        allTransactions = new ArrayList<>(validTransactions);
        allTransactions.addAll(expiredTransactions);
    }

    @Test
    public void testIsValidForStatistics() {
        validTransactions.forEach(t -> assertTrue(checkValidForStatistics(t)));
    }

    @Test
    public void testIsNotValidForStatistics() {
        expiredTransactions.forEach(t -> assertFalse(checkValidForStatistics(t)));
    }

    @Test
    public void testFilterValidTransactions() {
        assertEquals(validTransactions, getValidTransactions(allTransactions));
    }

    @Test
    public void testCalculateStatistics() {
        Statistics stats1 = calculateStatistics(validTransactions);
        Statistics stats2 = calculateStatistics(allTransactions);
        assertEquals(validTransactions.size(), stats1.getCount().longValue());
        assertEquals(Double.valueOf(740.5), stats1.getMax());
        assertEquals(Double.valueOf(7.0), stats1.getMin());
        assertEquals(Double.valueOf(940.9), stats1.getSum());
        assertEquals(Double.valueOf(156.82), stats1.getAvg());

        assertEquals(allTransactions.size(), stats2.getCount().longValue());
        assertEquals(Double.valueOf(740.5), stats2.getMax());
        assertEquals(Double.valueOf(5.6), stats2.getMin());
        assertEquals(Double.valueOf(1239.45), stats2.getSum());
        assertEquals(Double.valueOf(95.34), stats2.getAvg());
    }

}