package com.ironhack.MidtermProject.model.account;

import com.ironhack.MidtermProject.enums.Status;
import com.ironhack.MidtermProject.model.classes.Money;
import com.ironhack.MidtermProject.model.user.AccountHolder;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name = "id")
public class StudentCheckingAcc extends Account {
    private String secretKey;
    @Enumerated(EnumType.STRING)
    private Status status;

    public StudentCheckingAcc() {
    }

    /**
     * Constructor without secretKey
     **/
    public StudentCheckingAcc(AccountHolder primaryOwner, AccountHolder secondaryOwner, Money balance, Status status) {
        super(primaryOwner, secondaryOwner, balance);
        this.secretKey = generateKey();
        setStatus(status);
    }

    /**
     * Constructor with everything
     **/
    public StudentCheckingAcc(AccountHolder primaryOwner, AccountHolder secondaryOwner, Money balance, String secretKey, Status status) {
        super(primaryOwner, secondaryOwner, balance);
        this.secretKey = secretKey;
        setStatus(status);
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

    public String generateKey() {
        String str = "ES";
        for (int i = 0; i < 22; i++) {
            str += String.valueOf((int) (Math.random() * 10));
        }
        return str;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        if (status == null) this.status = Status.ACTIVE;
        else this.status = status;
    }
}
