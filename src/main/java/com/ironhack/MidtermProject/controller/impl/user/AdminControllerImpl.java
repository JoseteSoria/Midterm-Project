package com.ironhack.MidtermProject.controller.impl.user;
import com.ironhack.MidtermProject.controller.interfaces.user.AdminController;
import com.ironhack.MidtermProject.model.user.Admin;
import com.ironhack.MidtermProject.service.user.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AdminControllerImpl implements AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/admins")
    @ResponseStatus(code = HttpStatus.OK)
    public List<Admin> findAll(){ return adminService.findAll(); }
}
