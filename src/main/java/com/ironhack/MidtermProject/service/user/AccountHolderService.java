package com.ironhack.MidtermProject.service.user;

import com.ironhack.MidtermProject.dto.AccountMainFields;
import com.ironhack.MidtermProject.enums.Status;
import com.ironhack.MidtermProject.enums.TransactionType;
import com.ironhack.MidtermProject.exceptions.FraudException;
import com.ironhack.MidtermProject.exceptions.IdNotFoundException;
import com.ironhack.MidtermProject.exceptions.NoOwnerException;
import com.ironhack.MidtermProject.exceptions.StatusException;
import com.ironhack.MidtermProject.model.account.*;
import com.ironhack.MidtermProject.model.classes.Money;
import com.ironhack.MidtermProject.model.classes.Transaction;
import com.ironhack.MidtermProject.model.user.AccountHolder;
import com.ironhack.MidtermProject.model.user.ThirdParty;
import com.ironhack.MidtermProject.model.user.User;
import com.ironhack.MidtermProject.repository.account.*;
import com.ironhack.MidtermProject.repository.user.AccountHolderRepository;
import com.ironhack.MidtermProject.service.account.StudentCheckingAccService;
import com.ironhack.MidtermProject.service.classes.TransactionService;
import com.ironhack.MidtermProject.util.PasswordUtility;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

@Service
public class AccountHolderService {

    @Autowired
    private AccountHolderRepository accountHolderRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private CheckingAccRepository checkingAccRepository;
    @Autowired
    private CreditCardAccRepository creditCardAccRepository;
    @Autowired
    private SavingsAccRepository savingsAccRepository;
    @Autowired
    private StudentCheckingAccRepository studentCheckingAccRepository;
    @Autowired
    private TransactionService transactionService;

    private static final Logger LOGGER = LogManager.getLogger(AccountHolderService.class);

    public List<AccountHolder> findAll() {
        return accountHolderRepository.findAll();
    }

    public AccountHolder checkFindById(Integer id, User user) {
        AccountHolder accountHolder = new AccountHolder();
        switch(user.getRole()){
            case ADMIN:
                accountHolder = findById(id);
                break;
            case ACCOUNT_HOLDER:
                accountHolder = findById(id);
                if(accountHolder.getId()==user.getId() || accountHolder.getId() == user.getId()){
                    break;
                }
                else throw new NoOwnerException("You are not the owner of this account");
            case THIRD_PARTY:
                throw new NoOwnerException("You are a third party. You are not the owner of this account");
        }
        return accountHolder;
    }

    public AccountHolder findById(Integer id) {
        return accountHolderRepository.findById(id).orElseThrow(() -> new IdNotFoundException("Account not found with the id provided"));
    }

    public AccountHolder store(AccountHolder accountHolder) {
        return accountHolderRepository.save(accountHolder);
    }


    public List<AccountMainFields> findAllAccountAsPrimaryOwnerById(Integer id, User user) {
        AccountHolder accountHolder = checkFindById(id, user);
        List<Object[]> objects = accountHolderRepository.findAccountByOwner(id);
        if (objects == null) throw new NoOwnerException("No accounts found");
        List<AccountMainFields> accountMainFieldsList = new ArrayList<>();
        for (Object[] objects1 : objects) {
            AccountMainFields accountMainFields = new AccountMainFields((Integer) objects1[0], accountHolder.getName(),
                    new Money((BigDecimal) objects1[1], Currency.getInstance((String) objects1[2])));
            accountMainFieldsList.add(accountMainFields);
        }
        return accountMainFieldsList;
    }

