package com.ironhack.MidtermProject.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ironhack.MidtermProject.enums.Role;
import com.ironhack.MidtermProject.model.account.Account;
import com.ironhack.MidtermProject.model.classes.Address;
import com.ironhack.MidtermProject.model.classes.Transaction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Entity
@PrimaryKeyJoinColumn(name = "id")
public class AccountHolder extends User{
    @NotNull
    private Date dateOfBirthday;

    @NotNull
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "country", column = @Column(name = "primary_country")),
            @AttributeOverride(name="city",column = @Column(name = "primary_city")),
            @AttributeOverride(name="street",column = @Column(name = "primary_street"))
    })
    private Address primaryAddress;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "country", column = @Column(name = "mailing_country")),
            @AttributeOverride(name="city",column = @Column(name = "mailing_city")),
            @AttributeOverride(name="street",column = @Column(name = "mailing_street"))
    })
    private Address mailingAddress;
    @NotNull
    private Boolean loggedIn;

    @JsonIgnore
    @OneToMany(mappedBy = "primaryOwner")
    public List<Account> accountsAsPrimaryOwner;
    @JsonIgnore
    @OneToMany(mappedBy = "secondaryOwner")
    public List<Account> accountsAsSecondaryOwner;

    public AccountHolder() {}

    public AccountHolder(String name, String username, String password, Date dateOfBirthday, Address primaryAddress, Address mailingAddress) {
        super(name, username, password, Role.ACCOUNT_HOLDER);
        this.dateOfBirthday = dateOfBirthday;
        this.primaryAddress = primaryAddress;
        this.mailingAddress = mailingAddress;
        this.loggedIn = false;
    }

    public Date getDateOfBirthday() {
        return dateOfBirthday;
    }

    public void setDateOfBirthday(Date dateOfBirthday) {
        this.dateOfBirthday = dateOfBirthday;
    }

    public Address getPrimaryAddress() {
        return primaryAddress;
    }

    public void setPrimaryAddress(Address primaryAddress) {
        this.primaryAddress = primaryAddress;
    }

    public Address getMailingAddress() {
        return mailingAddress;
    }

    public void setMailingAddress(Address mailingAddress) {
        this.mailingAddress = mailingAddress;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(Boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public List<Account> getAccountsAsPrimaryOwner() {
        return accountsAsPrimaryOwner;
    }

    public void setAccountsAsPrimaryOwner(List<Account> accountsAsPrimaryOwner) {
        this.accountsAsPrimaryOwner = accountsAsPrimaryOwner;
    }

    public List<Account> getAccountsAsSecondaryOwner() {
        return accountsAsSecondaryOwner;
    }

    public void setAccountsAsSecondaryOwner(List<Account> accountsAsSecondaryOwner) {
        this.accountsAsSecondaryOwner = accountsAsSecondaryOwner;
    }
}
