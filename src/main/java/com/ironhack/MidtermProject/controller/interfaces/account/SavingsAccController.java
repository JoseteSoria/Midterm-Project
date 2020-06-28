package com.ironhack.MidtermProject.controller.interfaces.account;

import com.ironhack.MidtermProject.model.account.SavingsAcc;
import com.ironhack.MidtermProject.model.user.User;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

public interface SavingsAccController {

    public List<SavingsAcc> findAll();

    public SavingsAcc findById(User user, Integer id);

    public void addBalance(User user, Integer id, BigDecimal amount, Currency currency, String secretKey, String header);

    public void reduceBalance(User user, Integer id, BigDecimal amount, Currency currency, String secretKey, String header);

    public SavingsAcc store(SavingsAcc savingsAcc);

    public SavingsAcc changeStatus(Integer id, String status);

}
