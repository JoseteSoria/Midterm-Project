package com.ironhack.MidtermProject.service.account;

import com.ironhack.MidtermProject.dto.CheckingAccCreation;
import com.ironhack.MidtermProject.exceptions.IdNotFoundException;
import com.ironhack.MidtermProject.model.account.CheckingAcc;
import com.ironhack.MidtermProject.model.account.StudentCheckingAcc;
import com.ironhack.MidtermProject.model.classes.Money;
import com.ironhack.MidtermProject.model.user.AccountHolder;
import com.ironhack.MidtermProject.repository.account.CheckingAccRepository;
import com.ironhack.MidtermProject.repository.account.StudentCheckingAccRepository;
import com.ironhack.MidtermProject.repository.user.AccountHolderRepository;
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

    public List<CheckingAcc> findAll(){ return checkingAccRepository.findAll(); }

    public CheckingAcc findById(Integer id) {
        return checkingAccRepository.findById(id).orElseThrow(()-> new IdNotFoundException("Checking account not found with thar id"));
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
        if(primOwner.getDateOfBirthday().after(new Date(System.currentTimeMillis()-31556926000l * 24))){
            StudentCheckingAcc studentCheckingAcc = new StudentCheckingAcc(primOwner, checkingAccCreation.getSecondaryOwner(),
                    checkingAccCreation.getBalance(),checkingAccCreation.getSecretKey(),checkingAccCreation.getStatus());
            studentCheckingAccRepository.save(studentCheckingAcc);
            checkingAccCreation.setType("StudentChecking Account");
        }
        else{
            CheckingAcc checkingAcc = new CheckingAcc(checkingAccCreation.getPrimaryOwner(),checkingAccCreation.getSecondaryOwner(),
                    checkingAccCreation.getBalance(),checkingAccCreation.getStatus());
            checkingAccRepository.save(checkingAcc);
            checkingAccCreation.setType("Checking Account");
        }
        return checkingAccCreation;
    }

    @Transactional
    public void debitBalance(Integer id, BigDecimal amount, Currency currency){
        if(currency == null){
            currency = Currency.getInstance("USD");
        }
        CheckingAcc checkingAcc = checkingAccRepository.findById(id).
                orElseThrow(()-> new IdNotFoundException("Checking account not found with thar id"));
        checkingAcc.debitBalance(new Money(amount, currency));
        checkingAccRepository.save(checkingAcc);
    }

    @Transactional
    public void creditBalance(Integer id, BigDecimal amount, Currency currency){
        if(currency == null){
            currency = Currency.getInstance("USD");
        }
        CheckingAcc checkingAcc = checkingAccRepository.findById(id).
                orElseThrow(()-> new IdNotFoundException("Checking account not found with thar id"));
        checkingAcc.creditBalance(new Money(amount, currency));
        checkingAccRepository.save(checkingAcc);
    }

}
