package com.ironhack.MidtermProject.controller.impl.account;

import com.ironhack.MidtermProject.controller.interfaces.account.CheckingAccController;
import com.ironhack.MidtermProject.model.account.CheckingAcc;
import com.ironhack.MidtermProject.service.account.CheckingAccService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CheckingAccControllerImpl implements CheckingAccController {
    @Autowired
    private CheckingAccService checkingAccService;

    @GetMapping("/checkingAccounts")
    @ResponseStatus(code = HttpStatus.OK)
    public List<CheckingAcc> findAll(){ return checkingAccService.findAll(); }
}
