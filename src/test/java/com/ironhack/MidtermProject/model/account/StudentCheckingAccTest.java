package com.ironhack.MidtermProject.model.account;

import com.ironhack.MidtermProject.enums.Status;
import com.ironhack.MidtermProject.model.classes.Address;
import com.ironhack.MidtermProject.model.classes.Money;
import com.ironhack.MidtermProject.model.user.AccountHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StudentCheckingAccTest {

    Address add1;
    Date d1, d2;
    AccountHolder ah1, ah2;
    StudentCheckingAcc ac1, ac2, ac3, ac4;

    @BeforeEach
    void setUp() {
        add1 = new Address("Spain", "Madrid", "Canal");
        d1 = Date.valueOf("1980-10-12");
        d2 = Date.valueOf("1982-05-18");
        ah1 = new AccountHolder("Juan", "juanito", "juanito", d1, add1, null);
        ah2 = new AccountHolder("Pedro", "pedrito", "pedrito", d2, add1, null);
        ac3 = new StudentCheckingAcc();
        ac1 = new StudentCheckingAcc(ah1, ah2, new Money(new BigDecimal("10000")), Status.ACTIVE);
        ac2 = new StudentCheckingAcc(ah1, ah2, new Money(new BigDecimal("10000")), "ES2394827394", Status.FROZEN);
    }

    @Test
    void generateKey_WellDone() {
        assertTrue(ac1.getSecretKey().contains("ES"));
    }

    @Test
    void setStatus_NullStatus_Active() {
        ac2.setStatus(null);
        assertEquals(Status.ACTIVE, ac2.getStatus());
    }

    @Test
    void setSecretKey() {
        ac2.setSecretKey("AM1237429");
        assertEquals("AM1237429", ac2.getSecretKey());
    }

}