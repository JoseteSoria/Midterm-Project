package com.ironhack.MidtermProject.controller.impl.account;

import com.ironhack.MidtermProject.controller.interfaces.account.CreditCardAccController;
import com.ironhack.MidtermProject.model.account.CreditCardAcc;
import com.ironhack.MidtermProject.service.account.CreditCardAccService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CreditCardAccControllerImpl implements CreditCardAccController {
    @Autowired
    private CreditCardAccService creditCardAccService;

    @GetMapping("/creditCardAccounts")
    @ResponseStatus(code = HttpStatus.OK)
    public List<CreditCardAcc> findAll(){ return creditCardAccService.findAll(); }
}
