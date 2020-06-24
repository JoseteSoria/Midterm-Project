package com.ironhack.MidtermProject.controller.impl.account;

import com.ironhack.MidtermProject.controller.interfaces.account.SavingsAccController;
import com.ironhack.MidtermProject.model.account.SavingsAcc;
import com.ironhack.MidtermProject.service.account.SavingsAccService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public SavingsAcc findById(@PathVariable Integer id){ return savingsAccService.findById(id); }

    @PatchMapping("/savings-accounts/{id}/debit")
    @ResponseStatus(code = HttpStatus.OK)
    public void reduceBalance(@PathVariable Integer id, @RequestParam(name = "amount") BigDecimal amount,
                              @RequestParam (name = "currency", required = false) Currency currency){
        savingsAccService.addBalance(id, amount, currency);
    }
    @PatchMapping("/savings-accounts/{id}/credit")
    @ResponseStatus(code = HttpStatus.OK)
    public void addBalance(@PathVariable Integer id, @RequestParam(name = "amount") BigDecimal amount,
                              @RequestParam (name = "currency", required = false) Currency currency){
        savingsAccService.reduceBalance(id, amount, currency);
    }

    @PostMapping("/savings-accounts")
    @ResponseStatus(code = HttpStatus.CREATED)
    public SavingsAcc store(@RequestBody SavingsAcc savingsAcc){
        // We have to create a new account to set the penaltyFee properly
        SavingsAcc s1 = new SavingsAcc(savingsAcc.getPrimaryOwner(),savingsAcc.getSecondaryOwner(), savingsAcc.getBalance(),
                savingsAcc.getSecretKey(),savingsAcc.getMinimumBalance(),savingsAcc.getInterestRate(),savingsAcc.getStatus());
        return savingsAccService.create(s1);
    }
}
