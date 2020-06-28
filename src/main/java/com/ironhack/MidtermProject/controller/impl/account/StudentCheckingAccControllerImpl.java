package com.ironhack.MidtermProject.controller.impl.account;

import com.ironhack.MidtermProject.controller.interfaces.account.StudentCheckingAccController;
import com.ironhack.MidtermProject.model.account.StudentCheckingAcc;
import com.ironhack.MidtermProject.model.user.User;
import com.ironhack.MidtermProject.service.account.StudentCheckingAccService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public List<StudentCheckingAcc> findAll() {
        return studentCheckingAccService.findAll();
    }

    @GetMapping("/student-checking-accounts/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public StudentCheckingAcc findById(@AuthenticationPrincipal User user, @PathVariable Integer id) {
        return studentCheckingAccService.checkFindById(id, user);
    }

    @PatchMapping("/student-checking-accounts/{id}/credit")
    @ResponseStatus(code = HttpStatus.OK)
    public void reduceBalance(@AuthenticationPrincipal User user, @PathVariable Integer id,
                              @RequestParam(name = "amount") BigDecimal amount,
                              @RequestParam(name = "currency", required = false) Currency currency,
                              @RequestParam(name = "secret-key", required = false) String secretKey,
                              @RequestHeader(name = "hash-key", required = false) String header) {
        studentCheckingAccService.addBalance(user, id, amount, currency, secretKey, header);
    }

    @PatchMapping("/student-checking-accounts/{id}/debit")
    @ResponseStatus(code = HttpStatus.OK)
    public void addBalance(@AuthenticationPrincipal User user, @PathVariable Integer id,
                           @RequestParam(name = "amount") BigDecimal amount,
                           @RequestParam(name = "currency", required = false) Currency currency,
                           @RequestParam(name = "secret-key", required = false) String secretKey,
                           @RequestHeader(name = "hash-key", required = false) String header) {
        studentCheckingAccService.reduceBalance(user, id, amount, currency, secretKey, header);
    }

    @PutMapping("/student-checking-accounts/{id}/set-status/{status}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public StudentCheckingAcc changeStatus(@PathVariable Integer id, @PathVariable String status) {
        return studentCheckingAccService.changeStatus(id, status);
    }
}
