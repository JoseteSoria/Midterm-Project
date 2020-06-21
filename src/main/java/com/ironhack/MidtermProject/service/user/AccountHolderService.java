package com.ironhack.MidtermProject.service.user;

import com.ironhack.MidtermProject.model.user.AccountHolder;
import com.ironhack.MidtermProject.repository.user.AccountHolderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountHolderService {

    @Autowired
    private AccountHolderRepository accountHolderRepository;

    public List<AccountHolder> findAll(){ return accountHolderRepository.findAll(); }
}
