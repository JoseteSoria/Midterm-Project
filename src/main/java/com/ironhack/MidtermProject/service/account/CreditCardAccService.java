package com.ironhack.MidtermProject.service.account;

import com.ironhack.MidtermProject.model.account.CreditCardAcc;
import com.ironhack.MidtermProject.repository.account.CreditCardAccRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CreditCardAccService {

    @Autowired
    private CreditCardAccRepository creditCardAccRepository;

    public List<CreditCardAcc> findAll(){ return creditCardAccRepository.findAll(); }
}
