package com.ironhack.MidtermProject.model.account;

import com.ironhack.MidtermProject.enums.Status;
import com.ironhack.MidtermProject.exceptions.NotEnoughMoneyException;
import com.ironhack.MidtermProject.model.classes.Address;
import com.ironhack.MidtermProject.model.classes.Money;
import com.ironhack.MidtermProject.model.user.AccountHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Date;

import static org.junit.jupiter.api.Assertions.*;

class SavingsAccTest {

    Address add1;
    Date d1, d2;
    AccountHolder ah1, ah2;
    SavingsAcc ac1, ac2, ac3, ac4;

    @BeforeEach
    void setUp() {
        add1 = new Address("Spain", "Madrid", "Canal");
        d1 = Date.valueOf("1980-10-12");
        d2 = Date.valueOf("1982-05-18");
        ah1 = new AccountHolder("Juan", "juanito", "juanito", d1, add1, null);
        ah2 = new AccountHolder("Pedro", "pedrito", "pedrito", d2, add1, null);
        ac3 = new SavingsAcc();
        ac1 = new SavingsAcc(ah1,ah2,new Money(new BigDecimal("10000")), Status.ACTIVE);
        ac2 = new SavingsAcc(ah1,ah2,new Money(new BigDecimal("30000")), new Money(new BigDecimal("1000")), new BigDecimal("0.5"), Status.FROZEN);
        ac4 = new SavingsAcc(ah1,ah2,new Money(new BigDecimal("40000")), "AS2382739232194", new Money(new BigDecimal("1000")), new BigDecimal("0.5"), Status.FROZEN);
    }

    @Test
    void generateKey_WellDone(){
        assertTrue(ac1.getSecretKey().contains("ES"));
    }

    @Test
    void setStatus_NullStatus_Active(){
        ac2.setStatus(null);
        assertEquals(Status.ACTIVE, ac2.getStatus());
    }

    @Test
    void reduceBalance_BelowMinimumBalance_Exception(){
        ac1.reduceBalance(new Money(new BigDecimal("9200")));
        assertThrows(NotEnoughMoneyException.class, ()-> ac1.reduceBalance(new Money(new BigDecimal(100))));
    }

    @Test
    void reduceBalance_NotEnoughAmount_Exception(){
        assertThrows(NotEnoughMoneyException.class, ()-> ac1.reduceBalance(new Money(new BigDecimal(20000))));
    }

    @Test
    void setMinimumBalance_Null_1000(){
        ac1.setMinimumBalance(null);
        assertEquals(new BigDecimal("1000.00"), ac1.getMinimumBalance().getAmount());
    }

    @Test
    void setMinimumBalance_OverLimits_Correct(){
        ac1.setMinimumBalance(new Money(new BigDecimal("2000")));
        assertEquals(new BigDecimal("1000.00"), ac1.getMinimumBalance().getAmount());
        ac1.setMinimumBalance(new Money(new BigDecimal("50")));
        assertEquals(new BigDecimal("100.00"), ac1.getMinimumBalance().getAmount());
    }

    @Test
    void setInterestRate_Null_00025(){
        ac1.setInterestRate(null);
        assertEquals(new BigDecimal("0.0025"), ac1.getInterestRate());
    }

    @Test
    void setInterestRate_OverLimits_Correct(){
        ac1.setInterestRate(new BigDecimal("0.6"));
        assertEquals(new BigDecimal("0.5"), ac1.getInterestRate());
        ac1.setInterestRate(new BigDecimal("0.002"));
        assertEquals(new BigDecimal("0.0025"), ac1.getInterestRate());
    }

    @Test
    void updateDateInterestRate_ChangeDate(){
        ac1.setInterestRate(new BigDecimal("0.3"));
        ac1.setDateInterestRate(Date.valueOf("2010-04-20"));
        BigDecimal originalMoney = ac1.getBalance().getAmount();
        ac1.updateDateInterestRate();
        assertTrue(ac1.getDateInterestRate().after(Date.valueOf("2010-04-20")));
        assertTrue(ac1.getBalance().getAmount().compareTo(originalMoney)>0);
    }


}