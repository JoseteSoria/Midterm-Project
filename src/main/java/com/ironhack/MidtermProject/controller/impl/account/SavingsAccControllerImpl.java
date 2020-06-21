package com.ironhack.MidtermProject.controller.impl.account;

import com.ironhack.MidtermProject.controller.interfaces.account.SavingsAccController;
import com.ironhack.MidtermProject.model.account.SavingsAcc;
import com.ironhack.MidtermProject.service.account.SavingsAccService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SavingsAccControllerImpl implements SavingsAccController {
    @Autowired
    private SavingsAccService savingsAccService;

    @GetMapping("/savingsAccounts")
    @ResponseStatus(code = HttpStatus.OK)
    public List<SavingsAcc> findAll(){ return savingsAccService.findAll(); }
}
