package com.ironhack.MidtermProject.dto;

import com.ironhack.MidtermProject.model.classes.Money;

public class AccountMainFields {
    private Integer id;
    private String ownerName;
    private Money balance;

    public AccountMainFields() {
    }

    public AccountMainFields(Integer id, String ownerName, Money balance) {
        setId(id);
        setOwnerName(ownerName);
        setBalance(balance);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public Money getBalance() {
        return balance;
    }

    public void setBalance(Money balance) {
        this.balance = balance;
    }
}
