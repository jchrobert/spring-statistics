package com.jcr.api.statistics.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

import static java.time.Instant.now;

/**
 * Data model for a transaction.
 *
 * @author jean-charles.robert
 * @since 09.06.18
 */
@Data
@AllArgsConstructor
@Builder
public class Statistics {

    private Double sum;
    private Double avg;
    private Double max;
    private Double min;
    private Long count;
    @JsonIgnore
    private Instant timestamp;

    public static Statistics empty() {
        return new Statistics(0.0, 0.0, 0.0, 0.0, 0L, now());
    }


}
