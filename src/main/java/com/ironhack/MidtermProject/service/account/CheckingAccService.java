package com.ironhack.MidtermProject.service.account;

import com.ironhack.MidtermProject.dto.CheckingAccCreation;
import com.ironhack.MidtermProject.enums.Status;
import com.ironhack.MidtermProject.enums.TransactionType;
import com.ironhack.MidtermProject.exceptions.IdNotFoundException;
import com.ironhack.MidtermProject.exceptions.NoOwnerException;
import com.ironhack.MidtermProject.exceptions.StatusException;
import com.ironhack.MidtermProject.model.account.CheckingAcc;
import com.ironhack.MidtermProject.model.account.StudentCheckingAcc;
import com.ironhack.MidtermProject.model.classes.Money;
import com.ironhack.MidtermProject.model.classes.Transaction;
import com.ironhack.MidtermProject.model.user.AccountHolder;
import com.ironhack.MidtermProject.model.user.ThirdParty;
import com.ironhack.MidtermProject.model.user.User;
import com.ironhack.MidtermProject.repository.account.CheckingAccRepository;
import com.ironhack.MidtermProject.repository.account.StudentCheckingAccRepository;
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
import java.util.Date;
import java.util.List;

@Service
public class CheckingAccService {
    @Autowired
    private CheckingAccRepository checkingAccRepository;
    @Autowired
    private StudentCheckingAccRepository studentCheckingAccRepository;
    @Autowired
    private AccountHolderRepository accountHolderRepository;
    @Autowired
    private ThirdPartyRepository thirdPartyRepository;
    @Autowired
    private TransactionService transactionService;

    private static final Logger LOGGER = LogManager.getLogger(CheckingAccService.class);

    public List<CheckingAcc> findAll(){ return checkingAccRepository.findAll(); }

    public CheckingAcc checkFindById(Integer id, User user) {
        CheckingAcc checkingAcc = new CheckingAcc();
        switch(user.getRole()){
            case ADMIN:
                checkingAcc = findById(id);
                break;
            case ACCOUNT_HOLDER:
                checkingAcc = findById(id);
                if((checkingAcc.getPrimaryOwner()!=null && checkingAcc.getPrimaryOwner().getId()==user.getId()) || (checkingAcc.getSecondaryOwner()!=null && checkingAcc.getSecondaryOwner().getId() == user.getId())){
                    if(checkLoggedIn(user, checkingAcc))
                    {
                        return checkingAcc;
                    }
                    else
                        throw new StatusException("You are not logged in");
                }
                else throw new NoOwnerException("You are not the owner of this account");
            case THIRD_PARTY:
                throw new NoOwnerException("You are a third party. You are not the owner of this account");
        }
        return checkingAcc;
    }

    public CheckingAcc findById(Integer id) {
        return checkingAccRepository.findById(id).orElseThrow(()-> new IdNotFoundException("Checking account not found with that id"));
    }

    public CheckingAccCreation create(CheckingAccCreation checkingAccCreation){
        AccountHolder primOwner = new AccountHolder();
        if(checkingAccCreation.getPrimaryOwner().getId()!=null) {
            primOwner = accountHolderRepository.findById(checkingAccCreation.getPrimaryOwner().getId())
                    .orElseThrow(() -> new IdNotFoundException("Not primary Owner found with that id"));
        }
        else{
            primOwner =new AccountHolder(checkingAccCreation.getPrimaryOwner().getName(),checkingAccCreation.getPrimaryOwner().getUsername(),
                    checkingAccCreation.getPrimaryOwner().getPassword(), checkingAccCreation.getPrimaryOwner().getDateOfBirthday(),
                    checkingAccCreation.getPrimaryOwner().getPrimaryAddress(), checkingAccCreation.getPrimaryOwner().getMailingAddress());
        }
        accountHolderRepository.save(primOwner);
        if(primOwner.getDateOfBirthday().after(new Date(System.currentTimeMillis()-31556926000l * 24))){
            StudentCheckingAcc studentCheckingAcc = new StudentCheckingAcc(primOwner, checkingAccCreation.getSecondaryOwner(),
                    checkingAccCreation.getBalance(),checkingAccCreation.getSecretKey(),checkingAccCreation.getStatus());
            studentCheckingAcc.setPrimaryOwner(primOwner);
            studentCheckingAccRepository.save(studentCheckingAcc);
            checkingAccCreation.setType("StudentChecking Account");
        }
        else{
            CheckingAcc checkingAcc = new CheckingAcc(checkingAccCreation.getPrimaryOwner(),checkingAccCreation.getSecondaryOwner(),
                    checkingAccCreation.getBalance(),checkingAccCreation.getStatus());
            checkingAcc.setPrimaryOwner(primOwner);
            checkingAccRepository.save(checkingAcc);
            checkingAccCreation.setType("Checking Account");
        }
        return checkingAccCreation;
    }

