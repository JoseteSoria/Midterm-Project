package com.ironhack.MidtermProject.model.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ironhack.MidtermProject.enums.Status;
import com.ironhack.MidtermProject.exceptions.NotEnoughMoneyException;
import com.ironhack.MidtermProject.model.classes.Money;
import com.ironhack.MidtermProject.model.user.AccountHolder;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;

@Entity
@PrimaryKeyJoinColumn(name = "id")
public class CheckingAcc extends Account{
    private String secretKey;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "minBalance_amount")),
            @AttributeOverride(name = "currency",column = @Column(name = "minBalance_currency")),
    })
    private Money minimumBalance;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "monthFee_amount")),
            @AttributeOverride(name = "currency",column = @Column(name = "monthFee_currency")),
    })
    private Money monthlyMaintenanceFee;
    @JsonIgnore
    private Date dateMonthlyMaintenance;
    @Enumerated(EnumType.STRING)
    private Status status;

    public CheckingAcc() {
        this.minimumBalance = new Money(new BigDecimal("250"));
        this.monthlyMaintenanceFee = new Money(new BigDecimal("12"));
        this.dateMonthlyMaintenance = new Date();
    }
    /**Constructor without monthlyMaintenanceFee nor minimumBalance nor secret Key**/
    public CheckingAcc(AccountHolder primaryOwner, AccountHolder secondaryOwner, Money balance, Status status) {
        super(primaryOwner, secondaryOwner, balance);
        this.secretKey = generateKey();
        this.minimumBalance = new Money(new BigDecimal("250"));
        this.monthlyMaintenanceFee = new Money(new BigDecimal("12"));
        setStatus(status);
        this.dateMonthlyMaintenance = new Date();
    }

    /**Constructor without secretKey**/
    public CheckingAcc(AccountHolder primaryOwner, AccountHolder secondaryOwner, Money balance, Money minimumBalance, Money monthlyMaintenanceFee, Status status) {
        super(primaryOwner, secondaryOwner, balance);
        this.secretKey = generateKey();
        this.minimumBalance = minimumBalance;
        this.monthlyMaintenanceFee = monthlyMaintenanceFee;
        setStatus(status);
        this.dateMonthlyMaintenance = new Date();
    }

    /**Constructor with everything**/
    public CheckingAcc(AccountHolder primaryOwner, AccountHolder secondaryOwner, Money balance, String secretKey, Money minimumBalance, Money monthlyMaintenanceFee, Status status) {
        super(primaryOwner, secondaryOwner, balance);
        setSecretKey(secretKey);
        this.minimumBalance = minimumBalance;
        this.monthlyMaintenanceFee = monthlyMaintenanceFee;
        this.status = status;
    }


    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String generateKey(){
//        Base64.getEncoder().encodeToString(LocalDateTime.now().toString().getBytes())
        String str = "ES";
        for(int i = 0; i<22; i++) {
            str += String.valueOf((int)(Math.random()*10));
        }
        return str;
    }

    public Money getMinimumBalance() {
        return minimumBalance;
    }

//    public void setMinimumBalance(Money minimumBalance) {
//        this.minimumBalance = minimumBalance;
//    }
//
//    public Money getMonthlyMaintenanceFee() {
//        return monthlyMaintenanceFee;
//    }
//
//    public void setMonthlyMaintenanceFee(Money monthlyMaintenanceFee) {
//        this.monthlyMaintenanceFee = monthlyMaintenanceFee;
//    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        if(status == null) this.status = Status.ACTIVE;
        else this.status = status;
    }

    public Date getDateMonthlyMaintenance() {
        return dateMonthlyMaintenance;
    }

    public void setDateMonthlyMaintenance(Date dateMonthlyMaintenance) {
        this.dateMonthlyMaintenance = dateMonthlyMaintenance;
    }

    @Override
    public void reduceBalance(Money balance){
        if(this.getBalance().getAmount().compareTo(this.getMinimumBalance().getAmount())<=0){
            throw new NotEnoughMoneyException("Your balance has reach the minimum balance");
        }
        super.reduceBalance(balance);
        if (this.getBalance().getAmount().compareTo(this.getMinimumBalance().getAmount())<0){
            this.getBalance().decreaseAmount(this.getPenaltyFee().getAmount());
        }
    }

    public void updateDateInterestRate() {
        // if more than a month
        if(this.dateMonthlyMaintenance.before(new Date(System.currentTimeMillis()-2629743000l ))) {
            // number of months
            Integer months = Integer.valueOf((int)((System.currentTimeMillis()-this.dateMonthlyMaintenance.getTime())/2629743000l));
            for(int i = 0; i< months; i++){
                this.reduceBalance(this.monthlyMaintenanceFee);
            }
            setDateMonthlyMaintenance(new Date(this.getDateMonthlyMaintenance().getTime()+(months*2629743000l)));
        }
    }
}
