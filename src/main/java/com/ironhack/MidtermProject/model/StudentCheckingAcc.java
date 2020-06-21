package com.ironhack.MidtermProject.model;

import com.ironhack.MidtermProject.enums.Status;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
public class StudentCheckingAcc extends Account{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String secretKey;
    @Enumerated(EnumType.STRING)
    private Status status;

    public StudentCheckingAcc() {}

    public StudentCheckingAcc(String primaryOwner, String secondaryOwner, BigDecimal balance, BigDecimal penaltyFee, String secretKey, Status status) {
        super(primaryOwner, secondaryOwner, balance, penaltyFee);
        this.secretKey = secretKey;
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
