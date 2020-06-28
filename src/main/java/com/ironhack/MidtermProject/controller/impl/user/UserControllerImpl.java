package com.ironhack.MidtermProject.controller.impl.user;

import com.ironhack.MidtermProject.controller.interfaces.user.UserController;
import com.ironhack.MidtermProject.model.user.User;
import com.ironhack.MidtermProject.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserControllerImpl implements UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/users")
    @ResponseStatus(code = HttpStatus.OK)
    public List<User> findAll() {
        return userService.findAll();
    }

}
