package com.ironhack.MidtermProject.controller.impl.account;

import com.ironhack.MidtermProject.controller.interfaces.account.CreditCardAccController;
import com.ironhack.MidtermProject.model.account.CreditCardAcc;
import com.ironhack.MidtermProject.service.account.CreditCardAccService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

@RestController
public class CreditCardAccControllerImpl implements CreditCardAccController {
    @Autowired
    private CreditCardAccService creditCardAccService;

    @GetMapping("/credit-card-accounts")
    @ResponseStatus(code = HttpStatus.OK)
    public List<CreditCardAcc> findAll(){ return creditCardAccService.findAll(); }

    @GetMapping("/credit-card-accounts/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public CreditCardAcc findById(@PathVariable Integer id){ return creditCardAccService.findById(id); }

    @PatchMapping("/credit-card-accounts/{id}/debit")
    @ResponseStatus(code = HttpStatus.OK)
    public void reduceBalance(@PathVariable Integer id, @RequestParam(name = "amount") BigDecimal amount,
                              @RequestParam (name = "currency", required = false) Currency currency){
        creditCardAccService.debitBalance(id, amount, currency);
    }
    @PatchMapping("/credit-card-accounts/{id}/credit")
    @ResponseStatus(code = HttpStatus.OK)
    public void addBalance(@PathVariable Integer id, @RequestParam(name = "amount") BigDecimal amount,
                              @RequestParam (name = "currency", required = false) Currency currency){
        creditCardAccService.creditBalance(id, amount, currency);
    }

    @PostMapping("/credit-card-accounts")
    @ResponseStatus(code = HttpStatus.CREATED)
    public CreditCardAcc store(@RequestBody CreditCardAcc creditCardAcc){
        // We have to create a new account to set the penaltyFee properly
        CreditCardAcc c1 = new CreditCardAcc(creditCardAcc.getPrimaryOwner(),creditCardAcc.getSecondaryOwner(),
        creditCardAcc.getBalance(),creditCardAcc.getCreditLimit(),creditCardAcc.getInterestRate());
        return creditCardAccService.create(c1);
    }

}
