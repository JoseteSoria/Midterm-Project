package com.ironhack.MidtermProject.model.user;

import com.ironhack.MidtermProject.enums.Role;

import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

@Inheritance(strategy = InheritanceType.JOINED)
public class SecuredUser extends User{
    protected String password;

    public SecuredUser() {
    }

    public SecuredUser(String name, String username, Role role, String password) {
        super(name, username, role);
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
