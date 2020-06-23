package com.ironhack.MidtermProject.model.account;

import com.ironhack.MidtermProject.enums.Status;
import com.ironhack.MidtermProject.model.classes.Money;
import com.ironhack.MidtermProject.model.user.AccountHolder;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@PrimaryKeyJoinColumn(name = "id")
public class StudentCheckingAcc extends Account{
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Integer id;
    private String secretKey;
    @Enumerated(EnumType.STRING)
    private Status status;

    public StudentCheckingAcc() {}

    public StudentCheckingAcc(AccountHolder primaryOwner, AccountHolder secondaryOwner, Money balance, String secretKey, Status status) {
        super(primaryOwner, secondaryOwner, balance);
        this.secretKey = secretKey;
        this.status = status;
    }

//    public Integer getId() {
//        return id;
//    }
//
//    public void setId(Integer id) {
//        this.id = id;
//    }

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