    @Transactional
    public void reduceBalance(User user, Integer id, BigDecimal amount, Currency currency, String secretKey, String header){
        checkAllowance(user, id, secretKey, header);
        if(currency == null){
            currency = Currency.getInstance("USD");
        }
        CheckingAcc checkingAcc = checkingAccRepository.findById(id).
                orElseThrow(()-> new IdNotFoundException("Checking account not found with that id"));

        Transaction transaction = new Transaction(user.getId(), null, checkingAcc, new Money(amount, currency), TransactionType.CREDIT);
        if(transactionService.checkTransaction(transaction)){
            LOGGER.info("CREDIT TRANSACTION CHECKING ACCOUNT. USER ORDER-ID : " + user.getId());
            transactionService.create(transaction);
            checkingAcc.reduceBalance(new Money(amount, currency));
        }
        else{
            //fraud
            checkingAcc.setStatus(Status.FROZEN);
            checkingAccRepository.save(checkingAcc);
        }
        checkingAccRepository.save(checkingAcc);

    }

    @Transactional
    public void addBalance(User user, Integer id, BigDecimal amount, Currency currency, String secretKey, String header){
        checkAllowance(user, id, secretKey, header);
        if(currency == null){
            currency = Currency.getInstance("USD");
        }
        CheckingAcc checkingAcc = checkingAccRepository.findById(id).
                orElseThrow(()-> new IdNotFoundException("Checking account not found with thar id"));

        Transaction transaction = new Transaction(user.getId(), checkingAcc, null, new Money(amount, currency), TransactionType.DEBIT);
        if(transactionService.checkTransaction(transaction)){
            LOGGER.info("DEBIT TRANSACTION CHECKING ACCOUNT. USER ORDER-ID : " + user.getId());
            transactionService.create(transaction);
            checkingAcc.addBalance(new Money(amount, currency));
        }
        else{
            //fraud
            checkingAcc.setStatus(Status.FROZEN);
            checkingAccRepository.save(checkingAcc);
        }
        checkingAccRepository.save(checkingAcc);
    }


    private void checkAllowance(User user, Integer id, String secretKey, String header) {
        CheckingAcc checkingAcc = findById(id);
        switch(user.getRole()){
            case ADMIN:
                break;
            case ACCOUNT_HOLDER:
                if((checkingAcc.getPrimaryOwner()!=null && checkingAcc.getPrimaryOwner().getId()== user.getId()) || (checkingAcc.getSecondaryOwner()!=null && checkingAcc.getSecondaryOwner().getId() == user.getId())){
                    if(checkLoggedIn(user, checkingAcc)) {
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
                else if(!secretKey.equals(checkingAcc.getSecretKey()))
                    throw new NoOwnerException("The secret Key is incorrect for that account");
                else
                    break;
        }
        if(checkingAcc.getStatus().equals(Status.FROZEN))
            throw new StatusException("This account is frozen");
    }

    public boolean checkLoggedIn(User user, CheckingAcc checkingAcc){
        if((checkingAcc.getPrimaryOwner()!=null && (checkingAcc.getPrimaryOwner().getId() == user.getId()) && checkingAcc.getPrimaryOwner().isLoggedIn()) ||
                (checkingAcc.getSecondaryOwner()!=null && (checkingAcc.getSecondaryOwner().getId()== user.getId()) && checkingAcc.getSecondaryOwner().isLoggedIn()))
        {
            return true;
        }else
            return false;
    }

    public CheckingAcc changeStatus(Integer id, String status) {
        Status newStatus;
        try {
            newStatus = Status.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new StatusException("There's no status " + status.toUpperCase());
        }
        CheckingAcc checkingAcc = checkingAccRepository.findById(id).orElseThrow(
                () -> new IdNotFoundException("No checking account with that id"));
        if (checkingAcc.getStatus().equals(newStatus))
            throw new StatusException("The opportunity with id " + id + " is already " + newStatus);
        checkingAcc.setStatus(newStatus);
        checkingAccRepository.save(checkingAcc);
        return checkingAcc;

    }
}
