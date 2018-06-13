package com.jcr.api.statistics.service;

import com.jcr.api.statistics.model.Transaction;
import com.jcr.api.statistics.model.TransactionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.jcr.api.statistics.util.StatsUtil.TRANSACTION_VALID_PREDICATE;
import static java.util.Arrays.asList;

/**
 * Service dealing with transactions.
 *
 * @author jean-charles.robert
 * @since 11.06.18
 */
@Service
public class TransactionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionService.class);

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    // use a thread-safe non-blocking queue to store the transactions
    private ConcurrentLinkedQueue<Transaction> transactionsQueue = new ConcurrentLinkedQueue<>();

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

    @Async("simpleExecutor")
    public void removeExpiredTransactions() {
        LOGGER.debug("Remove expired transactions.");
        transactionsQueue.removeIf(TRANSACTION_VALID_PREDICATE.negate());
    }

    /**
     * Gets the list from the current queue.
     * The {@link ConcurrentLinkedQueue#toArray()} assures a "snapshot" of the queue at a certain point of time.
     *
     * @return list of current transactions.
     */
    public List<Transaction> getTransactions() {
        return asList(transactionsQueue.toArray(new Transaction[0]));
    }

}
