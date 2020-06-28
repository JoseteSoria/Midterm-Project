package com.ironhack.MidtermProject.controller.impl.account;

import com.ironhack.MidtermProject.controller.interfaces.account.AccountController;
import com.ironhack.MidtermProject.model.account.Account;
import com.ironhack.MidtermProject.service.account.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AccountControllerImpl implements AccountController {
    @Autowired
    private AccountService accountService;

    @GetMapping("/accounts")
    @ResponseStatus(code = HttpStatus.OK)
    public List<Account> findAll() {
        return accountService.findAllAccount();
    }
}
