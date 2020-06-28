package com.ironhack.MidtermProject.service.account;

import com.ironhack.MidtermProject.enums.Role;
import com.ironhack.MidtermProject.enums.Status;
import com.ironhack.MidtermProject.enums.TransactionType;
import com.ironhack.MidtermProject.exceptions.IdNotFoundException;
import com.ironhack.MidtermProject.exceptions.NoOwnerException;
import com.ironhack.MidtermProject.exceptions.StatusException;
import com.ironhack.MidtermProject.model.account.SavingsAcc;
import com.ironhack.MidtermProject.model.classes.Money;
import com.ironhack.MidtermProject.model.classes.Transaction;
import com.ironhack.MidtermProject.model.user.AccountHolder;
import com.ironhack.MidtermProject.model.user.User;
import com.ironhack.MidtermProject.repository.account.SavingsAccRepository;
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
import java.util.List;

@Service
public class SavingsAccService extends AccountService {

    @Autowired
    private SavingsAccRepository savingsAccRepository;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private AccountHolderRepository accountHolderRepository;
    @Autowired
    private ThirdPartyRepository thirdPartyRepository;

    private static final Logger LOGGER = LogManager.getLogger(SavingsAccService.class);

    public List<SavingsAcc> findAll() {
        return savingsAccRepository.findAll();
    }

    public SavingsAcc checkFindById(Integer id, User user) {
        SavingsAcc savingsAcc = new SavingsAcc();
        switch (user.getRole()) {
            case ADMIN:
                savingsAcc = findById(id);
                LOGGER.info("ACCESS TO SAVINGS ACCOUNT" + id + " . USER ORDER-ID : " + user.getId());
                break;
            case ACCOUNT_HOLDER:
                savingsAcc = findById(id);
                if (savingsAcc.getPrimaryOwner().getId().equals(user.getId()) || savingsAcc.getSecondaryOwner().getId().equals(user.getId())) {
                    if (checkLoggedIn(user, savingsAcc)) {
                        LOGGER.info("ACCESS TO SAVINGS ACCOUNT" + id + " . USER ORDER-ID : " + user.getId());
                        return savingsAcc;
                    } else
                        throw new StatusException("You are not logged in");
                } else throw new NoOwnerException("You are not the owner of this account");
            case THIRD_PARTY:
                throw new NoOwnerException("You are a third party. You are not the owner of this account");
        }
        return savingsAcc;
    }

    public SavingsAcc findById(Integer id) {
        SavingsAcc savingsAcc = savingsAccRepository.findById(id).orElseThrow(() -> new IdNotFoundException("Savings account not found with that id"));
        savingsAcc.updateDateInterestRate();
        savingsAccRepository.save(savingsAcc);
        return savingsAccRepository.findById(id).orElseThrow(() -> new IdNotFoundException("Savings account not found with that id"));
    }

    public SavingsAcc create(SavingsAcc s1) {
        SavingsAcc savingsAcc = new SavingsAcc(s1.getPrimaryOwner(), s1.getSecondaryOwner(), s1.getBalance(),
                s1.getSecretKey(), s1.getMinimumBalance(), s1.getInterestRate(), s1.getStatus());
        AccountHolder[] owners = checkOwner(savingsAcc);
        accountHolderRepository.save(owners[0]);
        // Due to flushing issues it has to be repeated the condition to save the data properly
        if (savingsAcc.getSecondaryOwner() != null) {
            accountHolderRepository.save(owners[1]);
            savingsAcc.setSecondaryOwner(owners[1]);
        }
        savingsAcc.setPrimaryOwner(owners[0]);
        return savingsAccRepository.save(savingsAcc);
    }

    @Transactional
    public void reduceBalance(User user, Integer id, BigDecimal amount, Currency currency, String secretKey, String header) {
        checkAllowance(user, id, secretKey, header);
        if (currency == null) {
            currency = Currency.getInstance("USD");
        }
        SavingsAcc savingsAcc = savingsAccRepository.findById(id).
                orElseThrow(() -> new IdNotFoundException("Savings account not found with that id"));
        Transaction transaction = new Transaction(user.getId(), null, savingsAcc, new Money(amount, currency), TransactionType.CREDIT);
        if (transactionService.checkTransaction(transaction)) {
            LOGGER.info("CREDIT TRANSACTION SAVINGS ACCOUNT. USER ORDER-ID : " + user.getId());
            transactionService.create(transaction);
            savingsAcc.reduceBalance(new Money(amount, currency));
        } else {
            //fraud
            savingsAcc.setStatus(Status.FROZEN);
            savingsAccRepository.save(savingsAcc);
        }
        savingsAccRepository.save(savingsAcc);
    }

    @Transactional
    public void addBalance(User user, Integer id, BigDecimal amount, Currency currency, String secretKey, String header) {
        checkAllowance(user, id, secretKey, header);
        if (currency == null) {
            currency = Currency.getInstance("USD");
        }
        SavingsAcc savingsAcc = savingsAccRepository.findById(id).
                orElseThrow(() -> new IdNotFoundException("Savings account not found with thar id"));
        Transaction transaction = new Transaction(user.getId(), savingsAcc, null, new Money(amount, currency), TransactionType.DEBIT);
        if (transactionService.checkTransaction(transaction)) {
            LOGGER.info("DEBIT TRANSACTION SAVINGS ACCOUNT. USER ORDER-ID : " + user.getId());
            transactionService.create(transaction);
            savingsAcc.addBalance(new Money(amount, currency));
        } else {
            //fraud
            savingsAcc.setStatus(Status.FROZEN);
            savingsAccRepository.save(savingsAcc);
        }
        savingsAccRepository.save(savingsAcc);
    }

    @Override
    public void checkAllowance(User user, Integer id, String secretKey, String header) {
        super.checkAllowance(user, id, secretKey, header);
        SavingsAcc savingsAcc = findById(id);
        if (user.getRole().equals(Role.THIRD_PARTY) && !secretKey.equals(savingsAcc.getSecretKey()))
            throw new NoOwnerException("The secret-key is incorrect for that account");
        if (savingsAcc.getStatus().equals(Status.FROZEN))
            throw new StatusException("This account is frozen");
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
