package com.ironhack.MidtermProject.model.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ironhack.MidtermProject.exceptions.NoOwnerException;
import com.ironhack.MidtermProject.exceptions.NotEnoughMoneyException;
import com.ironhack.MidtermProject.helper.Helpers;
import com.ironhack.MidtermProject.model.classes.Money;
import com.ironhack.MidtermProject.model.classes.Transaction;
import com.ironhack.MidtermProject.model.user.AccountHolder;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

//@MappedSuperclass
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer id;
    @ManyToOne
    protected AccountHolder primaryOwner;
    @ManyToOne
    protected AccountHolder secondaryOwner;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "balance_amount")),
            @AttributeOverride(name = "currency",column = @Column(name = "balance_currency")),
    })
    protected Money balance;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "penaltyFee_amount")),
            @AttributeOverride(name = "currency",column = @Column(name = "penaltyFee_currency")),
    })
    protected Money penaltyFee;

    @JsonIgnore
    @OneToMany(mappedBy = "senderAccount")
    public List<Transaction> transactionsAsSender;
    @JsonIgnore
    @OneToMany(mappedBy = "beneficiaryAccount")
    public List<Transaction> transactionsAsBeneficiary;

    public Account(){}

    public Account(AccountHolder primaryOwner, AccountHolder secondaryOwner, Money balance) {
        setOwners(primaryOwner,secondaryOwner);
        setBalance(balance);
        this.penaltyFee = new Money(new BigDecimal("40"));
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
        if(balance == null) this.balance = new Money(new BigDecimal("0"));
        else this.balance = balance;
    }

    public void setOwners(AccountHolder primaryOwner, AccountHolder secondaryOwner){
        if(primaryOwner!= null){
            setPrimaryOwner(primaryOwner);
            setSecondaryOwner(secondaryOwner);
        }
        else if(primaryOwner == null && secondaryOwner!= null){
            setPrimaryOwner(secondaryOwner);
            setSecondaryOwner(null);
        }
        else{
            throw new NoOwnerException("At least 1 Owner has to be provided");
        }
    }

    public AccountHolder getPrimaryOwner() {
        return primaryOwner;
    }

    public void setPrimaryOwner(AccountHolder primaryOwner) {
        this.primaryOwner = primaryOwner;
    }

    public AccountHolder getSecondaryOwner() {
        return secondaryOwner;
    }

    public void setSecondaryOwner(AccountHolder secondaryOwner) {
        this.secondaryOwner = secondaryOwner;
    }

    public void addBalance(Money balance){
        if (balance.getCurrency()!= this.balance.getCurrency()){
            balance = Helpers.convertMoney(balance, this.balance);
        }
        this.balance.increaseAmount(balance.getAmount());
    }

    public void reduceBalance(Money balance){
        if (balance.getCurrency()!= this.balance.getCurrency()){
            balance = Helpers.convertMoney(balance, this.balance);
        }
        if(this.balance.getAmount().compareTo(balance.getAmount())<0) {
            throw new NotEnoughMoneyException("There is not so much money");
        }
        this.balance.decreaseAmount(balance.getAmount());
    }
}
