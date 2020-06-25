package com.ironhack.MidtermProject.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ironhack.MidtermProject.enums.Role;
import com.ironhack.MidtermProject.model.account.Account;
import com.ironhack.MidtermProject.model.classes.Address;
import com.ironhack.MidtermProject.model.classes.Transaction;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@PrimaryKeyJoinColumn(name = "id")
public class AccountHolder extends User{
    private Date dateOfBirthday;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "country", column = @Column(name = "primary_country")),
            @AttributeOverride(name="city",column = @Column(name = "primary_city")),
            @AttributeOverride(name="street",column = @Column(name = "primary_street")),
            @AttributeOverride(name="postalCode",column = @Column(name = "primary_postal_code"))
    })
    private Address primaryAddress;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "country", column = @Column(name = "mailing_country")),
            @AttributeOverride(name="city",column = @Column(name = "mailing_city")),
            @AttributeOverride(name="street",column = @Column(name = "mailing_street")),
            @AttributeOverride(name="postalCode",column = @Column(name = "mailing_postal_code"))
    })
    private Address mailingAddress;

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
