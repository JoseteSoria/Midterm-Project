package com.ironhack.MidtermProject.model.account;

import com.ironhack.MidtermProject.enums.Status;
import com.ironhack.MidtermProject.model.classes.Money;
import com.ironhack.MidtermProject.model.user.AccountHolder;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@PrimaryKeyJoinColumn(name = "id")
public class StudentCheckingAcc extends Account{
    private String secretKey;
    @Enumerated(EnumType.STRING)
    private Status status;

    public StudentCheckingAcc() {}

    /**Constructor without secretKey**/
    public StudentCheckingAcc(AccountHolder primaryOwner, AccountHolder secondaryOwner, Money balance, Status status) {
        super(primaryOwner, secondaryOwner, balance);
        this.secretKey = generateKey();
        this.status = status;
    }
    /**Constructor with everything**/
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

    public String generateKey(){
        String str = "ES";
        for(int i = 0; i<22; i++) {
            str += String.valueOf((int)(Math.random()*10));
        }
        return str;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}
