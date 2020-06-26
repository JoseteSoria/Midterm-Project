package com.ironhack.MidtermProject.controller.impl.account;

import com.ironhack.MidtermProject.controller.interfaces.account.CreditCardAccController;
import com.ironhack.MidtermProject.model.account.CreditCardAcc;
import com.ironhack.MidtermProject.model.user.User;
import com.ironhack.MidtermProject.service.account.CreditCardAccService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public CreditCardAcc findById(@AuthenticationPrincipal User user, @PathVariable Integer id){
        return creditCardAccService.checkFindById(id,user);
    }

    @PatchMapping("/credit-card-accounts/{id}/debit")
    @ResponseStatus(code = HttpStatus.OK)
    public void addBalance(@AuthenticationPrincipal User user, @PathVariable Integer id,
                              @RequestParam(name = "amount")BigDecimal amount,
                              @RequestParam (name = "currency", required = false) Currency currency,
                              @RequestParam(name = "secret-key", required = false) String secretKey,
                              @RequestHeader(name = "hash-key", required = false) String header){
        creditCardAccService.debitBalance(user, id, amount, currency, secretKey, header);
    }
    @PatchMapping("/credit-card-accounts/{id}/credit")
    @ResponseStatus(code = HttpStatus.OK)
    public void reduceBalance(@AuthenticationPrincipal User user, @PathVariable Integer id,
                           @RequestParam(name = "amount")BigDecimal amount,
                           @RequestParam (name = "currency", required = false) Currency currency,
                           @RequestParam(name = "secret-key", required = false) String secretKey,
                           @RequestHeader(name = "hash-key", required = false) String header){
        creditCardAccService.creditBalance(user, id, amount, currency, secretKey, header);
    }

    @PostMapping("/credit-card-accounts")
    @ResponseStatus(code = HttpStatus.CREATED)
    public CreditCardAcc store(@RequestBody CreditCardAcc creditCardAcc){
        // We have to create a new account to set the penaltyFee properly
        CreditCardAcc c1 = new CreditCardAcc(creditCardAcc.getPrimaryOwner(),creditCardAcc.getSecondaryOwner(),
        creditCardAcc.getCreditLimit(),creditCardAcc.getInterestRate());
        return creditCardAccService.create(c1);
    }

}
