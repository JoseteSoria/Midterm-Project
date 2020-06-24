package com.ironhack.MidtermProject.service.user;

import com.ironhack.MidtermProject.dto.AccountMainFields;
import com.ironhack.MidtermProject.enums.Status;
import com.ironhack.MidtermProject.enums.TransactionType;
import com.ironhack.MidtermProject.exceptions.FraudException;
import com.ironhack.MidtermProject.exceptions.IdNotFoundException;
import com.ironhack.MidtermProject.exceptions.NoOwnerException;
import com.ironhack.MidtermProject.model.account.*;
import com.ironhack.MidtermProject.model.classes.Money;
import com.ironhack.MidtermProject.model.classes.Transaction;
import com.ironhack.MidtermProject.model.user.AccountHolder;
import com.ironhack.MidtermProject.repository.account.CheckingAccRepository;
import com.ironhack.MidtermProject.repository.account.CreditCardAccRepository;
import com.ironhack.MidtermProject.repository.account.SavingsAccRepository;
import com.ironhack.MidtermProject.repository.account.StudentCheckingAccRepository;
import com.ironhack.MidtermProject.repository.user.AccountHolderRepository;
import com.ironhack.MidtermProject.service.classes.TransactionService;
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
    private CheckingAccRepository checkingAccRepository;
    @Autowired
    private CreditCardAccRepository creditCardAccRepository;
    @Autowired
    private SavingsAccRepository savingsAccRepository;
    @Autowired
    private StudentCheckingAccRepository studentCheckingAccRepository;
    @Autowired
    private TransactionService transactionService;

    public List<AccountHolder> findAll() {
        return accountHolderRepository.findAll();
    }

    public AccountHolder findById(Integer id) {
        return accountHolderRepository.findById(id).orElseThrow(() -> new IdNotFoundException("Account not found with the id provided"));
    }

    public AccountHolder store(AccountHolder accountHolder) {
        return accountHolderRepository.save(accountHolder);
    }


    public List<AccountMainFields> findAllAccountAsPrimaryOwnerById(Integer id) {
        AccountHolder accountHolder = findById(id);
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
        Integer senderId = transaction.getOrderingAccountId();
        Integer receiverId = transaction.getBeneficiaryAccountId();
        //Look up for account type for sender
        CheckingAcc senderCheckingAcc = checkingAccRepository.findById(senderId).orElse(null);
        StudentCheckingAcc senderStudentCheckingAcc = studentCheckingAccRepository.findById(senderId).orElse(null);
        CreditCardAcc senderCreditCardAcc = creditCardAccRepository.findById(senderId).orElse(null);
        SavingsAcc senderSavingsAcc = savingsAccRepository.findById(senderId).orElse(null);

        boolean transactionAllowedSender = false;

        if (senderCreditCardAcc != null) {
            transactionAllowedSender = transactionService.checkTransaction(transaction);
        } else {
            transactionAllowedSender = transactionService.checkTransaction(transaction);
        }


        //Sender
        if (senderCheckingAcc != null) {
            if (transactionAllowedSender) {
                senderCheckingAcc.reduceBalance(transaction.getQuantity());
            } else {
                senderCheckingAcc.setStatus(Status.FROZEN);
            }
            checkingAccRepository.save(senderCheckingAcc);
        } else if (senderStudentCheckingAcc != null) {
            if (transactionAllowedSender) {
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
            if (transactionAllowedSender) {
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
        CheckingAcc receiverCheckingAcc = checkingAccRepository.findById(receiverId).orElse(null);
        StudentCheckingAcc receiverStudentCheckingAcc = studentCheckingAccRepository.findById(receiverId).orElse(null);
        CreditCardAcc receiverCreditCardAcc = creditCardAccRepository.findById(receiverId).orElse(null);
        SavingsAcc receiverSavingsAcc = savingsAccRepository.findById(receiverId).orElse(null);

        //Receiver
        if (receiverCheckingAcc != null) {
            receiverCheckingAcc.addBalance(transaction.getQuantity());
            checkingAccRepository.save(receiverCheckingAcc);
        } else if (receiverStudentCheckingAcc != null) {
            receiverStudentCheckingAcc.addBalance(transaction.getQuantity());
            studentCheckingAccRepository.save(receiverStudentCheckingAcc);
        } else if (receiverCreditCardAcc != null) {
            receiverCreditCardAcc.addBalance(transaction.getQuantity());
            creditCardAccRepository.save(receiverCreditCardAcc);
        } else if (receiverSavingsAcc != null) {
            receiverSavingsAcc.addBalance(transaction.getQuantity());
            savingsAccRepository.save(receiverSavingsAcc);
        } else if (receiverCheckingAcc == null && receiverStudentCheckingAcc == null &&
                receiverCreditCardAcc == null && receiverSavingsAcc == null) {
            throw new IdNotFoundException("Receiver account not found with that id");
        }
        return true;

    }

    public void prepareTransference(Integer id, Integer receiverId, BigDecimal amount, Currency currency) {
        Money transferAmount = new Money();;
        if (currency == null) {
            transferAmount = new Money(amount);
        } else {
            transferAmount = new Money(amount, currency);
        }
        Transaction transaction = new Transaction(id, receiverId, transferAmount, TransactionType.TRANSFERENCE);
        boolean transAllowed = makeTransference(transaction);
    }
}
