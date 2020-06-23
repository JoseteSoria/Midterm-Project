package com.ironhack.MidtermProject.service.account;

import com.ironhack.MidtermProject.exceptions.IdNotFoundException;
import com.ironhack.MidtermProject.model.account.CheckingAcc;
import com.ironhack.MidtermProject.model.account.StudentCheckingAcc;
import com.ironhack.MidtermProject.model.classes.Money;
import com.ironhack.MidtermProject.repository.account.StudentCheckingAccRepository;
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

    public List<StudentCheckingAcc> findAll(){ return studentCheckingAccRepository.findAll(); }

    public StudentCheckingAcc findById(Integer id) {
        return studentCheckingAccRepository.findById(id).orElseThrow(()-> new IdNotFoundException("Student Checking account not found with thar id"));
    }

    @Transactional
    public void debitBalance(Integer id, BigDecimal amount, Currency currency){
        if(currency == null){
            currency = Currency.getInstance("USD");
        }
        StudentCheckingAcc studentCheckingAcc = studentCheckingAccRepository.findById(id).
                orElseThrow(()-> new IdNotFoundException("Student Checking account not found with thar id"));
        studentCheckingAcc.debitBalance(new Money(amount, currency));
        studentCheckingAccRepository.save(studentCheckingAcc);
    }

    @Transactional
    public void creditBalance(Integer id, BigDecimal amount, Currency currency){
        if(currency == null){
            currency = Currency.getInstance("USD");
        }
        StudentCheckingAcc studentCheckingAcc = studentCheckingAccRepository.findById(id).
                orElseThrow(()-> new IdNotFoundException("Student Checking account not found with thar id"));
        studentCheckingAcc.creditBalance(new Money(amount, currency));
        studentCheckingAccRepository.save(studentCheckingAcc);
    }

}