    @Transactional
    public boolean makeTransference(Transaction transaction) {
        Account senderId = transaction.getSenderAccount();
        Account receiverId = transaction.getBeneficiaryAccount();
        //Look up for account type for sender
        CheckingAcc senderCheckingAcc = checkingAccRepository.findById(senderId.getId()).orElse(null);
        StudentCheckingAcc senderStudentCheckingAcc = studentCheckingAccRepository.findById(senderId.getId()).orElse(null);
        CreditCardAcc senderCreditCardAcc = creditCardAccRepository.findById(senderId.getId()).orElse(null);
        SavingsAcc senderSavingsAcc = savingsAccRepository.findById(senderId.getId()).orElse(null);

        boolean transactionAllowedSender = false;

        if (senderCreditCardAcc != null) {
            transactionAllowedSender = transactionService.checkTransaction(transaction);
        } else {
            transactionAllowedSender = transactionService.checkTransaction(transaction);
        }


        //Sender
        if (senderCheckingAcc != null) {
            if(senderCheckingAcc.getStatus().equals(Status.FROZEN))
                throw new StatusException("Sender Account FROZEN");
            else if (transactionAllowedSender) {
                senderCheckingAcc.reduceBalance(transaction.getQuantity());
            } else {
                senderCheckingAcc.setStatus(Status.FROZEN);
            }
            checkingAccRepository.save(senderCheckingAcc);
        } else if (senderStudentCheckingAcc != null) {
            if(senderStudentCheckingAcc.getStatus().equals(Status.FROZEN))
                throw new StatusException("Sender Account FROZEN");
            else if (transactionAllowedSender) {
                senderStudentCheckingAcc.reduceBalance(transaction.getQuantity());
            } else {
                senderStudentCheckingAcc.setStatus(Status.FROZEN);
            }
            studentCheckingAccRepository.save(senderStudentCheckingAcc);
        } else if (senderCreditCardAcc != null) {
            if (transactionAllowedSender) {
                senderCreditCardAcc.reduceBalance(transaction.getQuantity());
            } else {
                throw new FraudException("You are committing fraud");
            }
            creditCardAccRepository.save(senderCreditCardAcc);
        } else if (senderSavingsAcc != null) {
            if(senderSavingsAcc.getStatus().equals(Status.FROZEN))
                throw new StatusException("Sender Account FROZEN");
            else if (transactionAllowedSender) {
                senderSavingsAcc.reduceBalance(transaction.getQuantity());
            } else {
                senderSavingsAcc.setStatus(Status.FROZEN);
            }
            savingsAccRepository.save(senderSavingsAcc);
        } else if (senderCheckingAcc == null && senderStudentCheckingAcc == null &&
                senderCreditCardAcc == null && senderSavingsAcc == null) {
            throw new IdNotFoundException("Sender account not found with that id");
        }

        if (transactionAllowedSender != true) {
            return transactionAllowedSender;
        }

        //Look up for account type for receiver
        CheckingAcc receiverCheckingAcc = checkingAccRepository.findById(receiverId.getId()).orElse(null);
        StudentCheckingAcc receiverStudentCheckingAcc = studentCheckingAccRepository.findById(receiverId.getId()).orElse(null);
        CreditCardAcc receiverCreditCardAcc = creditCardAccRepository.findById(receiverId.getId()).orElse(null);
        SavingsAcc receiverSavingsAcc = savingsAccRepository.findById(receiverId.getId()).orElse(null);

        //Receiver
        if (receiverCheckingAcc != null) {
            if(receiverCheckingAcc.getStatus().equals(Status.FROZEN))
                throw new StatusException("Receiver Account FROZEN");
            receiverCheckingAcc.addBalance(transaction.getQuantity());
            checkingAccRepository.save(receiverCheckingAcc);
        } else if (receiverStudentCheckingAcc != null) {
            if(receiverStudentCheckingAcc.getStatus().equals(Status.FROZEN))
                throw new StatusException("Receiver Account FROZEN");
            receiverStudentCheckingAcc.addBalance(transaction.getQuantity());
            studentCheckingAccRepository.save(receiverStudentCheckingAcc);
        } else if (receiverCreditCardAcc != null) {
            receiverCreditCardAcc.addBalance(transaction.getQuantity());
            creditCardAccRepository.save(receiverCreditCardAcc);
        } else if (receiverSavingsAcc != null) {
            if(receiverSavingsAcc.getStatus().equals(Status.FROZEN))
                throw new StatusException("Receiver Account FROZEN");
            receiverSavingsAcc.addBalance(transaction.getQuantity());
            savingsAccRepository.save(receiverSavingsAcc);
        } else if (receiverCheckingAcc == null && receiverStudentCheckingAcc == null &&
                receiverCreditCardAcc == null && receiverSavingsAcc == null) {
            throw new IdNotFoundException("Receiver account not found with that id");
        }
        return true;

    }

    public void prepareTransference(User user, Integer id, Integer receiverId, BigDecimal amount, Currency currency) {
        Money transferAmount = new Money();;
        if (currency == null) {
            transferAmount = new Money(amount);
        } else {
            transferAmount = new Money(amount, currency);
        }
        Account sender = accountRepository.findById(id).orElseThrow(()-> new IdNotFoundException("No sender account found"));
        Account receiver = accountRepository.findById(receiverId).orElseThrow(()-> new IdNotFoundException("No receiver account found"));
        if(!user.getId().equals(sender.getPrimaryOwner().getId())&&!user.getId().equals(sender.getSecondaryOwner().getId()))
            throw new NoOwnerException("You are not the owner of this account");
        Transaction transaction = new Transaction(user.getId(), sender, receiver, transferAmount, TransactionType.TRANSFERENCE);
        boolean transAllowed = makeTransference(transaction);
        if(!transAllowed)
            throw new StatusException("Check accounts status. Something went wrong. Fraud?");
        else{
            LOGGER.info("TRANSFERENCE TRANSACTION. USER ORDER-ID : " + user.getId());
            transactionService.create(transaction);
        }
    }

}
