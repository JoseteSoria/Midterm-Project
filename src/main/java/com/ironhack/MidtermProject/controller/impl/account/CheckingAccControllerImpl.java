package com.ironhack.MidtermProject.controller.impl.account;

import com.ironhack.MidtermProject.controller.interfaces.account.CheckingAccController;
import com.ironhack.MidtermProject.dto.CheckingAccCreation;
import com.ironhack.MidtermProject.model.account.CheckingAcc;
import com.ironhack.MidtermProject.service.account.CheckingAccService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

@RestController
public class CheckingAccControllerImpl implements CheckingAccController {
    @Autowired
    private CheckingAccService checkingAccService;

    @GetMapping("/checking-accounts")
    @ResponseStatus(code = HttpStatus.OK)
    public List<CheckingAcc> findAll(){ return checkingAccService.findAll(); }

    @GetMapping("/checking-accounts/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public CheckingAcc findById(@PathVariable Integer id){
        return checkingAccService.findById(id);
    }

    @PatchMapping("/checking-accounts/{id}/debit")
    @ResponseStatus(code = HttpStatus.OK)
    public void reduceBalance(@PathVariable Integer id, @RequestParam(name = "amount")BigDecimal amount,
                              @RequestParam (name = "currency", required = false)Currency currency){
        checkingAccService.addBalance(id, amount, currency);
    }

    @PatchMapping("/checking-accounts/{id}/credit")
    @ResponseStatus(code = HttpStatus.OK)
    public void addBalance(@PathVariable Integer id, @RequestParam(name = "amount")BigDecimal amount,
                              @RequestParam (name = "currency", required = false)Currency currency){
        checkingAccService.reduceBalance(id, amount, currency);
    }

    @PostMapping("/checking-accounts")
    @ResponseStatus(code = HttpStatus.CREATED)
    public CheckingAccCreation store(@RequestBody CheckingAccCreation checkingAccCreation){
        return checkingAccService.create(checkingAccCreation);
    }
}
