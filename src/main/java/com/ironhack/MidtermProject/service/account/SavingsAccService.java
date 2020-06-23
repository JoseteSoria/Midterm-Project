package com.ironhack.MidtermProject.service.account;

import com.ironhack.MidtermProject.enums.TransactionType;
import com.ironhack.MidtermProject.exceptions.IdNotFoundException;
import com.ironhack.MidtermProject.model.account.CheckingAcc;
import com.ironhack.MidtermProject.model.account.SavingsAcc;
import com.ironhack.MidtermProject.model.classes.Money;
import com.ironhack.MidtermProject.model.classes.Transaction;
import com.ironhack.MidtermProject.repository.account.SavingsAccRepository;
import com.ironhack.MidtermProject.repository.classes.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

@Service
public class SavingsAccService {

    @Autowired
    private SavingsAccRepository savingsAccRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    public List<SavingsAcc> findAll(){ return savingsAccRepository.findAll(); }

    public SavingsAcc findById(Integer id) {
        SavingsAcc savingsAcc = savingsAccRepository.findById(id).orElseThrow(()-> new IdNotFoundException("Savings account not found with thar id"));
        savingsAcc.updateDateInterestRate();
        savingsAccRepository.save(savingsAcc);
        return savingsAccRepository.findById(id).orElseThrow(()-> new IdNotFoundException("Savings account not found with thar id"));
    }

    public SavingsAcc create(SavingsAcc savingsAcc){
        return savingsAccRepository.save(savingsAcc);
    }

    @Transactional
    public void debitBalance(Integer id, BigDecimal amount, Currency currency){
        if(currency == null){
            currency = Currency.getInstance("USD");
        }
        SavingsAcc savingsAcc = savingsAccRepository.findById(id).
                orElseThrow(()-> new IdNotFoundException("Savings account not found with thar id"));
        savingsAcc.debitBalance(new Money(amount, currency));
        savingsAccRepository.save(savingsAcc);
        Transaction transaction = new Transaction(id, new Money(amount, currency), TransactionType.DEBIT );
        transactionRepository.save(transaction);
    }

    @Transactional
    public void creditBalance(Integer id, BigDecimal amount, Currency currency){
        if(currency == null){
            currency = Currency.getInstance("USD");
        }
        SavingsAcc savingsAcc = savingsAccRepository.findById(id).
                orElseThrow(()-> new IdNotFoundException("Savings account not found with thar id"));
        savingsAcc.creditBalance(new Money(amount, currency));
        savingsAccRepository.save(savingsAcc);
        Transaction transaction = new Transaction(id, new Money(amount, currency), TransactionType.CREDIT );
        transactionRepository.save(transaction);
    }
}
