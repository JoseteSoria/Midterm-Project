package com.ironhack.MidtermProject.controller.impl.account;

import com.ironhack.MidtermProject.controller.interfaces.account.SavingsAccController;
import com.ironhack.MidtermProject.model.account.SavingsAcc;
import com.ironhack.MidtermProject.model.account.StudentCheckingAcc;
import com.ironhack.MidtermProject.model.user.User;
import com.ironhack.MidtermProject.service.account.SavingsAccService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

@RestController
public class SavingsAccControllerImpl implements SavingsAccController {
    @Autowired
    private SavingsAccService savingsAccService;

    @GetMapping("/savings-accounts")
    @ResponseStatus(code = HttpStatus.OK)
    public List<SavingsAcc> findAll(){ return savingsAccService.findAll(); }

    @GetMapping("/savings-accounts/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public SavingsAcc findById(@AuthenticationPrincipal User user, @PathVariable Integer id){
        return savingsAccService.checkFindById(id, user);
    }

    @PatchMapping("/savings-accounts/{id}/debit")
    @ResponseStatus(code = HttpStatus.OK)
    public void reduceBalance(@AuthenticationPrincipal User user, @PathVariable Integer id,
                              @RequestParam(name = "amount")BigDecimal amount,
                              @RequestParam (name = "currency", required = false) Currency currency,
                              @RequestParam(name = "secret-key", required = false) String secretKey,
                              @RequestHeader(name = "hash-key", required = false) String header){
        savingsAccService.addBalance(user, id, amount, currency, secretKey, header);
    }
    @PatchMapping("/savings-accounts/{id}/credit")
    @ResponseStatus(code = HttpStatus.OK)
    public void addBalance(@AuthenticationPrincipal User user, @PathVariable Integer id,
                           @RequestParam(name = "amount")BigDecimal amount,
                           @RequestParam (name = "currency", required = false) Currency currency,
                           @RequestParam(name = "secretKey", required = false) String secretKey,
                           @RequestHeader(required = false) String header){
        savingsAccService.reduceBalance(user, id, amount, currency, secretKey, header);
    }

    @PostMapping("/savings-accounts")
    @ResponseStatus(code = HttpStatus.CREATED)
    public SavingsAcc store(@RequestBody SavingsAcc savingsAcc){
        return savingsAccService.create(savingsAcc);
    }
    @PutMapping("/savings-accounts/{id}/set-status/{status}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public SavingsAcc changeStatus(@PathVariable Integer id, @PathVariable String status){
        return savingsAccService.changeStatus(id, status);
    }

}
