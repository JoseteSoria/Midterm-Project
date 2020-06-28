package com.ironhack.MidtermProject.controller.impl.classes;

import com.ironhack.MidtermProject.controller.interfaces.classes.TransactionController;
import com.ironhack.MidtermProject.model.classes.Transaction;
import com.ironhack.MidtermProject.service.classes.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TransactionControllerImpl implements TransactionController {
    @Autowired
    private TransactionService transactionService;

    @GetMapping("/transactions")
    @ResponseStatus(code = HttpStatus.OK)
    public List<Transaction> findAll() {
        return transactionService.findAll();
    }
}
