package com.jcr.api.statistics.util;

import com.jcr.api.statistics.model.Statistics;
import com.jcr.api.statistics.model.Transaction;

import java.text.DecimalFormat;
import java.time.Duration;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.time.Instant.now;
import static java.util.stream.Collectors.toList;

/**
 * Collects some util and reusable methods.
 * Methods logic are meant to be easily unit testable.
 *
 * @author jean-charles.robert
 * @since 09.06.18
 */
public final class StatsUtil {


    private static final int TIME_LIMIT = 60;
    private static final DecimalFormat DOUBLE_DECIMAL_FORMAT = new DecimalFormat("##.##");

    private StatsUtil() {
    }

    /**
     * Valid for statistics if the transaction time is between 0 and 60 seconds comparing to the current instant
     *
     * @return true if the time is valid (less than 60 seconds)
     */

    public static boolean isValidForStatistics(Transaction transaction) {
        return TRANSACTION_VALID_PREDICATE.test(transaction);
    }

    /**
     * Predicate validating the time of a transaction.
     */
    public static final Predicate<Transaction> TRANSACTION_VALID_PREDICATE =
            t -> {
                final long duration = Duration.between(t.getInstant(), now()).getSeconds();
                return duration >= 0 && duration <= TIME_LIMIT;
            };


    /**
     * Filters valid transactions.
     *
     * @param transactions list of all transactions
     * @return list of valid transactions
     */
    public static List<Transaction> getValidTransactions(final List<Transaction> transactions) {
        return transactions.stream().filter(TRANSACTION_VALID_PREDICATE).collect(toList());
    }

    public static Statistics calculateStatistics(final List<Transaction> transactions) {
        DoubleSummaryStatistics currentStats = transactions.stream()
                .filter(TRANSACTION_VALID_PREDICATE)
                .collect(Collectors.summarizingDouble(Transaction::getAmount));
        return toStatistics(currentStats);
    }

    public static Statistics toStatistics(DoubleSummaryStatistics doubleSummaryStatistics) {
        return SUMMARY_TO_STATISTICS.apply(doubleSummaryStatistics);
    }

    private static final Function<DoubleSummaryStatistics, Statistics> SUMMARY_TO_STATISTICS =
            summaryStats -> Statistics.builder()
                    .sum(format(summaryStats.getSum()))
                    .avg(format(summaryStats.getAverage()))
                    .max(summaryStats.getMax() != Double.NEGATIVE_INFINITY ? summaryStats.getMax() : 0)
                    .min(summaryStats.getMin() != Double.POSITIVE_INFINITY ? summaryStats.getMin() : 0)
                    .count(summaryStats.getCount()).build();


    private static Double format(final Double value) {
        return Double.valueOf(DOUBLE_DECIMAL_FORMAT.format(value));
    }

}
