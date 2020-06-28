package com.ironhack.MidtermProject.service.account;

import com.ironhack.MidtermProject.dto.CheckingAccCreation;
import com.ironhack.MidtermProject.enums.Role;
import com.ironhack.MidtermProject.enums.Status;
import com.ironhack.MidtermProject.enums.TransactionType;
import com.ironhack.MidtermProject.exceptions.IdNotFoundException;
import com.ironhack.MidtermProject.exceptions.NoOwnerException;
import com.ironhack.MidtermProject.exceptions.StatusException;
import com.ironhack.MidtermProject.model.account.Account;
import com.ironhack.MidtermProject.model.account.CheckingAcc;
import com.ironhack.MidtermProject.model.account.StudentCheckingAcc;
import com.ironhack.MidtermProject.model.classes.Money;
import com.ironhack.MidtermProject.model.classes.Transaction;
import com.ironhack.MidtermProject.model.user.AccountHolder;
import com.ironhack.MidtermProject.model.user.User;
import com.ironhack.MidtermProject.repository.account.CheckingAccRepository;
import com.ironhack.MidtermProject.repository.account.StudentCheckingAccRepository;
import com.ironhack.MidtermProject.repository.classes.TransactionRepository;
import com.ironhack.MidtermProject.repository.user.AccountHolderRepository;
import com.ironhack.MidtermProject.repository.user.ThirdPartyRepository;
import com.ironhack.MidtermProject.service.classes.TransactionService;
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
public class CheckingAccService extends AccountService {
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
    @Autowired
    private TransactionRepository transactionRepository;

    private static final Logger LOGGER = LogManager.getLogger(CheckingAccService.class);

    public List<CheckingAcc> findAll() {
        return checkingAccRepository.findAll();
    }

    public CheckingAcc checkFindById(Integer id, User user) {
        CheckingAcc checkingAcc = new CheckingAcc();
        switch (user.getRole()) {
            case ADMIN:
                checkingAcc = findById(id);
                LOGGER.info("ACCESS TO CHECKING ACCOUNT" + id + " . USER ORDER-ID : " + user.getId());
                break;
            case ACCOUNT_HOLDER:
                checkingAcc = findById(id);
                if ((checkingAcc.getPrimaryOwner() != null && checkingAcc.getPrimaryOwner().getId().equals(user.getId())) || (checkingAcc.getSecondaryOwner() != null && checkingAcc.getSecondaryOwner().getId().equals(user.getId()))) {
                    if (checkLoggedIn(user, checkingAcc)) {
                        LOGGER.info("ACCESS TO CHECKING ACCOUNT" + id + " . USER ORDER-ID : " + user.getId());
                        return checkingAcc;
                    } else
                        throw new StatusException("You are not logged in");
                } else throw new NoOwnerException("You are not the owner of this account");
            case THIRD_PARTY:
                throw new NoOwnerException("You are a third party. You are not the owner of this account");
        }
        return checkingAcc;
    }

    public CheckingAcc findById(Integer id) {
        CheckingAcc checkingAcc = checkingAccRepository.findById(id).orElseThrow(() -> new IdNotFoundException("Credit Card account not found with that id"));
        checkingAcc.updateDateInterestRate();
        checkingAccRepository.save(checkingAcc);
        return checkingAccRepository.findById(id).orElseThrow(() -> new IdNotFoundException("Checking account not found with that id"));
    }

    public CheckingAccCreation create(CheckingAccCreation checkingAccCreation) {
        // An account is needed to call the checkOwner method bus account is abstract. StudentCheckingAcc is the simplest one
        Account account = new StudentCheckingAcc(checkingAccCreation.getPrimaryOwner(),
                checkingAccCreation.getSecondaryOwner(), checkingAccCreation.getBalance(), Status.ACTIVE);
        AccountHolder[] owners = checkOwner(account);
        accountHolderRepository.save(owners[0]);
        if (checkingAccCreation.getSecondaryOwner() != null) {
            accountHolderRepository.save(owners[1]);
        }
        if (owners[0].getDateOfBirthday().after(new Date(System.currentTimeMillis() - 31556926000l * 24))) {
            StudentCheckingAcc studentCheckingAcc = new StudentCheckingAcc(owners[0], checkingAccCreation.getSecondaryOwner(),
                    checkingAccCreation.getBalance(), checkingAccCreation.getSecretKey(), checkingAccCreation.getStatus());
            studentCheckingAcc.setPrimaryOwner(owners[0]);
            if (checkingAccCreation.getSecondaryOwner() != null) {
                studentCheckingAcc.setSecondaryOwner(owners[1]);
            }
            studentCheckingAccRepository.save(studentCheckingAcc);
            checkingAccCreation.setType("StudentChecking Account");
        } else {
            CheckingAcc checkingAcc = new CheckingAcc(checkingAccCreation.getPrimaryOwner(), checkingAccCreation.getSecondaryOwner(),
                    checkingAccCreation.getBalance(), checkingAccCreation.getStatus());
            checkingAcc.setPrimaryOwner(owners[0]);
            if (checkingAccCreation.getSecondaryOwner() != null) {
                checkingAcc.setSecondaryOwner(owners[1]);
            }
            checkingAccRepository.save(checkingAcc);
            checkingAccCreation.setType("Checking Account");
        }
        checkingAccCreation.setPrimaryOwner(owners[0]);
        return checkingAccCreation;
    }

