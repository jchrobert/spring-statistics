package com.jcr.api.statistics.controller;

import com.jcr.api.statistics.model.Statistics;
import com.jcr.api.statistics.model.Transaction;
import com.jcr.api.statistics.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.jcr.api.statistics.util.StatsUtil.checkValidForStatistics;

/**
 * Controller defining the endpoints.
 *
 * @author jean-charles.robert
 * @since 09.06.18
 */
@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/transaction")
    public void inputTransaction(@Valid @RequestBody Transaction transaction) throws ExpiredTransactionException {
        if (!checkValidForStatistics(transaction)) {
            throw new ExpiredTransactionException();
        }
        statisticsService.addTransaction(transaction);

    }


    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/statistics")
    @ResponseBody
    public Statistics getStatistics() {
        return statisticsService.getStatistics();
    }


    @ExceptionHandler(ExpiredTransactionException.class)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void handleExpiredTransaction() {
    }

    public class ExpiredTransactionException extends Exception {
    }
}
