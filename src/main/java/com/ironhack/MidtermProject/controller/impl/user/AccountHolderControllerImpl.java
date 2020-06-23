package com.ironhack.MidtermProject.controller.impl.user;

import com.ironhack.MidtermProject.controller.interfaces.user.AccountHolderController;
import com.ironhack.MidtermProject.dto.AccountMainFields;
import com.ironhack.MidtermProject.model.account.Account;
import com.ironhack.MidtermProject.model.user.AccountHolder;
import com.ironhack.MidtermProject.service.user.AccountHolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AccountHolderControllerImpl implements AccountHolderController {

    @Autowired
    private AccountHolderService accountHolderService;

    @GetMapping("/account-holders")
    @ResponseStatus(code = HttpStatus.OK)
    public List<AccountHolder> findAll(){ return accountHolderService.findAll(); }

    @GetMapping("/account-holders/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public AccountHolder findById(@PathVariable Integer id){ return accountHolderService.findById(id); }

    @GetMapping("/account-holders/{id}/accounts")
    @ResponseStatus(code = HttpStatus.OK)
    public List<AccountMainFields> findAllAccountById(@PathVariable Integer id){ return accountHolderService.findAllAccountAsPrimaryOwnerById(id); }


    @PostMapping("/account-holders")
    @ResponseStatus(code = HttpStatus.CREATED)
    public AccountHolder create(@RequestBody AccountHolder accountHolder){
        return accountHolderService.store(accountHolder);
    }

}
