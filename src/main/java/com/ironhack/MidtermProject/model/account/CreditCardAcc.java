package com.ironhack.MidtermProject.model.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ironhack.MidtermProject.model.classes.Money;
import com.ironhack.MidtermProject.model.user.AccountHolder;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@PrimaryKeyJoinColumn(name = "id")
public class CreditCardAcc extends Account{

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Integer id;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "creditLimit_amount")),
            @AttributeOverride(name = "currency",column = @Column(name = "creditLimit_currency")),
    })
    private Money creditLimit;
    private BigDecimal interestRate;
    @JsonIgnore
    public Date dateInterestRate;

    public CreditCardAcc() {
        this.creditLimit = new Money(new BigDecimal("100"));
        this.interestRate = new BigDecimal("0.2");
    }

    /**Constructor without creditLimit nor interestRate or dateInterestRate**/
    public CreditCardAcc(AccountHolder primaryOwner, AccountHolder secondaryOwner, Money balance) {
        super(primaryOwner, secondaryOwner, balance);
        this.creditLimit = new Money(new BigDecimal("100"));
        this.interestRate = new BigDecimal("0.2");
        this.dateInterestRate = new Date();
    }

    /**Constructor with everything**/
    public CreditCardAcc(AccountHolder primaryOwner, AccountHolder secondaryOwner, Money balance, Money creditLimit, BigDecimal interestRate) {
        super(primaryOwner, secondaryOwner, balance);
        setCreditLimit(creditLimit);
        setInterestRate(interestRate);
        this.dateInterestRate = new Date();
    }

//    public Integer getId() {
//        return id;
//    }
//
//    public void setId(Integer id) {
//        this.id = id;
//    }

    public Money getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(Money creditLimit) {
        if(creditLimit == null){
            this.creditLimit = new Money(new BigDecimal("100"));
        }
        else if(creditLimit.getAmount().compareTo(new BigDecimal("100000"))>=0) {
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
        if(interestRate == null){
            this.interestRate = new BigDecimal("0.2");
        }
        else if(interestRate.compareTo(new BigDecimal("0.2"))>=0) {
            this.interestRate = new BigDecimal("0.2");
        }else if(interestRate.compareTo(new BigDecimal("0.1"))<=0) {
            this.interestRate = new BigDecimal("0.1");
        }else {
            this.interestRate = interestRate;
        }
    }

    public Date getDateInterestRate() {
        return dateInterestRate;
    }

    public void setDateInterestRate(Date dateInterestRate) {
        this.dateInterestRate = dateInterestRate;
    }

    public void updateDateInterestRate(){
        // if more than a month
        if(this.dateInterestRate.before(new Date(System.currentTimeMillis()-2629743000l ))) {
            // number of months
            Integer months = Integer.valueOf((int)((System.currentTimeMillis()-this.dateInterestRate.getTime())/2629743000l));
            this.creditBalance(new Money(this.balance.getAmount().multiply(interestRate.multiply(new BigDecimal(months)).divide(new BigDecimal("12")))));
            setDateInterestRate(new Date());
        }
    }
}
