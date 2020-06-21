package com.ironhack.MidtermProject.model.account;

import com.ironhack.MidtermProject.enums.Status;
import com.ironhack.MidtermProject.model.classes.Money;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
public class SavingsAcc extends Account{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
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

    public SavingsAcc() {
        this.interestRate = new BigDecimal("0.0025");
        this.minimumBalance = new Money(new BigDecimal("1000"));
    }

    /**Constructor without interestRate nor minimumBalance**/
    public SavingsAcc(String primaryOwner, String secondaryOwner, Money balance, String secretKey, Status status) {
        super(primaryOwner, secondaryOwner, balance);
        this.secretKey = secretKey;
        this.minimumBalance = new Money(new BigDecimal("1000"));
        this.interestRate = new BigDecimal("0.0025");
        this.status = status;
    }
    /**Constructor with everything**/
    public SavingsAcc(String primaryOwner, String secondaryOwner, Money balance, String secretKey, Money minimumBalance, BigDecimal interestRate, Status status) {
        super(primaryOwner, secondaryOwner, balance);
        this.secretKey = secretKey;
        setMinimumBalance(minimumBalance);
        setInterestRate(interestRate);
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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
        if(minimumBalance.getAmount().compareTo(new BigDecimal("1000"))>=0) {
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
        this.status = status;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        if(interestRate.compareTo(new BigDecimal("0,5")) >= 0){
            this.interestRate = new BigDecimal("0.5");
        }
        else{
            this.interestRate = interestRate;
        }
    }
}
