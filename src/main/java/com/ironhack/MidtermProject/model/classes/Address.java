package com.ironhack.MidtermProject.model.classes;

import javax.persistence.Embeddable;

@Embeddable
public class Address {
    // No tiene un @Id porque no va a ser una entidad en base de datos
    private String country;
    private String city;
    private String street;
    private Integer postalCode;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public Integer getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(Integer postal_code) {
        this.postalCode = postalCode;
    }
}

