package com.ironhack.MidtermProject.service.account;

import com.ironhack.MidtermProject.enums.Status;
import com.ironhack.MidtermProject.enums.TransactionType;
import com.ironhack.MidtermProject.exceptions.IdNotFoundException;
import com.ironhack.MidtermProject.model.account.StudentCheckingAcc;
import com.ironhack.MidtermProject.model.classes.Money;
import com.ironhack.MidtermProject.model.classes.Transaction;
import com.ironhack.MidtermProject.repository.account.StudentCheckingAccRepository;
import com.ironhack.MidtermProject.service.classes.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

@Service
public class StudentCheckingAccService {

    @Autowired
    private StudentCheckingAccRepository studentCheckingAccRepository;
    @Autowired
    private TransactionService transactionService;

    public List<StudentCheckingAcc> findAll(){ return studentCheckingAccRepository.findAll(); }

    public StudentCheckingAcc findById(Integer id) {
        return studentCheckingAccRepository.findById(id).orElseThrow(()-> new IdNotFoundException("Student Checking account not found with thar id"));
    }

    @Transactional
    public void reduceBalance(Integer id, BigDecimal amount, Currency currency){
        if(currency == null){
            currency = Currency.getInstance("USD");
        }
        StudentCheckingAcc studentCheckingAcc = studentCheckingAccRepository.findById(id).
                orElseThrow(()-> new IdNotFoundException("Student Checking account not found with that id"));
        Transaction transaction = new Transaction(id, null, new Money(amount, currency), TransactionType.CREDIT);
        if(transactionService.checkTransaction(transaction)){
            transactionService.create(transaction);
            studentCheckingAcc.reduceBalance(new Money(amount, currency));
        }
        else{
            //fraud
            studentCheckingAcc.setStatus(Status.FROZEN);
            studentCheckingAccRepository.save(studentCheckingAcc);
        }
        studentCheckingAccRepository.save(studentCheckingAcc);
    }

    @Transactional
    public void addBalance(Integer id, BigDecimal amount, Currency currency){
        if(currency == null){
            currency = Currency.getInstance("USD");
        }
        StudentCheckingAcc studentCheckingAcc = studentCheckingAccRepository.findById(id).
                orElseThrow(()-> new IdNotFoundException("Student Checking account not found with that id"));
        Transaction transaction = new Transaction(null, id, new Money(amount, currency), TransactionType.DEBIT);
        if(transactionService.checkTransaction(transaction)){
            transactionService.create(transaction);
            studentCheckingAcc.addBalance(new Money(amount, currency));
        }
        else{
            //fraud
            studentCheckingAcc.setStatus(Status.FROZEN);
            studentCheckingAccRepository.save(studentCheckingAcc);
        }
        studentCheckingAccRepository.save(studentCheckingAcc);
    }

}
