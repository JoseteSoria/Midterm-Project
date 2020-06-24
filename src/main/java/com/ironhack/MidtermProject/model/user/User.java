package com.ironhack.MidtermProject.model.user;

import com.ironhack.MidtermProject.enums.Role;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

//@MappedSuperclass
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer id;
    protected String name;
    protected String username;

    @Enumerated(EnumType.STRING)
    protected Role role;

    public User() {}

    public User(String name, String username, Role role) {
        this.name = name;
        this.username = username;
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
}
