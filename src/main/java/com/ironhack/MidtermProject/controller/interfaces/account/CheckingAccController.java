package com.ironhack.MidtermProject.controller.interfaces.account;

import com.ironhack.MidtermProject.dto.CheckingAccCreation;
import com.ironhack.MidtermProject.model.account.CheckingAcc;
import com.ironhack.MidtermProject.model.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

public interface CheckingAccController {

    public List<CheckingAcc> findAll();

    public CheckingAcc findById(User user, Integer id);

    public void addBalance(User user, Integer id, BigDecimal amount, Currency currency, String secretKey, String header);

    public void reduceBalance(User user, Integer id, BigDecimal amount, Currency currency, String secretKey, String header);

    public CheckingAccCreation store(CheckingAccCreation checkingAccCreation);

    public CheckingAcc changeStatus(Integer id, String status);

}
