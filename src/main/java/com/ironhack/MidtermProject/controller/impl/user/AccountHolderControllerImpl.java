package com.ironhack.MidtermProject.controller.impl.user;

import com.ironhack.MidtermProject.controller.interfaces.user.AccountHolderController;
import com.ironhack.MidtermProject.dto.AccountMainFields;
import com.ironhack.MidtermProject.model.account.Account;
import com.ironhack.MidtermProject.model.user.AccountHolder;
import com.ironhack.MidtermProject.model.user.User;
import com.ironhack.MidtermProject.service.user.AccountHolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public AccountHolder findById(@AuthenticationPrincipal User user, @PathVariable Integer id){
        return accountHolderService.checkFindById(id, user);
    }

    @GetMapping("/account-holders/{id}/accounts")
    @ResponseStatus(code = HttpStatus.OK)
    public List<AccountMainFields> findAllAccountById(@AuthenticationPrincipal User user, @PathVariable Integer id){
        return accountHolderService.findAllAccountAsPrimaryOwnerById(id, user);
    }

    @PostMapping("/account-holders")
    @ResponseStatus(code = HttpStatus.CREATED)
    public AccountHolder create(@RequestBody AccountHolder accountHolder){
        return accountHolderService.store(accountHolder);
    }

    @PatchMapping("/account-holders/{id}/logged-in/{looggedIn}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void setLogged(@AuthenticationPrincipal User user, @PathVariable(name = "id") Integer id,
                          @PathVariable(name = "looggedIn") boolean loggedIn){
        accountHolderService.setLogged(user, id, loggedIn);
    }


    @PostMapping("/account-holders/transference/{id}")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public void transference(@AuthenticationPrincipal User user, @PathVariable Integer id,
                             @RequestParam(name = "receiver-account-id") Integer receiverId,
                             @RequestParam(name = "amount")BigDecimal amount,
                             @RequestParam (name = "currency", required = false) Currency currency){
        accountHolderService.prepareTransference(user, id, receiverId, amount, currency);
    }

}
