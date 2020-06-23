package com.ironhack.MidtermProject.service.account;

import com.ironhack.MidtermProject.enums.TransactionType;
import com.ironhack.MidtermProject.exceptions.IdNotFoundException;
import com.ironhack.MidtermProject.model.account.CheckingAcc;
import com.ironhack.MidtermProject.model.account.CreditCardAcc;
import com.ironhack.MidtermProject.model.classes.Money;
import com.ironhack.MidtermProject.model.classes.Transaction;
import com.ironhack.MidtermProject.repository.account.CreditCardAccRepository;
import com.ironhack.MidtermProject.repository.classes.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

@Service
public class CreditCardAccService {

    @Autowired
    private CreditCardAccRepository creditCardAccRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    public List<CreditCardAcc> findAll(){
        return creditCardAccRepository.findAll();
    }

    public CreditCardAcc findById(Integer id) {
        CreditCardAcc creditCardAcc = creditCardAccRepository.findById(id).orElseThrow(()-> new IdNotFoundException("Credit Card account not found with thar id"));
        creditCardAcc.updateDateInterestRate();
        creditCardAccRepository.save(creditCardAcc);
        return creditCardAccRepository.findById(id).orElseThrow(()-> new IdNotFoundException("Credit Card account not found with thar id"));
    }

    public CreditCardAcc create(CreditCardAcc creditCardAcc){
        return creditCardAccRepository.save(creditCardAcc);
    }

    @Transactional
    public void debitBalance(Integer id, BigDecimal amount, Currency currency){
        if(currency == null){
            currency = Currency.getInstance("USD");
        }
        CreditCardAcc creditCardAcc = creditCardAccRepository.findById(id).
                orElseThrow(()-> new IdNotFoundException("Credit Card account not found with thar id"));
        creditCardAcc.debitBalance(new Money(amount, currency));
        creditCardAccRepository.save(creditCardAcc);
        Transaction transaction = new Transaction(id, new Money(amount, currency), TransactionType.DEBIT );
        transactionRepository.save(transaction);
    }

    @Transactional
    public void creditBalance(Integer id, BigDecimal amount, Currency currency){
        if(currency == null){
            currency = Currency.getInstance("USD");
        }
        CreditCardAcc creditCardAcc = creditCardAccRepository.findById(id).
                orElseThrow(()-> new IdNotFoundException("Credit card account not found with thar id"));
        creditCardAcc.creditBalance(new Money(amount, currency));
        creditCardAccRepository.save(creditCardAcc);
        Transaction transaction = new Transaction(id, new Money(amount, currency), TransactionType.CREDIT );
        transactionRepository.save(transaction);
    }

}
