package com.ironhack.MidtermProject.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;

@Entity
public class CreditCardAcc extends Account{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private BigDecimal creditLimit;
    private BigDecimal interestRate;

    public CreditCardAcc() {}

    public CreditCardAcc(String primaryOwner, String secondaryOwner, BigDecimal balance, BigDecimal penaltyFee, BigDecimal creditLimit, BigDecimal interestRate) {
        super(primaryOwner, secondaryOwner, balance, penaltyFee);
        this.creditLimit = creditLimit;
        this.interestRate = interestRate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }
}
