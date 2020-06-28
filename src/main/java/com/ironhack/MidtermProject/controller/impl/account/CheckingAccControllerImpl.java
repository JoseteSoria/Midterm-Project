package com.ironhack.MidtermProject.controller.impl.account;

import com.ironhack.MidtermProject.controller.interfaces.account.CheckingAccController;
import com.ironhack.MidtermProject.dto.CheckingAccCreation;
import com.ironhack.MidtermProject.model.account.CheckingAcc;
import com.ironhack.MidtermProject.model.user.User;
import com.ironhack.MidtermProject.service.account.CheckingAccService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public List<CheckingAcc> findAll() {
        return checkingAccService.findAll();
    }

    @GetMapping("/checking-accounts/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public CheckingAcc findById(@AuthenticationPrincipal User user, @PathVariable Integer id) {
        return checkingAccService.checkFindById(id, user);
    }

    @PatchMapping("/checking-accounts/{id}/credit")
    @ResponseStatus(code = HttpStatus.OK)
    public void addBalance(@AuthenticationPrincipal User user, @PathVariable Integer id,
                           @RequestParam(name = "amount") BigDecimal amount,
                           @RequestParam(name = "currency", required = false) Currency currency,
                           @RequestParam(name = "secret-key", required = false) String secretKey,
                           @RequestHeader(name = "hash-key", required = false) String header) {
        checkingAccService.addBalance(user, id, amount, currency, secretKey, header);
    }

    @PatchMapping("/checking-accounts/{id}/debit")
    @ResponseStatus(code = HttpStatus.OK)
    public void reduceBalance(@AuthenticationPrincipal User user, @PathVariable Integer id,
                              @RequestParam(name = "amount") BigDecimal amount,
                              @RequestParam(name = "currency", required = false) Currency currency,
                              @RequestParam(name = "secret-key", required = false) String secretKey,
                              @RequestHeader(name = "hash-key", required = false) String header) {
        checkingAccService.reduceBalance(user, id, amount, currency, secretKey, header);
    }

    @PostMapping("/checking-accounts")
    @ResponseStatus(code = HttpStatus.CREATED)
    public CheckingAccCreation store(@RequestBody CheckingAccCreation checkingAccCreation) {
        return checkingAccService.create(checkingAccCreation);
    }

    @PutMapping("/checking-accounts/{id}/set-status/{status}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public CheckingAcc changeStatus(@PathVariable Integer id, @PathVariable String status) {
        return checkingAccService.changeStatus(id, status);
    }
}
