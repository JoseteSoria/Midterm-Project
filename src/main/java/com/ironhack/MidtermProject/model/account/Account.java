package com.ironhack.MidtermProject.model.account;

import com.ironhack.MidtermProject.model.classes.Money;
import com.ironhack.MidtermProject.model.user.AccountHolder;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Integer id;
    private String primaryOwner;
    private String secondaryOwner;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "balance_amount")),
            @AttributeOverride(name = "currency",column = @Column(name = "balance_currency")),
    })
    private Money balance;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "penaltyFee_amount")),
            @AttributeOverride(name = "currency",column = @Column(name = "penaltyFee_currency")),
    })
    private Money penaltyFee;

    @ManyToOne
    @JoinColumn(name = "account_holder_id")
    private AccountHolder accountHolder;


    public Account(){}

    public Account(String primaryOwner, String secondaryOwner, Money balance) {
        this.primaryOwner = primaryOwner;
        this.secondaryOwner = secondaryOwner;
        this.balance = balance;
        this.penaltyFee = new Money(new BigDecimal("40"));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public AccountHolder getAccountHolder() {
        return accountHolder;
    }

    public void setAccountHolder(AccountHolder accountHolder) {
        this.accountHolder = accountHolder;
    }
}
