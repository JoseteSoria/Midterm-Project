package com.ironhack.MidtermProject.model.account;

import com.ironhack.MidtermProject.model.classes.Money;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
public class CreditCardAcc extends Account{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "creditLimit_amount")),
            @AttributeOverride(name = "currency",column = @Column(name = "creditLimit_currency")),
    })
    private Money creditLimit;
    private BigDecimal interestRate;

    public CreditCardAcc() {
        this.creditLimit = new Money(new BigDecimal("100"));
        this.interestRate = new BigDecimal("0.2");
    }

    /**Constructor without creditLimit nor interestRate**/
    public CreditCardAcc(String primaryOwner, String secondaryOwner, Money balance) {
        super(primaryOwner, secondaryOwner, balance);
        this.creditLimit = new Money(new BigDecimal("100"));
        this.interestRate = new BigDecimal("0.2");
    }

    /**Constructor with everything**/
    public CreditCardAcc(String primaryOwner, String secondaryOwner, Money balance, Money creditLimit, BigDecimal interestRate) {
        super(primaryOwner, secondaryOwner, balance);
        setCreditLimit(creditLimit);
        setInterestRate(interestRate);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Money getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(Money creditLimit) {
        if(creditLimit.getAmount().compareTo(new BigDecimal("100000"))>=0) {
            this.creditLimit = new Money(new BigDecimal("100000"));
        }else if(creditLimit.getAmount().compareTo(new BigDecimal("100"))<=0) {
            this.creditLimit = new Money(new BigDecimal("100"));
        }else {
            this.creditLimit = creditLimit;
        }
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        if(interestRate.compareTo(new BigDecimal("0.2"))>=0) {
            this.interestRate = new BigDecimal("0.2");
        }else if(interestRate.compareTo(new BigDecimal("0.1"))<=0) {
            this.interestRate = new BigDecimal("0.1");
        }else {
            this.interestRate = interestRate;
        }
    }
}
