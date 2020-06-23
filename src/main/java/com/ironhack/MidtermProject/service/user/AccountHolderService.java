package com.ironhack.MidtermProject.service.user;

import com.ironhack.MidtermProject.dto.AccountMainFields;
import com.ironhack.MidtermProject.exceptions.IdNotFoundException;
import com.ironhack.MidtermProject.exceptions.NoOwnerException;
import com.ironhack.MidtermProject.model.account.Account;
import com.ironhack.MidtermProject.model.classes.Money;
import com.ironhack.MidtermProject.model.user.AccountHolder;
import com.ironhack.MidtermProject.repository.user.AccountHolderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

@Service
public class AccountHolderService {

    @Autowired
    private AccountHolderRepository accountHolderRepository;

    public List<AccountHolder> findAll(){ return accountHolderRepository.findAll(); }

    public AccountHolder findById(Integer id) {
        return accountHolderRepository.findById(id).orElseThrow(()-> new IdNotFoundException("Account not found with the id provided"));
    }

    public AccountHolder store(AccountHolder accountHolder) {
        return accountHolderRepository.save(accountHolder);
    }


    public List<AccountMainFields> findAllAccountAsPrimaryOwnerById(Integer id) {
        AccountHolder accountHolder = findById(id);
        List<Object[]> objects = accountHolderRepository.findAccountByOwner(id);
        if(objects == null) throw new NoOwnerException("No accounts found");
        List<AccountMainFields> accountMainFieldsList = new ArrayList<>();
        for(Object[] objects1: objects){
            AccountMainFields accountMainFields = new AccountMainFields((Integer)objects1[0],accountHolder.getName(),
                    new Money((BigDecimal)objects1[1], Currency.getInstance((String)objects1[2])));
            accountMainFieldsList.add(accountMainFields);
        }
        return accountMainFieldsList;
    }
}
