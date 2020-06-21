package com.ironhack.MidtermProject.controller.impl.user;

import com.ironhack.MidtermProject.controller.interfaces.user.AccountHolderController;
import com.ironhack.MidtermProject.model.user.AccountHolder;
import com.ironhack.MidtermProject.service.user.AccountHolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AccountHolderControllerImpl implements AccountHolderController {

    @Autowired
    private AccountHolderService accountHolderService;

    @GetMapping("/accountHolders")
    @ResponseStatus(code = HttpStatus.OK)
    public List<AccountHolder> findAll(){ return accountHolderService.findAll(); }

}
