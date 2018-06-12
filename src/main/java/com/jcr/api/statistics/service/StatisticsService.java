package com.jcr.api.statistics.service;

import com.jcr.api.statistics.model.Statistics;
import com.jcr.api.statistics.model.Transaction;
import com.jcr.api.statistics.model.TransactionEvent;
import com.jcr.api.statistics.util.StatsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

import static com.jcr.api.statistics.util.StatsUtil.TRANSACTION_VALID_PREDICATE;
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

    // use a thread-safe non-blocking queue to store the transactions
    private ConcurrentLinkedQueue<Transaction> transactionsQueue = new ConcurrentLinkedQueue<>();
    // use a thread-safe non-blocking map to cache the statistics object);
    private AtomicReference<Statistics> statisticsCache = new AtomicReference<>(Statistics.empty());

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    /**
     * Retrieves the statistic from the cache.
     * Therefore the endpoint call and execution is always constant O(1).
     *
     * @return the current statistics
     */
    public Statistics getStatistics() {
        return statisticsCache.get();
    }

    /**
     * Adds a transaction to the queue.
     * The endpoint call is constant in space and time because {@link ConcurrentLinkedQueue#add(Object)} is O(1).
     * An event is pushed to execute the other more expensive actions asynchronously
     *
     * @param transaction transaction to add
     */
    public void addTransaction(Transaction transaction) {
        LOGGER.debug("Adding transaction {}", transaction);
        transactionsQueue.add(transaction);
        eventPublisher.publishEvent(new TransactionEvent(transaction));
    }

    @EventListener
    public void updateStatistics(TransactionEvent event) {
        if (!isObsolete(event.getEventTime())) {
            updateStatistics();
        }
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
        final List<Transaction> currentTransactions = new ArrayList<>(transactionsQueue);
        // statistics filter and calculation
        Statistics newStats = StatsUtil.calculateStatistics(getValidTransactions(currentTransactions));
        newStats.setTimestamp(executionTime);
        if (!isObsolete(executionTime)) {
            boolean updated = statisticsCache.compareAndSet(currentStats, newStats);
            LOGGER.debug("Statistics updated {} {}", updated, statisticsCache.get());
        }
    }

    @Async("simpleExecutor")
    public void removeExpiredTransactions() {
        LOGGER.debug("Remove expired transactions.");
        transactionsQueue.removeIf(TRANSACTION_VALID_PREDICATE.negate());
    }

    public boolean isObsolete(final Instant eventTime) {
        return eventTime.isBefore(statisticsCache.get().getTimestamp());
    }
}
