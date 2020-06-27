package com.ironhack.MidtermProject.model.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ironhack.MidtermProject.enums.Status;
import com.ironhack.MidtermProject.exceptions.NotEnoughMoneyException;
import com.ironhack.MidtermProject.model.classes.Money;
import com.ironhack.MidtermProject.model.user.AccountHolder;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@PrimaryKeyJoinColumn(name = "id")
public class SavingsAcc extends Account{
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
    private BigDecimal interestRate;
    @Enumerated(EnumType.STRING)
    private Status status;
    @JsonIgnore
    public Date dateInterestRate;

    public SavingsAcc() {
        this.interestRate = new BigDecimal("0.0025");
        this.minimumBalance = new Money(new BigDecimal("1000"));
        this.dateInterestRate = new Date();
    }

    /**Constructor without interestRate nor minimumBalance or secretKey**/
    public SavingsAcc(AccountHolder primaryOwner, AccountHolder secondaryOwner, Money balance, Status status) {
        super(primaryOwner, secondaryOwner, balance);
        this.secretKey = generateKey();
        this.minimumBalance = new Money(new BigDecimal("1000"));
        this.interestRate = new BigDecimal("0.0025");
        this.status = status;
        this.dateInterestRate = new Date();
    }

    /**Constructor without secretKey**/
    public SavingsAcc(AccountHolder primaryOwner, AccountHolder secondaryOwner, Money balance, Money minimumBalance, BigDecimal interestRate, Status status) {
        super(primaryOwner, secondaryOwner, balance);
        this.secretKey = generateKey();
        setMinimumBalance(minimumBalance);
        setInterestRate(interestRate);
        setStatus(status);
        this.dateInterestRate = new Date();
    }

    /**Constructor with everything**/
    public SavingsAcc(AccountHolder primaryOwner, AccountHolder secondaryOwner, Money balance, String secretKey, Money minimumBalance, BigDecimal interestRate, Status status) {
        super(primaryOwner, secondaryOwner, balance);
        setSecretKey(secretKey);
        setMinimumBalance(minimumBalance);
        setInterestRate(interestRate);
        setStatus(status);
        this.dateInterestRate = new Date();
    }

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

    public Money getMinimumBalance() {
        return minimumBalance;
    }

    public void setMinimumBalance(Money minimumBalance) {
        if(minimumBalance == null){
            this.minimumBalance = new Money(new BigDecimal("1000"));
        }
        else if(minimumBalance.getAmount().compareTo(new BigDecimal("1000"))>=0) {
            this.minimumBalance = new Money(new BigDecimal("1000"));
        }else if(minimumBalance.getAmount().compareTo(new BigDecimal("100"))<=0) {
            this.minimumBalance = new Money(new BigDecimal("100"));
        }else {
            this.minimumBalance = minimumBalance;
        }
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        if(status == null) this.status = Status.ACTIVE;
        else this.status = status;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        if(interestRate == null){
            this.interestRate = new BigDecimal("0.0025");
        }
        else if(interestRate.compareTo(new BigDecimal("0.5")) >= 0){
            this.interestRate = new BigDecimal("0.5");
        }
        else if(interestRate.compareTo(new BigDecimal("0.0025")) <= 0){
            this.interestRate = new BigDecimal("0.0025");
        }
        else{
            this.interestRate = interestRate;
        }

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

    public Date getDateInterestRate() {
        return dateInterestRate;
    }

    public void setDateInterestRate(Date dateInterestRate) {
        this.dateInterestRate = dateInterestRate;
    }

    public void updateDateInterestRate(){
        if(this.dateInterestRate.before(new Date(System.currentTimeMillis()-31556926000l ))) {
            Integer years = Integer.valueOf((int)((System.currentTimeMillis()-this.dateInterestRate.getTime())/31556926000l));
                for (int i = 0; i < years; i++) {
                    this.addBalance(new Money(this.balance.getAmount().multiply(this.interestRate)));
                }
            setDateInterestRate(new Date(this.getDateInterestRate().getTime()+(years*31556926000l)));
        }
    }

}
