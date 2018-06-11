package com.jcr.api.statistics.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduled task configuration.
 *
 * @author jean-charles.robert
 * @since 10.06.18
 */
@Component
@EnableScheduling
public class ScheduledTasks {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledTasks.class);

    @Autowired
    private StatisticsService statisticsService;

    /**
     * Runs remove of expired transactions every 2 minutes.
     */
    @Scheduled(fixedRateString = "${statistics.remove.transactions.fixedRate:120000}")
    public void purgeTransactions() {
        statisticsService.removeExpiredTransactions();
    }

    /**
     * Update statistics every seconds.
     */
    @Scheduled(fixedRateString = "${statistics.update.fixedRate:1000}")
    public void updateStatistics() {
        statisticsService.updateStatistics();
    }


}
