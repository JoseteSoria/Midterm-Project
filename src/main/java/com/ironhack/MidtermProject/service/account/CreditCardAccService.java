package com.ironhack.MidtermProject.service.account;

import com.ironhack.MidtermProject.enums.Status;
import com.ironhack.MidtermProject.enums.TransactionType;
import com.ironhack.MidtermProject.exceptions.IdNotFoundException;
import com.ironhack.MidtermProject.exceptions.FraudException;
import com.ironhack.MidtermProject.exceptions.NoOwnerException;
import com.ironhack.MidtermProject.exceptions.StatusException;
import com.ironhack.MidtermProject.model.account.CheckingAcc;
import com.ironhack.MidtermProject.model.account.CreditCardAcc;
import com.ironhack.MidtermProject.model.classes.Money;
import com.ironhack.MidtermProject.model.classes.Transaction;
import com.ironhack.MidtermProject.model.user.AccountHolder;
import com.ironhack.MidtermProject.model.user.ThirdParty;
import com.ironhack.MidtermProject.model.user.User;
import com.ironhack.MidtermProject.repository.account.CreditCardAccRepository;
import com.ironhack.MidtermProject.repository.user.AccountHolderRepository;
import com.ironhack.MidtermProject.repository.user.ThirdPartyRepository;
import com.ironhack.MidtermProject.service.classes.TransactionService;
import com.ironhack.MidtermProject.util.PasswordUtility;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    @Autowired
    private ThirdPartyRepository thirdPartyRepository;

    private static final Logger LOGGER = LogManager.getLogger(CreditCardAccService.class);

    public List<CreditCardAcc> findAll(){
        return creditCardAccRepository.findAll();
    }

    public CreditCardAcc checkFindById(Integer id, User user) {
        CreditCardAcc creditCardAcc = new CreditCardAcc();
        switch(user.getRole()){
            case ADMIN:
                creditCardAcc = findById(id);
                break;
            case ACCOUNT_HOLDER:
                creditCardAcc = findById(id);
                if(creditCardAcc.getPrimaryOwner().getId()==user.getId() || creditCardAcc.getSecondaryOwner().getId() == user.getId()){
                    return creditCardAcc;
                }
                else throw new NoOwnerException("You are not the owner of this account");
            case THIRD_PARTY:
                throw new NoOwnerException("You are a third party. You are not the owner of this account");
        }
        return creditCardAcc;
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
    public void debitBalance(User user, Integer id, BigDecimal amount, Currency currency, String secretKey, String header){
        checkAllowance(user, id, secretKey, header);
        if(currency == null){
            currency = Currency.getInstance("USD");
        }
        CreditCardAcc creditCardAcc = creditCardAccRepository.findById(id).
                orElseThrow(()-> new IdNotFoundException("Credit Card account not found with that id"));
        Transaction transaction = new Transaction(user.getId(), creditCardAcc, null,  new Money(amount, currency), TransactionType.DEBIT);
        if(transactionService.checkTransaction(transaction)){
            LOGGER.info("DEBIT TRANSACTION CREDIT-CARD ACCOUNT. USER ORDER-ID : " + user.getId());
            transactionService.create(transaction);
            creditCardAcc.reduceBalance(new Money(amount, currency));
        }
        else{
            //fraud
            throw new FraudException("Operation cancelled. You are committing fraud!");
        }
        creditCardAccRepository.save(creditCardAcc);

    }

    @Transactional
    public void creditBalance(User user, Integer id, BigDecimal amount, Currency currency, String secretKey, String header){
        checkAllowance(user, id, secretKey, header);
        if(currency == null){
            currency = Currency.getInstance("USD");
        }
        CreditCardAcc creditCardAcc = creditCardAccRepository.findById(id).
                orElseThrow(()-> new IdNotFoundException("Credit card account not found with that id"));
        Transaction transaction = new Transaction(user.getId(), null, creditCardAcc, new Money(amount, currency), TransactionType.CREDIT);
        // Any amount to receive is allowed.
        if(transactionService.checkTransaction(transaction)){
            LOGGER.info("CREDIT TRANSACTION CREDIT-CARD ACCOUNT. USER ORDER-ID : " + user.getId());
            transactionService.create(transaction);
            creditCardAcc.addBalance(new Money(amount, currency));
        }
        else{
            //fraud
            throw new FraudException("Operation cancelled. You are committing fraud!");
        }
        creditCardAccRepository.save(creditCardAcc);
    }

    private void checkAllowance(User user, Integer id, String secretKey, String header) {
        CreditCardAcc creditCardAcc = findById(id);
        switch(user.getRole()){
            case ADMIN:
                break;
            case ACCOUNT_HOLDER:
                if((creditCardAcc.getPrimaryOwner()!=null && creditCardAcc.getPrimaryOwner().getId()== user.getId()) || (creditCardAcc.getSecondaryOwner()!=null && creditCardAcc.getSecondaryOwner().getId() == user.getId())){
                    break;
                }
                else throw new NoOwnerException("You are not the owner of this account");
            case THIRD_PARTY:
                throw new NoOwnerException("You are a third party. You can not access to this credit card");
        }
    }


}