    @Transactional
    public void reduceBalance(User user, Integer id, BigDecimal amount, Currency currency, String secretKey, String header) {
        checkAllowance(user, id, secretKey, header);
        if (currency == null) {
            currency = Currency.getInstance("USD");
        }
        CheckingAcc checkingAcc = checkingAccRepository.findById(id).
                orElseThrow(() -> new IdNotFoundException("Checking account not found with that id"));

        Transaction transaction = new Transaction(user.getId(), null, checkingAcc, new Money(amount, currency), TransactionType.CREDIT);
        if (transactionService.checkTransaction(transaction)) {
            LOGGER.info("CREDIT TRANSACTION CHECKING ACCOUNT. USER ORDER-ID : " + user.getId());
            transactionRepository.save(transaction);
            checkingAcc.reduceBalance(new Money(amount, currency));
        } else {
            //fraud
            checkingAcc.setStatus(Status.FROZEN);
            checkingAccRepository.save(checkingAcc);
        }
        checkingAccRepository.save(checkingAcc);

    }

    @Transactional
    public void addBalance(User user, Integer id, BigDecimal amount, Currency currency, String secretKey, String header) {
        checkAllowance(user, id, secretKey, header);
        if (currency == null) {
            currency = Currency.getInstance("USD");
        }
        CheckingAcc checkingAcc = checkingAccRepository.findById(id).
                orElseThrow(() -> new IdNotFoundException("Checking account not found with thar id"));

        Transaction transaction = new Transaction(user.getId(), checkingAcc, null, new Money(amount, currency), TransactionType.DEBIT);
        if (transactionService.checkTransaction(transaction)) {
            LOGGER.info("DEBIT TRANSACTION CHECKING ACCOUNT. USER ORDER-ID : " + user.getId());
            transactionService.create(transaction);
            checkingAcc.addBalance(new Money(amount, currency));
        } else {
            //fraud
            checkingAcc.setStatus(Status.FROZEN);
            checkingAccRepository.save(checkingAcc);
        }
        checkingAccRepository.save(checkingAcc);
    }

    @Override
    public void checkAllowance(User user, Integer id, String secretKey, String header) {
        super.checkAllowance(user, id, secretKey, header);
        CheckingAcc checkingAcc = findById(id);
        if (user.getRole().equals(Role.THIRD_PARTY) && !secretKey.equals(checkingAcc.getSecretKey()))
            throw new NoOwnerException("The secret-key is incorrect for that account");
        if (checkingAcc.getStatus().equals(Status.FROZEN))
            throw new StatusException("This account is frozen");
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

    //    private void checkAllowance(User user, Integer id, String secretKey, String header) {
//        CheckingAcc checkingAcc = findById(id);
//        switch(user.getRole()){
//            case ADMIN:
//                break;
//            case ACCOUNT_HOLDER:
//                if((checkingAcc.getPrimaryOwner()!=null && checkingAcc.getPrimaryOwner().getId()== user.getId()) || (checkingAcc.getSecondaryOwner()!=null && checkingAcc.getSecondaryOwner().getId() == user.getId())){
//                    if(checkLoggedIn(user, checkingAcc)) {
//                        break;
//                    }
//                    else
//                        throw new StatusException("You are not logged in");
//                }
//                else throw new NoOwnerException("You are not the owner of this account");
//            case THIRD_PARTY:
//                ThirdParty thirdParty = thirdPartyRepository.findById(user.getId())
//                        .orElseThrow(()-> new IdNotFoundException("No third party found"));
//                if(header == null || secretKey == null)
//                    throw new NoOwnerException("You are a third party. You have to provide more info.");
//                else if(!PasswordUtility.passwordEncoder.matches(header, thirdParty.getHashKey()))
//                    throw new NoOwnerException("Your header is wrong");
//                else if(!secretKey.equals(checkingAcc.getSecretKey()))
//                    throw new NoOwnerException("The secret Key is incorrect for that account");
//                else
//                    break;
//        }
//        if(checkingAcc.getStatus().equals(Status.FROZEN))
//            throw new StatusException("This account is frozen");
//    }

}
