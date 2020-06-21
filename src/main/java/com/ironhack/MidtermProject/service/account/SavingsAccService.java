package com.ironhack.MidtermProject.service.account;

import com.ironhack.MidtermProject.model.account.SavingsAcc;
import com.ironhack.MidtermProject.repository.account.SavingsAccRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SavingsAccService {

    @Autowired
    private SavingsAccRepository savingsAccRepository;

    public List<SavingsAcc> findAll(){ return savingsAccRepository.findAll(); }
}
