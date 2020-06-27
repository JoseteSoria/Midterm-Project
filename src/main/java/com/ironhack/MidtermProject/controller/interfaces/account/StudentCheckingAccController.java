package com.ironhack.MidtermProject.controller.interfaces.account;

import com.ironhack.MidtermProject.dto.CheckingAccCreation;
import com.ironhack.MidtermProject.model.account.CheckingAcc;
import com.ironhack.MidtermProject.model.account.StudentCheckingAcc;
import com.ironhack.MidtermProject.model.user.User;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

public interface StudentCheckingAccController {

    public List<StudentCheckingAcc> findAll();

    public StudentCheckingAcc findById(User user, Integer id);

    public void addBalance(User user, Integer id, BigDecimal amount, Currency currency, String secretKey, String header);

    public void reduceBalance(User user, Integer id, BigDecimal amount, Currency currency, String secretKey, String header);

    public StudentCheckingAcc changeStatus(Integer id, String status);

}
