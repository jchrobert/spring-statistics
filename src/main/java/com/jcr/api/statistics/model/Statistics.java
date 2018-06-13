package com.jcr.api.statistics.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

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

    public static Statistics empty() {
        return new Statistics(0.0, 0.0, 0.0, 0.0, 0L);
    }


}
