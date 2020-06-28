package com.ironhack.MidtermProject.service.user;

import com.ironhack.MidtermProject.enums.Role;
import com.ironhack.MidtermProject.exceptions.IdNotFoundException;
import com.ironhack.MidtermProject.exceptions.UserAlreadyExistException;
import com.ironhack.MidtermProject.model.user.User;
import com.ironhack.MidtermProject.repository.user.UserRepository;
import com.ironhack.MidtermProject.security.CustomSecurityUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

@Service
public class UserService implements UserDetailsService, Serializable {
    private static final long serialVersionUID = 2L;

    @Autowired
    private UserRepository userRepository;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findAllByUsername(username).orElseThrow(() -> {
            throw new UsernameNotFoundException("User not found");
        });
        return new CustomSecurityUser(user);
    }

    @Transactional
    public User createUser(User user) {
        if (userRepository.findAllByUsername(user.getUsername()).isPresent())
            throw new UserAlreadyExistException("There is already a user with the same name");
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public List<User> viewAllAccountHolders() {
        return userRepository.findByRoleEquals(Role.ACCOUNT_HOLDER).orElseThrow(() -> new IdNotFoundException("There are no Account Holders"));
    }

    public List<User> viewAllUsers() {
        return userRepository.findAll();
    }
}
