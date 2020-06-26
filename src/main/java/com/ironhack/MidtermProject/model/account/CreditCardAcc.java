package com.ironhack.MidtermProject.model.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ironhack.MidtermProject.exceptions.NotEnoughMoneyException;
import com.ironhack.MidtermProject.helper.Helpers;
import com.ironhack.MidtermProject.model.classes.Money;
import com.ironhack.MidtermProject.model.user.AccountHolder;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
    public CreditCardAcc(AccountHolder primaryOwner, AccountHolder secondaryOwner) {
        super(primaryOwner, secondaryOwner, new Money(new BigDecimal("0")));
        this.creditLimit = new Money(new BigDecimal("100"));
        this.interestRate = new BigDecimal("0.2");
        this.dateInterestRate = new Date();
    }

    /**Constructor with everything**/
    public CreditCardAcc(AccountHolder primaryOwner, AccountHolder secondaryOwner, Money creditLimit, BigDecimal interestRate) {
        super(primaryOwner, secondaryOwner, new Money(new BigDecimal("0")));
        setCreditLimit(creditLimit);
        setInterestRate(interestRate);
        this.dateInterestRate = new Date();
    }

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
            BigDecimal monthlyInterestRate = (this.interestRate).divide(new BigDecimal("12"),4, RoundingMode.HALF_EVEN);
            try{
                for(int i = 0; i< months; i++){
                    this.addBalance(new Money(this.balance.getAmount().multiply(monthlyInterestRate)));
                }
            }catch (NotEnoughMoneyException e){
                this.balance = this.creditLimit;
            }
            finally{
                setDateInterestRate(new Date(this.getDateInterestRate().getTime()+(months*2629743000l)));
            }
        }
    }

    @Override
    public void reduceBalance(Money balance){
        if (balance.getCurrency()!= this.balance.getCurrency()){
            balance = Helpers.convertMoney(balance, this.balance);
        }
        this.balance.decreaseAmount(balance.getAmount());
    }

    @Override
    public void addBalance(Money balance){
        if (balance.getCurrency()!= this.balance.getCurrency()){
            balance = Helpers.convertMoney(balance, this.balance);
        }
        if(this.balance.getAmount().compareTo(creditLimit.getAmount())>=0){
            throw new NotEnoughMoneyException("Your balance has reach the credit limit.");
        }
        this.balance.increaseAmount(balance.getAmount());
        if(this.balance.getAmount().compareTo(creditLimit.getAmount())>0){
            this.getBalance().increaseAmount(this.getPenaltyFee().getAmount());
        }

    }


}
