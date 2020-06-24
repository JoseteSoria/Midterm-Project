package com.ironhack.MidtermProject.service.account;

import com.ironhack.MidtermProject.enums.TransactionType;
import com.ironhack.MidtermProject.exceptions.IdNotFoundException;
import com.ironhack.MidtermProject.exceptions.FraudException;
import com.ironhack.MidtermProject.model.account.CreditCardAcc;
import com.ironhack.MidtermProject.model.classes.Money;
import com.ironhack.MidtermProject.model.classes.Transaction;
import com.ironhack.MidtermProject.model.user.AccountHolder;
import com.ironhack.MidtermProject.repository.account.CreditCardAccRepository;
import com.ironhack.MidtermProject.repository.user.AccountHolderRepository;
import com.ironhack.MidtermProject.service.classes.TransactionService;
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
    private TransactionService transactionService;
    @Autowired
    private AccountHolderRepository accountHolderRepository;

    public List<CreditCardAcc> findAll(){
        return creditCardAccRepository.findAll();
    }

    public CreditCardAcc findById(Integer id) {
        CreditCardAcc creditCardAcc = creditCardAccRepository.findById(id).orElseThrow(()-> new IdNotFoundException("Credit Card account not found with that id"));
        creditCardAcc.updateDateInterestRate();
        creditCardAccRepository.save(creditCardAcc);
        return creditCardAccRepository.findById(id).orElseThrow(()-> new IdNotFoundException("Credit Card account not found with that id"));
    }

    public CreditCardAcc create(CreditCardAcc creditCardAcc){
        AccountHolder primOwner = new AccountHolder();
        if(creditCardAcc.getPrimaryOwner().getId()!=null) {
            primOwner = accountHolderRepository.findById(creditCardAcc.getPrimaryOwner().getId())
                    .orElseThrow(() -> new IdNotFoundException("Not primary Owner found with that id"));
        }
        else{
            primOwner =new AccountHolder(creditCardAcc.getPrimaryOwner().getName(),creditCardAcc.getPrimaryOwner().getUsername(),
                    creditCardAcc.getPrimaryOwner().getPassword(), creditCardAcc.getPrimaryOwner().getDateOfBirthday(),
                    creditCardAcc.getPrimaryOwner().getPrimaryAddress(), creditCardAcc.getPrimaryOwner().getMailingAddress());
        }
        accountHolderRepository.save(primOwner);
        creditCardAcc.setPrimaryOwner(primOwner);
        return creditCardAccRepository.save(creditCardAcc);
    }

    @Transactional
    public void debitBalance(Integer id, BigDecimal amount, Currency currency){
        if(currency == null){
            currency = Currency.getInstance("USD");
        }
        CreditCardAcc creditCardAcc = creditCardAccRepository.findById(id).
                orElseThrow(()-> new IdNotFoundException("Credit Card account not found with that id"));
        Transaction transaction = new Transaction(null, id,  new Money(amount, currency), TransactionType.DEBIT);
        transactionService.create(transaction);
        creditCardAcc.reduceBalance(new Money(amount, currency));
        creditCardAccRepository.save(creditCardAcc);

    }

    @Transactional
    public void creditBalance(Integer id, BigDecimal amount, Currency currency){
        if(currency == null){
            currency = Currency.getInstance("USD");
        }
        CreditCardAcc creditCardAcc = creditCardAccRepository.findById(id).
                orElseThrow(()-> new IdNotFoundException("Credit card account not found with that id"));
        Transaction transaction = new Transaction(id, null, new Money(amount, currency), TransactionType.CREDIT);
        // Any amount to receive is allowed.
        if(transactionService.checkTransaction(transaction)){
            transactionService.create(transaction);
            creditCardAcc.addBalance(new Money(amount, currency));
        }
        else{
            //fraud
            throw new FraudException("Operation cancelled. You are committing fraud!");
        }
        creditCardAccRepository.save(creditCardAcc);

//        if(transactionService.checkLimit(transaction, creditCardAcc.getCreditLimit().getAmount())){
//            transactionService.create(transaction);
//            creditCardAcc.creditBalance(new Money(amount, currency));
//        }
//        else{
//            //fraud
//            throw new FraudException("You are committing fraud!");
//        }
    }

}
