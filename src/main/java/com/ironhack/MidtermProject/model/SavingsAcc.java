package com.ironhack.MidtermProject.model;

import com.ironhack.MidtermProject.enums.Status;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
public class SavingsAcc extends Account{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String secretKey;
    private BigDecimal minimumBalance;
    private BigDecimal interestRate;
    @Enumerated(EnumType.STRING)
    private Status status;

    public SavingsAcc() {}

    public SavingsAcc(String primaryOwner, String secondaryOwner, BigDecimal balance, BigDecimal penaltyFee, String secretKey, BigDecimal minimumBalance, BigDecimal interestRate, Status status) {
        super(primaryOwner, secondaryOwner, balance, penaltyFee);
        this.secretKey = secretKey;
        this.minimumBalance = minimumBalance;
        this.interestRate = interestRate;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public BigDecimal getMinimumBalance() {
        return minimumBalance;
    }

    public void setMinimumBalance(BigDecimal minimumBalance) {
        this.minimumBalance = minimumBalance;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }
}
