package com.ironhack.MidtermProject.model.account;

import com.ironhack.MidtermProject.model.classes.Money;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
public class CreditCardAcc extends Account{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "creditLimit_amount")),
            @AttributeOverride(name="currency",column = @Column(name = "creditLimit_currency")),
    })
    private Money creditLimit;
    private BigDecimal interestRate;

    public CreditCardAcc() {}

    public CreditCardAcc(String primaryOwner, String secondaryOwner, Money balance, BigDecimal penaltyFee, Money creditLimit, BigDecimal interestRate) {
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

    public Money getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(Money creditLimit) {
        this.creditLimit = creditLimit;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }
}
