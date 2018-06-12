package com.jcr.api.statistics.service;

import com.jcr.api.statistics.model.Statistics;
import com.jcr.api.statistics.model.Transaction;
import com.jcr.api.statistics.model.TransactionEvent;
import com.jcr.api.statistics.util.StatsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static com.jcr.api.statistics.util.StatsUtil.getValidTransactions;

/**
 * Service.
 *
 * @author jean-charles.robert
 * @since 09.06.18
 */
@Service
public class StatisticsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(StatisticsService.class);

    // use a thread-safe non-blocking map to cache the statistics object);
    private AtomicReference<Statistics> statisticsCache = new AtomicReference<>(Statistics.empty());

    @Autowired
    private TransactionService transactionService;

    /**
     * Retrieves the statistic from the cache.
     * Therefore the endpoint call and execution is always constant O(1).
     *
     * @return the current statistics
     */
    public Statistics getStatistics() {
        return statisticsCache.get();
    }


    @EventListener
    public void updateStatisticsEvent(TransactionEvent event) {
        LOGGER.debug("Event received {}", event);
        updateStatistics();
    }

    /**
     * Updates the statistics of the current transactions in the queue.
     * - calculates statistics
     * - checks overwriting by concurrency: according to the duration of the calculation,
     * the cache may have been changed by another thread with a more recent queue.
     * - update the cache if needed.
     */
    @Async("updateExecutor")
    public void updateStatistics() {
        LOGGER.debug("Starting to update the statistics");
        final Instant executionTime = Instant.now();
        Statistics currentStats = statisticsCache.get();

        // statistics filter and calculation
        final List<Transaction> currentTransactions = getValidTransactions(transactionService.getTransactions());
        Statistics newStats = StatsUtil.calculateStatistics(currentTransactions);
        newStats.setTimestamp(executionTime);
        boolean updated = statisticsCache.compareAndSet(currentStats, newStats);
        LOGGER.debug("Statistics updated={}, stats={}", updated, statisticsCache.get());

    }

}
