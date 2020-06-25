package com.ironhack.MidtermProject.service.account;

import com.ironhack.MidtermProject.enums.Status;
import com.ironhack.MidtermProject.enums.TransactionType;
import com.ironhack.MidtermProject.exceptions.IdNotFoundException;
import com.ironhack.MidtermProject.exceptions.NoOwnerException;
import com.ironhack.MidtermProject.exceptions.StatusException;
import com.ironhack.MidtermProject.model.account.CheckingAcc;
import com.ironhack.MidtermProject.model.account.CreditCardAcc;
import com.ironhack.MidtermProject.model.account.SavingsAcc;
import com.ironhack.MidtermProject.model.classes.Money;
import com.ironhack.MidtermProject.model.classes.Transaction;
import com.ironhack.MidtermProject.model.user.AccountHolder;
import com.ironhack.MidtermProject.model.user.ThirdParty;
import com.ironhack.MidtermProject.model.user.User;
import com.ironhack.MidtermProject.repository.account.SavingsAccRepository;
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
public class SavingsAccService {

    @Autowired
    private SavingsAccRepository savingsAccRepository;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private AccountHolderRepository accountHolderRepository;
    @Autowired
    private ThirdPartyRepository thirdPartyRepository;

    private static final Logger LOGGER = LogManager.getLogger(SavingsAccService.class);

    public List<SavingsAcc> findAll(){ return savingsAccRepository.findAll(); }

    public SavingsAcc checkFindById(Integer id, User user) {
        SavingsAcc savingsAcc = new SavingsAcc();
        switch(user.getRole()){
            case ADMIN:
                savingsAcc = findById(id);
                break;
            case ACCOUNT_HOLDER:
                savingsAcc = findById(id);
                if(savingsAcc.getPrimaryOwner().getId()==user.getId() || savingsAcc.getSecondaryOwner().getId() == user.getId()){
                    if(checkLoggedIn(user, savingsAcc))
                    {
                        return savingsAcc;
                    }
                    else
                        throw new StatusException("You are not logged in");
                }
                else throw new NoOwnerException("You are not the owner of this account");
            case THIRD_PARTY:
                throw new NoOwnerException("You are a third party. You are not the owner of this account");
        }
        return savingsAcc;
    }

    public SavingsAcc findById(Integer id) {
        SavingsAcc savingsAcc = savingsAccRepository.findById(id).orElseThrow(()-> new IdNotFoundException("Savings account not found with that id"));
        savingsAcc.updateDateInterestRate();
        savingsAccRepository.save(savingsAcc);
        return savingsAccRepository.findById(id).orElseThrow(()-> new IdNotFoundException("Savings account not found with that id"));
    }

    public SavingsAcc create(SavingsAcc savingsAcc){
        SavingsAcc s1 = new SavingsAcc(savingsAcc.getPrimaryOwner(),savingsAcc.getSecondaryOwner(), savingsAcc.getBalance(),
                savingsAcc.getSecretKey(),savingsAcc.getMinimumBalance(),savingsAcc.getInterestRate(),savingsAcc.getStatus());
        AccountHolder primOwner = new AccountHolder();
        if(s1.getPrimaryOwner().getId()!=null) {
            primOwner = accountHolderRepository.findById(s1.getPrimaryOwner().getId())
                    .orElseThrow(() -> new IdNotFoundException("Not primary Owner found with that id"));
        }
        else{
            primOwner =new AccountHolder(s1.getPrimaryOwner().getName(),s1.getPrimaryOwner().getUsername(),
                    s1.getPrimaryOwner().getPassword(), s1.getPrimaryOwner().getDateOfBirthday(),
                    s1.getPrimaryOwner().getPrimaryAddress(), s1.getPrimaryOwner().getMailingAddress());
        }
        accountHolderRepository.save(primOwner);
        s1.setPrimaryOwner(primOwner);
        return savingsAccRepository.save(s1);
    }

