package com.ironhack.MidtermProject.service.account;

import com.ironhack.MidtermProject.model.account.StudentCheckingAcc;
import com.ironhack.MidtermProject.repository.account.StudentCheckingAccRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentCheckingAccService {

    @Autowired
    private StudentCheckingAccRepository studentCheckingAccRepository;

    public List<StudentCheckingAcc> findAll(){ return studentCheckingAccRepository.findAll(); }
}
