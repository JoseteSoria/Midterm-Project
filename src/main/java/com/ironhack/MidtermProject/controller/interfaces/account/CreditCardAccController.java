package com.ironhack.MidtermProject.controller.interfaces.account;

import com.ironhack.MidtermProject.model.account.CreditCardAcc;
import com.ironhack.MidtermProject.model.user.User;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

public interface CreditCardAccController {
    public List<CreditCardAcc> findAll();

    public CreditCardAcc findById(User user, Integer id);

    public void addBalance(User user, Integer id, BigDecimal amount, Currency currency, String secretKey, String header);

    public void reduceBalance(User user, Integer id, BigDecimal amount, Currency currency, String secretKey, String header);

    public CreditCardAcc store(CreditCardAcc creditCardAcc);

}
