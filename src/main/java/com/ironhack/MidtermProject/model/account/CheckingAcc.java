package com.ironhack.MidtermProject.model.account;

import com.ironhack.MidtermProject.enums.Status;
import com.ironhack.MidtermProject.model.classes.Money;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
public class CheckingAcc extends Account{
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Integer id;
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
    @Enumerated(EnumType.STRING)
    private Status status;

    public CheckingAcc() {
        this.minimumBalance = new Money(new BigDecimal("250"));
        this.monthlyMaintenanceFee = new Money(new BigDecimal("12"));
    }
    /**Constructor without monthlyMaintenanceFee nor minimumBalance**/
    public CheckingAcc(String primaryOwner, String secondaryOwner, Money balance, String secretKey, Status status) {
        super(primaryOwner, secondaryOwner, balance);
        this.secretKey = secretKey;
        this.minimumBalance = new Money(new BigDecimal("250"));
        this.monthlyMaintenanceFee = new Money(new BigDecimal("12"));
        this.status = status;
    }

    /**Constructor with everything**/
    public CheckingAcc(String primaryOwner, String secondaryOwner, Money balance, String secretKey, Money minimumBalance, Money monthlyMaintenanceFee, Status status) {
        super(primaryOwner, secondaryOwner, balance);
        this.secretKey = secretKey;
        this.minimumBalance = minimumBalance;
        this.monthlyMaintenanceFee = monthlyMaintenanceFee;
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

    public Money getMinimumBalance() {
        return minimumBalance;
    }

    public void setMinimumBalance(Money minimumBalance) {
        this.minimumBalance = minimumBalance;
    }

    public Money getMonthlyMaintenanceFee() {
        return monthlyMaintenanceFee;
    }

    public void setMonthlyMaintenanceFee(Money monthlyMaintenanceFee) {
        this.monthlyMaintenanceFee = monthlyMaintenanceFee;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
