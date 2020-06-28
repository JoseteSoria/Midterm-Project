package com.ironhack.MidtermProject.controller.impl.user;

import com.ironhack.MidtermProject.controller.interfaces.user.AdminController;
import com.ironhack.MidtermProject.model.user.Admin;
import com.ironhack.MidtermProject.service.user.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AdminControllerImpl implements AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/admins")
    @ResponseStatus(code = HttpStatus.OK)
    public List<Admin> findAll() {
        return adminService.findAll();
    }

    @PostMapping("/admins")
    @ResponseStatus(code = HttpStatus.CREATED)
    public Admin store(@RequestBody Admin admin) {
        return adminService.create(admin);
    }
}
