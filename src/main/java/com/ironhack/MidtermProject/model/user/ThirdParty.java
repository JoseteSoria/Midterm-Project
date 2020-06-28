package com.ironhack.MidtermProject.model.user;

import com.ironhack.MidtermProject.enums.Role;
import com.ironhack.MidtermProject.util.PasswordUtility;

import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.validation.constraints.NotNull;

@Entity
@PrimaryKeyJoinColumn(name = "id")
public class ThirdParty extends User {
    @NotNull
    private String hashKey;

    public ThirdParty() {
    }

    public ThirdParty(String name, String username, String password, String hashKey) {
        super(name, username, password, Role.THIRD_PARTY);
        this.hashKey = PasswordUtility.passwordEncoder.encode(hashKey);
    }

    public String getHashKey() {
        return hashKey;
    }

    public void setHashKey(String hashKey) {
        this.hashKey = hashKey;
    }
}