    @Transactional
    public void reduceBalance(User user, Integer id, BigDecimal amount, Currency currency, String secretKey, String header){
        checkAllowance(user, id, secretKey, header);
        if(currency == null){
            currency = Currency.getInstance("USD");
        }
        SavingsAcc savingsAcc = savingsAccRepository.findById(id).
                orElseThrow(()-> new IdNotFoundException("Savings account not found with that id"));
        Transaction transaction = new Transaction(user.getId(), null, savingsAcc, new Money(amount, currency), TransactionType.CREDIT);
        if(transactionService.checkTransaction(transaction)){
            LOGGER.info("CREDIT TRANSACTION SAVINGS ACCOUNT. USER ORDER-ID : " + user.getId());
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
    public void addBalance(User user, Integer id, BigDecimal amount, Currency currency, String secretKey, String header){
        checkAllowance(user, id, secretKey, header);
        if(currency == null){
            currency = Currency.getInstance("USD");
        }
        SavingsAcc savingsAcc = savingsAccRepository.findById(id).
                orElseThrow(()-> new IdNotFoundException("Savings account not found with thar id"));
        Transaction transaction = new Transaction(user.getId(), savingsAcc, null, new Money(amount, currency), TransactionType.DEBIT);
        if(transactionService.checkTransaction(transaction)){
            LOGGER.info("DEBIT TRANSACTION SAVINGS ACCOUNT. USER ORDER-ID : " + user.getId());
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

    private void checkAllowance(User user, Integer id, String secretKey, String header) {
        SavingsAcc savingsAcc = findById(id);
        if(savingsAcc.getStatus().equals(Status.FROZEN))
            throw new StatusException("This account is frozen");
        switch(user.getRole()){
            case ADMIN:
                break;
            case ACCOUNT_HOLDER:
                if((savingsAcc.getPrimaryOwner()!=null && savingsAcc.getPrimaryOwner().getId()== user.getId()) || (savingsAcc.getSecondaryOwner()!=null && savingsAcc.getSecondaryOwner().getId() == user.getId())){
                    if(checkLoggedIn(user, savingsAcc)) {
                        break;
                    }
                    else
                        throw new StatusException("You are not logged in");
                }
                else throw new NoOwnerException("You are not the owner of this account");
            case THIRD_PARTY:
                ThirdParty thirdParty = thirdPartyRepository.findById(user.getId())
                        .orElseThrow(()-> new IdNotFoundException("No third party found"));
                if(header == null || secretKey == null)
                    throw new NoOwnerException("You are a third party. You have to provide more info.");
                else if(!PasswordUtility.passwordEncoder.matches(header, thirdParty.getHashKey()))
                    throw new NoOwnerException("Your header is wrong");
                else if(!secretKey.equals(savingsAcc.getSecretKey()))
                    throw new NoOwnerException("The secret Key is incorrect for that account");
                else
                    break;
        }
    }

    public boolean checkLoggedIn(User user, SavingsAcc savingsAcc){
        if((savingsAcc.getPrimaryOwner()!=null && (savingsAcc.getPrimaryOwner().getId() == user.getId()) && savingsAcc.getPrimaryOwner().isLoggedIn()) ||
                (savingsAcc.getSecondaryOwner()!=null && (savingsAcc.getSecondaryOwner().getId()== user.getId()) && savingsAcc.getSecondaryOwner().isLoggedIn()))
        {
            return true;
        }else
            return false;
    }

    public SavingsAcc changeStatus(Integer id, String status) {
        Status newStatus;
        try {
            newStatus = Status.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new StatusException("There's no status " + status.toUpperCase());
        }
        SavingsAcc savingsAcc = savingsAccRepository.findById(id).orElseThrow(
                () -> new IdNotFoundException("No checking account with that id"));
        if (savingsAcc.getStatus().equals(newStatus))
            throw new StatusException("The opportunity with id " + id + " is already " + newStatus);
        savingsAcc.setStatus(newStatus);
        savingsAccRepository.save(savingsAcc);
        return savingsAcc;
    }
}
