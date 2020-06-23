package com.ironhack.MidtermProject.controller.impl.user;

import com.ironhack.MidtermProject.controller.interfaces.user.AccountHolderController;
import com.ironhack.MidtermProject.dto.AccountMainFields;
import com.ironhack.MidtermProject.model.account.Account;
import com.ironhack.MidtermProject.model.user.AccountHolder;
import com.ironhack.MidtermProject.service.user.AccountHolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Currency;
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

    @PostMapping("/account-holders/transference/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void transference(@PathVariable Integer id, @RequestParam(name = "receiver-account-id") Integer receiver_id,
                             @RequestParam(name = "amount")BigDecimal amount, @RequestParam (name = "currency", required = false) Currency currency){
        accountHolderService.prepareTransference(id, receiver_id, amount, currency);
    }

}
