package com.jcr.api.statistics.controller;

import com.jcr.api.statistics.model.Transaction;
import com.jcr.api.statistics.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.jcr.api.statistics.util.StatsUtil.checkValidForStatistics;

/**
 * Transaction endpoint.
 *
 * @author jean-charles.robert
 * @since 11.06.18
 */
@RestController
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/transaction")
    public void inputTransaction(@Valid @RequestBody Transaction transaction) throws ExpiredTransactionException {
        if (!checkValidForStatistics(transaction)) {
            throw new ExpiredTransactionException();
        }
        transactionService.addTransaction(transaction);
    }

    @ExceptionHandler(ExpiredTransactionException.class)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void handleExpiredTransaction() {
    }

    public class ExpiredTransactionException extends Exception {
    }
}
