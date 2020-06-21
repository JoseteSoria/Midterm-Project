package com.ironhack.MidtermProject.service.account;

import com.ironhack.MidtermProject.model.account.CheckingAcc;
import com.ironhack.MidtermProject.repository.account.CheckingAccRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CheckingAccService {
    @Autowired
    private CheckingAccRepository checkingAccRepository;

    public List<CheckingAcc> findAll(){ return checkingAccRepository.findAll(); }

}
