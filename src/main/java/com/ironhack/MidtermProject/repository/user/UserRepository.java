package com.ironhack.MidtermProject.repository.user;

import com.ironhack.MidtermProject.enums.Role;
import com.ironhack.MidtermProject.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    public Optional<User> findAllByUsername(String username);
    public Optional<List<User>> findByRoleEquals(Role role);
}
