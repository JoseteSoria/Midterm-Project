package com.ironhack.MidtermProject.model.user;

import com.ironhack.MidtermProject.enums.Role;

import javax.persistence.*;

@Entity
@PrimaryKeyJoinColumn(name = "id")
public class ThirdParty extends User{

    private String hashKey;

    public ThirdParty() {}

    public ThirdParty(String name, String username, String password, String hashKey) {
        super(name, username, password, Role.THIRD_PARTY);
        this.hashKey = hashKey;
    }

    public String getHashKey() {
        return hashKey;
    }

    public void setHashKey(String hashKey) {
        this.hashKey = hashKey;
    }
}
