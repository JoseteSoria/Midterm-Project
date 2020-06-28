package com.ironhack.MidtermProject.model.user;

import com.ironhack.MidtermProject.model.classes.Address;
import org.junit.jupiter.api.BeforeEach;

import java.sql.Date;

class AccountHolderTest {

    Address add1;
    Date d1;
    AccountHolder u1, u2;

    @BeforeEach
    void setUp() {
        add1 = new Address("Spain", "Madrid", "Canal");
        d1 = Date.valueOf("1980-10-12");
        u2 = new AccountHolder();
        u1 = new AccountHolder("Simba", "reyleon", "reyleon", d1, add1, null);
    }
}