package com.ironhack.MidtermProject.service.user;


import com.ironhack.MidtermProject.model.user.Admin;
import com.ironhack.MidtermProject.repository.user.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    public List<Admin> findAll(){ return adminRepository.findAll(); }
}
