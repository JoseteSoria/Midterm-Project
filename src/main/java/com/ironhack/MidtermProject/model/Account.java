package com.ironhack.MidtermProject.model;

import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;
import java.math.BigDecimal;

@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Account {
    private String PrimaryOwner;
    private String SecondaryOwner;
    private Double penaltyFee;
    private BigDecimal balance;

    public Account(){}

    public Account(String primaryOwner, String secondaryOwner, Double penaltyFee, BigDecimal balance) {
        PrimaryOwner = primaryOwner;
        SecondaryOwner = secondaryOwner;
        this.penaltyFee = penaltyFee;
        this.balance = balance;
    }

    public String getPrimaryOwner() {
        return PrimaryOwner;
    }

    public void setPrimaryOwner(String primaryOwner) {
        PrimaryOwner = primaryOwner;
    }

    public String getSecondaryOwner() {
        return SecondaryOwner;
    }

    public void setSecondaryOwner(String secondaryOwner) {
        SecondaryOwner = secondaryOwner;
    }

    public Double getPenaltyFee() {
        return penaltyFee;
    }

    public void setPenaltyFee(Double penaltyFee) {
        this.penaltyFee = penaltyFee;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
