package com.ironhack.MidtermProject.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ironhack.MidtermProject.model.account.Account;
import com.ironhack.MidtermProject.model.classes.Address;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@PrimaryKeyJoinColumn(name = "id")
public class AccountHolder extends User{
    private LocalDate dateOfBirthday;
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
    @OneToMany(mappedBy = "accountHolder")
    public List<Account> accounts;

    public AccountHolder() {}

    public AccountHolder(String name, LocalDate dateOfBirthday, Address primaryAddress, Address mailingAddress) {
        super(name);
        this.dateOfBirthday = dateOfBirthday;
        this.primaryAddress = primaryAddress;
        this.mailingAddress = mailingAddress;
    }

    public LocalDate getDateOfBirthday() {
        return dateOfBirthday;
    }

    public void setDateOfBirthday(LocalDate dateOfBirthday) {
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

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }
}
