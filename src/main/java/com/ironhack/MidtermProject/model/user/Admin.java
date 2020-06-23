package com.ironhack.MidtermProject.model.user;

import javax.persistence.*;

@Entity
//@PrimaryKeyJoinColumn(name = "id")
public class Admin extends SecuredUser{
    public Admin() {}

    public Admin(String name, String username, String password) {
        super(name, username, password);
    }
}
