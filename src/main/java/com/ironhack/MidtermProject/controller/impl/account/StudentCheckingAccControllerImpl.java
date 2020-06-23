package com.ironhack.MidtermProject.controller.impl.account;

import com.ironhack.MidtermProject.controller.interfaces.account.StudentCheckingAccController;
import com.ironhack.MidtermProject.model.account.StudentCheckingAcc;
import com.ironhack.MidtermProject.service.account.StudentCheckingAccService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

@RestController
public class StudentCheckingAccControllerImpl implements StudentCheckingAccController {
    @Autowired
    private StudentCheckingAccService studentCheckingAccService;

    @GetMapping("/student-checking-accounts")
    @ResponseStatus(code = HttpStatus.OK)
    public List<StudentCheckingAcc> findAll(){ return studentCheckingAccService.findAll(); }

    @GetMapping("/student-checking-accounts/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public StudentCheckingAcc findById(@PathVariable Integer id){ return studentCheckingAccService.findById(id); }

    @PatchMapping("/student-checking-accounts/{id}/debit")
    @ResponseStatus(code = HttpStatus.OK)
    public void reduceBalance(@PathVariable Integer id, @RequestParam(name = "amount") BigDecimal amount,
                              @RequestParam (name = "currency", required = false) Currency currency){
        studentCheckingAccService.debitBalance(id, amount, currency);
    }

    @PatchMapping("/student-checking-accounts/{id}/credit")
    @ResponseStatus(code = HttpStatus.OK)
    public void addBalance(@PathVariable Integer id, @RequestParam(name = "amount") BigDecimal amount,
                              @RequestParam (name = "currency", required = false) Currency currency){
        studentCheckingAccService.creditBalance(id, amount, currency);
    }
}
