package com.ironhack.MidtermProject.service.account;

import com.ironhack.MidtermProject.enums.Role;
import com.ironhack.MidtermProject.enums.Status;
import com.ironhack.MidtermProject.enums.TransactionType;
import com.ironhack.MidtermProject.exceptions.IdNotFoundException;
import com.ironhack.MidtermProject.exceptions.NoOwnerException;
import com.ironhack.MidtermProject.exceptions.StatusException;
import com.ironhack.MidtermProject.model.account.StudentCheckingAcc;
import com.ironhack.MidtermProject.model.classes.Money;
import com.ironhack.MidtermProject.model.classes.Transaction;
import com.ironhack.MidtermProject.model.user.User;
import com.ironhack.MidtermProject.repository.account.StudentCheckingAccRepository;
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
public class StudentCheckingAccService extends AccountService {

    @Autowired
    private StudentCheckingAccRepository studentCheckingAccRepository;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private ThirdPartyRepository thirdPartyRepository;

    private static final Logger LOGGER = LogManager.getLogger(StudentCheckingAccService.class);

    public List<StudentCheckingAcc> findAll() {
        return studentCheckingAccRepository.findAll();
    }

    public StudentCheckingAcc checkFindById(Integer id, User user) {
        StudentCheckingAcc studentCheckingAcc = new StudentCheckingAcc();
        switch (user.getRole()) {
            case ADMIN:
                studentCheckingAcc = findById(id);
                break;
            case ACCOUNT_HOLDER:
                studentCheckingAcc = findById(id);
                if (studentCheckingAcc.getPrimaryOwner().getId().equals(user.getId()) || studentCheckingAcc.getSecondaryOwner().getId().equals(user.getId())) {
                    if (checkLoggedIn(user, studentCheckingAcc)) {
                        return studentCheckingAcc;
                    } else
                        throw new StatusException("You are not logged in");
                } else throw new NoOwnerException("You are not the owner of this account");
            case THIRD_PARTY:
                throw new NoOwnerException("You are a third party. You are not the owner of this account");
        }
        return studentCheckingAcc;
    }

    public StudentCheckingAcc findById(Integer id) {
        return studentCheckingAccRepository.findById(id).orElseThrow(() -> new IdNotFoundException("Student Checking account not found with that id"));
    }

    @Transactional
    public void reduceBalance(User user, Integer id, BigDecimal amount, Currency currency, String secretKey, String header) {
        checkAllowance(user, id, secretKey, header);
        if (currency == null) {
            currency = Currency.getInstance("USD");
        }
        StudentCheckingAcc studentCheckingAcc = studentCheckingAccRepository.findById(id).
                orElseThrow(() -> new IdNotFoundException("Student Checking account not found with that id"));
        Transaction transaction = new Transaction(user.getId(), null, studentCheckingAcc, new Money(amount, currency), TransactionType.CREDIT);
        if (transactionService.checkTransaction(transaction)) {
            LOGGER.info("CREDIT TRANSACTION STUDENT-CHECKING ACCOUNT. USER ORDER-ID : " + user.getId());
            transactionService.create(transaction);
            studentCheckingAcc.reduceBalance(new Money(amount, currency));
        } else {
            //fraud
            studentCheckingAcc.setStatus(Status.FROZEN);
            studentCheckingAccRepository.save(studentCheckingAcc);
        }
        studentCheckingAccRepository.save(studentCheckingAcc);
    }

    @Transactional
    public void addBalance(User user, Integer id, BigDecimal amount, Currency currency, String secretKey, String header) {
        checkAllowance(user, id, secretKey, header);
        if (currency == null) {
            currency = Currency.getInstance("USD");
        }
        StudentCheckingAcc studentCheckingAcc = studentCheckingAccRepository.findById(id).
                orElseThrow(() -> new IdNotFoundException("Student Checking account not found with that id"));
        Transaction transaction = new Transaction(user.getId(), studentCheckingAcc, null, new Money(amount, currency), TransactionType.DEBIT);
        if (transactionService.checkTransaction(transaction)) {
            LOGGER.info("DEBIT TRANSACTION STUDENT-CHECKING ACCOUNT. USER ORDER-ID : " + user.getId());
            transactionService.create(transaction);
            studentCheckingAcc.addBalance(new Money(amount, currency));
        } else {
            //fraud
            studentCheckingAcc.setStatus(Status.FROZEN);
            studentCheckingAccRepository.save(studentCheckingAcc);
        }
        studentCheckingAccRepository.save(studentCheckingAcc);
    }

    @Override
    public void checkAllowance(User user, Integer id, String secretKey, String header) {
        super.checkAllowance(user, id, secretKey, header);
        StudentCheckingAcc studentCheckingAcc = findById(id);
        if (user.getRole().equals(Role.THIRD_PARTY) && !secretKey.equals(studentCheckingAcc.getSecretKey()))
            throw new NoOwnerException("The secret-key is incorrect for that account");
        if (studentCheckingAcc.getStatus().equals(Status.FROZEN))
            throw new StatusException("This account is frozen");
    }


    public StudentCheckingAcc changeStatus(Integer id, String status) {
        Status newStatus;
        try {
            newStatus = Status.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new StatusException("There's no status " + status.toUpperCase());
        }
        StudentCheckingAcc studentCheckingAcc = studentCheckingAccRepository.findById(id).orElseThrow(
                () -> new IdNotFoundException("No checking account with that id"));
        if (studentCheckingAcc.getStatus().equals(newStatus))
            throw new StatusException("The opportunity with id " + id + " is already " + newStatus);
        studentCheckingAcc.setStatus(newStatus);
        studentCheckingAccRepository.save(studentCheckingAcc);
        return studentCheckingAcc;
    }
}
