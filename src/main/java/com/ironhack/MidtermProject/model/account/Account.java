package com.ironhack.MidtermProject.model.account;

import com.ironhack.MidtermProject.model.classes.Money;

import javax.persistence.*;
import java.math.BigDecimal;

@MappedSuperclass
public abstract class Account {
    private String primaryOwner;
    private String secondaryOwner;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "balance_amount")),
            @AttributeOverride(name="currency",column = @Column(name = "balance_currency")),
    })
    private Money balance;
    private Money penaltyFee;


    public Account(){}

    public Account(String primaryOwner, String secondaryOwner, Money balance) {
        this.primaryOwner = primaryOwner;
        this.secondaryOwner = secondaryOwner;
        this.balance = balance;
        this.penaltyFee = new Money(new BigDecimal("40"));
    }

    public String getPrimaryOwner() {
        return this.primaryOwner;
    }

    public void setPrimaryOwner(String primaryOwner) {
        this.primaryOwner = primaryOwner;
    }

    public String getSecondaryOwner() {
        return this.secondaryOwner;
    }

    public void setSecondaryOwner(String secondaryOwner) {
        this.secondaryOwner = secondaryOwner;
    }

    public Money getPenaltyFee() {
        return penaltyFee;
    }

    public void setPenaltyFee(Money penaltyFee) {
        this.penaltyFee = penaltyFee;
    }

    public Money getBalance() {
        return balance;
    }

    public void setBalance(Money balance) {
        this.balance = balance;
    }
}
