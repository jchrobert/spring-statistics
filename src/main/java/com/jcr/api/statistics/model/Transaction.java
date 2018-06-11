package com.jcr.api.statistics.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.Instant;

/**
 * Data model for incoming transactions.
 *
 * @author jean-charles.robert
 * @since 09.06.18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {

    @NotNull
    private Double amount;
    @NotNull
    private Long timestamp;

    public Instant getInstant() {
        return Instant.ofEpochMilli(timestamp);

    }
}
