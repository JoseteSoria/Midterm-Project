package com.ironhack.MidtermProject.controller.interfaces.user;

import com.ironhack.MidtermProject.model.user.Admin;

import java.util.List;

public interface AdminController {

    public List<Admin> findAll();

    public Admin store(Admin admin);

}
