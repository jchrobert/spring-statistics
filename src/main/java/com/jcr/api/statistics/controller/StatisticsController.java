package com.jcr.api.statistics.controller;

import com.jcr.api.statistics.model.Statistics;
import com.jcr.api.statistics.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Statistics endpoint.
 *
 * @author jean-charles.robert
 * @since 09.06.18
 */
@RestController
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;


    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/statistics")
    @ResponseBody
    public Statistics getStatistics() {
        return statisticsService.getStatistics();
    }

}
