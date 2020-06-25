package com.ironhack.MidtermProject.model.classes;

import com.ironhack.MidtermProject.enums.TransactionType;
import com.ironhack.MidtermProject.model.account.Account;
import com.ironhack.MidtermProject.model.user.AccountHolder;
import org.springframework.transaction.TransactionSuspensionNotSupportedException;

import javax.persistence.*;
import java.util.Date;

@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer orderingId;
    @ManyToOne
    private Account beneficiaryAccount;
    @ManyToOne
    private Account senderAccount;
    private Money quantity;
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;
    private Date date;

    public Transaction(){}

    public Transaction(Integer orderingId, Money quantity, TransactionType transactionType) {
        this.orderingId = orderingId;
        this.quantity = quantity;
        this.transactionType = transactionType;
        this.date = new Date();
    }

    public Transaction(Integer orderingId, Account beneficiaryAccount, Account senderAccount, Money quantity, TransactionType transactionType) {
        this.orderingId = orderingId;
        this.beneficiaryAccount = beneficiaryAccount;
        this.senderAccount = senderAccount;
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

    public Integer getOrderingId() {
        return orderingId;
    }

    public void setOrderingId(Integer orderingId) {
        this.orderingId = orderingId;
    }

    public Account getBeneficiaryAccount() {
        return beneficiaryAccount;
    }

    public void setBeneficiaryAccount(Account beneficiaryAccount) {
        this.beneficiaryAccount = beneficiaryAccount;
    }

    public Account getSenderAccount() {
        return senderAccount;
    }

    public void setSenderAccount(Account senderAccountId) {
        this.senderAccount = senderAccountId;
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
