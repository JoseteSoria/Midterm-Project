package com.ironhack.MidtermProject.service.classes;

import com.ironhack.MidtermProject.model.classes.Transaction;
import com.ironhack.MidtermProject.repository.classes.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;

    public List<Transaction> findAll(){ return transactionRepository.findAll(); }
}
