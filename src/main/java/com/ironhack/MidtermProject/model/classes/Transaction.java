package com.ironhack.MidtermProject.model.classes;

import com.ironhack.MidtermProject.enums.TransactionType;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer orderingAccountId;
    private Integer beneficiaryAccountId;
    private Money quantity;
    @Enumerated
    private TransactionType transactionType;
    private Date date;

    public Transaction(){}

    public Transaction(Integer orderingAccountId, Money quantity, TransactionType transactionType) {
        this.orderingAccountId = orderingAccountId;
        this.quantity = quantity;
        this.transactionType = transactionType;
        this.date = new Date();
    }

    public Transaction(Integer orderingAccountId, Integer beneficiaryAccountId, Money quantity, TransactionType transactionType) {
        this.orderingAccountId = orderingAccountId;
        this.beneficiaryAccountId = beneficiaryAccountId;
        this.quantity = quantity;
        this.transactionType = transactionType;
        this.date = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getOrderingAccountId() {
        return orderingAccountId;
    }

    public void setOrderingAccountId(Integer orderingAccountId) {
        this.orderingAccountId = orderingAccountId;
    }

    public Integer getBeneficiaryAccountId() {
        return beneficiaryAccountId;
    }

    public void setBeneficiaryAccountId(Integer beneficiaryAccountId) {
        this.beneficiaryAccountId = beneficiaryAccountId;
    }

    public Money getQuantity() {
        return quantity;
    }

    public void setQuantity(Money quantity) {
        this.quantity = quantity;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
