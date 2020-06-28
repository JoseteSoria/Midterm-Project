package com.ironhack.MidtermProject.model.account;

import com.ironhack.MidtermProject.exceptions.NotEnoughMoneyException;
import com.ironhack.MidtermProject.model.classes.Address;
import com.ironhack.MidtermProject.model.classes.Money;
import com.ironhack.MidtermProject.model.user.AccountHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

class CreditCardAccTest {

    Address add1;
    Date d1, d2;
    AccountHolder ah1, ah2;
    CreditCardAcc ac1, ac2, ac3;

    @BeforeEach
    void setUp() {
        add1 = new Address("Spain", "Madrid", "Canal");
        d1 = Date.valueOf("1980-10-12");
        d2 = Date.valueOf("1982-05-18");
        ah1 = new AccountHolder("Juan", "juanito", "juanito", d1, add1, null);
        ah2 = new AccountHolder("Pedro", "pedrito", "pedrito", d2, add1, null);
        ac3 = new CreditCardAcc();
        ac1 = new CreditCardAcc(ah1, ah2);
        ac2 = new CreditCardAcc(ah1, ah2, new Money(new BigDecimal("10000")), new BigDecimal("0.15"));
    }

    @Test
    void setCreditLimit_Over100000_100000() {
        ac1.setCreditLimit(new Money(new BigDecimal("3450000")));
        assertEquals(new BigDecimal("100000.00"), ac1.getCreditLimit().getAmount());
    }

    @Test
    void setCreditLimit_Under100_100() {
        ac1.setCreditLimit(new Money(new BigDecimal("50")));
        assertEquals(new BigDecimal("100.00"), ac1.getCreditLimit().getAmount());
    }

    @Test
    void setInterestRate_Over02_02() {
        ac1.setInterestRate(new BigDecimal("0.5"));
        assertEquals(new BigDecimal("0.2"), ac1.getInterestRate());
    }

    @Test
    void setInterestRate_Under01_01() {
        ac1.setInterestRate(new BigDecimal("0.05"));
        assertEquals(new BigDecimal("0.1"), ac1.getInterestRate());
    }

    @Test
    void updateDateInterestRate_ChangeDate() {
        ac1.setDateInterestRate(Date.valueOf("2020-04-20"));
        ac1.updateDateInterestRate();
        assertTrue(ac1.getDateInterestRate().after(Date.valueOf("2020-04-20")));
    }

    @Test
    void reduceBalance_Amount_Reduced() {
        ac1.reduceBalance(new Money(new BigDecimal(100)));
        assertEquals(new BigDecimal("-100.00"), ac1.getBalance().getAmount());
    }

    @Test
    void reduceBalance_AmountEUR_ReducedProperly() {
        ac1.reduceBalance(new Money(new BigDecimal(100), Currency.getInstance("EUR")));
        assertEquals(new BigDecimal("-112.00"), ac1.getBalance().getAmount());
    }

    @Test
    void addBalance_AmountEUR_SummedProperly() {
        ac1.addBalance(new Money(new BigDecimal(50), Currency.getInstance("EUR")));
        assertEquals(new BigDecimal("56.00"), ac1.getBalance().getAmount());
    }

    @Test
    void addBalance_AmountEURPlusPenaltyFee_SummedProperly() {
        ac1.addBalance(new Money(new BigDecimal(100), Currency.getInstance("EUR")));
        assertEquals(new BigDecimal("152.00"), ac1.getBalance().getAmount());
    }

    @Test
    void addBalance_AmountBadCurrency_Exception() {
        ac1.addBalance(new Money(new BigDecimal("100"), Currency.getInstance("EUR")));
        assertThrows(NotEnoughMoneyException.class, () -> ac1.addBalance(new Money(new BigDecimal("1"))));
    }

    @Test
    void updateDateInterestRate_ChangeDateLongTimeAgo_CreditLimit() {
        ac1.setInterestRate(new BigDecimal("0.18"));
        ac1.setCreditLimit(new Money(new BigDecimal("200")));
        ac1.setDateInterestRate(Date.valueOf("1990-04-20"));
        ac1.setBalance(new Money(new BigDecimal("10")));
        ac1.updateDateInterestRate();
        assertEquals(ac1.getCreditLimit().getAmount(), ac1.getBalance().getAmount());
    }
}