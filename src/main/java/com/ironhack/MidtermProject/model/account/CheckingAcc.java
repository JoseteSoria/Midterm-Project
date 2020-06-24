package com.ironhack.MidtermProject.model.account;

import com.ironhack.MidtermProject.enums.Status;
import com.ironhack.MidtermProject.exceptions.NotEnoughMoneyException;
import com.ironhack.MidtermProject.model.classes.Money;
import com.ironhack.MidtermProject.model.user.AccountHolder;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Base64;

@Entity
@PrimaryKeyJoinColumn(name = "id")
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
    /**Constructor without monthlyMaintenanceFee nor minimumBalance nor secret Key**/
    public CheckingAcc(AccountHolder primaryOwner, AccountHolder secondaryOwner, Money balance, Status status) {
        super(primaryOwner, secondaryOwner, balance);
        this.secretKey = generateKey();
        this.minimumBalance = new Money(new BigDecimal("250"));
        this.monthlyMaintenanceFee = new Money(new BigDecimal("12"));
        this.status = status;
    }

    /**Constructor with everything**/
    public CheckingAcc(AccountHolder primaryOwner, AccountHolder secondaryOwner, Money balance, String secretKey, Money minimumBalance, Money monthlyMaintenanceFee, Status status) {
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

    public String generateKey(){
        return Base64.getEncoder().encodeToString(LocalDateTime.now().toString().getBytes());
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

    @Override
    public void reduceBalance(Money balance){
        if(this.getBalance().getAmount().compareTo(this.getMinimumBalance().getAmount())<0){
            throw new NotEnoughMoneyException("Your balance is below the minimum balance");
        }
        super.reduceBalance(balance);
        if (this.getBalance().getAmount().compareTo(this.getMinimumBalance().getAmount())<0){
            this.getBalance().decreaseAmount(this.getPenaltyFee().getAmount());
        }
    }
}
