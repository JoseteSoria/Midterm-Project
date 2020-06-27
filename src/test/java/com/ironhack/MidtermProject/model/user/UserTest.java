package com.ironhack.MidtermProject.model.user;

import com.ironhack.MidtermProject.enums.Role;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    User u1, u2;
    @BeforeEach
    void setUp() {
        u2 = new User();
        u1 = new User("Simba", "reyleon", "reyleon", Role.ACCOUNT_HOLDER);
    }
}