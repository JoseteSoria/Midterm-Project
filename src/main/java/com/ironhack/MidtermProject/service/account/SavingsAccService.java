package com.ironhack.MidtermProject.service.account;

import com.ironhack.MidtermProject.enums.Status;
import com.ironhack.MidtermProject.enums.TransactionType;
import com.ironhack.MidtermProject.exceptions.IdNotFoundException;
import com.ironhack.MidtermProject.model.account.SavingsAcc;
import com.ironhack.MidtermProject.model.classes.Money;
import com.ironhack.MidtermProject.model.classes.Transaction;
import com.ironhack.MidtermProject.model.user.AccountHolder;
import com.ironhack.MidtermProject.repository.account.SavingsAccRepository;
import com.ironhack.MidtermProject.repository.user.AccountHolderRepository;
import com.ironhack.MidtermProject.service.classes.TransactionService;
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
    private TransactionService transactionService;
    @Autowired
    private AccountHolderRepository accountHolderRepository;

    public List<SavingsAcc> findAll(){ return savingsAccRepository.findAll(); }

    public SavingsAcc findById(Integer id) {
        SavingsAcc savingsAcc = savingsAccRepository.findById(id).orElseThrow(()-> new IdNotFoundException("Savings account not found with that id"));
        savingsAcc.updateDateInterestRate();
        savingsAccRepository.save(savingsAcc);
        return savingsAccRepository.findById(id).orElseThrow(()-> new IdNotFoundException("Savings account not found with that id"));
    }

    public SavingsAcc create(SavingsAcc savingsAcc){
        AccountHolder primOwner = new AccountHolder();
        if(savingsAcc.getPrimaryOwner().getId()!=null) {
            primOwner = accountHolderRepository.findById(savingsAcc.getPrimaryOwner().getId())
                    .orElseThrow(() -> new IdNotFoundException("Not primary Owner found with that id"));
        }
        else{
            primOwner =new AccountHolder(savingsAcc.getPrimaryOwner().getName(),savingsAcc.getPrimaryOwner().getUsername(),
                    savingsAcc.getPrimaryOwner().getPassword(), savingsAcc.getPrimaryOwner().getDateOfBirthday(),
                    savingsAcc.getPrimaryOwner().getPrimaryAddress(), savingsAcc.getPrimaryOwner().getMailingAddress());
        }
        accountHolderRepository.save(primOwner);
        savingsAcc.setPrimaryOwner(primOwner);
        return savingsAccRepository.save(savingsAcc);
    }

    @Transactional
    public void reduceBalance(Integer id, BigDecimal amount, Currency currency){
        if(currency == null){
            currency = Currency.getInstance("USD");
        }
        SavingsAcc savingsAcc = savingsAccRepository.findById(id).
                orElseThrow(()-> new IdNotFoundException("Savings account not found with that id"));
        Transaction transaction = new Transaction(id, null, new Money(amount, currency), TransactionType.CREDIT);
        if(transactionService.checkTransaction(transaction)){
            transactionService.create(transaction);
            savingsAcc.reduceBalance(new Money(amount, currency));
        }
        else{
            //fraud
            savingsAcc.setStatus(Status.FROZEN);
            savingsAccRepository.save(savingsAcc);
        }
        savingsAccRepository.save(savingsAcc);
    }

    @Transactional
    public void addBalance(Integer id, BigDecimal amount, Currency currency){
        if(currency == null){
            currency = Currency.getInstance("USD");
        }
        SavingsAcc savingsAcc = savingsAccRepository.findById(id).
                orElseThrow(()-> new IdNotFoundException("Savings account not found with thar id"));
        Transaction transaction = new Transaction(null, id, new Money(amount, currency), TransactionType.DEBIT);
        if(transactionService.checkTransaction(transaction)){
            transactionService.create(transaction);
            savingsAcc.addBalance(new Money(amount, currency));
        }
        else{
            //fraud
            savingsAcc.setStatus(Status.FROZEN);
            savingsAccRepository.save(savingsAcc);
        }
        savingsAccRepository.save(savingsAcc);
    }
}
