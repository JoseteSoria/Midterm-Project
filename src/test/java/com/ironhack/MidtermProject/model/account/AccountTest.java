package com.ironhack.MidtermProject.model.account;

import com.ironhack.MidtermProject.enums.Status;
import com.ironhack.MidtermProject.exceptions.CurrencyTypeException;
import com.ironhack.MidtermProject.exceptions.NoOwnerException;
import com.ironhack.MidtermProject.exceptions.NotEnoughMoneyException;
import com.ironhack.MidtermProject.model.classes.Address;
import com.ironhack.MidtermProject.model.classes.Money;
import com.ironhack.MidtermProject.model.user.AccountHolder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AccountTest {

    Address add1;
    Date d1, d2;
    AccountHolder ah1, ah2;
    Account ac1;

    @BeforeEach
    void setUp() {
        add1 = new Address("Spain", "Madrid", "Canal");
        d1 = Date.valueOf("1980-10-12");
        d2 = Date.valueOf("1982-05-18");
        ah1 = new AccountHolder("Juan", "juanito", "juanito", d1, add1, null);
        ah2 = new AccountHolder("Pedro", "pedrito", "pedrito", d2, add1, null);
        ac1 = new StudentCheckingAcc(ah1, ah2, new Money(new BigDecimal("1000")), Status.ACTIVE);
    }

    @Test
    void setBalance_NullBalance() {
        ac1.setBalance(null);
        assertEquals(new BigDecimal("0.00"), ac1.getBalance().getAmount());
    }

    @Test
    void setOwners_NoPrimaryOwner_SecondOwnerAsPrimary() {
        Account ac2 = new StudentCheckingAcc(null, ah2, new Money(new BigDecimal("1000")), Status.ACTIVE);
        assertEquals("Pedro", ac2.getPrimaryOwner().getName());
    }

    @Test
    void setOwners_NoOwners_NoOwnerException() {
        assertThrows(NoOwnerException.class, () -> ac1.setOwners(null, null));
    }

    @Test
    void addBalance_Amount_Summed() {
        ac1.addBalance(new Money(new BigDecimal(100)));
        assertEquals(new BigDecimal("1100.00"), ac1.getBalance().getAmount());
    }

    @Test
    void addBalance_AmountEUR_SummedProperly() {
        ac1.addBalance(new Money(new BigDecimal(100), Currency.getInstance("EUR")));
        assertEquals(new BigDecimal("1112.00"), ac1.getBalance().getAmount());
    }

    @Test
    void addBalance_AmountUSDTOEUR_SummedProperly() {
        Account ac2 = new StudentCheckingAcc(ah1, ah2, new Money(new BigDecimal("1000"), Currency.getInstance("EUR")), Status.ACTIVE);
        ac2.addBalance(new Money(new BigDecimal(100)));
        assertEquals(new BigDecimal("1089.29"), ac2.getBalance().getAmount());
    }

    @Test
    void addBalance_AmountBadCurrency_Exception() {
        assertThrows(CurrencyTypeException.class, () -> ac1.addBalance(new Money(new BigDecimal(100), Currency.getInstance("INR"))));
    }

    @Test
    void reduceBalance_Amount_Reduced() {
        ac1.reduceBalance(new Money(new BigDecimal(100)));
        assertEquals(new BigDecimal("900.00"), ac1.getBalance().getAmount());
    }

    @Test
    void setPenaltyFee() {
        ac1.setPenaltyFee(new Money(new BigDecimal("35")));
        assertEquals(new BigDecimal("35.00"), ac1.getPenaltyFee().getAmount());
    }

    @Test
    void reduceBalance_NotEnoughAmount_Exception() {
        assertThrows(NotEnoughMoneyException.class, () -> ac1.reduceBalance(new Money(new BigDecimal(10000))));
    }

    @Test
    void reduceBalance_AmountEUR_ReducedProperly() {
        ac1.reduceBalance(new Money(new BigDecimal(100), Currency.getInstance("EUR")));
        assertEquals(new BigDecimal("888.00"), ac1.getBalance().getAmount());
    }
}