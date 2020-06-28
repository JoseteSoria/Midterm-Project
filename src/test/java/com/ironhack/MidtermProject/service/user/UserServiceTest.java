package com.ironhack.MidtermProject.service.user;

import com.ironhack.MidtermProject.enums.Role;
import com.ironhack.MidtermProject.exceptions.UserAlreadyExistException;
import com.ironhack.MidtermProject.model.user.User;
import com.ironhack.MidtermProject.repository.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    User u1, u2;
    private List<User> users;

    @BeforeEach
    void setUp() {
        u1 = new User("Simba", "kinglyon", "kinglyon", Role.ACCOUNT_HOLDER);
        u2 = new User("Hercules", "strongman", "strongman", Role.ACCOUNT_HOLDER);
        users = Stream.of(u1, u2).collect(Collectors.toList());
        userRepository.saveAll(Stream.of(u1, u2).collect(Collectors.toList()));
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void findAll() {
        List<User> users = userService.findAll();
        assertEquals(2, users.size());
    }

    @Test
    void viewAll() {
        List<User> users = userService.viewAllUsers();
        assertEquals(2, users.size());
    }


    @Test
    void loadUserByUsername_UserNotExist_Exception() {
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("ajhljkhfgasg"));
    }

    @Test
    void createUser() {
        User user = new User("Manuel", "manolo", "manolo", Role.ACCOUNT_HOLDER);
        User result = userService.createUser(user);
        assertEquals("manolo", result.getUsername());
    }

    @Test
    void createUser_AlreadyExist_Exception() {
        User user = new User("Simba", "kinglyon", "kinglyon", Role.ACCOUNT_HOLDER);
        assertThrows(UserAlreadyExistException.class, () -> userService.createUser(user));
    }

    @Test
    void viewAllAccountHolders() {
        List<User> result = userService.viewAllAccountHolders();
        assertEquals(users.get(0).getName(), result.get(0).getName());
    }
}