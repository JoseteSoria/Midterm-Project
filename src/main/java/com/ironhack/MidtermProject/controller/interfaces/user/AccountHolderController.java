package com.ironhack.MidtermProject.controller.interfaces.user;

import com.ironhack.MidtermProject.dto.AccountMainFields;
import com.ironhack.MidtermProject.model.user.AccountHolder;
import com.ironhack.MidtermProject.model.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

public interface AccountHolderController {

    public List<AccountHolder> findAll();

    public AccountHolder findById(User user, Integer id);

    public List<AccountMainFields> findAllAccountById(User user, Integer id);

    public AccountHolder create(AccountHolder accountHolder);

    public void setLogged(User user, boolean loggedIn);

    public void transference(User user, Integer id, Integer receiverId, BigDecimal amount, Currency currency);

}
