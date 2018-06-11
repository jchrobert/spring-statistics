package com.jcr.api.statistics.model;

import lombok.Data;

import java.time.Instant;

/**
 * Wrapper of @{@link Transaction} with storage of the time of event.
 *
 * @author jean-charles.robert
 * @since 10.06.18
 */
@Data
public class TransactionEvent {

    private Transaction transaction;
    private Instant eventTime;

    public TransactionEvent(final Transaction transaction) {
        this.transaction = transaction;
        this.eventTime = Instant.now();
    }
}
