package com.ironhack.MidtermProject.controller.impl.account;

import com.ironhack.MidtermProject.controller.interfaces.account.StudentCheckingAccController;
import com.ironhack.MidtermProject.model.account.StudentCheckingAcc;
import com.ironhack.MidtermProject.service.account.StudentCheckingAccService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class StudentCheckingAccControllerImpl implements StudentCheckingAccController {
    @Autowired
    private StudentCheckingAccService studentCheckingAccService;

    @GetMapping("/studentCheckingAccounts")
    @ResponseStatus(code = HttpStatus.OK)
    public List<StudentCheckingAcc> findAll(){ return studentCheckingAccService.findAll(); }
}
