package com.ironhack.MidtermProject.service.user;

import com.ironhack.MidtermProject.dto.AccountMainFields;
import com.ironhack.MidtermProject.enums.TransactionType;
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
import com.ironhack.MidtermProject.repository.classes.TransactionRepository;
import com.ironhack.MidtermProject.repository.user.AccountHolderRepository;
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
    private TransactionRepository transactionRepository;

    public List<AccountHolder> findAll(){ return accountHolderRepository.findAll(); }

    public AccountHolder findById(Integer id) {
        return accountHolderRepository.findById(id).orElseThrow(()-> new IdNotFoundException("Account not found with the id provided"));
    }

    public AccountHolder store(AccountHolder accountHolder) {
        return accountHolderRepository.save(accountHolder);
    }


    public List<AccountMainFields> findAllAccountAsPrimaryOwnerById(Integer id) {
        AccountHolder accountHolder = findById(id);
        List<Object[]> objects = accountHolderRepository.findAccountByOwner(id);
        if(objects == null) throw new NoOwnerException("No accounts found");
        List<AccountMainFields> accountMainFieldsList = new ArrayList<>();
        for(Object[] objects1: objects){
            AccountMainFields accountMainFields = new AccountMainFields((Integer)objects1[0],accountHolder.getName(),
                    new Money((BigDecimal)objects1[1], Currency.getInstance((String)objects1[2])));
            accountMainFieldsList.add(accountMainFields);
        }
        return accountMainFieldsList;
    }

    @Transactional
    public void makeTransference(Transaction transaction){
        //Account senderAccount = accountHolderRepository.findAccountsById(transference.getSenderId());
        Integer senderId = transaction.getOrderingAccountId();
        Integer receiverId = transaction.getBeneficiaryAccountId();
        CheckingAcc senderCheckingAcc = checkingAccRepository.findById(senderId).orElse(null);
        StudentCheckingAcc senderStudentCheckingAcc = studentCheckingAccRepository.findById(senderId).orElse(null);
        CreditCardAcc senderCreditCardAcc = creditCardAccRepository.findById(senderId).orElse(null);
        SavingsAcc senderSavingsAcc = savingsAccRepository.findById(senderId).orElse(null);
        if(senderCheckingAcc!=null){
            senderCheckingAcc.debitBalance(transaction.getQuantity());
            checkingAccRepository.save(senderCheckingAcc);
        }
        else if(senderStudentCheckingAcc!=null){
            senderStudentCheckingAcc.debitBalance(transaction.getQuantity());
            studentCheckingAccRepository.save(senderStudentCheckingAcc);
        }
        else if(senderCreditCardAcc!=null){
            senderCreditCardAcc.debitBalance(transaction.getQuantity());
            creditCardAccRepository.save(senderCreditCardAcc);
        }
        else if(senderSavingsAcc!=null){
            senderSavingsAcc.debitBalance(transaction.getQuantity());
            savingsAccRepository.save(senderSavingsAcc);
        }
        else if (senderCheckingAcc == null && senderStudentCheckingAcc == null &&
                senderCreditCardAcc == null && senderSavingsAcc == null){
            throw new IdNotFoundException("Sender account not found with that id");
        }

        //Account receiverAccount = accountHolderRepository.findAccountsById(transference.getReceiverId());
        CheckingAcc receiverCheckingAcc = checkingAccRepository.findById(receiverId).orElse(null);
        StudentCheckingAcc receiverStudentCheckingAcc = studentCheckingAccRepository.findById(receiverId).orElse(null);
        CreditCardAcc receiverCreditCardAcc = creditCardAccRepository.findById(receiverId).orElse(null);
        SavingsAcc receiverSavingsAcc = savingsAccRepository.findById(receiverId).orElse(null);
        if(receiverCheckingAcc!=null){
            receiverCheckingAcc.creditBalance(transaction.getQuantity());
            checkingAccRepository.save(receiverCheckingAcc);
        }
        else if(receiverStudentCheckingAcc!=null){
            receiverStudentCheckingAcc.creditBalance(transaction.getQuantity());
            studentCheckingAccRepository.save(receiverStudentCheckingAcc);
        }
        else if(receiverCreditCardAcc!=null){
            receiverCreditCardAcc.creditBalance(transaction.getQuantity());
            creditCardAccRepository.save(receiverCreditCardAcc);
        }
        else if(receiverSavingsAcc!=null){
            receiverSavingsAcc.creditBalance(transaction.getQuantity());
            savingsAccRepository.save(receiverSavingsAcc);
        }
        else if (receiverCheckingAcc == null && receiverStudentCheckingAcc == null &&
                receiverCreditCardAcc == null && receiverSavingsAcc == null){
            throw new IdNotFoundException("Receiver account not found with that id");
        }
        transactionRepository.save(transaction);
    }

    public void prepareTransference(Integer id, Integer receiver_id, BigDecimal amount, Currency currency) {
        Money transferAmount = new Money();
        if(currency == null){
            transferAmount = new Money(amount);
        }
        else{
            transferAmount =  new Money(amount, currency);
        }
        Transaction transaction = new Transaction(id, receiver_id, transferAmount, TransactionType.TRANSFERENCE);
        makeTransference(transaction);
    }
}
