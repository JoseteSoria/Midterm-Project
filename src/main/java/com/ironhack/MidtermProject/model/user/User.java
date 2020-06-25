package com.ironhack.MidtermProject.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ironhack.MidtermProject.enums.Role;
import com.ironhack.MidtermProject.util.PasswordUtility;

import javax.persistence.*;
import javax.validation.constraints.NotNull;


//@MappedSuperclass
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer id;
    @NotNull
    protected String name;
    @NotNull
    protected String username;
    @NotNull
    protected String password;

    @NotNull
    @Enumerated(EnumType.STRING)
    protected Role role;

    public User() {}

    public User(String name, String username, String password, Role role) {
        this.name = name;
        this.username = username;
        this.password = PasswordUtility.passwordEncoder.encode(password);
        this.role = role;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
